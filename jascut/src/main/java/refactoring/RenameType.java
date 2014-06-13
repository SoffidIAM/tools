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
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import jascut.xml.RenameTypeXml;
import jascut.xml.Rule;

/**
 * The AST scanner for "rename type".
 *
 * @author tronicek
 */
public class RenameType extends UpdateScanner {

    private RenameTypeXml config;
    private JavacElements elems;
    private TreeMaker make;
    private JCCompilationUnit compilationUnit;
    private Name simpleName;
    private TreeStack treeStack = new TreeStack();
    private boolean importNeeded;
	private String oldPackage;
	private Name oldName;

    public RenameType(Rule config) {
        this.config = (RenameTypeXml) config;
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        make = TreeMaker.instance(ctx);
        String[] p = config.getTypeNew().split("\\.");
        String s = p[p.length - 1];
        simpleName = elems.getName(s);
        if (p.length > 0)
        	oldPackage = config.getTypeNew().substring(0, config.getTypeNew().lastIndexOf('.'));
        else
        	oldPackage = "";
        p = config.getTypeOrig().split("\\.");
        oldName = elems.getName(p[p.length-1]);
        oldPackage = config.getTypeOrig().substring(0, config.getTypeOrig().lastIndexOf('.'));
        
    }

    private boolean contains(List<JCExpression> exprs, JCExpression expr) {
        String s = expr.toString();
        for (JCExpression e : exprs) {
            if (s.equals(e.toString())) {
                return true;
            }
        }
        return false;
    }

    private List<JCExpression> removeDuplicates(List<JCExpression> exprs) {
        List<JCExpression> p = List.nil();
        for (JCExpression e : exprs) {
            if (!contains(p, e)) {
                p = p.append(e);
            }
        }
        return p;
    }

    @Override
    public void visitClassDef(JCClassDecl cd) {
        treeStack.push(cd);
        super.visitClassDef(cd);
        if (cd.name.equals(oldName) && false)
        {
        	cd.name = simpleName;
        }
        cd.implementing = removeDuplicates(cd.implementing);
        treeStack.pop();
    }

    Symbol lookup(Name name) {
        JCClassDecl cc = (JCClassDecl) treeStack.peek();
        assert cc != null;
        ClassSymbol cs = cc.sym;
        Scope.Entry e = cs.members().lookup(name);
        if (e.scope != null) {
            return e.sym;
        }
        e = compilationUnit.namedImportScope.lookup(name);
        if (e.scope != null) {
            return e.sym;
        }
        e = compilationUnit.packge.members().lookup(name);
        if (e.scope != null) {
            return e.sym;
        }
        e = compilationUnit.starImportScope.lookup(name);
        if (e.scope != null) {
            return e.sym;
        }
        return null;
    }

    @Override
    public void visitIdent(JCIdent id) {
        Symbol s = id.sym;
        if (s != null) {
            Name n = s.getQualifiedName();
            if (n.contentEquals(config.getTypeOrig())) {
                Symbol sym = lookup(simpleName);
                if (sym == null) {
                    id.name = simpleName;
                    importNeeded = true;
                } else {
                    Name p = sym.getQualifiedName();
                    if (p.contentEquals(config.getTypeNew())) {
                        id.name = simpleName;
                    } else {
                        id.name = elems.getName(config.getTypeNew());
                    }
                }
            }
        }
        super.visitIdent(id);
    }

    private JCExpression getSelected(String fullName) {
        String[] p = fullName.split("\\.");
        Name n = elems.getName(p[0]);
        JCExpression expr = make.Ident(n);
        for (int i = 1; i < p.length - 1; i++) {
            Name nn = elems.getName(p[i]);
            expr = make.Select(expr, nn);
        }
        return expr;
    }

    @Override
    public void visitSelect(JCFieldAccess fa) {
        Symbol s = fa.sym;
        if (s != null) {
            Name n = s.getQualifiedName();
            if (n.contentEquals(config.getTypeOrig())) {
                fa.selected = getSelected(config.getTypeNew());
                fa.name = simpleName;
            }
        }
        super.visitSelect(fa);
    }

    private void removeFromSymTab(Name name) {
        Scope.Entry e = compilationUnit.namedImportScope.lookup(name);
        if (e.scope != null) {
            compilationUnit.namedImportScope.remove(e.sym);
        }
        e = compilationUnit.starImportScope.lookup(name);
        if (e.scope != null) {
            compilationUnit.starImportScope.remove(e.sym);
        }
    }

    private void removeImport(JCCompilationUnit cu) {
        ListBuffer<JCTree> imps = new ListBuffer<JCTree>();
        ListBuffer<JCTree> decls = new ListBuffer<JCTree>();
        boolean changed = false;
        for (JCTree t : cu.defs) {
            if (t.getKind() == Kind.IMPORT) {
                JCImport im = (JCImport) t;
                String qn = im.getQualifiedIdentifier().toString();
                if (qn.equals(config.getTypeOrig())) {
                    changed = true;
                } else {
                    imps.append(t);
                }
            } else {
                decls.append(t);
            }
        }
        if (changed) {
            cu.defs = Imports.sort(imps).appendList(decls);
            String[] p = config.getTypeOrig().split("\\.");
            Name n = elems.getName(p[p.length - 1]);
            removeFromSymTab(n);
        }
    }

    private JCExpression qualifiedIdent(String qualid) {
        String[] p = qualid.split("\\.");
        Name n = elems.getName(p[0]);
        JCExpression expr = make.Ident(n);
        for (int i = 1; i < p.length; i++) {
            Name nn = elems.getName(p[i]);
            expr = make.Select(expr, nn);
        }
        return expr;
    }

    @Override
    public void visitTopLevel(JCCompilationUnit cu) {
        compilationUnit = cu;
        importNeeded = false;
        removeImport(cu);
        super.visitTopLevel(cu);
        if (!importNeeded) {
            return;
        }
        ListBuffer<JCTree> imps = new ListBuffer<JCTree>();
        ListBuffer<JCTree> decls = new ListBuffer<JCTree>();
        int i = 0;
        boolean b = false;
        for (JCTree t : cu.defs) {
            if (t.getKind() != Kind.IMPORT) {
                break;
            }
            JCImport im = (JCImport) t;
            String qn = im.getQualifiedIdentifier().toString();
            imps.append(t);
            if (qn.equals(config.getTypeNew())) {
                b = true;
            }
            i++;
        }
        if (!b) {
            JCImport imp = make.Import(qualifiedIdent(config.getTypeNew()), false);
            imps.append(imp);
        }
        for (; i < cu.defs.size(); i++) {
            decls.append(cu.defs.get(i));
        }
        cu.defs = Imports.sort(imps).appendList(decls);
    }
}
