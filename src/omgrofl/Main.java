package omgrofl;

import java.io.File;
import java.io.IOException;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.ScriptParser;
import omgrofl.interpreter.exceptions.ScriptExitException;
import omgrofl.interpreter.exceptions.ScriptInterruptedException;
import omgrofl.interpreter.exceptions.ScriptParseException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        File input = new File("input.txt");
        
        ScriptParser scriptParser = new ScriptParser();
        Memory memory = new Memory();
        
        try {
            Script script = scriptParser.parse(input, memory);
            //System.out.println(script);
            script.run();
        } catch (ScriptParseException e) {
            System.err.println(e);
        } catch (ScriptInterruptedException e) {
            System.err.println(e);
        } catch (ScriptExitException e) {
            // 
        }
        
        System.out.println();
    }
}
