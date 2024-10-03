package edu.handong.csee.isel.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
     * @param jsonFile json <code>File</code> to read
     * @return <code>ClassInfo</code> instance
     * @throws FileNotFoundException if the file does not exist, is a directory 
     *      rather than a regular file, or for some other reason cannot be 
     *      opened for reading
     * @throws JsonSyntaxException if json is not a valid representation for 
     *      an object of <code>ClassInfo</code>
     * @throws JsonIOException if there was a problem reading from the Reader
     * @throws IOException If an I/O error occurs
     */
    public ClassInfo read(File jsonFile) throws FileNotFoundException, 
            JsonSyntaxException, JsonIOException, IOException {
        try (BufferedReader br 
                = new BufferedReader(new FileReader(jsonFile))) {
            return gson.fromJson(br, ClassInfo.class);
        }
    }
}
