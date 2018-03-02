package omgrofl.interpreter;

import java.util.*;

public class Memory {

    protected Map<String, Object> variables = new HashMap<String, Object>();
    protected Deque<Object> stackQueue = new LinkedList<Object>();

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void addFirst(Object value) {
        stackQueue.addFirst(value);
    }

    public Object removeFirst() {
        try {
            return stackQueue.removeFirst();
        } catch (NoSuchElementException e) {
            return (Integer) 0;
        }
    }

    public Object removeLast() {
        try {
            return stackQueue.removeLast();
        } catch (NoSuchElementException e) {
            return (Integer) 0;
        }
    }

    @Override
    public String toString() {
        return variables.toString();
    }
}
