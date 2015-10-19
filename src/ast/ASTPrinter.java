package ast;

import ast.expressions.ChrLiteral;
import ast.expressions.IntLiteral;
import ast.expressions.Var;
import ast.statements.*;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    public Void visitBlock(Block b) {
        writer.print("Block(");
        // to complete
        writer.print("(");
        return null;
    }

    public Void visitProcedure(Procedure p) {
        writer.print("Procedure(");
        writer.print(p.type);
        writer.print(","+p.name+",");
        for (VarDecl vd : p.params) {            
            vd.accept(this);
            writer.print(",");
        }
        p.block.accept(this);
        writer.print(")");
        return null;
    }

    public Void visitProgram(Program p) {
        writer.print("Program(");
        for (VarDecl vd : p.varDecls) {
            vd.accept(this);
            writer.print(",");
        }
        for (Procedure proc : p.procs) {
            proc.accept(this);
            writer.print(",");
        }
        p.main.accept(this);
        writer.print(")");
	    writer.flush();
        return null;
    }

    public Void visitVarDecl(VarDecl vd){
        writer.print("VarDecl(");
        vd.var.accept(this);
        writer.print(","+vd.type);
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
        // TODO
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral strLiteral) {
        // TODO
        return null;
    }

    @Override
    public Void visitWhile(While whilez) {
        // TODO
        return null;
    }

    @Override
    public Void visitBinOp(BinOp binOp) {
        // TODO
        return null;
    }

    @Override
    public Void visitIf(If anIf) {
        // TODO
        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral intLiteral) {
        // TODO
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral chrLiteral) {
        // TODO
        return null;
    }

    @Override
    public Void visitReturn(Return aReturn) {
        // TODO
        return null;
    }

    @Override
    public Void visitAssign(Assign assign) {
        // TODO
        return null;
    }


    // to complete
    
}
