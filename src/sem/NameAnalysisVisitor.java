package sem;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import sem.symbols.VarSymbol;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	private final Scope scope;

	public NameAnalysisVisitor(Scope scope) {
		this.scope = scope;
	}

	public NameAnalysisVisitor() {
		this.scope = new Scope();
	}

    @Override
    public Void visitProgram(Program p) {

        for (VarDecl v : p.varDecls)
            visitVarDecl(v);

        return null;
    }

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
	public Void visitVarDecl(VarDecl vd) {
        Symbol symbol = scope.lookupCurrent(vd.var.name);

        if (symbol != null)
            error(String.format("Encountered duplicate declaration of %s", vd.var));
        else
            scope.put(new VarSymbol(vd));

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

    public Scope getScope() {
        return this.scope;
    }
}
