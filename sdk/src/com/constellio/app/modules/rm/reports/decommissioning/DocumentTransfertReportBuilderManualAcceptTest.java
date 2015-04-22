/*Constellio Enterprise Information Management

Copyright (c) 2015 "Constellio inc."

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.constellio.app.modules.rm.reports.decommissioning;

import java.util.Arrays;

import org.junit.Test;

import com.constellio.app.modules.rm.reports.builders.decommissioning.DocumentTransfertReportBuilder;
import com.constellio.app.modules.rm.reports.model.decommissioning.DocumentReportModel;
import com.constellio.app.modules.rm.reports.model.decommissioning.DocumentReportModel.DocumentTransfertModel_Calendar;
import com.constellio.app.modules.rm.reports.model.decommissioning.DocumentReportModel.DocumentTransfertModel_Document;
import com.constellio.app.modules.rm.reports.model.decommissioning.DocumentReportModel.DocumentTransfertModel_Identification;
import com.constellio.app.modules.rm.reports.model.decommissioning.ReportBooleanField;
import com.constellio.app.reports.builders.administration.plan.ReportBuilderTestFramework;
import com.constellio.data.utils.TimeProvider;

public class DocumentTransfertReportBuilderManualAcceptTest extends ReportBuilderTestFramework {

	@Test
	public void verify_report_has_one_page_and_informations_in_all_fields() {

		DocumentReportModel model = new DocumentReportModel();

		createCalendar(model);
		createIdentification(model);
		createDocumentList(model, 80);

		build(new DocumentTransfertReportBuilder(model, getIOLayerFactory().newIOServices(),
				getModelLayerFactory().getFoldersLocator()));
	}

	private void createCalendar(DocumentReportModel model) {
		DocumentTransfertModel_Calendar calendarModel = model.getCalendarModel();

		calendarModel.setCalendarNumber("004-005");
		calendarModel.setRuleNumber("006");
		calendarModel.setSemiActiveRange("30");
		calendarModel.setDispositionYear("2019");
		calendarModel.setConservationDisposition(
				Arrays.asList(new ReportBooleanField("Destruction -> D", true),
						new ReportBooleanField("Tri/T", false),
						new ReportBooleanField("Conservation -> C", false)));
		calendarModel.setSupports(
				Arrays.asList(new ReportBooleanField("DM", false),
						new ReportBooleanField("PA", true)));

		calendarModel.setQuantity("1");
		calendarModel.setExtremeDate("2004-2005");
	}

	private void createIdentification(DocumentReportModel model) {
		DocumentTransfertModel_Identification identificationModel = model.getIdentificationModel();
		identificationModel.setSentDate(TimeProvider.getLocalDateTime().toString());
		identificationModel.setBoxNumber("Transf2014-899-01");
		identificationModel.setOrganisationName("MSE");
		identificationModel.setPublicOrganisationNumber("001");
		identificationModel.setEspaceClassement("MSEE");
		identificationModel.setCodeEspaceClassement("003");
		identificationModel.setAdministrationAddress("230 rue du Minist�re \n Local 20304 20eme etage \n Departement MS local 3");
		identificationModel.setResponsible("Dakota Lindien");
		identificationModel.setFunction("Charg� de projet");
		identificationModel.setEmail("dakota.lindien@mse.gc.ca");
		identificationModel.setPhoneNumber("111-222-3333");
	}

	private void createDocumentList(DocumentReportModel model, int numberDocument) {

		DocumentTransfertModel_Document docModel = new DocumentTransfertModel_Document();

		docModel.setCode("14 3100");
		docModel.setDelayNumber("");
		docModel.setReferenceId("80");
		docModel.setTitle("Dossier papier 24");
		docModel.setStartingYear("2004");
		docModel.setEndingYear("2005");

		for (int index = 0; index < numberDocument; ++index) {
			model.addDocument(docModel);
		}
	}
}



