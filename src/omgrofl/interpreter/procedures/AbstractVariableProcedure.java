package omgrofl.interpreter.procedures;

import omgrofl.interpreter.Globals;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Variable;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;
import omgrofl.interpreter.parameters.VariableParameter;

public abstract class AbstractVariableProcedure extends AbstractProcedure {
    
    public void incrementVariableBy(Integer amount) {
        requireNumberOfParameters(1);
        
        Parameter parameter = parameters.get(0);
        if (parameter instanceof VariableParameter) {
            Variable variable = ((VariableParameter) parameter).getVariable();
            Object value = variable.getValue();
            
            if (value instanceof Integer)
                variable.setValue(Globals.adjustValue((Integer) value + amount));
            else
                throw new ScriptRuntimeException("unknown variable type");
            
        } else
            throw new ScriptRuntimeException("parameter should be a variable");
    }
}
