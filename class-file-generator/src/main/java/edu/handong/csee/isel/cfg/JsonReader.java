package edu.handong.csee.isel.cfg;

import java.io.InputStreamReader;
import java.io.InputStream;

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
    private static Gson gson = new Gson();

    private ClassInfo info;

    /**
     * Constructs a <code>JsonReader</code> object by reading 
     * <code>jsonFile</code>.
     * 
     * @param inputStream an input stream of json file to be read
     * @throws JsonIOException if there was a problem reading from the Reader
     * @throws JsonSyntaxException if the json file contains 
     *      malformed json element, or is not a valid representation for an 
     *      object of <code>ClassInfo</code>
     */
    public JsonReader(InputStream inputStream) 
            throws JsonIOException, JsonSyntaxException {
        info = gson.fromJson(new InputStreamReader(inputStream), 
                             ClassInfo.class);
    }

    /**
     * Makes <code>visitor</code> visit JVMS class file structure according to 
     * json file passed to the constructor of this <code>JsonReader</code> 
     * instance.
     * 
     * @param visitor <code>ClassWriter</code> to visit JVMS class file 
     *      structure.
     * @throws UnsupportedOpcodeException if unsupported opcode is used in the 
     *      json file
     */
    public void accept(ClassWriter visitor) throws UnsupportedOpcodeException {
        Label[] labels;
        int[] code;
        MethodVisitor mv;       
        
        visitor.visit(info.getVersion(), 
                      Opcodes.ACC_PUBLIC, 
                      info.getPackagename()
                          .replace('.', '/') 
                          + '/' 
                          + info.getClassname(), 
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
            if (SupportedOpcodes.isSupportedJumpOpcode(code[i])) {
                labels[(code[++i] << 8) + code[++i]] = new Label();
            }
        }

        for (int i = 0; i < code.length; i++) {
            if (labels[i] != null) {
                mv.visitLabel(labels[i]);
            }

            if (SupportedOpcodes.isSupportedNoArgOpcode(code[i])) {
                mv.visitInsn(code[i]);
            } else if (SupportedOpcodes.isSupportedUnaryOpcode(code[i])) {
                mv.visitIntInsn(code[i], code[++i]);
            } else if (SupportedOpcodes.isSupportedJumpOpcode(code[i])) {
                mv.visitJumpInsn(code[i], labels[(code[++i] << 8) + code[++i]]);
            } else if (SupportedOpcodes.isSupportedVarOpcode(code[i])) {
                mv.visitVarInsn(code[i], code[++i]);
            } else {
                throw new UnsupportedOpcodeException(
                        "Unsupported opcode " + code[i] + " is used.");
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
