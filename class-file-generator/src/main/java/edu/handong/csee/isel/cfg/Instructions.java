package edu.handong.csee.isel.cfg;

import org.objectweb.asm.Opcodes;

public class Instructions {
    private static int[] insns = { 
            Opcodes.NOP, Opcodes.ICONST_M1, Opcodes.ICONST_0, Opcodes.ICONST_1, 
            Opcodes.ICONST_2, Opcodes.ICONST_3, Opcodes.ICONST_4, 
            Opcodes.ICONST_5, Opcodes.FCONST_0, Opcodes.FCONST_1, 
            Opcodes.FCONST_2, Opcodes.IALOAD, Opcodes.FALOAD, Opcodes.IASTORE, 
            Opcodes.FASTORE, Opcodes.POP, Opcodes.DUP, Opcodes.IADD, 
            Opcodes.FADD, Opcodes.ISUB, Opcodes.FSUB, Opcodes.IMUL, 
            Opcodes.FMUL, Opcodes.IDIV, Opcodes.FDIV, Opcodes.IREM, 
            Opcodes.FREM, Opcodes.IRETURN, Opcodes.FRETURN, Opcodes.ARETURN, 
            Opcodes.ARRAYLENGTH };
    private static int[] intInsns = { Opcodes.NEWARRAY };
    private static int[] jumpInsns = { 
            Opcodes.IFEQ, Opcodes.IFNE, Opcodes.IFLT, Opcodes.IFGE, 
            Opcodes.IFGT, Opcodes.IFLE, Opcodes.GOTO };
    private static int[] varInsns = { 
            Opcodes.ILOAD, Opcodes.FALOAD, Opcodes.ALOAD, Opcodes.ISTORE, 
            Opcodes.FSTORE, Opcodes.ASTORE };

    public static boolean isInsn(int opcode) {
        for (int insn : insns) {
            if (opcode == insn) {
                return true;
            }
        }

        return false;
    }

    public static boolean isIntInsn(int opcode) {
        for (int intInsn : intInsns) {
            if (opcode == intInsn) {
                return true;
            }
        }

        return false;
    }

    public static boolean isJumpInsn(int opcode) {
        for (int jumpInsn : jumpInsns) {
            if (opcode == jumpInsn) {
                return true;
            }
        }

        return false;
    }
    
    public static boolean isVarInsn(int opcode) {
        for (int varInsn : varInsns) {
            if (opcode == varInsn) {
                return true;
            }
        }

        return false;
    }
}
