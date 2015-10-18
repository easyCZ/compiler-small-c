package ast.expressions;

import ast.ASTVisitor;
import ast.Expr;

/**
 * Created by easy on 18/10/2015.
 */
public class IntLiteral extends Expr {

    public final int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIntLiteral(this);
    }
}
