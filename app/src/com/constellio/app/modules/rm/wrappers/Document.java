package com.constellio.app.modules.rm.wrappers;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.constellio.app.modules.rm.model.CopyRetentionRule;
import com.constellio.app.modules.rm.model.CopyRetentionRuleInRule;
import com.constellio.app.modules.rm.model.enums.FolderStatus;
import com.constellio.app.modules.rm.wrappers.structures.Comment;
import com.constellio.app.modules.rm.wrappers.type.DocumentType;
import com.constellio.model.entities.records.Content;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Metadata;
import com.constellio.model.entities.schemas.MetadataSchemaTypes;

public class Document extends RMObject {
	public static final String SCHEMA_TYPE = "document";
	public static final String DEFAULT_SCHEMA = SCHEMA_TYPE + "_default";
	public static final String FOLDER = "folder";
	public static final String KEYWORDS = "keywords";
	public static final String DESCRIPTION = "description";
	public static final String CONTENT = "content";
	public static final String TYPE = "type";
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String COMMENTS = "comments";
	public static final String FOLDER_BORROWED = Folder.BORROWED;
	public static final String FOLDER_CATEGORY = Folder.CATEGORY;
	public static final String FOLDER_ADMINISTRATIVE_UNIT = Folder.ADMINISTRATIVE_UNIT;
	public static final String FOLDER_FILING_SPACE = Folder.FILING_SPACE;
	public static final String FOLDER_RETENTION_RULE = Folder.RETENTION_RULE;
	public static final String FOLDER_ARCHIVISTIC_STATUS = Folder.ARCHIVISTIC_STATUS;
	public static final String FOLDER_ACTUAL_DEPOSIT_DATE = Folder.ACTUAL_DEPOSIT_DATE;
	public static final String FOLDER_ACTUAL_DESTRUCTION_DATE = Folder.ACTUAL_DESTRUCTION_DATE;
	public static final String FOLDER_ACTUAL_TRANSFER_DATE = Folder.ACTUAL_TRANSFER_DATE;
	public static final String FOLDER_EXPECTED_DEPOSIT_DATE = Folder.EXPECTED_DEPOSIT_DATE;
	public static final String FOLDER_EXPECTED_DESTRUCTION_DATE = Folder.EXPECTED_DESTRUCTION_DATE;
	public static final String FOLDER_EXPECTED_TRANSFER_DATE = Folder.EXPECTED_TRANSFER_DATE;
	public static final String FOLDER_OPENING_DATE = Folder.OPENING_DATE;
	public static final String FOLDER_CLOSING_DATE = Folder.CLOSING_DATE;
	public static final String ACTUAL_TRANSFER_DATE_ENTERED = "actualTransferDateEntered";
	public static final String ACTUAL_DEPOSIT_DATE_ENTERED = "actualDepositDateEntered";
	public static final String ACTUAL_DESTRUCTION_DATE_ENTERED = "actualDestructionDateEntered";
	public static final String INHERITED_FOLDER_RETENTION_RULE = "inheritedRetentionRule";
	public static final String COPY_STATUS = Folder.COPY_STATUS;
	public static final String AUTHOR = "author";
	public static final String COMPANY = "company";
	public static final String SUBJECT = "subject";
	public static final String ALERT_USERS_WHEN_AVAILABLE = "alertUsersWhenAvailable";
	public static final String MAIN_COPY_RULE = "mainCopyRule";
	public static final String MAIN_COPY_RULE_ID_ENTERED = "mainCopyRuleIdEntered";
	public static final String APPLICABLE_COPY_RULES = "applicableCopyRule";
	public static final String SAME_SEMI_ACTIVE_FATE_AS_FOLDER = "sameSemiActiveFateAsFolder";
	public static final String SAME_INACTIVE_FATE_AS_FOLDER = "sameInactiveFateAsFolder";
	public static final String PUBLISHED = "published";
	public static final String CREATED_BY_ROBOT = "createdByRobot";
	public static final String CALENDAR_YEAR_ENTERED = Folder.CALENDAR_YEAR_ENTERED;
	public static final String CALENDAR_YEAR = Folder.CALENDAR_YEAR;

	public Document(Record record,
			MetadataSchemaTypes types) {
		super(record, types, SCHEMA_TYPE);
	}

	public Document setTitle(String title) {
		super.setTitle(title);
		return this;
	}

	public String getFolder() {
		return get(FOLDER);
	}

	public Document setFolder(String folder) {
		set(FOLDER, folder);
		return this;
	}

	public Document setFolder(Folder folder) {
		set(FOLDER, folder);
		return this;
	}

	public String getDescription() {
		return get(DESCRIPTION);
	}

	public Document setDescription(String description) {
		set(DESCRIPTION, description);
		return this;
	}

	public String getFolderAdministrativeUnit() {
		return get(FOLDER_ADMINISTRATIVE_UNIT);
	}

	public String getFolderCategory() {
		return get(FOLDER_CATEGORY);
	}

	public List<String> getKeywords() {
		return getList(KEYWORDS);
	}

	public Document setKeywords(List<String> keywords) {
		set(KEYWORDS, keywords);
		return this;
	}

	public Content getContent() {
		return get(CONTENT);
	}

	public Document setContent(Content content) {
		set(CONTENT, content);
		return this;
	}

	public String getType() {
		return get(TYPE);
	}

	public Document setType(DocumentType type) {
		set(TYPE, type);
		return this;
	}

	public Document setType(Record type) {
		set(TYPE, type);
		return this;
	}

