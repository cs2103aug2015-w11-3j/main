//@@author A0131891E
package parser;

import java.nio.file.Path;
import java.util.Date;
import common.Task;
import common.TasksBag;
import ui.view.CelebiViewController;

public class Command implements CommandInterface {

	public static enum Type {
		ADD, DELETE, UPDATE, 
		SHOW,
		SEARCH, FILTER_DATE, CLEAR_FILTERS,
		UNDO, REDO,
		MARK, UNMARK,
		QUIT, 
		INVALID,
		MOVE, HELP, 
		THEME
	}

	
	// Immutable, every command has these
	private final String _userInput;
	private final Type _cmdType;
	
	// identifiers
	private int _taskUID;
	private Task.DataType _taskField;
	private Type _helpCmdType;
	private TasksBag.ViewType _showType;
	
	// field values
	private String _name;
	private Date _startDate, _endDate;
	private Path _path;
//	private String _keyword;

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
	
	void setText (String name) {
		_name = name;
	}
	void setStart (Date d) {
		_startDate = d == null ? null : (Date) d.clone();
	}
	void setEnd (Date d) {
		_endDate = d == null ? null : (Date) d.clone();
	}
	void setHelpCmdType (Type t) {
		assert(t != Type.INVALID);
		_helpCmdType = t;
	}
	void setPath (Path p) {
		_path = p;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////

	// Full user input that triggered this command
	@Override
	public String getRawUserInput () {
		return _userInput;
	}
	
	// Identifiers

	@Override
	public Type getHelpCmdType() {
		return _helpCmdType;
	}
	@Override
	public Type getCmdType () {
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
	public String getText () {
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
	@Override
	public Path getPath() {
		return _path;
	}
	public TasksBag.ViewType getViewType () {
		return _showType;
	}

//	@Override
//	public String getSearchKeyword() {
//		return _keyword;
//	}

}
