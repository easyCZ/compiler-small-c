package sem;

import ast.*;
import ast.expressions.*;
import ast.statements.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	@Override
	public Void visitBlock(Block b) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitProcedure(Procedure p) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitProgram(Program p) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitVar(Var v) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitFunctionCallStmt(FunCallStmt funCallStmt) {
		return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral strLiteral) {
		return null;
	}

	@Override
	public Void visitWhile(While whilez) {
		return null;
	}

	@Override
	public Void visitBinOp(BinOp binOp) {
		return null;
	}

	@Override
	public Void visitIf(If anIf) {
		return null;
	}

	@Override
	public Void visitIntLiteral(IntLiteral intLiteral) {
		return null;
	}

	@Override
	public Void visitChrLiteral(ChrLiteral chrLiteral) {
		return null;
	}

	@Override
	public Void visitReturn(Return aReturn) {
		return null;
	}

	@Override
	public Void visitAssign(Assign assign) {
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr funCallExpr) {
		return null;
	}
}
