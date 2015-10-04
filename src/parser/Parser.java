package parser;

import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * @author cdubach
 */
public class Parser {

    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }


    /*
    * Returns true if the current token is equals to any of the expected ones.
    */

    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }

    private boolean accept(List<TokenClass> expected) {
        boolean result = false;
        for (TokenClass t: expected)
            result |= t == token.tokenClass;
        return result;
    }


    private void parseProgram() {
        parseIncludes();
        parseDecls();
        parseProcs();
        parseMain();
        expect(TokenClass.EOF);
    }

    private void parseIncludes() {
	    if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseDecls() {
	    variableDeclarations();
    }

    private void variableDeclarations() {
        Token ahead = lookAhead(1);
        if (accept(Token.TYPES) && ahead.tokenClass != TokenClass.MAIN) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
            expect(TokenClass.SEMICOLON);

            variableDeclarations();
        }
    }

    private void parseProcs() {

    }

    private boolean isTypeIdent() {
        return accept(Token.TYPES) && lookAhead(1).tokenClass == TokenClass.IDENTIFIER;
    }

    private boolean isMain() {
        return accept(Token.TYPES) && lookAhead(1).tokenClass == TokenClass.MAIN;
    }

    private void parseTypeIdent() {
        if (isTypeIdent()) {
            nextToken();
            expect(TokenClass.IDENTIFIER);
        }
    }

    private void parseMain() {
        if (isMain()) {
            nextToken(); // consume type

            expect(TokenClass.MAIN);
            expect(TokenClass.LPAR);
            expect(TokenClass.RPAR);

            expect(TokenClass.LBRA);
            parseBody();
            expect(TokenClass.RBRA);
        }
    }

    private void parseBody() {
        // TODO
    }

    public Token getToken() {
        return token;
    }

}
