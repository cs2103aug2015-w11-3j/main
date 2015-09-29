package logic;

public class IntegrityCommandException extends Exception {
	public String cMsg;
	
	public IntegrityCommandException(String msg){
		cMsg = msg;
	}
}
