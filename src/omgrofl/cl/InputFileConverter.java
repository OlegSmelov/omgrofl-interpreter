package omgrofl.cl;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.io.File;

public class InputFileConverter implements IStringConverter<File> {
    @Override
    public File convert(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.canRead())
                    throw new ParameterException("Can't read from the file");
                return file;
            }
            throw new ParameterException("File parameter should be a file");
        } else {
            throw new ParameterException("File doesn't exist");
        }
    }
}
