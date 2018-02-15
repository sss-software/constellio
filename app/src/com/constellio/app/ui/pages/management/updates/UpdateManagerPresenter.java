package com.constellio.app.ui.pages.management.updates;

import static com.constellio.app.services.migrations.VersionsComparator.isFirstVersionBeforeSecond;
import static com.constellio.app.ui.i18n.i18n.$;
import static com.constellio.app.ui.pages.management.updates.UpdateNotRecommendedReason.BATCH_PROCESS_IN_PROGRESS;
import static java.util.Arrays.asList;

import java.io.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.api.extensions.UpdateModeExtension.UpdateModeHandler;
import com.constellio.app.entities.modules.ProgressInfo;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.services.appManagement.AppManagementService.LicenseInfo;
import com.constellio.app.services.appManagement.AppManagementServiceException;
import com.constellio.app.services.appManagement.AppManagementServiceRuntimeException.CannotConnectToServer;
import com.constellio.app.services.recovery.UpdateRecoveryImpossibleCause;
import com.constellio.app.services.recovery.UpgradeAppRecoveryService;
import com.constellio.app.servlet.ConstellioMonitoringServlet;
import com.constellio.app.ui.pages.base.BasePresenter;
import com.constellio.app.utils.GradleFileVersionParser;
import com.constellio.data.utils.TimeProvider;
import com.constellio.model.conf.FoldersLocator;
import com.constellio.model.conf.FoldersLocatorMode;
import com.constellio.model.entities.CorePermissions;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.EventType;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.records.reindexing.ReindexationMode;
import com.constellio.model.services.records.reindexing.ReindexingServices;

