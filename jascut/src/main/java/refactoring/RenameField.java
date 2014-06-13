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
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ElementKind;
import jascut.xml.RenameFieldXml;
import jascut.xml.Rule;

/**
 * The AST scanner for "rename field".
 *
 * @author tronicek
 */
public class RenameField extends UpdateScanner {

    private RenameFieldXml config;
    private JavacElements elems;
    private Names names;
    private TreeMaker make;
    private Types types;
    private TreeStack treeStack = new TreeStack();
    private ClassSymbol clazz;
    private VarSymbol fieldOrig;
    private String fieldNew;
    private Name newName;
    private List<MethodSymbol> dangerousMethods = new ArrayList<MethodSymbol>();

    public RenameField(Rule config) {
        this.config = (RenameFieldXml) config;
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
        fieldNew = config.getFieldNew();
        newName = elems.getName(fieldNew);
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
    public void visitBlock(JCTree.JCBlock b) {
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
    public void visitIdent(JCIdent t) {
        Symbol sym = t.sym;
        if (sym != null && sym.getKind() == ElementKind.FIELD) {
            if (sym.equals(fieldOrig)) {
                boolean lvar = treeStack.isLocalVar(newName);
                boolean memb = treeStack.isMember(newName);
                if (sym.isStatic()) {
                    if (lvar || memb) {
                        Name owner = sym.owner.getQualifiedName();
                        t.name = elems.getName(owner + "." + fieldNew);
                    } else {
                        t.name = newName;
                    }
                } else {
                    JCClassDecl sub = findSubclass();
                    String out;
                    if (sub == treeStack.peekClass()) {
                        out = "";
                    } else {
                        out = sub.name + ".";
                    }
                    if (!isInherited(sub, newName)) {
                        // add typecast
                        t.name = elems.getName("((" + clazz.fullname + ") " + out + "this)." + fieldNew);
                    } else {
                        if (memb) {
                            t.name = elems.getName(out + "super." + fieldNew);
                        } else if (lvar) {
                            t.name = elems.getName(out + "this." + fieldNew);
                        } else {
                            t.name = newName;
                        }
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

    @Override
    public void visitSelect(JCFieldAccess t) {
        Symbol s = t.sym;
        if (s == null) {
            // static fields
            String cl = t.selected.toString();
            if (cl.equals(config.getType()) && t.name.contentEquals(config.getFieldOrig())) {
                t.name = newName;
            }
        } else {
            if (s.equals(fieldOrig)) {
                JCClassDecl sub = findSubclass();
                if (sub == null) {
                    ClassSymbol cs = (ClassSymbol) t.selected.type.tsym;
                    if (!isInherited(cs, newName)) {
                        // add typecast
                        //t.selected = make.TypeCast(clazz.type, t.selected);
                        Name n = names.fromString("((" + clazz.fullname + ") " + t.selected + ")");
                        t.selected = make.Ident(n);
                    }
                    t.name = newName;
                } else if (!isInherited(sub, newName)) {
                    // add typecast
                    //JCIdent i = make.Ident(names.fromString("this"));
                    //t.selected = make.TypeCast(clazz.type, i);
                    Name n = names.fromString("((" + clazz.fullname + ") this)");
                    t.selected = make.Ident(n);
                    t.name = newName;
                } else {
                    String sel = t.selected.toString();
                    if (sel.equals("this")) {
                        if (treeStack.isMember(newName)) {
                            // replace this with super
                            JCIdent i = (JCIdent) t.selected;
                            i.name = elems.getName("super");
                        }
                    }
                    t.name = newName;
                }
            }
        }
        super.visitSelect(t);
    }
}
