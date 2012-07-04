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
        File examplesDirectory = new File("examples");
        String[] files = examplesDirectory.list();
        
        Arrays.sort(files);
        
        ScriptParser scriptParser = new ScriptParser();
        
        for (String filename : files) {
            if (filename.endsWith(".omgrofl")) {
                System.out.print(filename);
                System.out.println(":");
                
                Memory memory = new Memory();
                
                try {
                    File input = new File(examplesDirectory, filename);
                    Script script = scriptParser.parse(input, memory);
                    script.run();
                } catch (ScriptParseException e) {
                    System.out.print("[failed to parse]");
                } catch (ScriptRuntimeException e) {
                    System.out.print("[runtime exception]");
                }
                
                System.out.println();
                System.out.println();
            }
        }
    }
}
