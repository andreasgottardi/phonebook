package at.goasystems.phonebook.model;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Backend {

	private static Logger logger = LoggerFactory.getLogger(Backend.class);
	private static Backend mThis = null;
	private List<User> users;

	private Backend() {
	}

	public static Backend getInstance() {
		if (mThis == null) {
			logger.debug("Backend initializing.");
			mThis = new Backend();
		}
		return mThis;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
