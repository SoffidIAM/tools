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
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * The AST scanner that stores end positions of ASTs.
 *
 * @author tronicek
 */
public class EndPosScanner extends TreeScanner {

    private JCCompilationUnit currentUnit;
    private Map<JCTree, JCTree> peers = new HashMap<JCTree, JCTree>();
    private Map<JCTree, Integer> endPositions = new HashMap<JCTree, Integer>();

    public EndPosScanner(Map<JCTree, JCTree> peers, Map<JCTree, Integer> endPositions) {
        this.peers = peers;
        this.endPositions = endPositions;
    }

    @Override
    public void scan(JCTree t) {
        if (t != null) {
            t.accept(this);
        }
    }

    @Override
    public void scan(List<? extends JCTree> trees) {
        if (trees != null) {
            JCTree prev = null;
            for (List<? extends JCTree> t = trees; t.nonEmpty(); t = t.tail) {
                if (prev != null) {
                    if (!endPositions.containsKey(prev)) {
                        int i = t.head.getStartPosition() - 1;
                        endPositions.put(prev, i);
                    }
                }
                scan(t.head);
                prev = t.head;
            }
        }
    }

    private void parentEnd(JCTree parent, JCTree tree) {
        if (!endPositions.containsKey(tree)) {
            Integer i = currentUnit.endPositions.get(parent);
            if (i != null) {
                endPositions.put(tree, i);
            }
        }
    }

    private void parentEnd(JCTree parent, List<? extends JCTree> trees) {
        if (!trees.isEmpty()) {
            parentEnd(parent, trees.tail);
        }
    }

    private List<JCTree> toList(JCTree... trees) {
        List<JCTree> p = List.nil();
        for (JCTree t : trees) {
            p = p.append(t);
        }
        return p;
    }

    private List<JCTree> concat(List<? extends JCTree>... trees) {
        List<JCTree> p = List.nil();
        for (List<? extends JCTree> tt : trees) {
        	if (tt == null)
        		p = p.append(null);
        	else
	            for (JCTree t : tt) {
	                p = p.append(t);
	        	}
        }
        return p;
    }

    private void endpos(JCTree parent, List<? extends JCTree> trees) {
        JCTree prev = null;
        for (JCTree t : trees) {
            if (t == null) {
                continue;
            }
            if (prev != null) {
                JCTree p = peers.get(prev);
                Integer i = endPositions.get(p);
                if (i == null) {
                    int j = t.getStartPosition() - 1;
                    endPositions.put(p, j);
                }
            }
            prev = t;
        }
        if (prev != null) {
            JCTree p = peers.get(prev);
            Integer i = endPositions.get(p);
            if (i == null) {
                Integer j = currentUnit.endPositions.get(parent);
                if (j != null) {
                    endPositions.put(p, j);
                }
            } else {
                Integer j = currentUnit.endPositions.get(parent);
                if (j == null) {
                    JCTree pp = peers.get(parent);
                    endPositions.put(pp, i);
                }
            }
        }
    }

    private void endpos(JCTree parent, JCTree... tt) {
        List<JCTree> p = toList(tt);
        endpos(parent, p);
    }

    private void endpos(JCTree parent, JCTree t1, List<? extends JCTree> t2) {
        List<JCTree> p = concat(toList(t1), t2);
        endpos(parent, p);
    }

    private void endpos(JCTree parent, List<? extends JCTree> t1, JCTree t2, List<? extends JCTree> t3) {
        List<JCTree> p = concat(t1, toList(t2), t3);
        endpos(parent, p);
    }

    private void endpos(JCTree parent, JCTree t1, List<? extends JCTree> t2, JCTree t3, List<? extends JCTree> t4,
            List<? extends JCTree> t5) {
        List<JCTree> p = concat(toList(t1), t2, toList(t3), t4, t5);
        endpos(parent, p);
    }

    private void endpos(JCTree parent, List<? extends JCTree> t1, JCTree t2, List<? extends JCTree> t3, JCTree t4) {
        List<JCTree> p = concat(t1, toList(t2), t3, toList(t4));
        endpos(parent, p);
    }

    private void endpos(JCTree parent, List<? extends JCTree> t1, JCTree t2) {
        List<JCTree> p = concat(t1, toList(t2));
        endpos(parent, p);
    }

    private void endpos(JCTree parent, JCTree t1, List<? extends JCTree> t2, JCTree t3, List<? extends JCTree> t4,
            List<? extends JCTree> t5, JCTree t6, JCTree t7) {
        List<JCTree> p = concat(toList(t1), t2, toList(t3), t4, t5, toList(t6), toList(t7));
        endpos(parent, p);
    }

