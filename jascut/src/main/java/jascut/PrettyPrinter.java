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
import com.sun.tools.javac.tree.Pretty;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.List;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The AST scanner that gathers positions and converts ASTs to text.
 *
 * @author tronicek
 */
public class PrettyPrinter extends Pretty {

    private PositionWriter out;
    private JCExpression pid;
    private Integer pkgPos;
    private Integer pkgEndPos;
    private Integer importPos;
    private Integer importEndPos;
    private Integer declPos;
    private Map<JCTree, Integer> startPos = new HashMap<JCTree, Integer>();
    private Map<JCTree, Integer> endPos = new HashMap<JCTree, Integer>();

    static class PrettyException extends RuntimeException {

        public PrettyException(Throwable cause) {
            super(cause);
        }
    }

    public PrettyPrinter() {
        super(new PositionWriter(8192), true);
        // set the out field
        try {
            Class<?> c = Pretty.class;
            Field f = c.getDeclaredField("out");
            f.setAccessible(true);
            out = (PositionWriter) f.get(this);
        } catch (Exception e) {
            throw new PrettyException(e);
        }
    }

    public String getSource() {
        return out.toString();
    }

    public Integer getPkgPos() {
        return pkgPos;
    }

    public Integer getPkgEndPos() {
        return pkgEndPos;
    }

    public Integer getImportPos() {
        return importPos;
    }

    public Integer getImportEndPos() {
        return importEndPos;
    }

    public Integer getDeclPos() {
        return declPos;
    }

    public Map<JCTree, Integer> getStartPos() {
        return startPos;
    }

    public Map<JCTree, Integer> getEndPos() {
        return endPos;
    }

    public SourceCode sourceCode() {
        return new SourceCode(
                out.toString(), pkgPos, pkgEndPos, importPos,
                importEndPos, declPos, startPos, endPos);
    }

    private void start(JCTree t) {
        startPos.put(t, out.getCount());
    }

    private void end(JCTree t) {
        endPos.put(t, out.getCount());
    }

    private void open(int ownPrec) {
        try {
            Class<?> c = Pretty.class;
            Field f = c.getDeclaredField("prec");
            f.setAccessible(true);
            int prec = (Integer) f.get(this);
            if (ownPrec < prec) {
                out.write("(");
            }
        } catch (Exception e) {
            throw new PrettyException(e);
        }
    }

    private void close(int ownPrec) {
        try {
            Class<?> c = Pretty.class;
            Field f = c.getDeclaredField("prec");
            f.setAccessible(true);
            int prec = (Integer) f.get(this);
            if (ownPrec < prec) {
                out.write(")");
            }
        } catch (Exception e) {
            throw new PrettyException(e);
        }
    }

    @Override
    public void printExpr(JCTree t) throws IOException {
        if (t == pid) {
            pkgPos = out.getCount();
        }
        super.printExpr(t);
        if (t == pid) {
            pkgEndPos = out.getCount();
        }
    }

    @Override
    public void printTypeParameters(List<JCTypeParameter> trees) throws IOException {
        if (trees.nonEmpty()) {
            print("<");
            printExprs(trees);
            print("> ");
        }
    }

    @Override
    public void visitAnnotation(JCAnnotation t) {
        try {
            start(t);
            print("@");
            printExpr(t.annotationType);
            if (t.args.length() > 0) {
                print("(");
                printExprs(t.args);
                print(")");
            }
            end(t);
        } catch (IOException e) {
            throw new PrettyException(e);
        }
    }

    @Override
    public void visitApply(JCMethodInvocation t) {
        start(t);
        super.visitApply(t);
        end(t);
    }

    @Override
    public void visitAssert(JCAssert t) {
        start(t);
        super.visitAssert(t);
        end(t);
    }

    @Override
    public void visitAssign(JCAssign t) {
        start(t);
        super.visitAssign(t);
        end(t);
    }

    @Override
    public void visitAssignop(JCAssignOp t) {
        start(t);
        super.visitAssignop(t);
        end(t);
    }

    @Override
    public void visitBinary(JCBinary t) {
        start(t);
        super.visitBinary(t);
        end(t);
    }

    @Override
    public void visitBlock(JCBlock t) {
        start(t);
        super.visitBlock(t);
        end(t);
    }

    @Override
    public void visitBreak(JCBreak t) {
        start(t);
        super.visitBreak(t);
        end(t);
    }

    @Override
    public void visitCase(JCCase t) {
        start(t);
        super.visitCase(t);
        end(t);
    }

    @Override
    public void visitCatch(JCCatch t) {
        start(t);
        super.visitCatch(t);
        end(t);
    }

    @Override
    public void visitClassDef(JCClassDecl t) {
        if (declPos == null) {
            declPos = out.getCount();
        }
        start(t);
        super.visitClassDef(t);
        end(t);
    }

    @Override
    public void visitConditional(JCConditional t) {
        start(t);
        super.visitConditional(t);
        end(t);
    }

    @Override
    public void visitContinue(JCContinue t) {
        start(t);
        super.visitContinue(t);
        end(t);
    }

    @Override
    public void visitDoLoop(JCDoWhileLoop t) {
        start(t);
        super.visitDoLoop(t);
        end(t);
    }

    @Override
    public void visitErroneous(JCErroneous t) {
        start(t);
        super.visitErroneous(t);
        end(t);
    }

