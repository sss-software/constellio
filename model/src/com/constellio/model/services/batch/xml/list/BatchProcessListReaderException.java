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
package com.constellio.model.services.batch.xml.list;

@SuppressWarnings("serial")
public class BatchProcessListReaderException extends Exception {

	public BatchProcessListReaderException() {
		super();
	}

	public BatchProcessListReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public BatchProcessListReaderException(String message) {
		super(message);
	}

	public BatchProcessListReaderException(Throwable cause) {
		super(cause);
	}

	public static class NoBatchProcessesInList extends BatchProcessListReaderException {

		public NoBatchProcessesInList(String status) {
			super("No batch processes in " + status + " list");
		}
	}
}
