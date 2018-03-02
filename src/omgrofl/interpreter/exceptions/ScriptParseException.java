package omgrofl.interpreter.exceptions;

public class ScriptParseException extends Exception {

    private Integer line;

    public ScriptParseException() {
        super();
        line = null;
    }

    public ScriptParseException(String message) {
        super(message);
    }

    public ScriptParseException(Integer line, String message) {
        super(message);
        this.line = line;
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
