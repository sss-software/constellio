package com.constellio.model.services.records.cache2;

import com.constellio.data.dao.dto.records.RecordDTO;
import com.constellio.data.utils.ImpossibleRuntimeException;
import com.constellio.data.utils.LangUtils;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchema;
import com.mchange.v2.collection.MapEntry;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.constellio.model.entities.schemas.MetadataValueType.CONTENT;
import static com.constellio.model.entities.schemas.MetadataValueType.STRING;
import static com.constellio.model.entities.schemas.MetadataValueType.STRUCTURE;
import static com.constellio.model.entities.schemas.MetadataValueType.TEXT;

/**
 * This utility class handle the reading and writing of a byte array regrouping a Record DTO metadata values
 * <p>
 * <p>
 * | metadatasSize | metadata1Id | value1IndexInByteArray | metadataNId | valueNIndexInByteArray | allValues |
 * <p>
 * Metadatas size is stocked using 2 bytes (Short)
 * Metadata id are stocked using 2 bytes (Short)
 * Value index reprensent the index of the data in the byte array. These indexes are stocked using 2 bytes (Short)
 * The size of values are not stocked in the byte array, but they can be easily computed by comparing the value index
 * with the index of the next metadata
 * <p>
 * Integer values
 * <p>
 * Header size :
 * <p>
 * 3 => 14
 * 4 => 18
 */
public class CacheRecordDTOUtils {

	private static final byte BYTES_TO_WRITE_METADATA_ID_AND_INDEX = 2;
	private static final byte BYTES_TO_WRITE_METADATA_VALUES_SIZE = 2;
	private static final byte BYTES_TO_WRITE_BOOLEAN_VALUES_SIZE = 1;
	private static final byte BYTES_TO_WRITE_BYTE_VALUES_SIZE = 1;
	private static final byte BYTES_TO_WRITE_INTEGER_VALUES_SIZE = 4;
	private static final byte BYTES_TO_WRITE_DOUBLE_VALUES_SIZE = 8;
	private static final byte BYTES_TO_WRITE_LONG_VALUES_SIZE = 8;
	private static final byte BYTES_TO_WRITE_DATE_VALUES_SIZE = 3;

	private static final byte KEY_IS_NOT_AN_INT = 0;
	private static final byte KEY_LENGTH = 11;

	private static final byte VALUE_IS_NOT_FOUND = -1;


