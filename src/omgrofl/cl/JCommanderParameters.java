package omgrofl.cl;

import com.beust.jcommander.Parameter;
import java.io.File;

public class JCommanderParameters {
    @Parameter(names = { "-f", "--file" }, description = "Input file name",
            converter = InputFileConverter.class)
    public File inputFile = null;
    
    @Parameter(names = { "-i", "--interpret" }, description = "Force to use interpreter")
    public boolean useInterpreter = false;
    
    @Parameter(names = { "-j", "--jit" }, description = "Force to use JIT")
    public boolean useJIT = false;
    
    @Parameter(names = { "-o", "--output" }, description =
            "Output file name (.class file)", converter = OutputFileConverter.class)
    public File outputFile = null;
}
