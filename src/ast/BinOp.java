package ast;


public class BinOp extends Expr {

    public final Expr lhs;
    public final Op op;
    public final Expr rhs;

    public BinOp(Expr lhs, Op op, Expr rhs) {
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBinOp(this);
    }

    @Override
    public String toString() {
        return "BinOp(" + lhs + ", " + op + ", " + rhs + ")";
    }
}
