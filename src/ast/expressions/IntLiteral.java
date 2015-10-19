package ast.expressions;

import ast.ASTVisitor;
import ast.Expr;


public class IntLiteral extends Expr {

    public final int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIntLiteral(this);
    }

    @Override
    public String toString() {
        return "IntLiteral(" + value + ")";
    }
}
