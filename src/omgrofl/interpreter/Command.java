package omgrofl.interpreter;

import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public abstract class Command {
    protected Integer sourceCodeLine = null;

    public Integer getSourceCodeLine() {
        return sourceCodeLine;
    }

    public void setSourceCodeLine(Integer sourceCodeLine) {
        this.sourceCodeLine = sourceCodeLine;
    }

    public abstract void execute() throws ScriptInterruptedException, ScriptExitException;
}
