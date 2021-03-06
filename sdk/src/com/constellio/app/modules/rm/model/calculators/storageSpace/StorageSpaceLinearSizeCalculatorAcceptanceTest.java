package com.constellio.app.modules.rm.model.calculators.storageSpace;

import com.constellio.app.modules.rm.RMConfigs;
import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.model.enums.DecommissioningType;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.StorageSpace;
import com.constellio.app.modules.rm.wrappers.type.ContainerRecordType;
import com.constellio.model.entities.calculators.CalculatorParameters;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.model.services.search.SearchServices;
import com.constellio.sdk.tests.ConstellioTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by Constellio on 2016-12-19.
 */

public class StorageSpaceLinearSizeCalculatorAcceptanceTest extends ConstellioTest {

	RMTestRecords records = new RMTestRecords(zeCollection);

	StorageSpaceLinearSizeCalculator calculator;

	RMSchemasRecordsServices rm;

	RecordServices recordServices;

	SearchServices searchServices;

	@Mock
	CalculatorParameters parameters;

	@Before
	public void setUp() throws RecordServicesException {
		calculator = spy(new StorageSpaceLinearSizeCalculator());
		prepareSystem(
				withZeCollection().withConstellioRMModule().withAllTestUsers().withRMTest(records)
		);

		rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
		recordServices = getModelLayerFactory().newRecordServices();
		searchServices = getModelLayerFactory().newSearchServices();
		recordServices.add(buildDefaultContainerType());
	}

	@Test
	public void givenParametersThenCalculatorReturnsGoodValue() {
		givenDisabledAfterTestValidations();
		when(parameters.get(calculator.enteredLinearSizeParam)).thenReturn(new Double(5));

		assertThat(calculator.calculate(parameters)).isEqualTo(5);

		when(parameters.get(calculator.linearSizeSumParam)).thenReturn(new Double(9001));

		assertThat(calculator.calculate(parameters)).isEqualTo(5);

		when(parameters.get(calculator.enteredLinearSizeParam)).thenReturn(null);

		assertThat(calculator.calculate(parameters)).isEqualTo(9001);

		when(parameters.get(calculator.linearSizeSumParam)).thenReturn(null);

		assertThat(calculator.calculate(parameters)).isNull();

		when(parameters.get(calculator.numberOfChildSizeSumParam)).thenReturn(new Double(2));

		when(parameters.get(calculator.childLinearSizeSumParam)).thenReturn(new Double(5));

		assertThat(calculator.calculate(parameters)).isEqualTo(5);

		when(parameters.get(calculator.enteredLinearSizeParam)).thenReturn(new Double(10));

		assertThat(calculator.calculate(parameters)).isEqualTo(5);

		when(parameters.get(calculator.numberOfChildSizeSumParam)).thenReturn(new Double(0));

		assertThat(calculator.calculate(parameters)).isEqualTo(10);
	}

	@Test
	public void givenStorageSpaceHasChildStorageSpaceThenLinearSizeIsEqualToChildSumSum()
			throws RecordServicesException {

		StorageSpace parentStorage = buildDefaultStorageSpace().setCapacity(200);
		recordServices.add(parentStorage);
		StorageSpace childStorage = buildChildStorageSpace().setCapacity(100).setParentStorageSpace(parentStorage);
		recordServices.add(childStorage);

		getModelLayerFactory().getBatchProcessesManager().waitUntilAllFinished();
		Record record = searchServices.searchSingleResult(from(rm.storageSpace.schemaType()).where(Schemas.IDENTIFIER).isEqualTo("storageTest"));
		assertThat(rm.wrapStorageSpace(record).getCapacity()).isEqualTo(200L);
		assertThat(rm.wrapStorageSpace(record).getChildLinearSizeSum()).isEqualTo(new Double(100));
		assertThat(rm.wrapStorageSpace(record).getAvailableSize()).isEqualTo(new Double(100));

	}

	@Test
	public void givenContainerWithLinearSizeLinkedToStorageSpaceWithoutLinearSizeEnteredThenLinearSizeIsEqualToSum()
			throws RecordServicesException {

		StorageSpace storageRecord = buildDefaultStorageSpace();
		recordServices.add(storageRecord);
		addContainersLinkedToStorageSpace(storageRecord.getId());

		getModelLayerFactory().getBatchProcessesManager().waitUntilAllFinished();
		Record record = searchServices.searchSingleResult(from(rm.storageSpace.schemaType()).where(Schemas.IDENTIFIER).isEqualTo("storageTest"));
		assertThat(rm.wrapStorageSpace(record).getLinearSizeEntered()).isNull();
		assertThat(rm.wrapStorageSpace(record).getLinearSizeSum()).isEqualTo(new Double(6));
		assertThat(rm.wrapStorageSpace(record).getLinearSize()).isEqualTo(new Double(6));
	}

