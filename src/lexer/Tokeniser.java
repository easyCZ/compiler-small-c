package lexer;

import lexer.Token.TokenClass;
import lexer.exceptions.UnexpectedCharacter;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    private final HashMap<Character, TokenClass> uniqueMatchers;
    private final HashMap<String, TokenClass> lookaheadMatchers;
    private final List<String> ESCAPABLES = Arrays.asList(
            "\\t",
            "\\b",
            "\\n",
            "\\r",
            "\\f",
            "\\'",
            "\\\"",
            "\\\\"
    );


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

        if (Character.isAlphabetic(c) || c == '_') {
            String identifier = identifier(c);

            if (identifier.equals("int")) return new Token(TokenClass.INT, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("void")) return new Token(TokenClass.VOID, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("char")) return new Token(TokenClass.CHAR, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("if")) return new Token(TokenClass.IF, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("else")) return new Token(TokenClass.ELSE, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("while")) return new Token(TokenClass.WHILE, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("return")) return new Token(TokenClass.RETURN, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals(Token.PRINT_S)) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals(Token.PRINT_C)) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals(Token.PRINT_I)) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("main")) return new Token(TokenClass.MAIN, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("read_c")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
            else if (identifier.equals("read_i")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
            else return new Token(TokenClass.IDENTIFIER, identifier, line, col);
        }

        // if we reach this point, it means we did not recognise a valid token
        return error(c, Character.toString(c), line, col);
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
                return error(c, buffer.toString(), line, col);
            }

            return new Token(TokenClass.NUMBER, buffer.toString(), line, col);
        }

        if (buffer.charAt(0) == '0' && buffer.length() > 1) {
            // Cannot have a number starting with a 0 and followed by other digits
            return error(c, buffer.toString(), line, col);
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
            return error(expected, buffer.toString(), line, col);
        }
        catch (EOFException e) {
            return error(expected, buffer.toString());
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
                return error(c, "'" + buffer.toString());
            }

            if (isBackslash(c)) buffer.append(escape(c));

            // We should be expecting a closing quote
            if (scanner.peek() != '\'') {
                // Read until we find a quote
                while (scanner.peek() != '\'') {
                    buffer.append(scanner.next());
                }

                // Consume terminating quote
                buffer.append(scanner.next());

                return error(c, "'" + buffer.toString());
            }
        }
        catch (EOFException e) {
            return error(c, "'" + buffer.toString());
        }
        catch (UnexpectedCharacter unexpectedCharacter) {
            // Read until we find a quote
            while (scanner.peek() != '\'') {
                buffer.append(scanner.next());
            }

            // Consume terminating quote
            buffer.append(scanner.next());

            return error(c, buffer.toString(), scanner.getLine(), scanner.getColumn());
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

            while (scanner.peek() != '"') {

                c = scanner.next();
                buffer.append(c);

                if (isBackslash(c)) buffer.append(escape(c));
            }

            c = scanner.next(); // consume end quotes

        }
        catch (EOFException e) {
            return error(c, buffer.toString());
        }

        catch (UnexpectedCharacter uc) {
            // read until next "
            while (scanner.peek() != '"') {
                c = scanner.next();
                buffer.append(c);
            }

            // Read closing "
            c = scanner.next();
            buffer.append(c);

            return error(uc.character, "\"" + buffer.toString(), uc.line, uc.col);
        }

        int newLineIndex = buffer.indexOf("\n");
        if (newLineIndex > -1) {
            return error(buffer.charAt(newLineIndex), buffer.toString(), line, col + newLineIndex);
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

    private Token error(char c, String data, int line, int col) {
        error(c, line, col);
        return new Token(TokenClass.INVALID, data, line, col);
    }

    private Token error(char c, String data) {
        error(c, scanner.getLine(), scanner.getColumn());
        return new Token(TokenClass.INVALID, data, scanner.getLine(), scanner.getColumn());
    }

    private char readAndAssert(char expected) throws IOException, UnexpectedCharacter {
        char next = scanner.next();

        if (next != expected)
            throw new UnexpectedCharacter(next, scanner.getLine(), scanner.getColumn());

        return next;
    }

    private char escape(char c) throws UnexpectedCharacter, IOException {
        // Received a \, next should be an escape char
        if (ESCAPABLES.contains(Character.toString(c) + scanner.peek())) {
            // consume and pass back
            return scanner.next();
        }
        throw new UnexpectedCharacter(scanner.peek(), scanner.getLine(), scanner.getColumn());
    }

    private boolean isBackslash(char c) {
        return c == '\\';
    }

}
