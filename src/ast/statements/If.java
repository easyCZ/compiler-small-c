package ast.statements;

import ast.ASTVisitor;
import ast.Expr;
import ast.Stmt;


public class If extends Stmt {

    public final Expr ifExpr;
    public final Stmt ifStmt;
    public final Stmt elseStmt;

    public If(Expr ifExpr, Stmt ifStatement, Stmt elseStatement) {
        this.ifExpr = ifExpr;
        this.ifStmt = ifStatement;
        this.elseStmt = elseStatement;
    }

    public boolean hasElse() {
        return elseStmt != null;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitIf(this);
    }

    @Override
    public String toString() {
        return "If(" + ifExpr + ", " + ifStmt + (elseStmt != null ? ", " + elseStmt : "") + ");";
    }
}
