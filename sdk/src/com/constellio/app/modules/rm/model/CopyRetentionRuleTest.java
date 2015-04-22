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
package com.constellio.app.modules.rm.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.constellio.app.modules.rm.model.enums.CopyType;
import com.constellio.app.modules.rm.model.enums.DisposalType;
import com.constellio.sdk.tests.ConstellioTest;

public class CopyRetentionRuleTest extends ConstellioTest {

	CopyRetentionRuleFactory factory = new CopyRetentionRuleFactory();

	@Test
	public void whenSetAttributeValueThenBecomeDirty() {
		CopyRetentionRule rule = new CopyRetentionRule();
		assertThat(rule.isDirty()).isFalse();

		rule = new CopyRetentionRule();
		rule.setCode("zeCode:\n\tline2");
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setCopyType(CopyType.PRINCIPAL);
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setMediumTypeIds(Arrays.asList("firstType", "secondType"));
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setContentTypesComment("zeContentTypesComment");
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setActiveRetentionPeriod(RetentionPeriod.OPEN_888);
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setActiveRetentionComment("zeActive_RétentionComment");
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setSemiActiveRetentionPeriod(RetentionPeriod.OPEN_999);
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setSemiActiveRetentionComment("zeSemi=;ActiveRetention-Comment");
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setInactiveDisposalType(DisposalType.DESTRUCTION);
		assertThat(rule.isDirty()).isTrue();

		rule = new CopyRetentionRule();
		rule.setInactiveDisposalComment("zeInactive:Disposable\nComment");
		assertThat(rule.isDirty()).isTrue();
	}

	@Test
	public void whenConvertingStructureWithCommentsAndCodeThenRemainsEqual()
			throws Exception {

		CopyRetentionRule rule = new CopyRetentionRule();
		rule.setCode("zeCode:\n\tline2");
		rule.setCopyType(CopyType.PRINCIPAL);
		rule.setMediumTypeIds(Arrays.asList("firstType", "secondType"));
		rule.setContentTypesComment("zeContentTypesComment");
		rule.setActiveRetentionPeriod(RetentionPeriod.OPEN_888);
		rule.setActiveRetentionComment("zeActive_RétentionComment");
		rule.setSemiActiveRetentionPeriod(RetentionPeriod.OPEN_999);
		rule.setSemiActiveRetentionComment("zeSemi=;ActiveRetention-Comment");
		rule.setInactiveDisposalType(DisposalType.DESTRUCTION);
		rule.setInactiveDisposalComment("zeInactive:Disposable\nComment");

		String stringValue = factory.toString(rule);
		CopyRetentionRule builtRule = (CopyRetentionRule) factory.build(stringValue);
		String stringValue2 = factory.toString(builtRule);

		assertThat(builtRule).isEqualTo(rule);
		assertThat(stringValue2).isEqualTo(stringValue);
		assertThat(builtRule.isDirty()).isFalse();

	}

	@Test
	public void whenConvertingStructureWithNullStatusAndCodeThenRemainsEqual()
			throws Exception {

		CopyRetentionRule rule = new CopyRetentionRule();
		rule.setCode("zeCode:\n\tline2");
		rule.setCopyType(CopyType.PRINCIPAL);
		rule.setMediumTypeIds(Arrays.asList("firstType", "secondType"));
		rule.setContentTypesComment("zeContentTypesComment");
		rule.setActiveRetentionPeriod(null);
		rule.setActiveRetentionComment("zeActive_RétentionComment");
		rule.setSemiActiveRetentionPeriod(null);
		rule.setSemiActiveRetentionComment("zeSemi=;ActiveRetention-Comment");
		rule.setInactiveDisposalType(null);
		rule.setInactiveDisposalComment("zeInactive:Disposable\nComment");

		String stringValue = factory.toString(rule);
		CopyRetentionRule builtRule = (CopyRetentionRule) factory.build(stringValue);
		String stringValue2 = factory.toString(builtRule);

		assertThat(builtRule).isEqualTo(rule);
		assertThat(stringValue2).isEqualTo(stringValue);
		assertThat(builtRule.isDirty()).isFalse();
		assertThat(builtRule.getActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(builtRule.getSemiActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(builtRule.getInactiveDisposalType()).isNull();

	}

	@Test
	public void whenConvertingStructureWithoutCommentsAndCodeThenRemainsEqual()
			throws Exception {

		CopyRetentionRule rule = new CopyRetentionRule();
		rule.setCopyType(CopyType.SECONDARY);
		rule.setMediumTypeIds(Arrays.asList("firstType", "secondType", "thirdType"));
		rule.setActiveRetentionPeriod(RetentionPeriod.fixed(1));
		rule.setSemiActiveRetentionPeriod(RetentionPeriod.fixed(2));
		rule.setInactiveDisposalType(DisposalType.SORT);

		String stringValue = factory.toString(rule);
		CopyRetentionRule builtRule = (CopyRetentionRule) factory.build(stringValue);
		String stringValue2 = factory.toString(builtRule);

		assertThat(builtRule).isEqualTo(rule);
		assertThat(stringValue2).isEqualTo(stringValue);

	}

	@Test
	public void whenCreateCopyRetentionRuleWithEmptyPeriodsThenSetToNull()
			throws Exception {

		CopyRetentionRule rule = CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, Arrays.asList("PA", "FI"), "888-0-");
		assertThat(rule.getActiveRetentionPeriod()).isEqualTo(RetentionPeriod.OPEN_888);
		assertThat(rule.getSemiActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(rule.getInactiveDisposalType()).isNull();

		rule = CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, Arrays.asList("PA", "FI"), "0-2-");
		assertThat(rule.getActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(rule.getSemiActiveRetentionPeriod()).isEqualTo(RetentionPeriod.fixed(2));
		assertThat(rule.getInactiveDisposalType()).isNull();

		rule = CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, Arrays.asList("PA", "FI"), "0-0-");
		assertThat(rule.getActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(rule.getSemiActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(rule.getInactiveDisposalType()).isNull();

		rule = CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, Arrays.asList("PA", "FI"), "0-0-T");
		assertThat(rule.getActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(rule.getSemiActiveRetentionPeriod()).isSameAs(RetentionPeriod.ZERO);
		assertThat(rule.getInactiveDisposalType()).isEqualTo(DisposalType.SORT);

	}

	@Test
	public void whenEvaluateDecommissioningActionsThenBasedOnTypes()
			throws Exception {

		List<String> types = Arrays.asList("PA", "FI");

		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-0-D").canTransferToSemiActive()).isFalse();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-0-D").canDeposit()).isFalse();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-0-D").canDestroy()).isTrue();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-0-D").canSort()).isFalse();

		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-T").canTransferToSemiActive()).isTrue();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-T").canDeposit()).isTrue();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-T").canDestroy()).isTrue();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-T").canSort()).isTrue();

		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-C").canTransferToSemiActive()).isTrue();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-C").canDeposit()).isTrue();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-C").canDestroy()).isFalse();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-C").canSort()).isFalse();

		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-").canTransferToSemiActive()).isTrue();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-").canDeposit()).isFalse();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-").canDestroy()).isFalse();
		assertThat(CopyRetentionRule.newRetentionRule(CopyType.PRINCIPAL, types, "888-2-").canSort()).isFalse();

	}
}
