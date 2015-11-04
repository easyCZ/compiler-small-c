package sem;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import sem.symbols.ProcSymbol;
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

        for (Procedure p : program.procs) {
            p.accept(this);
        }
        program.main.accept(this);

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

        Symbol symbol = scope.lookup(p.name);

        if (symbol != null)
            error(String.format("Encountered duplicate procedure definition '%s'", p.name));
        else {
            symbol = new ProcSymbol(p);
            scope.put(symbol);
        }

        Scope oldScope = scope;

        // process method contents
        scope = new Scope(oldScope);
        visitVarDecls(p.params);
        p.block.accept(this);

        // Return to proper scope
        scope = oldScope;

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
        // Check name is defined
        Symbol symbol = scope.lookup(funCallStmt.name);

        if (symbol == null) {
            error(String.format("Undeclared procedure call '%s' used.", funCallStmt.name));
            return null;
        }
        else if (!symbol.isProc()) {
            error(String.format("Expression '%s' must be used as function call.", funCallStmt.name));
            return null;
        }

        ProcSymbol procSymbol = (ProcSymbol) symbol;
        funCallStmt.setProcedure(procSymbol.procedure);

        // Check arguments
        for (Expr expr : funCallStmt.arguments)
            expr.accept(this);

        return null;
	}

	@Override
	public Void visitStrLiteral(StrLiteral strLiteral) {
		return null;
	}

	@Override
	public Void visitWhile(While whilez) {
        whilez.expr.accept(this);
        whilez.statement.accept(this);
		return null;
	}

	@Override
	public Void visitBinOp(BinOp binOp) {

        binOp.lhs.accept(this);
        binOp.rhs.accept(this);

        return null;
	}

	@Override
	public Void visitIf(If anIf) {
        anIf.ifExpr.accept(this);
        anIf.ifStmt.accept(this);
        if (anIf.hasElse()) anIf.elseStmt.accept(this);
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
        if (aReturn.hasReturn()) aReturn.returnz.accept(this);
		return null;
	}

	@Override
	public Void visitAssign(Assign assign) {

        assign.var.accept(this);
        assign.expr.accept(this);

        return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr funCallExpr) {
        // Check name is defined
        Symbol symbol = scope.lookup(funCallExpr.name);

        if (symbol == null) {
            error(String.format("Undeclared procedure call '%s' used.", funCallExpr.name));
            return null;
        }
        else if (!symbol.isProc()) {
            error(String.format("Expression '%s' must be used as function call.", funCallExpr.name));
            return null;
        }

        ProcSymbol procSymbol = (ProcSymbol) symbol;
        funCallExpr.setProcedure(procSymbol.procedure);

        // Check arguments
        for (Expr expr : funCallExpr.arguments)
            expr.accept(this);

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

}
