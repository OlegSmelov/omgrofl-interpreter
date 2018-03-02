package omgrofl.interpreter.parameters;

import omgrofl.interpreter.Parameter;

public class ObjectParameter implements Parameter {

    protected Object value;

    public ObjectParameter(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String toString() {
        // Special case: use character's code instead of
        // the character itself
        if (value instanceof Character) {
            return "" + (int) ((Character) value);
        } else if (value instanceof String) {
            return "\"" + (String) value + "\"";
        } else {
            return String.valueOf(value);
        }
    }
}
