package com.constellio.app.ui.framework.containers;

import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.data.DataProvider;
import com.constellio.app.ui.framework.data.SearchEventVODataProvider;
import com.constellio.app.ui.framework.items.RecordVOItem;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.addons.lazyquerycontainer.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchEventVOLazyContainer extends LazyQueryContainer implements RefreshableContainer {
    public static final String CLICK_COUNT = "searchEvent_default_clickCount";
    public static final String ORIGINAL_QUERY = "searchEvent_default_originalQuery";
    public static final String PAGE_NAVIGATION_COUNT = "searchEvent_default_pageNavigationCount";
    public static final String PARAMS = "searchEvent_default_params";
    public static final String QUERY = "searchEvent_default_query";
    public static final String USER = "searchEvent_default_username";
    public static final String CREATION_DATE = "searchEvent_default_createdOn";
    public static final String Q_TIME = "searchEvent_default_qTime";
    public static final String NUM_FOUND = "searchEvent_default_numFound";
    public static final String NUM_PAGE = "searchEvent_default_numPage";

    public static final List<String> PROPERTIES = Collections.unmodifiableList(Arrays.asList(CREATION_DATE, QUERY, CLICK_COUNT, NUM_FOUND, Q_TIME, NUM_PAGE));
    public static final List<String> PROPERTIES_WITH_PARAMS = Collections.unmodifiableList(Arrays.asList(CREATION_DATE, QUERY, CLICK_COUNT, NUM_FOUND, Q_TIME, NUM_PAGE, PARAMS));

    public SearchEventVOLazyContainer(QueryDefinition queryDefinition, QueryFactory queryFactory) {
        super(queryDefinition, queryFactory);
    }

    public static class SearchEventVOLazyQueryDefinition extends LazyQueryDefinition {
        private Map<String, MetadataVO> definedMetadatas  = new HashMap<>();

        public SearchEventVOLazyQueryDefinition(SearchEventVODataProvider dataProvider, List<String> properties) {
            super(true, 100, null);

            if(properties == null || properties.isEmpty()) {
                properties = PROPERTIES_WITH_PARAMS;
            }

            MetadataSchemaVO schema = dataProvider.getSchema();
            List<MetadataVO> dataProviderDisplayMetadataVOs = schema.getDisplayMetadatas();
            for (MetadataVO metadataVO : dataProviderDisplayMetadataVOs) {
                if(properties.contains(metadataVO.getCode())) {
                    definedMetadatas.put(metadataVO.getCode(), metadataVO);
                }
            }

            for(String code: properties) {
                MetadataVO metadataVO = definedMetadatas.get(code);
                Class<?> javaType;
                if (metadataVO != null) {
                    javaType = metadataVO.getJavaType();
                } else {
                    javaType = String.class;
                }

                super.addProperty(StringUtils.trimToEmpty(code), javaType, getDefaultValue(javaType), true, true);
            }
        }

        private Object getDefaultValue(Class type) {
            if(Number.class.isAssignableFrom(type)) {
                return new BigDecimal(0);
            }

            return null;
        }

        public Map<String, MetadataVO> getDefinedMetadatas() {
            return (Map<String, MetadataVO>) Collections.unmodifiableMap(definedMetadatas);
        }
    }

    public static class SearchEventVOLazyQueryFactory implements QueryFactory, Serializable {
        private final Map<String, MetadataVO> definedMetadatas;
        private final SearchEventVODataProvider dataProvider;

        public SearchEventVOLazyQueryFactory(SearchEventVODataProvider dataProvider, Map<String, MetadataVO> definedMetadatas) {
            this.dataProvider = dataProvider;
            this.definedMetadatas = definedMetadatas;
        }

        @Override
        public Query constructQuery(final QueryDefinition queryDefinition) {
            return new Query() {

                @Override
                public int size() {
                    return (int) dataProvider.size();
                }

                @Override
                public void saveItems(List<Item> addedItems, List<Item> modifiedItems, List<Item> removedItems) {
                    throw new UnsupportedOperationException("Query is read-only");
                }

                @Override
                public List<Item> loadItems(int startIndex, int count) {
                    List<Item> items = new ArrayList<>();
                    List<RecordVO> recordVOsFromFirstDataProvider = dataProvider.listRecordVOs(startIndex, count);
                    for (RecordVO recordVO : recordVOsFromFirstDataProvider) {
                        Item item = new SearchEventVOItem(recordVO, definedMetadatas);
                        items.add(item);
                    }
                    return items;
                }

                @Override
                public boolean deleteAllItems() {
                    throw new UnsupportedOperationException("Query is read-only");
                }

                @Override
                public Item constructItem() {
                    throw new UnsupportedOperationException("Query is read-only");
                }
            };
        }

        public static class SearchEventVOItem extends RecordVOItem {
            private final Map<String, MetadataVO> definedMetadatas;

            public SearchEventVOItem(RecordVO recordVO, Map<String, MetadataVO> definedMetadatas) {
                super(recordVO);
                this.definedMetadatas = definedMetadatas;
            }

            @Override
            public Property<?> getItemProperty(Object id) {
                if(NUM_PAGE.equals(id)) {
                    final Property<?> paramsProp = super.getItemProperty(this.definedMetadatas.get(PARAMS));

                    return new AbstractProperty<Object>() {
                        @Override
                        public Object getValue() {
                            String params = paramsProp.getValue().toString();
                            double rows = getString("rows", params);
                            double start = getString("start", params);

                            if(start < 0) {
                                return 1;
                            } else if(rows == 0) {
                                return start;
                            } else {
                                return (int) ((start / rows) + 1);
                            }
                        }

                        @Override
                        public void setValue(Object newValue)
                                throws ReadOnlyException {
                            throw new ReadOnlyException("This column is read-only");
                        }

                        @Override
                        public Class<? extends Object> getType() {
                            return String.class;
                        }
                    };
                }

                return super.getItemProperty(this.definedMetadatas.get(id));
            }

            private double getString(String token, String params) {
                Pattern p = Pattern.compile(token+"=(\\S+),");   // the pattern to search for
                Matcher m = p.matcher(params);

                if (m.find()) {
                    try {
                        return Double.parseDouble(m.group(1));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return 0;
                    }
                } else {
                    return 0;
                }
            }

            @Override
            public Collection<?> getItemPropertyIds() {
                return definedMetadatas.keySet();
            }
        }
    }

    public static SearchEventVOLazyContainer defaultInstance(SearchEventVODataProvider dataProvider, List<String> properties) {
        SearchEventVOLazyQueryDefinition qDef = new SearchEventVOLazyQueryDefinition(dataProvider, properties);
        SearchEventVOLazyQueryFactory qFact = new SearchEventVOLazyQueryFactory(dataProvider, qDef.getDefinedMetadatas());

        final SearchEventVOLazyContainer searchEventVOLazyContainer = new SearchEventVOLazyContainer(qDef, qFact);

        dataProvider.addDataRefreshListener(new DataProvider.DataRefreshListener() {
            @Override
            public void dataRefresh() {
                searchEventVOLazyContainer.refresh();
            }
        });

        return searchEventVOLazyContainer;
    }
}
