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
package com.constellio.app.modules.rm.reports.administration.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.constellio.app.modules.rm.reports.builders.administration.plan.AdministrativeUnitReportBuilder;
import com.constellio.app.modules.rm.reports.builders.administration.plan.ClassificationPlanReportBuilder;
import com.constellio.app.modules.rm.reports.builders.administration.plan.UserReportBuilder;
import com.constellio.app.modules.rm.reports.model.administration.plan.AdministrativeUnitReportModel;
import com.constellio.app.modules.rm.reports.model.administration.plan.AdministrativeUnitReportModel.AdministrativeUnitReportModel_AdministrativeUnit;
import com.constellio.app.modules.rm.reports.model.administration.plan.AdministrativeUnitReportModel.AdministrativeUnitReportModel_FilingSpace;
import com.constellio.app.modules.rm.reports.model.administration.plan.AdministrativeUnitReportModel.AdministrativeUnitReportModel_User;
import com.constellio.app.modules.rm.reports.model.administration.plan.ClassificationPlanReportModel;
import com.constellio.app.modules.rm.reports.model.administration.plan.UserReportModel;
import com.constellio.app.modules.rm.reports.model.administration.plan.UserReportModel.UserReportModel_AdministrativeUnit;
import com.constellio.app.modules.rm.reports.model.administration.plan.UserReportModel.UserReportModel_FilingSpace;
import com.constellio.app.modules.rm.reports.model.administration.plan.UserReportModel.UserReportModel_User;
import com.constellio.app.reports.builders.administration.plan.ReportBuilderTestFramework;

public class UserReportBuilderManualAcceptTest extends ReportBuilderTestFramework {

	UserReportModel model;

	@Before
	public void setUp()
			throws Exception {
	}

	@Test
	public void whenBuildEmptyClassificationPlanReportThenOk() {
		model = new UserReportModel();
		build(new UserReportBuilder(model,
				getModelLayerFactory().getFoldersLocator()));
	}

	@Test
	public void whenBuildDetailedClassificationPlanReportThenOk() {
		model = configAdminUnits();
		build(new UserReportBuilder(model,
				getModelLayerFactory().getFoldersLocator()));
	}