    private void endpos(JCTree parent, JCTree t1, List<? extends JCTree> t2, List<? extends JCTree> t3) {
        List<JCTree> p = concat(toList(t1), t2, t3);
        endpos(parent, p);
    }

    private void endpos(JCTree parent, JCTree t1, JCTree t2, List<? extends JCTree> t3, List<? extends JCTree> t4, JCTree t5) {
        List<JCTree> p = concat(toList(t1), toList(t2), t3, t4, toList(t5));
        endpos(parent, p);
    }

    private void endpos(JCTree parent, JCTree t1, List<? extends JCTree> t2, JCTree t3) {
        List<JCTree> p = concat(toList(t1), t2, toList(t3));
        endpos(parent, p);
    }

    @Override
    public void visitAnnotation(JCAnnotation t) {
        scan(t.annotationType);
        scan(t.args);
        endpos(t, t.annotationType, t.args);
    }

    @Override
    public void visitApply(JCMethodInvocation t) {
        scan(t.typeargs);
        scan(t.meth);
        scan(t.args);
        endpos(t, t.typeargs, t.meth, t.args);
    }

    @Override
    public void visitAssert(JCAssert t) {
        scan(t.cond);
        scan(t.detail);
        endpos(t, t.cond, t.detail);
    }

    @Override
    public void visitAssign(JCAssign t) {
        scan(t.lhs);
        scan(t.rhs);
        endpos(t, t.lhs, t.rhs);
    }

    @Override
    public void visitAssignop(JCAssignOp t) {
        scan(t.lhs);
        scan(t.rhs);
        endpos(t, t.lhs, t.rhs);
    }

    @Override
    public void visitBinary(JCBinary t) {
        scan(t.lhs);
        scan(t.rhs);
        endpos(t, t.lhs, t.rhs);
    }

    @Override
    public void visitBlock(JCBlock t) {
        scan(t.stats);
        endpos(t, t.stats);
    }

    @Override
    public void visitBreak(JCBreak t) {
    }

    @Override
    public void visitCase(JCCase t) {
        scan(t.pat);
        scan(t.stats);
        endpos(t, t.pat, t.stats);
    }

    @Override
    public void visitCatch(JCCatch t) {
        scan(t.param);
        scan(t.body);
        endpos(t, t.param, t.body);
    }

    @Override
    public void visitClassDef(JCClassDecl t) {
        scan(t.mods);
        scan(t.typarams);
        scan(t.extending);
        scan(t.implementing);
        scan(t.defs);
        endpos(t, t.mods, t.typarams, t.extending, t.implementing, t.defs);
    }

    @Override
    public void visitConditional(JCConditional t) {
        scan(t.cond);
        scan(t.truepart);
        scan(t.falsepart);
        endpos(t, t.cond, t.truepart, t.falsepart);
    }

    @Override
    public void visitContinue(JCContinue t) {
    }

    @Override
    public void visitDoLoop(JCDoWhileLoop t) {
        scan(t.body);
        scan(t.cond);
        endpos(t, t.body, t.cond);
    }

    @Override
    public void visitErroneous(JCErroneous t) {
    }

    @Override
    public void visitExec(JCExpressionStatement t) {
        scan(t.expr);
        endpos(t, t.expr);
    }

    @Override
    public void visitForLoop(JCForLoop t) {
        scan(t.init);
        scan(t.cond);
        scan(t.step);
        scan(t.body);
        endpos(t, t.init, t.cond, t.step, t.body);
    }

    @Override
    public void visitForeachLoop(JCEnhancedForLoop t) {
        scan(t.var);
        scan(t.expr);
        scan(t.body);
        endpos(t, t.var, t.expr, t.body);
    }

    @Override
    public void visitIdent(JCIdent t) {
    }

    @Override
    public void visitIf(JCIf t) {
        scan(t.cond);
        scan(t.thenpart);
        scan(t.elsepart);
        endpos(t, t.cond, t.thenpart, t.elsepart);
    }

    @Override
    public void visitImport(JCImport t) {
        scan(t.qualid);
        endpos(t, t.qualid);
    }

    @Override
    public void visitIndexed(JCArrayAccess t) {
        scan(t.indexed);
        scan(t.index);
        endpos(t, t.indexed, t.index);
    }

    @Override
    public void visitLabelled(JCLabeledStatement t) {
        scan(t.body);
        endpos(t, t.body);
    }

    @Override
    public void visitLetExpr(LetExpr t) {
        scan(t.defs);
        scan(t.expr);
        endpos(t, t.defs, t.expr);
    }

