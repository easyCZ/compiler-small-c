package sem;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import sem.symbols.VarSymbol;

import java.util.List;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	private Scope scope;

	public NameAnalysisVisitor(Scope scope) {
		this.scope = scope;
	}

	public NameAnalysisVisitor() {
		this.scope = new Scope();
	}

    @Override
    public Void visitProgram(Program program) {

        visitVarDecls(program.varDecls);

//        TODO: Test
//        for (Procedure p : program.procs) {
//            p.accept(this);
//        }
//
//        program.main.accept(this);

        return null;
    }

	@Override
	public Void visitBlock(Block b) {
		Scope oldScope = scope;

        scope = new Scope(oldScope);
        visitVarDecls(b.varDecls);
        visitStatements(b.statements);

        scope = oldScope;

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
        Symbol symbol = scope.lookup(v.name);

        // Symbol must be in the table
        if (symbol == null) {
            error(String.format("Attempted to use an undeclared variable %s", v.name));
        }
        else if (!symbol.isVar()) {
            error(String.format("Attempted to use %s as a variable.", v.name));
        }
        else {
            VarSymbol varSymbol = (VarSymbol) symbol;
            v.setVarDecl(varSymbol.varDecl);
        }

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

    private void visitVarDecls(List<VarDecl> varDeclList) {
        for (VarDecl v : varDeclList)
            v.accept(this);
    }

    private void visitStatements(List<Stmt> statements) {
        for (Stmt stmt : statements)
            stmt.accept(this);
    }

    public Scope getScope() {
        return this.scope;
    }
}
