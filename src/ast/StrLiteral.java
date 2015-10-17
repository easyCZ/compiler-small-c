package ast;


public class StrLiteral extends Expr {

    public final String string;

    public StrLiteral(String string) {
        this.string = string;
    }


    @Override
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStrLiteral(this);
    }
}
