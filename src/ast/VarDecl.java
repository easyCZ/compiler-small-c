package ast;

import ast.expressions.Var;

public class VarDecl implements Tree {

    public final Type type;
    public final Var var;

    public VarDecl(Type type, Var var) {
        this.type = type;
        this.var = var;
    }

     public <T> T accept(ASTVisitor<T> v) {
	return v.visitVarDecl(this);
    }

    @Override
    public String toString() {
        return "VarDecl(" + type + ", " + var + ")";
    }
}