    @Override
    public void visitExec(JCExpressionStatement t) {
        start(t);
        super.visitExec(t);
        end(t);
    }

    @Override
    public void visitForLoop(JCForLoop t) {
        start(t);
        super.visitForLoop(t);
        end(t);
    }

    @Override
    public void visitForeachLoop(JCEnhancedForLoop t) {
        start(t);
        super.visitForeachLoop(t);
        end(t);
    }

    @Override
    public void visitIdent(JCIdent t) {
        start(t);
        super.visitIdent(t);
        end(t);
    }

    @Override
    public void visitIf(JCIf t) {
        start(t);
        super.visitIf(t);
        end(t);
    }

    @Override
    public void visitImport(JCImport t) {
        if (importPos == null) {
            importPos = out.getCount();
        }
        start(t);
        super.visitImport(t);
        end(t);
        importEndPos = out.getCount();
    }

    @Override
    public void visitIndexed(JCArrayAccess t) {
        start(t);
        super.visitIndexed(t);
        end(t);
    }

    @Override
    public void visitLabelled(JCLabeledStatement t) {
        start(t);
        super.visitLabelled(t);
        end(t);
    }

    @Override
    public void visitLetExpr(LetExpr t) {
        start(t);
        super.visitLetExpr(t);
        end(t);
    }

    @Override
    public void visitLiteral(JCLiteral t) {
        start(t);
        super.visitLiteral(t);
        end(t);
    }

    @Override
    public void visitMethodDef(JCMethodDecl t) {
        start(t);
        MethodSymbol ms = t.sym;
        if ((ms.flags() & Flags.GENERATEDCONSTR) == 0) {
            super.visitMethodDef(t);
        }
        end(t);
    }

    @Override
    public void visitModifiers(JCModifiers t) {
        start(t);
        super.visitModifiers(t);
        end(t);
    }

    @Override
    public void visitNewArray(JCNewArray t) {
        start(t);
        super.visitNewArray(t);
        end(t);
    }

    @Override
    public void visitNewClass(JCNewClass t) {
        start(t);
        super.visitNewClass(t);
        end(t);
    }

    @Override
    public void visitParens(JCParens t) {
        start(t);
        super.visitParens(t);
        end(t);
    }

    @Override
    public void visitReturn(JCReturn t) {
        start(t);
        super.visitReturn(t);
        end(t);
    }

    @Override
    public void visitSelect(JCFieldAccess t) {
        start(t);
        super.visitSelect(t);
        end(t);
    }

    @Override
    public void visitSkip(JCSkip t) {
        start(t);
        super.visitSkip(t);
        end(t);
    }

    @Override
    public void visitSwitch(JCSwitch t) {
        start(t);
        super.visitSwitch(t);
        end(t);
    }

    @Override
    public void visitSynchronized(JCSynchronized t) {
        start(t);
        super.visitSynchronized(t);
        end(t);
    }

    @Override
    public void visitThrow(JCThrow t) {
        start(t);
        super.visitThrow(t);
        end(t);
    }

    @Override
    public void visitTopLevel(JCCompilationUnit t) {
        pid = t.pid;
        start(t);
        super.visitTopLevel(t);
        end(t);
    }

    @Override
    public void visitTree(JCTree t) {
        start(t);
        super.visitTree(t);
        end(t);
    }

    @Override
    public void visitTry(JCTry t) {
        start(t);
        super.visitTry(t);
        end(t);
    }

    @Override
    public void visitTypeApply(JCTypeApply t) {
        start(t);
        super.visitTypeApply(t);
        end(t);
    }

    @Override
    public void visitTypeArray(JCArrayTypeTree t) {
        start(t);
        super.visitTypeArray(t);
        end(t);
    }

    @Override
    public void visitTypeBoundKind(TypeBoundKind t) {
        start(t);
        super.visitTypeBoundKind(t);
        end(t);
    }

    @Override
    public void visitTypeCast(JCTypeCast t) {
        try {
            start(t);
            open(TreeInfo.prefixPrec);
            print("(");
            printExpr(t.clazz);
            print(") ");
            printExpr(t.expr, TreeInfo.prefixPrec);
            close(TreeInfo.prefixPrec);
            end(t);
        } catch (IOException e) {
            throw new PrettyException(e);
        }
    }

    @Override
    public void visitTypeUnion(JCTypeUnion t) {
        start(t);
        super.visitTypeUnion(t);
        end(t);
    }

    @Override
    public void visitTypeIdent(JCPrimitiveTypeTree t) {
        start(t);
        super.visitTypeIdent(t);
        end(t);
    }

    @Override
    public void visitTypeParameter(JCTypeParameter t) {
        start(t);
        super.visitTypeParameter(t);
        end(t);
    }

    @Override
    public void visitTypeTest(JCInstanceOf t) {
        start(t);
        super.visitTypeTest(t);
        end(t);
    }

    @Override
    public void visitUnary(JCUnary t) {
        start(t);
        super.visitUnary(t);
        end(t);
    }

    @Override
    public void visitVarDef(JCVariableDecl t) {
        start(t);
        super.visitVarDef(t);
        end(t);
    }

    @Override
    public void visitWhileLoop(JCWhileLoop t) {
        start(t);
        super.visitWhileLoop(t);
        end(t);
    }

    @Override
    public void visitWildcard(JCWildcard t) {
        start(t);
        super.visitWildcard(t);
        end(t);
    }
}
