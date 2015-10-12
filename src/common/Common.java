package common;

import java.util.Date;

import logic.exceptions.IntegrityCommandException;

public class Common {

	static Common instance;

	private String usrFileDirectory; // User data file directory
	private final String configDirectory = "usr.conf"; // Config to store global
														// settings

	public static Common getInstance() {
		if (instance == null) {
			instance = new Common();
		}
		return instance;
	}

	public Common() {

	}

	public String getConfigDirectory() {
		return configDirectory;
	}

	public String getUsrFileDirectory() {
		if (usrFileDirectory == null) {
		}
		return usrFileDirectory;
	}

	/*
	 * Ensures that end date must be after start date
	 */
	public static boolean verifyDate(Date dateStart, Date dateEnd) throws IntegrityCommandException {

		if (dateStart != null && dateEnd != null) {
			if (dateStart.after(dateEnd)) {
				throw new IntegrityCommandException("End date is earlier than start date!");
			}
		}
		return true;
	}
}
