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
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import jascut.typeparser.TypeParser;
import jascut.xml.ArgumentValueXml;
import jascut.xml.ArgumentXml;
import jascut.xml.ChangeMethodSignatureXml;
import jascut.xml.InvalidInputException;
import jascut.xml.Rule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * The AST scanner for "change method signature".
 *
 * @author tronicek
 */
public class ChangeMethodSignature extends UpdateScanner {

    private ChangeMethodSignatureXml config;
    private JavacElements elems;
    private Names names;
    private TreeMaker make;
    private Types types;
    private ClassSymbol clazz;
    private MethodSymbol method;
    private Map<Name, String> paramMap = new HashMap<Name, String>();
    private JCCompilationUnit compilationUnit;
    private TreeStack treeStack = new TreeStack();

    public ChangeMethodSignature(Rule config) {
        this.config = (ChangeMethodSignatureXml) config;
    }

    @Override
    public void visitBlock(JCTree.JCBlock b) {
        treeStack.push(b);
        super.visitBlock(b);
        treeStack.pop();
    }

    @Override
    public void visitClassDef(JCClassDecl cd) {
        if (cd.sym == clazz) { // no change inside of the same class
            return;
        }
        treeStack.push(cd);
        super.visitClassDef(cd);
        treeStack.pop();
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        names = Names.instance(ctx);
        make = TreeMaker.instance(ctx);
        types = Types.instance(ctx);
        clazz = elems.getTypeElement(config.getType());
        method = findMethodSymbol(clazz, config.getMethodOrig(), config.getArgsOrig());
        if (method == null) {
            String s = String.format("method not found: class %s, method %s(%s)",
                    config.getType(), config.getMethodOrig(), config.getArgsOrig());
            error(s);
            throw new UpdateException();
        }
    }

    private String simpleName(String cls) {
        String[] p = cls.split("\\.");
        return p[p.length - 1];
    }

