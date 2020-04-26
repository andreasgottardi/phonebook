package at.goasystems.phonebook;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import at.goasystems.phonebook.control.LdapConnection;
import at.goasystems.phonebook.model.User;

@SpringBootTest
class TestLdap {

	private static Logger logger = LoggerFactory.getLogger(TestLdap.class);

	@Value("${ldap.url}")
	private String ldapurl;

	@Value("${ldap.domain}")
	private String ldapdomain;

	@Value("${ldap.basedn}")
	private String ldapbasedn;

	@Value("${ldap.username}")
	private String ldapusername;

	@Value("${ldap.password}")
	private String ldappassword;

	@Value("${ldap.usergroup}")
	private String usergroup;

	@Test
	void testUsers() {
		logger.debug("Ldap test initialized.");
		LdapConnection lc = new LdapConnection(ldapurl, ldapdomain, ldapbasedn, ldapusername, ldappassword, null);
		List<User> users = lc.getUsers("(&(objectClass=user)(memberOf=" + usergroup + "))");
		assertNotNull(users);
		assertTrue(users.size() > 0);
		logger.debug("{}", users.get(0).getAvatar());
	}

	@Test
	void testCompanies() {
		logger.debug("Ldap test initialized.");
		LdapConnection lc = new LdapConnection(ldapurl, ldapdomain, ldapbasedn, ldapusername, ldappassword, null);
		List<User> users = lc.getUsers("(&(objectClass=user)(memberOf=" + usergroup + "))");
		Map<String, Integer> companies = lc.getCompanies(users);
		assertNotNull(companies);
		assertTrue(companies.keySet().size() > 0);
		logger.debug("{}", users.get(0).getAvatar());
	}
}
