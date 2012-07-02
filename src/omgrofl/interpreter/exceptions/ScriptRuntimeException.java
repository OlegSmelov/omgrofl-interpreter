package omgrofl.interpreter.exceptions;

public class ScriptRuntimeException extends RuntimeException {

    public ScriptRuntimeException(String message) {
        super(message);
    }

    public ScriptRuntimeException() {
        super();
    }
}
