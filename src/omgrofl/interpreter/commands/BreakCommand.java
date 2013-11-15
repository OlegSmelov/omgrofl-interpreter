package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.Globals;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public class BreakCommand implements Command {

    @Override
    public void execute() throws ScriptInterruptedException {
        throw new ScriptInterruptedException();
    }

    @Override
    public String toString() {
        return Globals.breakOperator;
    }
}
