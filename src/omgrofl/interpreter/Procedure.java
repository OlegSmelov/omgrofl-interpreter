package omgrofl.interpreter;

public interface Procedure {
    void addParameter(Parameter parameter);
    void clearParameters();
    String getName();
    
    Object execute();
}
