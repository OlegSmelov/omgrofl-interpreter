package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.CommandSequence;
import omgrofl.Globals;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Variable;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public class ForLoopCommand extends Command {

    protected Variable variable;
    protected Parameter initialValueParameter;
    protected Parameter endValueParameter;
    protected CommandSequence script;
    
    protected Integer initialValue;
    protected Integer endValue;
    protected boolean forwardDirection;

    public ForLoopCommand(
            Variable variable,
            Parameter initialValueParameter,
            Parameter endValueParameter,
            CommandSequence script) {
        this.variable = variable;
        this.initialValueParameter = initialValueParameter;
        this.endValueParameter = endValueParameter;
        this.script = script;
    }

    public CommandSequence getCommandSequence() {
        return script;
    }

    @Override
    public void execute() throws ScriptExitException {
        this.initialValue = (Integer) initialValueParameter.evaluate();
        this.endValue = (Integer) endValueParameter.evaluate();
        this.forwardDirection = (initialValue <= endValue);
        int delta = forwardDirection ? 1 : -1;

        variable.setValue(initialValue);

        try {
            while (notFinished()) {
                script.run();
                variable.setValue((Integer) variable.getValue() + delta);
            }
        } catch (ScriptInterruptedException e) {
            // do nothing, this is normal
        }
    }

    private boolean notFinished() {
        if (forwardDirection) {
            return (Integer) variable.getValue() <= endValue;
        } else {
            return (Integer) variable.getValue() >= endValue;
        }
    }

    @Override
    public String toString() {
        return Globals.loopOperator + " " + variable.getName()
                + " " + Globals.isOperator + " " + String.valueOf(initialValue)
                + " " + Globals.toOperator + " " + String.valueOf(endValue) + "\n"
                + Globals.indent(script.toString())
                + Globals.endOperator;
    }
}
