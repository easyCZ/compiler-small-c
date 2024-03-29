package parser;


import ast.*;
import ast.expressions.*;
import ast.statements.*;
import lexer.Scanner;
import lexer.Tokeniser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ParserAstTest {


    /* Variable Declarations */
    @Test
    public void variableDeclaration_SingleParsed() {
        List<VarDecl> varDecls = getParser("int i;").parseVariableDeclarations();
        assertVardecl(Type.INT, "i", varDecls.get(0));
    }

    @Test
    public void variableDeclaration_MultipleParsed() {
        List<VarDecl> varDecls = getParser("" +
                "int i;" +
                "char myChar;" +
                "void blah;" +
        "").parseVariableDeclarations();
        assertVardecl(Type.INT, "i", varDecls.get(0));
        assertVardecl(Type.CHAR, "myChar", varDecls.get(1));
        assertVardecl(Type.VOID, "blah", varDecls.get(2));
    }

    /* Procedures */
    @Test
    public void procedures_SingleParsed() {
        List<Procedure> procedures = getParser("void test() {}").parseProcedures();
        assertProcedure(Type.VOID, "test", procedures.get(0));

    }

    @Test
    public void procedures_MultipleParsed() {
        List<Procedure> procedures = getParser("" +
                "void test() {}" +
                "char foo() {}" +
                "int bar() {}").parseProcedures();
        assertProcedure(Type.VOID, "test", procedures.get(0));
        assertProcedure(Type.CHAR, "foo", procedures.get(1));
        assertProcedure(Type.INT, "bar", procedures.get(2));
    }

    @Test
    public void procedures_WithSingleArgument() {
        List<Procedure> procedures = getParser("void test(int i) {}").parseProcedures();
        assertVardecl(Type.INT, "i", procedures.get(0).params.get(0));
    }

    @Test
    public void procedures_WithMultipleArguments() {
        List<Procedure> procedures = getParser("" +
                "void test(int i, char foo, void bar) {}").parseProcedures();
        assertVardecl(Type.INT, "i", procedures.get(0).params.get(0));
        assertVardecl(Type.CHAR, "foo", procedures.get(0).params.get(1));
        assertVardecl(Type.VOID, "bar", procedures.get(0).params.get(2));
    }

    /* Body */
    @Test
    public void body_WithNoDeclarations() {
        Block block = getParser("{}").parseBody();
        assertEquals(0, block.varDecls.size());
    }

    @Test
    public void body_WithSingleDeclaration() {
        Block block = getParser("{ int i; }").parseBody();
        assertEquals(1, block.varDecls.size());
        assertVardecl(Type.INT, "i", block.varDecls.get(0));
    }

    @Test
    public void body_WithMultipleDeclarations() {
        Block block = getParser("{ int i; char x;}").parseBody();
        assertEquals(2, block.varDecls.size());
        assertVardecl(Type.INT, "i", block.varDecls.get(0));
        assertVardecl(Type.CHAR, "x", block.varDecls.get(1));
    }

    @Test
    public void body_WithSingleStatement() {
        // TODO
    }

    @Test
    public void body_WithMultipleStatements() {
        // TODO
    }


    /* Statements */
    @Test
    public void statement_FunctionCallNoArguments() {
        FunCallStmt stmt = (FunCallStmt) getParser("foo()").parseStatement();
        assertEquals("foo", stmt.name);
        assertEquals(0, stmt.arguments.size());
    }

    @Test
    public void statement_FunctionCallWithArguments() {
        FunCallStmt stmt = (FunCallStmt) getParser("foo(a, b, c, d)").parseStatement();
        assertEquals("foo", stmt.name);
        assertEquals(4, stmt.arguments.size());

        int i = 0;
        for (String s : Arrays.asList("a", "b", "c", "d")) {
            assertEquals(s, ((Var) stmt.arguments.get(i)).name);
            i += 1;
        }
    }

    @Test
    public void statement_PrintStringLiteral() {
        FunCallStmt stmt = (FunCallStmt) getParser("print_s(\"hello\")").parseStatement();
        assertEquals("print_s", stmt.name);
        assertEquals(1, stmt.arguments.size());

        assertEquals("hello", ((StrLiteral)stmt.arguments.get(0)).string);
    }

    /* While */
    @Test
    public void while_parsed() {
        While whilez = (While) getParser("while (x) {}").parseStatement();

        assertEquals("x", ((Var) whilez.expr).name);
        assertNotNull(whilez.statement);
    }

    /* If */
    @Test
    public void if_parsed() {
        If ifz = (If) getParser("if (x) {}").parseStatement();
        assertEquals("x", ((Var) ifz.ifExpr).name);
        assertNotNull(ifz.ifStmt);
        assertNull(ifz.elseStmt);
    }

    @Test
    public void if_parsedWithElse() {
        If ifz = (If) getParser("if (x) {} else {}").parseStatement();
        assertEquals("x", ((Var) ifz.ifExpr).name);
        assertNotNull(ifz.ifStmt);
        assertNotNull(ifz.elseStmt);
    }

    @Test
    public void if_parsedWithElseAndExpressionConditional() {
        If ifz = (If) getParser("if (x == 10 / 2) {} else {}").parseStatement();

        assertNotNull(ifz.ifStmt);
        assertNotNull(ifz.elseStmt);
        BinOp expr = (BinOp) ifz.ifExpr;

        assertEquals("x", ((Var) expr.lhs).name);
        assertEquals(Op.EQ, expr.op);
        BinOp rhs = (BinOp) expr.rhs;
        assertEquals(10, ((IntLiteral)rhs.lhs).value);
        assertEquals(Op.DIV, rhs.op);
        assertEquals(2, ((IntLiteral) rhs.rhs).value);
    }

    /* main */
    @Test
    public void main_parsed() {
        Procedure p = getParser("void main() {}").parseMain();
        assertProcedure(Type.VOID, "main", p);
        assertEquals(0, p.params.size());
    }

    /* factors */
    @Test
    public void factor_Identifier() {
        Var v = (Var) getParser("x").parseFactor();
        assertEquals("x", v.name);
    }

    @Test
    public void factor_Number() {
        IntLiteral i = (IntLiteral) getParser("12345").parseFactor();
        assertEquals(12345, i.value);
    }

    @Test
    public void factor_negativeIdentifier() {
        BinOp binOp = (BinOp) getParser("-x").parseFactor();
        assertEquals(0, ((IntLiteral) binOp.lhs).value);
        assertEquals(Op.SUB, binOp.op);
        assertEquals("x", ((Var) binOp.rhs).name);
    }

    @Test
    public void factor_negativeNumber() {
        BinOp binOp = (BinOp) getParser("-12345").parseFactor();
        assertEquals(0, ((IntLiteral) binOp.lhs).value);
        assertEquals(Op.SUB, binOp.op);
        assertEquals(12345, ((IntLiteral) binOp.rhs).value);
    }

    @Test
    public void factor_character() {
        ChrLiteral c = (ChrLiteral) getParser("'h'").parseFactor();
        assertEquals('h', c.value);
    }

    @Test
    public void factor_FunctionCallNoArguments() {
        FunCallExpr f = (FunCallExpr) getParser("foo()").parseFactor();
        assertEquals("foo", f.name);
        assertEquals(0, f.arguments.size());
    }

    @Test
    public void factor_FunctionCallWithArguments() {
        FunCallExpr f = (FunCallExpr) getParser("foo(bar, zoo)").parseFactor();
        assertEquals("foo", f.name);
        assertEquals(2, f.arguments.size());

        List<Expr> arguments = f.arguments;

        int i = 0;
        for (String var : Arrays.asList("bar", "zoo")) {
            assertEquals(var, ((Var) arguments.get(i)).name);
            i += 1;
        }
    }

    @Test
    public void factor_ReadInteger() {
        FunCallExpr f = (FunCallExpr) getParser("read_i()").parseFactor();
        assertEquals("read_i", f.name);
        assertEquals(0, f.arguments.size());
    }

    @Test
    public void factor_ReadCharacter() {
        FunCallExpr f = (FunCallExpr) getParser("read_c()").parseFactor();
        assertEquals("read_c", f.name);
        assertEquals(0, f.arguments.size());
    }

    /* read statement */
    @Test
    public void statement_ReadChar() {
        FunCallStmt read = (FunCallStmt) getParser("read_c()").parseStatement();
        assertEquals("read_c", read.name);
        assertEquals(0, read.arguments.size());
    }

    @Test
    public void statement_ReadInt() {
        FunCallStmt read = (FunCallStmt) getParser("read_i()").parseStatement();
        assertEquals("read_i", read.name);
        assertEquals(0, read.arguments.size());
    }

    /* Print statement */
    @Test
    public void statement_PrintInt() {
        FunCallStmt read = (FunCallStmt) getParser("print_i()").parseStatement();
        assertEquals("print_i", read.name);
        assertNotNull(read.arguments);
    }

    @Test
    public void statement_PrintChar() {
        FunCallStmt read = (FunCallStmt) getParser("print_c()").parseStatement();
        assertEquals("print_c", read.name);
        assertNotNull(read.arguments);
    }

    /* Return */
    @Test
    public void return_noArgumentsValid() {
        Return returnz = (Return) getParser("return;").parseStatement();
        assertNull(returnz.returnz);
        assertFalse(returnz.hasReturn());
    }

    @Test
    public void return_WithArguments() {
        Return returnz = (Return) getParser("return -x;").parseStatement();
        assertNotNull(returnz.returnz);
        assertTrue(returnz.hasReturn());
    }

    /* Assignment */
    @Test
    public void assign_Number() {
        Assign assign = (Assign) getParser("foo = 10;").parseStatement();
        assertEquals("foo", assign.var.name);
        assertNotNull(assign.expr);
    }

    /* Expressions */
    @Test
    public void expression_mix() {
        BinOp binOp = (BinOp) getParser("2+3+4").parseLexicalExpression();
        assertEquals(2, ((IntLiteral) binOp.lhs).value);
        assertEquals(Op.ADD, binOp.op);
        BinOp rhs = (BinOp) binOp.rhs;
        assertEquals(3, ((IntLiteral) rhs.lhs).value);
        assertEquals(Op.ADD, rhs.op);
        assertEquals(4, ((IntLiteral) rhs.rhs).value);
    }

    @Test
    public void expression_chain() {
        BinOp binOp = (BinOp) getParser("i / 5 * 6").parseLexicalExpression();

        Var i = (Var) binOp.lhs;
        Op div = binOp.op;
        BinOp rhs = (BinOp) binOp.rhs;
        IntLiteral five = (IntLiteral) rhs.lhs;
        IntLiteral six = (IntLiteral) rhs.rhs;
        Op times = rhs.op;

        assertEquals("i", i.name);
        assertEquals(Op.DIV, div);
        assertEquals(5, five.value);
        assertEquals(Op.MUL, times);
        assertEquals(6, six.value);
    }

    @Test public void expression_MixedTimesAndPlus() {
        BinOp binOp = (BinOp) getParser("2 * 3 + 4").parseLexicalExpression();

        BinOp mul = (BinOp) binOp.lhs;
        IntLiteral two = (IntLiteral) mul.lhs;
        Op times = mul.op;
        IntLiteral three = (IntLiteral) mul.rhs;

        Op plus = binOp.op;
        IntLiteral four = (IntLiteral) binOp.rhs;

        assertEquals(2, two.value);
        assertEquals(Op.MUL, times);
        assertEquals(3, three.value);
        assertEquals(Op.ADD, plus);
        assertEquals(4, four.value);

    }



    private void assertProcedure(Type type, String name, Procedure p) {
        assertEquals(type, p.type);
        assertEquals(name, p.name);
    }

    private void assertVardecl(Type type, String varName, VarDecl vardecl) {
        assertEquals(type, vardecl.type);
        assertEquals(varName, vardecl.var.name);
    }

    private Program getProgram(String s) {
        Parser parser = getParser(s);
        return parser.parse();
    }

    private Parser getParser(String program) {
        Tokeniser tokeniser = new Tokeniser(new Scanner(program));
        Parser p = new Parser(tokeniser);
        p.nextToken();
        return p;
    }

}