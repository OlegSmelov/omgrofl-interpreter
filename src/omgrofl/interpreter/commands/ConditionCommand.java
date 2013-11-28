package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.CommandSequence;
import omgrofl.interpreter.Condition;
import omgrofl.Globals;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class ConditionCommand extends Command {
    
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
        boolean conditionValue;
        
        try {
            conditionValue = condition.evaluate();
        } catch (ScriptRuntimeException exception) {
            exception.setLine(sourceCodeLine);
            throw exception;
        }
        
        if (conditionValue)
            commandSequence.run();
    }

    @Override
    public String toString() {
        return Globals.conditionOperator + " " + condition.toString() + "\n"
                + Globals.indent(commandSequence.toString())
                + Globals.endOperator;
    }
}
