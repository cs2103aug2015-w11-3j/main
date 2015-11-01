//@@author A0125546E
package logic.exceptions;

public class NoUndoActionException extends ActionException {

    private static final long serialVersionUID = -6305570505075435759L;

    public NoUndoActionException(String msg) {
        super(msg);
    }
}
