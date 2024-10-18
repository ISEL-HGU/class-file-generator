package edu.handong.csee.isel.cfg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.objectweb.asm.ClassWriter;

public class ClassFileGenerator {
    private final Logger LOGGER 
            = Logger.getLogger(ClassFileGenerator.class.getName());

    private CommandLine cmd;    
    private Options opts;

    private ClassFileGenerator(String[] args) throws ParseException {
        opts = defineOptions();
        cmd = new DefaultParser().parse(opts, args);
    }

    private Options defineOptions() {
        return new Options().addOption(Option.builder("h")
                                             .longOpt("help")
                                             .desc("print this message")
                                             .build())
                            .addOption(Option.builder("d")
                                             .longOpt("directory")
                                             .hasArg()
                                             .argName("output_directory")
                                             .desc("set output directory that " 
                                                   + "generated class files to " 
                                                   + "be saved")
                                             .build());
    }

    /**
     * Generates class file to <code>dstDir</code> by using 
     * <code>srcFile</code>.
     *  
     * @param dstDir directory that class file to be generated
     * @param srcFile json file for generating class file
     * @throws FileNotFoundException if the file does not exist, is a directory 
     *      rather than a regular file, or for some other reason cannot be 
     *      opened for reading
     * @throws JsonSyntaxException if the file is not a json file, contains 
     *      malformed json element, or is not a valid representation for an 
     *      object of <code>ClassInfo</code>
     * @throws JsonIOException if there was a problem reading from the Reader
     * @throws IllegalAccessException  if the getter methods of created 
     *      <code>ClassInfo</code> object are inaccessible
     * @throws InvocationTargetExeception  if the getter methods of created 
     *      <code>ClassInfo</code> object throw exceptions
     * @throws UnsupportedBytecodeException  if unsupported bytecode is used in 
     *      the json file
     * @throws IOException if an I/O error occurs to internal Writer of this 
     *      method
     */
    private void generateClassFile(File dstDir, File srcFile)
            throws FileNotFoundException, JsonSyntaxException, JsonIOException, 
                   IllegalAccessException, InvocationTargetException, 
                   UnsupportedBytecodeException, IOException {
        ClassWriter writer;
        JsonReader reader;

        writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        reader = new JsonReader(srcFile);

        reader.accept(writer);

        BufferedOutputStream bos;
        File classFile;
        File dstPkgDir;

        dstPkgDir = new File(dstDir, 
                             reader.readPackagename()
                                   .replace('.', File.separatorChar));
            
        if (dstPkgDir.mkdirs()) {
            LOGGER.info("Created package directory " + dstPkgDir + ".");
        }

        classFile = new File(dstPkgDir, reader.readClassname() + ".class");
        bos = new BufferedOutputStream(new FileOutputStream(classFile)); 
        
        try {
            bos.write(writer.toByteArray());
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                LOGGER.warning("Cannot close the writer after writing " 
                               + classFile + ".");
            }
        }   
    }

    private void generateClassFiles(File dstDir, File srcDir) 
            throws IOException {
        LOGGER.info("------- Start generating class files. -------\n"
                    + "source directory: " 
                    + srcDir
                    + "\ndestination directory: " 
                    + dstDir);
        
        if (dstDir.mkdirs()) {
            LOGGER.info("Created destination directory " + dstDir + ".");
        }
                    
        Files.walkFileTree(srcDir.toPath(), 
                           new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(
                                        Path path, BasicFileAttributes attr) {
                                    try {
                                        generateClassFile(dstDir, path.toFile());
                                    } catch (FileNotFoundException e) {
                                        LOGGER.info("Skip reading " 
                                                    + path
                                                    + ". It may not exist, or "
                                                    + "be opened.");    
                                    } catch (JsonSyntaxException e) {
                                        LOGGER.info("Skip reading " 
                                                    + path 
                                                    + ". " 
                                                    + e.getMessage());
                                    } catch (JsonIOException e) {
                                        LOGGER.warning("Unable to read "
                                                       + path 
                                                       + ". There is a problem "
                                                       + "in reader while "
                                                       + "reading.");
                                    } catch (IllegalAccessException 
                                             | InvocationTargetException e) {
                                        LOGGER.severe("Failed to read " 
                                                      + path
                                                      + ". Unable to check the "
                                                      + "json file format.");
                                    } catch (UnsupportedBytecodeException e) {
                                        LOGGER.info("Skip parsing " 
                                                    + path 
                                                    + ". "
                                                    + e.getMessage());
                                    } catch (IOException e) {
                                        LOGGER.warning("Unable to write class "
                                                       + "file using " 
                                                       + path 
                                                       + ". There is a" 
                                                       + "problem in writer "
                                                       + "while writing.");
                                    }

                                    return FileVisitResult.CONTINUE;
                                }});

        LOGGER.info("------- Finished generating class files. -------");
    }

    private void run() throws IOException {
        String[] args;

        args = cmd.getArgs();

        if (cmd.hasOption("h")) {
            new HelpFormatter().printHelp("cfg [options] path_to_directory",             
                                          opts);
        } else if (args.length == 0) {
            System.err.println("usage: cfg [options] path_to_directory");
            System.err.println("use -h or --help to see options");
        }
        
        if (args.length > 0) {
            generateClassFiles(
                    new File(cmd.getOptionValue(
                            "d", 
                            String.join(File.separator, 
                                        System.getProperty("user.home"), 
                                        "class-files"))), 
                new File(args[0]));
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        new ClassFileGenerator(args).run();
    }
}
