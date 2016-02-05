package com.constellio.data.dao.managers.config.values;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PropertiesConfiguration {

	private final String version;

	private final Map<String, String> properties;

	public PropertiesConfiguration(String hash, Map<String, String> properties) {
		super();
		this.version = hash;
		this.properties = properties;
	}

	public String getHash() {
		return version;
	}

	public Map<String, String> getProperties() {
		return new HashMap<String, String>(properties);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
