package edu.handong.csee.isel.cfg;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
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
    private static final Logger LOGGER 
            = Logger.getLogger(ClassFileGenerator.class.getName());

    private CommandLine cmd;    
    private Options opts;

    private ClassFileGenerator(String[] args) throws ParseException {
        opts = defineOptions();
        cmd = new DefaultParser(false).parse(opts, args);
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
     * @throws FileAlreadyExistsException if <code>dstDir</code> exists but is 
     *      not a directory
     * @throws FileExtensionException if <code>srcFile</code> is not a json file
     * @throws JsonSyntaxException if <code>srcFile</code> contains malformed 
     *      json element, or is not a valid representation for an object of 
     *      <code>ClassInfo</code>
     * @throws JsonIOException if there was a problem reading from the Reader
     * @throws IOException if an I/O error occurs while reading 
     *      <code>srcFile</code> or writing class file 
     * @throws UnsupportedOpcodeException if unsupported opcode is used in 
     *      <code>srcFile</code>
     */
    private void generateClassFile(Path dstDir, Path srcFile)
            throws FileAlreadyExistsException, FileExtensionException, 
                   JsonSyntaxException, JsonIOException, 
                   IOException, UnsupportedOpcodeException {
        if (!srcFile.getFileName().toString().endsWith(".json")) { 
            throw new FileExtensionException("The file is not a json file.");
        }
        
        ClassWriter writer;
        JsonReader reader;

        writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        
        try (InputStream in = Files.newInputStream(srcFile)) {
            reader = new JsonReader(in);
                    
            reader.accept(writer);
        } 

        Path dstPkgDir;

        dstPkgDir = dstDir.resolve(reader.readPackagename()
                                         .replace('.', File.separatorChar));
        
        Files.createDirectories(dstPkgDir);
    
        try (OutputStream out 
                = Files.newOutputStream(
                        dstPkgDir.resolve(reader.readClassname() + ".class"))) {
            out.write(writer.toByteArray());
        }   
    }

    private void generateClassFiles(Path dstDir, Path srcDir) 
            throws FileAlreadyExistsException, IOException {
        LOGGER.info("------- Start generating class files. -------\n"
                    + "source directory: " 
                    + srcDir
                    + "\ndestination directory: " 
                    + dstDir);
        
        Files.walkFileTree(
                Files.createDirectories(dstDir),
                new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(
                                Path path, BasicFileAttributes attr) {
                            try {
                                generateClassFile(dstDir, path);
                            } catch (FileAlreadyExistsException e) {
                                LOGGER.info("Skip writing class file of "
                                            + path
                                            + "."
                                            + e.getMessage());
                            } catch (JsonSyntaxException e) {
                                LOGGER.info("Skip reading " 
                                            + path 
                                            + ". " 
                                            + e.getMessage());
                            } catch (JsonIOException e) {
                                LOGGER.warning("Skip reading "
                                               + path 
                                               + "."
                                               + e.getMessage());
                            } catch (UnsupportedOpcodeException e) {
                                LOGGER.info("Skip parsing " 
                                            + path 
                                            + ". "
                                            + e.getMessage());
                            } catch (IOException e) {
                                LOGGER.warning("Skip reading" 
                                               + path 
                                               + " or writing class file of" 
                                               + path
                                               + "."
                                               + e.getMessage());
                            }

                            return FileVisitResult.CONTINUE;
                        }});

        LOGGER.info("------- Finished generating class files. -------");
    }

    private void run() throws FileAlreadyExistsException, IOException {
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
                    Path.of(cmd.getOptionValue(
                                "d", 
                                String.join(File.separator, 
                                            System.getProperty("user.home"), 
                                            "class-files"))), 
                    Path.of(args[0]));
        }
    }

    public static void main(String[] args) {
        try {
            new ClassFileGenerator(args).run();
        } catch (FileAlreadyExistsException e) {
            LOGGER.info("Terminated from generating class files." 
                        + e.getMessage());
        } catch (IOException e) {
            LOGGER.severe("Termated from generating class files."
                          + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            LOGGER.info("Failed to parse command line tokens. " 
                        + e.getMessage());
        }
    }
}
