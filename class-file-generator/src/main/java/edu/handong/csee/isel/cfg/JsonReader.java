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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A parser to make a <code>ClassWriter</code> visit a ClassFile structure, 
 * as defined in the Java Virtual Machine Specification (JVMS). 
 * <br></br>
 * This class parses the Json file content and calls the appropriate visit 
 * methods of a given <code>ClassWriter</code> for a method and bytecode 
 * instructions encountered. The Json file should follow the desired format and 
 * specification described in README of the github repository.
 * 
 * @see https://github.com/ISEL-HGU/class-file-generator
 */
public class JsonReader {
    private ClassInfo info;

    /**
     * Constructs a <code>JsonReader</code> object by reading 
     * <code>jsonFile</code>.
     * 
     * @param jsonFile json file to read
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
    public JsonReader(File jsonFile) 
            throws FileNotFoundException, JsonSyntaxException, 
                   JsonIOException, IllegalAccessException,
                   InvocationTargetException {
        try (BufferedReader br 
                = new BufferedReader(new FileReader(jsonFile))) {
            if (!jsonFile.getPath().endsWith(".json")) {
                throw new JsonSyntaxException("The file is not a json file.");
            }

            info = new Gson().fromJson(br, ClassInfo.class);

            for (Method method : info.getClass().getMethods()) {
                if (method.getName().startsWith("get") 
                        && method.invoke(info) == null) { 
                    throw new JsonSyntaxException(
                            "The json file does not follow the expected "
                            + "format.");
                }
            }
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw (FileNotFoundException) e;
            }

            Logger.getLogger(getClass().getName())
                  .warning("Cannot close the reader after reading " 
                           + jsonFile + ".");
        }
    }

    /**
     * Makes <code>visitor</code> visit JVMS class file structure according to 
     * json file passed to the constructor of this <code>JsonReader</code> 
     * instance.
     * 
     * @param visitor <code>ClassWriter</code> to visit JVMS class file 
     *      structure.
     * @throws UnsupportedBytecodeException if unsupported bytecode is 
     *      used in the json file
     */
    public void accept(ClassWriter visitor) 
            throws UnsupportedBytecodeException {
        Label[] labels;
        int[] code;
        MethodVisitor mv;       
        
        visitor.visit(info.getVersion(), 
                      Opcodes.ACC_PUBLIC, 
                      info.getPackagename()
                          .replace('.', '/') + '/' + info.getClassname(), 
                      null,
                      Type.getInternalName(Object.class), 
                      null);
        mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, 
                                 "<init>", 
                                 "()V", 
                                 null, 
                                 null);
        
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, 
                           Type.getInternalName(Object.class), 
                           "<init>", 
                           "()V", 
                           false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        
        mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, 
                                 info.getMethodname(), 
                                 info.getMethodDesc(), 
                                 null, 
                                 null);
        
        mv.visitCode();

        code = info.getBytecode();      
        labels = new Label[code.length];

        for (int i = 0; i < labels.length; i++) {
            if (Instructions.isJumpInsn(code[i])) {
                labels[(code[++i] << 8) + code[++i]] = new Label();
            }
        }

        for (int i = 0; i < code.length; i++) {
            if (labels[i] != null) {
                mv.visitLabel(labels[i]);
            }

            if (Instructions.isInsn(code[i])) {
                mv.visitInsn(code[i]);
            } else if (Instructions.isIntInsn(code[i])) {
                mv.visitIntInsn(code[i], code[++i]);
            } else if (Instructions.isJumpInsn(code[i])) {
                mv.visitJumpInsn(code[i], labels[(code[++i] << 8) + code[++i]]);
            } else if (Instructions.isVarInsn(code[i])) {
                mv.visitVarInsn(code[i], code[++i]);
            } else {
                throw new UnsupportedBytecodeException(
                        "Unsupported bytecode " + code[i] + " is used.");
            }
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        visitor.visitEnd();
    }

    /**
     * Reads classname element of this <code>JsonReader</code>.
     * 
     * @return the classname element
     */
    public String readClassname() {
        return info.getClassname();
    }

    /**
     * Reads packagename element of this <code>JsonReader</code>.
     * 
     * @return the packagename element
     */
    public String readPackagename() {
        return info.getPackagename();
    }
}
