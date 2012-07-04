package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Variable;

public class StackPushCommand implements Command {

    protected Variable variable;
    protected Memory memory;

    public StackPushCommand(Variable variable, Memory memory) {
        this.variable = variable;
        this.memory = memory;
    }

    @Override
    public void execute() {
        memory.addFirst(variable.getValue());
    }
}
