package parser.wrappers;


import ast.Expr;
import ast.Op;

public class OpExpr {

    public final Op op;
    public final Expr expr;

    public OpExpr(Op op, Expr expr) {
        this.op = op;
        this.expr = expr;
    }

}
