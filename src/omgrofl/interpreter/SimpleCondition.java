package omgrofl.interpreter;

import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class SimpleCondition implements Condition {
    
    protected Parameter left, right;
    protected int operation;

    public SimpleCondition(Parameter left, Parameter right, int operation) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    public Parameter getLeftParameter() {
        return left;
    }

    public Parameter getRightParameter() {
        return right;
    }

    public int getOperation() {
        return operation;
    }
    
    @Override
    public boolean evaluate() throws ScriptRuntimeException {
        Object leftObject = left.evaluate();
        Object rightObject = right.evaluate();
        
        if (leftObject instanceof Integer && rightObject instanceof Integer) {
            int leftValue = ((Integer) leftObject).intValue();
            int rightValue = ((Integer) rightObject).intValue();
            
            switch (operation) {
                case EQUAL:
                    return leftValue == rightValue;
                case GREATER:
                    return leftValue > rightValue;
                case LESS:
                    return leftValue < rightValue;
                case GREATER_OR_EQUAL:
                    return leftValue >= rightValue;
                case LESS_OR_EQUAL:
                    return leftValue <= rightValue;
                case NOT_EQUAL:
                    return leftValue != rightValue;
                default:
                    return false;
            }
            
        } else
            throw new ScriptRuntimeException("Cannot compare non-integer values");
    }

    @Override
    public String toString() {
        String operationString = "<unknown command>";
        
        switch (operation) {
                case EQUAL:
                    operationString = "==";
                    break;
                case GREATER:
                    operationString = ">";
                    break;
                case LESS:
                    operationString = "<";
                    break;
                case GREATER_OR_EQUAL:
                    operationString = ">=";
                    break;
                case LESS_OR_EQUAL:
                    operationString = "<=";
                    break;
                case NOT_EQUAL:
                    operationString = "!=";
                    break;
            }
        
        return left.toString() + " " + operationString + " " + right.toString();
    }
}
