package omgrofl.interpreter.procedures;

import java.util.ArrayList;
import java.util.List;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.Procedure;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public abstract class AbstractProcedure implements Procedure {

    protected List<Parameter> parameters = new ArrayList<Parameter>();

    @Override
    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    @Override
    public void clearParameters() {
        parameters.clear();
    }

    @Override
    public Parameter getParameter(int index) {
        return parameters.get(index);
    }

    @Override
    public int getParameterCount() {
        return parameters.size();
    }

    @Override
    public String toString() {
        return getName() + parameters;
    }

    public void requireNumberOfParameters(int number) throws ScriptRuntimeException {
        if (parameters.size() != number) {
            throw new ScriptRuntimeException("wrong number of parameters ("
                    + parameters.size() + "), " + number + " required");
        }
    }
}
