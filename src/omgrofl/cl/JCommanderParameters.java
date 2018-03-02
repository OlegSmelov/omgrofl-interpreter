package omgrofl.cl;

import com.beust.jcommander.Parameter;
import java.io.File;
import java.util.List;

public class JCommanderParameters {

    @Parameter(description = "Input file name", converter = InputFileConverter.class)
    public List<File> inputFiles = null;
}
