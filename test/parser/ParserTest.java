package parser;


import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;
import org.junit.Test;
import util.MockTokeniser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    private Tokeniser tokeniser;

    private static final List<Token> INCLUDE_STATEMENT = Arrays.asList(
            new Token(Token.TokenClass.INCLUDE),
            new Token(Token.TokenClass.STRING_LITERAL)
    );

    private static final List<Token> INT_VAR_DECL = Arrays.asList(
            new Token(Token.TokenClass.INT),
            new Token(Token.TokenClass.IDENTIFIER),
            new Token(Token.TokenClass.SEMICOLON)
    );

    private static final List<Token> CHAR_VAR_DECL = Arrays.asList(
            new Token(Token.TokenClass.CHAR),
            new Token(Token.TokenClass.IDENTIFIER),
            new Token(Token.TokenClass.SEMICOLON)
    );

    private static final List<Token> VOID_VAR_DECL = Arrays.asList(
            new Token(Token.TokenClass.VOID),
            new Token(Token.TokenClass.IDENTIFIER),
            new Token(Token.TokenClass.SEMICOLON)
    );

    private static final List<Token> VOID_MAIN_EMPTY_FUNC = Arrays.asList(
            new Token(Token.TokenClass.VOID),
            new Token(Token.TokenClass.MAIN),
            new Token(Token.TokenClass.LPAR),
            new Token(Token.TokenClass.RPAR),
            new Token(Token.TokenClass.LBRA),
            new Token(Token.TokenClass.RBRA)
    );

    /* include */

