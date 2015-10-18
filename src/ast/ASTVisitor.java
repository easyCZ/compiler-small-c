package ast;

import ast.expressions.Var;
import ast.statements.FunCallStmt;
import ast.statements.If;
import ast.statements.While;

public interface ASTVisitor<T> {

    public T visitBlock(Block b);
    public T visitProcedure(Procedure p);
    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    public T visitVar(Var v);
    public T visitFunctionCallStmt(FunCallStmt funCallStmt);

    public T visitStrLiteral(StrLiteral strLiteral);

    T visitWhile(While whilez);

    T visitBinOp(BinOp binOp);

    T visitIf(If anIf);

    // to complete ... (should have one visit method for each concrete AST node class)
}
