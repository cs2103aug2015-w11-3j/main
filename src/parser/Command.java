package parser;

import java.util.Date;
import common.Celebi;

public class Command {

	public static final enum Type {
		Add,
		Delete, Update, 
		Sort, Search, FilterByTags, 
		Tag,
		Quit, Invalid
	}

	static final enum Fields {
		// Create/Update
	}

	private final Type _cmdType;
	private final int _taskUID;
	// create/update fields
	private final String _name, _descr;
	private final Date _startDate, _endDate;
	private final Celebi.Priority _priority;
	// organising (sort/filter/search)
	private final String searchKey


	public ParsedCommand (Command cmd){
		_cmd = cmd;
	}
	

	// getters

	public Command getCmdType () {
		return _cmdType;
	}
	public String getName () {
		return _name;
	}
	public String getDescr () {
		return _descr;
	}
	public Date getStart () {
		return _startDate.clone();
	}
	public Date getEnd () {
		return _endDate.clone();
	}
	public Celebi.Priority getPriority () {
		return _priority;
	}

}
