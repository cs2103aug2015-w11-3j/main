package parser;

import java.util.Date;
import common.Celebi;

public class Command {

	public static enum Type {
		Add,
		Delete, Update, 
		Sort, Search, FilterByTags, 
		Tag,
		Quit, Invalid
	}

	static enum Fields {
		// Create/Update
	}

	private Type _cmdType;
	private int _taskUID;
	// create/update fields
	private String _name, _descr;
	private Date _startDate, _endDate;
	private Celebi.Priority _priority;
	// organising (sort/filter/search)
	private String searchKey;


	public Command (Command.Type cmd){
		_cmdType = cmd;
	}
	

	// getters

	public Command.Type getCmdType () {
		return _cmdType;
	}
	public String getName () {
		return _name;
	}
	public String getDescr () {
		return _descr;
	}
	public Date getStart () {
		return (Date)_startDate.clone();
	}
	public Date getEnd () {
		return (Date)_endDate.clone();
	}
	public Celebi.Priority getPriority () {
		return _priority;
	}

}
