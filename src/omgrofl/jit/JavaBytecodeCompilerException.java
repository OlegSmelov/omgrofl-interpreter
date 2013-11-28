package omgrofl.jit;

public class JavaBytecodeCompilerException extends Exception {
    private Integer line = null;
    
    public JavaBytecodeCompilerException(String message) {
        super(message);
    }

    public JavaBytecodeCompilerException(Integer line, String message) {
        this(message);
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
        if (line == null)
            return getMessage();
        else
            return "Line " + line + ": " + getMessage();
    }
}
