package omgrofl.interpreter;

public class Variable {
    
    protected Memory memory;
    protected String name;
    
    public Variable(Memory memory, String name) {
        this.memory = memory;
        this.name = name;
    }
    
    public Object getValue() {
        return memory.getVariable(name);
    }
    
    public String getName() {
        return name;
    }
    
    public void setValue(Object value) {
        memory.setVariable(name, value);
    }
}
