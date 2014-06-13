/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.typeparser;

import java.util.ArrayList;

/**
 * The type parser.
 *
 * @author tronicek
 */
public class TypeParser {

    Lexer lexer;
    Token token;
    StringBuilder template = new StringBuilder();
    ArrayList<String> types = new ArrayList<String>();

    public static TypeParser parse(String input) {
        TypeParser p = new TypeParser(input);
        p.type();
        return p;
    }

    TypeParser(String input) {
        this.lexer = new Lexer(input);
        token = lexer.next();
    }

    public String getTemplate() {
        return template.toString();
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    void consume(Token exp) {
        if (exp != token) {
            throw new ParseException("expected: " + exp + ", found: " + token);
        }
        token = lexer.next();
    }

    void type() {
        template.append("%s");
        types.add(lexer.ident);
        consume(Token.IDENT);
        typeParams();
        brackets();
    }

    void typeParams() {
        if (token == Token.LESS_THAN) {
            consume(Token.LESS_THAN);
            template.append("<");
            typeParamList();
            consume(Token.GREATER_THAN);
            template.append(">");
        }
    }

    void brackets() {
        while (token == Token.LEFT_BRACKET) {
            consume(Token.LEFT_BRACKET);
            consume(Token.RIGHT_BRACKET);
            template.append("[]");
        }
    }

    void typeParamList() {
        typeParam();
        typeParamListRest();
    }

    void typeParam() {
        switch (token) {
            case IDENT:
                type();
                break;
            case QUEST:
                wildcard();
                break;
            default:
                throw new ParseException("expected IDENT or QUEST, found: " + token);
        }
    }

    void typeParamListRest() {
        while (token == Token.COMMA) {
            consume(Token.COMMA);
            template.append(", ");
            typeParam();
        }
    }

    void wildcard() {
        consume(Token.QUEST);
        template.append("?");
        switch (token) {
            case EXTENDS:
                consume(Token.EXTENDS);
                template.append(" extends ");
                typeBounds();
                break;
            case SUPER:
                consume(Token.SUPER);
                template.append(" super ");
                typeBounds();
                break;
            default:
                ;
        }
    }

    void typeBounds() {
        type();
        typeBoundsRest();
    }

    void typeBoundsRest() {
        while (token == Token.AMPER) {
            consume(Token.AMPER);
            template.append(" & ");
            type();
        }
    }
}
