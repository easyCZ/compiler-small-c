package sem.type;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import sem.BaseSemanticVisitor;

import java.util.Arrays;
import java.util.List;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

    public Type procedureType;
    public String procedureName;

	private static final List<Op> ARITHMETIC_OPS = Arrays.asList(Op.ADD, Op.SUB, Op.MUL, Op.DIV, Op.MOD);

	@Override
	public Type visitBlock(Block b) {
        // Returns the type of the latest statement
        for (VarDecl varDecl : b.varDecls)
            varDecl.accept(this);

        Type type = null;
        for (Stmt stmt : b.statements)
            type = stmt.accept(this);

        return type;
	}

	@Override
	public Type visitProcedure(Procedure p) {
        procedureType = p.type;
        procedureName = p.name;

        for (VarDecl vd: p.params)
            vd.accept(this);

        Type t = p.block.accept(this);

        // Reset
        procedureType = null;
        return p.type;
	}

	@Override
	public Type visitProgram(Program p) {
        for (VarDecl varDecl : p.varDecls)
            varDecl.accept(this);
        for (Procedure proc : p.procs)
            proc.accept(this);

        return p.main.accept(this);
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		// Void is disallowed
        if (vd.type == Type.VOID) error("Variable declaration cannot be void");
        return null;
	}

	@Override
	public Type visitVar(Var v) {
        VarDecl varDecl = v.getVarDecl();

        if (varDecl == null) {
            error(String.format(
                    "Encountered undeclared variable %s",
                    v.name
            ));
            return Type.VOID;
        }
		v.type = varDecl.type;
        return v.type;
	}

	@Override
	public Type visitFunctionCallStmt(FunCallStmt funCallStmt) {
        Procedure definition = funCallStmt.getProcedure();

        if (definition == null) {
            // Undeclared function call stmt
            error(String.format(
                    "Function call %s is undeclared.", funCallStmt.name
            ));
            return Type.VOID;
        }

        if (funCallStmt.arguments.size() != definition.params.size()) {
            error(String.format(
                    "Function call expression '%s' requires %d arguments, not %d",
                    funCallStmt.name,
                    definition.params.size(),
                    funCallStmt.arguments.size()));
            // Still return the type that it should have so we can continue
            return definition.type;
        }

        for (int i = 0; i < funCallStmt.arguments.size(); i++) {
            Expr funCallArg = funCallStmt.arguments.get(i);
            VarDecl defCallArg = definition.params.get(i);

            Type argType = funCallArg.accept(this);
            Type defType = defCallArg.type;

            if (argType != defType) {
                error(String.format(
                        "Function call expression '%s' expected argument of type %s but got %s at position %d",
                        funCallStmt.name, defType, argType, i + 1
                ));
            }
        }

        return definition.type;
	}

	@Override
	public Type visitStrLiteral(StrLiteral strLiteral) {
		return Type.STRING;
	}

	@Override
	public Type visitWhile(While whilez) {
        // Expr needs to be an int
        Type exprType = whilez.expr.accept(this);

        if (exprType != Type.INT) {
            error(String.format(
                    "While (expression) needs to be of type INT. Encountered %s",
                    exprType
            ));
        }

        whilez.statement.accept(this);
        return Type.INT;
	}

	@Override
	public Type visitBinOp(BinOp binOp) {
		Op op = binOp.op;

        // Arithmetics
        if (ARITHMETIC_OPS.contains(op)) {
            // Both sides need to be ints
            Type lhs = binOp.lhs.accept(this);
            Type rhs = binOp.rhs.accept(this);

            if (lhs != Type.INT) error(String.format(
                    "Invalid type found on LHS = %s. Arithmetic expression require INT types on both sides.",
                    lhs
            ));

            if (rhs != Type.INT) error(String.format(
                    "Invalid type found on RHS = %s. Arithmetic expression require INT types on both sides.",
                    lhs
            ));

        }
        // Comparisons
        else {
            // Both sides need to be the same type
            Type lhs = binOp.lhs.accept(this);
            Type rhs = binOp.rhs.accept(this);

            if (lhs != rhs) {
                error(String.format(
                        "Expected BinaryOp LHS = RHS, found %s = %s.",
                        lhs,
                        rhs
                ));
            }

        }

        // Let the analysis continue without null dragons
        return Type.INT;
	}

	@Override
	public Type visitIf(If anIf) {
        Type condition = anIf.ifExpr.accept(this);
        if (condition != Type.INT) error(String.format(
                "Condition needs to resolve to an integer. %s encountered.",
                condition
        ));

        anIf.ifStmt.accept(this);
        if (anIf.hasElse()) anIf.elseStmt.accept(this);

        return Type.VOID;
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

        Type t = Type.VOID;
        if (aReturn.hasReturn()) {
            t = aReturn.returnz.accept(this);
        }

        if (procedureType != t) error(String.format(
                "Unexpected return type %s from procedure '%s'. %s required.",
                t,
                procedureName,
                procedureType
        ));

        return t;
	}

	@Override
	public Type visitAssign(Assign assign) {
        Type type = assign.expr.accept(this);
        Type lhsType = assign.var.getVarDecl().type;
        if (type != lhsType) {
            error(String.format(
                    "Illegal assignment %s %s = %s",
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

        if (definition == null) {
            // Undeclared function call stmt
            error(String.format(
                    "Function call %s is undeclared.", funCallExpr.name
            ));
            return Type.VOID;
        }

        if (funCallExpr.arguments.size() != definition.params.size()) {
            error(String.format(
                    "Function call expression '%s' requires %d arguments, not %d",
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
                        "Function call expression '%s' expected argument of type %s but got %s at position %d",
                        funCallExpr.name, defType, argType, i + 1
                ));
            }
        }

        // Check it is not returning void
        if (definition.type == Type.VOID)
            error(String.format(
                    "Function call expression '%s' is not allowed to return VOID",
                    funCallExpr.name
            ));

        return definition.type;
	}

}
