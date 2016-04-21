package com.constellio.app.modules.rm.ui.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.entities.UserDocumentVO;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.app.utils.HttpRequestUtils;
import com.constellio.model.conf.FoldersLocator;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.UserDocument;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.schemas.SchemaUtils;
import com.vaadin.server.VaadinServletService;

public class ConstellioAgentUtils {

	public static final String AGENT_DOWNLOAD_URL = "http://constellio.com/agent/";

	public static boolean isAgentSupported() {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		return isAgentSupported(request);
	}

	public static boolean isAgentSupported(HttpServletRequest request) {
		return HttpRequestUtils.isWindows(request) || HttpRequestUtils.isMacOsX(request) || HttpRequestUtils.isLocalhost(request);
	}

	public static String getAgentBaseURL() {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		return getAgentBaseURL(request);
	}

	public static String getAgentBaseURL(HttpServletRequest request) {
		String agentBaseURL;
		// FIXME Should not obtain ConstellioFactories through singleton
		ModelLayerFactory modelLayerFactory = ConstellioFactories.getInstance().getModelLayerFactory();
		SystemConfigurationsManager systemConfigurationsManager = modelLayerFactory.getSystemConfigurationsManager();
		RMConfigs rmConfigs = new RMConfigs(systemConfigurationsManager);
		if (rmConfigs.isAgentEnabled()) {
			String baseURL = HttpRequestUtils.getBaseURL(request, true);

			StringBuffer sb = new StringBuffer();
			sb.append(baseURL);
			sb.append("/agentPath");
			agentBaseURL = sb.toString();
		} else {
			agentBaseURL = null;
		}
		return agentBaseURL;
	}

	public static String getAgentInitURL() {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		return getAgentInitURL(request);
	}

	public static String getAgentInitURL(HttpServletRequest request) {
		String agentBaseURL = getAgentBaseURL();
		return addConstellioProtocol(agentBaseURL, request);
	}

	public static String getAgentURL(RecordVO recordVO, ContentVersionVO contentVersionVO) {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		SessionContext sessionContext = ConstellioUI.getCurrentSessionContext();
		return getAgentURL(recordVO, contentVersionVO, request, sessionContext);
	}

	public static String getAgentURL(RecordVO recordVO, ContentVersionVO contentVersionVO, HttpServletRequest request, SessionContext sessionContext) {
		String agentURL;
		if (recordVO != null && contentVersionVO != null && isAgentSupported(request)) {
			// FIXME Should not obtain ConstellioFactories through singleton
			ModelLayerFactory modelLayerFactory = ConstellioFactories.getInstance().getModelLayerFactory();
			SystemConfigurationsManager systemConfigurationsManager = modelLayerFactory.getSystemConfigurationsManager();
			RMConfigs rmConfigs = new RMConfigs(systemConfigurationsManager);
			if (rmConfigs.isAgentEnabled() && (!(recordVO instanceof UserDocumentVO) || rmConfigs.isAgentEditUserDocuments())) {
				String resourcePath = getResourcePath(recordVO, contentVersionVO, sessionContext);
				if (resourcePath != null) {
					String agentBaseURL = getAgentBaseURL(request);
					StringBuffer sb = new StringBuffer();
					sb.append(agentBaseURL);
					sb.append(resourcePath);
					agentURL = sb.toString();
				} else {
					agentURL = null;
				}
			} else {
				agentURL = null;
			}
		} else {
			agentURL = null;
		}
		return addConstellioProtocol(agentURL, request);
	}
	
	public static String getAgentSmbURL(String smbPath) {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		return getAgentSmbURL(smbPath, request);
	}
	
	public static String getAgentSmbURL(String smbPath, HttpServletRequest request) {
		String agentSmbURL;
		
		String passthroughPath;
		if (HttpRequestUtils.isWindows(request)) {
			passthroughPath = StringUtils.replace(smbPath, "/", "\\");
			passthroughPath = StringUtils.removeStart(passthroughPath, "smb:");
		} else {
			passthroughPath = smbPath;
		}

		String agentBaseURL = getAgentBaseURL();
		StringBuffer sb = new StringBuffer();
		sb.append(agentBaseURL);
		sb.append("/passthrough/");
		sb.append(passthroughPath);
		agentSmbURL = sb.toString();
		return addConstellioProtocol(agentSmbURL, request);
	}

