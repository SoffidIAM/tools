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
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.util.List;
import java.util.Map;

/**
 * The class that represents source code and positions of ASTs in text.
 *
 * @author tronicek
 */
public class SourceCode {

    public class Position {

        public final Integer start;
        public final Integer end;

        public Position(Integer start, Integer end) {
            this.start = start;
            this.end = end;
        }
    }
    private String text;
    private Integer pkgPos;
    private Integer pkgEndPos;
    private Integer importPos;
    private Integer importEndPos;
    private Integer declPos;
    private Map<JCTree, Integer> startPos;
    private Map<JCTree, Integer> endPos;

    public SourceCode(String text, Integer pkgPos, Integer pkgEndPos,
            Integer importPos, Integer importEndPos, Integer declPos,
            Map<JCTree, Integer> startPos, Map<JCTree, Integer> endPos) {
        this.text = text;
        this.pkgPos = pkgPos;
        this.pkgEndPos = pkgEndPos;
        this.importPos = importPos;
        this.importEndPos = importEndPos;
        this.declPos = declPos;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getText() {
        return text;
    }

    public Position getPosition(JCTree t) {
        Integer s = startPos.get(t);
        Integer e = endPos.get(t);
        return new Position(s, e);
    }

//    public int getLineStart(int line) {
//        return lineMap.getStartPosition(line);
//    }
//
//    public int getLineNumber(int pos) {
//        return lineMap.getLineNumber(pos);
//    }
//
//    public boolean isEmptyLine(int line) {
//        int s = lineMap.getStartPosition(line);
//        int e = lineMap.getStartPosition(line + 1);
//        String p = text.substring(s, e);
//        return p.trim().isEmpty();
//    }
//
    public Integer pkgPos() {
        return pkgPos;
    }

    public Integer pkgEndPos() {
        return pkgEndPos;
    }

    public Integer importPos() {
        return importPos;
    }

    public Integer importEndPos() {
        return importEndPos;
    }

    public Integer declPos() {
        return declPos;
    }

    public String classInterfaces(JCClassDecl t) {
        Position p = classInterfacesPos(t);
        return text.substring(p.start, p.end);
    }

    public Position classInterfacesPos(JCClassDecl t) {
        List<JCExpression> p = t.implementing;
//        if (p.isEmpty()) {
//        }
        int s = startPos.get(p.head);
        int e = endPos.get(p.last());
        return new Position(s, e);
    }


    public String methodSignature(JCMethodDecl t) {
        Position p = methodSignaturePos(t);
        return text.substring(p.start, p.end);
    }

    public Position methodSignaturePos(JCMethodDecl t) {
        int s = startPos.get(t.mods);
        if (s < 0) {
            if (t.typarams.isEmpty()) {
                s = startPos.get(t.restype);
            } else {
                s = startPos.get(t.typarams.head);
            }
        }
        int e = t.body == null ? endPos.get(t) : startPos.get(t.body);
        return new Position(s, e);
    }

    public String substring(JCTree t) {
        int s = startPos.get(t);
        int e = endPos.get(t);
        return text.substring(s, e);
    }

    public String substring(int s, int e) {
        return text.substring(s, e);
    }

    public int skipSpaces(int pos) {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
        return pos;
    }
}
