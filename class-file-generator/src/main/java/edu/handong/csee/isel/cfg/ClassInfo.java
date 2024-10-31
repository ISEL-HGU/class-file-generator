package edu.handong.csee.isel.cfg;

import java.lang.IllegalAccessException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;

/**
 * Class that contains information for creating class file.
 */
public class ClassInfo {  
    private static Random rnd = new Random();

    private int[] bytecode;
    private String classname;
    private String methodname;
    private String methodDesc;
    private String packagename;
    private int version;

    public ClassInfo() {
        version = 55;
        packagename = "gen";
        classname = "GeneratedClass" + rnd.nextInt();
        methodname = "solve";
        methodDesc = "()V";
        bytecode = new int[] { 177 };
    }

    public ClassInfo(int version, String packagename, String classname, 
                     String methodname, String methodDesc, int[] bytecode) {
        this.version = version;
        this.packagename = packagename;
        this.classname = classname;
        this.methodname = methodname;
        this.methodDesc = methodDesc;
        this.bytecode = bytecode;
    }

    public int[] getBytecode() {
        return bytecode;
    }

    public String getClassname() {
        return classname;
    }

    public String getMethodname() {
        return methodname;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public String getPackagename() {
        return packagename;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ClassInfo)) {
            return false;
        } 
        
        for (Field field : getClass().getDeclaredFields()) {
            try {
                Object val;
                
                val = field.get(this);

                if (val instanceof int[]) {
                    if (!Arrays.equals((int []) val, (int []) field.get(obj))) {
                        return false;
                    }
                } else {
                    if (!val.equals(field.get(obj))) {
                        return false;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();

                return false;
            }
        }
        
        return true;
    }
}
