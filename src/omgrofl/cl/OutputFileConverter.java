package omgrofl.cl;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.io.File;
import java.io.IOException;

public class OutputFileConverter implements IStringConverter<File> {
    @Override
    public File convert(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.canWrite())
                    throw new ParameterException("Can't write to file");
                return file;
            }
            throw new ParameterException("File parameter should be a file");
        } else {
            try {
                if (file.createNewFile())
                    return file;
            } catch (IOException ex) {
                throw new ParameterException(ex.getMessage());
            }
            throw new ParameterException("File doesn't exist");
        }
    }
}
