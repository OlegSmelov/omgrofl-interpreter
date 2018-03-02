package omgrofl.interpreter.commands;

import java.io.IOException;
import omgrofl.interpreter.Command;
import omgrofl.Globals;
import omgrofl.interpreter.Variable;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class ReadCharacterCommand extends Command {

    protected Variable variable;

    public ReadCharacterCommand(Variable variable) {
        this.variable = variable;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public void execute() throws ScriptInterruptedException, ScriptExitException {
        int newValue = 0;
        try {
            newValue = Globals.adjustValue(System.in.read());
        } catch (IOException e) {
            throw new ScriptRuntimeException(sourceCodeLine,
                    "Error reading from standard input: " + e.getMessage());
        }
        variable.setValue(newValue);
    }

    @Override
    public String toString() {
        return Globals.readCharacterOperator + " " + variable.getName();
    }
}
