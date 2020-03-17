package com.constellio.app.modules.rm.ui.pages.trigger;


import com.constellio.app.modules.rm.data.TriggerDataProvider;
import com.constellio.app.modules.rm.navigation.RMViews;
import com.constellio.app.ui.framework.data.RecordVODataProvider;
import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.model.entities.Language;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class RecordTriggerManagerPresenter extends BasePresenter<RecordTriggerManagerView> {
	private Record targetedRecord;
	private RecordServices recordServices;
	private MetadataSchemasManager metadataSchemasManager;

	public RecordTriggerManagerPresenter(RecordTriggerManagerView view) {
		super(view);

		this.recordServices = appLayerFactory.getModelLayerFactory().newRecordServices();
		this.metadataSchemasManager = appLayerFactory.getModelLayerFactory().getMetadataSchemasManager();
	}

	public void forParams(String parameters) {
		if (StringUtils.isNotBlank(parameters)) {
			targetedRecord = recordServices.getRecordSummaryById(view.getCollection(), parameters);
			String schemaLabel = metadataSchemasManager.getSchemaOf(targetedRecord).getSchemaType().getLabel(Language.withLocale(view.getSessionContext().getCurrentLocale()));
			view.setRecordTitle(schemaLabel.toLowerCase() + " " + targetedRecord.getTitle());
		} else {
			throw new IllegalStateException("this page require a recordid");
		}
	}

	public Record getTargetedRecord() {
		return targetedRecord;
	}

	@Override
	protected boolean hasPageAccess(String params, User user) {
		return true;
	}

	@Override
	protected boolean hasRestrictedRecordAccess(String params, User user, Record restrictedRecord) {
		return user.hasWriteAccess().on(restrictedRecord);
	}

	@Override
	protected List<String> getRestrictedRecordIds(String params) {
		return Arrays.asList(targetedRecord.getId());
	}

	public RecordVODataProvider getDataProvider() {
		return new TriggerDataProvider(targetedRecord.getId(), appLayerFactory, view.getSessionContext());
	}

	public void addRecordTriggerClicked() {
		view.navigate().to(RMViews.class).addEditTriggerToRecord(targetedRecord.getId());
	}
}
