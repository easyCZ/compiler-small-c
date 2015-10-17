package parser;

import ast.*;
import ast.expressions.Var;
import ast.statements.FunCallStmt;
import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;
import parser.wrappers.TypeIdentifier;

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

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
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


    private Program parseProgram() {
        parseIncludes();
        List<VarDecl> varDecls = parseVariableDeclarations();
        List<Procedure> procs = parseProcedures();
        Procedure main = parseMain();

        expect(TokenClass.EOF);
        return new Program(varDecls, procs, main);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
	    if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    public List<VarDecl> parseVariableDeclarations() {
        List<VarDecl> variableDeclarations = new LinkedList<>();

        if (isVariableDeclaration()) {
            variableDeclarations.add(parseVariableDeclaration());
            variableDeclarations.addAll(parseVariableDeclarations());
        }

        return variableDeclarations;
    }

    private VarDecl parseVariableDeclaration() {

        TypeIdentifier typeIdentifier = parseTypeIdent();
        expect(TokenClass.SEMICOLON);
        return new VarDecl(typeIdentifier.type, typeIdentifier.var);
    }

    public List<Procedure> parseProcedures() {
        List<Procedure> procedures = new LinkedList<>();
        if (isProcedure()) {
            procedures.add(parseProcedure());
            procedures.addAll(parseProcedures());
        }
        return procedures;
    }

    private Procedure parseProcedure() {
        TypeIdentifier typeIdentifier = parseTypeIdent();
        expect(TokenClass.LPAR);
        List<VarDecl> params = parseParams();
        expect(TokenClass.RPAR);
        Block block = parseBody();

        return new Procedure(
                typeIdentifier.type,
                typeIdentifier.var.name,
                params,
                block
        );
    }

    private List<VarDecl> parseParams() {
        List<VarDecl> params = new LinkedList<>();

        if (isTypeIdentifier()) {
            TypeIdentifier ti = parseTypeIdent();
            params.add(new VarDecl(ti.type, ti.var));

            if (isParamRepetition()) {
                expect(TokenClass.COMMA);
                params.addAll(parseParams());
            }
        }

        return params;
    }

    private boolean isParamRepetition() {
        return accept(TokenClass.COMMA);
    }


    private Type parseType() {
        Token t = expect(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID);

        assert t != null;
        switch (t.tokenClass) {
            case INT:
                return Type.INT;
            case CHAR:
                return Type.CHAR;
            case VOID:
                return Type.VOID;
        }

        return null;
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

    private TypeIdentifier parseTypeIdent() {
        Type type = parseType();
        Token identifier = expect(TokenClass.IDENTIFIER);
        return new TypeIdentifier(type, new Var(identifier.data));
    }

    private Procedure parseMain() {
        nextToken(); // consume type

        expect(TokenClass.MAIN);
        expect(TokenClass.LPAR);
        expect(TokenClass.RPAR);

        parseBody();

        // TODO: AST
        return null;
    }

    public Block parseBody() {
        expect(TokenClass.LBRA);
        List<VarDecl> varDecls = parseVariableDeclarations();
        List<Stmt> statements = parserStatementList();
        expect(TokenClass.RBRA);

        return new Block(varDecls, statements);
    }

    private List<Stmt> parserStatementList() {
        List<Stmt> statements = new LinkedList<>();
        if (isStatement()) {
            statements.add(parseStatement());
            statements.addAll(parserStatementList());
        }

        return statements;
    }

    public Stmt parseStatement() {
        if (isFuncationCall()) {
            FunCallStmt funCallStmt = parseFunctionCall();
            expect(TokenClass.SEMICOLON);
            return funCallStmt;
        }

        if (isPrintString()) {
            Token print = expect(TokenClass.PRINT);
            expect(TokenClass.LPAR);
            Token argument = expect(TokenClass.STRING_LITERAL);
            expect(TokenClass.RPAR);
            expect(TokenClass.SEMICOLON);

            List<Expr> arguments = new LinkedList<>();
            arguments.add(new StrLiteral(argument.data));
            return new FunCallStmt(print.data, arguments);
        }

        switch (token.tokenClass) {
            case LBRA:
                expect(TokenClass.LBRA);
                parseVariableDeclarations();
                parserStatementList();
                expect(TokenClass.RBRA);
                // TODO: AST
                return null;

            case WHILE:
                expect(TokenClass.WHILE);
                expect(TokenClass.LPAR);
                parseExpression();
                expect(TokenClass.RPAR);
                parseStatement();
                // TODO: AST
                return null;

            case IF:
                expect(TokenClass.IF);
                expect(TokenClass.LPAR);
                parseExpression();
                expect(TokenClass.RPAR);
                parseStatement();
                parseElseStatement();
                // TODO: AST
                return null;

            case IDENTIFIER:
                expect(TokenClass.IDENTIFIER);
                expect(TokenClass.ASSIGN);
                parseLexicalExpression();
                expect(TokenClass.SEMICOLON);
                // TODO: AST
                return null;

            case RETURN:
                expect(TokenClass.RETURN);
                parseReturnOptional();
                expect(TokenClass.SEMICOLON);
                // TODO: AST
                return null;

            case PRINT:
                expect(TokenClass.PRINT);
                expect(TokenClass.LPAR);
                parseLexicalExpression();
                expect(TokenClass.RPAR);
                expect(TokenClass.SEMICOLON);
                // TODO: AST
                return null;

            case READ:
                expect(TokenClass.READ);
                expect(TokenClass.LPAR);
                expect(TokenClass.RPAR);
                expect(TokenClass.SEMICOLON);
                // TODO: AST
                return null;

            default:
                error(
                        TokenClass.LBRA,
                        TokenClass.WHILE,
                        TokenClass.IF,
                        TokenClass.IDENTIFIER,
                        TokenClass.READ);
                return null;
        }
    }

    private boolean isPrintString() {
        return token.tokenClass == TokenClass.PRINT && token.data.equals(Token.PRINT_S);
    }

    public FunCallStmt parseFunctionCall() {
        Token identifier = expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        List<Expr> arguments = parseArgumentList();
        expect(TokenClass.RPAR);

        return new FunCallStmt(identifier.data, arguments);
    }

    private List<Expr> parseArgumentList() {
        List<Expr> arguments = new LinkedList<>();

        if (accept(TokenClass.IDENTIFIER)) {
            Token t = expect(TokenClass.IDENTIFIER);
            arguments.add(new Var(t.data));
            arguments.addAll(parseArgumentRepetition());
        }

        return arguments;
    }

    private List<Expr> parseArgumentRepetition() {
        List<Expr> arguments = new LinkedList<>();
        if (accept(TokenClass.COMMA)) {
            expect(TokenClass.COMMA);
            Token t = expect(TokenClass.IDENTIFIER);
            arguments.add(new Var(t.data));

            arguments.addAll(parseArgumentRepetition());
        }

        return arguments;
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
