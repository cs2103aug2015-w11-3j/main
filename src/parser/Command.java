package parser;

import java.util.Date;
import common.Task;

public class Command implements CommandInterface {

	public static enum Type {
		Add, Delete, Update, 
		Sort, ShowAll,
		//Search, FilterByTags, 
		//Tag,
		Undo,Redo,
		Mark, Unmark,
		Quit, 
		Invalid
	}

	// Immutable, every command has these
	private final String _userInput;
	private final Type _cmdType;
	
	// identifiers
	private int _taskUID;
	private Task.DataType _taskField;
	
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
	void setTaskField (Task.DataType f) {
		_taskField = f;
	}
	
	void setName (String name) {
		_name = name;
	}
	void setStart (Date d) {
		_startDate = d == null ? null : (Date) d.clone();
	}
	void setEnd (Date d) {
		_endDate = d == null ? null : (Date) d.clone();
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
	public int getTaskUID () {
		return _taskUID;
	}
	@Override
	public Task.DataType getTaskField () {
		return _taskField;
	}
	
	// Field values

	@Override
	public String getName () {
		return _name;
	}
	@Override
	public Date getStart () {
		return _startDate == null ? null : (Date)_startDate.clone();
	}
	@Override
	public Date getEnd () {
		return _endDate == null ? null : (Date)_endDate.clone();
	}

}
