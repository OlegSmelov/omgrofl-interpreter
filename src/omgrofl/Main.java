package omgrofl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.ScriptParser;
import omgrofl.interpreter.exceptions.ScriptParseException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;

public class Main {

    public interface ExitCodes {

        int SUCCESS = 0;
        int FILE_NOT_FOUND = 1;
        int PARSING_ERROR = 2;
        int RUNTIME_ERROR = 3;
    }

    public static void main(String[] args) {
        InputStream source = System.in;

        if (args.length > 0) {
            try {
                source = new FileInputStream(args[0]);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + e);
                System.exit(ExitCodes.FILE_NOT_FOUND);
            }
        }

        ScriptParser scriptParser = new ScriptParser();
        Memory memory = new Memory();

        try {
            Script script = scriptParser.parse(source, memory);

            script.run();
        } catch (ScriptParseException e) {
            System.err.println("Error parsing the script: " + e);
            System.exit(ExitCodes.PARSING_ERROR);
        } catch (ScriptRuntimeException e) {
            System.err.println("Runtime exception: " + e);
            System.exit(ExitCodes.RUNTIME_ERROR);
        }
    }
}
