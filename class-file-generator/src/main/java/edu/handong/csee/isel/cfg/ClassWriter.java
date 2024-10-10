package edu.handong.csee.isel.cfg;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;

/**
 * Class that writes class file 
 */
public class ClassWriter {

    /**
     * Writes class file that corresponds to <code>info</code>. 
     * @param file file to be written
     * @param info class information
     * @throws DuplicateMemberException if the method of <code>info</code> is 
     *      duplicated while creating class file 
     * @throws FileNotFoundException  if the <code>file</code> exists but is a 
     *      directory rather than a regular file, does not exist but cannot be 
     *      created, or cannot be opened for any other reason
     * @throws IOException
     */
    public static void write(File file, ClassInfo info) 
            throws DuplicateMemberException, FileNotFoundException, 
                   IOException {
        final int MAX_SATCK = 64;
        final int MAX_LOCALS = 64;

        byte[] code;
        String[] byteStrings;
        Bytecode b;
        ClassFile cf; 
        ConstPool cp;
        CodeAttribute cattr;
        MethodInfo minfo;
        
        cf = new ClassFile(false, 
                           info.getPackagename() + "." + info.getClassname(), 
                           "java.lang.Object");
        cf.setAccessFlags(AccessFlag.PUBLIC);
        cp = cf.getConstPool();
        
        b = new Bytecode(cp);
        b.addAload(0);
        b.addInvokespecial("java.lang.Object", MethodInfo.nameInit, "()V");
        b.addReturn(null);
        b.setMaxLocals(1);
        minfo = new MethodInfo(cp, MethodInfo.nameInit, "()V");
        minfo.setAccessFlags(AccessFlag.PUBLIC);
        minfo.setCodeAttribute(b.toCodeAttribute());
        cf.addMethod(minfo);

        byteStrings = info.getByteStrings();
        code = new byte[byteStrings.length];

        for (int i = 0; i < code.length; i++) {
            code[i] = new BigInteger(byteStrings[i], 
                                     ClassInfo.BYTESTRING_RADIX).byteValue();
        }

        cattr = new CodeAttribute(cp, MAX_SATCK, MAX_LOCALS, 
                                  code, new ExceptionTable(cp));
        minfo = new MethodInfo(cf.getConstPool(), info.getMethodname(), 
                               info.getMethodDesc());
        minfo.setAccessFlags(AccessFlag.PUBLIC);
        minfo.setCodeAttribute(cattr);  
        cf.addMethod(minfo);
        
        cf.write(new DataOutputStream(new FileOutputStream(file)));
    }
}