	private UserReportModel configAdminUnits() {
		
		UserReportModel model = new UserReportModel();
		
		UserReportModel_User user1 = new UserReportModel_User();
		
			List<UserReportModel_AdministrativeUnit> administrativeUnits01 = new ArrayList<UserReportModel_AdministrativeUnit>();
			UserReportModel_AdministrativeUnit administrativeUnits01_adminUnit01 = new UserReportModel_AdministrativeUnit();
			administrativeUnits01_adminUnit01.setCode("0111").setLabel("MCC").setDescription("");
			administrativeUnits01.add(administrativeUnits01_adminUnit01);
			
			List<UserReportModel_FilingSpace> filingSpaces01 =  new ArrayList<UserReportModel_FilingSpace>();
			UserReportModel_FilingSpace filingSpaces01_fillingSpace01 = new UserReportModel_FilingSpace();
			filingSpaces01_fillingSpace01.setCode("101").setLabel("Cent un").setDescription("");
			filingSpaces01.add(filingSpaces01_fillingSpace01);
			
			user1.setAdministrativeUnits(administrativeUnits01);
			user1.setFilingSpaces(filingSpaces01);
		
		UserReportModel_User user2 = new UserReportModel_User();
		
			List<UserReportModel_AdministrativeUnit> administrativeUnits02 = new ArrayList<UserReportModel_AdministrativeUnit>();
			
			UserReportModel_AdministrativeUnit administrativeUnits02_adminUnit01 = new UserReportModel_AdministrativeUnit();
			administrativeUnits02_adminUnit01.setCode("A-Salle").setLabel("Planification des ressources humaines").setDescription("");
			UserReportModel_AdministrativeUnit administrativeUnits02_adminUnit02 = new UserReportModel_AdministrativeUnit();
			administrativeUnits02_adminUnit02.setCode("B-Salle").setLabel("Organisation des ressources humaines").setDescription("");
			UserReportModel_AdministrativeUnit administrativeUnits02_adminUnit03 = new UserReportModel_AdministrativeUnit();
			administrativeUnits02_adminUnit03.setCode("C-Salle").setLabel("Administration des ressources humaines").setDescription("");
			UserReportModel_AdministrativeUnit administrativeUnits02_adminUnit04 = new UserReportModel_AdministrativeUnit();
			administrativeUnits02_adminUnit04.setCode("D-Salle").setLabel("Contrôle des ressources humaines").setDescription("");
			
			administrativeUnits02.add(administrativeUnits02_adminUnit01);
			administrativeUnits02.add(administrativeUnits02_adminUnit02);
			administrativeUnits02.add(administrativeUnits02_adminUnit03);
			administrativeUnits02.add(administrativeUnits02_adminUnit04);
		
			List<UserReportModel_FilingSpace> filingSpaces02 =  new ArrayList<UserReportModel_FilingSpace>();
			UserReportModel_FilingSpace filingSpaces02_fillingSpace01 = new UserReportModel_FilingSpace();
			filingSpaces01_fillingSpace01.setCode("66666").setLabel("Aux délices du jour").setDescription("");
			filingSpaces02.add(filingSpaces02_fillingSpace01);
		
		user2.setAdministrativeUnits(administrativeUnits02);
		user2.setFilingSpaces(filingSpaces02);
		
		UserReportModel_User user3 = new UserReportModel_User();
		
		UserReportModel_User user4 = new UserReportModel_User();
		
			List<UserReportModel_AdministrativeUnit> administrativeUnits04 = new ArrayList<UserReportModel_AdministrativeUnit>();
			UserReportModel_AdministrativeUnit administrativeUnits04_adminUnit01 = new UserReportModel_AdministrativeUnit();
			administrativeUnits04_adminUnit01.setCode("3700").setLabel("Direction générale du suivi des risques organisationnels et de la mesure de la performance - poste").setDescription("");
			administrativeUnits04.add(administrativeUnits04_adminUnit01);
		
		user4.setAdministrativeUnits(administrativeUnits04);
		
		UserReportModel_User user5 = new UserReportModel_User();
		user5.setAdministrativeUnits(administrativeUnits01);
		
		UserReportModel_User user6 = new UserReportModel_User();
		user6.setFilingSpaces(filingSpaces01);
		
		UserReportModel_User user7 = new UserReportModel_User();
		
			List<UserReportModel_AdministrativeUnit> administrativeUnits07 = new ArrayList<UserReportModel_AdministrativeUnit>();
			administrativeUnits07.add(administrativeUnits02_adminUnit01);
			
			List<UserReportModel_FilingSpace> filingSpaces07 =  new ArrayList<UserReportModel_FilingSpace>();
			UserReportModel_FilingSpace filingSpaces07_fillingSpace01 = new UserReportModel_FilingSpace();
			filingSpaces07_fillingSpace01.setCode("123").setLabel("123").setDescription("");
			filingSpaces07.add(filingSpaces07_fillingSpace01);
		
		user7.setAdministrativeUnits(administrativeUnits07);
		user7.setFilingSpaces(filingSpaces07);
		
		
		UserReportModel_User user8 = new UserReportModel_User();
		
			List<UserReportModel_FilingSpace> filingSpaces08 =  new ArrayList<UserReportModel_FilingSpace>();
			UserReportModel_FilingSpace filingSpaces08_fillingSpace01 = new UserReportModel_FilingSpace();
			filingSpaces08_fillingSpace01.setCode("1641").setLabel("Service conseil en communication externe").setDescription("");
			UserReportModel_FilingSpace filingSpaces08_fillingSpace02 = new UserReportModel_FilingSpace();
			filingSpaces08_fillingSpace02.setCode("3320-1").setLabel("Dotation").setDescription("");
			
			filingSpaces08.add(filingSpaces08_fillingSpace01);
			filingSpaces08.add(filingSpaces08_fillingSpace02);
		
		user8.setFilingSpaces(filingSpaces08);
		
		UserReportModel_User user9 = new UserReportModel_User();
		
			List<UserReportModel_FilingSpace> filingSpaces09 =  new ArrayList<UserReportModel_FilingSpace>();
			UserReportModel_FilingSpace filingSpaces09_fillingSpace01 = new UserReportModel_FilingSpace();
			filingSpaces09_fillingSpace01.setCode("7000-1").setLabel("Permis").setDescription("");
			filingSpaces09.add(filingSpaces09_fillingSpace01);
		
		user9.setFilingSpaces(filingSpaces09);
		
		user1.setUserId("37").setLastName("admin2").setFirstName("admin2").setUserName("admin2").setStatus("Actif").setUnit("0000");
		user2.setUserId("3").setLastName("Bissonnette").setFirstName("Natalie").setUserName("NBisonette").setStatus("Actif").setUnit("RH");
		user3.setUserId("29").setLastName("Blais").setFirstName("Maud").setUserName("Maud").setStatus("Actif").setUnit("");
		
		user4.setUserId("34").setLastName("Bolduc").setFirstName("Christian").setUserName("CBolduc").setStatus("Actif").setUnit("UA 3700");
		user5.setUserId("9").setLastName("Bouchard").setFirstName("Marc").setUserName("MBouchard").setStatus("Actif").setUnit("0000");
		user6.setUserId("30").setLastName("Chapdelaine").setFirstName("Maria").setUserName("MChapdelaine").setStatus("Actif").setUnit("CAF");
		
		user7.setUserId("35").setLastName("Couture").setFirstName("Mélanie").setUserName("couture_mt").setStatus("Actif").setUnit("RH");
		user8.setUserId("16").setLastName("Couture").setFirstName("Nadia").setUserName("CON16").setStatus("Actif").setUnit("UA 1641");
		user9.setUserId("39").setLastName("Voyer").setFirstName("Daniel").setUserName("vod00").setStatus("Actif").setUnit("7000");
		
		ArrayList<UserReportModel_User> users = new ArrayList<UserReportModel_User>();
		
		users.add(user1);
		users.add(user2);
		users.add(user3);
		
		users.add(user4);
		users.add(user5);
		users.add(user6);
		
		users.add(user7);
		users.add(user8);
		users.add(user9);
		
		model.setUsers(users);
		
		return model;
	}

}
