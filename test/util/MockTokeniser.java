package util;


import lexer.Scanner;
import lexer.Token;
import lexer.Tokeniser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MockTokeniser extends Tokeniser {

    private final Queue<Token> tokens;

    /*
        Simply provide tokens from the chain
     */
    public MockTokeniser(List<Token> tokens) {
        super(new Scanner(""));

        List<Token> cp = new ArrayList<>(tokens);
        cp.add(new Token(Token.TokenClass.EOF));

        this.tokens = new ArrayDeque<>(cp);
    }

    @Override
    public Token nextToken() {
        return tokens.poll();
    }
}
