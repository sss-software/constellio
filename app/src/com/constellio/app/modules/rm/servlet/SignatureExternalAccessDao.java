package com.constellio.app.modules.rm.servlet;

import com.constellio.app.modules.rm.constants.RMPermissionsTo;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.actions.DocumentRecordActionsServices;
import com.constellio.app.modules.rm.services.menu.behaviors.DocumentMenuItemActionBehaviors;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.users.UserServices;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServletResponse;

public class SignatureExternalAccessDao {
	private static final String UNAUTHORIZED = "Unauthorized";
	private static final String MISSING_DOCUMENT_PARAM = "Missing document param.";
	private static final String INVALID_DOCUMENT_PARAM = "The document does not exist.";
	private static final String MISSING_DATE_PARAM = "Missing expiration date param.";
	private static final String INVALID_DATE_PARAM = "The expiration date is not valid.";
	private static final String ACTION_IMPOSSIBLE = "The url generation is not possible for this type of document.";
	private static final String CANNOT_SAVE_RECORD = "Impossible to save record.";

	private AppLayerFactory appLayerFactory;

	private RecordServices recordServices;
	private UserServices userServices;
	private RMSchemasRecordsServices rm;
	private DocumentRecordActionsServices documentRecordActionsServices;
	private DocumentMenuItemActionBehaviors documentMenuItemActionBehaviors;

	public SignatureExternalAccessDao(AppLayerFactory appLayerFactory) {
		this.appLayerFactory = appLayerFactory;

		recordServices = appLayerFactory.getModelLayerFactory().newRecordServices();
		userServices = appLayerFactory.getModelLayerFactory().newUserServices();
	}

	private void initWithCollection(String collection) {
		rm = new RMSchemasRecordsServices(collection, appLayerFactory);
		documentRecordActionsServices = new DocumentRecordActionsServices(collection, appLayerFactory);
		documentMenuItemActionBehaviors = new DocumentMenuItemActionBehaviors(collection, appLayerFactory);
	}

	public String createExternalSignatureUrl(String username, String documentId, String expirationDate)
			throws SignatureExternalAccessServiceException {

		if (StringUtils.isBlank(documentId)) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_BAD_REQUEST, MISSING_DOCUMENT_PARAM);
		}

		Record documentRecord = recordServices.getDocumentById(documentId);
		if (documentRecord == null) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_BAD_REQUEST, INVALID_DOCUMENT_PARAM);
		}

		initWithCollection(documentRecord.getCollection());

		User user = userServices.getUserInCollection(username, documentRecord.getCollection());
		if (!user.hasWriteAccess().on(documentRecord) ||
			!user.has(RMPermissionsTo.GENERATE_EXTERNAL_SIGNATURE_URL).globally()) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED);
		}

		Document document = rm.wrapDocument(documentRecord);
		if (document == null) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_UNAUTHORIZED, INVALID_DOCUMENT_PARAM);
		}

		if (!documentRecordActionsServices.isGenerateExternalSignatureUrlActionPossible(documentRecord, user)) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_UNAUTHORIZED, ACTION_IMPOSSIBLE);
		}

		if (StringUtils.isBlank(expirationDate)) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_BAD_REQUEST, MISSING_DATE_PARAM);
		}

		LocalDate convertedDate;
		try {
			convertedDate = new LocalDate(expirationDate);
		} catch (Exception e) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_UNAUTHORIZED, INVALID_DATE_PARAM);
		}

		try {
			return documentMenuItemActionBehaviors.createExternalSignatureUrl(documentId, convertedDate);
		} catch (RecordServicesException e) {
			throw new SignatureExternalAccessServiceException(HttpServletResponse.SC_UNAUTHORIZED, CANNOT_SAVE_RECORD);
		}
	}
}
