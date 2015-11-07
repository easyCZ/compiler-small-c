package ast;

import ast.expressions.Var;

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

    private static final List<VarDecl> STRING_ARGS = Arrays.asList(new VarDecl(Type.STRING, new Var("s")));
    private static final List<VarDecl> CHAR_ARGS = Arrays.asList(new VarDecl(Type.CHAR, new Var("c")));
    private static final List<VarDecl> INT_ARGS = Arrays.asList(new VarDecl(Type.INT, new Var("i")));
    private static final List<VarDecl> NO_VARDECLS = Arrays.asList();
    private static final List<Stmt> NO_STMTS = Arrays.asList();
    private static final Block EMPTY_BLOCK = new Block(NO_VARDECLS, NO_STMTS);
    public static final Procedure PRINT_S = new Procedure(
            Type.VOID, "print_s", STRING_ARGS, EMPTY_BLOCK);
    public static final Procedure PRINT_C = new Procedure(Type.VOID, "print_c", CHAR_ARGS, EMPTY_BLOCK);
    public static final Procedure PRINT_I = new Procedure(Type.VOID, "print_i", INT_ARGS, EMPTY_BLOCK);
    public static final Procedure READ_I = new Procedure(Type.INT, "read_i", NO_VARDECLS, EMPTY_BLOCK);
    public static final Procedure READ_C = new Procedure(Type.CHAR, "read_c", NO_VARDECLS, EMPTY_BLOCK);


}
