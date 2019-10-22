package com.constellio.model.services.schemas.calculators;

import com.constellio.model.entities.security.Role;
import com.constellio.sdk.tests.ConstellioTest;
import org.junit.Test;

import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.getAccess;
import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.getPrincipalId;
import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.hasNegativeFlag;
import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.hasRemoveFlag;
import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.newAccessToken;
import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.newNegativeAccessToken;
import static com.constellio.model.services.schemas.calculators.IntegerTokenFactory.withRemovedNegativeFlag;
import static org.assertj.core.api.Assertions.assertThat;

public class IntAccessTokensCalculatorTest extends ConstellioTest {


	@Test
	public void whenCreatingTokensWithFlagsThenOK() {

		int maxSupportedPrincipals = 134_217_728;
		for (int principalId = 1; principalId < maxSupportedPrincipals; principalId++) {
			if (principalId % 10_000 == 0) {
				System.out.println("Testing principal id " + principalId);
			}
			int token = newAccessToken(principalId, Role.READ);

			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.READ);
			assertThat(hasRemoveFlag(token)).isFalse();
			assertThat(hasNegativeFlag(token)).isFalse();

			token = withRemovedNegativeFlag(token);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.READ);
			assertThat(hasRemoveFlag(token)).isTrue();
			assertThat(hasNegativeFlag(token)).isTrue();

			token = newAccessToken(principalId, Role.WRITE);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.WRITE);
			assertThat(hasRemoveFlag(token)).isFalse();
			assertThat(hasNegativeFlag(token)).isFalse();

			token = withRemovedNegativeFlag(token);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.WRITE);
			assertThat(hasRemoveFlag(token)).isTrue();
			assertThat(hasNegativeFlag(token)).isTrue();

			token = newAccessToken(principalId, Role.DELETE);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.DELETE);
			assertThat(hasRemoveFlag(token)).isFalse();
			assertThat(hasNegativeFlag(token)).isFalse();

			token = withRemovedNegativeFlag(token);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.DELETE);
			assertThat(hasRemoveFlag(token)).isTrue();
			assertThat(hasNegativeFlag(token)).isTrue();

			//Testing with negative flag

			token = newNegativeAccessToken(principalId, Role.READ);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.READ);
			assertThat(hasRemoveFlag(token)).isFalse();
			assertThat(hasNegativeFlag(token)).isTrue();

			token = withRemovedNegativeFlag(token);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.READ);
			assertThat(hasRemoveFlag(token)).isTrue();
			assertThat(hasNegativeFlag(token)).isTrue();

			token = newNegativeAccessToken(principalId, Role.WRITE);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.WRITE);
			assertThat(hasRemoveFlag(token)).isFalse();
			assertThat(hasNegativeFlag(token)).isTrue();

			token = withRemovedNegativeFlag(token);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.WRITE);
			assertThat(hasRemoveFlag(token)).isTrue();
			assertThat(hasNegativeFlag(token)).isTrue();

			token = newNegativeAccessToken(principalId, Role.DELETE);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.DELETE);
			assertThat(hasRemoveFlag(token)).isFalse();
			assertThat(hasNegativeFlag(token)).isTrue();

			token = withRemovedNegativeFlag(token);
			assertThat(getPrincipalId(token)).isEqualTo(principalId);
			assertThat(getAccess(token)).isEqualTo(Role.DELETE);
			assertThat(hasRemoveFlag(token)).isTrue();
			assertThat(hasNegativeFlag(token)).isTrue();

		}

	}
}
