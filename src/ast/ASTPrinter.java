package ast;

import ast.expressions.*;
import ast.statements.*;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    public Void visitBlock(Block b) {
        writer.print("Block(");

        for (int i = 0; i < b.varDecls.size() - 1; i++) {
            VarDecl varDecl = b.varDecls.get(i);
            varDecl.accept(this);
            writer.print(", ");
        }
        // Handle the last vardecl
        if (b.varDecls.size() > 0) {
            VarDecl varDecl = b.varDecls.get(b.varDecls.size() - 1);
            varDecl.accept(this);
        }

        // Join the two with a dot
        if (b.varDecls.size() > 0 && b.statements.size() > 0) {
            writer.print(", ");
        }

        for (int i = 0; i < b.statements.size() - 1; i++) {
            Stmt stmt = b.statements.get(i);
            stmt.accept(this);
            writer.print(", ");
        }

        // Handle the last statement
        if (b.statements.size() > 0) {
            Stmt stmt = b.statements.get(b.statements.size() -1);
            stmt.accept(this);
        }

        writer.print(")");
        return null;
    }

    public Void visitProcedure(Procedure p) {
        writer.print("Procedure(");
        writer.print(p.type);
        writer.print(", "+p.name+", ");
        for (VarDecl vd : p.params) {            
            vd.accept(this);
            writer.print(", ");
        }
        p.block.accept(this);
        writer.print(")");
        return null;
    }

    public Void visitProgram(Program p) {
        writer.print("Program(");
        for (VarDecl vd : p.varDecls) {
            vd.accept(this);
            writer.print(", ");
        }
        for (Procedure proc : p.procs) {
            proc.accept(this);
            writer.print(", ");
        }
        p.main.accept(this);
        writer.print(")");
	    writer.flush();
        return null;
    }

    public Void visitVarDecl(VarDecl vd){
        writer.print("VarDecl(");
        writer.print(vd.type + ", ");
        vd.var.accept(this);
        writer.print(")");
        return null;
    }

    public Void visitVar(Var v) {
        writer.print("Var(");
        writer.print(v.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunctionCallStmt(FunCallStmt funCallStmt) {
        writer.print("FunCallStmt(");
        writer.print(funCallStmt.name);
        for (Expr arg : funCallStmt.arguments) {
            writer.print(", ");
            arg.accept(this);
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral strLiteral) {
        writer.print("StrLiteral(");
        writer.print(strLiteral.string);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitWhile(While whilez) {
        writer.print("While(");
        whilez.expr.accept(this);
        writer.print(", ");
        whilez.statement.accept(this);
        writer.print(")");

        return null;
    }

    @Override
    public Void visitBinOp(BinOp binOp) {
        writer.print("BinOp(");
        binOp.lhs.accept(this);
        writer.print(", ");
        writer.print(binOp.op);
        writer.print(", ");
        binOp.rhs.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitIf(If anIf) {
        writer.print("If(");
        anIf.ifExpr.accept(this);
        writer.print(", ");
        anIf.ifStmt.accept(this);
        if (anIf.hasElse()) {
            writer.print(", ");
            anIf.elseStmt.accept(this);
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral intLiteral) {
        writer.print("IntLiteral(");
        writer.print(intLiteral.value);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral chrLiteral) {
        writer.print("ChrLiteral(");
        writer.print(chrLiteral.value);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitReturn(Return aReturn) {
        writer.print("Return(");
        if (aReturn.hasReturn())
            aReturn.returnz.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitAssign(Assign assign) {
        writer.print("Assign(");
        assign.var.accept(this);
        writer.print(", ");
        assign.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr funCallExpr) {
        writer.print("FunCallExpr(");
        writer.print(funCallExpr.name);
        for (Expr e: funCallExpr.arguments) {
            writer.print(", ");
            e.accept(this);
        }
        writer.print(")");
        return null;
    }

}
