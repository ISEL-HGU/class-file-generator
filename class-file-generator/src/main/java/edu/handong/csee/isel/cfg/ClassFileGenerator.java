package edu.handong.csee.isel.cfg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javassist.bytecode.DuplicateMemberException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

    private List<ClassInfo> createClassInfos(File dir) throws IOException {
        JsonReader reader;
        List<ClassInfo> classInfos;

        reader = new JsonReader();
        classInfos = new ArrayList<>();

        LOGGER.info("---Start reading json files from root directory" 
                    + dir.getPath() + ".---");
                    
        Files.walkFileTree(dir.toPath(), 
                           new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(
                                        Path path, BasicFileAttributes attr) {
                                    try {
                                        classInfos.add(
                                                reader.read(path.toFile()));
                                    } catch (FileNotFoundException e) {
                                        LOGGER.info("Skip reading " 
                                                    + path.toString()
                                                    + ". It may not exist, or "
                                                    + "be opened.");    
                                    } catch (JsonSyntaxException e) {
                                        LOGGER.info("Skip reading " 
                                                    + path.toString() 
                                                    + ". " + e.getMessage());
                                    } catch (JsonIOException e) {
                                        LOGGER.warning("Unable to read "
                                                      + path.toString() 
                                                      + ". There is a problem "
                                                      + "reading from reader.");
                                    } catch (IllegalAccessException 
                                             | InvocationTargetException e) {
                                        LOGGER.severe("Failed to read " 
                                                      + path.toString()
                                                      + ". Unable to check the "
                                                      + "json file format.");
                                    } 

                                    return FileVisitResult.CONTINUE;
                                }});

        LOGGER.info("---Finished reading json files from root directory " 
                    + dir.getPath() + ".---");

        return classInfos;
    }

    private void generateClassFiles(File outDir, List<ClassInfo> classinfos) {
        LOGGER.info("---Start writing class files to " 
                    + outDir.getPath() + ".---");
            
        if (outDir.mkdirs()) {
            LOGGER.info("Creating output directory " + outDir.getPath() + ".");
        }
    
        for (ClassInfo classinfo : classinfos) {
            File outPkgDir;
            File classFile;

            outPkgDir = new File(outDir, 
                                 classinfo.getPackagename()
                                          .replace(".", File.separator));
                
            if (outPkgDir.mkdirs()) {
                LOGGER.info("Creating package directory " 
                            + outPkgDir.getPath() + ".");
            }

            classFile = new File(outPkgDir, 
                                 classinfo.getClassname() + ".class");

            try {    
                ClassWriter.write(classFile, classinfo); 
            } catch (DuplicateMemberException e) {
                LOGGER.severe("Failed to write " 
                              + classFile.getPath()
                              + ". Unable to compose ClassFile object.");
            } catch (FileNotFoundException e) {
                LOGGER.warning("Failed to write " 
                              + classFile.getPath() 
                              + ". The class file cannot be created or "
                              + "opened.");
            } catch (IOException e) {
                LOGGER.warning("Failed to write " + classFile.getPath() + ".");
            } 
        }

        LOGGER.info("---Finished writing class files to " 
                    + outDir.getPath() + ".---");
    }

    private void run() throws IOException {
        String[] args;

        args = cmd.getArgs();

        if (cmd.hasOption("h")) {
            new HelpFormatter().printHelp(
                    "cfg [options] path_to_directory", opts);
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
                    createClassInfos(new File(args[0])));
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        new ClassFileGenerator(args).run();
    }
}
