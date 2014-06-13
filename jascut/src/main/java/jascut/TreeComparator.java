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
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import static com.sun.tools.javac.tree.JCTree.*;

/**
 * The class that contains static methods for comparison of ASTs.
 *
 * @author tronicek
 */
public class TreeComparator {

    public static boolean annotationListEquals(List<JCAnnotation> p1, List<JCAnnotation> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCAnnotation a1 = p1.get(i);
            JCAnnotation a2 = p2.get(i);
            if (!annotationEquals(a1, a2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean caseListEquals(List<JCCase> p1, List<JCCase> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCCase c1 = p1.get(i);
            JCCase c2 = p2.get(i);
            if (!caseEquals(c1, c2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean catchListEquals(List<JCCatch> p1, List<JCCatch> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCCatch c1 = p1.get(i);
            JCCatch c2 = p2.get(i);
            if (!catchEquals(c1, c2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean expressionListEquals(List<JCExpression> p1, List<JCExpression> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCExpression e1 = p1.get(i);
            JCExpression e2 = p2.get(i);
            if (!expressionEquals(e1, e2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean expressionStatementListEquals(List<JCExpressionStatement> p1, List<JCExpressionStatement> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCExpressionStatement s1 = p1.get(i);
            JCExpressionStatement s2 = p2.get(i);
            if (!expressionStatementEquals(s1, s2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean importListEquals(List<JCImport> p1, List<JCImport> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCImport i1 = p1.get(i);
            JCImport i2 = p2.get(i);
            if (!importEquals(i1, i2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean statementListEquals(List<JCStatement> p1, List<JCStatement> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCStatement s1 = p1.get(i);
            JCStatement s2 = p2.get(i);
            if (!statementEquals(s1, s2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean treeListEquals(List<? extends JCTree> p1, List<? extends JCTree> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCTree t1 = p1.get(i);
            JCTree t2 = p2.get(i);
            if (!treeEquals(t1, t2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean typeParameterListEquals(List<JCTypeParameter> p1, List<JCTypeParameter> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCTypeParameter tp1 = p1.get(i);
            JCTypeParameter tp2 = p2.get(i);
            if (!typeParameterEquals(tp1, tp2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean variableDeclListEquals(List<JCVariableDecl> p1, List<JCVariableDecl> p2) {
        if (p1 == null || p2 == null) {
            return p1 == p2;
        }
        if (p1.size() != p2.size()) {
            return false;
        }
        for (int i = 0; i < p1.size(); i++) {
            JCVariableDecl v1 = p1.get(i);
            JCVariableDecl v2 = p2.get(i);
            if (!variableDeclEquals(v1, v2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean annotationEquals(JCAnnotation a1, JCAnnotation a2) {
        return treeEquals(a1.annotationType, a2.annotationType)
                && expressionListEquals(a1.args, a2.args);
    }

    public static boolean arrayAccessEquals(JCArrayAccess t1, JCArrayAccess t2) {
        return expressionEquals(t1.indexed, t2.indexed)
                && expressionEquals(t1.index, t2.index);
    }

    public static boolean arrayTypeEquals(JCArrayTypeTree t1, JCArrayTypeTree t2) {
        return expressionEquals(t1.elemtype, t2.elemtype);
    }

    public static boolean assertEquals(JCAssert a1, JCAssert a2) {
        return expressionEquals(a1.cond, a2.cond)
                && expressionEquals(a1.detail, a2.detail);
    }

    public static boolean assignEquals(JCAssign a1, JCAssign a2) {
        return expressionEquals(a1.lhs, a2.lhs)
                && expressionEquals(a1.rhs, a2.rhs);
    }

    public static boolean assignOpEquals(JCAssignOp a1, JCAssignOp a2) {
        return expressionEquals(a1.lhs, a2.lhs)
                && a1.getTag() == a2.getTag()
                && expressionEquals(a1.rhs, a2.rhs);
    }

    public static boolean binaryEquals(JCBinary b1, JCBinary b2) {
        return expressionEquals(b1.lhs, b2.lhs)
                && b1.getTag() == b2.getTag()
                && expressionEquals(b1.rhs, b2.rhs);
    }

    public static boolean blockEquals(JCBlock b1, JCBlock b2) {
        if (b1 == null || b2 == null) {
            return b1 == b2;
        }
        return b1.flags == b2.flags
                && statementListEquals(b1.stats, b2.stats);
    }

    public static boolean breakEquals(JCBreak b1, JCBreak b2) {
        return nameEquals(b1.label, b2.label);
    }

    public static boolean caseEquals(JCCase c1, JCCase c2) {
        return expressionEquals(c1.pat, c2.pat)
                && statementListEquals(c1.stats, c2.stats);
    }

    public static boolean catchEquals(JCCatch c1, JCCatch c2) {
        return variableDeclEquals(c1.param, c2.param)
                && blockEquals(c1.body, c2.body);
    }

    public static boolean classDeclEquals(JCClassDecl c1, JCClassDecl c2) {
        if (c1 == null || c2 == null) {
            return c1 == c2;
        }
        return modifiersEquals(c1.mods, c2.mods)
                && typeParameterListEquals(c1.typarams, c2.typarams)
                && nameEquals(c1.name, c2.name)
                && treeEquals(c1.extending, c2.extending)
                && expressionListEquals(c1.implementing, c2.implementing)
                && treeListEquals(c1.defs, c2.defs);
    }

    public static boolean compilationUnitEquals(JCCompilationUnit t1, JCCompilationUnit t2) {
        return annotationListEquals(t1.packageAnnotations, t2.packageAnnotations)
                && expressionEquals(t1.pid, t2.pid)
                && treeListEquals(t1.defs, t2.defs);
    }

    public static boolean conditionalEquals(JCConditional c1, JCConditional c2) {
        return expressionEquals(c1.cond, c2.cond)
                && expressionEquals(c1.truepart, c2.truepart)
                && expressionEquals(c1.falsepart, c2.falsepart);
    }

    public static boolean continueEquals(JCContinue c1, JCContinue c2) {
        return nameEquals(c1.label, c2.label);
    }

    public static boolean doWhileLoopEquals(JCDoWhileLoop t1, JCDoWhileLoop t2) {
        return expressionEquals(t1.cond, t2.cond)
                && statementEquals(t1.body, t2.body);
    }

    public static boolean enhancedForLoopEquals(JCEnhancedForLoop t1, JCEnhancedForLoop t2) {
        return variableDeclEquals(t1.var, t2.var)
                && expressionEquals(t1.expr, t2.expr)
                && statementEquals(t1.body, t2.body);
    }

    public static boolean erroneousEquals(JCErroneous t1, JCErroneous t2) {
        if (t1 == null || t2 == null) {
            return t1 == t2;
        }
        return treeListEquals(t1.errs, t2.errs);
    }

    public static boolean expressionEquals(JCExpression e1, JCExpression e2) {
        if (e1 == null || e2 == null) {
            return e1 == e2;
        }
        if (e1.getKind() != e2.getKind()) {
            return false;
        }
        switch (e1.getKind()) {
            case AND:
            case CONDITIONAL_AND:
            case CONDITIONAL_OR:
            case DIVIDE:
            case EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL:
            case LEFT_SHIFT:
            case LESS_THAN:
            case LESS_THAN_EQUAL:
            case MINUS:
            case MULTIPLY:
            case NOT_EQUAL_TO:
            case OR:
            case PLUS:
            case REMAINDER:
            case RIGHT_SHIFT:
            case UNSIGNED_RIGHT_SHIFT:
            case XOR:
                return binaryEquals((JCBinary) e1, (JCBinary) e2);
            case AND_ASSIGNMENT:
            case DIVIDE_ASSIGNMENT:
            case LEFT_SHIFT_ASSIGNMENT:
            case MINUS_ASSIGNMENT:
            case MULTIPLY_ASSIGNMENT:
            case OR_ASSIGNMENT:
            case PLUS_ASSIGNMENT:
            case REMAINDER_ASSIGNMENT:
            case RIGHT_SHIFT_ASSIGNMENT:
            case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
            case XOR_ASSIGNMENT:
                return assignOpEquals((JCAssignOp) e1, (JCAssignOp) e2);
            case ANNOTATION:
                return annotationEquals((JCAnnotation) e1, (JCAnnotation) e2);
            case ARRAY_ACCESS:
                return arrayAccessEquals((JCArrayAccess) e1, (JCArrayAccess) e2);
            case ARRAY_TYPE:
                return arrayTypeEquals((JCArrayTypeTree) e1, (JCArrayTypeTree) e2);
            case ASSIGNMENT:
                return assignEquals((JCAssign) e1, (JCAssign) e2);
            case BITWISE_COMPLEMENT:
            case LOGICAL_COMPLEMENT:
            case POSTFIX_DECREMENT:
            case POSTFIX_INCREMENT:
            case PREFIX_DECREMENT:
            case PREFIX_INCREMENT:
            case UNARY_MINUS:
            case UNARY_PLUS:
                return unaryEquals((JCUnary) e1, (JCUnary) e2);
            case BOOLEAN_LITERAL:
            case CHAR_LITERAL:
            case DOUBLE_LITERAL:
            case FLOAT_LITERAL:
            case INT_LITERAL:
            case LONG_LITERAL:
            case NULL_LITERAL:
            case STRING_LITERAL:
                return literalEquals((JCLiteral) e1, (JCLiteral) e2);
            case CONDITIONAL_EXPRESSION:
                return conditionalEquals((JCConditional) e1, (JCConditional) e2);
            case ERRONEOUS:
                return erroneousEquals((JCErroneous) e1, (JCErroneous) e2);
            case IDENTIFIER:
                return identEquals((JCIdent) e1, (JCIdent) e2);
            case INSTANCE_OF:
                return instanceOfEquals((JCInstanceOf) e1, (JCInstanceOf) e2);
            case MEMBER_SELECT:
                return fieldAccessEquals((JCFieldAccess) e1, (JCFieldAccess) e2);
            case METHOD_INVOCATION:
                return methodInvocationEquals((JCMethodInvocation) e1, (JCMethodInvocation) e2);
            case NEW_ARRAY:
                return newArrayEquals((JCNewArray) e1, (JCNewArray) e2);
            case NEW_CLASS:
                return newClassEquals((JCNewClass) e1, (JCNewClass) e2);
            case PARAMETERIZED_TYPE:
                return typeApplyEquals((JCTypeApply) e1, (JCTypeApply) e2);
            case PARENTHESIZED:
                return parensEquals((JCParens) e1, (JCParens) e2);
            case PRIMITIVE_TYPE:
                return primitiveTypeTreeEquals((JCPrimitiveTypeTree) e1, (JCPrimitiveTypeTree) e2);
            case TYPE_CAST:
                return typeCastEquals((JCTypeCast) e1, (JCTypeCast) e2);
            case EXTENDS_WILDCARD:
            case SUPER_WILDCARD:
            case UNBOUNDED_WILDCARD:
                return wildcardEquals((JCWildcard) e1, (JCWildcard) e2);
        }
        assert false;
        return false;
    }

    public static boolean expressionStatementEquals(JCExpressionStatement t1, JCExpressionStatement t2) {
        return expressionEquals(t1.expr, t2.expr);
    }

    public static boolean fieldAccessEquals(JCFieldAccess t1, JCFieldAccess t2) {
        return expressionEquals(t1.selected, t2.selected)
                && nameEquals(t1.name, t2.name);
    }

    public static boolean forLoopEquals(JCForLoop t1, JCForLoop t2) {
        return statementListEquals(t1.init, t2.init)
                && expressionEquals(t1.cond, t2.cond)
                && expressionStatementListEquals(t1.step, t2.step)
                && statementEquals(t1.body, t2.body);
    }

    public static boolean identEquals(JCIdent i1, JCIdent i2) {
        return nameEquals(i1.name, i2.name);
    }

    public static boolean ifEquals(JCIf t1, JCIf t2) {
        return expressionEquals(t1.cond, t2.cond)
                && statementEquals(t1.thenpart, t2.thenpart)
                && statementEquals(t1.elsepart, t2.elsepart);
    }

    public static boolean importEquals(JCImport i1, JCImport i2) {
        return i1.staticImport == i2.staticImport
                && treeEquals(i1.qualid, i2.qualid);
    }

    public static boolean instanceOfEquals(JCInstanceOf t1, JCInstanceOf t2) {
        return expressionEquals(t1.expr, t2.expr)
                && treeEquals(t1.clazz, t2.clazz);
    }

    public static boolean labeledStatementEquals(JCLabeledStatement t1, JCLabeledStatement t2) {
        return nameEquals(t1.label, t2.label)
                && statementEquals(t1.body, t2.body);
    }

    public static boolean letExprEquals(LetExpr e1, LetExpr e2) {
        return treeEquals(e1.expr, e2.expr)
                && variableDeclListEquals(e1.defs, e2.defs);
    }

    public static boolean literalEquals(JCLiteral t1, JCLiteral t2) {
        return t1.typetag == t2.typetag
                && objectEquals(t1.value, t2.value);
    }

    public static boolean methodDeclEquals(JCMethodDecl t1, JCMethodDecl t2) {
        return modifiersEquals(t1.mods, t2.mods)
                && typeParameterListEquals(t1.typarams, t2.typarams)
                && expressionEquals(t1.restype, t2.restype)
                && nameEquals(t1.name, t2.name)
                && variableDeclListEquals(t1.params, t2.params)
                && expressionListEquals(t1.thrown, t2.thrown)
                && expressionEquals(t1.defaultValue, t2.defaultValue)
                && blockEquals(t1.body, t2.body);
    }

    public static boolean methodInvocationEquals(JCMethodInvocation t1, JCMethodInvocation t2) {
        return expressionListEquals(t1.typeargs, t2.typeargs)
                && expressionEquals(t1.meth, t2.meth)
                && expressionListEquals(t1.args, t2.args);
    }

    public static boolean methodSignatureEquals(JCMethodDecl t1, JCMethodDecl t2) {
        return modifiersEquals(t1.mods, t2.mods)
                && typeParameterListEquals(t1.typarams, t2.typarams)
                && expressionEquals(t1.restype, t2.restype)
                && nameEquals(t1.name, t2.name)
                && variableDeclListEquals(t1.params, t2.params)
                && expressionListEquals(t1.thrown, t2.thrown)
                && expressionEquals(t1.defaultValue, t2.defaultValue);
    }

    public static boolean modifiersEquals(JCModifiers m1, JCModifiers m2) {
        return annotationListEquals(m1.annotations, m2.annotations)
                && (m1.flags == m2.flags);
    }

    public static boolean nameEquals(Name n1, Name n2) {
        if (n1 == null || n2 == null) {
            return n1 == n2;
        }
        return n1.equals(n2);
    }

    public static boolean newArrayEquals(JCNewArray t1, JCNewArray t2) {
        return expressionEquals(t1.elemtype, t2.elemtype)
                && expressionListEquals(t1.dims, t2.dims)
                && expressionListEquals(t1.elems, t2.elems);
    }

    public static boolean newClassEquals(JCNewClass t1, JCNewClass t2) {
        return expressionEquals(t1.encl, t2.encl)
                && expressionEquals(t1.clazz, t2.clazz)
                && expressionListEquals(t1.typeargs, t2.typeargs)
                && expressionListEquals(t1.args, t2.args)
                && classDeclEquals(t1.def, t2.def);
    }

    public static boolean objectEquals(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return o1 == o2;
        }
        return o1.equals(o2);
    }

    public static boolean parensEquals(JCParens t1, JCParens t2) {
        return expressionEquals(t1.expr, t2.expr);
    }

    public static boolean primitiveTypeTreeEquals(JCPrimitiveTypeTree t1, JCPrimitiveTypeTree t2) {
        return t1.typetag == t2.typetag;
    }

    public static boolean returnEquals(JCReturn r1, JCReturn r2) {
        return expressionEquals(r1.expr, r2.expr);
    }

    public static boolean skipEquals(JCSkip t1, JCSkip t2) {
        return true;
    }

    public static boolean statementEquals(JCStatement s1, JCStatement s2) {
        if (s1 == null || s2 == null) {
            return s1 == s2;
        }
        if (s1.getKind() != s2.getKind()) {
            return false;
        }
        switch (s1.getKind()) {
            case ASSERT:
                return assertEquals((JCAssert) s1, (JCAssert) s2);
            case BLOCK:
                return blockEquals((JCBlock) s1, (JCBlock) s2);
            case BREAK:
                return breakEquals((JCBreak) s1, (JCBreak) s2);
            case CASE:
                return caseEquals((JCCase) s1, (JCCase) s2);
            case CLASS:
                return classDeclEquals((JCClassDecl) s1, (JCClassDecl) s2);
            case CONTINUE:
                return continueEquals((JCContinue) s1, (JCContinue) s2);
            case DO_WHILE_LOOP:
                return doWhileLoopEquals((JCDoWhileLoop) s1, (JCDoWhileLoop) s2);
            case EMPTY_STATEMENT:
                return skipEquals((JCSkip) s1, (JCSkip) s2);
            case ENHANCED_FOR_LOOP:
                return enhancedForLoopEquals((JCEnhancedForLoop) s1, (JCEnhancedForLoop) s2);
            case EXPRESSION_STATEMENT:
                return expressionStatementEquals((JCExpressionStatement) s1, (JCExpressionStatement) s2);
            case FOR_LOOP:
                return forLoopEquals((JCForLoop) s1, (JCForLoop) s2);
            case IF:
                return ifEquals((JCIf) s1, (JCIf) s2);
            case LABELED_STATEMENT:
                return labeledStatementEquals((JCLabeledStatement) s1, (JCLabeledStatement) s2);
            case RETURN:
                return returnEquals((JCReturn) s1, (JCReturn) s2);
            case SWITCH:
                return switchEquals((JCSwitch) s1, (JCSwitch) s2);
            case SYNCHRONIZED:
                return synchronizedEquals((JCSynchronized) s1, (JCSynchronized) s2);
            case THROW:
                return throwEquals((JCThrow) s1, (JCThrow) s2);
            case TRY:
                return tryEquals((JCTry) s1, (JCTry) s2);
            case VARIABLE:
                return variableDeclEquals((JCVariableDecl) s1, (JCVariableDecl) s2);
            case WHILE_LOOP:
                return whileLoopEquals((JCWhileLoop) s1, (JCWhileLoop) s2);
        }
        assert false;
        return false;
    }

    public static boolean switchEquals(JCSwitch s1, JCSwitch s2) {
        return expressionEquals(s1.selector, s2.selector)
                && caseListEquals(s1.cases, s2.cases);
    }

//    public static boolean symbolEquals(Symbol s1, Symbol s2) {
//        if (s1 == null || s2 == null) {
//            return s1 == s2;
//        }
//        return s1.equals(s2);
//    }
//
    public static boolean synchronizedEquals(JCSynchronized s1, JCSynchronized s2) {
        return expressionEquals(s1.lock, s2.lock)
                && blockEquals(s1.body, s2.body);
    }

    public static boolean throwEquals(JCThrow t1, JCThrow t2) {
        return expressionEquals(t1.expr, t2.expr);
    }

    public static boolean treeEquals(JCTree t1, JCTree t2) {
        if (t1 == null || t2 == null) {
            return t1 == t2;
        }
        if (t1.getKind() != t2.getKind()) {
            return false;
        }
        switch (t1.getKind()) {
            case AND:
            case CONDITIONAL_AND:
            case CONDITIONAL_OR:
            case DIVIDE:
            case EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL:
            case LEFT_SHIFT:
            case LESS_THAN:
            case LESS_THAN_EQUAL:
            case MINUS:
            case MULTIPLY:
            case NOT_EQUAL_TO:
            case OR:
            case PLUS:
            case REMAINDER:
            case RIGHT_SHIFT:
            case UNSIGNED_RIGHT_SHIFT:
            case XOR:
                return binaryEquals((JCBinary) t1, (JCBinary) t2);
            case AND_ASSIGNMENT:
            case DIVIDE_ASSIGNMENT:
            case LEFT_SHIFT_ASSIGNMENT:
            case MINUS_ASSIGNMENT:
            case MULTIPLY_ASSIGNMENT:
            case OR_ASSIGNMENT:
            case PLUS_ASSIGNMENT:
            case REMAINDER_ASSIGNMENT:
            case RIGHT_SHIFT_ASSIGNMENT:
            case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
            case XOR_ASSIGNMENT:
                return assignOpEquals((JCAssignOp) t1, (JCAssignOp) t2);
            case ANNOTATION:
                return annotationEquals((JCAnnotation) t1, (JCAnnotation) t2);
            case ARRAY_ACCESS:
                return arrayAccessEquals((JCArrayAccess) t1, (JCArrayAccess) t2);
            case ASSIGNMENT:
                return assignEquals((JCAssign) t1, (JCAssign) t2);
            case BITWISE_COMPLEMENT:
            case LOGICAL_COMPLEMENT:
            case POSTFIX_DECREMENT:
            case POSTFIX_INCREMENT:
            case PREFIX_DECREMENT:
            case PREFIX_INCREMENT:
            case UNARY_MINUS:
            case UNARY_PLUS:
                return unaryEquals((JCUnary) t1, (JCUnary) t2);
            case BOOLEAN_LITERAL:
            case CHAR_LITERAL:
            case DOUBLE_LITERAL:
            case FLOAT_LITERAL:
            case INT_LITERAL:
            case LONG_LITERAL:
            case NULL_LITERAL:
            case STRING_LITERAL:
                return literalEquals((JCLiteral) t1, (JCLiteral) t2);
            case CONDITIONAL_EXPRESSION:
                return conditionalEquals((JCConditional) t1, (JCConditional) t2);
            case ERRONEOUS:
                return erroneousEquals((JCErroneous) t1, (JCErroneous) t2);
            case IDENTIFIER:
                return identEquals((JCIdent) t1, (JCIdent) t2);
            case INSTANCE_OF:
                return instanceOfEquals((JCInstanceOf) t1, (JCInstanceOf) t2);
            case MEMBER_SELECT:
                return fieldAccessEquals((JCFieldAccess) t1, (JCFieldAccess) t2);
            case METHOD:
                return methodDeclEquals((JCMethodDecl) t1, (JCMethodDecl) t2);
            case METHOD_INVOCATION:
                return methodInvocationEquals((JCMethodInvocation) t1, (JCMethodInvocation) t2);
            case NEW_ARRAY:
                return newArrayEquals((JCNewArray) t1, (JCNewArray) t2);
            case NEW_CLASS:
                return newClassEquals((JCNewClass) t1, (JCNewClass) t2);
            case PARENTHESIZED:
                return parensEquals((JCParens) t1, (JCParens) t2);
            case TYPE_CAST:
                return typeCastEquals((JCTypeCast) t1, (JCTypeCast) t2);
            case ASSERT:
                return assertEquals((JCAssert) t1, (JCAssert) t2);
            case BLOCK:
                return blockEquals((JCBlock) t1, (JCBlock) t2);
            case BREAK:
                return breakEquals((JCBreak) t1, (JCBreak) t2);
            case CASE:
                return caseEquals((JCCase) t1, (JCCase) t2);
            case CLASS:
                return classDeclEquals((JCClassDecl) t1, (JCClassDecl) t2);
            case CONTINUE:
                return continueEquals((JCContinue) t1, (JCContinue) t2);
            case DO_WHILE_LOOP:
                return doWhileLoopEquals((JCDoWhileLoop) t1, (JCDoWhileLoop) t2);
            case EMPTY_STATEMENT:
                return skipEquals((JCSkip) t1, (JCSkip) t2);
            case ENHANCED_FOR_LOOP:
                return enhancedForLoopEquals((JCEnhancedForLoop) t1, (JCEnhancedForLoop) t2);
            case EXPRESSION_STATEMENT:
                return expressionStatementEquals((JCExpressionStatement) t1, (JCExpressionStatement) t2);
            case FOR_LOOP:
                return forLoopEquals((JCForLoop) t1, (JCForLoop) t2);
            case IF:
                return ifEquals((JCIf) t1, (JCIf) t2);
            case LABELED_STATEMENT:
                return labeledStatementEquals((JCLabeledStatement) t1, (JCLabeledStatement) t2);
            case RETURN:
                return returnEquals((JCReturn) t1, (JCReturn) t2);
            case SWITCH:
                return switchEquals((JCSwitch) t1, (JCSwitch) t2);
            case SYNCHRONIZED:
                return synchronizedEquals((JCSynchronized) t1, (JCSynchronized) t2);
            case THROW:
                return throwEquals((JCThrow) t1, (JCThrow) t2);
            case TRY:
                return tryEquals((JCTry) t1, (JCTry) t2);
            case VARIABLE:
                return variableDeclEquals((JCVariableDecl) t1, (JCVariableDecl) t2);
            case WHILE_LOOP:
                return whileLoopEquals((JCWhileLoop) t1, (JCWhileLoop) t2);
            case ARRAY_TYPE:
                return arrayTypeEquals((JCArrayTypeTree) t1, (JCArrayTypeTree) t2);
            case COMPILATION_UNIT:
                return compilationUnitEquals((JCCompilationUnit) t1, (JCCompilationUnit) t2);
            case EXTENDS_WILDCARD:
            case SUPER_WILDCARD:
            case UNBOUNDED_WILDCARD:
                return wildcardEquals((JCWildcard) t1, (JCWildcard) t2);
            case IMPORT:
                return importEquals((JCImport) t1, (JCImport) t2);
            case MODIFIERS:
                return modifiersEquals((JCModifiers) t1, (JCModifiers) t2);
            case PARAMETERIZED_TYPE:
                return typeApplyEquals((JCTypeApply) t1, (JCTypeApply) t2);
            case PRIMITIVE_TYPE:
                return primitiveTypeTreeEquals((JCPrimitiveTypeTree) t1, (JCPrimitiveTypeTree) t2);
            case TYPE_PARAMETER:
                return typeParameterEquals((JCTypeParameter) t1, (JCTypeParameter) t2);
        }
        assert false;
        return false;
    }

    public static boolean tryEquals(JCTry t1, JCTry t2) {
        return blockEquals(t1.body, t2.body)
                && catchListEquals(t1.catchers, t2.catchers)
                && blockEquals(t1.finalizer, t2.finalizer);
    }

    public static boolean typeApplyEquals(JCTypeApply t1, JCTypeApply t2) {
        return expressionEquals(t1.clazz, t2.clazz)
                && expressionListEquals(t1.arguments, t2.arguments);
    }

    public static boolean typeBoundKindEquals(TypeBoundKind t1, TypeBoundKind t2) {
        return t1.kind == t2.kind;
    }

    public static boolean typeCastEquals(JCTypeCast t1, JCTypeCast t2) {
        return treeEquals(t1.clazz, t2.clazz)
                && expressionEquals(t1.expr, t2.expr);
    }

    public static boolean typeUnionEquals(JCTypeUnion t1, JCTypeUnion t2) {
        return expressionListEquals(t1.alternatives, t2.alternatives);
    }

    public static boolean typeParameterEquals(JCTypeParameter tp1, JCTypeParameter tp2) {
        return nameEquals(tp1.name, tp2.name)
                && expressionListEquals(tp1.bounds, tp2.bounds);
    }

    public static boolean unaryEquals(JCUnary u1, JCUnary u2) {
        return u1.getTag() == u2.getTag()
                && expressionEquals(u1.arg, u2.arg);
    }

    public static boolean variableDeclEquals(JCVariableDecl v1, JCVariableDecl v2) {
        return modifiersEquals(v1.mods, v2.mods)
                && expressionEquals(v1.vartype, v2.vartype)
                && nameEquals(v1.name, v2.name)
                && expressionEquals(v1.init, v2.init);
    }

    public static boolean whileLoopEquals(JCWhileLoop t1, JCWhileLoop t2) {
        return expressionEquals(t1.cond, t2.cond)
                && statementEquals(t1.body, t2.body);
    }

    public static boolean wildcardEquals(JCWildcard t1, JCWildcard t2) {
        return typeBoundKindEquals(t1.kind, t2.kind)
                && treeEquals(t1.inner, t2.inner);
    }
}
