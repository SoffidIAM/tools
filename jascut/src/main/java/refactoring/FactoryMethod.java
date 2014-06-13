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
import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCInstanceOf;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import jascut.xml.ArgumentValueXml;
import jascut.xml.ArgumentXml;
import jascut.xml.FactoryMethodXml;
import jascut.xml.InvalidInputException;
import jascut.xml.Rule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * The AST scanner for "factory method".
 *
 * @author tronicek
 */
public class FactoryMethod extends UpdateScanner {

    private FactoryMethodXml config;
    private JavacElements elems;
    private Names names;
    private TreeMaker make;
    private TreeStack treeStack = new TreeStack();
    private Types types;
    private ClassSymbol clazz;
    private MethodSymbol constructor;
    private String methodNew;
    private Name newName;
    private JCCompilationUnit compilationUnit;
    private Name simpleName;
    private boolean importNeeded;
    private boolean staticImportNeeded;
    private List<MethodSymbol> dangerousMethods = new ArrayList<MethodSymbol>();

    public FactoryMethod(Rule config) {
        this.config = (FactoryMethodXml) config;
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        names = Names.instance(ctx);
        make = TreeMaker.instance(ctx);
        types = Types.instance(ctx);
        clazz = elems.getTypeElement(config.getTypeOrig());
        constructor = findConstructorSymbol(clazz, config.getArgsOrig());
        if (constructor == null) {
            String s = String.format("constructor not found: class %s",
                    config.getTypeOrig());
            error(s);
            throw new UpdateException();
        }
        String s = simpleName(config.getTypeNew());
        simpleName = elems.getName(s);
        methodNew = config.getMethodNew();
        newName = elems.getName(methodNew);
        lookupDangerousMethods();
    }

    private String simpleName(String cls) {
        String[] p = cls.split("\\.");
        return p[p.length - 1];
    }

    private MethodSymbol findConstructorSymbol(ClassSymbol cl, String args) {
        for (Element e : cl.members().getElements()) {
            if (e.getKind() == ElementKind.CONSTRUCTOR) {
                MethodSymbol ms = (MethodSymbol) e;
                String params = paramsToString(ms);
                if (params.equals(args)) {
                    return ms;
                }
            }
        }
        return null;
    }

