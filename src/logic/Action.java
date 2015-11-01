//@@author A0125546E
package logic;

import logic.exceptions.LogicException;

public interface Action {
	public Feedback execute() throws LogicException;
}
