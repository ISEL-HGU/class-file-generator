package edu.handong.csee.isel.cfg;

import java.util.Arrays;

/**
 * Class that contains information for creating class file
 */
public class ClassInfo {
    public static final int BYTESTRING_RADIX = 16;
   
    private String[] byteStrings;
    private String classname;
    private String methodname;
    private String methodDesc;
    private String packagename;

    public ClassInfo(String[] byteStrings, String classname, 
                     String methodname, String methodDesc, 
                     String packagename) {
        this.byteStrings = byteStrings;
        this.classname = classname;
        this.methodname = methodname;
        this.methodDesc = methodDesc;
        this.packagename = packagename;
    }

    public String[] getByteStrings() {
        return byteStrings;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } 

        ClassInfo info = (ClassInfo) obj;
        
        if (Arrays.equals(byteStrings, info.getByteStrings())
                && classname.equals(info.getClassname())
                && methodname.equals(info.getMethodname())
                && methodDesc.equals(info.getMethodDesc())
                && packagename.equals(info.getPackagename())) {
            return true;
        } else {
            return false;
        }
    }
}
