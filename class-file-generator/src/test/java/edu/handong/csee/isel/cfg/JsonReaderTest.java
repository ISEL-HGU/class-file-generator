package edu.handong.csee.isel.cfg;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.objectweb.asm.ClassWriter;

public class JsonReaderTest {
    private static final String TEST_RESOURCES_DIR = "src/test/resources";
    private static final String PACKAGE_DIR = "edu/handong/csee/isel/cfg/gen";

    private static Path invalFilesDir 
            = Path.of(TEST_RESOURCES_DIR, "inval-files");
    private static Path packageDir 
            = Path.of(TEST_RESOURCES_DIR + "/bin", PACKAGE_DIR); 
    private static Path validFilesDir 
            = Path.of(TEST_RESOURCES_DIR, "valid-files");

    @BeforeAll 
    public static void createPackageDirectory() {
        try {
            Files.createDirectories(packageDir);
        } catch (IOException e) {
            fail("Failed to create the package directory.", e);
        }
    }

    @Test
    public void testConstructor() {
        Object[] validFilePaths = null;
        Field field = null;

        try {
            validFilePaths = Files.list(validFilesDir).sorted().toArray();
            field = JsonReader.class.getDeclaredField("info");
        } catch (IOException e) {
            fail("Failed to open the valid files directory.", e);
        } catch (NoSuchFieldException e) {
            fail("Failed to get the field.", e);
        }

        field.setAccessible(true);

        ClassInfo[] actualVals;
        ClassInfo[] expectedVals;

        expectedVals = new ClassInfo[] { 
                new ClassInfo(55,
                              "edu.handong.csee.isel.cfg.gen",
                              "Add", 
                              "add", 
                              "(II)I", 
                              new int[] { 21, 1, 21, 2, 96, 172 }),
                new ClassInfo(
                        55, 
                        "edu.handong.csee.isel.cfg.gen",
                        "Bubblesort",
                        "bubblesort",
                        "([I)[I",
                        new int[] { 4, 54, 2, 21, 2, 25, 1, 190, 100, 156, 0, 
                                    108, 3, 54, 3, 3, 54, 4, 21, 4, 25, 1, 
                                    190, 21, 2, 100, 100, 156, 0, 91, 25, 1, 21, 
                                    4, 46, 25, 1, 21, 4, 4, 96, 46, 100, 158, 
                                    0, 82, 25, 1, 21, 4, 46, 54, 5, 25, 1, 
                                    21, 4, 25, 1, 21, 4, 4, 96, 46, 79, 25, 
                                    1, 21, 4, 4, 96, 21, 5, 79, 21, 3, 154, 
                                    0, 82, 4, 54, 3, 21, 4, 4, 96, 54, 4, 
                                    167, 0, 18, 21, 3, 154, 0, 99, 25, 1, 176, 
                                    21, 2, 4, 96, 54, 2, 167, 0, 3, 25, 1, 
                                    176 }),
                new ClassInfo(
                        55,
                        "edu.handong.csee.isel.cfg.gen",
                        "Smallest",
                        "smallest",
                        "([I)I",
                        new int[] { 25, 1, 3, 46, 54, 2, 25, 1, 58, 3, 25, 
                                    3, 190, 54, 4, 3, 54, 5, 21, 5, 21, 4, 
                                    100, 156, 0, 54, 25, 3, 21, 5, 46, 54, 6, 
                                    21, 6, 21, 2, 100, 156, 0, 45, 21, 6, 54, 
                                    2, 21, 5, 4, 96, 54, 5, 167, 0, 18, 21, 
                                    2, 172 }),
                new ClassInfo(55,
                              "edu.handong.csee.isel.cfg.gen", 
                              "Sub", 
                              "sub", 
                              "(II)I", 
                              new int[] { 21, 1, 21, 2, 100, 172 }) }; 
        actualVals = new ClassInfo[expectedVals.length];

        for (int i = 0; i < actualVals.length; i++) { 
            try (InputStream in 
                    = Files.newInputStream((Path) validFilePaths[i])) {
                actualVals[i] = (ClassInfo) field.get(new JsonReader(in));  
            } catch (IllegalAccessException e) {
                fail("Failed to access the field.", e);
            } catch (IOException e) {
                fail("I/O error occured while reading the valid json file.", e);
            } catch (JsonIOException | JsonSyntaxException e) {
                fail("Failed to read the valid json file.", e);
            }
        }

        assertArrayEquals(expectedVals, actualVals);

        try {
            for (Path invalFile 
                    : Files.list(invalFilesDir.resolve("read"))
                           .collect(Collectors.toList())) {
                assertThrows(JsonSyntaxException.class, 
                             () -> { new JsonReader(
                                        Files.newInputStream(invalFile)); },
                             "Failed to throw the exception while reading the " 
                                +"invalid json file "
                                + invalFile
                                + ".");
            }
        } catch (IOException e) {
            fail("Failed to open the inval files directory.", e);
        }
    } 

