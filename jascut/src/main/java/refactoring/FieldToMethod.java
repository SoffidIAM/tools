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
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCInstanceOf;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import jascut.xml.FieldToMethodXml;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ElementKind;
import jascut.xml.Rule;

/**
 * The AST scanner for "field to method".
 *
 * @author tronicek
 */
public class FieldToMethod extends UpdateScanner {

    private FieldToMethodXml config;
    private JavacElements elems;
    private Names names;
    private TreeMaker make;
    private Types types;
    private TreeStack treeStack = new TreeStack();
    private ClassSymbol clazz;
    private VarSymbol fieldOrig;
    private JCCompilationUnit compilationUnit;
    private List<MethodSymbol> dangerousMethods = new ArrayList<MethodSymbol>();

    public FieldToMethod(Rule config) {
        this.config = (FieldToMethodXml) config;
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        names = Names.instance(ctx);
        make = TreeMaker.instance(ctx);
        types = Types.instance(ctx);
        clazz = elems.getTypeElement(config.getType());
        fieldOrig = findVarSymbol(clazz, config.getFieldOrig());
        if (fieldOrig == null) {
            String s = String.format("field not found: class %s, field %s",
                    config.getType(), config.getFieldOrig());
            error(s);
            throw new UpdateException();
        }
        lookupDangerousMethods();
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
                    dangerousMethods.add((MethodSymbol) s);
                
            }
        }
    }

    private boolean inOwnClass() {
        for (JCClassDecl cd : treeStack.peekClasses()) {
            ClassSymbol cs = cd.sym;
            if (clazz.equals(cs)) {
                return true;
            }
        }
        return false;
    }

    private boolean isToReplace(JCExpression e) {
        if (inOwnClass()) {
            return false;
        }
        if (e != null && e.getKind() == Kind.MEMBER_SELECT) {
            JCFieldAccess fa = (JCFieldAccess) e;
            Symbol s = fa.sym;
            return s.equals(fieldOrig);
        }
        return false;
    }

    private JCExpression makeSelected(String fullName) {
        String[] p = fullName.split("\\.");
        Name n = elems.getName(p[0]);
        JCExpression expr = make.Ident(n);
        for (int i = 1; i < p.length; i++) {
            Name nn = elems.getName(p[i]);
            expr = make.Select(expr, nn);
        }
        return expr;
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

    private JCMethodInvocation makeMethodInvocation(JCTree tree, String methodName) {
        JCFieldAccess fa = (JCFieldAccess) tree;
        Name n = elems.getName(methodName);
        JCExpression expr = make.Select(fa.selected, n);
        com.sun.tools.javac.util.List<JCExpression> args = com.sun.tools.javac.util.List.nil();
        return make.Apply(null, expr, args);
    }

    private JCMethodInvocation makeGetterCall(JCTree tree) {
        return makeMethodInvocation(tree, config.getGetterNew());
    }

    private JCMethodInvocation makeSetterCall(JCTree tree, JCExpression arg) {
        JCFieldAccess fa = (JCFieldAccess) tree;
        Name n = elems.getName(config.getSetterNew());
        JCExpression expr = make.Select(fa.selected, n);
        com.sun.tools.javac.util.List<JCExpression> args = com.sun.tools.javac.util.List.of(arg);
        return make.Apply(null, expr, args);
    }

    @Override
    public void visitApply(JCMethodInvocation t) {
        JCExpression sel = t.getMethodSelect();
        MethodSymbol symb = (MethodSymbol) TreeInfo.symbol(sel);
        if (dangerousMethods.contains(symb)) {
            warning(t, "reflective method used: " + symb.name);
        }
        super.visitApply(t);
        boolean b = false;
        com.sun.tools.javac.util.List<JCExpression> args = com.sun.tools.javac.util.List.nil();
        for (JCExpression e : t.args) {
            if (isToReplace(e)) {
                JCMethodInvocation p = makeGetterCall(e);
                args = args.append(p);
                b = true;
            } else {
                args = args.append(e);
            }
        }
        if (b) {
            t.args = args;
        }
    }

    @Override
    public void visitAssert(JCAssert t) {
        super.visitAssert(t);
        if (isToReplace(t.detail)) {
            t.detail = makeGetterCall(t.detail);
        }
    }

    @Override
    public void visitAssign(JCAssign t) {
        super.visitAssign(t);
        if (isToReplace(t.rhs)) {
            t.rhs = makeGetterCall(t.rhs);
        }
    }

    @Override
    public void visitBinary(JCBinary t) {
        super.visitBinary(t);
        if (isToReplace(t.lhs)) {
            t.lhs = makeGetterCall(t.lhs);
        }
        if (isToReplace(t.rhs)) {
            t.rhs = makeGetterCall(t.rhs);
        }
    }

    @Override
    public void visitBlock(JCBlock t) {
        treeStack.push(t);
        super.visitBlock(t);
        treeStack.pop();
    }

    @Override
    public void visitClassDef(JCClassDecl t) {
        treeStack.push(t);
        super.visitClassDef(t);
        treeStack.pop();
    }

    @Override
    public void visitConditional(JCConditional t) {
        super.visitConditional(t);
        if (isToReplace(t.truepart)) {
            t.truepart = makeGetterCall(t.truepart);
        }
        if (isToReplace(t.falsepart)) {
            t.falsepart = makeGetterCall(t.falsepart);
        }
    }

    @Override
    public void visitDoLoop(JCDoWhileLoop t) {
        super.visitDoLoop(t);
        if (isToReplace(t.cond)) {
            t.cond = makeGetterCall(t.cond);
        }
    }

    @Override
    public void visitExec(JCExpressionStatement t) {
        super.visitExec(t);
        JCExpression expr = t.expr;
        if (expr.getKind() == Kind.ASSIGNMENT) {
            JCAssign a = (JCAssign) expr;
            if (isToReplace(a.lhs)) {
                t.expr = makeSetterCall(a.lhs, a.rhs);
            }
        }
    }

    @Override
    public void visitForLoop(JCForLoop t) {
        super.visitForLoop(t);
        if (isToReplace(t.cond)) {
            t.cond = makeGetterCall(t.cond);
        }
    }

    @Override
    public void visitForeachLoop(JCEnhancedForLoop t) {
        super.visitForeachLoop(t);
        if (isToReplace(t.expr)) {
            t.expr = makeGetterCall(t.expr);
        }
    }

    private JCClassDecl findSubclass() {
        for (JCClassDecl cd : treeStack.peekClasses()) {
            ClassSymbol cs = cd.sym;
            if (types.isSubtype(cs.type, clazz.type)) {
                return cd;
            }
        }
        return null;
    }

    private boolean isInherited(ClassSymbol cs, Name name) {
        if (cs == clazz) {
            return true;
        }
        Scope.Entry e = cs.members().lookup(name);
        if (e.scope != null) {
            return false;
        }
        return true;
    }

    private boolean isInherited(JCClassDecl cd, Name name) {
        Type t = cd.sym.getSuperclass();
        ClassSymbol cs = (ClassSymbol) t.tsym;
        return isInherited(cs, name);
    }

    @Override
    public void visitIndexed(JCArrayAccess t) {
        super.visitIndexed(t);
        if (isToReplace(t.index)) {
            t.index = makeGetterCall(t.index);
        }
    }

    @Override
    public void visitMethodDef(JCMethodDecl md) {
        treeStack.push(md);
        super.visitMethodDef(md);
        treeStack.pop();
    }

    @Override
    public void visitNewArray(JCNewArray t) {
        super.visitNewArray(t);
        boolean b = false;
        com.sun.tools.javac.util.List<JCExpression> ee = com.sun.tools.javac.util.List.nil();
        for (JCExpression e : t.elems) {
            if (isToReplace(e)) {
                JCMethodInvocation p = makeGetterCall(e);
                ee = ee.append(p);
                b = true;
            } else {
                ee = ee.append(e);
            }
        }
        if (b) {
            t.elems = ee;
        }
    }

    @Override
    public void visitParens(JCParens t) {
        super.visitParens(t);
        if (isToReplace(t.expr)) {
            t.expr = makeGetterCall(t.expr);
        }
    }

    @Override
    public void visitReturn(JCReturn r) {
        super.visitReturn(r);
        if (isToReplace(r.expr)) {
            r.expr = makeGetterCall(r.expr);
        }
    }

    @Override
    public void visitSelect(JCFieldAccess t) {
        super.visitSelect(t);
        if (isToReplace(t.selected)) {
            t.selected = makeGetterCall(t.selected);
        }
    }

    @Override
    public void visitTopLevel(JCCompilationUnit t) {
        compilationUnit = t;
        super.visitTopLevel(t);
    }

    @Override
    public void visitTypeCast(JCTypeCast t) {
        super.visitTypeCast(t);
        if (isToReplace(t.expr)) {
            t.expr = makeGetterCall(t.expr);
        }
    }

    @Override
    public void visitTypeTest(JCInstanceOf t) {
        super.visitTypeTest(t);
        if (isToReplace(t.expr)) {
            t.expr = makeGetterCall(t.expr);
        }
    }

    @Override
    public void visitUnary(JCUnary t) {
        super.visitUnary(t);
        if (isToReplace(t.arg)) {
            t.arg = makeGetterCall(t.arg);
        }
    }

    @Override
    public void visitVarDef(JCVariableDecl t) {
        super.visitVarDef(t);
        if (isToReplace(t.init)) {
            t.init = makeGetterCall(t.init);
        }
    }

    @Override
    public void visitWhileLoop(JCWhileLoop t) {
        super.visitWhileLoop(t);
        if (isToReplace(t.cond)) {
            t.cond = makeGetterCall(t.cond);
        }
    }
}
