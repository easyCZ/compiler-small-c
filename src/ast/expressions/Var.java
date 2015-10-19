package ast.expressions;

import ast.ASTVisitor;
import ast.Expr;

public class Var extends Expr {

    public final String name;
    
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
}
