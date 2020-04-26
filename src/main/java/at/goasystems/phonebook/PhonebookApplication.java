package at.goasystems.phonebook;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import at.goasystems.phonebook.control.LdapConnection;
import at.goasystems.phonebook.model.Backend;
import at.goasystems.phonebook.model.User;

@SpringBootApplication
public class PhonebookApplication {

	private static Logger logger = LoggerFactory.getLogger(PhonebookApplication.class);

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

	public static void main(String[] args) {
		SpringApplication.run(PhonebookApplication.class, args);
	}

	@PostConstruct
	private void init() {
		LdapConnection lc = new LdapConnection(ldapurl, ldapdomain, ldapbasedn, ldapusername, ldappassword, null);
		List<User> users = lc.getUsers("(&(objectClass=user)(memberOf=" + usergroup + "))");
		Backend.getInstance().setUsers(users);
		logger.debug("Users loaded.");
	}
}
