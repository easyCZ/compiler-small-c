package sem;

import ast.*;
import ast.expressions.*;
import ast.statements.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	@Override
	public Type visitBlock(Block b) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitProcedure(Procedure p) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitProgram(Program p) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVar(Var v) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitFunctionCallStmt(FunCallStmt funCallStmt) {
		return null;
	}

	@Override
	public Type visitStrLiteral(StrLiteral strLiteral) {
		return null;
	}

	@Override
	public Type visitWhile(While whilez) {
		return null;
	}

	@Override
	public Type visitBinOp(BinOp binOp) {
		return null;
	}

	@Override
	public Type visitIf(If anIf) {
		return null;
	}

	@Override
	public Type visitIntLiteral(IntLiteral intLiteral) {
		return null;
	}

	@Override
	public Type visitChrLiteral(ChrLiteral chrLiteral) {
		return null;
	}

	@Override
	public Type visitReturn(Return aReturn) {
		return null;
	}

	@Override
	public Type visitAssign(Assign assign) {
		return null;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr funCallExpr) {
		return null;
	}

}
