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



    private Tokeniser getTokeniser(String content) {
        return new Tokeniser(new Scanner(content));
    }


}