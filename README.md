# ClassFileGenerator
ClassFileGenerator is simple program that generates Java Virtual Machine (JVM) 
class files. It gets source directory that contains formatted Json files as an 
input and outputs desired class files to destination directory.  

## 1. Requirements

## 2. Building
ClassFileGenerator uses Maven to build the project.  
```console
cfg:~$ mvn 
```

## 3. Environment Setting
ClassFileGenerator uses script to execute the application. In order to execute 
script in any location, path to the script needs to be set.

### Linux:
```console
cfg:~$ PATH=$PATH:path/to/repository/scripts
```

### Windows:
```console
cfg:~$ setx /M "%path%;path\to\repository\scripts"
```

## 4. Usage
After [building](#2-building) and 
[setting environment](#3-environment-setting), use the application with 
following commands.

### Linux:
```console
cfg:~$ cfg.sh [options] path_to_directory
```

### Windows:
```console
cfg:~$ cfg.bat [options] path_to_directory 
```

<code>path_to_directory</code> denotes path of the source directory. To see 
<code>options</code>, use <code>-h</code> or <code>--help</code>.

## 5. Json File Format
Json files in the source directory must follow following format.
```Json
{
    pacakagename: "package_name",
    classname: "class_name",
    methodname: "method_name"
    methodDesc: "method_description"
    byteStrings: ["hex_string", ...]
}
```

