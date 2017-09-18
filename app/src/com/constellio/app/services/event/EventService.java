package com.constellio.app.services.event;

import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.data.dao.services.bigVault.SearchResponseIterator;
import com.constellio.data.io.services.facades.IOServices;
import com.constellio.data.io.services.zip.ZipService;
import com.constellio.data.io.services.zip.ZipServiceException;
import com.constellio.data.utils.TimeProvider;
import com.constellio.data.utils.hashing.HashingService;
import com.constellio.data.utils.hashing.HashingServiceException;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.Collection;
import com.constellio.model.entities.records.wrappers.Event;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.fromEveryTypesOfEveryCollection;

public class EventService {
    AppLayerFactory appLayerLayerFactory;

    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String ENCODING = "UTF-8";
    public static final String FOLDER = "eventsBackup";
    public static final String IO_STREAM_NAME_BACKUP_EVENTS_IN_VAULT = "com.constellio.app.services.event.EventService#backupEventsInVault";
    public static final String IO_STREAM_NAME_CLOSE = "com.constellio.app.services.event.EventService#close";


    private IOServices ioServices;
    private ZipService zipService;
    private MetadataSchemasManager metadataSchemasManager;
    private HashingService hashingService;


    public EventService(AppLayerFactory appLayerFactory) {
        this.appLayerLayerFactory = appLayerFactory;
        this.ioServices = appLayerFactory.getModelLayerFactory().getIOServicesFactory().newIOServices();
        this.zipService = appLayerFactory.getModelLayerFactory().getIOServicesFactory().newZipService();
        metadataSchemasManager = appLayerFactory.getModelLayerFactory().getMetadataSchemasManager();
        this.hashingService = appLayerFactory.getModelLayerFactory().getDataLayerFactory().getIOServicesFactory()
                .newHashingService(appLayerFactory.getModelLayerFactory().getDataLayerFactory().getDataLayerConfiguration().getHashingEncoding());;
    }

    public LocalDateTime getCurrentCutOff() {
        Integer periodInMonth = appLayerLayerFactory.getModelLayerFactory().getSystemConfigurationsManager().getValue(RMConfigs.KEEP_EVENTS_FOR_X_MONTH);
        LocalDateTime nowLocalDateTime = TimeProvider.getLocalDateTime();
        nowLocalDateTime = nowLocalDateTime.withSecondOfMinute(0).withHourOfDay(0).withMillisOfSecond(0).withMinuteOfHour(0);
        LocalDateTime cutoffLocalDateTime;


        cutoffLocalDateTime = nowLocalDateTime.minusMonths(periodInMonth);

        return cutoffLocalDateTime;

    }

    public LocalDateTime getLastDayTimeDeleted() {
        String dateTimeAsString = appLayerLayerFactory.getModelLayerFactory().getSystemConfigurationsManager().getValue(RMConfigs.LAST_BACKUP_DAY);
        LocalDateTime dateTime = null;

        if(dateTimeAsString != null) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
            dateTime = dateTimeFormatter.parseLocalDateTime(dateTimeAsString);
        }

