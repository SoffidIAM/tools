/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import java.util.ArrayList;
import java.util.List;
import jascut.SourceCode.Position;

/**
 * The class that rewrites changes to the original source code.
 *
 * @author tronicek
 */
public class CodeRewriter {

    private static class Change {

        int from;
        int to;
        int delta;

        Change(int from, int to, int delta) {
            this.from = from;
            this.to = to;
            this.delta = delta;
        }
    }
    private static final String newLine = "\n";
    private SourceCode original;
    private SourceCode modified;
    private StringBuilder source;
    private List<Change> changes = new ArrayList<Change>();

    public CodeRewriter(SourceCode original, SourceCode modified) {
        this.original = original;
        this.modified = modified;
        source = new StringBuilder(original.getText());
    }

    public String getModifiedCode() {
        return source.toString();
    }

    public int getChangeCount() {
        return changes.size();
    }

    private boolean wasRewritten(int start, int end) {
        assert start <= end;
        for (Change ch : changes) {
            if (ch.from <= start && end <= ch.to) {
                return true;
            }
        }
        return false;
    }

    private int delta(int pos) {
        int d = 0;
        for (Change ch : changes) {
            if (ch.from < pos) {
                d += ch.delta;
            }
        }
        return d;
    }

    private void storeChange(int start, int end, int delta) {
        Change ch = new Change(start, end, delta);
        changes.add(ch);
    }

    private void replace(int start, int end, String str) {
        if (!wasRewritten(start, end)) {
            int d = delta(start);
            source.replace(start + d, end + d, str);
            int dd = str.length() - (end - start);
            storeChange(start, end, dd);
        }
    }

    private void replace(Position pos, String str) {
        replace(pos.start, pos.end, str);
    }

    public void changeImports() {
        Integer end = original.importEndPos();
        if (end == null) {
            end = original.pkgEndPos();
        }
        int e1 = end == null ? 0 : original.skipSpaces(end);
        int e2 = modified.declPos();
        String s = modified.substring(0, e2);
        Integer b = original.pkgPos();
        if (b == null) {
            b = original.importPos();
        }
        int p = b == null ? 0 : b;
        replace(p, e1, s + newLine);
    }


    public void changeClassInterfaces(JCClassDecl from, JCClassDecl to) {
        Position p = original.classInterfacesPos(from);
        String s = modified.classInterfaces(to);
        replace(p, s);
    }

    public void changeMethodSignature(JCMethodDecl from, JCMethodDecl to) {
        Position p = original.methodSignaturePos(from);
        String s = modified.methodSignature(to);
        replace(p, s);
    }

    public void changeTree(JCTree from, JCTree to) {
        Position p = original.getPosition(from);
        String s = modified.substring(to);
        replace(p, s);
    }
}
