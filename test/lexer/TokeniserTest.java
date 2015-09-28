package lexer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokeniserTest {

    @Test public void next_SkipsWhitespace() {
        Token token = getTokeniser("    ").nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_SkipsLineBreak() {
        Token token = getTokeniser(" \n").nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MatchesEquals() {
        Token token = getTokeniser("==").nextToken();
        assertEquals(Token.TokenClass.EQ, token.tokenClass);
    }

    @Test public void next_MatchesNotEquals() {
        Token token = getTokeniser("!=").nextToken();
        assertEquals(Token.TokenClass.NE, token.tokenClass);
    }

    @Test public void next_MatchesAssignment() {
        Token token = getTokeniser("=").nextToken();
        assertEquals(Token.TokenClass.ASSIGN, token.tokenClass);
    }

    @Test public void next_MatchesLessThan() {
        Token token = getTokeniser("<").nextToken();
        assertEquals(Token.TokenClass.LT, token.tokenClass);
    }

    @Test public void next_MatchesLessThanEqual() {
        Token token = getTokeniser("<=").nextToken();
        assertEquals(Token.TokenClass.LE, token.tokenClass);
    }

    @Test public void next_MatchesGreaterThan() {
        Token token = getTokeniser(">").nextToken();
        assertEquals(Token.TokenClass.GT, token.tokenClass);
    }

    @Test public void next_MatchesGreaterThanEqual() {
        Token token = getTokeniser(">=").nextToken();
        assertEquals(Token.TokenClass.GE, token.tokenClass);
    }

    @Test public void next_MatchesPlus() {
        Token token = getTokeniser("+").nextToken();
        assertEquals(Token.TokenClass.PLUS, token.tokenClass);
    }

    @Test public void next_MatchesSemicolon() {
        Token token = getTokeniser(";").nextToken();
        assertEquals(Token.TokenClass.SEMICOLON, token.tokenClass);
    }

    @Test public void next_MatchesComma() {
        Token token = getTokeniser(",").nextToken();
        assertEquals(Token.TokenClass.COMMA, token.tokenClass);
    }

    @Test public void next_MatchesLeftParentheses() {
        Token token = getTokeniser("(").nextToken();
        assertEquals(Token.TokenClass.LPAR, token.tokenClass);
    }

    @Test public void next_MatchesRightParentheses() {
        Token token = getTokeniser(")").nextToken();
        assertEquals(Token.TokenClass.RPAR, token.tokenClass);
    }

    @Test public void next_MatchesRightBrace() {
        Token token = getTokeniser("}").nextToken();
        assertEquals(Token.TokenClass.RBRA, token.tokenClass);
    }

    @Test public void next_MatchesLeftBrace() {
        Token token = getTokeniser("{").nextToken();
        assertEquals(Token.TokenClass.LBRA, token.tokenClass);
    }

    @Test public void next_MatchesMinus() {
        Token token = getTokeniser("-").nextToken();
        assertEquals(Token.TokenClass.MINUS, token.tokenClass);
    }

    @Test public void next_MatchesTimes() {
        Token token = getTokeniser("*").nextToken();
        assertEquals(Token.TokenClass.TIMES, token.tokenClass);
    }

    @Test public void next_MatchesMod() {
        Token token = getTokeniser("%").nextToken();
        assertEquals(Token.TokenClass.MOD, token.tokenClass);
    }

    @Test public void next_MatchesDiv() {
        Token token = getTokeniser("/").nextToken();
        assertEquals(Token.TokenClass.DIV, token.tokenClass);
    }

    @Test public void next_SkipsSingleLineComment() {
        Token token = getTokeniser("" +
                "// This is a comment\n" +
                "<").nextToken();
        assertEquals(Token.TokenClass.LT, token.tokenClass);
    }

    @Test public void next_SkipsMultipleSingleLineComments() {
        Token token = getTokeniser("" +
                "// This is a comment\n" +
                "// This is another comment on a new line\n" +
                "<").nextToken();
        assertEquals(Token.TokenClass.LT, token.tokenClass);
    }

    @Test public void next_SkipsMultiLineCommentsSingleLine() {
        Token token = getTokeniser("" +
                "/* This is a multiline Comment */<").nextToken();
        assertEquals(Token.TokenClass.LT, token.tokenClass);
    }

//    @Test public void next_SkipsMultiLineCommentsSingleLineWithEscaping() {
//        Token token = getTokeniser("" +
//                "/* The syntax for comment is \\/* \\*\\/ */<").nextToken();
//        assertEquals(Token.TokenClass.LT, token.tokenClass);
//    }

    @Test public void next_SkipsMultiLineCommentsTerminalOnNewLine() {
        Token token = getTokeniser("" +
                "/*\n" +
                "   This is a multi\n" +
                "   Comment that I decided\n" +
                "       To intend strangely for fun\n" +
                "*/\n" +
                "<").nextToken();
        assertEquals(Token.TokenClass.LT, token.tokenClass);
    }

    @Test public void next_SkipsMultiLineCommentsTerminalInline() {
        Token token = getTokeniser("" +
                "/*\n" +
                "   This is a multi\n" +
                "   Comment that I decided\n" +
                "       To intend strangely for fun*/\n" +
                "<").nextToken();
        assertEquals(Token.TokenClass.LT, token.tokenClass);
    }

    @Test public void next_MatchesSingleDigit() {
        Token token = getTokeniser("5").nextToken();
        assertEquals(Token.TokenClass.NUMBER, token.tokenClass);
    }

    @Test public void next_MatchesMultipleDigitsAsOne() {
        Tokeniser tokeniser = getTokeniser("12345");

        Token token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.NUMBER, token.tokenClass);
        assertEquals("12345", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MatchesInclude() {
        Tokeniser tokeniser = getTokeniser("#include");
        Token token = tokeniser.nextToken();

        assertEquals(Token.TokenClass.INCLUDE, token.tokenClass);
        assertEquals("#include", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MarksTokenInvalidIfNotInclude() {
        Tokeniser tokeniser = getTokeniser("#includ");
        Token token = tokeniser.nextToken();

        assertEquals(Token.TokenClass.INVALID, token.tokenClass);
        assertEquals("#includ", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MarksTokenInvalidIfTooLong() {
        Tokeniser tokeniser = getTokeniser("#includee");
        Token token = tokeniser.nextToken();

        assertEquals(Token.TokenClass.INVALID, token.tokenClass);
        assertEquals("#includee", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MatchesIncludeAndStringLiteralReference() {
        Tokeniser tokeniser = getTokeniser("#include \"io.h\"");
        Token token = tokeniser.nextToken();

        assertEquals(Token.TokenClass.INCLUDE, token.tokenClass);
        assertEquals("#include", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.STRING_LITERAL, token.tokenClass);
        assertEquals("io.h", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MatchesCharacter() {
        Tokeniser tokeniser = getTokeniser("'a'");
        Token token = tokeniser.nextToken();

        assertEquals(Token.TokenClass.CHARACTER, token.tokenClass);
        assertEquals("a", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MatchesEscapedCharacter() {
        Tokeniser tokeniser = getTokeniser("'\\\''");
        Token token = tokeniser.nextToken();

        assertEquals(Token.TokenClass.CHARACTER, token.tokenClass);
        assertEquals("\\\'", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MarksUnclosedCharacterAsInvalid() {
        Tokeniser tokeniser = getTokeniser("'a");
        Token token = tokeniser.nextToken();

        assertEquals(Token.TokenClass.INVALID, token.tokenClass);
        assertEquals("'a", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }





    private Tokeniser getTokeniser(String content) {
        return new Tokeniser(new Scanner(content));
    }


}