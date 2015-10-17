package ast;

import ast.expressions.Var;
import ast.statements.FunCallStmt;

public interface ASTVisitor<T> {

    public T visitBlock(Block b);
    public T visitProcedure(Procedure p);
    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    public T visitVar(Var v);
    public T visitFunctionCallStmt(FunCallStmt funCallStmt);

    // to complete ... (should have one visit method for each concrete AST node class)
}
