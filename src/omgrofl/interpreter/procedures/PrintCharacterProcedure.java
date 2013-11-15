package omgrofl.interpreter.procedures;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import omgrofl.Globals;
import omgrofl.interpreter.Parameter;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class PrintCharacterProcedure extends AbstractProcedure {
    
    private PrintStream printStream;

    public PrintCharacterProcedure() {
        try {
            printStream = new PrintStream(System.out, true, Globals.defaultEncoding);
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Warning: unsupported encoding, using Java default");
            printStream = System.out;
        }
    }
    
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
            printStream.print(charValue);
        } else
            throw new ScriptRuntimeException("parameter is not a character");

        return null;
    }
}
