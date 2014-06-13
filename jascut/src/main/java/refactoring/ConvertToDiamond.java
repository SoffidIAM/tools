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
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import jascut.xml.Rule;
import jascut.xml.ConvertToDiamondXml;

/**
 * The AST scanner for "convert to diamond".
 *
 * @author tronicek
 */
public class ConvertToDiamond extends UpdateScanner {

    private ConvertToDiamondXml config;
    private JavacElements elems;
    private Names names;
    private TreeMaker make;
    private Types types;

    public ConvertToDiamond(Rule config) {
        this.config = (ConvertToDiamondXml) config;
    }

    @Override
    public void prepare(Context ctx) {
        elems = JavacElements.instance(ctx);
        names = Names.instance(ctx);
        make = TreeMaker.instance(ctx);
        types = Types.instance(ctx);
    }

    private void applyDiamond(JCNewClass nc) {
        JCTypeApply p = (JCTypeApply) nc.clazz;
        p.arguments = List.nil();
        //rewrite(p);
    }

    @Override
    public void visitAssign(JCAssign t) {
        JCExpression lhs = t.lhs;
        JCExpression rhs = t.rhs;
        if (lhs.getKind() == Kind.IDENTIFIER && rhs.getKind() == Kind.NEW_CLASS) {
            JCIdent id = (JCIdent) lhs;
            if (id.type.isParameterized()) {
                JCNewClass nc = (JCNewClass) rhs;
                applyDiamond(nc);
            }
        }
        super.visitAssign(t);
    }

    @Override
    public void visitVarDef(JCVariableDecl t) {
        VarSymbol vs = t.sym;
        Type typ = vs.type;
        if (typ.isParameterized()) {
            JCExpression expr = t.getInitializer();
            if (expr != null && expr.getKind() == Kind.NEW_CLASS) {
                JCNewClass nc = (JCNewClass) expr;
                applyDiamond(nc);
            }
        }
        super.visitVarDef(t);
    }
}
