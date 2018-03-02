package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.Globals;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Variable;

public class StackPopCommand extends Command {

    protected Variable variable;
    protected Memory memory;

    public StackPopCommand(Variable variable, Memory memory) {
        this.variable = variable;
        this.memory = memory;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public void execute() {
        variable.setValue(memory.removeFirst());
    }

    @Override
    public String toString() {
        return Globals.stackPopOperator + " " + variable.getName();
    }
}
