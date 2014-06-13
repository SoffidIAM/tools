/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package refactoring;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import jascut.xml.MoveFieldXml;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ElementKind;
import jascut.xml.Rule;

/**
 * The AST scanner for "move field".
 *
 * @author tronicek
 */
public class MoveField extends UpdateScanner {

    private MoveFieldXml config;
    private JavacElements elems;
    private TreeMaker make;
    private TreeStack treeStack = new TreeStack();
    private ClassSymbol clazz;
    private VarSymbol fieldOrig;
    private String fieldNew;
    private Name newName;
    private JCCompilationUnit compilationUnit;
    private Name simpleName;
    private boolean importNeeded;
    private boolean staticImportNeeded;
    private List<MethodSymbol> dangerousMethods = new ArrayList<MethodSymbol>();

    public MoveField(Rule config) {
        this.config = (MoveFieldXml) config;
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        make = TreeMaker.instance(ctx);
        clazz = elems.getTypeElement(config.getTypeOrig());
        fieldOrig = findVarSymbol(clazz, config.getFieldOrig());
        if (!fieldOrig.isStatic()) {
            String s = String.format("field is not static: class %s, field %s",
                    config.getTypeOrig(), config.getFieldOrig());
            error(s);
            throw new UpdateException();
        }
        if (fieldOrig == null) {
            String s = String.format("field not found: class %s, field %s",
                    config.getTypeOrig(), config.getFieldOrig());
            error(s);
            throw new UpdateException();
        }
        String s = simpleName(config.getTypeNew());
        simpleName = elems.getName(s);
        fieldNew = config.getFieldNew();
        newName = elems.getName(fieldNew);
        lookupDangerousMethods();
    }

    private String simpleName(String cls) {
        String[] p = cls.split("\\.");
        return p[p.length - 1];
    }

    private VarSymbol findVarSymbol(ClassSymbol cl, String var) {
        for (Symbol s : cl.members().getElements()) {
            if (s.getKind() == ElementKind.FIELD) {
                Name n = s.name;
                if (n.contentEquals(var)) {
                    return (VarSymbol) s;
                }
            }
        }
        return null;
    }

    private void lookupDangerousMethods() {
        ClassSymbol cl = elems.getTypeElement("java.lang.Class");
        for (Symbol s : cl.members().getElements()) {
            if (s.getKind() == ElementKind.METHOD) {
                String n = s.getSimpleName().toString();
                if (n.equals("getDeclaredField") ||
                		n.equals("getDeclaredFields") ||
                		n.equals("getField") ||
                		n.equals("getFields"))
                {
                        dangerousMethods.add((MethodSymbol) s);
                }
            }
        }
    }

    @Override
    public void visitApply(JCMethodInvocation mi) {
        JCExpression sel = mi.getMethodSelect();
        MethodSymbol symb = (MethodSymbol) TreeInfo.symbol(sel);
        if (dangerousMethods.contains(symb)) {
            warning(mi, "reflective method used: " + symb.name);
        }
        super.visitApply(mi);
    }

    @Override
    public void visitBlock(JCBlock b) {
        treeStack.push(b);
        super.visitBlock(b);
        treeStack.pop();
    }

    @Override
    public void visitClassDef(JCClassDecl cd) {
        treeStack.push(cd);
        super.visitClassDef(cd);
        treeStack.pop();
    }

    @Override
    public void visitIdent(JCIdent t) {
        Symbol sym = t.sym;
        if (sym != null && sym.getKind() == ElementKind.FIELD) {
            if (sym.equals(fieldOrig)) {
                if (sym.isStatic()) {
                    if (treeStack.isUsed(newName)) {
                        Symbol cls = lookup(simpleName);
                        if (cls == null) {
                            t.name = elems.getName(simpleName + "." + fieldNew);
                            importNeeded = true;
                        } else {
                            Name qn = cls.getQualifiedName();
                            if (qn.contentEquals(config.getTypeNew())) {
                                t.name = elems.getName(simpleName + "." + fieldNew);
                            } else {
                                t.name = elems.getName(config.getTypeNew() + "." + fieldNew);
                            }
                        }
                    } else {
                        t.name = newName;
                        staticImportNeeded = true;
                    }
                }
            }
        }
        super.visitIdent(t);
    }

    @Override
    public void visitMethodDef(JCMethodDecl md) {
        treeStack.push(md);
        super.visitMethodDef(md);
        treeStack.pop();
    }

    Symbol lookup(Name name) {
        Symbol s = treeStack.lookup(name);
        if (s != null) {
            return s;
        }
        Scope.Entry e = compilationUnit.namedImportScope.lookup(name);
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

    private JCExpression getSelected(String fullName) {
        String[] p = fullName.split("\\.");
        Name n = elems.getName(p[0]);
        JCExpression expr = make.Ident(n);
        for (int i = 1; i < p.length; i++) {
            Name nn = elems.getName(p[i]);
            expr = make.Select(expr, nn);
        }
        return expr;
    }

    @Override
    public void visitSelect(JCFieldAccess t) {
        Symbol s = t.sym;
        if (s != null && s.equals(fieldOrig)) {
//            ClassSymbol cs = (ClassSymbol) TreeInfo.symbol(t.selected);
//            String cl = cs.className();
//            if (cl.equals(config.getTypeOrig()) && t.name.contentEquals(config.getFieldOrig())) {
                Symbol sym = lookup(simpleName);
                if (sym == null) {
                    t.selected = make.Ident(simpleName);
                    importNeeded = true;
                } else {
                    Name p = sym.getQualifiedName();
                    if (p.contentEquals(config.getTypeNew())) {
                        t.selected = make.Ident(simpleName);
                    } else {
                        t.selected = getSelected(config.getTypeNew());
                    }
                }
                t.name = newName;
//            }
        }
        super.visitSelect(t);
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
            if (t.getKind() == Tree.Kind.IMPORT) {
                JCImport im = (JCImport) t;
                String qn = im.getQualifiedIdentifier().toString();
                if (qn.equals(config.getTypeOrig() + "." + config.getFieldOrig())) {
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
        if (!importNeeded && !staticImportNeeded) {
            return;
        }
        ListBuffer<JCTree> imps = new ListBuffer<JCTree>();
        ListBuffer<JCTree> decls = new ListBuffer<JCTree>();
        int i = 0;
        boolean b = false, b2 = false;
        for (JCTree t : cu.defs) {
            if (t.getKind() != Tree.Kind.IMPORT) {
                break;
            }
            JCImport im = (JCImport) t;
            String qn = im.getQualifiedIdentifier().toString();
            imps.append(t);
            if (qn.equals(config.getTypeNew())) {
                b = true;
            }
            if (qn.equals(config.getTypeNew() + "." + config.getFieldNew())) {
                b2 = true;
            }
            i++;
        }
        if (importNeeded && !b) {
            ClassSymbol cs = elems.getTypeElement(config.getTypeNew());
            boolean stat = cs.isStatic();
            JCImport imp = make.Import(qualifiedIdent(config.getTypeNew()), stat);
            imps.append(imp);
        }
        if (staticImportNeeded && !b2) {
            JCImport imp = make.Import(qualifiedIdent(config.getTypeNew() + "." + config.getFieldNew()), true);
            imps.append(imp);
        }
        for (; i < cu.defs.size(); i++) {
            decls.append(cu.defs.get(i));
        }
        cu.defs = Imports.sort(imps).appendList(decls);
    }
}
