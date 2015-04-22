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
package com.constellio.model.entities.records;

@SuppressWarnings("serial")
public class TransactionRuntimeException extends RuntimeException {

	private TransactionRuntimeException(String message) {
		super(message);
	}

	public static class RecordIdCollision extends TransactionRuntimeException {

		public RecordIdCollision() {
			super("Two different records added for the same id.");
		}

	}

	public static class RecordsWithoutIds extends TransactionRuntimeException {

		public RecordsWithoutIds() {
			super("Some records have no id, execute transaction before getting id");
		}

	}

	public static class DifferentCollectionsInRecords extends TransactionRuntimeException {

		public DifferentCollectionsInRecords(String collection, String otherCollection) {
			super("Differents collections in transaction's records: \"" + collection + "\" != \"" + otherCollection + "\"");
		}
	}

	public static class TransactionRuntimeException_ToMuchRecordsInTransaction extends TransactionRuntimeException {

		public TransactionRuntimeException_ToMuchRecordsInTransaction() {
			super("To much records in transaction, limit is 1000.");
		}
	}
}
