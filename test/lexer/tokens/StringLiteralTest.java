package lexer.tokens;

import lexer.Scanner;
import lexer.Token;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StringLiteralTest {

    private static final String VALID_STRING_LIT = "aBcDe1235\"";
    private static final String VALID_STRING_LIT_ESCAPED = "aBcD\\\"e1235\"";
    private static final String INVALID_STRING_LIT = "abcd\\\'";
    private static final String INVALID_STRING_LIT_2 = "abcd\\\"";

    private StringLiteral sut;

    @Before
    public void setUp() {

    }

    @Test
    public void testTokenize() throws Exception {
        Token token = getToken(VALID_STRING_LIT);

        assertEquals(Token.TokenClass.STRING_LITERAL, token.tokenClass);
        assertEquals(VALID_STRING_LIT.replace("\"", ""), token.data);
    }

    @Test
    public void testTokenize_ReturnsEscaped() throws Exception {
        Token token = getToken(VALID_STRING_LIT_ESCAPED);
        assertEquals(Token.TokenClass.STRING_LITERAL, token.tokenClass);
        assertEquals("aBcD\\\"e1235", token.data);
    }

    @Test
    public void testTokenize_ReturnsInvalidWhenNotClosed() throws Exception {
        Token token = getToken(INVALID_STRING_LIT);
        assertEquals(Token.TokenClass.INVALID, token.tokenClass);
        assertEquals("abcd\\\'", token.data);
    }

    @Test
    public void testTokenize_ReturnsInvalidWhenNotClosedButEscaped() throws Exception {
        Token token = getToken(INVALID_STRING_LIT_2);
        assertEquals(Token.TokenClass.INVALID, token.tokenClass);
        assertEquals("abcd\\\"", token.data);
    }

    private Token getToken(String content) throws IOException {
        sut = new StringLiteral('"', new Scanner(content));
        return sut.tokenize('"');
    }
}