/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package refactoring;

import com.sun.source.tree.LineMap;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Context;
import jascut.CompilationUnit;

/**
 * The abstract parent of refactoring scanners.
 *
 * @author tronicek
 */
public abstract class UpdateScanner extends TreeScanner {

    protected CompilationUnit currentUnit;

    public abstract void prepare(Context ctx);

    public void process(CompilationUnit cu) {
        currentUnit = cu;
        visitTopLevel(currentUnit.getWorkingCopy());
        currentUnit = null;
    }

    protected void error(String msg) {
        System.err.println("ERROR: " + msg);
    }

    protected void warning(JCTree t, String msg) {
        String file = currentUnit.getFileName();
        LineMap lineMap = currentUnit.getWorkingCopy().lineMap;
        int pos = t.getStartPosition();
        long line = lineMap.getLineNumber(pos);
        long col = lineMap.getColumnNumber(pos);
        String s = String.format("[file: %s, line: %d, column: %d] warning: %s", file, line, col, msg);
        if (currentUnit.addWarning(s)) {
            System.err.println(s);
        }
    }
}
