package logic;

public class IllegalAccessCommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1205053410414852622L;

	public String cMsg;

	public IllegalAccessCommandException(String msg) {
		cMsg = msg;
	}
}