	public Document setType(String type) {
		set(TYPE, type);
		return this;
	}

	public List<Comment> getComments() {
		return getList(COMMENTS);
	}

	public Document setComments(List<Comment> comments) {
		set(COMMENTS, comments);
		return this;
	}

	public Boolean getBorrowed() {
		return get(FOLDER_BORROWED);
	}

	public Document setBorrowed(Boolean borrowed) {
		set(FOLDER_BORROWED, borrowed);
		return this;
	}

	public String getSubject() {
		return get(SUBJECT);
	}

	public Document setSubject(String subject) {
		set(SUBJECT, subject);
		return this;
	}

	public String getCompany() {
		return get(COMPANY);
	}

	public Document setCompany(String company) {
		set(COMPANY, company);
		return this;
	}

	public String getAuthor() {
		return get(AUTHOR);
	}

	public Document setAuthor(String author) {
		set(AUTHOR, author);
		return this;
	}

	public List<String> getAlertUsersWhenAvailable() {
		return getList(ALERT_USERS_WHEN_AVAILABLE);
	}

	public Document setAlertUsersWhenAvailable(List<String> users) {
		set(ALERT_USERS_WHEN_AVAILABLE, users);
		return this;
	}

	public Document setFormCreatedBy(String userId) {
		set(FORM_CREATED_BY, userId);
		return this;
	}

	public Document setFormCreatedBy(Record user) {
		set(FORM_CREATED_BY, user);
		return this;
	}

	public Document setFormCreatedBy(User user) {
		set(FORM_CREATED_BY, user);
		return this;
	}

	public Document setFormCreatedOn(LocalDateTime dateTime) {
		set(FORM_CREATED_ON, dateTime);
		return this;
	}

	public Document setFormModifiedBy(String userId) {
		set(FORM_MODIFIED_BY, userId);
		return this;
	}

	public Document setFormModifiedBy(Record user) {
		set(FORM_MODIFIED_BY, user);
		return this;
	}

	public Document setFormModifiedBy(User user) {
		set(FORM_MODIFIED_BY, user);
		return this;
	}

	public Document setFormModifiedOn(LocalDateTime dateTime) {
		set(FORM_MODIFIED_ON, dateTime);
		return this;
	}

	public List<CopyRetentionRuleInRule> getApplicableCopyRules() {
		return getList(APPLICABLE_COPY_RULES);
	}

	public CopyRetentionRule getMainCopyRule() {
		return get(MAIN_COPY_RULE);
	}

	public String getMainCopyRuleIdEntered() {
		return get(MAIN_COPY_RULE_ID_ENTERED);
	}

	public Document setMainCopyRuleIdEntered(String mainCopyRuleIdEntered) {
		set(MAIN_COPY_RULE_ID_ENTERED, mainCopyRuleIdEntered);
		return this;
	}

	@Override
	public FolderStatus getArchivisticStatus() {
		return get(FOLDER_ARCHIVISTIC_STATUS);
	}

	public String getRetentionRule() {
		return get(FOLDER_RETENTION_RULE);
	}

	public LocalDate getFolderActualDepositDate() {
		return get(FOLDER_ACTUAL_DEPOSIT_DATE);
	}

	public LocalDate getFolderActualDestructionDate() {
		return get(FOLDER_ACTUAL_DESTRUCTION_DATE);
	}

	public LocalDate getFolderActualTransferDate() {
		return get(FOLDER_ACTUAL_TRANSFER_DATE);
	}

	public LocalDate getFolderExpectedDepositDate() {
		return get(FOLDER_EXPECTED_DEPOSIT_DATE);
	}

	public LocalDate getFolderExpectedDestructionDate() {
		return get(FOLDER_EXPECTED_DESTRUCTION_DATE);
	}

	public LocalDate getFolderExpectedTransferDate() {
		return get(FOLDER_EXPECTED_TRANSFER_DATE);
	}

	public LocalDate getActualTransferDateEntered() {
		return get(ACTUAL_TRANSFER_DATE_ENTERED);
	}

	public Document setActualTransferDateEntered(LocalDate transferDate) {
		set(ACTUAL_TRANSFER_DATE_ENTERED, transferDate);
		return this;
	}

	public LocalDate getActualDepositDateEntered() {
		return get(ACTUAL_DEPOSIT_DATE_ENTERED);
	}

	public Document setActualDepositDateEntered(LocalDate depositDate) {
		set(ACTUAL_DEPOSIT_DATE_ENTERED, depositDate);
		return this;
	}

	public LocalDate getActualDestructionDateEntered() {
		return get(ACTUAL_DESTRUCTION_DATE_ENTERED);
	}

	public Document setActualDestructionDateEntered(LocalDate destructionDate) {
		set(ACTUAL_DESTRUCTION_DATE_ENTERED, destructionDate);
		return this;
	}

	public boolean isSemiActiveSameFateAsFolder() {
		return get(SAME_SEMI_ACTIVE_FATE_AS_FOLDER);
	}

	public boolean isInactiveSameFateAsFolder() {
		return get(SAME_INACTIVE_FATE_AS_FOLDER);
	}

	public Document setPublished(boolean published) {
		set(PUBLISHED, published);
		return this;
	}

	public boolean isPublished() {
		return BooleanUtils.isTrue((Boolean) get(PUBLISHED));
	}

	public <T> Document set(Metadata metadata, T value) {
		set(metadata.getLocalCode(), value);
		return this;
	}

}
