package com.constellio.app.ui.entities;

import com.constellio.app.entities.schemasDisplay.enums.MetadataDisplayType;
import com.constellio.app.entities.schemasDisplay.enums.MetadataInputType;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.EnumWithSmallCode;
import com.constellio.model.entities.schemas.AllowedReferences;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.entities.schemas.ModifiableStructure;
import com.constellio.model.entities.schemas.StructureFactory;
import com.constellio.model.services.schemas.SchemaUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings("serial")
public class MetadataVO implements Serializable {
	final String code;
	final String datastoreCode;
	final MetadataValueType type;
	final String collection;
	final MetadataSchemaVO schema;
	final String schemaTypeCode;
	final Map<Locale, String> labels;
	final boolean readOnly;
	final boolean unmodifiable;
	final boolean required;
	final boolean multivalue;
	final boolean enabled;
	final Class<? extends Enum<?>> enumClass;
	final String[] taxonomyCodes;
	final MetadataInputType metadataInputType;
	final MetadataDisplayType metadataDisplayType;
	final AllowedReferences allowedReferences;
	final StructureFactory structureFactory;
	final String metadataGroup;
	final Object defaultValue;
	final String inputMask;
	final Set<String> customAttributes;
	final boolean multiLingual;
	final Locale locale;
	final Map<String, Object> customParameters;

	public MetadataVO(String code, MetadataValueType type, String collection, MetadataSchemaVO schema, boolean required,
			boolean multivalue, boolean readOnly, boolean unmodifiable, Map<Locale, String> labels,
			Class<? extends Enum<?>> enumClass, String[] taxonomyCodes, String schemaTypeCode,
			MetadataInputType metadataInputType,
			MetadataDisplayType metadataDisplayType, AllowedReferences allowedReferences, boolean enabled, StructureFactory structureFactory,
			String metadataGroup, Object defaultValue, Set<String> customAttributes, boolean multiLingual, Locale locale, Map<String,Object> customParameters) {
		this(code, null, type, collection, schema, required, multivalue, readOnly, unmodifiable, labels, enumClass,
				taxonomyCodes, schemaTypeCode, metadataInputType, metadataDisplayType, allowedReferences, enabled,
				structureFactory, metadataGroup,
				defaultValue, null, customAttributes, multiLingual, locale, customParameters);
	}



	public MetadataVO(String code, String datastoreCode, MetadataValueType type, String collection, MetadataSchemaVO schema,
					  boolean required, boolean multivalue, boolean readOnly, boolean unmodifiable,
					  Map<Locale, String> labels, Class<? extends Enum<?>> enumClass, String[] taxonomyCodes,
					  String schemaTypeCode, MetadataInputType metadataInputType, MetadataDisplayType metadataDisplayType,
					  AllowedReferences allowedReferences,
					  boolean enabled, StructureFactory structureFactory, String metadataGroup, Object defaultValue,
					  String inputMask, Set<String> customAttributes, boolean multiLingual, Locale locale,Map<String,Object> customParameters) {
		super();
		this.code = code;
		this.datastoreCode = datastoreCode;
		this.type = type;
		this.collection = collection;
		this.schema = schema;
		this.schemaTypeCode = schemaTypeCode;
		this.required = required;
		this.multivalue = multivalue;
		this.readOnly = readOnly;
		this.unmodifiable = unmodifiable;
		this.labels = labels;
		this.enumClass = enumClass;
		this.taxonomyCodes = taxonomyCodes;
		this.metadataInputType = metadataInputType;
		this.metadataDisplayType = metadataDisplayType;
		this.allowedReferences = allowedReferences;
		this.enabled = enabled;
		this.structureFactory = structureFactory;
		this.metadataGroup = metadataGroup;
		this.defaultValue = defaultValue;
		this.inputMask = inputMask;
		this.customAttributes = customAttributes;
		this.multiLingual = multiLingual;
		this.locale = locale;
		this.customParameters = customParameters;

		if (schema != null && !schema.getMetadatas().contains(this)) {
			schema.getMetadatas().add(this);
		}
	}

	public MetadataVO(String code, MetadataValueType type, String collection, MetadataSchemaVO schema,
			boolean required,
			boolean multivalue, boolean readOnly, Map<Locale, String> labels, Class<? extends Enum<?>> enumClass,
			String[] taxonomyCodes, String schemaTypeCode, MetadataInputType metadataInputType,
			MetadataDisplayType metadataDisplayType,
			AllowedReferences allowedReferences, String metadataGroup, Object defaultValue, boolean isWriteNullValues,
			Set<String> customAttributes, boolean multiLingual, Locale locale, Map<String, Object> customParameters) {

		this(code, type, collection, schema, required, multivalue, readOnly, false, labels, enumClass,
				taxonomyCodes, schemaTypeCode, metadataInputType, metadataDisplayType, allowedReferences, true, null,
				metadataGroup, defaultValue, customAttributes, multiLingual, locale, customParameters);
	}

