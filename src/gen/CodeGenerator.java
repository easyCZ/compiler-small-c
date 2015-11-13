package gen;

import ast.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class CodeGenerator {

    private static final String FILENAME = "out/Main.class";

    // Main Class
    private static final String CLASS_NAME = "Main";
    private static final String MAIN_METHOD = "main";

    private static final int PUBLIC = ACC_PUBLIC;
    private static final int PUBLIC_STATIC = ACC_PUBLIC + ACC_STATIC;

    public void emitProgram(Program program) {
        // TODO: emit a java class named Main that contains your program and write it to the file out/Main.class
        // to do this you will need to write a visitor which traverses the AST and emit the different global variables as static fields and the different procedures as static methods.

        GeneratingClassWriter cw = new GeneratingClassWriter();
        cw.visitProgram(program);

        byte[] bytes = cw.toByteArray();

        try {
            write(bytes);
        } catch (IOException e) {
            System.err.println("Failed to write file. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void write(byte[] data) throws IOException {
        Files.write(Paths.get(FILENAME), data);
    }
}
