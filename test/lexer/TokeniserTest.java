package lexer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test public void next_SkipsNestedComments() {
        Token token = getTokeniser("/*    //comment //comment */").nextToken();

        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_SkipsTwoCommentsAfterEachOther() {
        Token token = getTokeniser("/*abc*//*abc*/").nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
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

    @Test public void next_SkipsMultilineCommentsWithANewLineAfter() {
        Tokeniser tokeniser = getTokeniser("/*\n" +
                "  Some more after program contents, generally a license\n" +
                "*/\n");

        Token token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);

//        token = tokeniser.nextToken();
//        assertEquals(Token.TokenClass.EOF, token.tokenClass);
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

    @Test public void next_MatchesZeroAsDigit() {
        Tokeniser tokeniser = getTokeniser("0");

        Token token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.NUMBER, token.tokenClass);
        assertEquals("0", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    @Test public void next_MarksDigitsStartingWithZeroFollowedByOtherDigitsAsInvalid() {
        Tokeniser tokeniser = getTokeniser("01234");

        Token token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.INVALID, token.tokenClass);
        assertEquals("01234", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
        assertEquals(1, tokeniser.getErrorCount());
    }

    @Test public void next_MarksDigitsFollowedByIdentifiarsInvalid() {
        Tokeniser tokeniser = getTokeniser("123abs");

        Token token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.NUMBER, token.tokenClass);
        assertEquals("123", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.IDENTIFIER, token.tokenClass);
        assertEquals("abs", token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
        assertEquals(0, tokeniser.getErrorCount());
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

    /* Characters */
    @Test public void next_MatchesCharacter() {
        verifyTokenFollowedByEOF("'a'", Token.TokenClass.CHARACTER, "a");
    }

    @Test public void next_MatchesCharacterAlphabeticalLowercase() {
        verifyTokenFollowedByEOF("'z'", Token.TokenClass.CHARACTER, "z");
    }

    @Test public void next_MatchesCharacterAlphabeticalUppercase() {
        verifyTokenFollowedByEOF("'A'", Token.TokenClass.CHARACTER, "A");
    }

    @Test public void next_MatchesCharacterTab() {
        verifyTokenFollowedByEOF("'\\t'", Token.TokenClass.CHARACTER, "\\t");
    }

    @Test public void next_MatchesCharacterNewline() {
        verifyTokenFollowedByEOF("'\\n'", Token.TokenClass.CHARACTER, "\\n");
    }

    @Test public void next_marksInvalidChar() {
        verifyTokenFollowedByEOF("'ab'", Token.TokenClass.INVALID, "'ab'");
    }

    @Test public void next_MatchesCharacterDot() {
        verifyTokenFollowedByEOF("'.'", Token.TokenClass.CHARACTER, ".");
    }

    @Test public void next_marksInvalidCharButParsesIdentifier() {
        List<Token> expected = Arrays.asList(
                new Token(Token.TokenClass.INVALID, "''"),
                new Token(Token.TokenClass.IDENTIFIER, "abc")
        );

        verifyTokenSequence("''abc", expected, 1);
    }


    @Test public void next_MatchesCharacterComma() {
        verifyTokenFollowedByEOF("','", Token.TokenClass.CHARACTER, ",");
    }

    @Test public void character_escape_tab() {
        verifyTokenFollowedByEOF("'\\t'", Token.TokenClass.CHARACTER, "\\t");
    }

    @Test public void character_escape_backspace() {
        verifyTokenFollowedByEOF("'\\b'", Token.TokenClass.CHARACTER, "\\b");
    }

    @Test public void character_escape_newline() {
        verifyTokenFollowedByEOF("'\\n'", Token.TokenClass.CHARACTER, "\\n");
    }

    @Test public void character_escape_carriage_return() {
        verifyTokenFollowedByEOF("'\\r'", Token.TokenClass.CHARACTER, "\\r");
    }

    @Test public void character_escape_formfeed() {
        verifyTokenFollowedByEOF("'\\f'", Token.TokenClass.CHARACTER, "\\f");
    }

    @Test public void character_escape_single_quote() {
        verifyTokenFollowedByEOF("'\\''", Token.TokenClass.CHARACTER, "\\'");
    }

    @Test public void character_escape_double_quote() {
        verifyTokenFollowedByEOF("'\\\"'", Token.TokenClass.CHARACTER, "\\\"");
    }

    @Test public void character_escape_backslash() {
        verifyTokenFollowedByEOF("'\\\\'", Token.TokenClass.CHARACTER, "\\\\");
    }

    @Test public void next_MatchesCharacter2() {
        verifyTokenFollowedByEOF("'2'", Token.TokenClass.CHARACTER, "2");
    }


    @Test public void next_CharacterEscapeWithoutContentMarkedInvalid() {
        verifyTokenFollowedByEOF("'\\' abc", Token.TokenClass.INVALID, "'\\' abc");
    }

    @Test public void next_CharacterEscapedQuoteShouldBeValid() {
        List<Token> expected = Arrays.asList(
                new Token(Token.TokenClass.CHARACTER, "\\'"),
                new Token(Token.TokenClass.IDENTIFIER, "abc")
        );

        verifyTokenSequence("'\\'' abc", expected, 0);
    }

    @Test public void next_MatchesCharacterUnderscore() {
        verifyTokenFollowedByEOF("'_'", Token.TokenClass.CHARACTER, "_");
    }

    @Test public void character_empty_is_invalid() {
        verifyTokenFollowedByEOF("''", Token.TokenClass.INVALID, "''");
    }

    @Test public void next_MatchesEscapedCharacter() {
        verifyTokenFollowedByEOF("'\\''", Token.TokenClass.CHARACTER, "\\'");
    }

    @Test public void next_MarksUnclosedCharacterAsInvalid() {
        verifyTokenFollowedByEOF("'a", Token.TokenClass.INVALID, "'a");
    }

    /* String Literals */
    @Test public void next_MatchesSimpleStringLiteral() {
        verifyTokenFollowedByEOF(
            "\"I am a string Literal;\"",
            Token.TokenClass.STRING_LITERAL,
            "I am a string Literal;"
        );
    }

    @Test public void string_literal_escape_tab() {
        verifyTokenFollowedByEOF("\"\\t\"", Token.TokenClass.STRING_LITERAL, "\\t");
    }

    @Test public void string_literal_escape_backspace() {
        verifyTokenFollowedByEOF("\"\\b\"", Token.TokenClass.STRING_LITERAL, "\\b");
    }

    @Test public void string_literal_escape_newline() {
        verifyTokenFollowedByEOF("\"\\n\"", Token.TokenClass.STRING_LITERAL, "\\n");
    }

    @Test public void string_literal_escape_carriage_return() {
        verifyTokenFollowedByEOF("\"\\r\"", Token.TokenClass.STRING_LITERAL, "\\r");
    }

    @Test public void string_literal_escape_formfeed() {
        verifyTokenFollowedByEOF("\"\\f\"", Token.TokenClass.STRING_LITERAL, "\\f");
    }

    @Test public void string_literal_escape_single_quote() {
        verifyTokenFollowedByEOF("\"\\'\"", Token.TokenClass.STRING_LITERAL, "\\'");
    }

    @Test public void string_literal_escape_double_quote() {
        verifyTokenFollowedByEOF("\"\\\"\"", Token.TokenClass.STRING_LITERAL, "\\\"");
    }

    @Test public void string_literal_escape_backslash() {
        verifyTokenFollowedByEOF("\"\\\\\"", Token.TokenClass.STRING_LITERAL, "\\\\");
    }

    @Test public void string_literal_escape_double_quote_three_times() {
        verifyTokenFollowedByEOF("\"\\\"\\\"\\\"\"", Token.TokenClass.STRING_LITERAL, "\\\"\\\"\\\"");
    }

    @Test public void string_literal_single_backslash_is_invalid() {
        verifyTokenFollowedByEOF("\"\\\"", Token.TokenClass.INVALID, "\\\"");
    }

    @Test public void string_literal_only_valid_escapes_are_allowed() {
        verifyTokenFollowedByEOF("\"\\&\"", Token.TokenClass.INVALID, "\"\\&\"");
    }


    @Test public void next_MarksMultilineStringLiteralInvalid() {
        verifyTokenFollowedByEOF(
                "\"I am a string Literal;" +
                        "\n" +
                        "More String Literal here\"",
                Token.TokenClass.INVALID,
                "I am a string Literal;\nMore String Literal here"
        );
    }

    /* Identifiers */
    @Test public void next_CorrectlyMarksSimpleIdentifier() {
        verifyTokenFollowedByEOF("foo", Token.TokenClass.IDENTIFIER, "foo");
    }

    @Test public void next_CorrectlyMarksUppercaseIdentifiers() {
        verifyTokenFollowedByEOF("FOO", Token.TokenClass.IDENTIFIER, "FOO");
    }

    @Test public void next_CorrectlyMarksUppercaseLowercaseIdentifiers() {
        verifyTokenFollowedByEOF("fOObaZZ", Token.TokenClass.IDENTIFIER, "fOObaZZ");
    }

    @Test public void next_CorrectlyMarksUnderscoreIdentifiers() {
        verifyTokenFollowedByEOF("_", Token.TokenClass.IDENTIFIER, "_");
    }

    @Test public void next_CorrectlyMarksUnderscoreAndTextIdentifiers() {
        verifyTokenFollowedByEOF("hello_there", Token.TokenClass.IDENTIFIER, "hello_there");
    }

    @Test public void next_CorrectlyMarksStartingUnderscoreAndTextIdentifiers() {
        verifyTokenFollowedByEOF("_there", Token.TokenClass.IDENTIFIER, "_there");
    }

    @Test public void next_CorrectlyMarksIdentifiersWithDigits() {
        verifyTokenFollowedByEOF("foo123", Token.TokenClass.IDENTIFIER, "foo123");
    }

    @Test public void next_MarksIdentifiersStartingWithDigitsInvalid() {
        List<Token> expected = Arrays.asList(
            new Token(Token.TokenClass.NUMBER, "123"),
            new Token(Token.TokenClass.IDENTIFIER, "foo")
        );

        verifyTokenSequence("123foo", expected, 0);
    }

    /* Unknown */
    @Test public void next_MarksUnknownTokenAsInvalid() {
        ArrayList<Token> expected = new ArrayList<>();
        expected.add(new Token(Token.TokenClass.INVALID, "&", 1, 1));
        expected.add(new Token(Token.TokenClass.IDENTIFIER, "hello", 1, 1));

        verifyTokenSequence("&hello", expected, 1);
    }


    /*
        Full Program tests
     */
    @Test public void tokeniserProducesCorrectSequenceSimpleIncrementBy2() {
        ArrayList<Token> expected = new ArrayList<>(Arrays.asList(
            new Token(Token.TokenClass.INT, 1, 0),
            new Token(Token.TokenClass.IDENTIFIER, "foo", 1, 5),
            new Token(Token.TokenClass.LPAR, 1, 7),
            new Token(Token.TokenClass.INT, 1, 8),
            new Token(Token.TokenClass.IDENTIFIER, "i", 1, 12),
            new Token(Token.TokenClass.RPAR, 1, 13),
            new Token(Token.TokenClass.LBRA, 1, 15),

            new Token(Token.TokenClass.RETURN, 2, 5),
            new Token(Token.TokenClass.IDENTIFIER, "i", 2, 12),
            new Token(Token.TokenClass.PLUS, 2, 14),
            new Token(Token.TokenClass.NUMBER, "2", 2, 16),
            new Token(Token.TokenClass.SEMICOLON, 2, 17),

            new Token(Token.TokenClass.RBRA, 3, 1)
        ));

        String program = "" +
                "int foo (int i) {\n" +
                "    return i + 2;\n" +
                "}";

        verifyTokenSequence(program, expected);
    }

    @Test public void characterAssign() {
        String program = "char foo = 'c'";
        ArrayList<Token> expected = new ArrayList<>(Arrays.asList(
                new Token(Token.TokenClass.CHAR),
                new Token(Token.TokenClass.IDENTIFIER, "foo"),
                new Token(Token.TokenClass.ASSIGN),
                new Token(Token.TokenClass.CHARACTER, "c"),
                new Token(Token.TokenClass.SEMICOLON)
        ));

        verifyTokenSequence(program, expected);
    }

    @Test public void characterAssignEscapedChar() {
        String program = "char foo = '\\\''";
        ArrayList<Token> expected = new ArrayList<>(Arrays.asList(
                new Token(Token.TokenClass.CHAR),
                new Token(Token.TokenClass.IDENTIFIER, "foo"),
                new Token(Token.TokenClass.ASSIGN),
                new Token(Token.TokenClass.CHARACTER, "\\\'"),
                new Token(Token.TokenClass.SEMICOLON)
        ));

        verifyTokenSequence(program, expected);
    }

    @Test public void interleaveMultilineCommentsWithProgram() {
        String program = "int foo = \"test\" /* this is a comment */;";

        ArrayList<Token> expected = new ArrayList<>(Arrays.asList(
                new Token(Token.TokenClass.INT),
                new Token(Token.TokenClass.IDENTIFIER, "foo"),
                new Token(Token.TokenClass.ASSIGN),
                new Token(Token.TokenClass.STRING_LITERAL, "test"),
                new Token(Token.TokenClass.SEMICOLON)
        ));

        verifyTokenSequence(program, expected);
    }

    @Test public void interleaveCommentsWithProgram() {
        String program = "" +
                "int foo = \"test\" // this is a comment" +
                ";";

        ArrayList<Token> expected = new ArrayList<>(Arrays.asList(
                new Token(Token.TokenClass.INT),
                new Token(Token.TokenClass.IDENTIFIER, "foo"),
                new Token(Token.TokenClass.ASSIGN),
                new Token(Token.TokenClass.STRING_LITERAL, "test"),
                new Token(Token.TokenClass.SEMICOLON)
        ));

        verifyTokenSequence(program, expected);
    }

    private void verifyTokenSequence(String content, List<Token> expectedTokens) {
        verifyTokenSequence(content, expectedTokens, 0);
    }

    private void verifyTokenSequence(String content, List<Token> expectedTokens, int errorCount) {
        Tokeniser tokeniser = getTokeniser(content);

        Token next = tokeniser.nextToken();
        int i = 0;
        while (next.tokenClass != Token.TokenClass.EOF) {
            assertEquals(expectedTokens.get(i), next);
            next = tokeniser.nextToken();
            i++;
        }

        assertEquals(errorCount, tokeniser.getErrorCount());
    }

    private void verifyTokenFollowedByEOF(String program, Token.TokenClass clazz, String data) {
        Tokeniser tokeniser = getTokeniser(program);
        Token token = tokeniser.nextToken();

        assertEquals(clazz, token.tokenClass);
        assertEquals(data, token.data);

        token = tokeniser.nextToken();
        assertEquals(Token.TokenClass.EOF, token.tokenClass);
    }

    private Tokeniser getTokeniser(String content) {
        return new Tokeniser(new Scanner(content));
    }


}