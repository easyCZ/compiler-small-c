package ast.statements;


import ast.ASTVisitor;
import ast.Expr;
import ast.Stmt;

import java.util.Arrays;
import java.util.List;

public class FunCallStmt extends Stmt {

    public final String name;
    public final List<Expr> arguments;

    public FunCallStmt(String name, List<Expr> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitFunctionCallStmt(this);
    }

    @Override
    public String toString() {
        return "FunCallStmt(" + name + ", " + Arrays.toString(arguments.toArray()) + ")";
    }
}
