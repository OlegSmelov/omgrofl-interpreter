package omgrofl.interpreter.exceptions;

public class ScriptExitException extends Exception {

    public ScriptExitException(String message) {
        super(message);
    }

    public ScriptExitException() {
        super();
    }
}
