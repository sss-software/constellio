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
package com.constellio.app.modules.rm;

import static com.constellio.app.modules.rm.model.enums.CopyType.PRINCIPAL;
import static com.constellio.app.modules.rm.model.enums.CopyType.SECONDARY;
import static com.constellio.app.modules.rm.model.enums.DecommissioningListType.FOLDERS_TO_CLOSE;
import static com.constellio.app.modules.rm.model.enums.DecommissioningListType.FOLDERS_TO_DEPOSIT;
import static com.constellio.app.modules.rm.model.enums.DecommissioningListType.FOLDERS_TO_DESTROY;
import static com.constellio.app.modules.rm.model.enums.DecommissioningListType.FOLDERS_TO_TRANSFER;
import static com.constellio.app.modules.rm.model.enums.DecommissioningType.DEPOSIT;
import static com.constellio.app.modules.rm.model.enums.DecommissioningType.DESTRUCTION;
import static com.constellio.app.modules.rm.model.enums.DecommissioningType.TRANSFERT_TO_SEMI_ACTIVE;
import static java.util.Arrays.asList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.constellio.app.modules.rm.constants.RMRoles;
import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.model.enums.DecommissioningListType;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.logging.DecommissioningLoggingService;
import com.constellio.app.modules.rm.wrappers.DecommissioningList;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.modules.rm.wrappers.structures.DecomListFolderDetail;
import com.constellio.model.entities.batchprocess.BatchProcess;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.RecordWrapper;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.Authorization;
import com.constellio.model.entities.security.AuthorizationDetails;
import com.constellio.model.entities.security.CustomizedAuthorizationsBehavior;
import com.constellio.model.entities.security.Role;
import com.constellio.model.services.batch.manager.BatchProcessesManager;
import com.constellio.model.services.configs.SystemConfigurationsManager;
import com.constellio.model.services.contents.ContentManager;
import com.constellio.model.services.contents.ContentVersionDataSummary;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.logging.LoggingServices;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.security.AuthorizationsServices;
import com.constellio.model.services.security.roles.RolesManager;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.setups.Users;

public class DemoTestRecords {

	private static int id = 4200;

	private static String nextId() {
		String idWithTooMuchZeros = "0000000" + (id++);
		return idWithTooMuchZeros.substring(idWithTooMuchZeros.length() - 10);
	}

	//1. Changer tous les ids hardcodés par des nextId();

	public final String categoryId_20 = nextId();
	public final String categoryId_21 = nextId();
	public final String categoryId_231 = nextId();
	public final String categoryId_232 = nextId();
	public final String categoryId_233 = nextId();
	public final String categoryId_234 = nextId();
	public final String categoryId_22 = nextId();
	public final String categoryId_23 = nextId();

	public final String categoryId_10 = nextId();
	public final String categoryId14 = nextId();
	public final String categoryId_1000 = nextId();
	public final String categoryId_1100 = nextId();
	public final String categoryId_2000 = nextId();
	public final String categoryId_2100 = nextId();
	public final String categoryId_2110 = nextId();
	public final String categoryId_2120 = nextId();
	public final String categoryId_2130 = nextId();
	public final String categoryId_3000 = nextId();
	public final String categoryId_3100 = nextId();
	public final String categoryId_3200 = nextId();
	public final String categoryId_4000 = nextId();
	public final String categoryId_4100 = nextId();
	public final String categoryId_4200 = nextId();
	public final String categoryId_13 = nextId();

	public final String unitId_10 = nextId();
	public final String unitId_10A = nextId();
	public final String unitId_10B = nextId();
	public final String unitId_10C = nextId();
	public final String unitId_10D = nextId();
	public final String unitId_10E = nextId();

	public final String subdivId_1 = nextId();
	public final String subdivId_2 = nextId();
	public final String subdivId_3 = nextId();

	public final String filingId_A = nextId();
	public final String filingId_B = nextId();
	public final String filingId_C = nextId();
	public final String filingId_D = nextId();
	public final String filingId_E = nextId();

	public final String ruleId_1 = nextId();
	public final String ruleId_2 = nextId();
	public final String ruleId_3 = nextId();
	public final String ruleId_4 = nextId();

	public final String storageSpaceId_S01 = nextId();
	public final String storageSpaceId_S01_01 = nextId();
	public final String storageSpaceId_S01_02 = nextId();
	public final String storageSpaceId_S02 = nextId();
	public final String storageSpaceId_S02_01 = nextId();
	public final String storageSpaceId_S02_02 = nextId();

	public final String containerTypeId_boite22x22 = nextId();

	public final String containerId_bac19 = nextId();
	public final String containerId_bac18 = nextId();
	public final String containerId_bac17 = nextId();
	public final String containerId_bac16 = nextId();
	public final String containerId_bac15 = nextId();
	public final String containerId_bac14 = nextId();

	public final String containerId_bac13 = nextId();
	public final String containerId_bac12 = nextId();
	public final String containerId_bac11 = nextId();
	public final String containerId_bac10 = nextId();
	public final String containerId_bac09 = nextId();
	public final String containerId_bac08 = nextId();
	public final String containerId_bac07 = nextId();
	public final String containerId_bac06 = nextId();
	public final String containerId_bac05 = nextId();
	public final String containerId_bac04 = nextId();
	public final String containerId_bac03 = nextId();
	public final String containerId_bac02 = nextId();
	public final String containerId_bac01 = nextId();

	public String PA;
	public String MV;
	public String MD;
	public List<String> PA_MD;
	public final String folder_A01 = nextId();
	public final String folder_A02 = nextId();
	public final String folder_A03 = nextId();
	public final String folder_A04 = nextId();
	public final String folder_A05 = nextId();
	public final String folder_A06 = nextId();
	public final String folder_A07 = nextId();
	public final String folder_A08 = nextId();
	public final String folder_A09 = nextId();
	public final String folder_A10 = nextId();
	public final String folder_A11 = nextId();
	public final String folder_A12 = nextId();
	public final String folder_A13 = nextId();
	public final String folder_A14 = nextId();
	public final String folder_A15 = nextId();
	public final String folder_A16 = nextId();
	public final String folder_A17 = nextId();
	public final String folder_A18 = nextId();
	public final String folder_A19 = nextId();
	public final String folder_A20 = nextId();
	public final String folder_A21 = nextId();
	public final String folder_A22 = nextId();
	public final String folder_A23 = nextId();
	public final String folder_A24 = nextId();
	public final String folder_A25 = nextId();
	public final String folder_A26 = nextId();
	public final String folder_A27 = nextId();
	public final String folder_A42 = nextId();
	public final String folder_A43 = nextId();
	public final String folder_A44 = nextId();
	public final String folder_A45 = nextId();
	public final String folder_A46 = nextId();
	public final String folder_A47 = nextId();
	public final String folder_A48 = nextId();
	public final String folder_A49 = nextId();
	public final String folder_A50 = nextId();
	public final String folder_A51 = nextId();
	public final String folder_A52 = nextId();
	public final String folder_A53 = nextId();
	public final String folder_A54 = nextId();
	public final String folder_A55 = nextId();
	public final String folder_A56 = nextId();
	public final String folder_A57 = nextId();
	public final String folder_A58 = nextId();
	public final String folder_A59 = nextId();
	public final String folder_A79 = nextId();
	public final String folder_A80 = nextId();
	public final String folder_A81 = nextId();
	public final String folder_A82 = nextId();
	public final String folder_A83 = nextId();
	public final String folder_A84 = nextId();
	public final String folder_A85 = nextId();
	public final String folder_A86 = nextId();
	public final String folder_A87 = nextId();
	public final String folder_A88 = nextId();
	public final String folder_A89 = nextId();
	public final String folder_A90 = nextId();
	public final String folder_A91 = nextId();
	public final String folder_A92 = nextId();
	public final String folder_A93 = nextId();
	public final String folder_A94 = nextId();
	public final String folder_A95 = nextId();
	public final String folder_A96 = nextId();
	public final String folder_B01 = nextId();
	public final String folder_B02 = nextId();
	public final String folder_B03 = nextId();
	public final String folder_B04 = nextId();
	public final String folder_B05 = nextId();
	public final String folder_B06 = nextId();
	public final String folder_B07 = nextId();
	public final String folder_B08 = nextId();
	public final String folder_B09 = nextId();
	public final String folder_B30 = nextId();
	public final String folder_B31 = nextId();
	public final String folder_B32 = nextId();
	public final String folder_B33 = nextId();
	public final String folder_B34 = nextId();
	public final String folder_B35 = nextId();
	public final String folder_B50 = nextId();
	public final String folder_B51 = nextId();
	public final String folder_B52 = nextId();
	public final String folder_B53 = nextId();
	public final String folder_B54 = nextId();
	public final String folder_B55 = nextId();
	public final String folder_C01 = nextId();
	public final String folder_C02 = nextId();
	public final String folder_C03 = nextId();
	public final String folder_C04 = nextId();
	public final String folder_C05 = nextId();
	public final String folder_C06 = nextId();
	public final String folder_C07 = nextId();
	public final String folder_C08 = nextId();
	public final String folder_C09 = nextId();
	public final String folder_C30 = nextId();
	public final String folder_C31 = nextId();
	public final String folder_C32 = nextId();
	public final String folder_C33 = nextId();
	public final String folder_C34 = nextId();
	public final String folder_C35 = nextId();
	public final String folder_C50 = nextId();
	public final String folder_C51 = nextId();
	public final String folder_C52 = nextId();
	public final String folder_C53 = nextId();
	public final String folder_C54 = nextId();
	public final String folder_C55 = nextId();

