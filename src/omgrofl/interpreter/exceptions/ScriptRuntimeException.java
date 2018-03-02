package omgrofl.interpreter.exceptions;

public class ScriptRuntimeException extends RuntimeException {

    private Integer line = null;

    public ScriptRuntimeException(String message) {
        super(message);
    }

    public ScriptRuntimeException(Integer line, String message) {
        this(message);
        this.line = line;
    }

    public ScriptRuntimeException() {
        super();
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    @Override
    public String toString() {
        if (line == null) {
            return getMessage();
        } else {
            return "Line " + line + ": " + getMessage();
        }
    }
}
