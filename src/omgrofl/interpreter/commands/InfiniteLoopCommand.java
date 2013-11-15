package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.CommandSequence;
import omgrofl.Globals;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public class InfiniteLoopCommand implements Command {
    
    protected CommandSequence script;

    public InfiniteLoopCommand(CommandSequence script) {
        this.script = script;
    }

    public CommandSequence getCommandSequence() {
        return script;
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
