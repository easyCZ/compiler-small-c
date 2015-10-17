package parser;


import ast.Procedure;
import ast.Program;
import ast.Type;
import ast.VarDecl;
import lexer.Scanner;
import lexer.Tokeniser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

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