	public static byte[] convertDTOToByteArray(RecordDTO dto, MetadataSchema schema) {
		CachedRecordDTOByteArrayBuilder builder = new CachedRecordDTOByteArrayBuilder();

		for (Metadata metadata : schema.getMetadatas()) {
			if (isCached(metadata)) {
				if (metadata.isMultivalue()) {
					List<Object> values = (List<Object>) dto.getFields().get(metadata.getDataStoreCode());
					if (values != null && !values.isEmpty()) {
						switch (metadata.getType()) {
							case REFERENCE:
								try {
									builder.addMultivalueReferenceMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case BOOLEAN:
								try {
									builder.addMultivalueBooleanMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case STRING:
								try {
									builder.addMultivalueStringMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case INTEGER:
								try {
									builder.addMultivalueIntegerMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case NUMBER:
								try {
									builder.addMultivalueNumberMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case DATE:
								try {
									builder.addMultivalueLocalDateMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case DATE_TIME:
								try {
									builder.addMultivalueLocalDateTimeMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case ENUM:
								try {
									builder.addMultivalueEnumMetadata(metadata, (List) values);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
						}
					}
				} else {
					Object value = dto.getFields().get(metadata.getDataStoreCode());
					if (value != null) {
						switch (metadata.getType()) {
							case REFERENCE:
								try {
									builder.addSingleValueReferenceMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case BOOLEAN:
								try {
									builder.addSingleValueBooleanMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case STRING:
								try {
									builder.addSingleValueStringMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case INTEGER:
								try {
									builder.addSingleValueIntegerMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case NUMBER:
								try {
									builder.addSingleValueNumberMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case DATE:
								try {
									builder.addSingleValueLocalDateMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case DATE_TIME:
								try {
									builder.addSingleValueLocalDateTimeMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							case ENUM:
								try {
									builder.addSingleValueEnumMetadata(metadata, value);
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
						}
					}
				}
			}

		}

		try {
			return builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static boolean isCached(Metadata metadata) {
		if (metadata.getType() == STRUCTURE || metadata.getType() == TEXT || metadata.getType() == CONTENT) {
			return false;
		}

		if (metadata.getType() == STRING) {
			//			return Schemas.TITLE.isSameLocalCode(metadata); TODO RE-ENABLE
			return true;
		}

		return true;
	}

	public static int toIntKey(Object key) {
		if (key instanceof Integer) {
			return ((Integer) key);
		}

		if (key instanceof Long) {
			return KEY_IS_NOT_AN_INT;
		}

		if (key instanceof String) {
			long value = LangUtils.tryParseLong((String) key, 0);

			if (((String) key).length() == KEY_LENGTH && value < Integer.MAX_VALUE) {
				return (int) value;
			} else {
				return KEY_IS_NOT_AN_INT;
			}
		}

		throw new ImpossibleRuntimeException("Invalid key : " + key);
	}

	private static int threeByteArrayToInt(byte[] byteArray, short startingIndex) {
		int i = (short) (128 * ((byte) (byteArray[startingIndex + 1] & (byte) 0x7f)) + byteArray[startingIndex + 2]);
		if (byteArray[startingIndex] >= 0) {
			return i * 127 + byteArray[startingIndex];
		} else {
			return -1 * (i * 127) + (byteArray[startingIndex] + 1);
		}
	}

	private static void intTo3ByteArray(int dateValue, byte[] bytes) {
		if (dateValue >= 0) {
			bytes[0] = (byte) ((dateValue % 127));
			dateValue = dateValue / 127;
		} else {
			bytes[0] = (byte) (-1 * (1 + ((-1 * dateValue) % 127)));
			dateValue = ((dateValue * -1) / 127);
		}

		bytes[1] = (byte) (((short) (dateValue >> 7)) & ((short) 0x7f) | 0x80);
		bytes[2] = (byte) ((dateValue & ((short) 0x7f)));
	}

	public static <T> T readMetadata(byte[] byteArray, MetadataSchema schema, String metadataLocalCode) {
		short metadatasSize = metadatasSize(byteArray);
		Metadata metadataSearched = schema.getMetadataByDatastoreCode(metadataLocalCode);

		short[] indexes = getMetadataSearchedIndexAndNextMetadataIndex(byteArray, metadataSearched, metadatasSize);
		short metadataSearchedIndex = indexes[0];
		short nextMetadataIndex = indexes[1];

		return parseValueMetadata(byteArray, metadataSearched, metadataSearchedIndex, nextMetadataIndex);
	}

	public static Set<String> getStoredMetadatas(byte[] byteArray, MetadataSchema schema) {
		short metadatasSize = metadatasSize(byteArray);
		Set<String> storedMetadatas = new HashSet<>();

		for (Short id : getAllMetadatasId(byteArray, metadatasSize)) {
			storedMetadatas.add(schema.getMetadataById(id).getDataStoreCode());
		}

		return storedMetadatas;
	}

	public static Set<Object> getStoredValues(byte[] byteArray, MetadataSchema schema) {
		short metadatasSize = metadatasSize(byteArray);
		Set<Object> storedValues = new HashSet<>();

		short headerBytesSize = (short) (metadatasSize * (BYTES_TO_WRITE_METADATA_ID_AND_INDEX + BYTES_TO_WRITE_METADATA_ID_AND_INDEX) + BYTES_TO_WRITE_METADATA_ID_AND_INDEX);

		for (short i = BYTES_TO_WRITE_METADATA_ID_AND_INDEX; i < headerBytesSize; i += (BYTES_TO_WRITE_METADATA_ID_AND_INDEX + BYTES_TO_WRITE_METADATA_ID_AND_INDEX)) {
			// needed to know how to parse the value
			Metadata metadataSearched = schema.getMetadataById(parseShortFromByteArray(byteArray, i));
			// needed to know where to start and stop parsing the value
			// + 2 * 2 because the index is the next to bytes in the array
			short metadataSearchedIndex = (short) (headerBytesSize + parseShortFromByteArray(byteArray, (short) (i + BYTES_TO_WRITE_METADATA_ID_AND_INDEX)));
			// * 3 because we want to skip the index of the metadata we are searching since have it
			// and we want to skip the id of the following metadata, we only need it's index
			short nextMetadataIndex = (short) (headerBytesSize + parseShortFromByteArray(byteArray, (short) (i + (BYTES_TO_WRITE_METADATA_ID_AND_INDEX * 3)))); // TODO CORRECT THIS

			storedValues.add(parseValueMetadata(byteArray, metadataSearched, metadataSearchedIndex, nextMetadataIndex));
		}

		return storedValues;
	}

	public static Set<Entry<String, Object>> toEntrySet(byte[] byteArray, MetadataSchema schema) {
		Set<Entry<String, Object>> metadatasEntrySet = new HashSet<>();

		short metadatasSize = metadatasSize(byteArray);

		short headerBytesSize = (short) (metadatasSize * (BYTES_TO_WRITE_METADATA_ID_AND_INDEX + BYTES_TO_WRITE_METADATA_ID_AND_INDEX) + BYTES_TO_WRITE_METADATA_ID_AND_INDEX);

		for (short i = BYTES_TO_WRITE_METADATA_ID_AND_INDEX; i < headerBytesSize; i += (BYTES_TO_WRITE_METADATA_ID_AND_INDEX + BYTES_TO_WRITE_METADATA_ID_AND_INDEX)) {
			// needed to know how to parse the value
			Metadata metadataSearched = schema.getMetadataById(parseShortFromByteArray(byteArray, i));
			// needed to know where to start and stop parsing the value
			// + 2 * 2 because the index is the next to bytes in the array
			short metadataSearchedIndex = (short) (headerBytesSize + parseShortFromByteArray(byteArray, (short) (i + BYTES_TO_WRITE_METADATA_ID_AND_INDEX)));
			// * 3 because we want to skip the index of the metadata we are searching since have it
			// and we want to skip the id of the following metadata, we only need it's index
			short possibleNextMetadataIndex = (short) (i + (BYTES_TO_WRITE_METADATA_ID_AND_INDEX * 3));
			short nextMetadataIndex;
			// +1 to complete the 2 bytes taken by the short
			if (!(possibleNextMetadataIndex + 1 > headerBytesSize)) {
				nextMetadataIndex = (short) (headerBytesSize + parseShortFromByteArray(byteArray, possibleNextMetadataIndex));
			} else {
				nextMetadataIndex = (short) (byteArray.length + 1);
			}

			metadatasEntrySet.add(new MapEntry(metadataSearched.getDataStoreCode(), parseValueMetadata(byteArray, metadataSearched, metadataSearchedIndex, nextMetadataIndex)));
		}

		return metadatasEntrySet;
	}

	public static boolean containsMetadata(byte[] data, MetadataSchema schema, String key) {
		short metadatasSize = metadatasSize(data);
		Metadata metadataSearched = schema.getMetadataByDatastoreCode(key);

		// [0] because we want the searched index not the next one
		return VALUE_IS_NOT_FOUND != getMetadataSearchedIndexAndNextMetadataIndex(data, metadataSearched, metadatasSize)[0];
	}

	public static short metadatasSize(byte[] data) {
		// returns the first 2 bytes converted as a short because its the metadatasSize stored
		return parseShortFromByteArray(data, (short) 0);
	}

	private static short[] getMetadataSearchedIndexAndNextMetadataIndex(byte[] byteArray, Metadata metadataSearched,
																		short metadatasSize) {
		short[] indexes = new short[2];

		short metadataSearchedId = metadataSearched.getId();

		// *(2+2) for the bytes taken by the id and index of each metadata and +2 to skip the metadatasSize and the start of the array
		short headerBytesSize = (short) ((metadatasSize * (BYTES_TO_WRITE_METADATA_ID_AND_INDEX + BYTES_TO_WRITE_METADATA_ID_AND_INDEX))
										 + BYTES_TO_WRITE_METADATA_ID_AND_INDEX);

		// skipping first two byte because it's the metadatasSize
		// i+=2*2 because we are just looking for the metadataId not the metadataValue
		for (short i = BYTES_TO_WRITE_METADATA_ID_AND_INDEX; i < headerBytesSize; i += BYTES_TO_WRITE_METADATA_ID_AND_INDEX * 2) {
			short id = parseShortFromByteArray(byteArray, i);

			if (id == metadataSearchedId) {
				// Looking for next 2 bytes to get the index in the data part of the array
				indexes[0] = (short) (headerBytesSize + parseShortFromByteArray(byteArray, (short) (i + BYTES_TO_WRITE_METADATA_ID_AND_INDEX)));
				short possibleNextMetadataIndex = (short) (i + (BYTES_TO_WRITE_METADATA_ID_AND_INDEX * 3));
				// +1 to complete the 2 bytes taken by the short
				if (!(possibleNextMetadataIndex + 1 > headerBytesSize)) {
					indexes[1] = (short) (headerBytesSize + parseShortFromByteArray(byteArray, possibleNextMetadataIndex));
				} else {
					indexes[1] = (short) (byteArray.length + 1);
				}

				return indexes;
			}
		}

		//		return VALUE_IS_NOT_FOUND;
		indexes[0] = VALUE_IS_NOT_FOUND;
		return indexes;
	}

	private static List<Short> getAllMetadatasId(byte[] byteArray, short metadatasSize) {
		List<Short> metadatasId = new ArrayList<>();

		// *(2+2) for the bytes taken by the id and index of each metadata and +2 to skip the metadatasSize and the start of the array
		short headerBytesSize = (short) ((metadatasSize * (BYTES_TO_WRITE_METADATA_ID_AND_INDEX + BYTES_TO_WRITE_METADATA_ID_AND_INDEX))
										 + BYTES_TO_WRITE_METADATA_ID_AND_INDEX);

		for (short i = BYTES_TO_WRITE_METADATA_ID_AND_INDEX; i < headerBytesSize; i += BYTES_TO_WRITE_METADATA_ID_AND_INDEX * 2) {
			metadatasId.add(parseShortFromByteArray(byteArray, i));
		}

		return metadatasId;
	}

	private static <T> T parseValueMetadata(byte[] byteArray, Metadata metadataSearched, short metadataSearchedIndex,
											short nextMetadataIndex) {
		if (isIndexValid(metadataSearchedIndex)) {
			if (metadataSearched.isMultivalue()) {
				switch (metadataSearched.getType()) {
					case BOOLEAN:
						return (T) getMultivalueBooleanMetadata(byteArray, metadataSearchedIndex);
					case REFERENCE:
						return (T) getMultivalueReferenceMetadata(byteArray, metadataSearchedIndex);
					case STRING:
						return (T) getMultivalueStringMetadata(byteArray, metadataSearchedIndex);
					case INTEGER:
						return (T) getMultivalueIntegerMetadata(byteArray, metadataSearchedIndex);
					case NUMBER:
						return (T) getMultivalueNumberMetadata(byteArray, metadataSearchedIndex);
					case DATE:
						return (T) getMultivalueLocalDateMetadata(byteArray, metadataSearchedIndex);
					case DATE_TIME:
						return (T) getMultivalueLocalDateTimeMetadata(byteArray, metadataSearchedIndex);
					case ENUM:
						return (T) getMultivalueEnumMetadata(byteArray, metadataSearchedIndex, metadataSearched);
				}
			} else {
				switch (metadataSearched.getType()) {
					case BOOLEAN:
						return (T) getSingleValueBooleanMetadata(byteArray, metadataSearchedIndex);
					case REFERENCE:
						return (T) getSingleValueReferenceMetadata(byteArray, metadataSearchedIndex);
					case STRING:
						return (T) getSingleValueStringMetadata(byteArray, metadataSearchedIndex, nextMetadataIndex);
					case INTEGER:
						return (T) getSingleValueIntegerMetadata(byteArray, metadataSearchedIndex);
					case NUMBER:
						return (T) getSingleValueNumberMetadata(byteArray, metadataSearchedIndex);
					case DATE:
						return (T) getSingleValueLocalDateMetadata(byteArray, metadataSearchedIndex);
					case DATE_TIME:
						return (T) getSingleValueLocalDateTimeMetadata(byteArray, metadataSearchedIndex);
					case ENUM:
						return (T) getSingleValueEnumMetadata(byteArray, metadataSearchedIndex, metadataSearched);
				}
			}
		}

		return null;
	}

	private static boolean isIndexValid(short index) {
		if (VALUE_IS_NOT_FOUND == index) {
			return false;
		} else {
			return true;
		}
	}

	private static Boolean getSingleValueBooleanMetadata(byte[] byteArray, short metadataSearchedIndex) {
		return parseBooleanFromByteArray(byteArray, metadataSearchedIndex);
	}

	private static List<Boolean> getMultivalueBooleanMetadata(byte[] byteArray, short metadataSearchedIndex) {
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<Boolean> booleans = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				booleans.add(parseBooleanFromByteArray(byteArray, currentIndex));
				currentIndex += BYTES_TO_WRITE_BOOLEAN_VALUES_SIZE;
				numberOfMetadatasFound++;
			}

			return booleans;
		}

		return null;
	}

	private static String getSingleValueReferenceMetadata(byte[] byteArray, short metadataSearchedIndex) {
		int stringValue = parseIntFromByteArray(byteArray, metadataSearchedIndex);

		// if the value is higher than 0 it's positive so it is a regular id
		// else if it's negative it's a string containing letters most likely
		if (stringValue > 0) {
			return formatToId(stringValue);
		} else {
			return parseStringReferenceFromByteArray(byteArray, metadataSearchedIndex);
		}
	}

	private static List<String> getMultivalueReferenceMetadata(byte[] byteArray, short metadataSearchedIndex) {
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<String> references = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				int stringValue = parseIntFromByteArray(byteArray, currentIndex);

				// if the value is higher than 0 it's positive so it is a regular id
				// if it's equal to 0 the value is null
				// else it's negative and it's a string containing letters most likely
				if (stringValue > 0) {
					references.add(formatToId(stringValue));
					currentIndex += BYTES_TO_WRITE_INTEGER_VALUES_SIZE;
				} else if (stringValue == 0) {
					references.add(null);
					currentIndex += BYTES_TO_WRITE_INTEGER_VALUES_SIZE;
				} else {
					references.add(parseStringReferenceFromByteArray(byteArray, currentIndex));
					// in this case the stringValue represent the size of bytes taken by the string (1 each char) or the bytes length if you prefer
					// * -1 to get a positive value since the length of strings is stored as a negative
					// + 4 for the bytes taken by the size value
					currentIndex += stringValue * -1 + BYTES_TO_WRITE_INTEGER_VALUES_SIZE;
				}

				numberOfMetadatasFound++;
			}

			return references;
		}

		return null;
	}

	private static Integer getSingleValueIntegerMetadata(byte[] byteArray, short metadataSearchedIndex) {
		return parseIntFromByteArray(byteArray, metadataSearchedIndex);
	}

	private static List<Integer> getMultivalueIntegerMetadata(byte[] byteArray, short metadataSearchedIndex) {
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<Integer> integers = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				integers.add(parseIntFromByteArray(byteArray, currentIndex));
				currentIndex += BYTES_TO_WRITE_INTEGER_VALUES_SIZE;

				numberOfMetadatasFound++;
			}

			return integers;
		}

		return null;
	}

	private static Double getSingleValueNumberMetadata(byte[] byteArray, short metadataSearchedIndex) {
		return parseDoubleFromByteArray(byteArray, metadataSearchedIndex);
	}

	private static List<Double> getMultivalueNumberMetadata(byte[] byteArray, short metadataSearchedIndex) {
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<Double> doubles = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				doubles.add(parseDoubleFromByteArray(byteArray, currentIndex));
				currentIndex += BYTES_TO_WRITE_DOUBLE_VALUES_SIZE;

				numberOfMetadatasFound++;
			}

			return doubles;
		}

		return null;
	}

	private static String getSingleValueStringMetadata(byte[] byteArray, short metadataSearchedIndex,
													   short nextMetadataIndex) {
		// - 1 to not read the first byte of the next metadataIndex since "to" is inclusive
		byte[] stringValueAsByte = Arrays.copyOfRange(byteArray, metadataSearchedIndex, nextMetadataIndex - 1);

		return new String(stringValueAsByte);
	}

	private static List<String> getMultivalueStringMetadata(byte[] byteArray, short metadataSearchedIndex) {
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<String> strings = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				int stringValue = parseIntFromByteArray(byteArray, currentIndex);

				// if the stringValue is higher then 0 it's a valid string
				// else if the stringValue lower or equal to zero the string is null
				if (stringValue > 0) {
					strings.add(parseStringWithLengthFromByteArray(byteArray, currentIndex));
					// in this case the stringValue represent the size of bytes taken by the string (1 each char) or the bytes length if you prefer
					// + 4 for the bytes taken by the size value
					// this time the value is positive to represent a non null value
					currentIndex += stringValue + BYTES_TO_WRITE_INTEGER_VALUES_SIZE;
				} else if (stringValue <= 0) {
					strings.add(null);
					// + 2 only because we only need the size to no if it's null
					// negative string length is a null
					currentIndex += BYTES_TO_WRITE_INTEGER_VALUES_SIZE;
				}

				numberOfMetadatasFound++;
			}

			return strings;
		}

		return null;
	}

	private static LocalDate getSingleValueLocalDateMetadata(byte[] byteArray, short metadataSearchedIndex) {
		return parseLocalDateFromByteArray(byteArray, metadataSearchedIndex);
	}

	private static List<LocalDate> getMultivalueLocalDateMetadata(byte[] byteArray, short metadataSearchedIndex) {
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<LocalDate> dates = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				dates.add(parseLocalDateFromByteArray(byteArray, currentIndex));
				// + 3 for the bytes taken by LocalDate
				currentIndex += BYTES_TO_WRITE_DATE_VALUES_SIZE;

				numberOfMetadatasFound++;
			}

			return dates;
		}

		return null;
	}

	private static LocalDateTime getSingleValueLocalDateTimeMetadata(byte[] byteArray, short metadataSearchedIndex) {
		return parseLocalDateTimeFromByteArray(byteArray, metadataSearchedIndex);
	}

	private static List<LocalDateTime> getMultivalueLocalDateTimeMetadata(byte[] byteArray,
																		  short metadataSearchedIndex) {
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<LocalDateTime> dates = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				dates.add(parseLocalDateTimeFromByteArray(byteArray, currentIndex));
				// + 8 for the bytes taken by the epoch time of the LocalDateTime
				currentIndex += BYTES_TO_WRITE_LONG_VALUES_SIZE;

				numberOfMetadatasFound++;
			}

			return dates;
		}

		return null;
	}

	private static Enum getSingleValueEnumMetadata(byte[] byteArray, short metadataSearchedIndex,
												   Metadata metadataSearched) {
		return parseEnumFromByteArray(metadataSearched.getEnumClass(), byteArray[metadataSearchedIndex]);
	}

	private static List<Enum> getMultivalueEnumMetadata(byte[] byteArray, short metadataSearchedIndex,
														Metadata metadataSearched){
		short numberOfMetadatas = parseShortFromByteArray(byteArray, metadataSearchedIndex);

		if (numberOfMetadatas > 0) {
			List<Enum> enums = new ArrayList<>();

			short numberOfMetadatasFound = 0;
			// + 2 since we don't want to parse the size again
			short currentIndex = (short) (metadataSearchedIndex + BYTES_TO_WRITE_METADATA_VALUES_SIZE);

			while (numberOfMetadatas != numberOfMetadatasFound) {
				enums.add(parseEnumFromByteArray(metadataSearched.getEnumClass(), byteArray[currentIndex]));
				currentIndex += BYTES_TO_WRITE_BYTE_VALUES_SIZE;

				numberOfMetadatasFound++;
			}

			return enums;
		}

		return null;
	}

	private static String formatToId(int id) {
		return String.format("%0" + KEY_LENGTH + "d", id);
	}

	private static boolean parseBooleanFromByteArray(byte[] byteArray, short startingIndex) {
		return byteArray[startingIndex] == (byte) 1;
	}

	private static short parseShortFromByteArray(byte[] byteArray, short startingIndex) {
		// + 1 for the second byte taken by the short
		return (short) (((byteArray[startingIndex] & 0xFF) << 8) | (byteArray[startingIndex + 1] & 0xFF));
	}

	private static int parseIntFromByteArray(byte[] byteArray, short startingIndex) {
		// + 1, + 2, + 3 for to get the four bytes taken by the integer
		return byteArray[startingIndex] & 0xFF << 24 | (byteArray[startingIndex + 1] & 0xFF) << 16 |
			   (byteArray[startingIndex + 2] & 0xFF) << 8 | (byteArray[startingIndex + 3] & 0xFF);
	}

	private static String parseStringReferenceFromByteArray(byte[] byteArray, short startingIndex) {
		// * -1 to get the positive value of the bytes length of the array since it's stored as a negative
		// to not confuse a string and a id when parsing
		int stringBytesLength = -1 * parseIntFromByteArray(byteArray, startingIndex);

		// + 4 to skip the string length 4 bytes
		short startingStringPosition = (short) (startingIndex + BYTES_TO_WRITE_INTEGER_VALUES_SIZE);
		byte[] stringValueAsByte = Arrays.copyOfRange(byteArray, startingStringPosition, startingStringPosition + stringBytesLength);

		return new String(stringValueAsByte);
	}

	private static String parseStringWithLengthFromByteArray(byte[] byteArray, short startingIndex) {
		int stringBytesLength = parseIntFromByteArray(byteArray, startingIndex);

		// + 4 to skip the string length 4 bytes
		short startingStringPosition = (short) (startingIndex + BYTES_TO_WRITE_INTEGER_VALUES_SIZE);
		byte[] stringValueAsByte = Arrays.copyOfRange(byteArray, startingStringPosition, startingStringPosition + stringBytesLength);

		return new String(stringValueAsByte);
	}

	private static double parseDoubleFromByteArray(byte[] byteArray, short startingIndex) {
		return Double.longBitsToDouble(parseLongFromByteArray(byteArray, startingIndex));
	}

	private static long parseLongFromByteArray(byte[] byteArray, short startingIndex) {
		return ((long) (byteArray[startingIndex] & 0xFF)) << 56 | ((long) (byteArray[startingIndex + 1] & 0xFF)) << 48 |
			   ((long) (byteArray[startingIndex + 2] & 0xFF)) << 40 | ((long) (byteArray[startingIndex + 3] & 0xFF)) << 32 |
			   ((long) (byteArray[startingIndex + 4] & 0xFF)) << 24 | ((long) (byteArray[startingIndex + 5] & 0xFF)) << 16 |
			   ((long) (byteArray[startingIndex + 6] & 0xFF)) << 8 | ((long) (byteArray[startingIndex + 7] & 0xFF));
	}

	private static LocalDate parseLocalDateFromByteArray(byte[] byteArray, short startingIndex) {
		int value = threeByteArrayToInt(byteArray, startingIndex);
		short dayMonth = (short) ((value < 0 ? value * -1 : value) % 400);
		int deltaYear = value / 400;
		int year = deltaYear + 1900;
		short month = (short) (1 + dayMonth / 31);
		short day = (short) (1 + dayMonth % 31);

		return new LocalDate(year, month, day);
	}

	private static LocalDateTime parseLocalDateTimeFromByteArray(byte[] byteArray, short startingIndex) {
		long epochTimeInMillis = parseLongFromByteArray(byteArray, startingIndex);

		return new LocalDateTime(epochTimeInMillis);
	}

	private static <T extends Enum> T parseEnumFromByteArray(Class<? extends Enum> clazz, byte value) {
		try {
			// - acts as a plus since Byte.MIN_VALUE is -128
			return (T) ((Object[]) clazz.getMethod("values").invoke(null))[((int) value - Byte.MIN_VALUE)];
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private static class CachedRecordDTOByteArrayBuilder {

		private int headerByteArrayLength;
		private int dataByteArrayLength;
		private short metadatasSize;

		private ByteArrayOutputStream headerOutput;
		private ByteArrayOutputStream dataOutput;
		private DataOutputStream headerWriter;
		private DataOutputStream dataWriter;

		public CachedRecordDTOByteArrayBuilder() {
			this.headerByteArrayLength = 0;
			this.dataByteArrayLength = 0;
			this.metadatasSize = 0;

			this.headerOutput = new ByteArrayOutputStream();
			this.dataOutput = new ByteArrayOutputStream();
			this.headerWriter = new DataOutputStream(headerOutput);
			this.dataWriter = new DataOutputStream(dataOutput);
		}

		/**
		 * Value is stored using 1 byte
		 */
		private void addSingleValueBooleanMetadata(Metadata metadata, Object value) throws IOException {
			dataWriter.writeByte(((boolean) value ? 1 : 0));

			writeHeader(metadata);

			dataByteArrayLength += BYTES_TO_WRITE_BOOLEAN_VALUES_SIZE;
			metadatasSize++;
		}

		private void addMultivalueBooleanMetadata(Metadata metadata, List<Boolean> metadatas) throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (boolean value : metadatas) {
				dataWriter.writeByte((value ? 1 : 0));

				dataByteArrayLength += BYTES_TO_WRITE_BOOLEAN_VALUES_SIZE;
			}

			metadatasSize++;
		}

		/**
		 * Value is usually integer, but can also be String or Null.
		 * <p>
		 * Integer value is stocked using 4 bytes (Integer)
		 * String value is stocked using a 4 bytes negative value (Integer), where the value represent the size of bytes used to store the String value the characters are stored as bytes
		 */
		private void addSingleValueReferenceMetadata(Metadata metadata, Object value) throws IOException {
			int key = toIntKey(value);
			short size = 0;

			if (key != KEY_IS_NOT_AN_INT) {
				dataWriter.writeInt(key);
			} else {
				size = (short) value.toString().getBytes(StandardCharsets.UTF_8).length;
				writeStringReference((String) value, size);
			}

			writeHeader(metadata);

			// + size if it's a string to represent each byte taken by a char
			// else 0 to not take unused space
			dataByteArrayLength += BYTES_TO_WRITE_INTEGER_VALUES_SIZE + size;

			metadatasSize++;
		}

		/**
		 * Data is structured like this :
		 * | listSize | value1 | value 2 | value N |
		 * <p>
		 * listSize is stocked using 2 bytes (Short)
		 * <p>
		 * Values are usually integer, but can also be String or Null.
		 * <p>
		 * Integer value is stocked using 4 bytes (Integer)
		 * Null value is stocked using a 4 bytes "zero value" (Integer)
		 * String value is stocked using a 4 bytes negative value (Integer), where the value represent the size of bytes used to store the String value
		 */
		private void addMultivalueReferenceMetadata(Metadata metadata, List<String> metadatas)
				throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (String value : metadatas) {
				short size = 0;
				if (null != value) {
					int key = toIntKey(value);

					if (key != KEY_IS_NOT_AN_INT) {
						dataWriter.writeInt(key);
					} else {
						size = (short) value.getBytes(StandardCharsets.UTF_8).length;
						writeStringReference(value, size);
					}
				} else {
					dataWriter.writeInt(0);
				}

				// + size if it's a string to represent each byte taken by a char
				dataByteArrayLength += BYTES_TO_WRITE_INTEGER_VALUES_SIZE + size;
			}

			metadatasSize++;
		}

		private void addSingleValueStringMetadata(Metadata metadata, Object value) throws IOException {
			byte[] title = ((String) value).getBytes();

			// if empty string not worth writing
			if (0 < title.length) {
				dataWriter.write(title);

				writeHeader(metadata);

				dataByteArrayLength += title.length;
				metadatasSize++;
			}
		}

		private void addMultivalueStringMetadata(Metadata metadata, List<String> metadatas) throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (String value : metadatas) {
				short size = 0;
				if (null == value) {
					dataWriter.writeInt(0);
				} else {
					size = (short) value.getBytes(StandardCharsets.UTF_8).length;
					writeStringWithLength(value, size);
				}

				// + size if it's a string to represent each byte taken by a char
				dataByteArrayLength += BYTES_TO_WRITE_INTEGER_VALUES_SIZE + size;
			}

			metadatasSize++;
		}

		private void addSingleValueIntegerMetadata(Metadata metadata, Object value) throws IOException {
			// TODO Find a better way
			if (value instanceof Double) {
				dataWriter.writeInt(((Double) value).intValue());
			} else {
				dataWriter.writeInt((int) value);
			}

			writeHeader(metadata);

			dataByteArrayLength += BYTES_TO_WRITE_INTEGER_VALUES_SIZE;
			metadatasSize++;
		}

		private void addMultivalueIntegerMetadata(Metadata metadata, List<Integer> metadatas) throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (int value : metadatas) {
				dataWriter.writeInt(value);

				dataByteArrayLength += BYTES_TO_WRITE_INTEGER_VALUES_SIZE;
			}

			metadatasSize++;
		}

		private void addSingleValueNumberMetadata(Metadata metadata, Object value) throws IOException {
			dataWriter.writeDouble((double) value);

			writeHeader(metadata);

			dataByteArrayLength += BYTES_TO_WRITE_DOUBLE_VALUES_SIZE;
			metadatasSize++;
		}

		private void addMultivalueNumberMetadata(Metadata metadata, List<Double> metadatas) throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (double value : metadatas) {
				dataWriter.writeDouble(value);

				dataByteArrayLength += BYTES_TO_WRITE_DOUBLE_VALUES_SIZE;
			}

			metadatasSize++;
		}

		private void addSingleValueLocalDateMetadata(Metadata metadata, Object value) throws IOException {
			writeLocalDate((LocalDate) value);

			writeHeader(metadata);

			dataByteArrayLength += BYTES_TO_WRITE_DATE_VALUES_SIZE;
			metadatasSize++;
		}

		private void addMultivalueLocalDateMetadata(Metadata metadata, List<LocalDate> metadatas) throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (LocalDate date : metadatas) {
				writeLocalDate(date);

				dataByteArrayLength += BYTES_TO_WRITE_DATE_VALUES_SIZE;
			}

			metadatasSize++;
		}

		private void addSingleValueLocalDateTimeMetadata(Metadata metadata, Object value) throws IOException {
			writeLocalDateTime((LocalDateTime) value);

			writeHeader(metadata);

			dataByteArrayLength += BYTES_TO_WRITE_LONG_VALUES_SIZE;
			metadatasSize++;
		}

		private void addMultivalueLocalDateTimeMetadata(Metadata metadata, List<LocalDateTime> metadatas)
				throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (LocalDateTime dateTime : metadatas) {
				writeLocalDateTime(dateTime);

				dataByteArrayLength += BYTES_TO_WRITE_LONG_VALUES_SIZE;
			}

			metadatasSize++;
		}

