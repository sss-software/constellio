package com.constellio.app.entities.navigation;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDateTime;

import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.contextmenu.BaseContextMenu;
import com.constellio.app.ui.framework.data.RecordLazyTreeDataProvider;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.schemas.builders.CommonMetadataBuilder;

public abstract class PageItem implements CodedItem, Serializable {
	public enum Type {RECENT_ITEM_TABLE, RECORD_TABLE, RECORD_TREE}

	private final String code;
	private final Type type;

	protected PageItem(String code, Type type) {
		this.code = code;
		this.type = type;
	}

	@Override
	public String getCode() {
		return code;
	}

	public Type getType() {
		return type;
	}

	public static abstract class RecentItemTable extends PageItem {
		protected RecentItemTable(String code) {
			super(code, Type.RECENT_ITEM_TABLE);
		}

		public abstract List<RecentItem> getItems(ModelLayerFactory modelLayerFactory, SessionContext sessionContext);

		public static class RecentItem implements Serializable {
			public static final String CAPTION = "caption";
			public static final String LAST_ACCESS = "lastAccess";

			private final RecordVO record;
			private final String caption;

			public RecentItem(RecordVO record, String caption) {
				this.record = record;
				this.caption = caption;
			}

			public RecordVO getRecord() {
				return record;
			}

			public String getCaption() {
				return caption;
			}

			public String getId() {
				return record.getId();
			}

			public LocalDateTime getLastAccess() {
				return record.get(CommonMetadataBuilder.MODIFIED_ON);
			}
		}
	}

	public static abstract class RecordTable extends PageItem {
		public RecordTable(String code) {
			super(code, Type.RECORD_TABLE);
		}

		public abstract RecordVODataProvider getDataProvider(
				ModelLayerFactory modelLayerFactory, SessionContext sessionContext);
	}

	public static abstract class RecordTree extends PageItem {
		public RecordTree(String code) {
			super(code, Type.RECORD_TREE);
		}

		public int getDefaultTab() {
			return 0;
		}

		public abstract List<RecordLazyTreeDataProvider> getDataProviders(
				ModelLayerFactory modelLayerFactory, SessionContext sessionContext);

		public abstract BaseContextMenu getContextMenu();
	}
}