//    @Test public void parseIncludes_CorrectlyParsesSingleInclude() {
//        Parser parser = getParserAndParse(INCLUDE_STATEMENT);
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    @Test public void parsesMultipleIncludeStatements() {
//        Parser parser = getParser(duplicate(INCLUDE_STATEMENT, 3));
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    /* Var decls */
//
//    @Test public void parseIntegerVariableDeclaration() {
//        Parser parser = getParserAndParse(INT_VAR_DECL);
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    @Test public void parseMultipleIntegerVariableDeclarations() {
//        Parser parser = getParserAndParse(duplicate(INT_VAR_DECL, 3));
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    @Test public void parseCharacterVariableDeclaration() {
//        Parser parser = getParserAndParse(CHAR_VAR_DECL);
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    @Test public void parserMultipleCharacterVariableDeclarations() {
//        Parser parser = getParserAndParse(duplicate(CHAR_VAR_DECL, 3));
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    @Test public void parseVoidVariableDeclaration() {
//        Parser parser = getParserAndParse(VOID_VAR_DECL);
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    @Test public void parseMultipleVoidVariableDeclarations() {
//        Parser parser = getParserAndParse(duplicate(VOID_VAR_DECL, 3));
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    @Test public void parsesMixedVariableDeclarations() {
//        List<Token> exp = new ArrayList<>(INT_VAR_DECL);
//        exp.addAll(CHAR_VAR_DECL);
//        exp.addAll(VOID_VAR_DECL);
//
//        Parser parser = getParserAndParse(exp);
//        assertEquals(0, parser.getErrorCount());
//    }
//
//    /* procedures */
//    @Test public void parsesMainWithNoContent() {
//        Parser parser = getParserAndParse(VOID_MAIN_EMPTY_FUNC);
//        assertErrorCountAndEOF(parser);
//    }

    /* Comments */
    @Test public void failsWithSingleComment() {
        Parser p = getParserAndParse("// This is a comment");
        assertErrorCountAndToken(p, 1, Token.TokenClass.EOF);
    }

    @Test public void failsWithMultilineComment() {
        Parser p = getParserAndParse("/* This is a\n" +
                "multiline\n" +
                "comment */");
        assertErrorCountAndToken(p, 1, Token.TokenClass.EOF);
    }

    /* chars */
    @Test public void parsesCharDeclarations() {
        Parser p = getParserAndParse("" +
                "char a; \n" +
                "char b;\n" +
                "char abcd;\n" +

                "void main() {\n" +
                    "a = 'a'; \n" +
                    "b = 'b';\n" +
                    "abcd = '\\'';\n" +
                "}"
        );
        assertErrorCountAndEOF(p, 0);
    }

    /* Empty file */
    @Test public void failsWithEmptyFile() {
        Parser p = getParserAndParse("");
        assertErrorCountAndEOF(p, 1);
    }

    @Test public void parsFunctionCall_CorrectlyParsesMultipleArguments() {
        Parser p = getParser("hello(a, b, c)");
        p.nextToken();
        p.parseFunctionCall();

        assertErrorCountAndEOF(p);
    }

    @Test public void parsFunctionCall_CorrectlyParsesNoArguments() {
        Parser p = getParser("hello()");
        p.nextToken();
        p.parseFunctionCall();

        assertErrorCountAndEOF(p);
    }

    @Test public void parsFunctionCall_FailsWithInnerFunctionCall() {
        Parser p = getParser("hello(foo())");
        p.nextToken();
        p.parseFunctionCall();

        assertErrorCountAndToken(p, 1, Token.TokenClass.LPAR);
    }

    @Test public void parsFunctionCall_FailsWithTrailingComma() {
        Parser p = getParser("hello(a,)");
        p.nextToken();
        p.parseFunctionCall();

        assertErrorCountAndEOF(p, 1);
    }

    // Prints
    @Test public void parseStatement_PrintString() {
        Parser p = getParser("print_s(\"Hello world!\");");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_PrintStringWithFunctionCall() {
        Parser p = getParser("print_s(get_string());");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndToken(p, 1, Token.TokenClass.IDENTIFIER);
    }

    @Test public void parseStatement_PrintStringWithFunctionCallWithArguments() {
        Parser p = getParser("print_s(get_string(a, b, c));");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndToken(p, 1, Token.TokenClass.IDENTIFIER);
    }

    @Test public void parseStatement_PrintInteger() {
        Parser p = getParser("print_i(1234);");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_PrintIntegerWithFunctionCall() {
        Parser p = getParser("print_i(get_num());");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_PrintIntegerWithFunctionCallWithArguments() {
        Parser p = getParser("print_i(get_num(a, b, c));");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_PrintCharacter() {
        Parser p = getParser("print_c('c');");
        p.nextToken();
        p.parseStatement();
        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_PrintCharacterWithFunctionCall() {
        Parser p = getParser("print_c(foo());");
        p.nextToken();
        p.parseStatement();
        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_PrintCharacterWithFunctionCallAndArguments() {
        Parser p = getParser("print_c(foo(a, b, c));");
        p.nextToken();
        p.parseStatement();
        assertErrorCountAndEOF(p);
    }

    /* Comparison expressions */
    @Test public void parseExpression_failsWithEmptyBothComparisons() {
        Parser p = getParser(" == ");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p, 2);
    }

    @Test public void parseExpression_failsWithEmptyRightComparison() {
        Parser p = getParser("a == ");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p, 1);
    }

    @Test public void parseExpression_failsWithEmptyLeftComparison() {
        Parser p = getParser(" == a");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p, 1);
    }




    private Parser getParser(String program) {
        tokeniser = new Tokeniser(new Scanner(program));
        return new Parser(tokeniser);
    }

    private Parser getParser(List<Token> tokens) {
        tokeniser = new MockTokeniser(tokens);
        return new Parser(tokeniser);
    }

    private Parser getParserAndParse(List<Token> tokens) {
        Parser p = getParser(tokens);
        p.parse();
        return p;
    }

    private Parser getParserAndParse(String program) {
        Parser p = getParser(program);
        p.parse();
        return p;
    }

    private void assertErrorCountAndEOF(Parser p) {
        assertErrorCountAndEOF(p, 0);
    }

    private void assertErrorCountAndEOF(Parser p, int count) {
        assertEquals(count, p.getErrorCount());
        // End of file is followed by null
        assertEquals(Token.TokenClass.EOF, p.getToken().tokenClass);
    }

    private void assertErrorCountAndToken(Parser p, int count, Token.TokenClass t) {
        assertEquals(count, p.getErrorCount());
        assertEquals(t, p.getToken().tokenClass);
    }

    private List<Token> duplicate(List<Token> what, int times) {
        if (times <= 1) return what;

        List<Token> copy = new ArrayList<>();
        for (int i = 0; i < what.size() * times; i++) {
            copy.add(what.get(i % what.size()));
        }
        return copy;
    }

}