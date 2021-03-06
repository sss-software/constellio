package com.constellio.model.services.search;

import com.constellio.model.services.search.QueryElevation.DocElevation;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Elevations implements Serializable {
	private List<QueryElevation> queryElevations = new ArrayList<>();
	private List<String> docExclusions = new ArrayList<>();

	public void addOrUpdate(QueryElevation queryElevation) {
		boolean updated = false;
		for (QueryElevation currentQueryElevation : this.queryElevations) {
			if (Objects.equals(currentQueryElevation.getQuery(), queryElevation.getQuery())) {
				currentQueryElevation.addUpdate(queryElevation.getDocElevations());
				updated = true;
				break;
			}
		}

		if (!updated) {
			this.queryElevations.add(queryElevation);
		}
	}

	public boolean removeQueryElevation(String query) {
		for (Iterator iterator = this.queryElevations.iterator(); iterator.hasNext(); ) {
			QueryElevation queryElevation = (QueryElevation) iterator.next();
			if (Objects.equals(queryElevation.getQuery(), query)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	public void removeAllElevation() {
		this.queryElevations.clear();
	}

	public boolean addDocExclusion(String recordId) {
		if (!docExclusions.contains(recordId)) {
			docExclusions.add(recordId);
			return true;
		}
		return false;
	}

	public boolean removeDocExclusion(String recordId) {
		return docExclusions.remove(recordId);
	}

	public void removeAllDocExclusion() {
		docExclusions.clear();
	}

	public boolean removeDocElevation(String query, String recordId) {
		boolean removeQuery = false;
		boolean found = false;
		for (Iterator<QueryElevation> iterator = this.queryElevations.iterator(); iterator.hasNext(); ) {
			QueryElevation queryElevation = iterator.next();
			if (queryElevation.getQuery().equals(query)) {
				for (Iterator<DocElevation> iteratorDecElevation = queryElevation.getDocElevations().iterator(); iteratorDecElevation.hasNext(); ) {
					DocElevation docElevation = iteratorDecElevation.next();
					if (docElevation.getId().equals(recordId)) {
						iteratorDecElevation.remove();
						found = true;
						if (queryElevation.getDocElevations().size() <= 0) {
							removeQuery = true;
						}
						break;
					}
				}
			}
			if (found) {
				if (removeQuery) {
					iterator.remove();
				}
				break;
			}
		}
		return found;
	}

	public List<QueryElevation> getQueryElevations() {
		return queryElevations;
	}

	public List<String> getDocExclusions() {
		return docExclusions;
	}

	public QueryElevation getQueryElevation(String query) {
		for (QueryElevation queryElevation : queryElevations) {
			if (queryElevation.getQuery().equals(query)) {
				return queryElevation;
			}
		}
		return null;
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}


}
