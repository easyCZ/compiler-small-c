package gen;


import org.objectweb.asm.MethodVisitor;

public class GeneratingMethodVisitor extends MethodVisitor {


    public GeneratingMethodVisitor(int i, MethodVisitor methodVisitor) {
        super(i, methodVisitor);
    }
}
