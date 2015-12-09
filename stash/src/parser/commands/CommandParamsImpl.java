//@@author A0131891E
package parser.commands;

import java.nio.file.Path;
import java.util.Date;
import common.Task;
import common.TasksBag;
import ui.view.CelebiViewController.Skin;

/**
 * Simple implementation of CommandParams.
 * All fields stored as-is, no additional processing performed.
 * All getters and setters are trivial.
 * 
 * @author Leow Yijin
 */
public class CommandParamsImpl extends CommandParams {
	
	// identifiers
	private int _taskUID;
	private Task.DataType _taskField;
	private CommandParams.CmdType _secondaryCmdType;
	private TasksBag.ViewType _viewType;
	private Skin _theme;
	
	// field values
	private String _name;
	private Date _startDate, _endDate;
	private Path _path;

	////////////////////////////////////////////////////////////////////////////////////	
	// Constructor (package private)
	////////////////////////////////////////////////////////////////////////////////////
	CommandParamsImpl (CommandParams.CmdType cmd, String userInput) {
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
	void setSecondaryCmdType (CommandParams.CmdType t) {
		assert(t != CommandParams.CmdType.INVALID);
		_secondaryCmdType = t;
	}
	@Override
	public 
	void setTheme (Skin theme) {
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
	public CommandParams.CmdType getSecondaryCmdType() {
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
	public Skin getTheme () {
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
