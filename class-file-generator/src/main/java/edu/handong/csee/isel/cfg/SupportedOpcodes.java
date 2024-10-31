package edu.handong.csee.isel.cfg;

import org.objectweb.asm.Opcodes;

/**
 * The supported Java Virtual Machine (JVM) opcodes. The supported JVM opcodes 
 * only include opcodes that does not use JVM constant pool.  
 */
public class SupportedOpcodes {
    private static int[] noArgOpcodes 
            = { Opcodes.NOP, Opcodes.ICONST_M1, Opcodes.ICONST_0, 
                Opcodes.ICONST_1, Opcodes.ICONST_2, Opcodes.ICONST_3, 
                Opcodes.ICONST_4, Opcodes.ICONST_5, Opcodes.FCONST_0, 
                Opcodes.FCONST_1, Opcodes.FCONST_2, Opcodes.IALOAD, 
                Opcodes.FALOAD, Opcodes.IASTORE, Opcodes.FASTORE, 
                Opcodes.POP, Opcodes.DUP, Opcodes.IADD, 
                Opcodes.FADD, Opcodes.ISUB, Opcodes.FSUB, 
                Opcodes.IMUL, Opcodes.FMUL, Opcodes.IDIV, 
                Opcodes.FDIV, Opcodes.IREM, Opcodes.FREM, 
                Opcodes.IRETURN, Opcodes.FRETURN, Opcodes.ARETURN, 
                Opcodes.RETURN, Opcodes.ARRAYLENGTH };
    private static int[]  unaryOpcodes = { Opcodes.NEWARRAY };
    private static int[] jumpOpcodes
            = { Opcodes.IFEQ, Opcodes.IFNE, Opcodes.IFLT, 
                Opcodes.IFGE, Opcodes.IFGT, Opcodes.IFLE, 
                Opcodes.GOTO };
    private static int[] varOpcodes 
            = { Opcodes.ILOAD, Opcodes.FALOAD, Opcodes.ALOAD, 
                Opcodes.ISTORE, Opcodes.FSTORE, Opcodes.ASTORE };
    
    /**
     * Checks if <code>opcode</code> is a supported no argument opcode.
     * <br></br>
     * To see the full list of supported no argument opcodes, check README of 
     * the github repository.
     * @param opcode opcode to be checked
     * @return <code>true</code> if <code>opcode</code> is a supported no 
     *      argument opcode, <code>false</code> otherwise
     * @see https://github.com/ISEL-HGU/class-file-generator
     */
    public static boolean isSupportedNoArgOpcode(int opcode) {
        for (int noArgOpcode : noArgOpcodes) {
            if (opcode == noArgOpcode) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if <code>opcode</code> is a supported unary opcode.
     * <br></br>
     * Opcodes that load and store the local variables are considered as 
     * variable opcode instead. To see the full list of supported unary  
     * opcodes, check README of the github repository.
     * @param opcode opcode to be checked
     * @return <code>true</code> if <code>opcode</code> is a supported unary 
     *      opcode, <code>false</code> otherwise
     * @see https://github.com/ISEL-HGU/class-file-generator
     */
    public static boolean isSupportedUnaryOpcode(int opcode) {
        for (int unaryOpcode : unaryOpcodes) {
            if (opcode == unaryOpcode) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if <code>opcode</code> is a supported jump opcode.
     * <br></br>
     * To see the full list of supported jump opcodes, check README of the 
     * github repository.
     * @param opcode opcode to be checked
     * @return <code>true</code> if <code>opcode</code> is a supported jump  
     *      opcode, <code>false</code> otherwise
     * @see https://github.com/ISEL-HGU/class-file-generator
     */
    public static boolean isSupportedJumpOpcode(int opcode) {
        for (int jumpOpcode : jumpOpcodes) {
            if (opcode == jumpOpcode) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Checks if <code>opcode</code> is a supported variable opcode.
     * <br></br>
     * To see the full list of supported variable opcodes, check README of the 
     * github repository.
     * @param opcode opcode to be checked
     * @return <code>true</code> if <code>opcode</code> is a supported variable 
     *      opcode, <code>false</code> otherwise
     * @see https://github.com/ISEL-HGU/class-file-generator
     */
    public static boolean isSupportedVarOpcode(int opcode) {
        for (int varOpcode : varOpcodes) {
            if (opcode == varOpcode) {
                return true;
            }
        }

        return false;
    }
}
