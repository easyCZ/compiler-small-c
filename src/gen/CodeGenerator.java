package gen;

import ast.Program;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class CodeGenerator {

    private static final String FILENAME = "out/Main.class";
    private static final String IO_LIB = "lib/IO.class";
    private static final String IO_OUT = "out/IO.class";


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

        // Copy IO.class from lib to out
        InputStream inStream = null;
        OutputStream outStream = null;

        try{

            File in = new File(IO_LIB);
            File out = new File(IO_OUT);

            inStream = new FileInputStream(in);
            outStream = new FileOutputStream(out);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0){

                outStream.write(buffer, 0, length);

            }

            inStream.close();
            outStream.close();

            System.out.println(String.format("Copied %s to %s", IO_LIB, IO_OUT));

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void write(byte[] data) throws IOException {
        Files.write(Paths.get(FILENAME), data);
    }
}
