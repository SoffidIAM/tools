/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut;

import java.io.CharArrayWriter;

/**
 * The character stream that counts written characters.
 *
 * @author tronicek
 */
public class PositionWriter extends CharArrayWriter {

    public PositionWriter(int initialSize) {
        super(initialSize);
    }

    public int getCount() {
        return count;
    }
}
