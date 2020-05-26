package com.constellio.app.modules.restapi.certification.dao;

import com.constellio.app.modules.restapi.certification.dto.CertificationDto;
import com.constellio.app.modules.restapi.certification.dto.RectangleDto;
import com.constellio.app.modules.restapi.certification.dto.SignatureDto;
import com.constellio.app.modules.restapi.document.exception.DocumentContentNotFoundException;
import com.constellio.app.modules.restapi.resource.dao.ResourceDao;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.type.DocumentType;
import com.constellio.app.services.signature.PdfDocumentCertifyService;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.ContentVersion;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.pdftron.PdfSignatureAnnotation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CertificationDao extends ResourceDao {

	private static final String LAST_VERSION = "last";

	private PdfDocumentCertifyService pdfService;

	public List<PdfSignatureAnnotation> createCertification(User user, CertificationDto certification, String flush,
															Record document) throws Exception {
		Content content = getMetadataValue(document, Document.CONTENT);
		List<PdfSignatureAnnotation> listSignature = new ArrayList<>();
		if (content == null) {
			throw new DocumentContentNotFoundException(document.getId(), LAST_VERSION);
		}
		ContentVersion contentVersionVO = content.getLastMajorContentVersion();

		if (this.pdfService == null) {
			pdfService = new PdfDocumentCertifyService(this.appLayerFactory, document.getCollection(), document.getId(), document.getTypeCode(),
					contentVersionVO, user);
		}
		for (SignatureDto signature : certification.getSignatures()) {
			PdfSignatureAnnotation certified = new PdfSignatureAnnotation(signature.getPage(), convertRectangle(signature.getPosition()),
					certification.getUserId(), certification.getUsername(), certification.getImageData());
			listSignature.add(certified);
		}
		pdfService.certifyAndSign(certification.getFileAsStr(), listSignature);

		return listSignature;
	}

	private Rectangle convertRectangle(RectangleDto rectangleDto) {
		return new Rectangle((int) rectangleDto.getX(), (int) rectangleDto.getY(), (int) rectangleDto.getWidth(), (int) rectangleDto.getHeight());
	}

	@Override
	protected String getResourceSchemaType() {
		return Document.SCHEMA_TYPE;
	}

	@Override
	protected String getResourceTypeSchemaType() {
		return DocumentType.SCHEMA_TYPE;
	}
}
