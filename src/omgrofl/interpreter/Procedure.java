package omgrofl.interpreter;

public interface Procedure {

    void addParameter(Parameter parameter);

    void clearParameters();

    Parameter getParameter(int index);

    int getParameterCount();

    String getName();

    Object execute();
}