	public MetadataVO() {
		super();
		this.code = "";
		this.datastoreCode = null;
		this.type = null;
		this.collection = null;
		this.schema = null;
		this.schemaTypeCode = "";
		this.required = false;
		this.multivalue = false;
		this.readOnly = false;
		this.unmodifiable = false;
		this.labels = new HashMap<>();
		this.enumClass = null;
		this.taxonomyCodes = new String[0];
		this.metadataInputType = null;
		this.metadataDisplayType = null;
		this.enabled = true;
		this.allowedReferences = null;
		this.structureFactory = null;
		this.metadataGroup = null;
		this.defaultValue = null;
		this.inputMask = null;
		this.customAttributes = new HashSet<>();
		this.multiLingual = false;
		this.locale = null;
		this.customParameters = new HashMap<>();
	}

	public String getCode() {
		return code;
	}

	public String getLocalCode() {
		return getCodeWithoutPrefix(code);
	}

	public static String getCodeWithoutPrefix(String code) {
		String codeWithoutPrefix;
		if (code != null) {
			String[] splittedCode = SchemaUtils.underscoreSplitWithCache(code);
			if (splittedCode.length == 3) {
				codeWithoutPrefix = splittedCode[2];
			} else {
				codeWithoutPrefix = code;
			}
		} else {
			codeWithoutPrefix = null;
		}
		return codeWithoutPrefix;
	}

	public boolean codeMatches(String code) {
		return getCodeWithoutPrefix(this.code).equals(getCodeWithoutPrefix(code));
	}

	public MetadataValueType getType() {
		return type;
	}

	public final String getCollection() {
		return collection;
	}

	public MetadataSchemaVO getSchema() {
		return schema;
	}

	public final String getSchemaTypeCode() {
		return schemaTypeCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isMultivalue() {
		return multivalue;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isUnmodifiable() {
		return unmodifiable;
	}

	public final MetadataInputType getMetadataInputType() {
		return metadataInputType;
	}

	public final MetadataDisplayType getMetadataDisplayType() {
		return metadataDisplayType;
	}

	public final StructureFactory getStructureFactory() {
		return structureFactory;
	}

	public Map<Locale, String> getLabels() {
		return labels;
	}

	public String getLabel(Locale locale) {
		String label;
		if (labels.containsKey(locale)) {
			label = labels.get(locale);
		} else {
			label = labels.get(new Locale(locale.getLanguage()));
		}
		return label;
	}

	public String getLabel() {
		return getLabel(ConstellioUI.getCurrentSessionContext().getCurrentLocale());
	}

	public String getLabel(SessionContext sessionContext) {
		return getLabel(sessionContext.getCurrentLocale());
	}

	public void setLabel(Locale locale, String label) {
		labels.put(locale, label);
	}

	public Class<?> getJavaType() {
		switch (type) {
		case BOOLEAN:
			return Boolean.class;
		case DATE:
			return LocalDate.class;
		case DATE_TIME:
			return LocalDateTime.class;
		case INTEGER:
			return Integer.class;
		case NUMBER:
			return Double.class;
		case STRING:
			return String.class;
		case STRUCTURE:
			return ModifiableStructure.class;
		case CONTENT:
			return ContentVersionVO.class;
		case TEXT:
			return String.class;
		case REFERENCE:
			if (enumClass != null) {
				return EnumWithSmallCode.class;
			} else {
				return String.class;
			}
		case ENUM:
			return Enum.class;
		default:
			return null;
		}
	}

	public Class<? extends Enum<?>> getEnumClass() {
		return enumClass;
	}

	public final String[] getTaxonomyCodes() {
		return taxonomyCodes;
	}

	public final AllowedReferences getAllowedReferences() {
		return allowedReferences;
	}

	public final String getMetadataGroup() {
		return metadataGroup;
	}

	public String getDatastoreCode() {
		return datastoreCode;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public String getInputMask() {
		return inputMask;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		} else {
			MetadataVO other = (MetadataVO) obj;
			if (code == null) {
				if (other.code != null) {
					return false;
				}
			} else if (!getCodeWithoutPrefix(code).equals(getCodeWithoutPrefix(other.code))) {
				return false;
			} else if (schema == null) {
				if (other.schema != null) {
					return false;
				}
				//			} else if (!schema.equals(other.schema)) {
				//				return false;
			} else if (locale == null) {
				if (other.locale != null) {
					return false;
				}
			} else if (!locale.equals(other.locale)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Used by Vaadin to populate the header of the column in a table (since we use MetadataVO objects as property ids).
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String toString;
		try {
			toString = getLabel(ConstellioUI.getCurrentSessionContext().getCurrentLocale());
		} catch (RuntimeException e) {
			toString = code;
		}
		return toString;
	}

	public boolean isSameLocalCode(Object object) {

		if (object == null || !(object instanceof MetadataVO)) {
			return false;
		}

		MetadataVO other = (MetadataVO) object;

		String localCode = new SchemaUtils().getLocalCode(getCode(), getSchema().getCode());
		String otherLocalCode = new SchemaUtils().getLocalCode(other.getCode(), other.getSchema().getCode());

		return localCode.equals(otherLocalCode);
	}

	public Set<String> getCustomAttributes() {
		return customAttributes;
	}

	public boolean hasCustomAttributes(String customAttribute) {
		return customAttributes.contains(customAttribute);
	}

	public boolean isMultiLingual() {
		return multiLingual;
	}

	public Locale getLocale() {
		return locale;
	}

}
