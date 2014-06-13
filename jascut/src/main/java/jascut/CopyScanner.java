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
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCBreak;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCContinue;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCErroneous;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCInstanceOf;
import com.sun.tools.javac.tree.JCTree.JCLabeledStatement;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCSkip;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCSwitch;
import com.sun.tools.javac.tree.JCTree.JCSynchronized;
import com.sun.tools.javac.tree.JCTree.JCThrow;
import com.sun.tools.javac.tree.JCTree.JCTry;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCTypeUnion;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCWildcard;
import com.sun.tools.javac.tree.JCTree.LetExpr;
import com.sun.tools.javac.tree.JCTree.TypeBoundKind;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.List;
import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The AST scanner that makes a copy of ASTs.
 *
 * @author tronicek
 */
public class CopyScanner extends TreeScanner {

    private static class TreeStack {

        Deque<Object> p = new LinkedList<Object>();

        int size() {
            return p.size();
        }

        void push(Object v) {
            p.push(v);
        }

        List<JCAnnotation> popAnnos() {
            return (List<JCAnnotation>) p.pop();
        }

        JCBlock popBlock() {
            return (JCBlock) p.pop();
        }

        List<JCCase> popCases() {
            return (List<JCCase>) p.pop();
        }

        List<JCCatch> popCatches() {
            return (List<JCCatch>) p.pop();
        }

        JCClassDecl popClassDecl() {
            return (JCClassDecl) p.pop();
        }

        JCExpression popExpr() {
            return (JCExpression) p.pop();
        }

        List<JCExpression> popExprs() {
            return (List<JCExpression>) p.pop();
        }

        List<JCExpressionStatement> popExprStats() {
            return (List<JCExpressionStatement>) p.pop();
        }

        JCModifiers popMods() {
            return (JCModifiers) p.pop();
        }

        JCStatement popStat() {
            return (JCStatement) p.pop();
        }

        List<JCStatement> popStats() {
            return (List<JCStatement>) p.pop();
        }

        JCTree popTree() {
            return (JCTree) p.pop();
        }

        List<JCTree> popTrees() {
            return (List<JCTree>) p.pop();
        }

        TypeBoundKind popTypeBoundKind() {
            return (TypeBoundKind) p.pop();
        }

        List<JCTypeParameter> popTypeParams() {
            return (List<JCTypeParameter>) p.pop();
        }

        JCVariableDecl popVarDecl() {
            return (JCVariableDecl) p.pop();
        }

        List<JCVariableDecl> popVarDecls() {
            return (List<JCVariableDecl>) p.pop();
        }
    }
    private TreeMaker make;
    private JCCompilationUnit currentUnit;
    private Integer importPos;
    private Integer importEndPos;
    private Integer declPos;
    private Map<JCTree, JCTree> peers = new HashMap<JCTree, JCTree>();
    private Map<JCTree, Integer> startPositions = new HashMap<JCTree, Integer>();
    private Map<JCTree, Integer> endPositions = new HashMap<JCTree, Integer>();
    private TreeStack s = new TreeStack();
//    private JCTree prev;

    public CopyScanner(TreeMaker make) {
        this.make = make;
    }

    public Map<JCTree, JCTree> getPeers() {
        return peers;
    }

    public Map<JCTree, Integer> getEndPositions() {
        return endPositions;
    }

    private Integer getPkgPos() {
        if (currentUnit.pid == null) {
            return null;
        }
        return currentUnit.pos;
    }

    private Integer getPkgEndPos() {
        if (currentUnit.pid == null) {
            return null;
        }
        return currentUnit.endPositions.get(currentUnit.pid) + 1;
    }

    public JCTree getCopy() {
        assert s.size() == 1;
        return s.popTree();
    }

    public CompilationUnit backup() throws IOException {
        String src = currentUnit.sourcefile.getCharContent(true).toString();
        SourceCode orig = new SourceCode(
                src, getPkgPos(), getPkgEndPos(), importPos,
                importEndPos, declPos, startPositions, endPositions);
        return new CompilationUnit(orig, currentUnit, peers);
    }

    @Override
    public void scan(JCTree t) {
        if (t == null) {
            s.push(null);
        } else {
            t.accept(this);
        }
    }

