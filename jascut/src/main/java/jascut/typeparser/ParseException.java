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
 * The exception that is thrown when parsing fails.
 *
 * @author tronicek
 */
public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }
}