	public final String list_01 = nextId();
	public final String list_02 = nextId();
	public final String list_03 = nextId();
	public final String list_04 = nextId();
	public final String list_05 = nextId();
	public final String list_06 = nextId();
	public final String list_07 = nextId();
	public final String list_08 = nextId();
	public final String list_09 = nextId();
	public final String list_10 = nextId();
	public final String list_11 = nextId();
	public final String list_12 = nextId();
	public final String list_13 = nextId();
	public final String list_14 = nextId();
	public final String list_15 = nextId();
	public final String list_16 = nextId();

	private String collection;

	private RMSchemasRecordsServices schemas;

	private String alice_notInCollection;

	private String admin_userIdWithAllAccess;

	private String bob_userInAC;

	private String charles_userInA;

	private String dakota_managerInA_userInB;

	private String edouard_managerInB_userInC;

	private String gandalf_managerInABC;

	private String chuckNorris;

	private Users users = new Users();

	private RecordServices recordServices;

	private LoggingServices loggingServices;

	private DecommissioningLoggingService decommissioningLoggingService;

	private SystemConfigurationsManager systemConfigurationsManager;
	private SearchServices searchServices;
	private ContentManager contentManager;

	public DemoTestRecords(String collection) {
		this.collection = collection;
	}

	public DemoTestRecords setup(ModelLayerFactory modelLayerFactory)
			throws RecordServicesException {

		UserServices userServices = modelLayerFactory.newUserServices();
		users.setUp(userServices).withPasswords(modelLayerFactory.newAuthenticationService());

		schemas = new RMSchemasRecordsServices(collection, modelLayerFactory);

		recordServices = modelLayerFactory.newRecordServices();

		loggingServices = modelLayerFactory.newLoggingServices();

		contentManager = modelLayerFactory.getContentManager();

		decommissioningLoggingService = new DecommissioningLoggingService(modelLayerFactory);

		searchServices = modelLayerFactory.newSearchServices();

		PA = schemas.getMediumTypeByCode("PA").getId();
		MD = schemas.getMediumTypeByCode("DM").getId();
		MV = schemas.getMediumTypeByCode("FI").getId();
		PA_MD = asList(PA, MD);

		Transaction transaction = new Transaction();
		setupUsers(transaction, userServices);
		setupCategories(transaction);
		setupUniformSubdivisions(transaction);
		setupFilingSpaces(transaction);
		setupAdministrativeUnits(transaction);
		setupRetentionRules(transaction);

		recordServices.execute(transaction);

		setupAuthorizations(modelLayerFactory.newAuthorizationsServices(), modelLayerFactory.getRolesManager());
		waitForBatchProcesses(modelLayerFactory.getBatchProcessesManager());
		systemConfigurationsManager = modelLayerFactory.getSystemConfigurationsManager();

		return this;
	}

	private void waitForBatchProcesses(BatchProcessesManager batchProcessesManager) {
		for (BatchProcess batchProcess : batchProcessesManager.getAllNonFinishedBatchProcesses()) {
			batchProcessesManager.waitUntilFinished(batchProcess);
		}
	}

	private void setupAuthorizations(AuthorizationsServices authorizationsServices, RolesManager rolesManager) {

		Authorization unit10Users = new Authorization();
		unit10Users.setDetail(AuthorizationDetails.create("unit10Users", asList(Role.WRITE), collection));
		unit10Users.setGrantedOnRecords(asList(unitId_10));
		unit10Users.setGrantedToPrincipals(asList(bob_userInAC, charles_userInA, dakota_managerInA_userInB));
		authorizationsServices.add(unit10Users, CustomizedAuthorizationsBehavior.KEEP_ATTACHED, null);

		Authorization unit10Managers = new Authorization();
		unit10Managers.setDetail(AuthorizationDetails.create("unit10Managers", asList(Role.DELETE), collection));
		unit10Managers.setGrantedOnRecords(asList(unitId_10));
		unit10Managers
				.setGrantedToPrincipals(asList(dakota_managerInA_userInB, edouard_managerInB_userInC, gandalf_managerInABC));
		authorizationsServices.add(unit10Managers, CustomizedAuthorizationsBehavior.KEEP_ATTACHED, null);

		Authorization unit30Users = new Authorization();
		unit30Users.setDetail(AuthorizationDetails.create("unit30Users", asList(Role.WRITE), collection));
		unit30Users.setGrantedOnRecords(asList(unitId_10));
		unit30Users.setGrantedToPrincipals(asList(bob_userInAC, edouard_managerInB_userInC));
		authorizationsServices.add(unit30Users, CustomizedAuthorizationsBehavior.KEEP_ATTACHED, null);

		Authorization unit30Managers = new Authorization();
		unit30Managers.setDetail(AuthorizationDetails.create("unit30Managers", asList(Role.DELETE), collection));
		unit30Managers.setGrantedOnRecords(asList(unitId_10));
		unit30Managers.setGrantedToPrincipals(asList(gandalf_managerInABC));
		authorizationsServices.add(unit30Managers, CustomizedAuthorizationsBehavior.KEEP_ATTACHED, null);

	}

	private void setupUsers(Transaction transaction, UserServices userServices) {

		userServices.addUserToCollection(users.admin(), collection);
		userServices.addUserToCollection(users.bob(), collection);
		userServices.addUserToCollection(users.charles(), collection);
		userServices.addUserToCollection(users.dakotaLIndien(), collection);
		userServices.addUserToCollection(users.edouardLechat(), collection);
		userServices.addUserToCollection(users.gandalfLeblanc(), collection);
		userServices.addUserToCollection(users.chuckNorris(), collection);

		alice_notInCollection = users.alice().getUsername();

		LocalDateTime now = new LocalDateTime();

		admin_userIdWithAllAccess = transaction.add(users.adminIn(collection)).setCollectionDeleteAccess(true)
				.setCollectionReadAccess(true).setCollectionWriteAccess(true).setUserRoles(asList(RMRoles.RGD))
				.setLastLogin(now).getId();

		bob_userInAC = transaction.add(users.bobIn(collection)).setLastLogin(now).getId();
		charles_userInA = transaction.add(users.charlesIn(collection)).setLastLogin(now).getId();
		dakota_managerInA_userInB = transaction.add(users.dakotaLIndienIn(collection)).setLastLogin(now).getId();
		edouard_managerInB_userInC = transaction.add(users.edouardLechatIn(collection)).setLastLogin(now).getId();
		gandalf_managerInABC = transaction.add(users.gandalfLeblancIn(collection)).setLastLogin(now).getId();
		chuckNorris = transaction.add(users.chuckNorrisIn(collection).setCollectionAllAccess(true)).setLastLogin(now).getId();

	}

