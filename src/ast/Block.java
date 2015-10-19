package ast;

import java.util.Arrays;
import java.util.List;

public class Block extends Stmt {

    public final List<VarDecl> varDecls;
    public final List<Stmt> statements;

    public Block(List<VarDecl> varDecls, List<Stmt> statements) {
        this.varDecls = varDecls;
        this.statements = statements;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }

    @Override
    public String toString() {
        return "Block(" + Arrays.toString(varDecls.toArray()) + ", " + Arrays.toString(statements.toArray()) + ")";
    }
}
