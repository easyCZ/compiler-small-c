package lexer;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TokeniserServiceTest {

    private TokeniserService sut;

    private static final char INCLUDE_IDENTIFIER = '#';
    private static final String INCLUDE = "include";
    private static final String INVALID_INCLUDE = "incABCDEF ";


    private static final char CHAR_IDENTIFIER = '\'';
    private static final String CHAR_VALID = "a'";
    private static final String CHAR_UNTERMINATED = "a ";
    private static final String CHAR_TOO_LONG = "abc' ";
    private static final String CHAR_QUOTE_ESCAPED = "\\'' ";

    @Test
    public void test_include_reads_all() throws IOException {
        sut = getSut(INCLUDE);

        Token token = sut.include(INCLUDE_IDENTIFIER);
        assertEquals(Token.TokenClass.INCLUDE, token.tokenClass);
        assertEquals(INCLUDE_IDENTIFIER + INCLUDE, token.data);
        assertEquals("1:8", token.position.toString());
    }

    @Test
    public void test_IncludeInvalidMarksTokenInvalid() throws IOException {
        sut = getSut(INVALID_INCLUDE);
        Token token = sut.include('#');
        assertEquals('#' + INVALID_INCLUDE.trim(), token.data);
    }

    @Test
    public void test_IncludeInvalidSetsPositionPointerToFirstInvalidChar() throws IOException {
        Scanner scanner = buildScanner(INVALID_INCLUDE);
        sut = getSut(scanner);

        Token token = sut.include('#');
        assertEquals("1:5", token.position.toString());

        // Should be at the end of the chunk
        assertEquals(1, scanner.getLine());
        assertEquals(10, scanner.getColumn());
    }

    @Test
    public void test_CharacterReadsUntilLastQuote() throws IOException {
        sut = getSut(CHAR_VALID);
        Token token = sut.character(CHAR_IDENTIFIER);

        assertEquals(Token.TokenClass.CHARACTER, token.tokenClass);
        assertEquals("a", token.data);
    }

    @Test
    public void test_CharacterUnterminatedReturnsInvalid() throws IOException {
        sut = getSut(CHAR_UNTERMINATED);
        Token token = sut.character(CHAR_IDENTIFIER);

        assertEquals(Token.TokenClass.INVALID, token.tokenClass);
        assertEquals("a ", token.data);
    }

    @Test
    public void test_CharacterEscapedReturnsLastQuote() throws IOException {
        sut = getSut(CHAR_QUOTE_ESCAPED);
        Token token = sut.character(CHAR_IDENTIFIER);

        assertEquals(Token.TokenClass.CHARACTER, token.tokenClass);
        assertEquals("\\'", token.data);

    }

    private Scanner buildScanner(String content) {
        return new Scanner(content);
    }

    private TokeniserService getSut(String content) {
        return new TokeniserService(buildScanner(content));
    }

    private TokeniserService getSut(Scanner scanner) {
        return new TokeniserService(scanner);
    }
}