package omgrofl;

import omgrofl.cl.JCommanderParameters;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.ScriptParser;
import omgrofl.interpreter.exceptions.ScriptParseException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;
import omgrofl.jit.JavaBytecodeCompiler;
import omgrofl.jit.JavaBytecodeRunner;

public class Main {
    
    public interface ExitCodes {
        int Success = 0;
        int FileNotFound = 1;
        int ParsingError = 2;
        int CompilationError = 3;
        int RuntimeError = 4;
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
        
        if (!parameters.useInterpreter && !parameters.useJIT &&
                parameters.outputFile == null) {
            System.err.println("Specify one of the parameters: -j, -i, -o");
            printUsageToStderr(jCommander);
            return;
        }
        
        if (parameters.useInterpreter && parameters.useJIT) {
            System.err.println("Incompatible parameters (-i and -j)");
            printUsageToStderr(jCommander);
            return;
        }
        
        if (parameters.useInterpreter && parameters.outputFile != null) {
            System.err.println("Incompatible parameters (-i and -o)");
            printUsageToStderr(jCommander);
            return;
        }
        
        ScriptParser scriptParser = new ScriptParser();
        Memory memory = new Memory();
        Script script;
        
        String className = "Omgrofl";
        if (parameters.outputFile != null) {
            String filename = parameters.outputFile.getName();
            className = filename.substring(0, filename.lastIndexOf(".")).replace(" ", "_");
            // FIXME: class name should be validated more thoroughly
        }
        
        try {
            if (parameters.inputFile == null) {
                script = scriptParser.parse(System.in, memory);
            } else {
                script = scriptParser.parse(parameters.inputFile, memory);
            }
            
            if (parameters.useInterpreter)
                script.run();
            
            boolean compilationRequired = parameters.useJIT ||
                    parameters.outputFile != null;
            
            if (compilationRequired) {
                try {
                    JavaBytecodeCompiler compiler = new JavaBytecodeCompiler(className);
                    compiler.visit(script);

                    byte[] bytecode = compiler.getBytecode();
                    if (parameters.outputFile != null) {
                        FileOutputStream outFile = new FileOutputStream(parameters.outputFile);
                        outFile.write(bytecode);
                        outFile.close();
                    }

                    if (parameters.useJIT)
                        JavaBytecodeRunner.run(compiler.getBytecode(), className);
                } catch (Exception ex) {
                    System.err.println("Compilation error: " + ex.getMessage());
                    System.exit(ExitCodes.CompilationError);
                }
            }
        } catch (ScriptParseException e) {
            System.err.println("Error parsing the script: " + e);
            System.exit(ExitCodes.ParsingError);
        } catch (ScriptRuntimeException e) {
            System.err.println("Runtime exception: " + e);
            System.exit(ExitCodes.RuntimeError);
        } catch (FileNotFoundException ex) {
            System.err.println("File not found");
            System.exit(ExitCodes.FileNotFound);
        }
    }
}
