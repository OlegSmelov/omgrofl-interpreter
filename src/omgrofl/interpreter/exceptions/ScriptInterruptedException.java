package omgrofl.interpreter.exceptions;

public class ScriptInterruptedException extends Exception {
    
    public ScriptInterruptedException() {
        super();
    }
    
    public ScriptInterruptedException(String message) {
        super(message);
    }
}