    @Test
    public void testAccept() {
        Object[] validFilePaths = null;

        try {
            validFilePaths = Files.list(validFilesDir).sorted().toArray();
        } catch (IOException e) {
            fail("Failed to open the valid files directory.", e);
        }
        
        Class<?>[][] methodParamTypes;
        Object[][] methodArgs;
        Object[] actualReturns;
        Object[] expectedReturns;
        String[] classNames;
        String[] methodnames;

        classNames = new String[] { "edu.handong.csee.isel.cfg.gen.Add",
                                    "edu.handong.csee.isel.cfg.gen.Bubblesort", 
                                    "edu.handong.csee.isel.cfg.gen.Smallest",
                                    "edu.handong.csee.isel.cfg.gen.Sub"};
        methodnames = new String[] { "add", "bubblesort", "smallest", "sub" };
        methodParamTypes 
                = new Class[][] { { int.class, int.class }, 
                                  { int[].class },
                                  { int[].class }, 
                                  { int.class, int.class } };
        methodArgs 
                = new Object[][] { { 1, 2 }, 
                                   { new int[] { 3, 1, 2, 5, 4 } },
                                   { new int[] { 1, 2, 3 } }, 
                                   { 1, 2 } };
        expectedReturns = new Object[] { 3, new int[] { 1, 2, 3, 4, 5 }, 1, -1 };
        actualReturns = new Object[expectedReturns.length];

        for (int i = 0; i < actualReturns.length; i++) {
            JsonReader reader = null;

            try (InputStream in 
                    = Files.newInputStream((Path) validFilePaths[i])) {
                reader = new JsonReader(in);
            } catch (IOException e) {
                fail("I/O error occured while reading the valid json file.", e); 
            } catch (JsonIOException | JsonSyntaxException e) {
                fail("Failed to read the json file.", e);
            } 

            ClassWriter validFileWriter; 
                
            validFileWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            try {
                reader.accept(validFileWriter);
            } catch (UnsupportedOpcodeException e) {
                fail("Failed to parse the json file " 
                        + validFilePaths[i] 
                        + ".", 
                     e);
            }

            try (OutputStream out 
                    = Files.newOutputStream(
                            packageDir.resolve(
                                    reader.readClassname() + ".class"))) {    
                out.write(validFileWriter.toByteArray());
            } catch (IOException e) {
                fail("I/O error occured while writing the class file.", e);
            }

            try {
                Class<?> clazz;

                clazz = Class.forName(classNames[i]);
                
                actualReturns[i] 
                        = clazz.getMethod(methodnames[i], methodParamTypes[i])
                               .invoke(clazz.getConstructor().newInstance(), 
                                       methodArgs[i]);
            } catch (ClassNotFoundException e) {
                fail("Failed to find the generated class.", e);
            } catch (IllegalAccessException e) {
                fail("Failed to access the method of the generated class.", e);
            } catch (InstantiationException e) {
                fail("Failed to instantiate the generated class.", e);
            } catch (InvocationTargetException e) {
                fail("Method of the generated class threw an exception.", e);
            } catch (NoSuchMethodException e) {
                fail("Failed to find the method of the generated class.", e);
            } 
        }

        assertArrayEquals(expectedReturns, actualReturns);

        try {
            final ClassWriter invalFileWriter 
                    = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    
            for (Path invalFile 
                    : Files.list(invalFilesDir.resolve("parse"))
                           .collect(Collectors.toList())) {
                assertThrows(
                        UnsupportedOpcodeException.class, 
                        () -> { new JsonReader(Files.newInputStream(invalFile))
                                .accept(invalFileWriter); },
                        "Failed to throw the exception while parsing the json "
                            + "file "
                            + invalFile
                            + ".");
            }
        } catch (IOException e) {
            fail("Failed to open the inval files directory.", e);
        }
    }
}
