package gen.util;


import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;

public class LocalVar extends Var {

    public final int position;

    public LocalVar(int position) {
        this.position = position;
    }

    @Override
    public Void get(MethodVisitor methodVisitor) {
        methodVisitor.visitIntInsn(ILOAD, position);
        return null;
    }

    @Override
    public Void set(MethodVisitor methodVisitor) {
        methodVisitor.visitVarInsn(ISTORE, position);
        return null;
    }
}
