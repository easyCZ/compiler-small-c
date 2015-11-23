package gen.util;


import ast.VarDecl;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class StaticVar extends Var {

    private static final String MAIN = "Main";
    private static final String INTEGER = "I";
    private VarDecl varDecl;

    public StaticVar(VarDecl varDecl) {
        this.varDecl = varDecl;
    }

    @Override
    public Void get(MethodVisitor methodVisitor) {
        methodVisitor.visitFieldInsn(
                Opcodes.GETSTATIC,
                MAIN,
                varDecl.var.name,
                INTEGER);
        return null;
    }

    @Override
    public Void set(MethodVisitor methodVisitor) {
        methodVisitor.visitFieldInsn(
                Opcodes.PUTSTATIC,
                MAIN,
                varDecl.var.name,
                INTEGER);
        return null;
    }
}
