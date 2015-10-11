package logic.exceptions;

public class LogicException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8103789619642292889L;
	public final String cMsg;

	public LogicException(String msg){
		cMsg = msg;
	}
}