	@Test
	public void givenContainerIsMultivalueAndWithLinearSizeLinkedToStorageSpaceWithoutLinearSizeEnteredThenLinearSizeIsEqualToSum()
			throws RecordServicesException {
		givenConfig(RMConfigs.IS_CONTAINER_MULTIVALUE, true);
		StorageSpace storageRecord = buildDefaultStorageSpace();
		recordServices.add(storageRecord);
		addContainersLinkedToStorageSpace(storageRecord.getId());

		getModelLayerFactory().getBatchProcessesManager().waitUntilAllFinished();
		Record record = searchServices.searchSingleResult(from(rm.storageSpace.schemaType()).where(Schemas.IDENTIFIER).isEqualTo("storageTest"));
		assertThat(rm.wrapStorageSpace(record).getLinearSizeEntered()).isNull();
		assertThat(rm.wrapStorageSpace(record).getLinearSizeSum()).isEqualTo(new Double(6));
		assertThat(rm.wrapStorageSpace(record).getLinearSize()).isEqualTo(new Double(6));
	}

	@Test
	public void givenContainerWithLinearSizeLinkedToStorageSpaceWithLinearSizeEnteredThenLinearSizeIsEqualToEnteredValue()
			throws RecordServicesException {

		StorageSpace storageRecord = buildDefaultStorageSpace().setLinearSizeEntered(2);
		recordServices.add(storageRecord);
		addContainersLinkedToStorageSpace(storageRecord.getId());

		getModelLayerFactory().getBatchProcessesManager().waitUntilAllFinished();
		Record record = searchServices.searchSingleResult(from(rm.storageSpace.schemaType()).where(Schemas.IDENTIFIER).isEqualTo("storageTest"));
		assertThat(rm.wrapStorageSpace(record).getLinearSizeEntered()).isEqualTo(new Double(2));
		assertThat(rm.wrapStorageSpace(record).getLinearSizeSum()).isEqualTo(new Double(6));
		assertThat(rm.wrapStorageSpace(record).getLinearSize()).isEqualTo(new Double(2));
	}

	@Test
	public void givenStorageSpaceWithLinearSizeEnteredWithoutLinkedContainerThenLinearSizeIsEqualToEnteredValue()
			throws RecordServicesException {

		StorageSpace storageRecord = buildDefaultStorageSpace().setLinearSizeEntered(2);
		recordServices.add(storageRecord);

		getModelLayerFactory().getBatchProcessesManager().waitUntilAllFinished();
		Record record = searchServices.searchSingleResult(from(rm.storageSpace.schemaType()).where(Schemas.IDENTIFIER).isEqualTo("storageTest"));
		assertThat(rm.wrapStorageSpace(record).getLinearSizeEntered()).isEqualTo(new Double(2));
		assertThat(rm.wrapStorageSpace(record).getLinearSizeSum()).isEqualTo(new Double(0));
		assertThat(rm.wrapStorageSpace(record).getLinearSize()).isEqualTo(new Double(2));
	}

	@Test
	public void givenStorageSpaceWithoutLinearSizeEnteredAndWithoutLinkedContainerThenLinearSizeIsEqualToZero()
			throws RecordServicesException {

		StorageSpace storageRecord = buildDefaultStorageSpace();
		recordServices.add(storageRecord);

		getModelLayerFactory().getBatchProcessesManager().waitUntilAllFinished();
		Record record = searchServices.searchSingleResult(from(rm.storageSpace.schemaType()).where(Schemas.IDENTIFIER).isEqualTo("storageTest"));
		assertThat(rm.wrapStorageSpace(record).getLinearSizeEntered()).isNull();
		assertThat(rm.wrapStorageSpace(record).getLinearSizeSum()).isEqualTo(new Double(0));
		assertThat(rm.wrapStorageSpace(record).getLinearSize()).isEqualTo(new Double(0));
	}

	public StorageSpace buildDefaultStorageSpace() {
		return rm.newStorageSpaceWithId("storageTest").setCode("TEST").setTitle("storageTest");
	}

	public StorageSpace buildChildStorageSpace() {
		return rm.newStorageSpaceWithId("childStorage").setCode("CHILD").setTitle("childStorage");
	}

	public void addContainersLinkedToStorageSpace(String storageID)
			throws RecordServicesException {

		recordServices.add(rm.newContainerRecord().setTitle("title").setCapacity(new Double(2))
				.setStorageSpace(storageID).setType("containerTypeTest").setTemporaryIdentifier("containerTestTemporary1")
				.setAdministrativeUnits(asList(records.unitId_10))
				.setDecommissioningType(DecommissioningType.DEPOSIT)
		);
		recordServices.add(rm.newContainerRecord().setTitle("title").setCapacity(new Double(2))
				.setStorageSpace(storageID).setType("containerTypeTest").setTemporaryIdentifier("containerTestTemporary2")
				.setAdministrativeUnits(asList(records.unitId_10))
				.setDecommissioningType(DecommissioningType.DEPOSIT)
		);
		recordServices.add(rm.newContainerRecord().setTitle("title").setCapacity(new Double(2))
				.setStorageSpace(storageID).setType("containerTypeTest").setTemporaryIdentifier("containerTestTemporary3")
				.setAdministrativeUnits(asList(records.unitId_10))
				.setDecommissioningType(DecommissioningType.DEPOSIT)
		);
	}

	public ContainerRecordType buildDefaultContainerType() {
		return rm.newContainerRecordTypeWithId("containerTypeTest").setTitle("containerTypeTest").setCode("containerTypeTest");
	}
}
