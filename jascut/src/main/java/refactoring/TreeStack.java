package refactoring;

import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Name;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tronicek
 */
public class TreeStack {

    private Deque<JCTree> trees = new ArrayDeque<JCTree>();

    private class VarScanner extends TreeScanner {

        List<JCVariableDecl> vars = new ArrayList<JCVariableDecl>();

        @Override
        public void visitClassDef(JCClassDecl t) {
            // do not visit local classes
        }

        @Override
        public void visitVarDef(JCVariableDecl t) {
            vars.add(t);
            super.visitVarDef(t);
        }
    }

    public void push(JCTree tree) {
        trees.push(tree);
    }

    public JCTree pop() {
        return trees.pop();
    }

    public JCTree peek() {
        return trees.peek();
    }

    public JCClassDecl peekClass() {
        Iterator<JCTree> it = trees.iterator();
        while (it.hasNext()) {
            JCTree t = it.next();
            if (t.getKind() == Kind.CLASS) {
                return (JCClassDecl) t;
            }
        }
        return null;
    }

    public List<JCClassDecl> peekClasses() {
        List<JCClassDecl> cls = new ArrayList<JCClassDecl>();
        Iterator<JCTree> it = trees.iterator();
        while (it.hasNext()) {
            JCTree t = it.next();
            if (t.getKind() == Kind.CLASS) {
                cls.add((JCClassDecl) t);
            }
        }
        return cls;
    }

    public List<JCMethodDecl> peekMethods() {
        List<JCMethodDecl> methods = new ArrayList<JCMethodDecl>();
        Iterator<JCTree> it = trees.iterator();
        while (it.hasNext()) {
            JCTree t = it.next();
            if (t.getKind() == Kind.METHOD) {
                methods.add((JCMethodDecl) t);
            }
        }
        return methods;
    }

    public Symbol lookup(Name name) {
        for (JCTree t : trees) {
            Kind k = t.getKind();
            switch (k) {
                case CLASS: {
                    JCClassDecl cd = (JCClassDecl) t;
                    ClassSymbol cs = cd.sym;
                    Scope.Entry e = cs.members().lookup(name);
                    if (e.scope != null) {
                        return e.sym;
                    }
                    break;
                }
                case BLOCK:
                case METHOD: {
                    VarScanner vscan = new VarScanner();
                    vscan.scan(t);
                    for (JCVariableDecl v : vscan.vars) {
                        if (v.name.contentEquals(name)) {
                            return v.sym;
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    public boolean isUsed(Name name) {
        Symbol s = lookup(name);
        return s != null;
    }

    public boolean isMember(Name name) {
        for (JCTree t : trees) {
            if (t.getKind() == Kind.CLASS) {
                JCClassDecl cd = (JCClassDecl) t;
                ClassSymbol cs = cd.sym;
                Scope.Entry e = cs.members().lookup(name);
                if (e.scope != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLocalVar(Name name) {
        for (JCTree t : trees) {
            Kind k = t.getKind();
            if (k == Kind.BLOCK || k == Kind.METHOD) {
                VarScanner vscan = new VarScanner();
                vscan.scan(t);
                for (JCVariableDecl v : vscan.vars) {
                    if (v.name.contentEquals(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
