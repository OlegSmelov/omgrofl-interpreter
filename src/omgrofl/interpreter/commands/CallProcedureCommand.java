package omgrofl.interpreter.commands;

import omgrofl.interpreter.Command;
import omgrofl.interpreter.Procedure;

public class CallProcedureCommand implements Command {

    protected Procedure procedure;

    public CallProcedureCommand(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public void execute() {
        procedure.execute();
    }

    @Override
    public String toString() {
        return procedure.toString();
    }
}