    @Override
    public void visitLiteral(JCLiteral t) {
    }

    @Override
    public void visitMethodDef(JCMethodDecl t) {
        scan(t.mods);
        scan(t.typarams);
        scan(t.restype);
        scan(t.params);
        scan(t.thrown);
        scan(t.defaultValue);
        scan(t.body);
        endpos(t, t.mods, t.typarams, t.restype, t.params, t.thrown, t.defaultValue, t.body);
    }

    @Override
    public void visitModifiers(JCModifiers t) {
        scan(t.annotations);
        endpos(t, t.annotations);
    }

    @Override
    public void visitNewArray(JCNewArray t) {
        scan(t.elemtype);
        scan(t.dims);
        if (t.elems == null)
        {
	        endpos(t, t.elemtype, t.dims);
        }
        else
        {
	        scan(t.elems);
	        endpos(t, t.elemtype, t.dims, t.elems);
        }
    }

    @Override
    public void visitNewClass(JCNewClass t) {
        scan(t.encl);
        scan(t.clazz);
        scan(t.typeargs);
        scan(t.args);
        scan(t.def);
        endpos(t, t.encl, t.clazz, t.typeargs, t.args, t.def);
    }

    @Override
    public void visitParens(JCParens t) {
        scan(t.expr);
        endpos(t, t.expr);
    }

    @Override
    public void visitReturn(JCReturn t) {
        scan(t.expr);
        endpos(t, t.expr);
    }

    @Override
    public void visitSelect(JCFieldAccess t) {
        scan(t.selected);
        endpos(t, t.selected);
    }

    @Override
    public void visitSkip(JCSkip t) {
    }

    @Override
    public void visitSwitch(JCSwitch t) {
        scan(t.selector);
        scan(t.cases);
        endpos(t, t.selector, t.cases);
    }

    @Override
    public void visitSynchronized(JCSynchronized t) {
        scan(t.lock);
        scan(t.body);
        endpos(t, t.lock, t.body);
    }

    @Override
    public void visitThrow(JCThrow t) {
        scan(t.expr);
        endpos(t, t.expr);
    }

    @Override
    public void visitTopLevel(JCCompilationUnit t) {
        currentUnit = t;
        scan(t.packageAnnotations);
        scan(t.pid);
        scan(t.defs);
        endpos(t, t.packageAnnotations, t.pid, t.defs);
        currentUnit = null;
    }

    @Override
    public void visitTree(JCTree t) {
        assert false;
    }

    @Override
    public void visitTry(JCTry t) {
        scan(t.body);
        scan(t.catchers);
        scan(t.finalizer);
        endpos(t, t.body, t.catchers, t.finalizer);
    }

    @Override
    public void visitTypeApply(JCTypeApply t) {
        scan(t.clazz);
        scan(t.arguments);
        endpos(t, t.clazz, t.arguments);
    }

    @Override
    public void visitTypeArray(JCArrayTypeTree t) {
        scan(t.elemtype);
        endpos(t, t.elemtype);
    }

    @Override
    public void visitTypeBoundKind(TypeBoundKind t) {
    }

    @Override
    public void visitTypeCast(JCTypeCast t) {
        scan(t.clazz);
        scan(t.expr);
        endpos(t, t.clazz, t.expr);
    }

    @Override
    public void visitTypeUnion(JCTypeUnion t) {
        scan(t.alternatives);
        endpos(t, t.alternatives);
    }

    @Override
    public void visitTypeIdent(JCPrimitiveTypeTree t) {
    }

    @Override
    public void visitTypeParameter(JCTypeParameter t) {
        scan(t.bounds);
        endpos(t, t.bounds);
    }

    @Override
    public void visitTypeTest(JCInstanceOf t) {
        scan(t.expr);
        scan(t.clazz);
        endpos(t, t.expr, t.clazz);
    }

    @Override
    public void visitUnary(JCUnary t) {
        scan(t.arg);
        endpos(t, t.arg);
    }

    @Override
    public void visitVarDef(JCVariableDecl t) {
        scan(t.mods);
        scan(t.vartype);
        scan(t.init);
        endpos(t, t.mods, t.vartype, t.init);
    }

    @Override
    public void visitWhileLoop(JCWhileLoop t) {
        scan(t.cond);
        scan(t.body);
        endpos(t, t.cond, t.body);
    }

    @Override
    public void visitWildcard(JCWildcard t) {
        scan(t.kind);
        scan(t.inner);
        endpos(t, t.kind, t.inner);
    }
}
