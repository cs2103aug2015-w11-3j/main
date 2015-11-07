//@@author A0131891E
package parser;

import java.nio.file.Path;
import java.util.Date;
import common.Task;
import common.TasksBag;
import ui.view.CelebiViewController;

public class CommandImpl extends Command {
	
	// identifiers
	private int _taskUID;
	private Task.DataType _taskField;
	private Command.Type _secondaryCmdType;
	private TasksBag.ViewType _viewType;
	private CelebiViewController.Skin _theme;
	
	// field values
	private String _name;
	private Date _startDate, _endDate;
	private Path _path;
//	private String _keyword;

	////////////////////////////////////////////////////////////////////////////////////	
	// Constructor (package private)
	////////////////////////////////////////////////////////////////////////////////////
	CommandImpl (Command.Type cmd, String userInput) {
		super(cmd, userInput);
		_taskUID = -1;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// SETTERS / INITIALISERS (package private)
	////////////////////////////////////////////////////////////////////////////////////
	
	@Override 
	void setTaskUID (int uid) {
		_taskUID = uid;
	}
	@Override 
	void setTaskField (Task.DataType f) {
		_taskField = f;
	}
	@Override 
	void setViewType (TasksBag.ViewType v) {
		_viewType = v;
	}
	@Override 
	void setTheme (CelebiViewController.Skin theme) {
		_theme = theme;
	}
	
	@Override 
	void setText (String name) {
		_name = name;
	}
	@Override 
	void setStart (Date d) {
		_startDate = d == null ? null : (Date) d.clone();
	}
	@Override 
	void setEnd (Date d) {
		_endDate = d == null ? null : (Date) d.clone();
	}
	@Override 
	void setSecondaryCmdType (Command.Type t) {
		assert(t != Command.Type.INVALID);
		_secondaryCmdType = t;
	}
	@Override 
	void setPath (Path p) {
		_path = p;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////

	// Identifiers

	@Override
	public Command.Type getSecondaryCmdType() {
		return _secondaryCmdType;
	}
	@Override
	public int getTaskUID () {
		return _taskUID;
	}
	@Override
	public Task.DataType getTaskField () {
		return _taskField;
	}
	@Override
	public TasksBag.ViewType getViewType () {
		return _viewType;
	}
	@Override
	public CelebiViewController.Skin getTheme () {
		return _theme;
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

//	@Override
//	public String getSearchKeyword() {
//		return _keyword;
//	}

}
