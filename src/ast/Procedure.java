package ast;

import java.util.Arrays;
import java.util.List;

public class Procedure implements Tree {

    public final Type type;
    public final String name;
    public final List<VarDecl> params;
    public final Block block;

    public Procedure(Type type, String name, List<VarDecl> params, Block block) {
	    this.type = type;
	    this.name = name;
	    this.params = params;
	    this.block = block;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitProcedure(this);
    }

    public boolean isNative() {
        return Arrays.asList("print_s", "print_i", "print_c", "read_c", "read_i").contains(name);
    }

    @Override
    public String toString() {
        return "Procedure(" + type + ", " + name + ", " + Arrays.toString(params.toArray()) + ", " + block + ")";
    }

    public static final Procedure PRINT_S = new Procedure(
            Type.VOID, "print_s", null, null);
    public static final Procedure PRINT_C = new Procedure(Type.VOID, "print_c", null, null);
    public static final Procedure PRINT_I = new Procedure(Type.VOID, "print_i", null, null);
    public static final Procedure READ_I = new Procedure(Type.INT, "read_i", null, null);
    public static final Procedure READ_C = new Procedure(Type.VOID, "read_c", null, null);


}
