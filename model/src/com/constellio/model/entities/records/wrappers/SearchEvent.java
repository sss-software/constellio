package com.constellio.model.entities.records.wrappers;

import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;

import java.util.Arrays;
import java.util.List;

public class SearchEvent extends RecordWrapper {

	public static final String SCHEMA_TYPE = "searchEvent";
	public static final String DEFAULT_SCHEMA = SCHEMA_TYPE + "_default";
	public static final String USERNAME = "username";
	public static final String QUERY = "query";
	public static final String CLICK_COUNT = "clickCount";
	public static final String PAGE_NAVIGATION_COUNT = "pageNavigationCount";
	public static final String LAST_PAGE_NAVIGATION = "lastPageNavigation";
	public static final String PARAMS = "params";
	public static final String ORIGINAL_QUERY = "originalQuery";
	public static final String NUM_FOUND = "numFound";
	public static final String Q_TIME = "qTime";
	public static final String CAPSULE = "capsule";
	public static final String DWELL_TIME = "dwellTime";
	public static final String CLICKS = "clicks";

	public SearchEvent(Record record, MetadataSchemaTypes types) {
		super(record, types, SCHEMA_TYPE + "_");
	}

	public String getUsername() {
		return get(USERNAME);
	}

	public SearchEvent setUsername(String username) {
		set(USERNAME, username);
		return this;
	}

	public String getQuery() {
		return get(QUERY);
	}

	public SearchEvent setQuery(String query) {
		set(QUERY, query);
		return this;
	}

	public String getOriginalQuery() {
		return get(ORIGINAL_QUERY);
	}

	public SearchEvent setOriginalQuery(String query) {
		set(ORIGINAL_QUERY, query);
		return this;
	}

	public SearchEvent setParams(List<String> listParams) {
		set(PARAMS, listParams);
		return this;
	}

	public List<String> getParams() {
		return get(PARAMS);
	}

	public int getClickCount() {
		return getPrimitiveInteger(CLICK_COUNT);
	}

	public SearchEvent setClickCount(int clickCount) {
		set(CLICK_COUNT, clickCount);
		return this;
	}

	public int getPageNavigationCount() {
		return getPrimitiveInteger(PAGE_NAVIGATION_COUNT);
	}

	public SearchEvent setPageNavigationCount(int pageNavigationCount) {
		set(PAGE_NAVIGATION_COUNT, pageNavigationCount);
		return this;
	}

	public long getNumFound() {
		Number value = get(NUM_FOUND);
		return value == null ? 0 : value.longValue();
	}

	public SearchEvent setNumFound(long numFound) {
		set(NUM_FOUND, numFound);
		return this;
	}

	public long getQTime() {
		Number value = get(Q_TIME);
		return value == null ? 0 : value.longValue();
	}

	public int getLastPageNavigation() {
		Number value = get(LAST_PAGE_NAVIGATION);
		return value == null ? 0 : value.intValue();
	}

	public SearchEvent setQTime(long qTime) {
		set(Q_TIME, qTime);
		return this;
	}

	public long getDwellTime() {
		Number dwellTime = get(DWELL_TIME);
		return dwellTime == null ? 0 : dwellTime.longValue();
	}

	public SearchEvent setDwellTime(long dwellTime) {
		set(DWELL_TIME, dwellTime);
		return this;
	}

	public List<String> getCapsule() {
		return get(CAPSULE);
	}

	public SearchEvent setCapsule(List<String> capsule) {
		set(CAPSULE, capsule);
		return this;
	}

	public SearchEvent setCapsule(Record... capsule) {
		set(CAPSULE, Arrays.asList(capsule));
		return this;
	}

	public SearchEvent setCapsule(Capsule... capsule) {
		set(CAPSULE, Arrays.asList(capsule));
		return this;
	}

	public List<String> getClicks() {
		return get(CLICKS);
	}

	public SearchEvent setClicks(List<String> clicks) {
		set(CLICKS, clicks);
		return this;
	}
}
