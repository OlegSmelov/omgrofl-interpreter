package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Globals;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public class InfiniteLoopCommand implements Command {
    
    protected Script script;

    public InfiniteLoopCommand(Script script) {
        this.script = script;
    }

    @Override
    public void execute() throws ScriptExitException {
        try {
            while (true)
                script.run();
        } catch (ScriptInterruptedException e) {
            // do nothing, this is normal
        }
    }

    @Override
    public String toString() {
        return Globals.loopOperator + "\n" +
                Globals.indent(script.toString()) +
                Globals.endOperator;
    }
}
