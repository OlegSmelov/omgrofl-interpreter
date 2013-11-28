package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.Globals;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Variable;

public class StackPushCommand extends Command {

    protected Variable variable;
    protected Memory memory;

    public StackPushCommand(Variable variable, Memory memory) {
        this.variable = variable;
        this.memory = memory;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public void execute() {
        memory.addFirst(variable.getValue());
    }
    
    @Override
    public String toString() {
        return Globals.stackPushOperator + " " + variable.getName();
    }
}
