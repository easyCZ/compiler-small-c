package parser;


import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;
import org.junit.Test;
import util.MockTokeniser;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    private Tokeniser tokeniser;

    @Test public void parseIncludes_CorrectlyParsesSingleInclude() {
        List<Token> mocks = Arrays.asList(
                new Token(Token.TokenClass.INCLUDE),
                new Token(Token.TokenClass.STRING_LITERAL)
        );

        Parser parser = getParserAndParse(mocks);
        assertEquals(0, parser.getErrorCount());
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

}