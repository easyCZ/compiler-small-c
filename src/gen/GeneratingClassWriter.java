package gen;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import org.objectweb.asm.ClassWriter;


public class GeneratingClassWriter extends ClassWriter implements ASTVisitor {

    private final Program program;

    public GeneratingClassWriter(Program program) {
        super(COMPUTE_MAXS);
        this.program = program;
    }

    @Override
    public Object visitBlock(Block b) {
        return null;
    }

    @Override
    public Object visitProcedure(Procedure p) {
        return null;
    }

    @Override
    public Object visitProgram(Program p) {
        return null;
    }

    @Override
    public Object visitVarDecl(VarDecl vd) {
        return null;
    }

    @Override
    public Object visitVar(Var v) {
        return null;
    }

    @Override
    public Object visitFunctionCallStmt(FunCallStmt funCallStmt) {
        return null;
    }

    @Override
    public Object visitStrLiteral(StrLiteral strLiteral) {
        return null;
    }

    @Override
    public Object visitWhile(While whilez) {
        return null;
    }

    @Override
    public Object visitBinOp(BinOp binOp) {
        return null;
    }

    @Override
    public Object visitIf(If anIf) {
        return null;
    }

    @Override
    public Object visitIntLiteral(IntLiteral intLiteral) {
        return null;
    }

    @Override
    public Object visitChrLiteral(ChrLiteral chrLiteral) {
        return null;
    }

    @Override
    public Object visitReturn(Return aReturn) {
        return null;
    }

    @Override
    public Object visitAssign(Assign assign) {
        return null;
    }

    @Override
    public Object visitFunCallExpr(FunCallExpr funCallExpr) {
        return null;
    }
}