        return dateTime;
    }

    public void setLastDayTimeDelete(LocalDateTime lastDayTime) {
        appLayerLayerFactory.getModelLayerFactory()
                .getSystemConfigurationsManager().setValue(RMConfigs.LAST_BACKUP_DAY, lastDayTime.toString(DATE_TIME_FORMAT));
    }

    public void backupAndRemove() {
        backupEventsInVault();
        removeOldEventFromSolr();
    }

    private void closeFile(File file, IndentingXMLStreamWriter indentingXMLStreamWriter, LocalDateTime localDateTime, String fileName) {
        if(indentingXMLStreamWriter != null) {
            InputStream zipFileInputStream = null;
            try {
                if(localDateTime != null) {
                    setLastDayTimeDelete(localDateTime.withTime(0, 0,0,0));
                }
                indentingXMLStreamWriter.writeEndElement();
                indentingXMLStreamWriter.writeEndDocument();
                indentingXMLStreamWriter.flush();
                indentingXMLStreamWriter.close();
                File zipFile = createNewFile(fileName + ".zip");
                zipService.zip(zipFile, Arrays.asList(file));

                zipFileInputStream = ioServices.newFileInputStream(zipFile, IO_STREAM_NAME_CLOSE);
                appLayerLayerFactory.getModelLayerFactory().getContentManager()
                        .getContentDao().add(FOLDER + "/" + fileName + ".zip", zipFileInputStream);
            } catch (XMLStreamException e) {
                throw new RuntimeException("Error while closing the Event writer outputStream. File : " + fileName, e);
            } catch (ZipServiceException e) {
                throw new RuntimeException("Error while zipping the file : " + fileName, e);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Error while zipping the file : " + fileName, e);
            } finally {
                    ioServices.closeQuietly(zipFileInputStream);
            }
        }
    }

    private String fileName(LocalDateTime localDateTime) {
        return localDateTime.getYear() + "-" + localDateTime.getMonthOfYear() + "-" + localDateTime.getDayOfMonth();
    }

    private File createNewFile(String fileName) {
        return ioServices.newTemporaryFileWithoutGuid(fileName);
    }

    public static final String EVENTS_XML_TAG = "Events";
    public static final String EVENT_XML_TAG = "Event";

    public List<Event> backupEventsInVault() {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        List<Event> eventList = new ArrayList<>();
        LocalDateTime lasDayTimeDelete = getLastDayTimeDeleted();
        File currentFile = null;
        if(lasDayTimeDelete == null || getCurrentCutOff().compareTo(getLastDayTimeDeleted()) < 0) {
            SearchServices searchServices = appLayerLayerFactory.getModelLayerFactory().newSearchServices();
            LogicalSearchQuery logicalSearchQuery = new LogicalSearchQuery();
            LogicalSearchCondition logicalSearchCondition = fromEveryTypesOfEveryCollection().where(Schemas.SCHEMA).isStartingWithText("event_")
                    .andWhere(Schemas.CREATED_ON).isLessThan(getCurrentCutOff());
            logicalSearchQuery.setCondition(logicalSearchCondition);
            logicalSearchQuery.sortAsc(Schemas.CREATED_ON);

            SearchResponseIterator<Record> searchResponseIterator = searchServices.recordsIteratorKeepingOrder(logicalSearchQuery, 25000);

            int dayOfTheMonth = -1;

            OutputStream fileOutputStream = null;
            XMLStreamWriter xmlStreamWriter = null;
            IndentingXMLStreamWriter writer = null;
            LocalDateTime oldLocalDateTime = null;
            LocalDateTime localDateTime = null;
            String fileName = null;
            try
            {
                while (searchResponseIterator.hasNext()) {
                    Record record = searchResponseIterator.next();

                    oldLocalDateTime = localDateTime;
                    localDateTime = record.get(Schemas.CREATED_ON);

                    try {
                        if (dayOfTheMonth != localDateTime.getDayOfMonth()) {
                            dayOfTheMonth = localDateTime.getDayOfMonth();
                            closeFile(currentFile, writer, oldLocalDateTime, fileName);
                            ioServices.closeQuietly(fileOutputStream);
                            fileName = fileName(localDateTime);
                            currentFile = createNewFile(fileName + ".xml");
                            fileOutputStream = ioServices.newFileOutputStream(currentFile, IO_STREAM_NAME_BACKUP_EVENTS_IN_VAULT);
                            xmlStreamWriter = factory.createXMLStreamWriter(fileOutputStream, ENCODING);
                            writer = new IndentingXMLStreamWriter(xmlStreamWriter);
                            writer.setIndentStep("  ");
                            writer.writeStartDocument(ENCODING, "1.0");
                            writer.writeStartElement(EVENTS_XML_TAG);

                        }
                        writer.writeStartElement(EVENT_XML_TAG);

                        MetadataSchemaTypes metadataSchemaTypes = metadataSchemasManager.getSchemaTypes(record.getCollection());
                        MetadataSchema metadataSchema = metadataSchemaTypes.getSchema(record.getSchemaCode());
                        for (Metadata metadata : metadataSchema.getMetadatas()) {
                            Object value = record.get(metadata);

                            boolean write;
                            if (value != null) {
                                write = true;
                                if (value instanceof java.util.Collection) {
                                    if (CollectionUtils.isNotEmpty((java.util.Collection) value)) {
                                        write = true;
                                    } else {
                                        write = false;
                                    }
                                }

                                if (write) {
                                    writer.writeAttribute(metadata.getLocalCode(), record.get(metadata).toString());
                                }
                            }
                        }

                        writer.writeEndElement();

                    } catch (Exception e) {
                        throw new RuntimeException("File not found for Event writing", e);
                    }
                }
            }
            finally {
                if(writer != null && localDateTime != null){
                    closeFile(currentFile, writer, localDateTime, fileName);
                    ioServices.closeQuietly(fileOutputStream);
                }
            }


        }

        return eventList;
    }

    public void removeOldEventFromSolr() {

    }
}