		private void addSingleValueEnumMetadata(Metadata metadata, Object value) throws IOException {
			writeEnum(((Enum) value));

			writeHeader(metadata);

			dataByteArrayLength += BYTES_TO_WRITE_BYTE_VALUES_SIZE;
			metadatasSize++;
		}

		private void addMultivalueEnumMetadata(Metadata metadata, List<Enum> metadatas) throws IOException {
			writeMultivalueSize(metadata, (short) metadatas.size());

			for (Enum e : metadatas) {
				writeEnum(e);

				dataByteArrayLength += BYTES_TO_WRITE_BYTE_VALUES_SIZE;
			}

			metadatasSize++;
		}

		private void writeMultivalueSize(Metadata metadata, short listSize) throws IOException {
			dataWriter.writeShort(listSize);
			writeHeader(metadata);
			dataByteArrayLength += BYTES_TO_WRITE_METADATA_VALUES_SIZE;
		}

		private void writeStringReference(String value, short size) throws IOException {
			dataWriter.writeInt(-size);
			dataWriter.writeBytes(value);
		}

		private void writeStringWithLength(String value, short size) throws IOException {
			dataWriter.writeInt(size);
			dataWriter.writeBytes(value);
		}

		private void writeLocalDate(LocalDate date) throws IOException {
			byte[] bytes = new byte[BYTES_TO_WRITE_DATE_VALUES_SIZE];

			int year = date.getYear();
			short month = (short) date.getMonthOfYear();
			short day = (short) date.getDayOfMonth();

			int dayMonth = ((month - 1) * 31 + (day - 1));
			int deltaYear = (year - 1900);

			int dateValue;
			if (deltaYear < 0) {
				dateValue = deltaYear * 400 + dayMonth * -1;
			} else {
				dateValue = deltaYear * 400 + dayMonth;
			}

			intTo3ByteArray(dateValue, bytes);
			dataWriter.write(bytes);
		}

