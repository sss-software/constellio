package com.constellio.app.ui.pages.management.schemaRecords;

import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.ui.pages.base.SingleSchemaBasePresenter;
import com.constellio.app.ui.pages.management.sequence.SequenceServices;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.HierarchicalValueListItem;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.extensions.ModelLayerCollectionExtensions;
import com.constellio.model.frameworks.validation.ValidationErrors;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesRuntimeException.RecordServicesRuntimeException_CannotLogicallyDeleteRecord;
import com.constellio.model.services.search.StatusFilter;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

import java.io.IOException;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;

@SuppressWarnings("serial")
public class DisplaySchemaRecordPresenter extends SingleSchemaBasePresenter<DisplaySchemaRecordView> {

	private transient SequenceServices sequenceServices;
	private transient RecordServices recordServices;
	private transient ModelLayerCollectionExtensions extensions;

	private String schemaCode;

	private RecordVO recordVO;

	private RecordToVOBuilder voBuilder = new RecordToVOBuilder();

	public DisplaySchemaRecordPresenter(DisplaySchemaRecordView view) {
		super(view);
		initTransientObjects();
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		initTransientObjects();
	}

	private void initTransientObjects() {
		ConstellioFactories constellioFactories = view.getConstellioFactories();
		SessionContext sessionContext = view.getSessionContext();
		sequenceServices = new SequenceServices(constellioFactories, sessionContext);
		recordServices = modelLayerFactory.newRecordServices();
		extensions = modelLayerFactory.getExtensions().forCollection(collection);
	}

	public void forParams(String params) {
		String id = params;
		recordVO = presenterService().getRecordVO(id, VIEW_MODE.DISPLAY, view.getSessionContext());
		schemaCode = recordVO.getSchema().getCode();
		setSchemaCode(schemaCode);
		view.setRecordVO(recordVO);

		final MetadataSchema schema = schema(schemaCode);
		if (isHierarchical()) {
			view.setSubRecords(new RecordVODataProvider(recordVO.getSchema(), voBuilder, view) {
				@Override
				protected LogicalSearchQuery getQuery() {
					Metadata parentMetadata = schema.getMetadata(HierarchicalValueListItem.PARENT);
					LogicalSearchQuery query = new LogicalSearchQuery();
					query.setCondition(from(schema).where(parentMetadata).is(recordVO.getRecord()));
					query.filteredWithUser(getCurrentUser());
					query.filteredByStatus(StatusFilter.ACTIVES);
					query.sortAsc(Schemas.TITLE);
					return query;
				}
			});
		}
	}

	private boolean isHierarchical() {
		MetadataSchema schema = schema(schemaCode);
		return schema.hasMetadataWithCode(HierarchicalValueListItem.PARENT);
	}

	public void backButtonClicked() {
		String schemaCode = getSchemaCode();
		view.navigate().to().listSchemaRecords(schemaCode);
	}

	void editButtonClicked() {
		editButtonClicked(recordVO);
	}

	private void editButtonClicked(RecordVO recordVO) {
		String schemaCode = getSchemaCode();
		view.navigate().to().editSchemaRecord(schemaCode, recordVO.getId());
	}

	void deleteButtonClicked() {
		deleteButtonClicked(recordVO);
	}

	private void deleteButtonClicked(RecordVO recordVO) {
		if (tryDelete(recordVO)) {
			String parentMetadataCode = HierarchicalValueListItem.PARENT;
			if (isHierarchical() && recordVO.get(parentMetadataCode) != null) {
				String parentRecordId = recordVO.get(parentMetadataCode);
				view.navigate().to().displaySchemaRecord(parentRecordId);
			} else {
				view.navigate().to().listSchemaRecords(schemaCode);
			}
		}
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		Record restrictedRecord = recordServices().getDocumentById(params);
		return new SchemaRecordsPresentersServices(appLayerFactory).canViewSchemaTypeRecord(restrictedRecord, user);
	}

	public boolean isSequenceTable() {
		return !sequenceServices.getAvailableSequences(recordVO.getId()).isEmpty();
	}

	private boolean tryDelete(RecordVO recordVO) {
		boolean success;
		Record record = recordVO.getRecord();
		ValidationErrors validationErrors = recordServices.validateLogicallyDeletable(record, getCurrentUser());
		if (validationErrors.isEmpty()) {
			try {
				delete(record, null, false, true);
				success = true;
			} catch (RecordServicesRuntimeException_CannotLogicallyDeleteRecord exception) {
				success = false;
			}
		} else {
			success = false;
		}
		return success;
	}

	@SuppressWarnings("deprecation")
	private boolean isEditButtonVisible(RecordVO recordVO) {
		Record record = recordVO.getRecord();
		User user = getCurrentUser();
		return !extensions.isModifyBlocked(record, user) && extensions.isRecordModifiableBy(record, user);
	}

	private boolean isDeleteButtonVisible(RecordVO recordVO) {
		Record record = recordVO.getRecord();
		User user = getCurrentUser();
		return extensions.isDeleteAuthorized(record, user) && recordServices.validateLogicallyDeletable(record, getCurrentUser()).isEmpty();
	}

	public boolean isEditButtonVisible() {
		return isEditButtonVisible(recordVO);
	}

	public boolean isDeleteButtonVisible() {
		return isDeleteButtonVisible(recordVO);
	}

	public void addSubRecordButtonClicked() {
		view.navigate().to().addSchemaRecord(recordVO.getSchema().getCode(), recordVO.getId());
	}

	public boolean isEditSubRecordButtonVisible(RecordVO subRecordVO) {
		return isEditButtonVisible(subRecordVO);
	}

	public void displaySubRecordButtonClicked(RecordVO subRecordVO) {
		view.navigate().to().displaySchemaRecord(subRecordVO.getId());
	}

	public void editSubRecordButtonClicked(RecordVO subRecordVO) {
		view.navigate().to().editSchemaRecord(subRecordVO.getSchema().getCode(), subRecordVO.getId());
	}

	public boolean isDeleteSubRecordButtonVisible(RecordVO subRecordVO) {
		return isDeleteButtonVisible(subRecordVO);
	}

	public void deleteSubRecordButtonClicked(RecordVO subRecordVO) {
		if (tryDelete(subRecordVO)) {
			view.navigate().to().displaySchemaRecord(recordVO.getId());
		}
	}

}
