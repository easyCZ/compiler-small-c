package ast.statements;


import ast.ASTVisitor;
import ast.Expr;
import ast.Stmt;

public class While extends Stmt {

    public final Expr expr;
    public final Stmt statement;

    public While(Expr expr, Stmt statement) {
        this.expr = expr;
        this.statement = statement;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitWhile(this);
    }

    @Override
    public String toString() {
        return "While(" + expr + ", " + statement + ")";
    }
}
