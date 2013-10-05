package omgrofl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.ScriptParser;
import omgrofl.interpreter.exceptions.ScriptParseException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class Main {
    
    public static void main(String[] args) throws IOException {
        String filename = null;
        if (args.length > 0) {
            filename = args[0];
        }
        
        ScriptParser scriptParser = new ScriptParser();
        Memory memory = new Memory();
        Script script;
        
        try {
            if (filename == null || filename.equals("-")) {
                script = scriptParser.parse(System.in, memory);
            } else {
                File inputFile = new File(filename);
                script = scriptParser.parse(inputFile, memory);
            }
            script.run();
        } catch (ScriptParseException e) {
            System.err.println("Error parsing the script: " + e.getMessage());
        } catch (ScriptRuntimeException e) {
            System.err.println("Runtime exception: " + e.getMessage());
        }
    }
}
