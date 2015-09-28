package lexer;

import lexer.Token.TokenClass;
import lexer.exceptions.UnexpectedCharacter;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author cdubach
 */
public class Tokeniser {

    private static final Logger LOGGER = Logger.getLogger(Tokeniser.class.getName());

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

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

	    // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        // Attempt a peak, otherwise fall back to single tokens
        try {
            char peek = scanner.peek();
            String pair = Character.toString(c) + Character.toString(peek);

            if (lookaheadMatchers.containsKey(pair)) {
                TokenClass tokenClass = lookaheadMatchers.get(pair);
                return buildTokenPair(pair, tokenClass, line, column);
            }

            // There are still comments to consider
            if (pair.equals("//")) return comment(c);
            if (pair.equals("/*")) return multilineComment(c);

        } catch (EOFException e) {
            // Token cannot be a lookahead token
            LOGGER.log(Level.INFO, "Attempted to perform a look ahead match. EOF encountered. Falling back to single char matchers.");
        }


        // Time for single char matches
        if (uniqueMatchers.containsKey(c)) {
            TokenClass tokenClass = uniqueMatchers.get(c);
            return new Token(tokenClass, line, column);
        }

        if (Character.isDigit(c)) return number(c);

        // Headers
        if (c == '#') return include(c);

        // Char
        if (c == '\'') return character();

        // String
        if (c == '"') return stringLiteral();

        String identifier = identifier(c);

        if (identifier.equals("int")) return new Token(TokenClass.INT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("void")) return new Token(TokenClass.VOID, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("char")) return new Token(TokenClass.CHAR, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("if")) return new Token(TokenClass.IF, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("else")) return new Token(TokenClass.ELSE, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("while")) return new Token(TokenClass.WHILE, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("return")) return new Token(TokenClass.RETURN, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("print_s")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("print_c")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("print_i")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("main")) return new Token(TokenClass.MAIN, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("read_c")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("read_i")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
        else return new Token(TokenClass.IDENTIFIER, identifier, line, column);


//        if we reach this point, it means we did not recognise a valid token
//        error(c,line,column);
//        return new Token(TokenClass.INVALID, line, column);
    }

    private Token number(char c) throws IOException {
        StringBuilder buffer = new StringBuilder(Character.toString(c));

        int line = scanner.getLine();
        int col = scanner.getColumn();

        try {
            while (Character.isDigit(scanner.peek())) {
                buffer.append(scanner.next());
            }
        } catch (EOFException e) {
            return new Token(TokenClass.NUMBER, buffer.toString(), line, col);
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

            // Read until the next whitespace, mark everything as unexpected
//            if (!Character.isWhitespace(e.character))
//                buffer.append(readToWhitespace());

            LOGGER.log(Level.WARNING, "Failed to parse include: " + buffer.toString());
            return new Token(Token.TokenClass.INVALID, buffer.toString(), line, col);
        }
        catch (EOFException e) {
            error(expected, line, col);
            return new Token(TokenClass.INVALID, buffer.toString(), line, col);
        }

        return new Token(Token.TokenClass.INCLUDE, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    public Token character() throws IOException {
        StringBuilder buffer = new StringBuilder();

        // Read until we reach a closing quote
        // Escaped quotes should be skipped
        char c;
        try {
            c = scanner.peek();

            while (scanner.peek() != '\'' || isEscaped(c /* previous */)) {
                c = scanner.next();
                buffer.append(c);
            }
        } catch (EOFException e) {
            return new Token(Token.TokenClass.INVALID, "'" + buffer.toString(), scanner.getLine(), scanner.getColumn());
        }

        // Consume closing '
        scanner.next();

        return new Token(Token.TokenClass.CHARACTER, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    public Token stringLiteral() throws IOException {
        StringBuilder buffer = new StringBuilder();

        char c;
        try {
            c = scanner.peek();
            while (scanner.peek() != '"' || isEscaped(c)) {
                c = scanner.next();
                buffer.append(c);
            }
        } catch (EOFException e) {
            return new Token(Token.TokenClass.INVALID, buffer.toString(), scanner.getLine(), scanner.getColumn());
        }

        scanner.next(); // consume end quotes
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

        char last = scanner.next();
        // Start reading comment
        buffer.append(last);


        while (c != '*' && scanner.peek() != '/' && last != '\\') {
            c = scanner.next();
            buffer.append(c);

            if (c == '\\') { // We're escaping
                // Force read the next char to consume it
                c = scanner.next();
                buffer.append(c);
            }

            last = c;
        }

        // We've reached closing *, consume /
        buffer.append(scanner.next());

//        System.out.println("Multiline Comment: " + buffer);
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

    private String readToWhitespace() throws IOException {
        StringBuilder buffer = new StringBuilder();

        while (!Character.isWhitespace(scanner.peek())) {
            char next = scanner.next();
            buffer.append(next);
        }

        return buffer.toString();
    }

    private boolean isEscaped(char c) {
        return c == '\\';
    }

}
