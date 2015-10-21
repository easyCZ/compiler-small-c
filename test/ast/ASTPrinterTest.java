package ast;


import ast.expressions.*;
import ast.statements.*;
import lexer.Scanner;
import lexer.Tokeniser;
import org.junit.Before;
import org.junit.Test;
import parser.Parser;

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
    public void visitBlockEmpty() {
        printer.visitBlock(new Block(new LinkedList<VarDecl>(), new LinkedList<Stmt>()));
        assertEquals("Block()", writer.toString());
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
                    "VarDecl(INT, Var(x)), " +
                    "VarDecl(CHAR, Var(y)), " +
                    "VarDecl(VOID, Var(z))" +
                ")", writer.toString());
    }

    @Test
    public void visitBlockOnlyStatments() {



        List<Stmt> stmts = new LinkedList<>();
        stmts.add(new Assign(new Var("x"), new IntLiteral(10)));
        stmts.add(new Assign(new Var("y"), new IntLiteral(11)));
        stmts.add(new Assign(new Var("z"), new IntLiteral(12)));

        printer.visitBlock(new Block(new LinkedList<VarDecl>(), stmts));
        assertEquals("" +
                "Block(" +
                    "Assign(Var(x), IntLiteral(10)), " +
                    "Assign(Var(y), IntLiteral(11)), " +
                    "Assign(Var(z), IntLiteral(12))" +
                ")", writer.toString());
    }

    @Test
    public void visitBlockMixed() {
        List<VarDecl> varDecls = new LinkedList<>();
        varDecls.add(new VarDecl(Type.INT, new Var("x")));
        varDecls.add(new VarDecl(Type.CHAR, new Var("y")));
        varDecls.add(new VarDecl(Type.VOID, new Var("z")));

        List<Stmt> stmts = new LinkedList<>();
        stmts.add(new Assign(new Var("x"), new IntLiteral(10)));
        stmts.add(new Assign(new Var("y"), new IntLiteral(11)));
        stmts.add(new Assign(new Var("z"), new IntLiteral(12)));

        printer.visitBlock(new Block(varDecls, stmts));
        assertEquals("" +
                "Block(" +
                "VarDecl(INT, Var(x)), " +
                "VarDecl(CHAR, Var(y)), " +
                "VarDecl(VOID, Var(z)), " +
                "Assign(Var(x), IntLiteral(10)), " +
                "Assign(Var(y), IntLiteral(11)), " +
                "Assign(Var(z), IntLiteral(12))" +
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

    @Test
    public void visitChrLiteral() {
        ChrLiteral chrLiteral = new ChrLiteral('h');
        printer.visitChrLiteral(chrLiteral);
        assertEquals("ChrLiteral(h)", writer.toString());
    }

    @Test
    public void visitAssign() {
        Assign assign = new Assign(new Var("x"), new IntLiteral(10));
        printer.visitAssign(assign);
        assertEquals("Assign(Var(x), IntLiteral(10))", writer.toString());
    }

    @Test
    public void visitReturnWithNoExpr() {
        Return returnz = new Return(null);
        printer.visitReturn(returnz);
        assertEquals("Return()", writer.toString());
    }

    @Test
    public void visitReturnWithExpr() {
        Return returnz = new Return(new IntLiteral(10));
        printer.visitReturn(returnz);
        assertEquals("Return(IntLiteral(10))", writer.toString());
    }

    @Test
    public void visitFunCallExpr_NoArguments() {
        FunCallExpr funCallExpr = new FunCallExpr("foo", new LinkedList<Expr>());
        printer.visitFunCallExpr(funCallExpr);
        assertEquals("FunCallExpr(foo)", writer.toString());
    }

    @Test
    public void visitFunCallExpr_With1Argument() {
        List<Expr> arguments = new LinkedList<>();
        arguments.add(new Var("x"));

        FunCallExpr funCallExpr = new FunCallExpr("foo", arguments);
        printer.visitFunCallExpr(funCallExpr);
        assertEquals("FunCallExpr(foo, Var(x))", writer.toString());
    }

    @Test
    public void visitFunCallExpr_With2Arguments() {
        List<Expr> arguments = new LinkedList<>();
        arguments.add(new Var("x"));
        arguments.add(new Var("y"));

        FunCallExpr funCallExpr = new FunCallExpr("foo", arguments);
        printer.visitFunCallExpr(funCallExpr);
        assertEquals("FunCallExpr(foo, Var(x), Var(y))", writer.toString());
    }

    @Test
    public void visitIf() {
        If ifz = new If(new Var("x"), new Return(null), null);
        printer.visitIf(ifz);
        assertEquals("If(Var(x), Return())", writer.toString());
    }

    @Test
    public void visitIfElse() {
        If ifz = new If(new Var("x"), new Return(null), new Return(new Var("y")));
        printer.visitIf(ifz);
        assertEquals("If(Var(x), Return(), Return(Var(y)))", writer.toString());
    }

    @Test
    public void assignment_full() {
        String program = "void main() {\n" +
                "    int i;\n" +
                "\n" +
                "    i = 9 / 3;\n" +
                "    i = i % 2;\n" +
                "    i = (i + 10) * (7 % -5);\n" +
                "    i = i + 3 + i + 5;\n" +
                "    i = i * 3 * 4;\n" +
                "    i = i % 5 % 6;\n" +
                "    i = i / 9;\n" +
                "    i = i + 3 * 4;\n" +
                "    i = i / 3 * 4;\n" +
                "    i = i - 3 + 4;\n" +
                "}";

        Scanner s = new Scanner(program);
        Tokeniser t = new Tokeniser(s);
        Parser p = new Parser(t);
        Program ast = p.parse();

        printer.visitProgram(ast);

        String out = writer.toString();
        out = out.replaceAll(" ", "");

        assertEquals(out, "Program(Procedure(VOID,main,Block(VarDecl(INT,Var(i)),Assign(Var(i),BinOp(IntLiteral(9),DIV,IntLiteral(3))),Assign(Var(i),BinOp(Var(i),MOD,IntLiteral(2))),Assign(Var(i),BinOp(BinOp(Var(i),ADD,IntLiteral(10)),MUL,BinOp(IntLiteral(7),MOD,BinOp(IntLiteral(0),SUB,IntLiteral(5))))),Assign(Var(i),BinOp(Var(i),ADD,BinOp(IntLiteral(3),ADD,BinOp(Var(i),ADD,IntLiteral(5))))),Assign(Var(i),BinOp(Var(i),MUL,BinOp(IntLiteral(3),MUL,IntLiteral(4)))),Assign(Var(i),BinOp(Var(i),MOD,BinOp(IntLiteral(5),MOD,IntLiteral(6)))),Assign(Var(i),BinOp(Var(i),DIV,IntLiteral(9))),Assign(Var(i),BinOp(Var(i),ADD,BinOp(IntLiteral(3),MUL,IntLiteral(4)))),Assign(Var(i),BinOp(Var(i),DIV,BinOp(IntLiteral(3),MUL,IntLiteral(4)))),Assign(Var(i),BinOp(Var(i),SUB,BinOp(IntLiteral(3),ADD,IntLiteral(4)))))))");
    }



}