    @Override
    public void scan(List<? extends JCTree> trees) {
        if (trees == null) {
            s.push(null);
        } else {
            List<JCTree> r = List.nil();
            JCTree prev = null;
            for (List<? extends JCTree> t = trees; t.nonEmpty(); t = t.tail) {
                if (prev != null) {
                    if (!endPositions.containsKey(prev)) {
                        int i = t.head.getStartPosition() - 1;
                        endPositions.put(prev, i);
                    }
                }
                scan(t.head);
                JCTree p = s.popTree();
                r = r.append(p);
                prev = t.head;
            }
            s.push(r);
        }
    }

    private void store(JCTree t, JCTree p) {
        int i = t.getStartPosition();
//        if (!endPositions.containsKey(prev)) {
//            endPositions.put(prev, i - 1);
//        }
        peers.put(t, p);
        startPositions.put(p, i);
        Integer e = currentUnit.endPositions.get(t);
        if (e != null) {
            endPositions.put(p, e);
        }
//        prev = p;
    }

    @Override
    public void visitAnnotation(JCAnnotation t) {
        scan(t.annotationType);
        JCTree atype = s.popTree();
        scan(t.args);
        List<JCExpression> args = s.popExprs();
        JCAnnotation p = make.Annotation(atype, args);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitApply(JCMethodInvocation t) {
        scan(t.typeargs);
        List<JCExpression> typeargs = s.popExprs();
        scan(t.meth);
        JCExpression meth = s.popExpr();
        scan(t.args);
        List<JCExpression> args = s.popExprs();
        JCMethodInvocation p = make.Apply(typeargs, meth, args);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitAssert(JCAssert t) {
        scan(t.cond);
        JCExpression cond = s.popExpr();
        scan(t.detail);
        JCExpression detail = s.popExpr();
        JCAssert p = make.Assert(cond, detail);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitAssign(JCAssign t) {
        scan(t.lhs);
        JCExpression lhs = s.popExpr();
        scan(t.rhs);
        JCExpression rhs = s.popExpr();
        JCAssign p = make.Assign(lhs, rhs);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitAssignop(JCAssignOp t) {
        scan(t.lhs);
        JCExpression lhs = s.popExpr();
        scan(t.rhs);
        JCExpression rhs = s.popExpr();
        JCAssignOp p = make.Assignop(t.getTag(), lhs, rhs);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitBinary(JCBinary t) {
        scan(t.lhs);
        JCExpression lhs = s.popExpr();
        scan(t.rhs);
        JCExpression rhs = s.popExpr();
        JCBinary p = make.Binary(t.getTag(), lhs, rhs);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitBlock(JCBlock t) {
        scan(t.stats);
        List<JCStatement> stats = s.popStats();
        JCBlock p = make.Block(t.flags, stats);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitBreak(JCBreak t) {
        JCBreak p = make.Break(t.label);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitCase(JCCase t) {
        scan(t.pat);
        JCExpression pat = s.popExpr();
        scan(t.stats);
        List<JCStatement> stats = s.popStats();
        JCCase p = make.Case(pat, stats);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitCatch(JCCatch t) {
        scan(t.param);
        JCVariableDecl param = s.popVarDecl();
        scan(t.body);
        JCBlock body = s.popBlock();
        JCCatch p = make.Catch(param, body);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitClassDef(JCClassDecl t) {
        if (declPos == null) {
            declPos = t.getStartPosition();
        }
        scan(t.mods);
        JCModifiers mods = s.popMods();
        scan(t.typarams);
        List<JCTypeParameter> typarams = s.popTypeParams();
        scan(t.extending);
        JCExpression extending = s.popExpr();
        scan(t.implementing);
        List<JCExpression> implementing = s.popExprs();
        scan(t.defs);
        List<JCTree> defs = s.popTrees();
        JCClassDecl p = make.ClassDef(mods, t.name, typarams, extending, implementing, defs);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitConditional(JCConditional t) {
        scan(t.cond);
        JCExpression cond = s.popExpr();
        scan(t.truepart);
        JCExpression truepart = s.popExpr();
        scan(t.falsepart);
        JCExpression falsepart = s.popExpr();
        JCConditional p = make.Conditional(cond, truepart, falsepart);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitContinue(JCContinue t) {
        JCContinue p = make.Continue(t.label);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitDoLoop(JCDoWhileLoop t) {
        scan(t.body);
        JCStatement body = s.popStat();
        scan(t.cond);
        JCExpression cond = s.popExpr();
        JCDoWhileLoop p = make.DoLoop(body, cond);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitErroneous(JCErroneous t) {
        JCErroneous p = make.Erroneous(t.errs);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitExec(JCExpressionStatement t) {
        scan(t.expr);
        JCExpression expr = s.popExpr();
        JCExpressionStatement p = make.Exec(expr);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitForLoop(JCForLoop t) {
        scan(t.init);
        List<JCStatement> init = s.popStats();
        scan(t.cond);
        JCExpression cond = s.popExpr();
        scan(t.step);
        List<JCExpressionStatement> step = s.popExprStats();
        scan(t.body);
        JCStatement body = s.popStat();
        JCForLoop p = make.ForLoop(init, cond, step, body);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitForeachLoop(JCEnhancedForLoop t) {
        scan(t.var);
        JCVariableDecl var = s.popVarDecl();
        scan(t.expr);
        JCExpression expr = s.popExpr();
        scan(t.body);
        JCStatement body = s.popStat();
        JCEnhancedForLoop p = make.ForeachLoop(var, expr, body);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitIdent(JCIdent t) {
        JCIdent p = make.Ident(t.name);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitIf(JCIf t) {
        scan(t.cond);
        JCExpression cond = s.popExpr();
        scan(t.thenpart);
        JCStatement thenpart = s.popStat();
        scan(t.elsepart);
        JCStatement elsepart = s.popStat();
        JCIf p = make.If(cond, thenpart, elsepart);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitImport(JCImport t) {
        if (importPos == null) {
            importPos = t.getStartPosition();
        }
        scan(t.qualid);
        importEndPos = currentUnit.endPositions.get(t);
        JCTree qualid = s.popTree();
        JCImport p = make.Import(qualid, t.staticImport);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitIndexed(JCArrayAccess t) {
        scan(t.indexed);
        JCExpression indexed = s.popExpr();
        scan(t.index);
        JCExpression index = s.popExpr();
        JCArrayAccess p = make.Indexed(indexed, index);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitLabelled(JCLabeledStatement t) {
        scan(t.body);
        JCStatement body = s.popStat();
        JCLabeledStatement p = make.Labelled(t.label, body);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitLetExpr(LetExpr t) {
        scan(t.defs);
        List<JCVariableDecl> defs = s.popVarDecls();
        scan(t.expr);
        JCExpression expr = s.popExpr();
        LetExpr p = make.LetExpr(defs, expr);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitLiteral(JCLiteral t) {
        JCLiteral p = make.Literal(t.typetag, t.value);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitMethodDef(JCMethodDecl t) {
        scan(t.mods);
        JCModifiers mods = s.popMods();
        scan(t.typarams);
        List<JCTypeParameter> typeparams = s.popTypeParams();
        scan(t.restype);
        JCExpression restype = s.popExpr();
        scan(t.params);
        List<JCVariableDecl> params = s.popVarDecls();
        scan(t.thrown);
        List<JCExpression> thrown = s.popExprs();
        scan(t.defaultValue);
        JCExpression defaultValue = s.popExpr();
        scan(t.body);
        JCBlock body = s.popBlock();
        JCMethodDecl p = make.MethodDef(mods, t.name, restype, typeparams,
                params, thrown, body, defaultValue);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitModifiers(JCModifiers t) {
        scan(t.annotations);
        List<JCAnnotation> annos = s.popAnnos();
        JCModifiers p = make.Modifiers(t.flags, annos);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitNewArray(JCNewArray t) {
        scan(t.elemtype);
        JCExpression elemtype = s.popExpr();
        scan(t.dims);
        List<JCExpression> dims = s.popExprs();
        scan(t.elems);
        List<JCExpression> elems = s.popExprs();
        JCNewArray p = make.NewArray(elemtype, dims, elems);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitNewClass(JCNewClass t) {
        scan(t.encl);
        JCExpression encl = s.popExpr();
        scan(t.clazz);
        JCExpression clazz = s.popExpr();
        scan(t.typeargs);
        List<JCExpression> typeargs = s.popExprs();
        scan(t.args);
        List<JCExpression> args = s.popExprs();
        scan(t.def);
        JCClassDecl def = s.popClassDecl();
        JCNewClass p = make.NewClass(encl, typeargs, clazz, args, def);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitParens(JCParens t) {
        scan(t.expr);
        JCExpression expr = s.popExpr();
        JCParens p = make.Parens(expr);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitReturn(JCReturn t) {
        scan(t.expr);
        JCExpression expr = s.popExpr();
        JCReturn p = make.Return(expr);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitSelect(JCFieldAccess t) {
        scan(t.selected);
        JCExpression sel = s.popExpr();
        JCFieldAccess p = make.Select(sel, t.name);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitSkip(JCSkip t) {
        JCSkip p = make.Skip();
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitSwitch(JCSwitch t) {
        scan(t.selector);
        JCExpression sel = s.popExpr();
        scan(t.cases);
        List<JCCase> cases = s.popCases();
        JCSwitch p = make.Switch(sel, cases);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitSynchronized(JCSynchronized t) {
        scan(t.lock);
        JCExpression lock = s.popExpr();
        scan(t.body);
        JCBlock body = s.popBlock();
        JCSynchronized p = make.Synchronized(lock, body);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitThrow(JCThrow t) {
        scan(t.expr);
        JCExpression expr = s.popExpr();
        JCThrow p = make.Throw(expr);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTopLevel(JCCompilationUnit t) {
        assert s.size() == 0;
        currentUnit = t;
        scan(t.packageAnnotations);
        List<JCAnnotation> pa = s.popAnnos();
        scan(t.pid);
        JCExpression pid = s.popExpr();
        scan(t.defs);
        List<JCTree> defs = s.popTrees();
        JCCompilationUnit p = make.TopLevel(pa, pid, defs);
        store(t, p);
        s.push(p);
        assert s.size() == 1;
    }

    @Override
    public void visitTree(JCTree t) {
        assert false;
    }

    @Override
    public void visitTry(JCTry t) {
        scan(t.body);
        JCBlock body = s.popBlock();
        scan(t.catchers);
        List<JCCatch> cats = s.popCatches();
        scan(t.finalizer);
        JCBlock fin = s.popBlock();
        JCTry p = make.Try(body, cats, fin);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeApply(JCTypeApply t) {
        scan(t.clazz);
        JCExpression clazz = s.popExpr();
        scan(t.arguments);
        List<JCExpression> args = s.popExprs();
        JCTypeApply p = make.TypeApply(clazz, args);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeArray(JCArrayTypeTree t) {
        scan(t.elemtype);
        JCExpression elemtype = s.popExpr();
        JCArrayTypeTree p = make.TypeArray(elemtype);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeBoundKind(TypeBoundKind t) {
        TypeBoundKind p = make.TypeBoundKind(t.kind);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeCast(JCTypeCast t) {
        scan(t.clazz);
        JCTree clazz = s.popTree();
        scan(t.expr);
        JCExpression expr = s.popExpr();
        JCTypeCast p = make.TypeCast(clazz, expr);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeUnion(JCTypeUnion t) {
        scan(t.alternatives);
        List<JCExpression> alts = s.popExprs();
        JCTypeUnion p = make.TypeUnion(alts);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeIdent(JCPrimitiveTypeTree t) {
        JCPrimitiveTypeTree p = make.TypeIdent(t.typetag);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeParameter(JCTypeParameter t) {
        scan(t.bounds);
        List<JCExpression> bounds = s.popExprs();
        JCTypeParameter p = make.TypeParameter(t.name, bounds);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitTypeTest(JCInstanceOf t) {
        scan(t.expr);
        JCExpression expr = s.popExpr();
        scan(t.clazz);
        JCTree clazz = s.popTree();
        JCInstanceOf p = make.TypeTest(expr, clazz);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitUnary(JCUnary t) {
        scan(t.arg);
        JCExpression arg = s.popExpr();
        JCUnary p = make.Unary(t.getTag(), arg);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitVarDef(JCVariableDecl t) {
        scan(t.mods);
        JCModifiers mods = s.popMods();
        scan(t.vartype);
        JCExpression vartype = s.popExpr();
        scan(t.init);
        JCExpression init = s.popExpr();
        JCVariableDecl p = make.VarDef(mods, t.name, vartype, init);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitWhileLoop(JCWhileLoop t) {
        scan(t.cond);
        JCExpression cond = s.popExpr();
        scan(t.body);
        JCStatement body = s.popStat();
        JCWhileLoop p = make.WhileLoop(cond, body);
        store(t, p);
        s.push(p);
    }

    @Override
    public void visitWildcard(JCWildcard t) {
        scan(t.kind);
        TypeBoundKind kind = s.popTypeBoundKind();
        scan(t.inner);
        JCTree inner = s.popTree();
        JCWildcard p = make.Wildcard(kind, inner);
        store(t, p);
        s.push(p);
    }
}
