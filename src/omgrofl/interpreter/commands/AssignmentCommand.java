package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Variable;

public class AssignmentCommand extends Command {

    protected Variable variable;
    protected Parameter value;

    public AssignmentCommand(Variable variable, Parameter value) {
        this.variable = variable;
        this.value = value;
    }

    public Variable getVariable() {
        return variable;
    }

    public Parameter getValue() {
        return value;
    }

    @Override
    public void execute() {
        variable.setValue(value.evaluate());
    }

    @Override
    public String toString() {
        return variable.getName() + " = " + value;
    }
}
