//@@author A0131891E
package parser.commands.abstracted;

import java.util.Collection;

import parser.commands.CommandParams.CmdType;

/**
 * Interface for all concrete command classes.
 * Declares methods for other classes to access
 * the implementing command's parsing information.
 * 
 * @author Leow Yijin
 */
public interface ConcreteCommand {
	
	CmdType getCmdType();
	String getTooltipStr();
	String getReservedKeyword();
	Collection<String> getDefaultKeywords();
}
