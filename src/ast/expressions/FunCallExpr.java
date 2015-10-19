package ast.expressions;

import ast.ASTVisitor;
import ast.Expr;

import java.util.Arrays;
import java.util.List;


public class FunCallExpr extends Expr {

    public final String name;
    public final List<Expr> arguments;

    public FunCallExpr(String name, List<Expr> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFunCallExpr(this);
    }

    @Override
    public String toString() {
        return "FunCallExpr(" + name + ", " + Arrays.toString(arguments.toArray()) + ")";
    }
}
