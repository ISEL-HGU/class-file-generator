package edu.handong.csee.isel.cfg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;

import com.google.gson.JsonSyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonReaderTest {
    
    @BeforeAll 
    public static void assertCurrDir() {
        assertTrue(System.getProperty("user.dir")
                         .endsWith(String.join(File.separator, 
                                   "class-file-generator", 
                                   "class-file-generator")));
    }

    @Test
    public void testRead() {
        ClassInfo[] actualResults;
        ClassInfo[] expectedResults;
        File[] invalFiles;
        File[] validFiles;
        File testResourcesDir;
        JsonReader reader;
        
        testResourcesDir = new File("src/test/resources");
        validFiles = new File(testResourcesDir, "valid-files").listFiles();
        invalFiles = new File(testResourcesDir, "inval-files").listFiles();
        reader = new JsonReader();
        expectedResults = new ClassInfo[] {
                new ClassInfo(new String[] { "1B", "1C", "60", "AC" },
                              "Add", "add", "(II)I", 
                              "edu.handong.csee.isel.cfg.gen"),
                new ClassInfo(new String[] { "1B", "1C", "64", "AC" }, 
                              "Sub", "sub", "(II)I", 
                              "edu.handong.csee.isel.cfg.gen") }; 
        actualResults = new ClassInfo[validFiles.length];
        
        try {
            for (int i = 0; i < actualResults.length; i++) {
                actualResults[i] = reader.read(validFiles[i]);
            }
        } catch (Exception e) {
            fail();
        }

        for (int i = 0; i < actualResults.length; i++) {
            assertEquals(expectedResults[i], actualResults[i]);
        }
        
        assertThrows(FileNotFoundException.class, 
                     () -> { reader.read(
                                new File(testResourcesDir, 
                                         "not_exist.json")); });
        
        for (File invalFile : invalFiles) {
            assertThrows(JsonSyntaxException.class,
                        () -> { reader.read(invalFile); });
        }
    }
}
