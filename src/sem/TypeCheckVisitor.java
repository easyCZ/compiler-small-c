package sem;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	private Map<String, Type> environment;

	public TypeCheckVisitor() {
		environment = new HashMap<>();
	}

	public TypeCheckVisitor(Map<String, Type> env) {
		this.environment = env;
	}

	@Override
	public Type visitBlock(Block b) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitProcedure(Procedure p) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitProgram(Program p) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitVar(Var v) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitFunctionCallStmt(FunCallStmt funCallStmt) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitStrLiteral(StrLiteral strLiteral) {
		return Type.STRING;
	}

	@Override
	public Type visitWhile(While whilez) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitBinOp(BinOp binOp) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitIf(If anIf) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitIntLiteral(IntLiteral intLiteral) {
		return Type.INT;
	}

	@Override
	public Type visitChrLiteral(ChrLiteral chrLiteral) {
		return Type.CHAR;
	}

	@Override
	public Type visitReturn(Return aReturn) {

		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitAssign(Assign assign) {
		throw new NotImplementedException();
//		return null;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr funCallExpr) {
		throw new NotImplementedException();
//		return null;
	}

}
