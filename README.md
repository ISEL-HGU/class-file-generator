# ClassFileGenerator
ClassFileGenerator is simple program that generates Java Virtual Machine (JVM) 
class files. It gets source directory that contains formatted Json files as an 
input and outputs desired class files to destination directory.  

## 1. Requirements

## 2. Building
ClassFileGenerator uses Maven to build the project.  
```console
mvn install
```

## 3. Environment Setting
ClassFileGenerator uses script to run the application. Path to the script 
needs to be appended to environment variable <code>Path</code> in order to 
execute the script in any location. Additionally, path to the local maven 
repository need to be set to environment variable <code>MAVEN_LOCAL</code> in
order to execute the script.

### Linux:
```bash
PATH=$PATH:path/to/repo/scripts
```

### Windows:
```powershell
[Environment]::SetEnvironmentVariable('PATH', [Environment]::GetEnvironmentVariable('PATH', 'Machine') + ';path\to\class-file-generator\scripts', 'Machine')

[Environment]::SetEnvironmentVariable('MAVEN_LOCAL', 'path\to\maven\local\repo', 'Machine')
```

## 4. Usage
After [building](#2-building) and 
[setting environment](#3-environment-setting), run the application with 
following commands.

### Linux:
```bash
cfg [options] path_to_directory
```

### Windows:
```powershell
cfg [options] path_to_directory 
```

<code>path_to_directory</code> denotes path of the source directory. To see 
<code>options</code>, use <code>-h</code> or <code>--help</code>.

## 5. Json File Format
Json files in the source directory must follow following format.
```
{
    pacakagename: "package_name",
    classname: "class_name",
    methodname: "method_name"
    methodDesc: "method_description"
    byteStrings: ["hex_string", ...]
}
```

