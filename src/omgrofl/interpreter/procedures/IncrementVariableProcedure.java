package omgrofl.interpreter.procedures;

import omgrofl.interpreter.Globals;

public class IncrementVariableProcedure extends AbstractVariableProcedure {

    @Override
    public String getName() {
        return Globals.incrementVariableOperator;
    }

    @Override
    public Object execute() {
        incrementVariableBy(1);
        return null;
    }
}