    private String paramsToString(Symbol.MethodSymbol ms) {
        StringBuilder sb = new StringBuilder();
        com.sun.tools.javac.util.List<Type> tt = ms.type.getParameterTypes();
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

    private void lookupDangerousMethods() {
        ClassSymbol cl = elems.getTypeElement("java.lang.Class");
        for (Symbol s : cl.members().getElements()) {
            if (s.getKind() == ElementKind.METHOD) {
                String n = s.getSimpleName().toString();
	            if (n.equals("getDeclaredConstructor") ||
                		n.equals("getDeclaredConstructors") ||
                		n.equals("getConstructor") ||
                		n.equals("getConstructors"))
	            {
                        dangerousMethods.add((MethodSymbol) s);
                }
            }
        }
    }

    private boolean isToReplace(JCExpression e) {
        if (e != null && e.getKind() == Kind.NEW_CLASS) {
            JCNewClass nc = (JCNewClass) e;
            Symbol s = nc.constructor;
            return s.equals(constructor);
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

    private JCMethodInvocation makeMethodInvocation(JCTree tree) {
        JCNewClass nc = (JCNewClass) tree;
        com.sun.tools.javac.util.List<JCExpression> typeArgs = null;
        String targs = config.getTypeArgsNew();
        if (targs == null) {
            JCExpression expr = nc.clazz;
            if (expr.getKind() == Kind.PARAMETERIZED_TYPE) {
                JCTypeApply ta = (JCTypeApply) expr;
                typeArgs = ta.getTypeArguments();
            }
        } else {
            typeArgs = com.sun.tools.javac.util.List.nil();
            for (String ta : config.getTypeArgsNew().split(",")) {
                String s = ta.trim();
                if (s.length() > 0) {
                    String cl = simplifyType("%s", new ArrayList<String>(Arrays.asList(s)));
                    Name n = elems.getName(cl);
                    JCIdent i = make.Ident(n);
                    typeArgs = typeArgs.append(i);
                }
            }
            if (typeArgs.length() == 0) {
                typeArgs = null;
            }
        }
        JCExpression expr;
        Symbol sym = lookup(simpleName);
        if (sym == null) {
            expr = make.Ident(simpleName);
            importNeeded = true;
        } else {
            Name p = sym.getQualifiedName();
            if (p.contentEquals(config.getTypeNew())) {
                expr = make.Ident(simpleName);
            } else {
                expr = makeSelected(config.getTypeNew());
            }
        }
        Name n = elems.getName(config.getMethodNew());
        expr = make.Select(expr, n);
        com.sun.tools.javac.util.List<JCExpression> args;
        if (config.getArgsNew() == null) {
            args = nc.args;
        } else {
            args = com.sun.tools.javac.util.List.nil();
            for (ArgumentXml arg : config.getArgsNew()) {
                JCExpression p = argument(nc, arg);
                args = args.append(p);
            }
        }
        return make.Apply(typeArgs, expr, args);
    }

    @Override
    public void visitApply(JCMethodInvocation mi) {
        JCExpression sel = mi.getMethodSelect();
        MethodSymbol symb = (MethodSymbol) TreeInfo.symbol(sel);
        if (dangerousMethods.contains(symb)) {
            warning(mi, "reflective method used: " + symb.name);
        }
        super.visitApply(mi);
        boolean b = false;
        com.sun.tools.javac.util.List<JCExpression> args = com.sun.tools.javac.util.List.nil();
        for (JCExpression e : mi.args) {
            if (isToReplace(e)) {
                JCMethodInvocation p = makeMethodInvocation(e);
                args = args.append(p);
                b = true;
            } else {
                args = args.append(e);
            }
        }
        if (b) {
            mi.args = args;
        }
    }

    @Override
    public void visitAssert(JCAssert a) {
        super.visitAssert(a);
        if (isToReplace(a.detail)) {
            a.detail = makeMethodInvocation(a.detail);
        }
    }

    @Override
    public void visitAssign(JCAssign a) {
        super.visitAssign(a);
        if (isToReplace(a.rhs)) {
            a.rhs = makeMethodInvocation(a.rhs);
        }
    }

    @Override
    public void visitBinary(JCBinary b) {
        super.visitBinary(b);
        if (isToReplace(b.lhs)) {
            b.lhs = makeMethodInvocation(b.lhs);
        }
        if (isToReplace(b.rhs)) {
            b.rhs = makeMethodInvocation(b.rhs);
        }
    }

    @Override
    public void visitBlock(JCTree.JCBlock b) {
        treeStack.push(b);
        super.visitBlock(b);
        treeStack.pop();
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl cd) {
        treeStack.push(cd);
        super.visitClassDef(cd);
        treeStack.pop();
    }

    @Override
    public void visitConditional(JCConditional c) {
        super.visitConditional(c);
        if (isToReplace(c.truepart)) {
            c.truepart = makeMethodInvocation(c.truepart);
        }
        if (isToReplace(c.falsepart)) {
            c.falsepart = makeMethodInvocation(c.falsepart);
        }
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

    @Override
    public void visitExec(JCExpressionStatement es) {
        super.visitExec(es);
        if (isToReplace(es.expr)) {
            es.expr = makeMethodInvocation(es.expr);
        }
    }

    @Override
    public void visitMethodDef(JCMethodDecl md) {
        treeStack.push(md);
        super.visitMethodDef(md);
        treeStack.pop();
    }

    @Override
    public void visitNewArray(JCNewArray na) {
        super.visitNewArray(na);
        boolean b = false;
        com.sun.tools.javac.util.List<JCExpression> ee = com.sun.tools.javac.util.List.nil();
        for (JCExpression e : na.elems) {
            if (isToReplace(e)) {
                JCMethodInvocation p = makeMethodInvocation(e);
                ee = ee.append(p);
                b = true;
            } else {
                ee = ee.append(e);
            }
        }
        if (b) {
            na.elems = ee;
        }
    }

    @Override
    public void visitParens(JCParens t) {
        super.visitParens(t);
        if (isToReplace(t.expr)) {
            t.expr = makeMethodInvocation(t.expr);
        }
    }

    @Override
    public void visitReturn(JCReturn r) {
        super.visitReturn(r);
        if (isToReplace(r.expr)) {
            r.expr = makeMethodInvocation(r.expr);
        }
    }

    @Override
    public void visitTypeTest(JCInstanceOf t) {
        super.visitTypeTest(t);
        if (isToReplace(t.expr)) {
            t.expr = makeMethodInvocation(t.expr);
        }
    }

    @Override
    public void visitVarDef(JCVariableDecl vd) {
        super.visitVarDef(vd);
        if (isToReplace(vd.init)) {
            vd.init = makeMethodInvocation(vd.init);
        }
    }

    private JCLiteral toLiteral(String type, String value) {
        if ("byte".equals(type)) {
            Byte b = Byte.valueOf(value);
            return make.Literal(TypeTags.INT, b);
        }
        if ("short".equals(type)) {
            Short s = Short.valueOf(value);
            return make.Literal(TypeTags.INT, s);
        }
        if ("char".equals(type)) {
            if (value.length() != 1) {
                throw new InvalidInputException("char may contain only a single character");
            }
            Integer i = Integer.valueOf(value.charAt(0));
            return make.Literal(TypeTags.CHAR, i);
        }
        if ("int".equals(type)) {
            Integer i = Integer.valueOf(value);
            return make.Literal(TypeTags.INT, i);
        }
        if ("long".equals(type)) {
            Long j = Long.valueOf(value);
            return make.Literal(TypeTags.LONG, j);
        }
        if ("float".equals(type)) {
            Float f = Float.valueOf(value);
            return make.Literal(TypeTags.FLOAT, f);
        }
        if ("double".equals(type)) {
            Double d = Double.valueOf(value);
            return make.Literal(TypeTags.DOUBLE, d);
        }
        if ("boolean".equals(type)) {
            Integer i = "true".equals(value) ? 1 : 0;
            return make.Literal(TypeTags.BOOLEAN, i);
        }
        if ("java.lang.String".equals(type)) {
            return make.Literal(value);
        }
        throw new InvalidInputException("unknown type: " + type);
    }

    private JCExpression argument(JCNewClass nc, ArgumentXml arg) {
        Integer ref = arg.getRef();
        if (ref != null) {
            return nc.args.get(ref);
        }
        ArgumentValueXml v = arg.getValue();
        switch (v.getKind()) {
            case REFERENCE: {
                int i = Integer.parseInt(v.getValue());
                return nc.args.get(i);
            }
            case LITERAL:
                return toLiteral(arg.getType(), v.getValue());
            case IDENTIFIER: {
                String s = v.getValue();
                String[] p = s.split("\\.");
                if (p.length > 0) {
                    String id = p[p.length - 1];
                    String cl = s.substring(0, s.length() - id.length() - 1);
                    cl = simplifyType("%s", new ArrayList<String>(Arrays.asList(cl)));
                    s = cl + "." + id;
                }
                Name n = names.fromString(s);
                return make.Ident(n);
            }
            case NULL:
            default:
                return make.Literal(TypeTags.BOT, null);
        }
    }

    boolean isAccessible(String qname) {
        String sname = simpleName(qname);
        Name n = elems.getName(sname);
        Symbol sym = lookup(n);
        if (sym != null && sym.getQualifiedName().contentEquals(qname)) {
            return true;
        }
        return false;
    }

    String resolve(ListBuffer<JCTree> imports, String simpleName) {
        Name n = elems.getName(simpleName);
        Symbol sym = lookup(n);
        if (sym != null) {
            return sym.getQualifiedName().toString();
        }
        for (JCTree t : imports) {
            JCTree.JCImport imp = (JCTree.JCImport) t;
            String qn = imp.qualid.toString();
            if (qn.endsWith("." + simpleName)) {
                return qn;
            }
        }
        return null;
    }

    void addImports(ArrayList<String> types) {
        ListBuffer<JCTree> imps = new ListBuffer<JCTree>();
        ListBuffer<JCTree> decls = new ListBuffer<JCTree>();
        for (JCTree t : compilationUnit.defs) {
            if (t.getKind() == Kind.IMPORT) {
                imps.append(t);
            } else {
                decls.append(t);
            }
        }
        boolean changed = false;
        for (int i = 0; i < types.size(); i++) {
            String qname = types.get(i);
            if (isPrimitive(qname)) {
                continue;
            }
            String sname = simpleName(qname);
            String res = resolve(imps, sname);
            if (res == null) {
                Name n = names.fromString(qname);
                JCIdent id = make.Ident(n);
                JCImport imp = make.Import(id, false);
                imps.append(imp);
                types.set(i, sname);
                changed = true;
            } else {
                if (res.equals(qname)) {
                    types.set(i, sname);
                }
            }
        }
        if (changed) {
            compilationUnit.defs = Imports.sort(imps).appendList(decls);
        }
    }

    String simplifyType(String template, ArrayList<String> types) {
        addImports(types);
        Object[] args = types.toArray();
        return String.format(template, args);
    }

    boolean isOnImport(String fname) {
        for (JCImport imp : compilationUnit.getImports()) {
            if (fname.equals(imp.qualid.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean isPrimitive(String type) {
        if (type.equals("byte") ||
        		type.equals("short") ||
        		type.equals("char") ||
        		type.equals("int") ||
        		type.equals("long") ||
        		type.equals("float") ||
        		type.equals("double") ||
        		type.equals("boolean") ||
        		type.equals("void"))
        {
                return true;
        }
        return false;
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
//        removeImport(cu);
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
            if (qn.equals(config.getTypeNew() + "." + config.getMethodNew())) {
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
            JCImport imp = make.Import(qualifiedIdent(config.getTypeNew() + "." + config.getMethodNew()), true);
            imps.append(imp);
        }
        for (; i < cu.defs.size(); i++) {
            decls.append(cu.defs.get(i));
        }
        cu.defs = Imports.sort(imps).appendList(decls);
    }
}
