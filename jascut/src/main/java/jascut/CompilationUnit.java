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
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class that represents a compilation unit.
 *
 * @author tronicek
 */
public class CompilationUnit {

	private SourceCode original;
    private JCCompilationUnit workingCopy;
    private Map<JCTree, JCTree> peers;
    private List<String> warnings = new ArrayList<String>();

    public CompilationUnit(SourceCode original, JCCompilationUnit workingCopy, Map<JCTree, JCTree> peers) {
        this.original = original;
        this.workingCopy = workingCopy;
        this.peers = peers;
    }

    public String getFileName() {
        return workingCopy.sourcefile.getName();
    }

    public boolean addWarning(String warning) {
        if (!warnings.contains(warning)) {
            warnings.add(warning);
            return true;
        }
        return false;
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public JCCompilationUnit getWorkingCopy() {
        return workingCopy;
    }

    private String getModifiedSource() throws IOException {
        PrettyPrinter pp = new PrettyPrinter();
        workingCopy.accept(pp);
        SourceCode modified = pp.sourceCode();
        CodeRewriter rewriter = new CodeRewriter(original, modified);
        ChangeScanner scan = new ChangeScanner(workingCopy, peers, rewriter);
        scan.scanForChanges();
        return scan.getModifiedCode();
    }

    public void write(Writer out) throws IOException {
        String src = getModifiedSource();
        out.write(src);
    }
}
