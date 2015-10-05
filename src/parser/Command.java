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

	////////////////////////////////////////////////////////////////////////////////////	
	// Constructor (package private)
	////////////////////////////////////////////////////////////////////////////////////
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
	@Override
	public String getRawUserInput () {
		return _userInput;
	}
	
	// Identifiers

	@Override
	public Command.Type getCmdType () {
		return _cmdType;
	}
	@Override
	public int getCelebiUID () {
		return _taskUID;
	}
	@Override
	public Celebi.DataType getCelebiField () {
		return _taskField;
	}
	
	// Field values

	@Override
	public String getName () {
		return _name;
	}
	@Override
	public Date getStart () {
		return (Date)_startDate.clone();
	}
	@Override
	public Date getEnd () {
		return (Date)_endDate.clone();
	}

}
