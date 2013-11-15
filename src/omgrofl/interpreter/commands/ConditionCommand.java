package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.CommandSequence;
import omgrofl.interpreter.Condition;
import omgrofl.Globals;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public class ConditionCommand implements Command {
    
    protected Condition condition;
    protected CommandSequence commandSequence;

    public ConditionCommand(Condition condition, CommandSequence commandSequence) {
        this.condition = condition;
        this.commandSequence = commandSequence;
    }

    public Condition getCondition() {
        return condition;
    }

    public CommandSequence getCommandSequence() {
        return commandSequence;
    }

    @Override
    public void execute() throws ScriptInterruptedException, ScriptExitException {
        if (condition.evaluate())
            commandSequence.run();
    }

    @Override
    public String toString() {
        return Globals.conditionOperator + " " + condition.toString() + "\n"
                + Globals.indent(commandSequence.toString())
                + Globals.endOperator;
    }
}
