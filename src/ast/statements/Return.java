package ast.statements;

import ast.ASTVisitor;
import ast.Expr;
import ast.Stmt;


public class Return extends Stmt {

    public final Expr returnz;

    public Return(Expr returnz) {
        this.returnz = returnz;
    }

    public boolean hasReturn() {
        return returnz != null;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitReturn(this);
    }

    @Override
    public String toString() {
        return "Expr(" + (hasReturn() ? returnz : "") + ")";
    }
}