	private void setupCategories(Transaction transaction) {

		transaction.add(schemas.newCategoryWithId(categoryId_10).setCode("10").setTitle("Gestion Interne"));
		//Ressources Humaines
		transaction.add(schemas.newCategoryWithId(categoryId14).setCode("14").setTitle("Gestion des Ressources Humaines")
				.setParent(categoryId_10).setRetentionRules(asList(ruleId_1)));
		transaction
				.add(schemas.newCategoryWithId(categoryId_1000).setCode("1000").setTitle("Planification des Ressources Humaines")
						.setParent(categoryId14).setRetentionRules(asList(ruleId_1, ruleId_2)));
		transaction.add(schemas.newCategoryWithId(categoryId_1100).setCode("1100")
				.setTitle("Analyse des besoins des Ressources Humaines").setParent(categoryId_1000));
		transaction.add(
				schemas.newCategoryWithId(categoryId_2000).setCode("2000").setTitle("Organisation des Ressources Humaines")
						.setParent(categoryId14).setRetentionRules(asList(ruleId_4)));
		transaction.add(schemas.newCategoryWithId(categoryId_2100).setCode("2100").setTitle("Embauche du Personnel")
				.setParent(categoryId_2000));
		transaction.add(schemas.newCategoryWithId(categoryId_2110).setCode("2110").setTitle("Recrutement à l'interne")
				.setParent(categoryId_2100));
		transaction.add(schemas.newCategoryWithId(categoryId_2120).setCode("2120").setTitle("Recrutement à l'externe")
				.setParent(categoryId_2100));
		transaction.add(schemas.newCategoryWithId(categoryId_2130).setCode("2130").setTitle("Affichage de Postes")
				.setParent(categoryId_2100));
		transaction.add(schemas.newCategoryWithId(categoryId_3000).setCode("3000")
				.setTitle("Administration des Ressources Humaines").setParent(categoryId14));
		transaction.add(schemas.newCategoryWithId(categoryId_3100).setCode("3100").setTitle("Dossiers du Personnel")
				.setParent(categoryId_3000));
		transaction.add(schemas.newCategoryWithId(categoryId_3200).setCode("3200")
				.setTitle("Formation et Perfectionnement du Personnel").setParent(categoryId_3000));
		transaction.add(schemas.newCategoryWithId(categoryId_4000).setCode("4000").setTitle("Contrôle des Ressources Humaines")
				.setParent(categoryId14));
		transaction
				.add(schemas.newCategoryWithId(categoryId_4100).setCode("4100").setTitle("Évaluation des Ressources Humaines")
						.setParent(categoryId_4000));
		transaction.add(schemas.newCategoryWithId(categoryId_4200).setCode("4200").setTitle("Mouvement du Personnel")
				.setParent(categoryId_4000));
		//Web
		transaction.add(schemas.newCategoryWithId(categoryId_13).setCode("13").setTitle("Gestion du Site vitrine")
				.setParent(categoryId_10).setRetentionRules(asList(ruleId_1, ruleId_2, ruleId_3, ruleId_4)));

		transaction.add(schemas.newCategoryWithId(categoryId_20).setCode("20").setTitle("Gestion Externe"));
		//Clients
		transaction.add(schemas.newCategoryWithId(categoryId_21).setCode("21").setTitle("Gestion des fichiers Clients")
				.setParent(categoryId_20));
		//Fournisseurs
		transaction.add(schemas.newCategoryWithId(categoryId_22).setCode("22").setTitle("Gestion des fichiers Fournisseurs")
				.setParent(categoryId_20));
		//Assurances
		transaction.add(schemas.newCategoryWithId(categoryId_23).setCode("23").setTitle("Gestion des fichiers d'Assurance")
				.setParent(categoryId_20).setRetentionRules(asList(ruleId_1, ruleId_2, ruleId_3, ruleId_4)));
		transaction.add(schemas.newCategoryWithId(categoryId_231).setCode("231").setTitle("Gestion des voitures de fonction")
				.setParent(categoryId_23).setRetentionRules(asList(ruleId_2)));
		transaction
				.add(schemas.newCategoryWithId(categoryId_232).setCode("232").setTitle("Gestion des dossiers santé des salariés")
						.setParent(categoryId_23));
		transaction.add(schemas.newCategoryWithId(categoryId_233).setCode("233").setTitle("Rapports d'Accidents")
				.setParent(categoryId_23).setRetentionRules(asList(ruleId_3)));
		transaction
				.add(schemas.newCategoryWithId(categoryId_234).setCode("234").setTitle("Contrats").setParent(categoryId_23)
						.setRetentionRules(asList(ruleId_3)));

	}

	private void setupAdministrativeUnits(Transaction transaction) {
		transaction.add(schemas.newAdministrativeUnitWithId(unitId_10)).setCode("RH").setTitle("Ressources humaines")
				.setAdress("1265 Charest O, Suite 1040");

		transaction.add(schemas.newAdministrativeUnitWithId(unitId_10A)).setCode("A")
				.setTitle("Salle A - Planification des Ressources Humaines")
				.setFilingSpaces(asList(filingId_A)).setParent(unitId_10).setAdress("1265 Charest O, Suite 1040");

		transaction.add(schemas.newAdministrativeUnitWithId(unitId_10B)).setCode("B")
				.setTitle("Salle B - Organisation des Ressources Humaines")
				.setFilingSpaces(asList(filingId_B)).setParent(unitId_10).setAdress("1265 Charest O, Suite 1040");

		transaction.add(schemas.newAdministrativeUnitWithId(unitId_10C)).setCode("C")
				.setTitle("Salle C - Administration des Ressources Humaines")
				.setFilingSpaces(asList(filingId_C)).setParent(unitId_10).setAdress("1265 Charest O, Suite 1040");

		transaction.add(schemas.newAdministrativeUnitWithId(unitId_10D)).setCode("D")
				.setTitle("Salle D - Contrôle des Ressources Humaines")
				.setFilingSpaces(asList(filingId_D)).setParent(unitId_10).setAdress("1265 Charest O, Suite 1040");

		transaction.add(schemas.newAdministrativeUnitWithId(unitId_10E)).setCode("E").setTitle("Salle E - Dossiers Semi-Actifs")
				.setFilingSpaces(asList(filingId_E)).setParent(unitId_10).setAdress("1265 Charest O, Suite 1040");
	}

	private void setupUniformSubdivisions(Transaction transaction) {
		transaction.add(schemas.newUniformSubdivisionWithId(subdivId_1).setCode("sub1").setTitle("Subdiv. 1")
				.setRetentionRules(asList(ruleId_2)));
		transaction.add(schemas.newUniformSubdivisionWithId(subdivId_2).setCode("sub2").setTitle("Subdiv. 2"));
		transaction.add(schemas.newUniformSubdivisionWithId(subdivId_3).setCode("sub3").setTitle("Subdiv. 3"));
	}

	private void setupFilingSpaces(Transaction transaction) {

		transaction.add(schemas.newFilingSpaceWithId(filingId_A)).setCode("A").setTitle("Salle A")
				.setUsers(asList(bob_userInAC, charles_userInA, admin_userIdWithAllAccess))
				.setDescription("Salle de réunion prévue pour quinze personnes maximum. Disposition en U")
				.setAdministrators(asList(dakota_managerInA_userInB, gandalf_managerInABC, admin_userIdWithAllAccess));

		transaction.add(schemas.newFilingSpaceWithId(filingId_B)).setCode("B").setTitle("Salle B")
				.setUsers(asList(dakota_managerInA_userInB))
				.setDescription("Bureau de Dakota l'Indien")
				.setAdministrators(asList(edouard_managerInB_userInC, gandalf_managerInABC));

		transaction.add(schemas.newFilingSpaceWithId(filingId_C)).setCode("C").setTitle("Salle C")
				.setUsers(asList(edouard_managerInB_userInC, bob_userInAC))
				.setDescription("Bureau de Edouard et Bob")
				.setAdministrators(asList(gandalf_managerInABC));

		transaction.add(schemas.newFilingSpaceWithId(filingId_D)).setCode("D").setTitle("Salle D")
				.setDescription("Salle prévue pour quinze personnes maximum. Avec tableau et rétroprojecteur");

		transaction.add(schemas.newFilingSpaceWithId(filingId_E)).setCode("E").setTitle("Salle E")
				.setDescription("Salle d'archivage des dossiers");

	}

