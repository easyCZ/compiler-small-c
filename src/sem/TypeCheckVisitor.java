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
		// Void is disallowed
        if (vd.type == Type.VOID) error("Variable declaration cannot be void");
        return null;
	}

	@Override
	public Type visitVar(Var v) {
		v.type = v.getVarDecl().type;
        return v.type;
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
        Type type = assign.expr.accept(this);
        Type lhsType = assign.var.getVarDecl().type;
        if (type != lhsType) {
            error(String.format(
                    "Illegal assignment of %s %s to %s",
                    assign.var.type,
                    assign.var.name,
                    type
            ));
        }
        return lhsType;
	}

	@Override
	public Type visitFunCallExpr(FunCallExpr funCallExpr) {
		Procedure definition = funCallExpr.getProcedure();

        if (funCallExpr.arguments.size() != definition.params.size()) {
            error(String.format(
                    "Function call '%s' requires %d arguments, not %d",
                    funCallExpr.name,
                    definition.params.size(),
                    funCallExpr.arguments.size()));
            // Still return the type that it should have so we can continue
            return definition.type;
        }

        for (int i = 0; i < funCallExpr.arguments.size(); i++) {
            Expr funCallArg = funCallExpr.arguments.get(i);
            VarDecl defCallArg = definition.params.get(i);

            Type argType = funCallArg.accept(this);
            Type defType = defCallArg.type;

            if (argType != defType) {
                error(String.format(
                        "Function call '%s' expected argument of type %s but got %s at position %d",
                        funCallExpr.name, defType, argType, i + 1
                ));
            }
        }

        return definition.type;
	}

}
