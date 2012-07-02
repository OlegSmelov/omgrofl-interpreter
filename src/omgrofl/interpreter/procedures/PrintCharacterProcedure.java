package omgrofl.interpreter.procedures;

import omgrofl.interpreter.Globals;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class PrintCharacterProcedure extends AbstractProcedure {
    
    @Override
    public String getName() {
        return Globals.printCharacterOperator;
    }

    @Override
    public Object execute() {
        requireNumberOfParameters(1);
        
        Parameter parameter = parameters.get(0);
        Object value = parameter.evaluate();
        
        if (value instanceof Character)
            System.out.print((Character) value);
        else if (value instanceof Integer) {
            Character charValue = (char) ((Integer) value).intValue();
            System.out.print(charValue);
        } else
            throw new ScriptRuntimeException("parameter is not a character");

        return null;
    }
}
