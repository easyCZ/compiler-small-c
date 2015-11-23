package gen.util;


import org.objectweb.asm.MethodVisitor;

public interface GeneratingVar {

    Void get(MethodVisitor methodVisitor);
    Void set(MethodVisitor methodVisitor);
}
