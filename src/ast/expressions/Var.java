package ast.expressions;

import ast.ASTVisitor;
import ast.Expr;
import ast.VarDecl;

public class Var extends Expr {

    public final String name;
    private VarDecl varDecl;
    
    public Var(String name){
	    this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitVar(this);
    }

    @Override
    public String toString() {
        return "Var(" + name + ")";
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public void setVarDecl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }
}
