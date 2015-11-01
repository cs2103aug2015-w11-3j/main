//@@author A0133920N
package storage;

public class BadFileContentException extends Exception{

	private static final long serialVersionUID = -8645781534663329964L;
	public final String cMsg;
	
	public BadFileContentException (String msg) {
		cMsg = msg;
	}

}
