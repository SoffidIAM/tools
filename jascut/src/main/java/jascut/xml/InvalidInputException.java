/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut.xml;

/**
 * The exception that is thrown when input is not valid.
 *
 * @author tronicek
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
