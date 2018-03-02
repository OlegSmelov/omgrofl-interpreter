package omgrofl.interpreter.factories;

import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Procedure;
import omgrofl.interpreter.Variable;
import omgrofl.interpreter.parameters.ObjectParameter;
import omgrofl.interpreter.parameters.ProcedureParameter;
import omgrofl.interpreter.parameters.VariableParameter;

public class ParameterFactory {

    public Parameter getParameter(Object value) {
        if (value instanceof Procedure) {
            return new ProcedureParameter((Procedure) value);
        } else if (value instanceof Variable) {
            return new VariableParameter((Variable) value);
        } else {
            return new ObjectParameter(value);
        }
    }
}