		private void writeLocalDateTime(LocalDateTime dateTime) throws IOException {
			dataWriter.writeLong(dateTime.toDateTime().getMillis());
		}

		private void writeEnum(Enum e) throws IOException {
			// + acts as a minus since Byte.MIN_VALUE is -128
			dataWriter.writeByte((byte) (e.ordinal() + Byte.MIN_VALUE));
		}

		private void writeHeader(Metadata metadata) throws IOException {
			headerWriter.writeShort(metadata.getId());
			headerWriter.writeShort(dataByteArrayLength);
			headerByteArrayLength += BYTES_TO_WRITE_METADATA_ID_AND_INDEX + BYTES_TO_WRITE_METADATA_ID_AND_INDEX; // 2 bytes (short) * each header write (2)
		}

		public byte[] build() throws IOException {
			// index 0 & 1 are placeholders (short) for the number of data (metadatasSize/metadatasCount) in the final array
			byte[] data = new byte[BYTES_TO_WRITE_METADATA_VALUES_SIZE + headerByteArrayLength + dataByteArrayLength];
			System.arraycopy(headerOutput.toByteArray(), 0, data, BYTES_TO_WRITE_METADATA_VALUES_SIZE, headerByteArrayLength);
			System.arraycopy(dataOutput.toByteArray(), 0, data, headerByteArrayLength + BYTES_TO_WRITE_METADATA_VALUES_SIZE, dataByteArrayLength);

			closeStreams();
			writeMetadatasSizeToHeader(data);

			return data;
		}

		private void writeMetadatasSizeToHeader(byte[] data) {
			data[0] = (byte) ((metadatasSize >> 8) & 0xff);
			data[1] = (byte) (metadatasSize & 0xff);
		}

		private void closeStreams() throws IOException {
			this.headerOutput.close();
			this.dataOutput.close();
			this.headerWriter.close();
			this.dataWriter.close();
		}
	}
}
