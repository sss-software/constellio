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
package com.constellio.sdk.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.MetadataVO;
import com.constellio.app.ui.entities.MetadataValueVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.MetadataValueType;

public class FakeSessionContext implements SessionContext {

	boolean fake;

	UserVO user;
	String collection;
	Locale locale;

	private static FakeSessionContext current;

	public FakeSessionContext(UserVO user, String collection) {
		this.user = user;
		this.collection = collection;
		this.locale = Locale.FRENCH;
		FakeSessionContext.current = this;
		this.fake = true;
	}

	public FakeSessionContext getCurrent() {
		return current;
	}

	public static SessionContext noUserInCollection(String collection) {
		return new FakeSessionContext(null, collection);
	}

	public static SessionContext noUserNoCollection() {
		return new FakeSessionContext(null, null);
	}

	public static SessionContext adminInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "admin", "Admin", "Admin", "admin@doculibre.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext aliceInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "alice", "Alice", "Wonderland", "alice.wonderland@doculibre.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext bobInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "bob", "Bob", "Gratton", "bob.gratton@doculibre.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext chuckNorrisInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "chuck", "Chuck", "Norris", "chuck.norris@doculibre.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext dakotaInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "dakota", "Dakota", "L'Indien", "dakota.indien@gmail.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext edouardInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "edouard", "Édouard", "Lechat", "edouard.lechat@doculibre.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext xavierInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "charles", "Charles-François", "Xavier",
				"charlesfrancois.xavier@doculibre.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext gandalfInCollection(String collection) {
		UserVO userVO = newUserVO(collection, "gandalf", "Gandalf", "Leblanc", "gandalf.leblanc@doculibre.com");
		return new FakeSessionContext(userVO, collection);
	}

	public static SessionContext forRealUserIncollection(User user) {
		List<MetadataValueVO> metadataValueVOs = new ArrayList<>();
		MetadataSchemaVO userSchema = userSchema(user.getCollection());
		metadataValueVOs.add(new MetadataValueVO(userNameMetadata(userSchema), user.getUsername()));
		metadataValueVOs.add(new MetadataValueVO(firstNameMetadata(userSchema), user.getFirstName()));
		metadataValueVOs.add(new MetadataValueVO(lastNameMetadata(userSchema), user.getLastName()));
		metadataValueVOs.add(new MetadataValueVO(emailMetadata(userSchema), user.getEmail()));

		UserVO userVO = new UserVO(user.getId(), metadataValueVOs, VIEW_MODE.DISPLAY);
		FakeSessionContext context = new FakeSessionContext(userVO, user.getCollection());
		context.fake = false;
		return context;
	}

	private static UserVO newUserVO(String collection, String username, String firstName, String lastName, String email) {
		List<MetadataValueVO> metadataValueVOs = new ArrayList<>();
		MetadataSchemaVO userSchema = userSchema(collection);
		metadataValueVOs.add(new MetadataValueVO(userNameMetadata(userSchema), username));
		metadataValueVOs.add(new MetadataValueVO(firstNameMetadata(userSchema), firstName));
		metadataValueVOs.add(new MetadataValueVO(lastNameMetadata(userSchema), lastName));
		metadataValueVOs.add(new MetadataValueVO(emailMetadata(userSchema), email));

		return new UserVO(username + "Id", metadataValueVOs, VIEW_MODE.DISPLAY);
	}

	private static MetadataSchemaVO userSchema(String collection) {
		Map<Locale, String> labels = new HashMap<>();
		labels.put(Locale.FRENCH, "Utilisateur");
		labels.put(Locale.ENGLISH, "User");

		return new MetadataSchemaVO(User.DEFAULT_SCHEMA, collection, labels);
	}

	private static MetadataVO emailMetadata(MetadataSchemaVO userSchema) {
		Map<Locale, String> labels = new HashMap<>();
		labels.put(Locale.FRENCH, "Courriel");
		labels.put(Locale.ENGLISH, "Email");
		String collection = userSchema.getCollection();

		return new MetadataVO(User.EMAIL, MetadataValueType.STRING, collection, userSchema, true, false, false, labels, null,
				null, null, null, null, null);
	}

	private static MetadataVO lastNameMetadata(MetadataSchemaVO userSchema) {
		Map<Locale, String> labels = new HashMap<>();
		labels.put(Locale.FRENCH, "Nom");
		labels.put(Locale.ENGLISH, "Last name");
		String collection = userSchema.getCollection();

		return new MetadataVO(User.LASTNAME, MetadataValueType.STRING, collection, userSchema, true, false, false, labels, null,
				null, null, null, null, null);
	}

	private static MetadataVO firstNameMetadata(MetadataSchemaVO userSchema) {
		Map<Locale, String> labels = new HashMap<>();
		labels.put(Locale.FRENCH, "Prénom");
		labels.put(Locale.ENGLISH, "First name");
		String collection = userSchema.getCollection();

		return new MetadataVO(User.FIRSTNAME, MetadataValueType.STRING, collection, userSchema, true, false, false, labels, null,
				null, null, null, null, null);
	}

	private static MetadataVO userNameMetadata(MetadataSchemaVO userSchema) {
		Map<Locale, String> labels = new HashMap<>();
		labels.put(Locale.FRENCH, "Nom d'utilisateur");
		labels.put(Locale.ENGLISH, "Username");
		String collection = userSchema.getCollection();

		return new MetadataVO(User.USERNAME, MetadataValueType.STRING, collection, userSchema, true, false, false, labels, null,
				null, null, null, null, null);
	}

	@Override
	public UserVO getCurrentUser() {
		return user;
	}

	@Override
	public void setCurrentUser(UserVO user) {
		this.user = user;
	}

	@Override
	public String getCurrentCollection() {
		return collection;
	}

	@Override
	public void setCurrentCollection(String collection) {
		this.collection = collection;
	}

	@Override
	public Locale getCurrentLocale() {
		return locale;
	}

	@Override
	public void setCurrentLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public String getCurrentUserIPAddress() {
		return "127.0.0.1";
	}

}
