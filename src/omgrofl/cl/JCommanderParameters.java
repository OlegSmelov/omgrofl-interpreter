package omgrofl.cl;

import com.beust.jcommander.Parameter;
import java.io.File;

public class JCommanderParameters {
    @Parameter(names = { "-f", "--file" }, description = "Input file name",
            converter = InputFileConverter.class)
    public File inputFile = null;
}
