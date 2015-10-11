package logic;

import logic.exceptions.IntegrityCommandException;
import logic.exceptions.LogicException;

public interface Action {
	public Feedback execute() throws LogicException;
}
