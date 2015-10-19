package ast.expressions;

import ast.Expr;

import java.util.LinkedList;
import java.util.List;


public class FunCallExpr {

    public final String name;
    public final List<Expr> arguments;

    public FunCallExpr(String name, LinkedList<Expr> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

}
