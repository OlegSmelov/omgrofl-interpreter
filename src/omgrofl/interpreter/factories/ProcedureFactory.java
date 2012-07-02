package omgrofl.interpreter.factories;

import java.util.List;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Procedure;
import omgrofl.interpreter.procedures.PrintProcedure;

public class ProcedureFactory {
    
    private Procedure createProcedure(String name) throws RuntimeException {
        if (name.equals("print"))
            return new PrintProcedure();
        throw new RuntimeException("unknown procedure " + name);
    }
    
    public Procedure getProcedure(String name) {
        return createProcedure(name);
    }
    
    public Procedure getProcedure(String name, List<Parameter> parameters) throws RuntimeException {
        Procedure procedure = createProcedure(name);
        for (Parameter parameter : parameters)
            procedure.addParameter(parameter);
        return procedure;
        
    }
    
    public Procedure getProcedure(String name, Parameter[] parameters) throws RuntimeException {
        Procedure procedure = createProcedure(name);
        for (Parameter parameter : parameters)
            procedure.addParameter(parameter);
        return procedure;
    }
}
