package gen;

import ast.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class CodeGenerator {

    private static final String FILENAME = "out/Main.class";


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
