package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Globals;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.Variable;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;

public class ConditionCommand implements Command {
    
    protected Variable variable;
    protected Object value;
    protected Script script;

    public ConditionCommand(Variable variable, Object value, Script script) {
        this.variable = variable;
        this.value = value;
        this.script = script;
    }

    @Override
    public void execute() throws ScriptInterruptedException, ScriptExitException {
        if (value.equals(variable.getValue()))
            script.run();
    }

    @Override
    public String toString() {
        return Globals.conditionOperator + " " + variable.getName() + " iz liek "
                + String.valueOf(value) + "\n"
                + Globals.indent(script.toString())
                + Globals.endOperator;
    }
}
