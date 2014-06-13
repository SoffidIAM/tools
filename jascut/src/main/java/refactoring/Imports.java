/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package refactoring;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The class that sorts imports.
 *
 * @author tronicek
 */
public class Imports {

    public static List<JCTree> sort(ListBuffer<JCTree> imports) {
        ArrayList<JCImport> imps = new ArrayList<JCImport>();
        for (JCTree t : imports) {
            imps.add((JCImport) t);
        }
        Collections.sort(imps, new ImportComparator());
        ListBuffer<JCTree> p = new ListBuffer<JCTree>();
        String prev = null;
        for (JCImport i : imps) {
            if (prev == null) {
                p.add(i);
                prev = i.toString();
            } else {
                String s = i.toString();
                if (!s.equals(prev)) {
                    p.add(i);
                    prev = i.toString();
                }
            }
        }
        return p.toList();
    }

    static class ImportComparator implements Comparator<JCImport> {

        @Override
        public int compare(JCImport i1, JCImport i2) {
            if (i1.isStatic() && !i2.isStatic()) {
                return 1;
            }
            if (!i1.isStatic() && i2.isStatic()) {
                return -1;
            }
            String n1 = i1.qualid.toString();
            String n2 = i2.qualid.toString();
            return n1.compareTo(n2);
        }
    }
}
