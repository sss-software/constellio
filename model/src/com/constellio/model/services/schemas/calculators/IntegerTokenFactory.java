package com.constellio.model.services.schemas.calculators;

import com.constellio.model.entities.security.Role;

public class IntegerTokenFactory {

	static int FACTOR = (int) Math.pow(2, 28);

	static int REMOVED = 3;
	static int NEGATIVE = 1;

	public static int newAccessToken(int principalId, String access) {
		return newAccessToken(principalId, access, false, false);
	}

	public static int newNegativeAccessToken(int principalId, String access) {
		return newAccessToken(principalId, access, true, false);
	}

	public static int newAccessToken(int principalId, String access, boolean negative, boolean removedFlag) {

		int flags = 0;
		if (Role.WRITE.equals(access)) {
			flags = 1;

		} else if (Role.DELETE.equals(access)) {
			flags = 2;

		}

		flags *= 2;
		if (negative) {
			flags += 1;
		}

		return ((flags * FACTOR) + principalId) * (removedFlag ? -1 : 1);
	}

	public static int getPrincipalId(int token) {
		if (token < 0) {
			token *= -1;
		}
		return token % FACTOR;
	}

	public static String getAccess(int token) {
		if (token < 0) {
			token *= -1;
		}
		token /= FACTOR;
		token /= 2;

		if (token == 1) {
			return Role.WRITE;

		} else if (token == 2) {
			return Role.DELETE;

		} else {
			return Role.READ;
		}
	}

	public static boolean hasRemoveFlag(int token) {
		return token < 0;
	}

	public static boolean hasNegativeFlag(int token) {
		if (token < 0) {
			token *= -1;
		}
		token /= FACTOR;
		return token % 2 == 1;
	}

	public static int withRemovedNegativeFlag(int token) {
		String access = getAccess(token);
		int principalId = getPrincipalId(token);
		return newAccessToken(principalId, access, true, true);
	}


}