	private static String getResourcePath(RecordVO recordVO, ContentVersionVO contentVersionVO, SessionContext sessionContext) {
		String resourcePath;

		ConstellioFactories constellioFactories = ConstellioFactories.getInstance();
		ModelLayerFactory modelLayerFactory = constellioFactories.getModelLayerFactory();
		RecordServices recordServices = modelLayerFactory.newRecordServices();

		Record record = recordServices.getDocumentById(recordVO.getId());
		String schemaCode = record.getSchemaCode();
		String schemaTypeCode = new SchemaUtils().getSchemaTypeCode(schemaCode);

		String collectionName = record.getCollection();
		UserVO currentUserVO = sessionContext.getCurrentUser();
		String currentUsername = currentUserVO.getUsername();
		String currentUserId = currentUserVO.getId();

		MetadataSchemaTypes types = types(sessionContext);

		if (UserDocument.SCHEMA_TYPE.equals(schemaTypeCode)) {
			UserDocument userDocument = new UserDocument(record, types);
			if (currentUserId.equals(userDocument.getUser())) {
				StringBuffer sb = new StringBuffer();
				sb.append("/");
				sb.append(currentUsername);
				sb.append("/");
				sb.append(collectionName);
				sb.append("/userDocuments");
				sb.append("/");
				sb.append(userDocument.getId());
				sb.append("/");
				sb.append(contentVersionVO.getFileName());
				resourcePath = sb.toString();
			} else {
				resourcePath = null;
			}
		} else if (Document.SCHEMA_TYPE.equals(schemaTypeCode)) {
			Document document = new Document(record, types);
			Content content = document.getContent();
			if (content == null) {
				resourcePath = null;
			} else if (currentUserId.equals(content.getCheckoutUserId())) {
				StringBuffer sb = new StringBuffer();
				sb.append("/");
				sb.append(currentUsername);
				sb.append("/");
				sb.append(collectionName);
				sb.append("/checkedOutDocuments");
				sb.append("/");
				sb.append(document.getId());
				sb.append("/");
				sb.append(contentVersionVO.getFileName());
				resourcePath = sb.toString();
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("/");
				sb.append(currentUsername);
				sb.append("/");
				sb.append(collectionName);
				sb.append("/notCheckedOutDocuments");
				sb.append("/");
				sb.append(document.getId());
				sb.append("/");
				sb.append(contentVersionVO.getFileName());
				resourcePath = sb.toString();
			}
		} else {
			resourcePath = null;
		}

		return resourcePath;
	}

	private static final MetadataSchemaTypes types(SessionContext sessionContext) {
		String collectionName = sessionContext.getCurrentCollection(); 
		ModelLayerFactory modelLayerFactory = ConstellioFactories.getInstance().getModelLayerFactory();
		MetadataSchemasManager metadataSchemasManager = modelLayerFactory.getMetadataSchemasManager();
		return metadataSchemasManager.getSchemaTypes(collectionName);
	}

	public static String addConstellioProtocol(String url) {
		HttpServletRequest request = VaadinServletService.getCurrentServletRequest();
		return addConstellioProtocol(url, request);
	}

	public static String addConstellioProtocol(String url, HttpServletRequest request) {
		String agentURL;
		if (url != null) {
			String encoding;
			if (HttpRequestUtils.isWindows(request)) {
				encoding = "cp1252";
			} else {
				// TODO Validate after implementing the agent for other OS.
				encoding = "UTF-8";
			}
			String encodedURL; 
			try {
				encodedURL = URLEncoder.encode(url, encoding);
			} catch (UnsupportedEncodingException e) {
				if ("cp1252".equals(encoding)) {
					try {
						encodedURL = URLEncoder.encode(url, "ISO-8859-1");
					} catch (UnsupportedEncodingException e2) {
						throw new RuntimeException(e2);
					}
				} else {
					throw new RuntimeException(e);
				}
			}
			agentURL = "constellio://" + encodedURL;
		} else {
			agentURL = null;
		}
		return agentURL;
	}
	
	public static String getAgentDownloadURL() {
		return AGENT_DOWNLOAD_URL;
	}

	public static String getAgentVersion() {
		FoldersLocator foldersLocator = new FoldersLocator();
		File resourcesFolder = foldersLocator.getModuleResourcesFolder("rm");
		File agentFolder = new File(resourcesFolder, "agent");
		File agentVersionFile = new File(agentFolder, "constellio-agent.version");
		String version;
		try {
			version = FileUtils.readFileToString(agentVersionFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return version;
	}

	public static void main(String[] args)
			throws Exception {
		URI location = new URI("http://constellio.doculibre.com/#!agentSetup");
		String contextPath = "/constellio";
		String schemeSpecificPart = location.getSchemeSpecificPart().substring(2);
		String schemeSpecificPartBeforeContextPath;
		if (StringUtils.isNotBlank(contextPath)) {
			if (schemeSpecificPart.indexOf(contextPath) != -1) {
				schemeSpecificPartBeforeContextPath = StringUtils.substringBeforeLast(schemeSpecificPart, contextPath);
			} else {
				contextPath = null;
				schemeSpecificPartBeforeContextPath = StringUtils.removeEnd(schemeSpecificPart, "/");
			}
		} else {
			schemeSpecificPartBeforeContextPath = StringUtils.removeEnd(schemeSpecificPart, "/");
		}

		StringBuffer sb = new StringBuffer();
		sb.append(location.getScheme());
		sb.append("://");
		sb.append(schemeSpecificPartBeforeContextPath);
		if (StringUtils.isNotBlank(contextPath)) {
			sb.append(contextPath);
		}
		sb.append("/agentPath");
		System.out.println(sb);
	}

}
