package at.goasystems.phonebook.control;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
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
import at.goasystems.phonebook.tools.ImageTools;

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
			byte[] pictbytes = null;
			try {
				BufferedImage avatar = ImageTools
						.resizeImage(ImageIO.read(LdapConnection.class.getResourceAsStream("/avatar.jpg")));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(avatar, "JPEG", baos);
				pictbytes = baos.toByteArray();
				baos.close();
			} catch (IOException e) {
				logger.error("Error generating avatar.", e);
			}
			return String.format("%s", new String(Base64.encodeBase64(pictbytes)));
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