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
    public void nextToken() {
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
        parseVariableDeclarations();
        parseProcedures();
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

    private void parseVariableDeclarations() {
        if (isVariableDeclaration()) {
            parseVariableDeclaration();
            parseVariableDeclarations();
        }
    }

    private void parseVariableDeclaration() {
        parseTypeIdent();
        expect(TokenClass.SEMICOLON);
    }

    public void parseProcedures() {
        if (isProcedure()) {
            parseProcedure();
            parseProcedures();
        }
    }

    private void parseProcedure() {
        parseTypeIdent();
        expect(TokenClass.LPAR);
        parseParams();
        expect(TokenClass.RPAR);
        parseBody();

    }

    private void parseParams() {
        if (isTypeIdentifier()) {
            parseTypeIdent();

            if (isParamRepetition()) {
                expect(TokenClass.COMMA);
                parseParams();
            }
        }
    }

//    private void parseParamRepetition() {
//        expect(TokenClass.COMMA);
//        parseTypeIdent();
//
//        parseParams();
//    }


    private boolean isParamRepetition() {
        return accept(TokenClass.COMMA);
    }


    private void parseType() {
        expect(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID);
    }

    private boolean isProcedure() {
        return isTypeIdentifier();
    }

    private boolean isTypeIdentifier() {
        return isType() && lookAhead(1).tokenClass == TokenClass.IDENTIFIER;
    }

    private boolean isType() {
        return accept(Token.TYPES);
    }

    private boolean isVariableDeclaration() {
        return isTypeIdentifier() && lookAhead(2).tokenClass == TokenClass.SEMICOLON;
    }

    private boolean isMain() {
        return accept(Token.TYPES) && lookAhead(1).tokenClass == TokenClass.MAIN;
    }

    private void parseTypeIdent() {
            parseType();
            expect(TokenClass.IDENTIFIER);
    }

    private void parseMain() {
        nextToken(); // consume type

        expect(TokenClass.MAIN);
        expect(TokenClass.LPAR);
        expect(TokenClass.RPAR);

        parseBody();
    }

    public void parseBody() {
        expect(TokenClass.LBRA);
        parseVariableDeclarations();
        parserStatementList();
        expect(TokenClass.RBRA);
    }

    private void parserStatementList() {
        if (isStatement()) {
            parseStatement();
            parserStatementList();
        }
    }

    public void parseStatement() {
        if (isFuncationCall()) {
            parseFunctionCall();
            expect(TokenClass.SEMICOLON);
            return;
        }

        if (isPrintString()) {
            expect(TokenClass.PRINT);
            expect(TokenClass.LPAR);
            expect(TokenClass.STRING_LITERAL);
            expect(TokenClass.RPAR);
            expect(TokenClass.SEMICOLON);
            return;
        }

        switch (token.tokenClass) {
            case LBRA:
                expect(TokenClass.LBRA);
                parseVariableDeclarations();
                parserStatementList();
                expect(TokenClass.RBRA);
                break;

            case WHILE:
                expect(TokenClass.WHILE);
                expect(TokenClass.LPAR);
                parseExpression();
                expect(TokenClass.RPAR);
                parseStatement();
                break;

            case IF:
                expect(TokenClass.IF);
                expect(TokenClass.LPAR);
                parseExpression();
                expect(TokenClass.RPAR);
                parseStatement();
                parseElseStatement();
                break;

            case IDENTIFIER:
                expect(TokenClass.IDENTIFIER);
                expect(TokenClass.ASSIGN);
                parseLexicalExpression();
                expect(TokenClass.SEMICOLON);
                break;

            case RETURN:
                expect(TokenClass.RETURN);
                parseReturnOptional();
                expect(TokenClass.SEMICOLON);
                break;

            case PRINT:
                expect(TokenClass.PRINT);
                expect(TokenClass.LPAR);
                parseLexicalExpression();
                expect(TokenClass.RPAR);
                expect(TokenClass.SEMICOLON);
                break;

            case READ:
                expect(TokenClass.READ);
                expect(TokenClass.LPAR);
                expect(TokenClass.RPAR);
                expect(TokenClass.SEMICOLON);
                break;

            default:
                error(
                        TokenClass.LBRA,
                        TokenClass.WHILE,
                        TokenClass.IF,
                        TokenClass.IDENTIFIER,
                        TokenClass.READ);
        }
    }

    private boolean isPrintString() {
        return token.tokenClass == TokenClass.PRINT && token.data.equals(Token.PRINT_S);
    }

    public void parseFunctionCall() {
        expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        parseArgumentList();
        expect(TokenClass.RPAR);
    }

    private void parseArgumentList() {
        if (accept(TokenClass.IDENTIFIER)) {
            expect(TokenClass.IDENTIFIER);
            parseArgumentRepetition();
        }
    }

    private void parseArgumentRepetition() {
        if (accept(TokenClass.COMMA)) {
            expect(TokenClass.COMMA);
            expect(TokenClass.IDENTIFIER);

            parseArgumentRepetition();
        }
    }

    private void parseReturnOptional() {
        if (!accept(TokenClass.SEMICOLON)) {
            parseLexicalExpression();
        }
    }

    public void parseLexicalExpression() {
        if (isTerm()) {
            parseTerm();
            parseLexicalExpressionRepetition();
        }
        else errorExpectedFactor();


    }

    private boolean isTerm() {
        return isFactor();
    }

    private boolean isFactor() {
        return accept(TokenClass.MINUS, TokenClass.IDENTIFIER, TokenClass.NUMBER, TokenClass.CHARACTER, TokenClass.LPAR, TokenClass.READ) || isFuncationCall();
    }

    private void errorExpectedFactor() {
        error(TokenClass.MINUS, TokenClass.IDENTIFIER, TokenClass.NUMBER, TokenClass.CHARACTER, TokenClass.LPAR);
    }

    private void parseLexicalExpressionRepetition() {
        if (isLexicalExpressionRep()) {
            expect(TokenClass.PLUS, TokenClass.MINUS);
            parseTerm();
            parseLexicalExpressionRepetition();
        }
    }

    private boolean isLexicalExpressionRep() {
        return accept(TokenClass.PLUS, TokenClass.MINUS);
    }

    public void parseTerm() {
        parseFactor();
        parseTermRepetition();
    }

    private void parseTermRepetition() {
        if (isTermRepetition()) {
            expect(TokenClass.DIV, TokenClass.TIMES, TokenClass.MOD);
            parseFactor();
            parseTermRepetition();
        }
    }

    private boolean isTermRepetition() {
        return accept(TokenClass.DIV, TokenClass.TIMES, TokenClass.MOD);
    }

    private void parseFactor() {
        if (isFuncationCall()) {
            parseFunctionCall();
        }

        switch (token.tokenClass) {
            case LPAR:
                expect(TokenClass.LPAR);
                parseLexicalExpression();
                expect(TokenClass.RPAR);
                break;

            case IDENTIFIER:
                expect(TokenClass.IDENTIFIER);
                break;

            case NUMBER:
                expect(TokenClass.NUMBER);
                break;

            case MINUS:
                expect(TokenClass.MINUS);
                expect(TokenClass.IDENTIFIER, TokenClass.NUMBER);
                break;

            case CHARACTER:
                expect(TokenClass.CHARACTER);
                break;

            case READ:
                expect(TokenClass.READ);
                expect(TokenClass.LPAR);
                expect(TokenClass.RPAR);
                break;
//
//            default:
//                error(TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.NUMBER, TokenClass.MINUS, TokenClass.CHARACTER, TokenClass.READ);
//                break;
        }
    }

    private void parseElseStatement() {
        if (isElse()) {
            expect(TokenClass.ELSE);
            parseStatement();
        }
    }

    private boolean isElse() {
        return accept(TokenClass.ELSE);
    }

    public void parseExpression() {
        parseLexicalExpression();

        if (isComparator(token)) {
            expect(TokenClass.GT,
                    TokenClass.LT,
                    TokenClass.GE,
                    TokenClass.LE,
                    TokenClass.NE,
                    TokenClass.EQ);
            parseLexicalExpression();
        }
    }

    private boolean isComparator(Token ahead) {
        return accept(Token.COMPARATORS);
    }

    private boolean isStatement() {
        return accept(
                TokenClass.LBRA,
                TokenClass.WHILE,
                TokenClass.IF,
                TokenClass.IDENTIFIER,
                TokenClass.RETURN,
                TokenClass.PRINT,
                TokenClass.READ
        ) || isFuncationCall();
    }

    private boolean isFuncationCall() {
        return accept(TokenClass.IDENTIFIER) && lookAhead(1).tokenClass == TokenClass.LPAR;
    }

    public Token getToken() {
        return token;
    }

}
