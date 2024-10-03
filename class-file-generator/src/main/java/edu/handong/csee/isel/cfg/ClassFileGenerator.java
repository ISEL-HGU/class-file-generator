package edu.handong.csee.isel.cfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParseException;

import javassist.bytecode.DuplicateMemberException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ClassFileGenerator {

    public static void main(String[] args) {
        String[] prog_args;
        Options opts;
        CommandLine cmd = null;

        opts = defineOptions();

        try {
            cmd = new DefaultParser().parse(opts, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        prog_args = cmd.getArgs();
        
        if (cmd.hasOption("h")) {
            new HelpFormatter().printHelp(
                    "cfg [options] path_to_directory", opts);
        } else if (prog_args.length == 0) {
            System.err.println("usage: cfg [options] path_to_directory");
            System.err.println("use -h or --help to see options");
        }
        
        if (prog_args.length > 0) {
            generateClassFiles(
                    new File(cmd.getOptionValue(
                            "d", 
                            String.join(File.separator, 
                                        System.getProperty("user.home"), 
                                        "class-files"))), 
                    createClassInfos(new File(prog_args[0])));
        }
    }

    private static Options defineOptions() {
        return new Options().addOption(Option.builder("d")
                                              .longOpt("directory")
                                              .hasArg()
                                              .argName("output_directory")
                                              .desc("set output directory " + 
                                                    "that generated class " + 
                                                    "files to be saved")
                                              .build())
                            .addOption(Option.builder("h")
                                            .longOpt("help")
                                            .desc("print this message")
                                            .build());
    }

    private static List<ClassInfo> createClassInfos(File dir) {
        File[] files;
        JsonReader jr;
        List<ClassInfo> classInfos;

        files = dir.listFiles();
        jr = new JsonReader();
        classInfos = new ArrayList<>();

        for (File file : files) {
            try {
                classInfos.add(jr.read(file));
            } catch (IOException | JsonParseException e) {
                e.printStackTrace();    
            }
        }

        return classInfos;
    }

    private static void generateClassFiles(File outDir, 
                                           List<ClassInfo> classinfos) {

        outDir.mkdirs();
    
        for (ClassInfo classinfo : classinfos) {
            try {
                File pkgDir;
                
                pkgDir = new File(outDir, 
                                  classinfo.getPackagename()
                                           .replace(".", File.separator));
                pkgDir.mkdirs();
                
                ClassWriter.write(
                        new File(pkgDir, classinfo.getClassname() + ".class"), 
                        classinfo);
            } catch (IOException | DuplicateMemberException e) {
                e.printStackTrace();
            }
        }
    }
}
