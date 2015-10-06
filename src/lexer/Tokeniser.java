package lexer;

import lexer.Token.TokenClass;
import lexer.exceptions.UnexpectedCharacter;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;


public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    private final HashMap<Character, TokenClass> uniqueMatchers;
    private final HashMap<String, TokenClass> lookaheadMatchers;


    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;

        this.uniqueMatchers = buildUniqueMatchers();
        this.lookaheadMatchers = buildLookaheadMatchers();
    }

    private HashMap<String, TokenClass> buildLookaheadMatchers() {
        HashMap<String, TokenClass> matchers = new HashMap<>();
        matchers.put("==", TokenClass.EQ);
        matchers.put("!=", TokenClass.NE);
        matchers.put("<=", TokenClass.LE);
        matchers.put(">=", TokenClass.GE);
        return matchers;
    }

    private HashMap<Character, TokenClass> buildUniqueMatchers() {
        HashMap<Character, TokenClass> matchers = new HashMap<>();
        matchers.put('+', TokenClass.PLUS);
        matchers.put(';', TokenClass.SEMICOLON);
        matchers.put(',', TokenClass.COMMA);
        matchers.put('(', TokenClass.LPAR);
        matchers.put(')', TokenClass.RPAR);
        matchers.put('{', TokenClass.LBRA);
        matchers.put('}', TokenClass.RBRA);
        matchers.put('=', TokenClass.ASSIGN);
        matchers.put('-', TokenClass.MINUS);
        matchers.put('*', TokenClass.TIMES);
        matchers.put('%', TokenClass.MOD);
        matchers.put('/', TokenClass.DIV);
        matchers.put('<', TokenClass.LT);
        matchers.put('>', TokenClass.GT);
        return matchers;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    private Token next() throws IOException {

	    // get the next character
        char c = scanner.next();

        int line = scanner.getLine();
        int col = scanner.getColumn();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        // Attempt a peak, otherwise fall back to single tokens
        try {
            char peek = scanner.peek();
            String pair = Character.toString(c) + Character.toString(peek);

            if (lookaheadMatchers.containsKey(pair)) {
                TokenClass tokenClass = lookaheadMatchers.get(pair);
                return buildTokenPair(pair, tokenClass, line, col);
            }

            // There are still comments to consider
            if (pair.equals("//")) return comment(c);
            if (pair.equals("/*")) return multilineComment(c);

        } catch (EOFException e) {
            if (line != scanner.getLine() || col != scanner.getColumn())
                // comments have processed some stream, go again
                return next();
        }


        // Time for single char matches
        if (uniqueMatchers.containsKey(c)) {
            TokenClass tokenClass = uniqueMatchers.get(c);
            return new Token(tokenClass, line, col);
        }

        if (Character.isDigit(c)) return number(c);

        // Headers
        if (c == '#') return include(c);

        // Char
        if (c == '\'') return character(c);

        // String
        if (c == '"') return stringLiteral(c);

        if (Character.isAlphabetic(c)) {
            String identifier = identifier(c);

            if (identifier.equals("int")) return new Token(TokenClass.INT, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("void")) return new Token(TokenClass.VOID, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("char")) return new Token(TokenClass.CHAR, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("if")) return new Token(TokenClass.IF, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("else")) return new Token(TokenClass.ELSE, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("while")) return new Token(TokenClass.WHILE, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("return")) return new Token(TokenClass.RETURN, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("print_s")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("print_c")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("print_i")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("main")) return new Token(TokenClass.MAIN, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("read_c")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("read_i")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
            else return new Token(TokenClass.IDENTIFIER, identifier, line, col);
        }


        // if we reach this point, it means we did not recognise a valid token
        error(c, line, col);
        return new Token(TokenClass.INVALID, Character.toString(c), line, col);
    }

    private Token number(char c) throws IOException {

        int line = scanner.getLine();
        int col = scanner.getColumn() -1; // Need to mark the previous col
        StringBuilder buffer = new StringBuilder(Character.toString(c));

        try {
            while (Character.isDigit(scanner.peek())) {
                buffer.append(scanner.next());
            }
        } catch (EOFException e) {
            if (buffer.charAt(0) == '0' && buffer.length() > 1) {
                // Cannot have a number starting with a 0 and followed by other digits
                error(c, line, col);
                return new Token(TokenClass.INVALID, buffer.toString(), line, col);
            }

            return new Token(TokenClass.NUMBER, buffer.toString(), line, col);
        }

        if (buffer.charAt(0) == '0' && buffer.length() > 1) {
            // Cannot have a number starting with a 0 and followed by other digits
            error(c, line, col);
            return new Token(TokenClass.INVALID, buffer.toString(), line, col);
        }

        return new Token(TokenClass.NUMBER, buffer.toString(), line, col);
    }

    /*
        We have detected that we have # symbol, read the rest and validate.
     */
    public Token include(char startChar /* # */) throws IOException {
        StringBuilder buffer = new StringBuilder(Character.toString(startChar));

        int line = scanner.getLine();
        int col = scanner.getColumn();

        char expected = 'i';
        final String include = "include";
        try {
            for (Character c : include.toCharArray()) {
                expected = c;
                buffer.append(readAndAssert(c));
            }
        }
        catch (UnexpectedCharacter e) {

            buffer.append(e.character);
            error(e.character, scanner.getLine(), scanner.getColumn());
            return new Token(Token.TokenClass.INVALID, buffer.toString(), line, col);
        }
        catch (EOFException e) {
            error(expected, line, col);
            return new Token(TokenClass.INVALID, buffer.toString(), line, col);
        }

        return new Token(Token.TokenClass.INCLUDE, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    public Token character(char c) throws IOException {
        StringBuilder buffer = new StringBuilder();

        // Read until we reach a closing quote
        // Escaped quotes should be skipped
        try {
            c = scanner.next();
            buffer.append(c);

            // There's no content - invalid
            if (c == '\'') {
                error(c, scanner.getLine(), scanner.getColumn());
                return new Token(Token.TokenClass.INVALID, "'" + buffer.toString(), scanner.getLine(), scanner.getColumn());
            }


            if (c == '\\') { // We're escaping
                c = scanner.next();
                buffer.append(c);
                char peek = scanner.peek();

                // No escaped content
                if (c == '\'' && peek != '\'' ) {
                    // Read until we find a quote
                    while (scanner.peek() != '\'') {
                        buffer.append(scanner.next());
                    }

                    // Consume terminating quote
                    buffer.append(scanner.next());

                    error(c, scanner.getLine(), scanner.getColumn());
                    return new Token(Token.TokenClass.INVALID, "'" + buffer.toString(), scanner.getLine(), scanner.getColumn());
                }

            }

            // We should be expecting a closing quote
            if (scanner.peek() != '\'') {
                // Read until we find a quote
                while (scanner.peek() != '\'') {
                    buffer.append(scanner.next());
                }

                // Consume terminating quote
                buffer.append(scanner.next());

                error(c, scanner.getLine(), scanner.getColumn());
                return new Token(Token.TokenClass.INVALID, "'" + buffer.toString(), scanner.getLine(), scanner.getColumn());
            }
//
//            c = scanner.peek();
//
//            while (scanner.peek() != '\'' || isEscaped(c /* previous */)) {
//                c = scanner.next();
//                buffer.append(c);
//            }
        } catch (EOFException e) {
            error(c, scanner.getLine(), scanner.getColumn());
            return new Token(Token.TokenClass.INVALID, "'" + buffer.toString(), scanner.getLine(), scanner.getColumn());
        }

        // Consume closing '
        scanner.next();

        return new Token(Token.TokenClass.CHARACTER, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    public Token stringLiteral(char c) throws IOException {
        StringBuilder buffer = new StringBuilder();

        int line = scanner.getLine();
        int col = scanner.getColumn() - 1;

        try {
            c = scanner.peek();

            while ((scanner.peek() != '"' || isEscaped(c))) {
                c = scanner.next();
                buffer.append(c);
            }

            c = scanner.next(); // consume end quotes

        } catch (EOFException e) {
            error(c, line, col);
            return new Token(Token.TokenClass.INVALID, buffer.toString(), scanner.getLine(), scanner.getColumn());
        }

        int newLineIndex = buffer.indexOf("\n");
        if (newLineIndex > -1) {
            error(buffer.charAt(newLineIndex), line, col + newLineIndex);
            return new Token(TokenClass.INVALID, buffer.toString(), line, col + newLineIndex);
        }

        return new Token(Token.TokenClass.STRING_LITERAL, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    private Token buildTokenPair(String pair, TokenClass tokenClass, int line, int column) throws IOException {
        // We have read the first character, peeked the second. Advance the scanner pointer
        scanner.next();
        return new Token(tokenClass, pair, line, column);
    }

    private Token multilineComment(char c) throws IOException {

        StringBuilder buffer = new StringBuilder(Character.toString(c));
        // Consume astrix
        buffer.append(scanner.next());

        c = scanner.next();
        // Start reading comment
        buffer.append(c);

        while (!buffer.substring(buffer.length() - 2, buffer.length()).equals("*/")) {
            c = scanner.next();
            buffer.append(c);

//            if (c == '\\') { // We're escaping
//                // Force read the next char to consume it
//                c = scanner.next();
//                buffer.append(c);
//            }

        }

        return next();
    }

    private Token comment(char c) throws IOException {
        // Go to the end of line
        char next = scanner.peek();
        while (next != '\n') {
            scanner.next();
            next = scanner.peek();
        }
        return next();
    }

    private String identifier(char c) throws IOException {
        StringBuilder buffer = new StringBuilder(Character.toString(c));

        try {
            char peek = scanner.peek();

            if (Character.isAlphabetic(peek))
                buffer.append(scanner.next());

            peek = scanner.peek();
            while (Character.isAlphabetic(peek) || Character.isDigit(peek) || peek == '_') {
                buffer.append(scanner.next());
                peek = scanner.peek();
            }

        } catch (EOFException e) {
            return buffer.toString();
        }
        return buffer.toString();
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
        error++;
    }

    private char readAndAssert(char expected) throws IOException, UnexpectedCharacter {
        char next = scanner.next();

        if (next != expected)
            throw new UnexpectedCharacter(next, scanner.getLine(), scanner.getColumn());

        return next;
    }

    private boolean isEscaped(char c) {
        return c == '\\';
    }

}
