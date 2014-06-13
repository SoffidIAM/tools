/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package refactoring;

import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import jascut.xml.Rule;
import jascut.xml.RenameMethodXml;

/**
 * The AST scanner for "rename method".
 *
 * @author tronicek
 */
public class RenameMethod extends UpdateScanner {

    private RenameMethodXml config;
    private JavacElements elems;
    private Names names;
    private Types types;
    private ClassSymbol clazz;
    private MethodSymbol method;
    private String methodNew;
    private TreeStack treeStack = new TreeStack();

    public RenameMethod(Rule config) {
        this.config = (RenameMethodXml) config;
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        names = Names.instance(ctx);
        types = Types.instance(ctx);
        clazz = elems.getTypeElement(config.getType());
        if (clazz == null) {
            String s = String.format("class not found: %s", config.getType());
            error(s);
            throw new UpdateException();
        }
        method = findMethodSymbol(clazz, config.getMethodOrig(), config.getArgsOrig());
        if (method == null) {
            String s = String.format("method not found: class %s, method %s(%s)",
                    config.getType(), config.getMethodOrig(), config.getArgsOrig());
            error(s);
            throw new UpdateException();
        }
        methodNew = config.getMethodNew();
    }

    private MethodSymbol findMethodSymbol(ClassSymbol cl, String method, String args) {
        for (Element e : cl.members().getElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                String n = e.getSimpleName().toString();
                MethodSymbol ms = (MethodSymbol) e;
                String params = paramsToString(ms);
                //System.out.println("method: " + n + ", args: " + params);
                if (n.equals(method) && params.equals(args)) {
                    //System.out.println("method: " + n + ", args: " + tt);
                    return ms;
                }
            }
        }
        return null;
    }

    private MethodSymbol findMethodSymbol(String method, String args) {
        for (JCClassDecl cd : treeStack.peekClasses()) {
            MethodSymbol ms = findMethodSymbol(cd.sym, method, args);
            if (ms != null) {
                return ms;
            }
        }
        return null;
    }

    private ClassSymbol findSubclass() {
        for (JCClassDecl cd : treeStack.peekClasses()) {
            ClassSymbol cs = cd.sym;
            if (types.isSubtype(cs.type, clazz.type)) {
                return cs;
            }
        }
        return null;
    }

    private String paramsToString(MethodSymbol ms) {
        StringBuilder sb = new StringBuilder();
        List<Type> tt = ms.type.getParameterTypes();
        for (int i = 0; i < tt.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            Type t = tt.get(i);
            sb.append(t.toString());
            if (t.getUpperBound() != null) {
                sb.append(" extends ");
                sb.append(t.getUpperBound());
            }
        }
        if (ms.isVarArgs()) {
            int len = sb.length();
            sb.replace(len - 2, len, "...");
        }
        return sb.toString();
    }

    private boolean inOwnMethod() {
        for (JCMethodDecl md : treeStack.peekMethods()) {
            MethodSymbol met = md.sym;
            if (elems.overrides(met, method, method.enclClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitApply(JCMethodInvocation mi) {
        JCExpression sel = mi.getMethodSelect();
        MethodSymbol symb = (MethodSymbol) TreeInfo.symbol(sel);
        if (symb != null) {
            if (symb.equals(method) || elems.overrides(symb, method, method.enclClass())) {
                MethodSymbol ms = findMethodSymbol(methodNew, config.getArgsOrig());
//            if (ms != null) {
//                ClassSymbol cs = (ClassSymbol) ms.owner;
//                List<Type> tt = ms.type.getParameterTypes();
//                String s = String.format("cannot rename method call %s to %s because of conflict with %s(%s) in class %s",
//                        config.getMethodOrig(), config.getMethodNew(), methodNew, tt.toString(), cs.fullname);
//                warning(mi, s);
//            }
                Name nn = names.fromString(methodNew);
                switch (sel.getKind()) {
                    case MEMBER_SELECT: {
                        JCFieldAccess p = (JCFieldAccess) sel;
                        p.name = nn;
                        break;
                    }
                    case IDENTIFIER: {
                        JCIdent p = (JCIdent) sel;
                        if (ms == null) {
                            if (inOwnMethod()) {
                                p.name = names.fromString("super." + methodNew);
                            } else {
                                p.name = nn;
                            }
                        } else {
                            ClassSymbol csorig = findSubclass();
                            assert csorig != null;
                            JCClassDecl cd = treeStack.peekClass();
                            if (csorig == cd.sym) {
                                p.name = names.fromString("super." + methodNew);
                            } else {
                                p.name = names.fromString(csorig.name + ".this." + methodNew);
                            }
                        }
                    }
                }
            }
        }
        super.visitApply(mi);
    }

    @Override
    public void visitClassDef(JCClassDecl cd) {
        if (cd.sym == clazz) { // no change inside of the same class
            return;
        }
        treeStack.push(cd);
        ClassSymbol cs = cd.sym;
        if (types.isSubtype(cs.type, clazz.type)) {
            MethodSymbol ms = findMethodSymbol(cs, methodNew, config.getArgsOrig());
            if (ms != null) {
                JCMethodDecl md = (JCMethodDecl) elems.getTree(ms);
                List<Type> tt = ms.type.getParameterTypes();
                String s = String.format("class %s contains method %s(%s) which overrides the new method",
                        cs.fullname, ms.name, tt.toString());
                warning(md, s);
            }
        }
        super.visitClassDef(cd);
        treeStack.pop();
    }

    @Override
    public void visitMethodDef(JCMethodDecl md) {
        treeStack.push(md);
        MethodSymbol ms = md.sym;
        if (elems.overrides(ms, method, method.enclClass())) {
            ClassSymbol cs = (ClassSymbol) ms.owner;
            MethodSymbol ms2 = findMethodSymbol(cs, methodNew, config.getArgsOrig());
            if (ms2 != null) {
                //JCMethodDecl md2 = (JCMethodDecl) elems.getTree(ms2);
                List<Type> tt = ms2.type.getParameterTypes();
                String s = String.format("cannot rename method declaration %s to %s because of conflict with %s(%s) in class %s",
                        config.getMethodOrig(), config.getMethodNew(), ms2.name, tt.toString(), cs.fullname);
                warning(md, s);
            } else {
                //System.out.println("renaming declaration: " + md.name + " -> " + methodNew);
                Name nn = names.fromString(methodNew);
                md.name = nn;
            }
        }
        super.visitMethodDef(md);
        treeStack.pop();
    }
}
