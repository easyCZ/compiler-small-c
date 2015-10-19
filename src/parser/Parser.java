package parser;

import ast.*;
import ast.expressions.ChrLiteral;
import ast.expressions.FunCallExpr;
import ast.expressions.IntLiteral;
import ast.expressions.Var;
import ast.statements.*;
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

    public Procedure parseMain() {
        Token t = expect(TokenClass.VOID);
        Type type = Type.VOID;

        expect(TokenClass.MAIN);
        expect(TokenClass.LPAR);
        expect(TokenClass.RPAR);

        Block body = parseBody();
        return new Procedure(type, "main", new LinkedList<VarDecl>(), body);
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

            if (argument != null) {
                arguments.add(new StrLiteral(argument.data));
            }

            return new FunCallStmt(print.data, arguments);
        }

        switch (token.tokenClass) {
            case LBRA:
                expect(TokenClass.LBRA);
                List<VarDecl> varDecls = parseVariableDeclarations();
                List<Stmt> statements = parserStatementList();
                expect(TokenClass.RBRA);

                return new Block(varDecls, statements);

            case WHILE:
                expect(TokenClass.WHILE);
                expect(TokenClass.LPAR);
                Expr whileExpr = parseExpression();
                expect(TokenClass.RPAR);
                Stmt whileStmt = parseStatement();
                return new While(whileExpr, whileStmt);

            case IF:
                expect(TokenClass.IF);
                expect(TokenClass.LPAR);
                Expr ifExpr = parseExpression();
                expect(TokenClass.RPAR);
                Stmt ifStatement = parseStatement();
                Stmt elseStatement = parseElseStatement();
                return new If(ifExpr, ifStatement, elseStatement);

            case IDENTIFIER:
                Token var = expect(TokenClass.IDENTIFIER);
                expect(TokenClass.ASSIGN);
                Expr expr = parseLexicalExpression();
                expect(TokenClass.SEMICOLON);

                if (var != null & expr != null)
                    return new Assign(new Var(var.data), expr);
                return null;

            case RETURN:
                expect(TokenClass.RETURN);
                Expr returnz = parseReturnOptional();
                expect(TokenClass.SEMICOLON);
                return new Return(returnz);

            case PRINT:
                Token print = expect(TokenClass.PRINT);
                expect(TokenClass.LPAR);
                Expr argExpr = parseLexicalExpression();
                expect(TokenClass.RPAR);
                expect(TokenClass.SEMICOLON);

                List<Expr> arguments = new LinkedList<Expr>();
                if (argExpr != null) arguments.add(argExpr);
                if (print != null) return new FunCallStmt(print.data, arguments);
                return null;

            case READ:
                Token read = expect(TokenClass.READ);
                expect(TokenClass.LPAR);
                expect(TokenClass.RPAR);
                expect(TokenClass.SEMICOLON);
                if (read != null)
                    return new FunCallStmt(read.data, new LinkedList<Expr>());
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

            if (t != null) arguments.add(new Var(t.data));

            arguments.addAll(parseArgumentRepetition());
        }

        return arguments;
    }

    private Expr parseReturnOptional() {
        if (!accept(TokenClass.SEMICOLON)) {
            return parseLexicalExpression();
        }
        return null;
    }

    public Expr parseLexicalExpression() {
        if (isTerm()) {
            Expr term = parseTerm();
            return parseLexicalExpressionRepetition(term);
        }
        else errorExpectedFactor();

        return null;
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

    private Expr parseLexicalExpressionRepetition(Expr lhs) {
        if (isLexicalExpressionRep()) {
            Token t = expect(TokenClass.PLUS, TokenClass.MINUS);
            Expr term = parseTerm();
            BinOp rhs = new BinOp(lhs, Op.getOp(t.tokenClass), term);
            return parseLexicalExpressionRepetition(rhs);
        }
        return lhs;
    }

    private boolean isLexicalExpressionRep() {
        return accept(TokenClass.PLUS, TokenClass.MINUS);
    }

    public Expr parseTerm() {
        Expr factor = parseFactor();
        return parseTermRepetition(factor);
    }

    private Expr parseTermRepetition(Expr lhs) {
        if (isTermRepetition()) {

            Token operation = expect(TokenClass.DIV, TokenClass.TIMES, TokenClass.MOD);
            Expr rhs = parseTermRepetition(parseFactor());
            if (operation != null && rhs != null) {
                return new BinOp(lhs, Op.getOp(operation.tokenClass), rhs);
            }
        }
        return lhs;
    }

    private boolean isTermRepetition() {
        return accept(TokenClass.DIV, TokenClass.TIMES, TokenClass.MOD);
    }

    public Expr parseFactor() {

        if (isFuncationCall()) {
            FunCallStmt funCallStmt = parseFunctionCall();
            return new FunCallExpr(funCallStmt.name, funCallStmt.arguments);
        }

        switch (token.tokenClass) {
            case LPAR:
                expect(TokenClass.LPAR);
                Expr expr = parseLexicalExpression();
                expect(TokenClass.RPAR);
                return expr;

            case IDENTIFIER:
                Token identifier = expect(TokenClass.IDENTIFIER);
                return new Var(identifier.data);

            case NUMBER:
                Token number = expect(TokenClass.NUMBER);
                return new IntLiteral(Integer.parseInt(number.data));

            case MINUS:
                expect(TokenClass.MINUS);
                Token val = expect(TokenClass.IDENTIFIER, TokenClass.NUMBER);

                IntLiteral zero = new IntLiteral(0);
                if (val == null) return zero;

                Op op = Op.SUB;
                return new BinOp(
                        zero,
                        op,
                        val.tokenClass == TokenClass.IDENTIFIER
                            ? new Var(val.data)
                            : new IntLiteral(Integer.parseInt(val.data))
                );

            case CHARACTER:
                Token c = expect(TokenClass.CHARACTER);
                return new ChrLiteral(c.data.charAt(0));

            case READ:
                Token read = expect(TokenClass.READ);
                expect(TokenClass.LPAR);
                expect(TokenClass.RPAR);

                if (read != null)
                    return new FunCallExpr(read.data, new LinkedList<Expr>());
                return null;

        }

        return null;
    }

    private Stmt parseElseStatement() {
        if (isElse()) {
            expect(TokenClass.ELSE);
            return parseStatement();
        }
        return null;
    }

    private boolean isElse() {
        return accept(TokenClass.ELSE);
    }

    public Expr parseExpression() {
        Expr lhs = parseLexicalExpression();

        if (isComparator(token)) {
            Token t = expect(TokenClass.GT,
                    TokenClass.LT,
                    TokenClass.GE,
                    TokenClass.LE,
                    TokenClass.NE,
                    TokenClass.EQ);

            Op op = Op.getOp(t.tokenClass);
            Expr rhs = parseLexicalExpression();
            return new BinOp(lhs, op, rhs);
        }

        return lhs;
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
