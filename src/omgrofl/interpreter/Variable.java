package omgrofl.interpreter;

import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class Variable {

    protected Memory memory;
    protected String name;

    public Variable(Memory memory, String name) {
        this.memory = memory;
        this.name = name;
    }

    public Object getValue() {
        Object value = memory.getVariable(name.toLowerCase());
        if (value == null) {
            throw new ScriptRuntimeException(
                    "Attempt to read uninitialized variable " + name);
        }
        return value;
    }

    public String getName() {
        return name;
    }

    public void setValue(Object value) {
        memory.setVariable(name.toLowerCase(), value);
    }
}
