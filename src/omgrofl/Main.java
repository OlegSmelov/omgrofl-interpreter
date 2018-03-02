package omgrofl;

import omgrofl.cl.JCommanderParameters;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
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

        int Success = 0;
        int FileNotFound = 1;
        int ParsingError = 2;
        int CompilationError = 3;
        int RuntimeError = 4;
        int IOError = 5;
    }

    private static void printUsageToStderr(JCommander jCommander) {
        StringBuilder usageMessage = new StringBuilder();
        jCommander.usage(usageMessage);
        System.err.println(usageMessage.toString());
    }

    public static void main(String[] args) {
        JCommanderParameters parameters = new JCommanderParameters();

        JCommander jCommander = new JCommander(parameters);
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println("Error: " + e.getMessage());
            printUsageToStderr(jCommander);
            return;
        }

        ScriptParser scriptParser = new ScriptParser();
        Memory memory = new Memory();

        try {
            InputStream source = (parameters.inputFiles != null)
                    ? new FileInputStream(parameters.inputFiles.get(0))
                    : System.in;

            Script script = scriptParser.parse(source, memory);

            script.run();
        } catch (ScriptParseException e) {
            System.err.println("Error parsing the script: " + e);
            System.exit(ExitCodes.ParsingError);
        } catch (ScriptRuntimeException e) {
            System.err.println("Runtime exception: " + e);
            System.exit(ExitCodes.RuntimeError);
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            System.exit(ExitCodes.FileNotFound);
        }
    }
}
