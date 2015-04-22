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
package com.constellio.app.modules.rm.ui.entities;

import java.io.Serializable;

import org.joda.time.LocalDate;

import com.constellio.app.modules.rm.model.enums.FolderMediaType;

public class FolderDetailVO implements Serializable {
	private String folderId;
	private boolean folderIncluded;
	private String validationUserId;
	private LocalDate validationDate;
	private String containerRecordId;
	private FolderMediaType mediumType;
	private String retentionRuleId;
	private String categoryCode;
	private boolean packageable;
	private boolean sortable;
	private boolean reversedSort;
	private boolean selected;

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public boolean isFolderIncluded() {
		return folderIncluded;
	}

	public void setFolderIncluded(boolean folderIncluded) {
		this.folderIncluded = folderIncluded;
	}

	public String getValidationUserId() {
		return validationUserId;
	}

	public void setValidationUserId(String validationUserId) {
		this.validationUserId = validationUserId;
	}

	public LocalDate getValidationDate() {
		return validationDate;
	}

	public void setValidationDate(LocalDate validationDate) {
		this.validationDate = validationDate;
	}

	public FolderMediaType getMediumType() {
		return mediumType;
	}

	public void setMediumType(FolderMediaType mediumType) {
		this.mediumType = mediumType;
	}

	public String getRetentionRuleId() {
		return retentionRuleId;
	}

	public void setRetentionRuleId(String retentionRuleId) {
		this.retentionRuleId = retentionRuleId;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getContainerRecordId() {
		return containerRecordId;
	}

	public void setContainerRecordId(String containerRecordId) {
		this.containerRecordId = containerRecordId;
	}

	public boolean isPackageable() {
		return packageable;
	}

	public void setPackageable(boolean packageable) {
		this.packageable = packageable;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isReversedSort() {
		return reversedSort;
	}

	public void setReversedSort(boolean reversedSort) {
		this.reversedSort = reversedSort;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
