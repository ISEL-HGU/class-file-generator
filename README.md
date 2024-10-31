# ClassFileGenerator

ClassFileGenerator is simple program that generates Java Virtual Machine (JVM) class files. It takes root source  
directory that contains formatted Json files as an argument and saves desired class files to destination directory.  

</br>

## 1. Requirements

- Java 11 >=

</br>

## 2. Building

ClassFileGenerator uses Maven to build the project.  

```console
mvn package
```

</br>

## 3. Environment Setting

ClassFileGenerator uses script to run the application. Path to the script needs to be appended to environment variable  
`Path` in order to execute the script in any location.

### Linux

The following command sets environment variables temporally. To set them permanently, type the command to `~/.bashrc`.

```bash
PATH=$PATH:path/to/class-file-generator-repo/class-file-generator/target/appassembler/bin
```

### Windows

The following command sets environment variables permanently.

```powershell
[Environment]::SetEnvironmentVariable('PATH', [Environment]::GetEnvironmentVariable('PATH', 'Machine') + ';path\to\class-file-generator-repo\class-file-generator\target\appassembler\bin', 'Machine')
```

</br>

## 4. Usage

After [building](#2-building) and [setting environment](#3-environment-setting), run the application with the following command.

```console
cfg [options] path_to_src_directory
```

`path_to_src_directory` denotes absolute or relative path of the root source directory.

### Options

- -d, --directory <output_directory>: Set output directory. <output_directory> can be both absolute and relative  
  path of the output directory. If this option is not specified, output class files are saved in `~/class-files`.

- -h, --help: Print help message.

</br>

## 5. Json File Format

Json files in the root source directory must follow the following format. If elements of the following Json file format  
are not provided, default values are applied.

```json
{
    "version" : VERSION_NUMBER,
    "packagename": "package name",
    "classname": "class name",
    "methodname": "method name",
    "methodDesc": "method description",
    "bytecode": [ OPCODE, OPERAND, OPCODE, ... ]
}

```

- `version`: Integer value that denotes the major version of generating class file. It follows Java Virtual Machine  
  Specification (JVMS) (e.g., major version of Java 6 is 50, Java 7 is 51, and so on). The default value is `55`.

- `packagename`: String value that denotes the package name of generating class file. The name of the package can  
  be seperated with either '.' or '/'. The default value is `"gen"`.
  
- `classname`: String value that denotes the name of generating class file. The default value is  
  `"GeneratedClass<randomInteger>"`.

- `methodname`: String value that denotes the method name of generating class file. The default value is `"solve"`.
  
- `methodDesc`: String value that denotes the method description of generating class file. It follow JVMS (e.g.,  
  method description of `int method(int, int)` is (II)I). The default value is `"()V"`.

- `bytecode`: Integer array that denotes the bytecode of generating class file. It follows JVMS (e.g., opcode value of  
  ALOAD is 21 and IADD is 96), but [partial opcodes](#supported-opcodes) are supported. The default value is `[ 177 ]`.

### Example

The following json file generates the following class file that represents the following java source code.

### Json File

```json
{
    "version": 55, 
    "packagename": "edu.handong.csee.isel.cfg.gen",
    "classname": "Add", 
    "methodname": "add",
    "methodDesc": "(II)I",
    "bytecode": [ 21, 1, 21, 2, 96, 172 ]
}

```

### Class File

```class
CA FE BA BE 00 00 00 37 00 0C 01 00 21 65 64 75  
2F 68 61 6E 64 6F 6E 67 2F 63 73 65 65 2F 69 73  
65 6C 2F 63 66 67 2F 67 65 6E 2F 41 64 64 07 00  
01 01 00 10 6A 61 76 61 2F 6C 61 6E 67 2F 4F 62  
6A 65 63 74 07 00 03 01 00 06 3C 69 6E 69 74 3E  
01 00 03 28 29 56 0C 00 05 00 06 0A 00 04 00 07  
01 00 03 61 64 64 01 00 05 28 49 49 29 49 01 00  
04 43 6F 64 65 00 01 00 02 00 04 00 00 00 00 00  
02 00 01 00 05 00 06 00 01 00 0B 00 00 00 11 00  
01 00 01 00 00 00 05 2A B7 00 08 B1 00 00 00 00  
00 01 00 09 00 0A 00 01 00 0B 00 00 00 10 00 02 
00 03 00 00 00 04 1B 1C 60 AC 00 00 00 00 00 00 

```

### Java Source Code

```java
package edu.handong.csee.isel.cfg.gen;

public class Add {

    public int add(int a, int b) {
        return a + b;
    }
}

```

</br>

## 6. Limitations

The generated class files do not have fields. Also, they have default constructor and one other method only. The  
source json file must not contain unsupported opcodes in `bytecode` array.

### Supported Opcodes

Only JVM opcodes that do not use constant pool are supported. The supported opcodes can be divided into no  
argument opcodes, unary opcodes, jump opcodes, and variable opcodes.

- No Argument Opcodes: Opcodes that have no argument.  

- Unary Opcodes: Opcodes that have one argument. Opcodes that load and store the local variables also have one  
  argument, but they are considered as variable opcodes instead.

- Jump Opcodes: Opcodes that jumps to an other instruction.

- Variable Opcodes: Opcodes that load and store the local variables.

| No Argument Opcodes | Unary Opcodes | Jump Opcodes | Variable Opcodes |
| :-----------------: | :-----------: | :----------: | :--------------: |
| NOP, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, FCONST_0, FCONST_1, FCONST_2, IALOAD, FALOAD, IASTORE, FASTORE, POP, DUP, IADD, FADD, ISUB, FSUB, IMUL, FMUL, IDIV, FDIV, IREM, FREM, IRETURN, FRETURN, ARETURN, RETURN, ARRAYLENGTH | NEWARRAY | IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, GOTO | ILOAD, FALOAD, ALOAD, ISTORE, FSTORE, ASTORE |  
