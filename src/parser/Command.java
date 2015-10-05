package parser;

import java.util.Date;
import common.Celebi;

public class Command implements CommandInterface {

	public static enum Type {
		Add,
		Delete, Update, 
		//Sort, Search, FilterByTags, 
		//Tag,
		Quit, 
		Invalid
	}

	// Immutable, every command has these
	private final String _userInput;
	private final Type _cmdType;
	
	// identifiers
	private int _taskUID;
	private Celebi.DataType _taskField;
	
	// field values
	private String _name;
	private Date _startDate, _endDate;

	// Constructor (package private)
	Command (Command.Type cmd, String userInput) {
		_cmdType = cmd;
		_userInput = userInput;
		_taskUID = -1;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// SETTERS / INITIALISERS (package private)
	////////////////////////////////////////////////////////////////////////////////////
	
	void setTaskUID (int uid) {
		_taskUID = uid;
	}
	void setTaskField (Celebi.DataType f) {
		_taskField = f;
	}
	
	void setName (String name) {
		_name = name;
	}
	void setStart (Date d) {
		_startDate = d;
	}
	void setEnd (Date d) {
		_endDate = d;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////

	// INVALID INPUT
	public String getRawUserInput () {
		return _userInput;
	}
	
	// Identifiers
	
	public Command.Type getCmdType () {
		return _cmdType;
	}
	public int getCelebiUID () {
		return _taskUID;
	}
	public Celebi.DataType getCelebiField () {
		return _taskField;
	}
	
	// Field values
	
	public String getName () {
		return _name;
	}
	public Date getStart () {
		return (Date)_startDate.clone();
	}
	public Date getEnd () {
		return (Date)_endDate.clone();
	}

}
