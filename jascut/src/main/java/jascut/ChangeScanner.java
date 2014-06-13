/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
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
import java.util.Map;
import reporting.Report;
import static jascut.TreeComparator.*;

/**
 * The AST scanner that checks for changes in source code.
 *
 * @author tronicek
 */
public class ChangeScanner extends TreeScanner {

    private JCCompilationUnit workingCopy;
    private Map<JCTree, JCTree> peers;
    private CodeRewriter rewriter;

    public ChangeScanner(JCCompilationUnit workingCopy, Map<JCTree, JCTree> peers, CodeRewriter rewriter) {
        this.workingCopy = workingCopy;
        this.peers = peers;
        this.rewriter = rewriter;
    }

    public void scanForChanges() {
        workingCopy.accept(this);
        String n = workingCopy.sourcefile.getName();
        int cc = rewriter.getChangeCount();
        Report.changes(n, cc);
    }

    public String getModifiedCode() {
        return rewriter.getModifiedCode();
    }

    private <T extends JCTree> T getPeer(T t) {
        return (T) peers.get(t);
    }

    @Override
    public void visitAnnotation(JCAnnotation t) {
        super.visitAnnotation(t);
    }

    @Override
    public void visitApply(JCMethodInvocation t) {
        JCMethodInvocation p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!methodInvocationEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitApply(t);
    }

    @Override
    public void visitAssert(JCAssert t) {
        JCAssert p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!assertEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitAssert(t);
    }

    @Override
    public void visitAssign(JCAssign t) {
        JCAssign p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!assignEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitAssign(t);
    }

    @Override
    public void visitAssignop(JCAssignOp t) {
        super.visitAssignop(t);
    }

    @Override
    public void visitBinary(JCBinary t) {
        super.visitBinary(t);
    }

    @Override
    public void visitBlock(JCBlock t) {
        super.visitBlock(t);
    }

    @Override
    public void visitBreak(JCBreak t) {
        super.visitBreak(t);
    }

    @Override
    public void visitCase(JCCase t) {
        super.visitCase(t);
    }

    @Override
    public void visitCatch(JCCatch t) {
        super.visitCatch(t);
    }

    @Override
    public void visitClassDef(JCClassDecl t) {
        JCClassDecl p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!expressionListEquals(p.implementing, t.implementing)) {
            rewriter.changeClassInterfaces(p, t);
        }
        super.visitClassDef(t);
    }

    @Override
    public void visitConditional(JCConditional t) {
        JCConditional p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!conditionalEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitConditional(t);
    }

    @Override
    public void visitContinue(JCContinue t) {
        super.visitContinue(t);
    }

    @Override
    public void visitDoLoop(JCDoWhileLoop t) {
        super.visitDoLoop(t);
    }

    @Override
    public void visitErroneous(JCErroneous t) {
        super.visitErroneous(t);
    }

    @Override
    public void visitExec(JCExpressionStatement t) {
        JCExpressionStatement p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!expressionStatementEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitExec(t);
    }

    @Override
    public void visitForLoop(JCForLoop t) {
        JCForLoop p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!forLoopEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitForLoop(t);
    }

    @Override
    public void visitForeachLoop(JCEnhancedForLoop t) {
        JCEnhancedForLoop p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!enhancedForLoopEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitForeachLoop(t);
    }

    @Override
    public void visitIdent(JCIdent t) {
        JCIdent p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!identEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitIdent(t);
    }

    @Override
    public void visitIf(JCIf t) {
        super.visitIf(t);
    }

    @Override
    public void visitImport(JCImport t) {
        super.visitImport(t);
    }

    @Override
    public void visitIndexed(JCArrayAccess t) {
        super.visitIndexed(t);
    }

    @Override
    public void visitLabelled(JCLabeledStatement t) {
        super.visitLabelled(t);
    }

    @Override
    public void visitLetExpr(LetExpr t) {
        super.visitLetExpr(t);
    }

    @Override
    public void visitLiteral(JCLiteral t) {
        super.visitLiteral(t);
    }

    @Override
    public void visitMethodDef(JCMethodDecl t) {
        MethodSymbol ms = t.sym;
        if ((ms.flags() & Flags.GENERATEDCONSTR) != 0) {
            return;
        }
        JCMethodDecl p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!methodSignatureEquals(p, t)) {
            rewriter.changeMethodSignature(p, t);
        }
        super.visitMethodDef(t);
    }

    @Override
    public void visitModifiers(JCModifiers t) {
        super.visitModifiers(t);
    }

    @Override
    public void visitNewArray(JCNewArray t) {
        JCNewArray p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!newArrayEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitNewArray(t);
    }

    @Override
    public void visitNewClass(JCNewClass t) {
        JCNewClass p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!newClassEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitNewClass(t);
    }

    @Override
    public void visitParens(JCParens t) {
        JCParens p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!parensEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitParens(t);
    }

    @Override
    public void visitReturn(JCReturn t) {
        JCReturn p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!returnEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitReturn(t);
    }

    @Override
    public void visitSelect(JCFieldAccess t) {
        JCFieldAccess p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!fieldAccessEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitSelect(t);
    }

    @Override
    public void visitSkip(JCSkip t) {
        super.visitSkip(t);
    }

    @Override
    public void visitSwitch(JCSwitch t) {
        super.visitSwitch(t);
    }

    @Override
    public void visitSynchronized(JCSynchronized t) {
        super.visitSynchronized(t);
    }

    @Override
    public void visitThrow(JCThrow t) {
        super.visitThrow(t);
    }

    @Override
    public void visitTopLevel(JCCompilationUnit t) {
        JCCompilationUnit p = getPeer(t);
        if (!importListEquals(p.getImports(), t.getImports())) {
            rewriter.changeImports();
        }
        super.visitTopLevel(t);
    }

    @Override
    public void visitTree(JCTree t) {
        super.visitTree(t);
    }

    @Override
    public void visitTry(JCTry t) {
        super.visitTry(t);
    }

    @Override
    public void visitTypeApply(JCTypeApply t) {
        super.visitTypeApply(t);
    }

    @Override
    public void visitTypeArray(JCArrayTypeTree t) {
        super.visitTypeArray(t);
    }

    @Override
    public void visitTypeBoundKind(TypeBoundKind t) {
        super.visitTypeBoundKind(t);
    }

    @Override
    public void visitTypeCast(JCTypeCast t) {
        super.visitTypeCast(t);
    }

    @Override
    public void visitTypeUnion(JCTypeUnion t) {
        super.visitTypeUnion(t);
    }

    @Override
    public void visitTypeIdent(JCPrimitiveTypeTree t) {
        super.visitTypeIdent(t);
    }

    @Override
    public void visitTypeParameter(JCTypeParameter t) {
        super.visitTypeParameter(t);
    }

    @Override
    public void visitTypeTest(JCInstanceOf t) {
        JCInstanceOf p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!instanceOfEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitTypeTest(t);
    }

    @Override
    public void visitUnary(JCUnary t) {
        super.visitUnary(t);
    }

    @Override
    public void visitVarDef(JCVariableDecl t) {
        JCVariableDecl p = getPeer(t);
        if (p == null) {
            return;
        }
        if (!variableDeclEquals(p, t)) {
            rewriter.changeTree(p, t);
        }
        super.visitVarDef(t);
    }

    @Override
    public void visitWhileLoop(JCWhileLoop t) {
        super.visitWhileLoop(t);
    }

    @Override
    public void visitWildcard(JCWildcard t) {
        super.visitWildcard(t);
    }
}
