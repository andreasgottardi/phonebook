package at.goasystems.phonebook.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.goasystems.phonebook.model.User;

/**
 * @author ago
 */
public class LdapConnection {

	private static Logger logger = LoggerFactory.getLogger(LdapConnection.class);
	private static final String ERROR_MESSAGE = "Error";

	private InitialDirContext context;
	private String basedn;

	/**
	 * 
	 * @param url                  URL to ldap server (ldap|ldaps)://server:[port]
	 * @param domain               Domain in form dom.example.com
	 * @param basedn               Base distinguished name in form
	 *                             "dc=dom,dc=example,dc=com"
	 * @param username             Regular sAMAccountName
	 * @param password             Password in plain text
	 * @param additionalProperties For additional parameters.
	 */
	public LdapConnection(String url, String domain, String basedn, String username, String password,
			Map<String, String> additionalProperties) {

		this.basedn = basedn;

		Properties prop = new Properties();

		logger.debug("Connecting to {}", url);

		prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		prop.put(Context.PROVIDER_URL, url);
		prop.put(Context.SECURITY_AUTHENTICATION, "simple");
		prop.put(Context.SECURITY_PRINCIPAL, username + "@" + domain);
		prop.put(Context.SECURITY_CREDENTIALS, password);
		prop.put(Context.REFERRAL, "follow");

		if (additionalProperties != null) {
			prop.putAll(additionalProperties);
		}

		try {
			context = new InitialDirContext(prop);
		} catch (NamingException ex) {
			logger.error("Error creating initial context.", ex);
		}
	}

	public List<User> getUsers(String group) {
		NamingEnumeration<SearchResult> results = search(group);
		List<User> users = new ArrayList<>();
		try {
			while (results.hasMoreElements()) {
				User u = new User();
				SearchResult sr = results.nextElement();
				Attributes as = sr.getAttributes();
				String avatar = getAvatar(as.get("jpegPhoto"));
				u.setAvatar(avatar);
				u.setsAMAccountName(getValue(as.get("sAMAccountName")));
				u.setGivenName(getValue(as.get("givenName")));
				u.setSn(getValue(as.get("sn")));
				u.setTelephoneNumber(getValue(as.get("telephoneNumber")));
				u.setIpPhone(getValue(as.get("ipPhone")));
				u.setMobile(getValue(as.get("mobile")));
				u.setMail(getValue(as.get("mail")));
				u.setCompany(getValue(as.get("company")));
				u.setDepartment(getValue(as.get("department")));
				u.setTitle(getValue(as.get("title")));

				users.add(u);
			}
		} catch (NamingException e) {
			logger.error("Error loading users.", e);
		}
		return users;
	}

	/**
	 * Returns a map with all companies the users belong to and the number of users
	 * per company.
	 * 
	 * @param List of users.
	 * @return A map with all companies and number of users.
	 */
	public Map<String, Integer> getCompanies(List<User> users) {
		Map<String, Integer> companies = new HashMap<>();
		for (User user : users) {
			String company = user.getCompany();
			if (companies.containsKey(company)) {
				Integer number = companies.get(company);
				number = number + 1;
				companies.put(company, number);
			} else {
				companies.put(company, 1);
			}
		}
		return companies;
	}

	public String getAvatar(Attribute jpegphoto) throws NamingException {
		if (jpegphoto != null) {
			byte[] bytes = jpegphoto.get() == null ? null : convertObjToByteArray(jpegphoto.get());
			byte[] binarydata = bytes == null ? null : extractJpegPhoto(bytes);
			return binarydata == null ? "" : Base64.encodeBase64String(binarydata);
		} else {
			return " data:image/jpeg;base64,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";
		}
	}

	public NamingEnumeration<SearchResult> search(String searchFilter) {

		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> searchResult = null;
		try {
			searchResult = context.search(this.basedn, searchFilter, searchControls);
		} catch (NamingException ex) {
			logger.error("Error.", ex);
		}
		return searchResult;
	}

	private String getValue(Attribute attribute) throws NamingException {
		if (attribute == null || attribute.get() == null || attribute.get().toString() == null) {
			return "";
		}
		return attribute.get().toString();
	}

	private byte[] convertObjToByteArray(Object object) {
		byte[] objasbytes = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);) {
			oos.writeObject(object);
			oos.flush();
			objasbytes = baos.toByteArray();
		} catch (IOException e) {
			logger.error(ERROR_MESSAGE, e);
		}
		return objasbytes;
	}

	private byte[] extractJpegPhoto(byte[] ldapobject) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToolBox tb = new ToolBox();
		int status = 0;
		for (int i = 0; i < ldapobject.length; i++) {
			if (status == 0 && ldapobject[i] == tb.intToByte(0xFF) && ldapobject[i + 1] == tb.intToByte(0xD8)) {
				logger.debug("Start of image found at position {}", i);
				status = 1;
				baos.write(ldapobject[i]);
			} else if (status == 1) {
				baos.write(ldapobject[i]);
				status = 2;
			} else if (status == 2) {
				baos.write(ldapobject[i]);
			}
		}
		return baos.toByteArray();
	}
}