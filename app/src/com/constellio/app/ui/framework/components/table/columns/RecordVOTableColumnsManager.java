package com.constellio.app.ui.framework.components.table.columns;

import com.constellio.app.entities.schemasDisplay.SchemaDisplayConfig;
import com.constellio.app.modules.rm.ui.pages.cart.DefaultFavoritesTable;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.schemasDisplay.SchemasDisplayManager;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.framework.components.table.RecordVOTable;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.services.schemas.SchemaUtils;
import com.vaadin.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class RecordVOTableColumnsManager extends TableColumnsManager {

	public RecordVOTableColumnsManager() {
	}

	public RecordVOTableColumnsManager(RecordVOTable table, String tableId) {
		super(table, tableId);
	}

	@Override
	protected void decorateVisibleColumns(List<String> visibleColumnForUser, String tableId) {
		List<String> toRemove = new ArrayList<>();

		for (String id : visibleColumnForUser) {
			String[] parsedCode = SchemaUtils.underscoreSplitWithCache(id);
			if (parsedCode.length == 3 && metadataSchemaTypes.hasMetadata(id) && !currentUser.hasGlobalAccessToMetadata(metadataSchemaTypes.getMetadata(id))) {
				toRemove.add(id);
			}
		}

		for (String itemToRemove : toRemove) {
			visibleColumnForUser.remove(itemToRemove);
		}

		if (toRemove.size() > 0) {
			currentUser.setVisibleTableColumns(tableId, visibleColumnForUser);
		}
	}

	@Override
	protected List<String> getDefaultVisibleColumnIds(Table table) {
		List<String> defaultVisibleColumnIds;
		List<MetadataSchemaVO> schemaVOs;
		if (table instanceof DefaultFavoritesTable) {
			DefaultFavoritesTable defaultFavoritesTable = (DefaultFavoritesTable) table;
			schemaVOs = defaultFavoritesTable.getSchemas();
		} else {
			RecordVOTable recordVOTable = (RecordVOTable) table;
			schemaVOs = recordVOTable.getSchemas();
		}
		if (!schemaVOs.isEmpty()) {
			defaultVisibleColumnIds = new ArrayList<>();
			SessionContext sessionContext = ConstellioUI.getCurrentSessionContext();
			String collection = sessionContext.getCurrentCollection();
			AppLayerFactory appLayerFactory = constellioFactories.getAppLayerFactory();
			SchemasDisplayManager schemasDisplayManager = appLayerFactory.getMetadataSchemasDisplayManager();

			for (MetadataSchemaVO schemaVO : schemaVOs) {
				String schemaCode = schemaVO.getCode();
				SchemaDisplayConfig schemaDisplayConfig = schemasDisplayManager.getSchema(collection, schemaCode);
				defaultVisibleColumnIds.addAll(schemaDisplayConfig.getTableMetadataCodes());
			}

			Object[] tableVisibleColumns = table.getVisibleColumns();
			for (Object tableVisibleColumn : tableVisibleColumns) {
				if (!(tableVisibleColumn instanceof MetadataVO)) {
					String columnId = toColumnId(tableVisibleColumn);
					defaultVisibleColumnIds.add(columnId);
				}
			}
		} else {
			defaultVisibleColumnIds = super.getDefaultVisibleColumnIds(table);
		}
		return defaultVisibleColumnIds;
	}

	@Override
	protected String toColumnId(Object propertyId) {
		String columnId;
		if (propertyId instanceof MetadataVO) {
			columnId = ((MetadataVO) propertyId).getCode();
		} else {
			columnId = super.toColumnId(propertyId);
		}
		return columnId;
	}

}
