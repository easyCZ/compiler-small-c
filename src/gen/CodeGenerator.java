package gen;

import ast.Program;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getInternalName;

public class CodeGenerator {

    private static final String FILENAME = "out/Main.class";

    // Main Class
    private static final String CLASS_NAME = "Main";
    private static final String MAIN_METHOD = "main";

    private static final int PUBLIC = ACC_PUBLIC;
    private static final int PUBLIC_STATIC = ACC_PUBLIC + ACC_STATIC;

    public void emitProgram(Program program) {
//        ClassWriter cw = new ClassWriter(0);

        // Create class
//        cw.visit(V1_7, PUBLIC, CLASS_NAME, null, getInternalName(Object.class), null);
//
//
//        Method mainMethod = Method.getMethod("void main (String[])");
//        GeneratorAdapter main = new GeneratorAdapter(PUBLIC_STATIC, mainMethod, null, null, cw);
//
//        // Create main
//        cw.visitMethod(PUBLIC_STATIC, MAIN_METHOD, "([Ljava/lang/String;)V", null, null);


//        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", "I",
//                null, new Integer(-1)).visitEnd();
//        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "EQUAL", "I",
//                null, new Integer(0)).visitEnd();
//        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREATER", "I",
//                null, new Integer(1)).visitEnd();

//        cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo",
//                "(Ljava/lang/Object;)I", null, null).visitEnd();



        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(V1_6,
                ACC_PUBLIC,
                CLASS_NAME,
                null,
                getInternalName(Object.class),
                null);

//        MethodVisitor constructor = classWriter.visitMethod(
//                ACC_PUBLIC,
//                "<init>",
//                "()V",
//                null,
//                null);
//        constructor.visitVarInsn(ALOAD, 0);
//        constructor.visitMethodInsn(INVOKESPECIAL, getInternalName(Object.class), "<init>", "()V");
//        constructor.visitInsn(RETURN);
//        constructor.visitMaxs(1, 1);
//        constructor.visitEnd();

        MethodVisitor main = classWriter.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );

//        main.visitFieldInsn(GETSTATIC, getInternalName(System.class), "out", getDescriptor(PrintStream.class));
//        main.visitLdcInsn("Test");
//        main.visitMethodInsn(INVOKEVIRTUAL, getInternalName(PrintStream.class), "println", "(Ljava/lang/String;)V");
        main.visitInsn(RETURN);
//        main.visitMaxs(2, 2);
//        main.visitEnd();








        classWriter.visitEnd();
        byte[] b = classWriter.toByteArray();






        // TODO: emit a java class named Main that contains your program and write it to the file out/Main.class
        // to do this you will need to write a visitor which traverses the AST and emit the different global variables as static fields and the different procedures as static methods.

        try {
            write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(byte[] data) throws IOException {
        Files.write(Paths.get(FILENAME), data);
    }
}
