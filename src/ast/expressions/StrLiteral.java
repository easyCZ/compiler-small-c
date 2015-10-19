package ast.expressions;


import ast.ASTVisitor;
import ast.Expr;

public class StrLiteral extends Expr {

    public final String string;

    public StrLiteral(String string) {
        this.string = string;
    }


    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStrLiteral(this);
    }

    @Override
    public String toString() {
        return "StrLiteral(" + string + ")";
    }
}
