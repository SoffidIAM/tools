/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package refactoring;

import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeKind;
import jascut.xml.AddOverrideXml;
import jascut.xml.Rule;

/**
 * The AST scanner for "add @Override".
 *
 * @author tronicek
 */
public class AddOverride extends UpdateScanner {

    private AddOverrideXml config;
    private JavacElements elems;
    private Names names;
    private TreeMaker make;
    private Types types;

    public AddOverride(Rule config) {
        this.config = (AddOverrideXml) config;
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        names = Names.instance(ctx);
        make = TreeMaker.instance(ctx);
        types = Types.instance(ctx);
    }

    private boolean overridesAnyMethod(MethodSymbol met, Type sup) {
        Scope sc = sup.tsym.members();
        Iterable<Symbol> symbs = sc.getElementsByName(met.name);
        for (Symbol s : symbs) {
            if (s.getKind() == ElementKind.METHOD) {
                MethodSymbol ms = (MethodSymbol) s;
                if (elems.overrides(met, ms, ms.enclClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean overrides(MethodSymbol met, Type t) {
        if (overridesAnyMethod(met, t)) {
            return true;
        }
        Type sup = types.supertype(t);
        if (sup.getKind() == TypeKind.DECLARED) {
            if (overrides(met, sup)) {
                return true;
            }
        }
        for (Type i : types.interfaces(t)) {
            if (overrides(met, i)) {
                return true;
            }
        }
        return false;
    }

    private void addOverride(JCMethodDecl md) {
        JCModifiers mods = md.mods;
        List<JCAnnotation> annos = mods.annotations;
        if (!containsOverride(annos)) {
            Name n = names.fromString("Override");
            JCAnnotation an = make.Annotation(
                    make.Ident(n),
                    List.<JCExpression>nil());
            List<JCAnnotation> p = List.of(an);
            mods.annotations = p.appendList(annos);
        }
    }

    @Override
    public void visitMethodDef(JCMethodDecl md) {
        MethodSymbol met = md.sym;
        ClassSymbol cl = (ClassSymbol) met.getEnclosingElement();
        Type sup = cl.getSuperclass();
        if (sup.getKind() == TypeKind.DECLARED) {
            if (overrides(met, sup)) {
                addOverride(md);
            }
        }
        for (Type i : cl.getInterfaces()) {
            if (i.getKind() == TypeKind.DECLARED) {
                if (overrides(met, i)) {
                    addOverride(md);
                }
            }
        }
    }

    private boolean containsOverride(List<JCAnnotation> annos) {
        for (JCAnnotation p : annos) {
            JCIdent id = (JCIdent) p.annotationType;
            if (id.name.contentEquals("Override")) {
                return true;
            }
        }
        return false;
    }
}
