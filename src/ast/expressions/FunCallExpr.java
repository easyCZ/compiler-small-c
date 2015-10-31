package ast.expressions;

import ast.ASTVisitor;
import ast.Expr;
import ast.Procedure;

import java.util.Arrays;
import java.util.List;


public class FunCallExpr extends Expr {

    public final String name;
    public final List<Expr> arguments;
    private Procedure procedure;

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

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }
}
