package com.constellio.model.services.records.cache.cacheIndexConditions;

import com.constellio.data.utils.LazyIterator;
import com.constellio.model.services.records.IntegerRecordId;
import com.constellio.model.services.records.RecordId;
import com.constellio.model.services.records.StringRecordId;
import com.constellio.model.services.records.cache.MetadataIndexCacheDataStore;
import com.constellio.sdk.tests.ConstellioTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.constellio.model.services.records.cache.cacheIndexConditions.FixedIdsStreamer.createFromRecordIds;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class IntersectionSortedIdsStreamerTest extends ConstellioTest {

	@Test
	public void whenCreatingStreamerFromRecordIdsThenStreamedIdsAreSorted() {

		SortedIdsStreamer s1 = createFromRecordIds(ids(1, 2, 3, 5, 7, 8, 9, "42", "45"));
		SortedIdsStreamer s2 = createFromRecordIds(ids(1, 2, 3, 5, 6, 8, "42", "48"));
		SortedIdsStreamer s3 = createFromRecordIds(ids(1, 3, 5, 6, 8, "42"));

		assertThat(streamer(s1, s2, s3).stream(null).collect(toList())).isEqualTo(ids(1, 3, 5, 8, "42"));
		assertThat(streamer(s1, s3, s2).stream(null).collect(toList())).isEqualTo(ids(1, 3, 5, 8, "42"));
		assertThat(streamer(s2, s1, s3).stream(null).collect(toList())).isEqualTo(ids(1, 3, 5, 8, "42"));
		assertThat(streamer(s2, s3, s1).stream(null).collect(toList())).isEqualTo(ids(1, 3, 5, 8, "42"));
		assertThat(streamer(s3, s1, s2).stream(null).collect(toList())).isEqualTo(ids(1, 3, 5, 8, "42"));
		assertThat(streamer(s3, s2, s1).stream(null).collect(toList())).isEqualTo(ids(1, 3, 5, 8, "42"));

		assertThat(streamer(s1, s2).stream(null).collect(toList())).isEqualTo(ids(1, 2, 3, 5, 8, "42"));
		assertThat(streamer(s2, s1).stream(null).collect(toList())).isEqualTo(ids(1, 2, 3, 5, 8, "42"));
		assertThat(streamer(s2, s1, s2, s1, s2, s1).stream(null).collect(toList())).isEqualTo(ids(1, 2, 3, 5, 8, "42"));

		assertThat(streamer(s1).stream(null).collect(toList())).isEqualTo(ids(1, 2, 3, 5, 7, 8, 9, "42", "45"));
		assertThat(streamer(s1, s1).stream(null).collect(toList())).isEqualTo(ids(1, 2, 3, 5, 7, 8, 9, "42", "45"));

	}

	static int counter = 0;

	//@Test
	public void whenStreamingLargeQuantityOfIdsThenOk() {

		SortedIdsStreamer s1 = new SortedIdsStreamer() {

			@Override
			public Iterator<RecordId> iterator(MetadataIndexCacheDataStore dataStore) {
				return new LazyIterator<RecordId>() {

					int value;

					@Override
					protected RecordId getNextOrNull() {
						counter++;
						return value < 50_000_000 ? new IntegerRecordId(value += 5) : null;
					}
				};
			}
		};

		SortedIdsStreamer s2 = new SortedIdsStreamer() {

			@Override
			public Iterator<RecordId> iterator(MetadataIndexCacheDataStore dataStore) {
				return new LazyIterator<RecordId>() {

					int value;

					@Override
					protected RecordId getNextOrNull() {
						counter++;
						return value < 50_000_000 ? new IntegerRecordId(value += 5000) : null;
					}
				};
			}
		};

		SortedIdsStreamer s3 = new SortedIdsStreamer() {

			@Override
			public Iterator<RecordId> iterator(MetadataIndexCacheDataStore dataStore) {
				return new LazyIterator<RecordId>() {

					int value;

					@Override
					protected RecordId getNextOrNull() {
						counter++;
						return value < 50_000_000 ? new IntegerRecordId(value += 2) : null;
					}
				};
			}
		};

		for (int i = 0; i < 100; i++) {
			System.out.println(i);
			assertThat(streamer(s1, s2, s3).hasResults(null)).isTrue();
			System.out.println(counter);
			counter = 0;
		}

	}

	public static IntersectionSortedIdsStreamer streamer(SortedIdsStreamer... streamers) {
		return new IntersectionSortedIdsStreamer(asList(streamers));
	}


	public static List<RecordId> ids(Object... idsOfVariousTypes) {
		List<RecordId> ids = new ArrayList<>();

		for (Object id : idsOfVariousTypes) {
			if (id instanceof String) {
				ids.add(new StringRecordId((String) id));
			} else {
				ids.add(new IntegerRecordId((int) id));
			}
		}

		return ids;
	}
}
