package ast;

import ast.expressions.*;
import ast.statements.*;

public interface ASTVisitor<T> {

    T visitBlock(Block b);
    T visitProcedure(Procedure p);
    T visitProgram(Program p);
    T visitVarDecl(VarDecl vd);
    T visitVar(Var v);
    T visitFunctionCallStmt(FunCallStmt funCallStmt);
    T visitStrLiteral(StrLiteral strLiteral);
    T visitWhile(While whilez);
    T visitBinOp(BinOp binOp);
    T visitIf(If anIf);
    T visitIntLiteral(IntLiteral intLiteral);
    T visitChrLiteral(ChrLiteral chrLiteral);
    T visitReturn(Return aReturn);
    T visitAssign(Assign assign);

    T visitFunCallExpr(FunCallExpr funCallExpr);
}