    private MethodSymbol findMethodSymbol(ClassSymbol cl, String method, String args) {
        for (Element e : cl.members().getElements()) {
            if (e.getKind() == ElementKind.METHOD || e.getKind() == ElementKind.CONSTRUCTOR) {
                String n = e.getSimpleName().toString();
                MethodSymbol ms = (MethodSymbol) e;
                String pars = paramsToString(ms);
                //System.out.println("method: " + n + ", args: " + params);
                if (n.equals(method) && pars.equals(args)) {
                    //System.out.println("method: " + n + ", args: " + tt);
                    return ms;
                }
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

    private JCExpression argument(JCMethodInvocation mi, ArgumentXml arg) {
        Integer ref = arg.getRef();
        if (inOwnMethod()) {
            // if in own method, use method parameters as arguments
            JCExpression e = mi.meth;
            if (e.getKind() == Kind.MEMBER_SELECT) {
                JCExpression e2 = ((JCFieldAccess) e).selected;
                Name sup = null;
                switch (e2.getKind()) {
                    case IDENTIFIER:
                        sup = ((JCIdent) e2).name;
                        break;
                    case MEMBER_SELECT:
                        sup = ((JCFieldAccess) e2).name;
                }
                if (sup != null && sup.contentEquals("super")) {
                    Name n = ref == null ? names.fromString(arg.getName()) : method.params.get(ref).name;
                    return make.Ident(n);
                }
            }
        }
        if (ref != null) {
            return mi.args.get(ref);
        }
        ArgumentValueXml v = arg.getValue();
        switch (v.getKind()) {
            case REFERENCE: {
                int i = Integer.parseInt(v.getValue());
                return mi.args.get(i);
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

//    Symbol findInCurrentClass(Name name) {
//        ClassSymbol cs = currentClass.sym;
//        Scope.Entry e = cs.members().lookup(name);
//        if (e.scope != null) {
//            return e.sym;
//        }
//        return null;
//    }
//    MethodSymbol resolveName(Name name) {
//        ListBuffer<Type> args = new ListBuffer<>();
//        AttrContext attrContext = new AttrContext();
//        Env<AttrContext> env = new Env<>(currentClass, attrContext);
//        MethodType methodType = new Type.MethodType(args.toList(), TypeTags.VOID, List.nil(), currentClass.type);
//        return resolve.resolveInternalMethod(currentClass.pos(), env, null, name, args.toList(), null);
//    }
    private ClassSymbol findOuterClass() {
        for (JCClassDecl cl : treeStack.peekClasses()) {
            ClassSymbol cs = cl.sym;
            if (method.isMemberOf(cs, types)) {
                return cs;
            }
        }
        return null;
    }

    @Override
    public void visitApply(JCMethodInvocation mi) {
        JCExpression sel = mi.getMethodSelect();
        MethodSymbol symb = (MethodSymbol) TreeInfo.symbol(sel);
        if (symb != null) {
            if (symb.equals(method) || elems.overrides(symb, method, method.enclClass())) {
                if (config.getMethodNew() != null) {
                    Name nn = names.fromString(config.getMethodNew());
                    switch (sel.getKind()) {
                        case MEMBER_SELECT: {
                            JCFieldAccess p = (JCFieldAccess) sel;
                            p.name = nn;
                            break;
                        }
                        case IDENTIFIER: {
                            JCIdent p = (JCIdent) sel;
                            JCClassDecl cc = treeStack.peekClass();
                            ClassSymbol cs = cc.sym;
                            if (cs.isSubClass(elems.getTypeElement(config.getType()), types)) {
                                p.name = nn;
                            } else {
                                cs = findOuterClass();
                                String sn = cs.getSimpleName().toString();
                                p.name = names.fromString(sn + ".this." + config.getMethodNew());
                            }
                        }
                    }
                }
                if (config.getArgsNew() != null) {
                    List<JCExpression> args = List.nil();
                    for (ArgumentXml arg : config.getArgsNew()) {
                        JCExpression p = argument(mi, arg);
                        args = args.append(p);
                    }
                    mi.args = args;
                }
            }
        }
        super.visitApply(mi);
    }

    @Override
    public void visitNewClass(JCNewClass nc) {
        JCExpression sel = nc.getIdentifier();
        //ClassSymbol symb = (ClassSymbol) TreeInfo.symbol(sel);
        MethodSymbol symb = (MethodSymbol) nc.constructor;
        if (symb.equals(method)) {
            if (config.getMethodNew() != null) {
                Name nn = names.fromString(config.getMethodNew());
                switch (sel.getKind()) {
                    case MEMBER_SELECT: {
                        JCFieldAccess p = (JCFieldAccess) sel;
                        p.name = nn;
                        break;
                    }
                    case IDENTIFIER: {
                        JCIdent p = (JCIdent) sel;
                        JCClassDecl cc = treeStack.peekClass();
                        ClassSymbol cs = cc.sym;
                        if (cs.isSubClass(elems.getTypeElement(config.getType()), types)) {
                            p.name = nn;
                        } else {
                            cs = findOuterClass();
                            String sn = cs.getSimpleName().toString();
                            p.name = names.fromString(sn + ".this." + config.getMethodNew());
                        }
                    }
                }
            }
            if (config.getArgsNew() != null) {
                List<JCExpression> args = List.nil();
                for (ArgumentXml arg : config.getArgsNew()) {
                    JCExpression p = argument(nc, arg);
                    args = args.append(p);
                }
                nc.args = args;
            }
        }
        super.visitNewClass(nc);
    }

    @Override
    public void visitIdent(JCIdent i) {
        // rename parameters within the method
        if (!paramMap.isEmpty()) {
            Symbol s = i.sym;
            if (s != null) {
                Name n = i.sym.name;
                String nn = paramMap.get(n);
                if (nn != null) {
                    //System.out.printf("renaming %s -> %s%n", n.toString(), nn);
                    i.name = names.fromString(nn);
                }
            }
        }
    }

    Symbol findSymbol(Name name) {
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

    boolean isAccessible(String qname) {
        String sname = simpleName(qname);
        Name n = elems.getName(sname);
        Symbol sym = findSymbol(n);
        if (sym != null && sym.getQualifiedName().contentEquals(qname)) {
            return true;
        }
        return false;
    }

    String resolve(ListBuffer<JCTree> imports, String simpleName) {
        Name n = elems.getName(simpleName);
        Symbol sym = findSymbol(n);
        if (sym != null) {
            return sym.getQualifiedName().toString();
        }
        for (JCTree t : imports) {
            JCImport imp = (JCImport) t;
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
                return true;
        
        return false;
    }

    private JCExpression makeType(String type) {
        String s = type.replace(",", ", ");
        Name n = elems.getName(s);
        return make.Ident(n);
    }

    private long flags(long flags, String modifiers) {
        long f = flags;
        for (String s : modifiers.split("\\s+")) {
            if (s.equals("protected")) {
                    f ^= Flags.PUBLIC;
                    f |= Flags.PROTECTED;
            } else if (s.equals( "public")) {
                    f ^= Flags.PROTECTED;
                    f |= Flags.PUBLIC;
            }
        }
        return f;
    }

    @Override
    public void visitMethodDef(JCMethodDecl md) {
        treeStack.push(md);
        MethodSymbol met = md.sym;
        if (elems.overrides(met, method, method.enclClass())) {
            if (config.getModsNew() != null) {
                md.mods.flags = flags(met.flags(), config.getModsNew());
            }
            if (config.getReturnNew() != null) {
                String qname = config.getReturnNew();
                TypeParser p = TypeParser.parse(qname);
                qname = simplifyType(p.getTemplate(), p.getTypes());
                md.restype = makeType(qname);
            }
            if (config.getMethodNew() != null) {
                md.name = elems.getName(config.getMethodNew());
            }
            if (config.getArgsNew() != null) {
                List<JCVariableDecl> pars = List.nil();
                for (ArgumentXml arg : config.getArgsNew()) {
                    JCVariableDecl p;
                    Integer ref = arg.getRef();
                    if (ref == null) {
                        ArgumentValueXml v = arg.getValue();
                        if (v.getKind() == ArgumentValueXml.Kind.REFERENCE) {
                            int i = Integer.parseInt(v.getValue());
                            Name n = met.params.get(i).name;
                            paramMap.put(n, arg.getName());
                        }
                        p = parameter(arg);
                    } else {
                        p = md.params.get(ref);
                    }
                    pars = pars.append(p);
                }
                md.params = pars;
            }
            if (config.getThrowsNew() != null) {
                List<JCExpression> thr = List.nil();
                for (String s : config.getThrowsNew()) {
                    TypeParser p = TypeParser.parse(s);
                    String t = simplifyType(p.getTemplate(), p.getTypes());
                    thr = thr.append(makeType(t));
                }
                md.thrown = thr;
            }
        }
        super.visitMethodDef(md);
        paramMap.clear();
        treeStack.pop();
    }

    private JCVariableDecl parameter(ArgumentXml arg) {
        JCModifiers m = make.Modifiers(0L);
        Name n = names.fromString(arg.getName());
        String t = arg.getType();
        boolean varargs = t.endsWith("...");
        if (varargs) {
            t = t.substring(0, t.length() - 3);
        }
        TypeParser p = TypeParser.parse(t);
        t = simplifyType(p.getTemplate(), p.getTypes());
        if (varargs) {
            t = t.concat("...");
        }
        JCExpression id = makeType(t);
        return make.VarDef(m, n, id, null);
    }

    @Override
    public void visitTopLevel(JCCompilationUnit cu) {
        compilationUnit = cu;
        super.visitTopLevel(cu);
    }
}
