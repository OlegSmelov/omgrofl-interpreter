package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Procedure;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class CallProcedureCommand extends Command {

    protected Procedure procedure;

    public CallProcedureCommand(Procedure procedure) {
        this.procedure = procedure;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    @Override
    public void execute() {
        try {
            procedure.execute();
        } catch (ScriptRuntimeException exception) {
            exception.setLine(sourceCodeLine);
            throw exception;
        }
    }

    @Override
    public String toString() {
        return procedure.toString();
    }
}