public class UpdateManagerPresenter extends BasePresenter<UpdateManagerView> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateManagerPresenter.class);

	private static final String MEMINFO_PATH = "C:\\Users\\Constellio\\Desktop\\Support\\meminfo";

	private static final String MEMTOTAL_PARAMETER = "MemTotal";

	private static final String WRAPPER_CONF_PATH = "C:\\Users\\Constellio\\Desktop\\Support\\wrapper.conf";

	private static final String CONSTELLIO_MEMORY_PARAMETER = "wrapper.java.maxmemory";

	private static final String SOLR_CONF_PATH = "C:\\Users\\Constellio\\Desktop\\Support\\solr.in.sh";

	private static final String SOLR_MEMORY_PARAMETER = "SOLR_JAVA_MEM";

	public UpdateManagerPresenter(UpdateManagerView view) {
		super(view);
	}

	public boolean isAlternateUpdateAvailable() {
		return appSystemExtentions.alternateUpdateMode.isActive();
	}

	public String getAlternateUpdateName() {
		return appSystemExtentions.alternateUpdateMode.getCode();
	}

	public void standardUpdateRequested() {
		view.showStandardUpdatePanel();
	}

	public void alternateUpdateRequested() {
		UpdateModeHandler handler = appSystemExtentions.alternateUpdateMode.getHandler(view);
		view.showAlternateUpdatePanel(handler);
	}

	public boolean isLicensedForAutomaticUpdate() {
		return appLayerFactory.newApplicationService().isLicensedForAutomaticUpdate();
	}

	public LicenseInfo getLicenseInfo() {
		return appLayerFactory.newApplicationService().getLicenseInfo();
	}

	public boolean isAutomaticUpdateAvailable() {
		return isLicensedForAutomaticUpdate() && isFirstVersionBeforeSecond(getCurrentVersion(), getUpdateVersion());
	}

	public String getChangelog() {
		String changelog;
		try {
			changelog = appLayerFactory.newApplicationService().getChangelogFromServer();
		} catch (CannotConnectToServer cc) {
			changelog = null;
		}

		return changelog;
	}

	public String getUpdateVersion() {
		try {
			return appLayerFactory.newApplicationService().getVersionFromServer();
		} catch (CannotConnectToServer cc) {
			view.showErrorMessage($("UpdateManagerViewImpl.error.connection"));
			return "0";
		}
	}

	public void updateFromServer() {
		ProgressInfo progressInfo = view.openProgressPopup();
		try {
			appLayerFactory.newApplicationService().getWarFromServer(progressInfo);
			appLayerFactory.newApplicationService().update(progressInfo);
			view.showRestartRequiredPanel();
		} catch (CannotConnectToServer cc) {
			view.showErrorMessage($("UpdateManagerViewImpl.error.connection"));
		} catch (AppManagementServiceException ase) {
			view.showErrorMessage($("UpdateManagerViewImpl.error.file"));
		} finally {
			view.closeProgressPopup();
		}
	}

	public void restart() {
		try {
			appLayerFactory.newApplicationService().restart();
			RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);
			Record event = rm.newEvent()
					.setType(EventType.RESTARTING)
					.setUsername(getCurrentUser().getUsername())
					.setUserRoles(StringUtils.join(getCurrentUser().getAllRoles().toArray(), "; "))
					.setIp(getCurrentUser().getLastIPAddress())
					.setCreatedOn(TimeProvider.getLocalDateTime())
					.setTitle($("ListEventsView.restarting"))
					.getWrappedRecord();
			Transaction t = new Transaction();
			t.add(event);
			appLayerFactory.getModelLayerFactory().newRecordServices().execute(t);
		} catch (AppManagementServiceException | RecordServicesException ase) {
			view.showErrorMessage($("UpdateManagerViewImpl.error.restart"));
		}
		ConstellioMonitoringServlet.systemRestarting = true;
		view.navigate().to().serviceMonitoring();
	}

	public void restartAndReindex() {
		FoldersLocator foldersLocator = new FoldersLocator();
		if (foldersLocator.getFoldersLocatorMode() == FoldersLocatorMode.PROJECT) {
			//Application is started from a test, it cannot be restarted
			RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);
			LOGGER.info("Reindexing started");
			ReindexingServices reindexingServices = modelLayerFactory.newReindexingServices();
			reindexingServices.reindexCollections(ReindexationMode.RECALCULATE_AND_REWRITE);
			LOGGER.info("Reindexing finished");
			Record eventRestarting = rm.newEvent()
					.setType(EventType.RESTARTING)
					.setUsername(getCurrentUser().getUsername())
					.setUserRoles(StringUtils.join(getCurrentUser().getAllRoles().toArray(), "; "))
					.setIp(getCurrentUser().getLastIPAddress())
					.setCreatedOn(TimeProvider.getLocalDateTime())
					.setTitle($("ListEventsView.restarting"))
					.getWrappedRecord();
			Record eventReindexing = rm.newEvent()
					.setType(EventType.REINDEXING)
					.setUsername(getCurrentUser().getUsername())
					.setUserRoles(StringUtils.join(getCurrentUser().getAllRoles().toArray(), "; "))
					.setIp(getCurrentUser().getLastIPAddress())
					.setCreatedOn(TimeProvider.getLocalDateTime())
					.setTitle($("ListEventsView.reindexing"))
					.getWrappedRecord();
			Transaction t = new Transaction();
			t.addAll(asList(eventReindexing, eventRestarting));
			try {

				appLayerFactory.getModelLayerFactory().newRecordServices().execute(t);
			} catch (Exception e) {
				view.showErrorMessage(e.getMessage());
			}
		} else {
			appLayerFactory.newApplicationService().markForReindexing();
			RMSchemasRecordsServices rm = new RMSchemasRecordsServices(collection, appLayerFactory);
			Record eventRestarting = rm.newEvent()
					.setType(EventType.RESTARTING)
					.setUsername(getCurrentUser().getUsername())
					.setUserRoles(StringUtils.join(getCurrentUser().getAllRoles().toArray(), "; "))
					.setIp(getCurrentUser().getLastIPAddress())
					.setCreatedOn(TimeProvider.getLocalDateTime())
					.setTitle($("RedémarrageListEventsView.restarting"))
					.getWrappedRecord();
			Record eventReindexing = rm.newEvent()
					.setType(EventType.REINDEXING)
					.setUsername(getCurrentUser().getUsername())
					.setUserRoles(StringUtils.join(getCurrentUser().getAllRoles().toArray(), "; "))
					.setIp(getCurrentUser().getLastIPAddress())
					.setCreatedOn(TimeProvider.getLocalDateTime())
					.setTitle($("ListEventsView.reindexing"))
					.getWrappedRecord();
			Transaction t = new Transaction();
			t.addAll(asList(eventReindexing, eventRestarting));
			try {
				appLayerFactory.getModelLayerFactory().newRecordServices().execute(t);
			} catch (Exception e) {
				view.showErrorMessage(e.getMessage());
			}

			try {
				appLayerFactory.newApplicationService().restart();
			} catch (AppManagementServiceException ase) {
				view.showErrorMessage($("UpdateManagerViewImpl.error.restart"));
			}
		}
		ConstellioMonitoringServlet.systemRestarting = true;
		view.navigate().to().serviceMonitoring();
	}

	public void licenseUpdateRequested() {
		view.showLicenseUploadPanel();
	}

	public OutputStream getLicenseOutputStream() {
		FileOutputStream stream = null;
		try {
			File license = modelLayerFactory.getFoldersLocator().getUploadLicenseFile();
			stream = new FileOutputStream(license);
		} catch (FileNotFoundException fnfe) {
			view.showErrorMessage($("UpdateManagerViewImpl.error.upload"));
		}
		return stream;
	}

	public void licenseUploadSucceeded() {
		appLayerFactory.newApplicationService().storeLicense(modelLayerFactory.getFoldersLocator().getUploadLicenseFile());
		view.showMessage($("UpdateManagerViewImpl.licenseUpdated"));
		view.navigate().to().updateManager();
	}

	public void licenseUploadCancelled() {
		view.showStandardUpdatePanel();
	}

	public String getCurrentVersion() {
		String version = appLayerFactory.newApplicationService().getWarVersion();
		if (version == null || version.equals("5.0.0")) {
			File versionFile = new File(new FoldersLocator().getConstellioProject(), "version");
			if (versionFile.exists()) {
				try {
					version = FileUtils.readFileToString(versionFile);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				version = "no version file";
			}
		}
		return version;
	}

	@Override
	protected boolean hasPageAccess(String params, final User user) {
		return user.has(CorePermissions.MANAGE_SYSTEM_UPDATES).globally();
	}

	public boolean isRestartWithReindexButtonEnabled() {
		return !recoveryModeEnabled();
	}

	private boolean recoveryModeEnabled() {
		return appLayerFactory.getModelLayerFactory().getSystemConfigs().isInUpdateProcess();
	}

	public boolean isUpdateEnabled() {
		UpdateRecoveryImpossibleCause updatePossible = isUpdateWithRecoveryPossible();
		return updatePossible == null || updatePossible == UpdateRecoveryImpossibleCause.TOO_SHORT_MEMORY;
	}

	public UpdateRecoveryImpossibleCause isUpdateWithRecoveryPossible() {
		return appLayerFactory.newUpgradeAppRecoveryService()
				.isUpdateWithRecoveryPossible();
	}

	public String getExceptionDuringLastUpdate() {
		UpgradeAppRecoveryService upgradeService = appLayerFactory
				.newUpgradeAppRecoveryService();
		return upgradeService.getLastUpgradeExceptionMessage();
	}

	public UpdateNotRecommendedReason getUpdateNotRecommendedReason() {
		if (modelLayerFactory.getBatchProcessesManager().getCurrentBatchProcess() != null && !modelLayerFactory
				.getBatchProcessesManager().getPendingBatchProcesses().isEmpty()) {
			return BATCH_PROCESS_IN_PROGRESS;
		} else {
			return null;
		}
	}

	public String getTotalSystemMemory() {
		String memTotal = findValueOfParameter(MEMINFO_PATH, MEMTOTAL_PARAMETER, ":");
		if(memTotal != null) {
			memTotal = toHumanReadleNumbers(memTotal, "kB");
		}
		return memTotal;
	}

	public String getAllocatedMemoryForConstellio() {
		String allocatedMemory = findValueOfParameter(WRAPPER_CONF_PATH, CONSTELLIO_MEMORY_PARAMETER, "=");
		if(allocatedMemory != null) {
			allocatedMemory = toHumanReadleNumbers(allocatedMemory, "MB");
		}
		return allocatedMemory;
	}

	public String getAllocatedMemoryForSolr() {
		String allocatedMemory = findValueOfParameter(SOLR_CONF_PATH, SOLR_MEMORY_PARAMETER, "=");
		if(allocatedMemory != null) {
			allocatedMemory = allocatedMemory.replaceAll("\"", "");
			String[] splittedValue = allocatedMemory.split("Xmx");
			if(splittedValue.length == 2) {
				allocatedMemory = toHumanReadleNumbers(splittedValue[1], "m");
			} else {
				return null;
			}
		}
		return allocatedMemory;
	}

	private String toHumanReadableNumbers(String totalMemory) {
		if(totalMemory.trim().endsWith("kB")) {
			try {
				double totalMemoryInGB = Double.parseDouble(totalMemory.replace("kB", "").trim()) / (1024 * 1024);
				totalMemoryInGB = roundToTwoDecimals(totalMemoryInGB);
				return totalMemoryInGB + " GB";
			} catch (Exception e) {
				return totalMemory;
			}
		} else {
			return totalMemory;
		}
	}

	private String toHumanReadleNumbers(String totalMemory, String currentUnit) {
		String memoryWithoutUnit = totalMemory;
		memoryWithoutUnit = totalMemory.trim();
		if(memoryWithoutUnit.endsWith(currentUnit)) {
			memoryWithoutUnit = memoryWithoutUnit.replace(currentUnit, "");
		}

		int divisionFactor = 0;
		currentUnit = currentUnit.toLowerCase();
		if(currentUnit.equals("kb") || currentUnit.equals("k")) {
			divisionFactor = 1024*1024;
		} else if(currentUnit.equals("mb") || currentUnit.equals("m")) {
			divisionFactor = 1024;
		} else if(currentUnit.equals("gb") || currentUnit.equals("g")) {
			divisionFactor = 1;
		}

		if(divisionFactor > 0) {
			try {
				double totalMemoryInGB = Double.parseDouble(memoryWithoutUnit.trim()) / (divisionFactor);
				totalMemoryInGB = roundToTwoDecimals(totalMemoryInGB);
				return totalMemoryInGB + " GB";
			} catch (Exception e) {
				return totalMemory;
			}
		} else {
			return totalMemory;
		}
	}

	private double roundToTwoDecimals(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	public String findValueOfParameter(String fileUrl, String parameter, String separator) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(fileUrl)));
			String line;
			while((line = reader.readLine()) != null) {
				String[] params = line.split(separator);
				if(params.length == 2) {
					String argument = params[0];
					String value = params[1];
					if(argument.equals(parameter)) {
						return value;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Double getPercentageOfAllocatedMemory(String totalSystemMemory, String allocatedMemoryForConstellio, String allocatedMemoryForSolr) {
		if(totalSystemMemory != null && allocatedMemoryForConstellio != null && allocatedMemoryForSolr != null &&
				totalSystemMemory.endsWith(" GB") && allocatedMemoryForConstellio.endsWith(" GB") && allocatedMemoryForSolr.endsWith(" GB")) {
			return roundToTwoDecimals(
					(Double.parseDouble(allocatedMemoryForConstellio.replace(" GB", "")) + Double.parseDouble(allocatedMemoryForSolr.replace(" GB", "")))
					/ Double.parseDouble(totalSystemMemory.replace(" GB", ""))
			);
		} else {
			return null;
		}
	}
}
