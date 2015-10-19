package ast;


import ast.expressions.IntLiteral;
import ast.expressions.StrLiteral;
import ast.expressions.Var;
import ast.statements.FunCallStmt;
import ast.statements.While;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ASTPrinterTest {

    private Writer writer;
    private PrintWriter printWriter;
    private ASTPrinter printer;

    @Before
    public void setUp() {
        writer = new StringWriter();
        printWriter = new PrintWriter(writer);
        printer = new ASTPrinter(printWriter);
    }

    @Test
    public void visitFunctionCallStmt_NoArguments() {
        FunCallStmt funCallStmt = new FunCallStmt(
                "foo",
                new LinkedList<Expr>()
        );
        printer.visitFunctionCallStmt(funCallStmt);
        assertEquals("FunCallStmt(foo)", writer.toString());
    }

    @Test
    public void visitFunctionCallStmt_OneArgument() {
        List<Expr> arguments = new LinkedList<>();
        arguments.add(new Var("bar"));
        FunCallStmt funCallStmt = new FunCallStmt(
                "foo",
                arguments
        );
        printer.visitFunctionCallStmt(funCallStmt);
        assertEquals("FunCallStmt(foo, Var(bar))", writer.toString());
    }

    @Test
    public void visitFunctionCallStmt_MultipleArguments() {
        List<Expr> arguments = new LinkedList<>();
        arguments.add(new Var("bar"));
        arguments.add(new Var("zoo"));
        FunCallStmt funCallStmt = new FunCallStmt(
                "foo",
                arguments
        );
        printer.visitFunctionCallStmt(funCallStmt);
        assertEquals("FunCallStmt(foo, Var(bar), Var(zoo))", writer.toString());
    }

    @Test
    public void visitStrLiteral() {
        StrLiteral literal = new StrLiteral("hello");
        printer.visitStrLiteral(literal);
        assertEquals("StrLiteral(hello)", writer.toString());
    }

    @Test
    public void visitWhile() {
        While whilez = new While(new Var("x"), new Block(
                new LinkedList<VarDecl>(),
                new LinkedList<Stmt>()
        ));
        printer.visitWhile(whilez);
        assertEquals("While(Var(x), Block())", writer.toString());
    }

    @Test
    public void visitBlockOnlyVardecls() {
        List<VarDecl> varDecls = new LinkedList<>();
        varDecls.add(new VarDecl(Type.INT, new Var("x")));
        varDecls.add(new VarDecl(Type.CHAR, new Var("y")));
        varDecls.add(new VarDecl(Type.VOID, new Var("z")));

        printer.visitBlock(new Block(varDecls, new LinkedList<Stmt>()));
        assertEquals("" +
                "Block(" +
                    "VarDecl(Var(x), INT), " +
                    "VarDecl(Var(y), CHAR), " +
                    "VarDecl(Var(z), VOID)" +
                ")", writer.toString());
    }

    @Test
    public void visitBinOp() {
        BinOp binOp = new BinOp(new IntLiteral(10), Op.ADD, new IntLiteral(1));
        printer.visitBinOp(binOp);

        assertEquals("BinOp(IntLiteral(10), ADD, IntLiteral(1))", writer.toString());
    }

    @Test
    public void visitIntLiteral() {
        IntLiteral intLiteral = new IntLiteral(123);
        printer.visitIntLiteral(intLiteral);
        assertEquals("IntLiteral(123)", writer.toString());
    }


}