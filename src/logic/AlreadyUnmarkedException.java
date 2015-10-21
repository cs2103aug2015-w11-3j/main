package logic;

import logic.exceptions.LogicException;

public class AlreadyUnmarkedException extends LogicException {

    /**
     * 
     */
    private static final long serialVersionUID = 4423335997573387768L;

    public AlreadyUnmarkedException(String msg) {
        super(msg);
    }

}
