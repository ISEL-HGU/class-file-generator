package edu.handong.csee.isel.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Class that reads Json file 
 */
public class JsonReader {
    private Gson gson = new Gson();

    /**
     * Reads <code>jsonFile</code>.
     * @param jsonFile json file to read
     * @return <code>ClassInfo</code> instance
     * @throws FileNotFoundException if the file does not exist, is a directory 
     *      rather than a regular file, or for some other reason cannot be 
     *      opened for reading
     * @throws JsonSyntaxException if the file is not a json file, contains 
     *      malformed json element, or is not a valid representation for an 
     *      object of <code>ClassInfo</code>
     * @throws JsonIOException if there was a problem reading from the Reader
     * @throws IllegalAccessException if the getter methods of created 
     *      <code>ClassInfo</code> object are inaccessible
     * @throws InvocationTargetException if the getter methods of created 
     *      <code>ClassInfo</code> object throw exceptions
     */
    public ClassInfo read(File jsonFile) throws FileNotFoundException, 
            JsonSyntaxException, JsonIOException, IllegalAccessException,
            InvocationTargetException {     
        ClassInfo info = null;   
        
        try (BufferedReader br 
                = new BufferedReader(new FileReader(jsonFile))) {
            if (!jsonFile.getPath().endsWith(".json")) {
                throw new JsonSyntaxException("The file is not a json file.");
            }

            info = gson.fromJson(br, ClassInfo.class);
            
            for (Method method : info.getClass().getMethods()) {
                if (method.getName().startsWith("get") 
                        && method.invoke(info) == null) { 
                    throw new JsonSyntaxException(
                            "The json file does not follow expected format.");
                }
            }
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw (FileNotFoundException) e;
            }

            Logger.getLogger(getClass().getName())
                  .warning("Cannot close the reader after reading " 
                           + jsonFile.getPath() + ".");
        }

        return info;
    }
}
