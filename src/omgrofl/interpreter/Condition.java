package omgrofl.interpreter;

import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public interface Condition {

    int EQUAL = 1;
    int GREATER = 2;
    int LESS = 3;
    int GREATER_OR_EQUAL = 4;
    int LESS_OR_EQUAL = 5;
    int NOT_EQUAL = 6;

    boolean evaluate() throws ScriptRuntimeException;
}
