package com.constellio.model.conf;

import static java.util.Arrays.asList;

import java.util.List;

import org.joda.time.Duration;

import com.constellio.model.conf.ldap.LDAPDirectoryType;
import com.constellio.model.conf.ldap.LDAPServerConfiguration;
import com.constellio.model.conf.ldap.LDAPUserSyncConfiguration;
import com.constellio.model.conf.ldap.RegexFilter;
import com.constellio.sdk.SDKPasswords;

public class LDAPTestConfig {

	public static List<String> getUrls() {
		return asList(new String[] { getLDAPDevServerUrl() });
	}

	public static String getLDAPDevServerUrl() {
		return SDKPasswords.testLDAPServer();
	}


	public static List<String> getExchangeLDAPDevServerUrl() {
		return asList(SDKPasswords.testExchangeLDAPServer());
	}


	public static List<String> getDomains() {
		return asList(new String[] { "test" });
	}

	public static LDAPDirectoryType getDirectoryType() {
		return LDAPDirectoryType.ACTIVE_DIRECTORY;
	}

	public static List<String> getGroupBaseContextList() {
		return asList("OU=Groupes,DC=test,DC=doculibre,DC=ca");
	}

	public static String getPassword() {
		return SDKPasswords.testLDAPPassword();
	}

	public static String getUser() {
		return "administrator";
	}

	public static List<String> getUsersWithoutGroupsBaseContextList() {
		return asList("CN=Users,DC=test,DC=doculibre,DC=ca");
	}

	public static RegexFilter getUserFiler() {
		return new RegexFilter(".*", "testAuj");
	}

	public static RegexFilter getGroupFiler() {
		return new RegexFilter(".*", "GGS\\-SEC\\-ALF_tous|GGS\\-SEC\\-ALF_tous_centres_SCEC|.*_ext");
	}

	public static LDAPServerConfiguration getLDAPServerConfiguration() {
		return new LDAPServerConfiguration(getUrls(), getDomains(), getDirectoryType(), true, false);
	}

	public static LDAPServerConfiguration getSharepointLDAPServerConfiguration() {
		return new LDAPServerConfiguration(getUrls(), getDomains(), getDirectoryType(), true, true);
	}

	public static LDAPServerConfiguration getLDAPServerConfigurationInactive() {
		return new LDAPServerConfiguration(getUrls(), getDomains(), getDirectoryType(), false, false);
	}

	public static LDAPUserSyncConfiguration getLDAPUserSyncConfigurationWithSelectedCollections(
			List<String> selectedCollectionsCodes) {
		return new LDAPUserSyncConfiguration(getUser(), getPassword(), getUserFiler(), getGroupFiler(), null,
				getGroupBaseContextList(),
				getUsersWithoutGroupsBaseContextList(), selectedCollectionsCodes);
	}

	public static LDAPUserSyncConfiguration getLDAPUserSyncConfiguration() {
		return getLDAPUserSyncConfiguration((Duration) null);
	}

	public static LDAPUserSyncConfiguration getLDAPUserSyncConfiguration(Duration duration) {
		return new LDAPUserSyncConfiguration(getUser(), getPassword(), getUserFiler(), getGroupFiler(), duration,
				getGroupBaseContextList(), getUsersWithoutGroupsBaseContextList());
	}

	public static LDAPUserSyncConfiguration getLDAPUserSyncConfiguration(String password) {
		return new LDAPUserSyncConfiguration(getUser(), password, getUserFiler(), getGroupFiler(), null,
				getGroupBaseContextList(), getUsersWithoutGroupsBaseContextList());
	}


	public static LDAPServerConfiguration getExchangeLDAPServerConfiguration() {
		return new LDAPServerConfiguration(getExchangeLDAPDevServerUrl(), getDomains(), getDirectoryType(), true, false);
	}

	public static LDAPUserSyncConfiguration getExchangeLDAPUserSyncConfiguration() {
		return new LDAPUserSyncConfiguration(getUser(), SDKPasswords.testExchangeLDAPPassword(), new RegexFilter("indexer.*", null), getGroupFiler(), null,
				getGroupBaseContextList(),
				getUsersWithoutGroupsBaseContextList(), asList("zeCollection"));
	}

}
