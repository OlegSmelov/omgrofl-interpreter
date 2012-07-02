package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Globals;
import omgrofl.interpreter.exceptions.ScriptExitException;

public class ExitCommand implements Command {

    @Override
    public void execute() throws ScriptExitException {
        throw new ScriptExitException();
    }

    @Override
    public String toString() {
        return Globals.exitOperator;
    }
}
