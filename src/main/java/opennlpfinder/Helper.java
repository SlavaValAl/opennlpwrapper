package opennlpfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Helper {
    public static String resultFileName = "input.txt";
    public static String outFileName = "out.txt";
    
    public static String GetCorrectPath(String path, String defaultPath)
        throws IOException
    {
        if (path != null) {
            if (Files.exists(Paths.get(path))) {
                return path;
            }
        }
        if (defaultPath != null) {
            if (Files.exists(Paths.get(defaultPath))) {
                return defaultPath;
            }
        }
        throw new IOException();
    }
    
    public static String GetCorrectPath(String path)
        throws IOException
    {
        return GetCorrectPath(path, null);
    }
    
    public static File GetCorrectFilePath(File basepath, String path)
        throws IOException
    {
        if (path != null) {
            if (Files.exists(Paths.get(basepath.getAbsolutePath(), path))) {
                return new File(basepath, path);
            }
        }
        throw new IOException();
    }
    
    public static File GetCorrectFilePath(String basepath, String path)
        throws IOException
    {
        if (path != null) {
            if (Files.exists(Paths.get(basepath, path))) {
                return new File(basepath, path);
            }
        }
        throw new IOException();
    }
    
    
    public static File GetResultFilePath()
        throws IOException
    {
        String cliParam = System.getProperty("result.file");
        if (cliParam != null) {
            return new File(GetCorrectPath(cliParam));
        }
        
        return new File(OpenNLPFinder.GetDefaultDataPath(), Helper.outFileName);
    }
}