	private void setupRetentionRules(Transaction transaction) {
		CopyRetentionRule principal888_5_C = CopyRetentionRule.newPrincipal(asList(PA, MD), "888-5-C");
		CopyRetentionRule secondary888_0_D = CopyRetentionRule.newSecondary(asList(PA, MD), "888-0-D");
		transaction.add(schemas.newRetentionRuleWithId(ruleId_1)).setCode("1").setTitle("Rule #1")
				.setAdministrativeUnits(asList(unitId_10, unitId_10)).setApproved(true)
				.setCopyRetentionRules(asList(principal888_5_C, secondary888_0_D))
				.setKeywords(asList("Rule #1"))
				.setCorpus("Corpus  Rule 1")
				.setDescription("Description Rule 1")
				.setJuridicReference("Juridic reference Rule 1")
				.setGeneralComment("General Comment Rule 1")
				.setCopyRulesComment(asList("Copy rules comments"));

		CopyRetentionRule principal5_2_T = CopyRetentionRule.newPrincipal(asList(PA, MD), "5-2-T");
		CopyRetentionRule secondary2_0_D = CopyRetentionRule.newSecondary(asList(PA, MD), "2-0-D");
		transaction.add(schemas.newRetentionRuleWithId(ruleId_2)).setCode("2").setTitle("Rule #2")
				.setResponsibleAdministrativeUnits(true).setApproved(true)
				.setCopyRetentionRules(asList(principal5_2_T, secondary2_0_D));

		CopyRetentionRule principal999_4_T = CopyRetentionRule.newPrincipal(asList(PA, MD), "999-4-T");
		CopyRetentionRule secondary1_0_D = CopyRetentionRule.newSecondary(asList(PA, MD), "1-0-D");
		transaction.add(schemas.newRetentionRuleWithId(ruleId_3)).setCode("3").setTitle("Rule #3")
				.setResponsibleAdministrativeUnits(true).setApproved(true)
				.setCopyRetentionRules(asList(principal999_4_T, secondary1_0_D));

		CopyRetentionRule principal_PA_3_888_D = CopyRetentionRule.newPrincipal(asList(PA), "3-888-D");
		CopyRetentionRule principal_MD_3_888_C = CopyRetentionRule.newPrincipal(asList(MD), "3-888-C");
		transaction.add(schemas.newRetentionRuleWithId(ruleId_4)).setCode("4").setTitle("Rule #4")
				.setResponsibleAdministrativeUnits(true).setApproved(true)
				.setCopyRetentionRules(asList(principal_PA_3_888_D, principal_MD_3_888_C, secondary888_0_D));
	}

	public DemoTestRecords withFoldersAndContainersOfEveryStatus() {
		//Calculation of closing date is disabled because we want some folders without close date
		systemConfigurationsManager.setValue(RMConfigs.CALCULATED_CLOSING_DATE, false);

		systemConfigurationsManager.setValue(RMConfigs.YEAR_END_DATE, "10/31");

		Transaction transaction = new Transaction();
		setupStorageSpace(transaction);
		setupContainerTypes(transaction);
		setupContainers(transaction);
		setupFolders(transaction);
		setupDocuments(transaction);
		setupLists(transaction);
		try {
			recordServices.execute(transaction);
		} catch (RecordServicesException e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	private void setupDocuments(Transaction transaction) {

		transaction.add(newDocumentWithContent("cv-EmiliePoulain.odt").setFolder(folder_A01));
		transaction.add(newDocumentWithContent("guide-dev.pdf").setFolder(folder_A03));
		transaction.add(newDocumentWithContent("definition-comptable.pdf").setFolder(folder_A04));
		transaction.add(newDocumentWithContent("definition-commerciaux.pdf").setFolder(folder_A04));
		transaction.add(newDocumentWithContent("info-BastienAugerau.odt").setFolder(folder_A05));
		transaction.add(newDocumentWithContent("info-BastienBernadotte.odt").setFolder(folder_A06));
		transaction.add(newDocumentWithContent("info-BrittanyDaru.odt").setFolder(folder_A07));
		transaction.add(newDocumentWithContent("info-BorisGouvon.odt").setFolder(folder_A08));
		transaction.add(newDocumentWithContent("info-CarolineSuchet.odt").setFolder(folder_A10));
		transaction.add(newDocumentWithContent("info-DocuLibre.odt").setFolder(folder_A11));
		transaction.add(newDocumentWithContent("info-IBM.odt").setFolder(folder_A12));
		transaction.add(newDocumentWithContent("info-Google.odt").setFolder(folder_A13));
		transaction.add(newDocumentWithContent("info-Toyota.pdf").setFolder(folder_A14));
		transaction.add(newDocumentWithContent("assurance-EmiliePoulain.odt").setFolder(folder_A16));
		transaction.add(newDocumentWithContent("formations-internationale.odt").setFolder(folder_A18));

	}

	private Document newDocumentWithContent(String resource) {
		User user = users.adminIn(collection);
		ContentVersionDataSummary version = upload(resource);
		Content content = contentManager.createMajor(user, resource, version);

		return schemas.newDocument().setTitle(resource).setContent(content);
	}

	//
	//	public DemoTestRecords withEvents() {
	//		createRecordsEvents();
	//		createViewEvents();
	//		createDecommissioningEvents();
	//		createPermissionEvents();
	//		createBorrowAndReturnEvents();
	//		createLoginEvents();
	//		recordServices.flush();
	//		return this;
	//	}
	//
	//	private void createViewEvents() {
	//		User charles = users.charlesIn(collection);
	//		loggingServices.logRecordView(getFolder_A02().getWrappedRecord(), charles);
	//	}

	private void createLoginEvents() {
		User admin = users.adminIn(collection);
		loggingServices.login(admin);
		User charles = users.charlesIn(collection);
		loggingServices.login(charles);
		loggingServices.logout(charles);
	}

	//	private void createRecordsEvents() {
	//		Transaction transaction = new Transaction();
	//		User charles = users.charlesIn(collection);
	//		transaction
	//				.add(createEvent(charles.getUsername(), EventType.CREATE_FOLDER, new LocalDateTime().minusDays(1), folder_A01));
	//		transaction.add(createEvent(charles.getUsername(), EventType.CREATE_DOCUMENT, new LocalDateTime().minusDays(1), "11"));
	//		transaction
	//				.add(createEvent(charles.getUsername(), EventType.MODIFY_DOCUMENT, new LocalDateTime().minusDays(1), folder_A03));
	//		transaction.add(createEvent(charles.getUsername(), EventType.MODIFY_FOLDER, new LocalDateTime().minusDays(2), "13"));
	//		transaction
	//				.add(createEvent(charles.getUsername(), EventType.DELETE_FOLDER, new LocalDateTime().minusDays(2), folder_A05));
	//		System.out.println("=====" + getBob_userInAC().getTitle());
	//		transaction.add(createEvent(charles.getUsername(), EventType.CREATE_USER, new LocalDateTime().minusDays(2), bob_userInAC,
	//				getBob_userInAC().getTitle()));
	//		transaction.add(createEvent(charles.getUsername(), EventType.MODIFY_USER, new LocalDateTime().minusDays(2), chuckNorris,
	//				getChuckNorris().getTitle()));
	//		try {
	//			recordServices.execute(transaction);
	//		} catch (RecordServicesException e) {
	//			throw new RuntimeException(e);
	//		}
	//	}

	private RecordWrapper createEvent(String username, String eventType, LocalDateTime eventDate, String recordId) {
		return createEvent(username, eventType, eventDate, recordId, null);
	}

	private RecordWrapper createEvent(String username, String eventType, LocalDateTime eventDate, String recordId, String title) {
		return schemas.newEvent().setRecordId(recordId).setTitle(title).setUsername(username).setType(eventType)
				.setCreatedOn(eventDate);
	}

	//	private void createBorrowAndReturnEvents() {
	//		Folder folderA02 = getFolder_A02();
	//		Folder folderBorrowedByDakota = getFolder_A03();
	//		User bob = users.bobIn(collection);
	//		loggingServices.logBorrowRecord(folderA02.getWrappedRecord(), bob);
	//		loggingServices.logBorrowRecord(getContainerBac01().getWrappedRecord(), bob);
	//		loggingServices.logReturnRecord(folderA02.getWrappedRecord(), bob);
	//		User charles = users.charlesIn(collection);
	//		loggingServices.logBorrowRecord(folderBorrowedByDakota.getWrappedRecord(), charles);
	//	}

	private void createDecommissioningEvents() {
		DecommissioningList decommissioningList = schemas.newDecommissioningList();
		decommissioningList.setDecommissioningListType(DecommissioningListType.FOLDERS_TO_DEPOSIT);
		decommissioningList.setTitle("folder to deposit by bob");
		User bob = users.bobIn(collection);
		decommissioningLoggingService.logDecommissioning(decommissioningList, bob);

		DecommissioningList decommissioningList2 = schemas.newDecommissioningList();
		decommissioningList2.setDecommissioningListType(DecommissioningListType.FOLDERS_TO_DESTROY);
		decommissioningList2.setTitle("folder destroy by dakota");
		User dakota = users.dakotaLIndienIn(collection);
		decommissioningLoggingService.logDecommissioning(decommissioningList2, dakota);

		DecommissioningList decommissioningList3 = schemas.newDecommissioningList();
		decommissioningList3.setDecommissioningListType(DecommissioningListType.FOLDERS_TO_TRANSFER);
		decommissioningList3.setTitle("folder transfer by bob");
		decommissioningLoggingService.logDecommissioning(decommissioningList3, bob);
	}

	private void createPermissionEvents() {
		modifyPermission();
	}

	private void modifyPermission() {
		List<String> roles = new ArrayList<>();
		String zRole = "MANAGER";
		roles.add(zRole);
		LocalDate startDate = new LocalDate();
		LocalDate endDate = new LocalDate();
		AuthorizationDetails detail = new AuthorizationDetails(collection, "42", roles, startDate, endDate, false);
		List<String> grantedToPrincipals = new ArrayList<>();
		User dakota = users.gandalfLeblancIn(collection);
		User bob = users.bobIn(collection);
		grantedToPrincipals.add(dakota.getId());
		grantedToPrincipals.add(bob.getId());
		List<String> grantedOnRecords = new ArrayList<>();
		/*AdministrativeUnit administrativeUnit = records.getUnit10();
		Folder folder = createFolder(administrativeUnit);*/
		grantedOnRecords.addAll(Arrays.asList(folder_A01));
		Authorization authorization = new Authorization(detail, grantedToPrincipals, grantedOnRecords);

		List<String> grantedOnRecordsBefore = new ArrayList<>();
		grantedOnRecordsBefore.addAll(
				Arrays.asList(folder_A01, folder_A02));
		AuthorizationDetails detailBefore = new AuthorizationDetails(collection, "43", roles, startDate, endDate.minusDays(1),
				false);
		Authorization authorizationBefore = new Authorization(detailBefore, grantedToPrincipals, grantedOnRecordsBefore);

		User charles = users.charlesIn(collection);
		loggingServices.modifyPermission(authorization, authorizationBefore, charles);
	}

	private void setupLists(Transaction transaction) {
		transaction.add(schemas.newDecommissioningListWithId(list_01)).setTitle("Listes avec plusieurs supports à détruire")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DESTROY)
				.setContainerDetailsFor(containerId_bac18, containerId_bac19)
				.setFolderDetailsFor(asList(folder_A42, folder_A43, folder_A44, folder_A45, folder_A46, folder_A47));

		transaction.add(schemas.newDecommissioningListWithId(list_02)).setTitle("Liste analogique à détruire")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DESTROY)
				.setFolderDetailsFor(asList(folder_A54, folder_A55, folder_A56));

		transaction.add(schemas.newDecommissioningListWithId(list_03)).setTitle("Liste hybride à fermer")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_CLOSE)
				.setFolderDetailsFor(asList(folder_A01, folder_A02, folder_A03));

