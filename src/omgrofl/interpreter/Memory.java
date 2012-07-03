package omgrofl.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    
    protected Map<String, Object> variables = new HashMap<String, Object>();
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public String toString() {
        return variables.toString();
    }
}
