//@@author A0131891E
package parser.commands;

import java.nio.file.Path;
import java.util.Date;
import common.Task;
import common.TasksBag;
import ui.view.CelebiViewController;

public class CommandDataImpl extends CommandData {
	
	// identifiers
	private int _taskUID;
	private Task.DataType _taskField;
	private CommandData.Type _secondaryCmdType;
	private TasksBag.ViewType _viewType;
	private CelebiViewController.Skin _theme;
	
	// field values
	private String _name;
	private Date _startDate, _endDate;
	private Path _path;

	////////////////////////////////////////////////////////////////////////////////////	
	// Constructor (package private)
	////////////////////////////////////////////////////////////////////////////////////
	public CommandDataImpl (CommandData.Type cmd, String userInput) {
		super(cmd, userInput);
		_taskUID = -1;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// SETTERS 
	////////////////////////////////////////////////////////////////////////////////////
	
	// Identifiers 
	
	@Override
	public 
	void setTaskUID (int uid) {
		_taskUID = uid;
	}
	@Override
	public 
	void setTaskField (Task.DataType f) {
		_taskField = f;
	}
	@Override
	public 
	void setViewType (TasksBag.ViewType v) {
		_viewType = v;
	}
	@Override
	public 
	void setSecondaryCmdType (CommandData.Type t) {
		assert(t != CommandData.Type.INVALID);
		_secondaryCmdType = t;
	}
	@Override
	public 
	void setTheme (CelebiViewController.Skin theme) {
		_theme = theme;
	}
	
	// Data
	
	@Override
	public 
	void setText (String name) {
		_name = name;
	}
	@Override
	public 
	void setStart (Date d) {
		_startDate = d == null ? null : (Date) d.clone();
	}
	@Override
	public 
	void setEnd (Date d) {
		_endDate = d == null ? null : (Date) d.clone();
	}
	@Override
	public 
	void setPath (Path p) {
		_path = p;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////

	// Identifiers

	@Override
	public CommandData.Type getSecondaryCmdType() {
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
	
	// User data values

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
}