		transaction.add(schemas.newDecommissioningListWithId(list_04)).setTitle("Liste analogique à transférer")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_TRANSFER)
				.setContainerDetailsFor(containerId_bac14, containerId_bac15)
				.setFolderDetailsFor(asList(folder_A22, folder_A23, folder_A24));

		transaction.add(schemas.newDecommissioningListWithId(list_05)).setTitle("Liste hybride à transférer")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_TRANSFER)
				.setFolderDetailsFor(asList(folder_A19, folder_A20, folder_A21));

		transaction.add(schemas.newDecommissioningListWithId(list_06)).setTitle("Liste électronique à transférer")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_TRANSFER)
				.setFolderDetailsFor(asList(folder_A25, folder_A26, folder_A27));

		transaction.add(schemas.newDecommissioningListWithId(list_07)).setTitle("Liste analogique à détruire")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DESTROY)
				.setFolderDetailsFor(asList(folder_A54, folder_A55, folder_A56));

		transaction.add(schemas.newDecommissioningListWithId(list_08)).setTitle("Liste hybride à déposer")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DEPOSIT)
				.setFolderDetailsFor(folder_B30, folder_B33, folder_B35);

		transaction.add(schemas.newDecommissioningListWithId(list_09)).setTitle("Liste électronique à déposer")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DEPOSIT)
				.setFolderDetailsFor(asList(folder_A57, folder_A58, folder_A59));

		transaction.add(schemas.newDecommissioningListWithId(list_10)).setTitle("Liste avec plusieurs supports à déposer")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DEPOSIT)
				.setFolderDetailsFor(asList(folder_A42, folder_A43, folder_A44, folder_A48, folder_A49, folder_A50));

		transaction.add(schemas.newDecommissioningListWithId(list_11)).setTitle("Liste de fermeture traîtée")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_CLOSE)
				.setProcessingUser(dakota_managerInA_userInB).setProcessingDate(date(2012, 5, 5))
				.setFolderDetailsFor(asList(folder_A10, folder_A11, folder_A12, folder_A13, folder_A14, folder_A15));

		transaction.add(schemas.newDecommissioningListWithId(list_12)).setTitle("Liste de transfert traîtée")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_TRANSFER)
				.setProcessingUser(dakota_managerInA_userInB).setProcessingDate(date(2012, 5, 5))
				.setContainerDetailsFor(containerId_bac10, containerId_bac11, containerId_bac12)
				.setFolderDetailsFor(asList(folder_A45, folder_A46, folder_A47, folder_A48, folder_A49, folder_A50, folder_A51,
						folder_A52, folder_A53, folder_A54, folder_A55, folder_A56, folder_A57, folder_A58, folder_A59));

		transaction.add(schemas.newDecommissioningListWithId(list_13)).setTitle("Liste de transfert uniforme traîtée")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_TRANSFER)
				.setProcessingUser(dakota_managerInA_userInB).setProcessingDate(date(2012, 5, 5))
				.setContainerDetailsFor(containerId_bac13)
				.setFolderDetailsFor(asList(folder_A42, folder_A43, folder_A43));

		transaction.add(schemas.newDecommissioningListWithId(list_14)).setTitle("Liste de dépôt traîtée")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DEPOSIT)
				.setProcessingUser(dakota_managerInA_userInB).setProcessingDate(date(2012, 5, 5))
				.setContainerDetailsFor(containerId_bac05)
				.setFolderDetailsFor(folder_A79, folder_A80, folder_A81, folder_A82, folder_A83, folder_A84, folder_A85,
						folder_A86, folder_A87, folder_A88, folder_A89, folder_A90, folder_A91, folder_A92, folder_A93);

		transaction.add(schemas.newDecommissioningListWithId(list_15)).setTitle("Liste de dépôt uniforme traîtée")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_DEPOSIT)
				.setProcessingUser(dakota_managerInA_userInB).setProcessingDate(date(2012, 5, 5))
				.setContainerDetailsFor(containerId_bac04)
				.setFolderDetailsFor(asList(folder_A94, folder_A95, folder_A96));

		DecommissioningList zeList16 = schemas.newDecommissioningListWithId(list_16)
				.setTitle("Liste analogique à transférer en contenants")
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningListType(FOLDERS_TO_TRANSFER)
				.setContainerDetailsFor(containerId_bac14).setFolderDetailsFor(asList(folder_A22, folder_A23, folder_A24));
		for (DecomListFolderDetail detail : zeList16.getFolderDetails()) {
			detail.setContainerRecordId(containerId_bac14);
		}
		transaction.add(zeList16);

	}

	private void setupStorageSpace(Transaction transaction) {
		transaction.add(schemas.newStorageSpaceWithId(storageSpaceId_S01).setCode(storageSpaceId_S01).setTitle("Etagere 1"));
		transaction.add(schemas.newStorageSpaceWithId(storageSpaceId_S01_01).setCode(storageSpaceId_S01_01).setTitle("Tablette 1")
				.setParentStorageSpace(storageSpaceId_S01)).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE);
		transaction.add(schemas.newStorageSpaceWithId(storageSpaceId_S01_02).setCode(storageSpaceId_S01_02).setTitle("Tablette 2")
				.setParentStorageSpace(storageSpaceId_S01)).setDecommissioningType(DEPOSIT);
		transaction.add(schemas.newStorageSpaceWithId(storageSpaceId_S02).setCode(storageSpaceId_S02).setTitle("Etagere 2"));
		transaction.add(schemas.newStorageSpaceWithId(storageSpaceId_S02_01).setCode(storageSpaceId_S02_01).setTitle("Tablette 1")
				.setParentStorageSpace(storageSpaceId_S02)).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE);
		transaction.add(schemas.newStorageSpaceWithId(storageSpaceId_S02_02).setCode(storageSpaceId_S02_02).setTitle("Tablette 2")
				.setParentStorageSpace(storageSpaceId_S02)).setDecommissioningType(DEPOSIT);
	}

	private void setupContainerTypes(Transaction transaction) {
		transaction.add(schemas.newContainerRecordTypeWithId(containerTypeId_boite22x22)
				.setTitle("Boite 22X22").setCode("B22x22"));
	}

	private void setupContainers(Transaction transaction) {

		String noStorageSpace = null;

		transaction.add(schemas.newContainerRecordWithId(containerId_bac19)).setTemporaryIdentifier("10_A_12").setFull(false)
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningType(DESTRUCTION)
				.setType(containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac18)).setTemporaryIdentifier("10_A_11").setFull(false)
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningType(DESTRUCTION).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac17)).setTemporaryIdentifier("10_A_10").setFull(false)
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningType(DEPOSIT).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac16)).setTemporaryIdentifier("10_A_09").setFull(false)
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningType(DEPOSIT).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac15)).setTemporaryIdentifier("10_A_08").setFull(false)
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningType(
				TRANSFERT_TO_SEMI_ACTIVE).setType(containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac14)).setTemporaryIdentifier("10_A_07").setFull(false)
				.setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A).setDecommissioningType(
				TRANSFERT_TO_SEMI_ACTIVE).setType(containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac13).setTemporaryIdentifier("10_A_06").setFull(false)
				.setStorageSpace(storageSpaceId_S01_01).setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A)
				.setRealTransferDate(date(2008, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac12).setTemporaryIdentifier("10_A_05").setFull(false)
				.setStorageSpace(storageSpaceId_S01_01).setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A)
				.setRealTransferDate(date(2006, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac11).setTemporaryIdentifier("10_A_04").setFull(false)
				.setStorageSpace(storageSpaceId_S01_01).setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A)
				.setRealTransferDate(date(2005, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac10).setTemporaryIdentifier("10_A_03").setFull(true)
				.setStorageSpace(noStorageSpace).setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A)
				.setRealTransferDate(date(2007, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac09).setTemporaryIdentifier("11_B_02").setFull(false)
				.setStorageSpace(storageSpaceId_S02_01).setAdministrativeUnit(unitId_10B).setFilingSpace(filingId_B)
				.setRealTransferDate(date(2006, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac08).setTemporaryIdentifier("12_B_02").setFull(false)
				.setStorageSpace(storageSpaceId_S02_01).setAdministrativeUnit(unitId_10B).setFilingSpace(filingId_B)
				.setRealTransferDate(date(2007, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac07).setTemporaryIdentifier("30_C_03").setFull(false)
				.setStorageSpace(storageSpaceId_S02_01).setAdministrativeUnit(unitId_10C).setFilingSpace(filingId_C)
				.setRealTransferDate(date(2007, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac06).setTemporaryIdentifier("30_C_02").setFull(false)
				.setStorageSpace(noStorageSpace).setAdministrativeUnit(unitId_10C).setFilingSpace(filingId_C)
				.setRealTransferDate(date(2006, 10, 31))).setDecommissioningType(TRANSFERT_TO_SEMI_ACTIVE).setType(
				containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac05).setTemporaryIdentifier("10_A_02").setFull(true)
				.setStorageSpace(storageSpaceId_S01_02).setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A)
				.setRealTransferDate(date(2008, 10, 31)).setRealDepositDate(date(2012, 5, 15))).setDecommissioningType(
				DEPOSIT).setType(containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac04).setTemporaryIdentifier("10_A_01").setFull(false)
				.setStorageSpace(storageSpaceId_S01_02).setAdministrativeUnit(unitId_10A).setFilingSpace(filingId_A)
				.setRealTransferDate(date(2007, 10, 31)).setRealDepositDate(date(2010, 8, 17))).setDecommissioningType(
				DEPOSIT).setType(containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac03).setTemporaryIdentifier("11_B_01").setFull(false)
				.setStorageSpace(storageSpaceId_S02_02).setAdministrativeUnit(unitId_10B).setFilingSpace(filingId_B)
				.setRealTransferDate(date(2006, 10, 31)).setRealDepositDate(date(2009, 8, 17))).setDecommissioningType(
				DEPOSIT).setType(containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac02).setTemporaryIdentifier("12_B_01").setFull(false)
				.setStorageSpace(noStorageSpace).setAdministrativeUnit(unitId_10B).setFilingSpace(filingId_B)
				.setRealTransferDate(date(2007, 10, 31)).setRealDepositDate(date(2011, 2, 13))).setDecommissioningType(
				DEPOSIT).setType(containerTypeId_boite22x22);

		transaction.add(schemas.newContainerRecordWithId(containerId_bac01).setTemporaryIdentifier("30_C_01").setFull(true)
				.setStorageSpace(storageSpaceId_S02_02).setAdministrativeUnit(unitId_10C).setFilingSpace(filingId_C)
				.setRealTransferDate(date(2007, 10, 31)).setRealDepositDate(date(2011, 2, 13))).setDecommissioningType(
				DEPOSIT).setType(containerTypeId_boite22x22);
	}

	private void setupFolders(Transaction transaction) {
		transaction.add(schemas.newFolderWithId(folder_A01)).setTitle("Émilie Poulain").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant Mme Émilie Poulain")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_A02)).setTitle("Recrutement à l'externe - Tech. en Documentation")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_2110).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents relatifs aux recrutements externes")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4));

		transaction.add(schemas.newFolderWithId(folder_A03)).setTitle("Recrutement à l'interne - Tech. en Documentation")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_2110).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents relatifs aux promotions en interne")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5));

		transaction.add(schemas.newFolderWithId(folder_A04)).setTitle("Structure des Postes")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Schéma complet de la structure des postes, avec leur caractéristiques")
				.setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_A05)).setTitle("Bastien Augerau").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Bastien Augerau")
				.setOpenDate(date(2000, 11, 4));

		transaction.add(schemas.newFolderWithId(folder_A06)).setTitle("Bastien Bernadotte")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Bastien Bernadotte")
				.setOpenDate(date(2000, 11, 5));

		transaction.add(schemas.newFolderWithId(folder_A07)).setTitle("Brittany Daru").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client Mme Brittany Daru")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_A08)).setTitle("Boris Gouvon").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Boris Gouvon")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4));

		transaction.add(schemas.newFolderWithId(folder_A09)).setTitle("Burt Marmont").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Burt Marmont")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5));

		transaction.add(schemas.newFolderWithId(folder_A10)).setTitle("Caroline Suchet").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client Mme Caroline Suchet")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A11)).setTitle("DocuLibre").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_22).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le fournisseur DocuLibre")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A12)).setTitle("IBM").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_22).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le fournisseur IBM")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A13)).setTitle("Google").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_22).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le fournisseur Google")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A14)).setTitle("Toyota 102-CQA").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_231).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant la voiture de fonction immatriculée 102-CQA")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A15)).setTitle("Audi 734-FPL").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_231).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant la voiture immatriculée 734-FPL")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A16)).setTitle("Assurance Santé Émilie Poulain")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de Mme Émilie Poulain")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A17)).setTitle("Documents PDF présents sur le site")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_13).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Sauvegarde de l'ensemble des fichiers présents sur le site côté client au format pdf")
				.setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A18)).setTitle("Formation à l'internationale")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3200).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Formations possibles à l'international")
				.setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A19)).setTitle("Etude des sites concurrents")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_13).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA)
				.setDescription("Étude des sites concurrents")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A20)).setTitle("Propositions Commerciales")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_22).setRetentionRuleEntered(ruleId_3)
				.setDescription("Document vierge de devis")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A21)).setTitle("Accident du 20/02 - Émilie Poulain")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_233).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Accident du travail de Mme. Émilie Poulain en date du 20 février 2002")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A22)).setTitle("Contrat Auto Toyota 102-CQA")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_234).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Contrat avec la Banque Nationale du Québec")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A23)).setTitle("Contrat Auto Audi 734-FPL")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_234).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Contrat avec la Banque Nationale du Québec")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A24)).setTitle("Contrat 1250 Charest Ouest, Suite 1040")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_234).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Contrat d'assurance des locaux avec AllianZ")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 5)).setCloseDateEntered(date(2003, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A25)).setTitle("Accident du 07/02 - Rousseau Amélie")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_233).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Accident du travail de Mme. Rousseau Amélie en date du 7 février 2002")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A26)).setTitle("CGI").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_22).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Dossier concernant le fournisseur CGI")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A27)).setTitle("James Baxter").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Dossier concernant le client M. James Baxter")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 5)).setCloseDateEntered(date(2003, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_A42)).setTitle("Lisa Tyson").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA)
				.setDescription("Dossier concernant le client Mme Lisa Tyson")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setContainer(containerId_bac13);

		transaction.add(schemas.newFolderWithId(folder_A43)).setTitle("Gertrude Young").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client Mme Gertrude Young")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setContainer(containerId_bac13);

		transaction.add(schemas.newFolderWithId(folder_A44)).setTitle("Scott Morris").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_2)
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setDescription("Dossier concernant le client M. Scott Morris")
				.setActualTransferDate(date(2008, 10, 31)).setContainer(containerId_bac13);

		transaction.add(schemas.newFolderWithId(folder_A45)).setTitle("Linda Armstrong").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(MD)
				.setDescription("Dossier concernant le client Mme Linda Armstrong")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setContainer(containerId_bac12);

		transaction.add(schemas.newFolderWithId(folder_A46)).setTitle("Jeffrey West").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA)
				.setDescription("Dossier concernant le client M. Jeffrey West")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setContainer(containerId_bac12).setDescription("Babar");

		transaction.add(schemas.newFolderWithId(folder_A47)).setTitle("Letha Johnson").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_2)
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setDescription("Dossier concernant le client Mme Letha Johnson")
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac12);

		transaction.add(schemas.newFolderWithId(folder_A48)).setTitle("Betty Howell").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(MD)
				.setDescription("Dossier concernant le client Mme Betty Howell")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac11);

		transaction.add(schemas.newFolderWithId(folder_A49)).setTitle("Caroline Lacroix").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac11)
				.setDescription("Dossier concernant le client Mme Caroline Lacroix");

		transaction.add(schemas.newFolderWithId(folder_A50)).setTitle("Edward Unrein").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Edward Unrein")
				.setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setContainer(containerId_bac11);

		transaction.add(schemas.newFolderWithId(folder_A51)).setTitle("Accident du 30/05 - Rousseau Amélie")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_233).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Accident du travail de Mme Rousseau Amélie en date du 30 Mai 2000")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A52)).setTitle("Toyota 480-SHI").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_231).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant la voiture de fonction immatriculée 480-SHI")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A53)).setTitle("Honda 462-OBR").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_231).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant la voiture de fonction immatriculée 462-OBR")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A54)).setTitle("Betty Hayes").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Documents officiels de Mme Betty Hayes")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A55)).setTitle("Filibert Valdez").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Documents officiels de M. Filibert Valdez")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A56)).setTitle("Anne Fernandez").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Documents officiels de Mme Anne Fernandez")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 5)).setCloseDateEntered(date(2003, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A57)).setTitle("David Yates").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Documents officiels de M. David Yates")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A58)).setTitle("Henry Ford").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Documents officiels de M. Henry Ford")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A59)).setTitle("Roy Mathieu").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Documents officiels de M. Roy Mathieu")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 5)).setCloseDateEntered(date(2003, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setContainer(containerId_bac10);

		transaction.add(schemas.newFolderWithId(folder_A79)).setTitle("Alexandra Zielinski")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents officiels de Mme Alexandra Zielinski")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setActualDepositDate(date(2011, 2, 13))
				.setContainer(containerId_bac05);

		transaction.add(schemas.newFolderWithId(folder_A80)).setTitle("Andrea Chavez").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents officiels de Mme Andrea Chavez")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setActualDestructionDate(date(2011, 2, 13));

		transaction.add(schemas.newFolderWithId(folder_A81)).setTitle("Cynthia Adams").setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents officiels de Mme Cynthia Adams")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2008, 10, 31)).setActualDepositDate(date(2012, 2, 13))
				.setContainer(containerId_bac05);

		transaction.add(schemas.newFolderWithId(folder_A82)).setTitle("Assurance Santé Filibert Valdez")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de M. Filibert Valdez")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setActualDestructionDate(date(2007, 4, 14));

		transaction.add(schemas.newFolderWithId(folder_A83)).setTitle("Assurance Santé Anne Fernandez")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de Mme Anne Fernandez")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setActualDestructionDate(date(2007, 4, 14));

		transaction.add(schemas.newFolderWithId(folder_A84)).setTitle("Assurance Santé David Yates")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de M. David Yates")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDestructionDate(date(2008, 4, 14));

		transaction.add(schemas.newFolderWithId(folder_A85)).setTitle("Assurance Santé Henry Ford")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de M. Henry Ford")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDepositDate(date(2011, 5, 15))
				.setContainer(containerId_bac05);

		transaction.add(schemas.newFolderWithId(folder_A86)).setTitle("Assurance Santé Roy Mathieu")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de M. Roy Mathieu")
				.setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDepositDate(date(2011, 5, 15))
				.setContainer(containerId_bac05);

		transaction.add(schemas.newFolderWithId(folder_A87)).setTitle("Assurance Santé Alexandra Zielinski")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de Mme Alexandra Zielinski")
				.setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setActualDepositDate(date(2012, 5, 15))
				.setContainer(containerId_bac05);

		transaction.add(schemas.newFolderWithId(folder_A88)).setTitle("Assurance Santé Andrea Chavez")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de Mme Andrea Chavez")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDestructionDate(date(2011, 6, 16));

		transaction.add(schemas.newFolderWithId(folder_A89)).setTitle("Assurance Santé Cynthia Adams")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de Mme Cynthia Adams")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDepositDate(date(2011, 6, 16))
				.setContainer(containerId_bac05);

		transaction.add(schemas.newFolderWithId(folder_A90)).setTitle("Assurance Santé Betty Hayes")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant l'assurance santé de Mme Betty Hayes")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 11, 5)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setActualDestructionDate(date(2012, 6, 16));

		transaction.add(schemas.newFolderWithId(folder_A91)).setTitle("Assurance Santé Olivier Dufault")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_232).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Dossier concernant l'assurance santé de M. Olivier Dufault")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDestructionDate(date(2009, 7, 16));

		transaction.add(schemas.newFolderWithId(folder_A92)).setTitle("Documentation Comptable")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Documents d'information sur les pratiques du comptable")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDestructionDate(date(2009, 7, 16));

		transaction.add(schemas.newFolderWithId(folder_A93)).setTitle("Documentation Directeur des Ressources Humaines")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Documents d'information sur les pratiques du DRH")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 5)).setCloseDateEntered(date(2003, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setActualDestructionDate(date(2010, 7, 16));

		transaction.add(schemas.newFolderWithId(folder_A94)).setTitle("Documentation Secrétaire")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Documents d'information sur les pratiques du secrétaire")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDepositDate(date(2009, 8, 17))
				.setContainer(containerId_bac04);

		transaction.add(schemas.newFolderWithId(folder_A95)).setTitle("Documentation Commercial-Marketing")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Documents d'information sur les pratiques des commerciaux")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDepositDate(date(2009, 8, 17))
				.setContainer(containerId_bac04);

		transaction.add(schemas.newFolderWithId(folder_A96)).setTitle("Documentation Développeur")
				.setAdministrativeUnitEntered(unitId_10A)
				.setFilingSpaceEntered(filingId_A).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Documents d'information sur les pratiques du développeur")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 7, 5)).setCloseDateEntered(date(2003, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setActualDepositDate(date(2010, 8, 17))
				.setContainer(containerId_bac04);

		transaction.add(schemas.newFolderWithId(folder_B01)).setTitle("Affichage des Postes")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2130).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Hiérarchie des Postes internes et externes")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_B02)).setTitle("Programmes d'embauche")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2100).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Détails sur le programme d'embauche actuel")
				.setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_B03)).setTitle("Recrutement à l'externe - Aides")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2120).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Documents d'aide au recrutement externe")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_B04)).setTitle("Recrutement à l'interne - Aides")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_1000).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents d'aide au recrutement interne")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_B05)).setTitle("Recrutement à l'externe - Résultats")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_1000).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Convocations et Résultats d'entrevue des candidats à l'externe")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_B06)).setTitle("Recrutement à l'interne - Résultats")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2110).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Convocations et Résultats d'entrevue des candidats à l'interne")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_B07)).setTitle("Planning du Personnel")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2000).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Planning prévisionnel de travail du Personnel")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_B08)).setTitle("Planning des Congés")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2000).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Planning prévisionnel des congés")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_B09)).setTitle("Fiche de Paie").setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2000).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Documents vierge servant de modèle pour la paie")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_B30)).setTitle("Formulaire de demande de promotion")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2110).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Formulaire vierge de demande d'une promotion en interne")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setContainer(containerId_bac08);

		transaction.add(schemas.newFolderWithId(folder_B31)).setTitle("Formulaire de demande de mutation")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_4200).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Formulaire vierge de demande d'une mutation en interne")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setContainer(containerId_bac09);

		transaction.add(schemas.newFolderWithId(folder_B32)).setTitle("Demandes Spontanées")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_2120).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Divers CV de demandes spontanées")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac08);

		transaction.add(schemas.newFolderWithId(folder_B33)).setTitle("Accueil de Stagiaires")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Documents d'information sur l'accueil des stagiaires")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac09);

		transaction.add(schemas.newFolderWithId(folder_B34)).setTitle("Schémas et Maquettes")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_13).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Schémas et Maquettes du prochain site")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac08);

		transaction.add(schemas.newFolderWithId(folder_B35)).setTitle("Factures").setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_22).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Document vierge de Facture")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac09);

		transaction.add(schemas.newFolderWithId(folder_B50)).setTitle("Template").setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_13).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents concernant la charte graphique du site")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setActualDepositDate(date(2011, 2, 13))
				.setContainer(containerId_bac02);

		transaction.add(schemas.newFolderWithId(folder_B51)).setTitle("Grille de Tarifs").setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_20).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Grilles des tarifs actuels")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setActualDestructionDate(date(2007, 4, 14));

		transaction.add(schemas.newFolderWithId(folder_B52)).setTitle("Base de Données").setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_13).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Sauvegarde de la base de données du site")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDestructionDate(date(2006, 5, 15));

		transaction.add(schemas.newFolderWithId(folder_B53)).setTitle("Catalogue des Produits")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_20).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Catalogue de tous les produits actuels")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDestructionDate(date(2011, 6, 16));

		transaction.add(schemas.newFolderWithId(folder_B54)).setTitle("Document de présentation de l'entreprise")
				.setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_20).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Présentation de l'entreprise à destination des clients et fournisseurs")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDestructionDate(date(2009, 7, 16));

		transaction.add(schemas.newFolderWithId(folder_B55)).setTitle("Plan").setAdministrativeUnitEntered(unitId_10B)
				.setFilingSpaceEntered(filingId_B).setCategoryEntered(categoryId_20).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Plan google map pour accéder aux locaux 1250 Charest Ouest, Suite 1040")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDepositDate(date(2009, 8, 17))
				.setContainer(containerId_bac03);

		transaction.add(schemas.newFolderWithId(folder_C01)).setTitle("Formation du Personnel")
				.setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3200).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Calendriers, Formulaires d'inscription et Programmes de Formations du Personnel")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_C02)).setTitle("Rousseau Amélie").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Documents officiels de Mme Rousseau Amélie")
				.setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_C03)).setTitle("Accident du 10/05 - Filibert Valdez")
				.setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_233).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Accident du travail de M. Filibert Valdez en date du 10 Mai 1999")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(folder_C04)).setTitle("Logo").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_20).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Logo de l'entreprise")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_C05)).setTitle("Sondage").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_4200).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Document vierge de sondage du bien-être des salariés")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_C06)).setTitle("Charles Mozek").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_21).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Charles Mozek")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_C07)).setTitle("Contrat Banque Nationale de Québec")
				.setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_234).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Contrat entre l'entreprise et la Banque Nationale du Québec")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_C08)).setTitle("Documentation Assistance - Dépanage")
				.setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_1100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Documents d'information sur les pratiques de dépanage et d'assistance")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_C09)).setTitle("Statistiques").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_20).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Statistiques divers sur les ventes, les tarifs, etc. de l'année passée")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31));

		transaction.add(schemas.newFolderWithId(folder_C30)).setTitle("Mentions Légales").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_20).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents d'information sur les mentions légales de l'entreprise")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setContainer(containerId_bac07);

		transaction.add(schemas.newFolderWithId(folder_C31)).setTitle("Note de Frais").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_2000).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Document vierge de note de frais")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setContainer(containerId_bac07);

		transaction.add(schemas.newFolderWithId(folder_C32)).setTitle("Demande de matériel")
				.setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_2000).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Formulaire vierge de demande de matériel et fournitures")
				.setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac07);

		transaction.add(schemas.newFolderWithId(folder_C33)).setTitle("CV").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_2120).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Sauvegarde de CV")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setContainer(containerId_bac07);

		transaction.add(schemas.newFolderWithId(folder_C34)).setTitle("Scott Trucker").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Dossier concernant le client M. Scott Trucker")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac07);

		transaction.add(schemas.newFolderWithId(folder_C35)).setTitle("James Dawkins").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Dossier concernant le client M. James Dawkins")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setContainer(containerId_bac06);

		transaction.add(schemas.newFolderWithId(folder_C50)).setTitle("June Nocera").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client Mme June Nocera")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2007, 10, 31)).setActualDepositDate(date(2011, 2, 13))
				.setContainer(containerId_bac01);

		transaction.add(schemas.newFolderWithId(folder_C51)).setTitle("Michèle Gallucci").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Michèle Gallucci")
				.setCopyStatusEntered(SECONDARY).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2005, 10, 31)).setActualDestructionDate(date(2007, 4, 14));

		transaction.add(schemas.newFolderWithId(folder_C52)).setTitle("Robert Garcia").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_1)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Robert Garcia")
				.setDescription("Patate").setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDestructionDate(date(2006, 5, 15));

		transaction.add(schemas.newFolderWithId(folder_C53)).setTitle("Lee Taub").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_3)
				.setMediumTypes(PA, MD)
				.setDescription("Dossier concernant le client M. Lee Taub")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4)).setCloseDateEntered(date(2001, 10, 31))
				.setActualTransferDate(date(2004, 10, 31)).setActualDestructionDate(date(2011, 6, 16));

		transaction.add(schemas.newFolderWithId(folder_C54)).setTitle("Erwin Eckert").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(PA)
				.setDescription("Dossier concernant le client M. Erwin Eckert")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 5, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDestructionDate(date(2009, 7, 16));

		transaction.add(schemas.newFolderWithId(folder_C55)).setTitle("Daniel Nelson").setAdministrativeUnitEntered(unitId_10C)
				.setFilingSpaceEntered(filingId_C).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_4)
				.setMediumTypes(MD)
				.setDescription("Dossier concernant le client M. Daniel Nelson")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 6, 4)).setCloseDateEntered(date(2002, 10, 31))
				.setActualTransferDate(date(2006, 10, 31)).setActualDepositDate(date(2009, 8, 17))
				.setContainer(containerId_bac01);

		transaction.add(schemas.newFolderWithId(nextId())).setTitle("Évaluation des Ressources Humaines")
				.setAdministrativeUnitEntered(unitId_10D)
				.setFilingSpaceEntered(filingId_D).setCategoryEntered(categoryId_4100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Document permettant l'évaluation du Personnel")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(nextId())).setTitle("Mouvement du Personnel")
				.setAdministrativeUnitEntered(unitId_10D)
				.setFilingSpaceEntered(filingId_D).setCategoryEntered(categoryId_4200).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents relatifs aux affectations du Personnel")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(nextId())).setTitle("Affichage des postes de tech. en documentation")
				.setAdministrativeUnitEntered(unitId_10E)
				.setFilingSpaceEntered(filingId_E).setCategoryEntered(categoryId_2130).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documentation technique des postes")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(nextId())).setTitle("Guides d'évaluations")
				.setAdministrativeUnitEntered(unitId_10E)
				.setFilingSpaceEntered(filingId_E).setCategoryEntered(categoryId_4100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Anciennes versions des Guides d'évaluations")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

		transaction.add(schemas.newFolderWithId(nextId())).setTitle("Oliver Dufault").setAdministrativeUnitEntered(unitId_10E)
				.setFilingSpaceEntered(filingId_E).setCategoryEntered(categoryId_3100).setRetentionRuleEntered(ruleId_2)
				.setMediumTypes(PA, MD)
				.setDescription("Documents officiels de M. Dufault Olivier")
				.setCopyStatusEntered(PRINCIPAL).setOpenDate(date(2000, 10, 4));

	}

	private ContentVersionDataSummary upload(String resourceName) {
		InputStream inputStream = DemoTestRecords.class.getResourceAsStream("DemoTestRecords_" + resourceName);
		return contentManager.upload(inputStream);
	}

	private LocalDate date(int year, int month, int day) {
		return new LocalDate(year, month, day);
	}

	public String getCollection() {
		return collection;
	}

	public Users getUsers() {
		return users;
	}

}
