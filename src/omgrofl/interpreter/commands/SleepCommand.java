package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class SleepCommand extends Command {

    protected Parameter parameter;

    public SleepCommand(Parameter parameter) {
        this.parameter = parameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public void execute() throws ScriptInterruptedException, ScriptExitException {
        Object durationObject = parameter.evaluate();
        if (durationObject instanceof Integer) {
            try {
                int duration = ((Integer) durationObject).intValue();
                Thread.sleep(duration);
            } catch (InterruptedException e) {
            }
        } else {
            throw new ScriptRuntimeException(sourceCodeLine,
                    getClass().getName() + ": Parameter is not an integer");
        }
    }
}
