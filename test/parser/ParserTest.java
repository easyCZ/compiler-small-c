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

    @Test public void parseExpression_GreaterThan() {
        Parser p = getParser("1 > 1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_LessThan() {
        Parser p = getParser("1 < 1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_LessThanEqual() {
        Parser p = getParser("1 <= 1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_GreaterThanEqual() {
        Parser p = getParser("1 >= 1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_Equal() {
        Parser p = getParser("1 == 1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_NotEqual() {
        Parser p = getParser("1 != 1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_NestedSimple() {
        Parser p = getParser("-1 - 1 + 3 == 1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_Nested() {
        Parser p = getParser("15 * 1 - 3 <= 2");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseExpression_Single() {
        Parser p = getParser("1");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }


    /* Conditionals */
    @Test public void parseStatement_ParsesIf() {
        Parser p = getParser("if (a == b) {}");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_ParsesIfAndElse() {
        Parser p = getParser("if (a == b) {} else {}");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_ParsesNestedIf() {
        Parser p = getParser("" +
                "if (a == b) { \n" +
                "   if (c == b) {} \n" +
                "}");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_ParsesNestedIfWithElse() {
        Parser p = getParser("" +
                "if (a == b) { \n" +
                "   if (c == b) {} else {}\n" +
                "}");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_ParsesNestedInlineIf() {
        Parser p = getParser("" +
                "if (a == b) if (c == b) {} else {} else {}");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseStatement_CrazyNestedIf() {
        Parser p = getParser("" +
                "if (a == b) " +
                "   if (c == d) " +
                "       if (d == e) {} " +
                "       else {} " +
                "   else {} " +
                "else {\n" +
                "   if (foo <= bar) {} " +
                "   else " +
                "       if (1 <= 2) print_i(1);" +
                "       else print_i(3);" +
                "}");
        p.nextToken();
        p.parseStatement();

        assertErrorCountAndEOF(p);
    }

    /* Terms */
    @Test public void parseTerms_ParsesSingle() {
        Parser p = getParser("1");
        p.nextToken();
        p.parseTerm();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseTerms_ParsesChainedDiv() {
        Parser p = getParser("1 / 1");
        p.nextToken();
        p.parseTerm();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseTerms_ParsesChainedTimes() {
        Parser p = getParser("1 * 1");
        p.nextToken();
        p.parseTerm();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseTerms_ParsesChainedMod() {
        Parser p = getParser("1 % 1");
        p.nextToken();
        p.parseTerm();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseTerms_ParsesChained() {
        Parser p = getParser("1 / 1 * 5 % 3");
        p.nextToken();
        p.parseTerm();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseTerms_ParsesChainedWithFunctionCalls() {
        Parser p = getParser("1 / foo() * bar() % 3");
        p.nextToken();
        p.parseTerm();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseTerms_ParsesChainedWithIdentifiers() {
        Parser p = getParser("a / b * c % 3");
        p.nextToken();
        p.parseTerm();

        assertErrorCountAndEOF(p);
    }

    /* Lexical expressions */
    @Test public void parseLexicalExpression_Arithmetic() {
        Parser p = getParser("1 + 2 + 3");
        p.nextToken();
        p.parseLexicalExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseLexicalExpression_ArithmeticWithTimes() {
        Parser p = getParser("1 + 2 * 3");
        p.nextToken();
        p.parseLexicalExpression();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseLexicalExpression_ArithmeticWithEquals() {
        Parser p = getParser("1 + 2 * 3 == 7");
        p.nextToken();
        p.parseExpression();

        assertErrorCountAndEOF(p);
    }

    /* Functions */
    @Test public void parseProcedures_ParsesEmptyFunction() {
        Parser p = getParser("void foo() {}");
        p.nextToken();
        p.parseProcedures();

        assertErrorCountAndEOF(p);
    }

    @Test public void parseProcedures_ParsesEmptyFunctionMultiple() {
        Parser p = getParser("" +
                "void foo() {}\n" +
                "void foo() {}\n" +
                "void foo() {}\n");
        p.nextToken();
        p.parseProcedures();

        assertErrorCountAndEOF(p);
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