package logic;

/* 
 * ASD
 */
public class IntegrityCommandException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7658067372077628329L;
	public String cMsg;
	
	public IntegrityCommandException(String msg){
		cMsg = msg;
	}
}
