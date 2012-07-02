package omgrofl.interpreter.parameters;

import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Variable;

public class VariableParameter implements Parameter {

    protected Variable variable;
    
    public VariableParameter(Variable variable) {
        this.variable = variable;
    }
    
    @Override
    public Object evaluate() {
        return variable.getValue();
    }
    
    @Override
    public String toString() {
        return variable.getName();
    }
    
    public Variable getVariable() {
        return variable;
    }
}
