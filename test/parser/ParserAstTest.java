package parser;


import ast.Program;
import ast.Type;
import ast.VarDecl;
import lexer.Scanner;
import lexer.Tokeniser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserAstTest {


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

    private void assertVardecl(Type type, String varName, VarDecl vardecl) {
        assertEquals(type, vardecl.type);
        assertEquals(varName, vardecl.var.name);
    }

    private List<VarDecl> getVariableDeclarations(String s) {
        return getProgram(s).varDecls;
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