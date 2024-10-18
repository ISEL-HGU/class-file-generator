package edu.handong.csee.isel.cfg;

import java.lang.IllegalAccessException;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Class that contains information for creating class file.
 */
public class ClassInfo {  
    private int[] bytecode;
    private String classname;
    private String methodname;
    private String methodDesc;
    private String packagename;
    private int version;

    public ClassInfo(int[] bytecode, String classname, 
                     String methodname, String methodDesc, 
                     String packagename, int version) {
        this.bytecode = bytecode;
        this.classname = classname;
        this.methodname = methodname;
        this.methodDesc = methodDesc;
        this.packagename = packagename;
        this.version = version;
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
                Object val = field.get(this);

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
            }
        }
        
        return true;
    }
}
