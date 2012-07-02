package omgrofl.interpreter.parameters;

import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Procedure;

public class ProcedureParameter implements Parameter {
    
    protected Procedure procedure;
    
    public ProcedureParameter(Procedure procedure) {
        this.procedure = procedure;
    }
    
    @Override
    public Object evaluate() {
        return procedure.execute();
    }
    
    @Override
    public String toString() {
        return procedure.toString();
    }
}
