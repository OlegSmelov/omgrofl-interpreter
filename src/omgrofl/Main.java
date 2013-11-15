package omgrofl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import omgrofl.interpreter.Memory;
import omgrofl.interpreter.Script;
import omgrofl.interpreter.ScriptParser;
import omgrofl.interpreter.exceptions.ScriptParseException;
import omgrofl.interpreter.exceptions.ScriptRuntimeException;
import omgrofl.jit.JavaBytecodeCompiler;
import omgrofl.jit.JavaBytecodeRunner;

public class Main {
    
    public static void main(String[] args) throws IOException {
        String filename = null;
        if (args.length > 0) {
            filename = args[0];
        }
        
        ScriptParser scriptParser = new ScriptParser();
        Memory memory = new Memory();
        Script script = null;
        
        try {
            if (filename == null || filename.equals("-")) {
                script = scriptParser.parse(System.in, memory);
            } else {
                File inputFile = new File(filename);
                script = scriptParser.parse(inputFile, memory);
            }
            //script.run();
        } catch (ScriptParseException e) {
            System.err.println("Error parsing the script: " + e);
        } catch (ScriptRuntimeException e) {
            System.err.println("Runtime exception: " + e);
        }
        
        try {
            JavaBytecodeCompiler compiler = new JavaBytecodeCompiler("Omgrofl");
            compiler.visit(script);
            
            /*FileOutputStream outFile = new FileOutputStream("Omgrofl.class");
            byte[] bytecode = compiler.getBytecode();
            outFile.write(bytecode);
            outFile.close();*/
            
            JavaBytecodeRunner.run(compiler.getBytecode(), "Omgrofl");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
