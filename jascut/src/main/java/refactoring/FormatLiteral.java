/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package refactoring;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.util.Context;
import jascut.xml.FormatLiteralXml;
import jascut.xml.Rule;

/**
 * The AST scanner for "format literal".
 *
 * @author tronicek
 */
public class FormatLiteral extends UpdateScanner {

    private FormatLiteralXml config;

    public FormatLiteral(Rule config) {
        this.config = (FormatLiteralXml) config;
    }

    @Override
    public void prepare(Context ctx) {
    }

    @Override
    public void visitLiteral(JCLiteral t) {
        Kind k = t.getKind();
        if (k == Kind.INT_LITERAL || k == Kind.LONG_LITERAL) {
            String s = t.value.toString();
            s = format(s, config.getGroups(), config.getMinimumLength());
            t.value = s;
            //rewrite(t);
        }
        super.visitLiteral(t);
    }

    private String format(String str, int grps, int minLength) {
        if (str.contains("_") || str.length() <= minLength) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int i = str.length() % grps;
        if (i == 0) {
            i = grps;
        }
        String s = str.substring(0, i);
        sb.append(s);
        for (int j = i; j < str.length(); j += grps) {
            s = str.substring(j, j + grps);
            sb.append("_");
            sb.append(s);
        }
        return sb.toString();
    }
}
