package omgrofl.interpreter;

import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public interface Command {
    void execute() throws ScriptInterruptedException, ScriptExitException;
}
