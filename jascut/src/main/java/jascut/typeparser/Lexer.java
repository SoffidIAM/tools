/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.typeparser;

/**
 * The lexer for the type parser.
 *
 * @author tronicek
 */
public class Lexer {

    String input;
    int i;
    char c;
    String ident;

    Lexer(String input) {
        this.input = input;
        c = input.charAt(i);
    }

    void nextChar() {
        i++;
        if (i < input.length()) {
            c = input.charAt(i);
        }
    }

    void skipSpace() {
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }
        if (i < input.length()) {
            c = input.charAt(i);
        }
    }

    Token next() {
        skipSpace();
        if (i == input.length()) {
            return Token.EOI;
        }
        if (Character.isJavaIdentifierStart(c)) {
            int j = i;
            do {
                nextChar();
            } while (i < input.length() && (Character.isJavaIdentifierPart(c) || c == '.'));
            ident = input.substring(j, i);
            if ("extends".equals(ident)) {
                return Token.EXTENDS;
            }
            if ("super".equals(ident)) {
                return Token.SUPER;
            }
            return Token.IDENT;
        }
        if (c == '<') {
            nextChar();
            return Token.LESS_THAN;
        }
        if (c == '>') {
            nextChar();
            return Token.GREATER_THAN;
        }
        if (c == ',') {
            nextChar();
            return Token.COMMA;
        }
        if (c == '?') {
            nextChar();
            return Token.QUEST;
        }
        if (c == '&') {
            nextChar();
            return Token.AMPER;
        }
        if (c == '[') {
            nextChar();
            return Token.LEFT_BRACKET;
        }
        if (c == ']') {
            nextChar();
            return Token.RIGHT_BRACKET;
        }
        throw new ParseException("unexpected char: " + c);
    }
}
