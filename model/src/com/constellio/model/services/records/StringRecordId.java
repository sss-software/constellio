package com.constellio.model.services.records;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StringRecordId implements RecordId {

	private static Map<Integer, String> mapping = new HashMap<>();

	private int intValue;
	private String id;

	public StringRecordId(String id) {
		this.id = id;
		this.intValue = Math.abs(id.hashCode()) * -1;
		//The first 100 ids are reserved to handle eventual conflicts
		if (intValue > -100) {
			//Handling the zero hashcode
			intValue -= 101;
		}
		String currentStrValue = mapping.get(intValue);
		if (currentStrValue == null) {
			synchronized (mapping) {
				mapping.put(intValue, id);
			}
		} else if (!id.equals(currentStrValue)) {
			throw new IllegalArgumentException("Id '" + id + "' has same hashcode value than id '" + currentStrValue + "' : " + intValue);
		}
	}

	public StringRecordId(int id) {
		this.id = mapping.get(id);
		this.intValue = id;
	}

	@Override
	public String stringValue() {
		return id;
	}

	@Override
	public int intValue() {
		return intValue;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

	@Override
	public boolean lesserThan(RecordId anotherRecordId) {
		if (anotherRecordId instanceof IntegerRecordId) {
			return false;
		} else {
			return stringValue().compareTo(anotherRecordId.stringValue()) < 0;
		}
	}

	@Override
	public boolean lesserOrEqual(RecordId anotherRecordId) {
		if (anotherRecordId instanceof IntegerRecordId) {
			return false;
		} else {
			return stringValue().compareTo(anotherRecordId.stringValue()) <= 0;
		}
	}

	@Override
	public boolean greaterThan(RecordId anotherRecordId) {
		if (anotherRecordId instanceof IntegerRecordId) {
			return true;
		} else {
			return stringValue().compareTo(anotherRecordId.stringValue()) > 0;
		}
	}

	@Override
	public boolean greaterOrEqual(RecordId anotherRecordId) {
		if (anotherRecordId instanceof IntegerRecordId) {
			return true;
		} else {
			return stringValue().compareTo(anotherRecordId.stringValue()) >= 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StringRecordId that = (StringRecordId) o;
		return Objects.equals(intValue, that.intValue);
	}

	@Override
	public int hashCode() {
		return intValue;
	}

	@Override
	public int compareTo(@NotNull Object anotherRecordId) {
		if (anotherRecordId instanceof IntegerRecordId) {
			return 1;
		} else if (anotherRecordId instanceof StringRecordId) {
			return stringValue().compareTo(((StringRecordId) anotherRecordId).stringValue());

		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return id;
	}
}
