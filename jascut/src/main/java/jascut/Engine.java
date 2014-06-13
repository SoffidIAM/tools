/*
 * Copyright(c) Zdenek Tronicek, FIT CTU in Prague. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (CDDL). You can obtain a copy of the CDDL at
 * http://www.netbeans.org/cddl.html.
 *
 */
package jascut;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import jascut.xml.RuleList;
import jascut.xml.Rule;
import refactoring.UpdateScanner;
import reporting.Report;

/**
 * The refactoring engine.
 *
 * @author tronicek
 */
public class Engine {

    private Properties conf;
    //private List<CompilationUnit> units;
    private List<JCCompilationUnit> units;

    public Engine(Properties conf) {
        this.conf = conf;
    }

    private void prepareCompiler(Context ctx) {
        //Options.instance(ctx).put(OptionName.ENCODING, "utf-8");
        DiagnosticListener<JavaFileObject> diagnosticListener = new DiagnosticListener<JavaFileObject>() {
            @Override
            public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
                System.out.println(diagnostic);
            }
        };
        ctx.put(DiagnosticListener.class, diagnosticListener);
        JavaCompiler compiler = JavaCompiler.instance(ctx);
        compiler.attrParseOnly = true;
        //compiler.keepComments = true;
        //compiler.genEndPos = true;
        //compiler.verbose = true;
    }

//    private List<String> listFiles(String path) {
//        List<String> p = new ArrayList<>();
//        File dir = new File(path);
//        FileFilter filter = new FileFilter() {
//
//            @Override
//            public boolean accept(File f) {
//                if (f.isDirectory()) {
//                    return true;
//                }
//                if (f.getName().endsWith(".java")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File f : dir.listFiles(filter)) {
//            if (f.isFile()) {
//                p.add(f.getPath());
//            } else if (f.isDirectory()) {
//                List<String> pp = listFiles(f.getPath());
//                p.addAll(pp);
//            }
//        }
//        return p;
//    }
    
    private void listFiles (File dir, final String ext, final boolean recursively, List<String> p) throws IOException {
		for (File f: dir.listFiles())
		{
			if (f.isDirectory())
			{
				if (recursively)
					listFiles (f, ext, recursively, p);
			}
			else if (f.getName().endsWith(ext))
				p.add (f.getPath());
		}
	
    }
    
    private List<String> listFiles(File dir, final String ext, final boolean recursively) throws IOException {
        final List<String> p = new ArrayList<String>();
        listFiles (dir, ext, recursively, p);
        return p;
    }

    private String buildClasspath(String cp) throws IOException {
        List<String> jars = new LinkedList<String>();
        for (String s : cp.split("[;:]")) {
        	File p = new File (s);
            if (p.isDirectory()) {
                List<String> ff = listFiles(p, ".jar", false);
                jars.addAll(ff);
            } else {
                jars.add(s);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : jars) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparatorChar);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private Context prepare() throws Exception {
        Context ctx = new Context();
        prepareCompiler(ctx);
        // gather source files
        List<String> files = new LinkedList<String>();
        String sp = conf.getProperty("sourceDir");
        for (String s : sp.split("[;:]")) {
        	listFiles (new File(s), ".java", true, files);
        }
        JavacFileManager fileManager = (JavacFileManager) ctx.get(JavaFileManager.class);
        List<JavaFileObject> fileObjects = new ArrayList<JavaFileObject>();
        for (String f : files) {
            fileObjects.add(fileManager.getFileForInput(f));
        }
        JavacTool tool = JavacTool.create();
        String cp = buildClasspath(conf.getProperty("classpath", "."));
//        System.out.println("classpath: " + cp);
        List<String> options = Arrays.asList("-cp", cp);
        JavacTaskImpl javacTaskImpl = (JavacTaskImpl) tool.getTask(null, fileManager, null, options, null, fileObjects);
        javacTaskImpl.updateContext(ctx);
        // get compilation units
        //List<JCCompilationUnit> cus = new ArrayList<>();
        units = new ArrayList<JCCompilationUnit>();
        for (CompilationUnitTree t : javacTaskImpl.parse()) {
            //cus.add((JCCompilationUnit) t);
            units.add((JCCompilationUnit) t);
        }
        javacTaskImpl.analyze();
        // make a backup of compilation units
//        units = new ArrayList<>();
//        for (JCCompilationUnit cu : cus) {
//            CopyScanner scan = new CopyScanner(TreeMaker.instance(ctx));
//            cu.accept(scan);
//            CompilationUnit p = scan.backup();
//            units.add(p);
//        }
        return ctx;
    }

//    private void performRule(Rule rule, Context ctx) throws Exception {
//        Class<?> cl = Class.forName(rule.getRefactoringClass());
//        Constructor<?> cons = cl.getConstructor(Rule.class);
//        UpdateScanner scanner = (UpdateScanner) cons.newInstance(rule);
//        scanner.prepare(ctx);
//        for (CompilationUnit cu : units) {
//            scanner.process(cu);
//        }
//    }
//
    private void performRules(List<Rule> rules, Context ctx, JCCompilationUnit cu) throws Exception {
        CopyScanner scan = new CopyScanner(TreeMaker.instance(ctx));
        cu.accept(scan);
        EndPosScanner escan = new EndPosScanner(scan.getPeers(), scan.getEndPositions());
        cu.accept(escan);
        CompilationUnit p = scan.backup();
        for (Rule r : rules) {
            Class<?> cl = Class.forName(r.getRefactoringClass());
            Constructor<?> cons = cl.getConstructor(Rule.class);
            UpdateScanner scanner = (UpdateScanner) cons.newInstance(r);
            scanner.prepare(ctx);
            scanner.process(p);
        }
        writeSource(p);
    }

    private void writeSource(CompilationUnit cu) throws IOException {
        String sourceDir = conf.getProperty("sourceDir");
        String outputDir = conf.getProperty("outputDir");
        String path = cu.getFileName();
        String file = path;
        for (String s : sourceDir.split("[;:]")) {
        	if (path.startsWith(s))
        	{
        		String tail = path.substring(s.length());
        		file = outputDir;
        		if (! tail.startsWith(File.separator) &&  ! outputDir.endsWith(File.separator))
        			file = file + File.separator;
        		file = file + tail;
        	}
        }
        System.out.println ("Generating file "+file);
        int i = file.lastIndexOf(File.separator);
        File dir = new File(file.substring(0, i));
        dir.mkdirs();
       	PrintWriter out = new PrintWriter(file); 
        cu.write(out);
        out.close();
        if (cu.hasWarnings()) {
            String warnFile = file.substring(0, file.length() - 4) + "txt";
            PrintWriter out2 = new PrintWriter(warnFile);
            for (String s : cu.getWarnings()) {
                out2.println(s);
            }
        }
        Report.warnings(path, cu.getWarnings().size());
    }

    public void perform(RuleList rlist) throws Exception {
        Context ctx = prepare();
        String srcDir = conf.getProperty("sourceDir").split("[;:]")[0];
        
        for (JCCompilationUnit cu : units) {
        	if (cu.getSourceFile().getName().startsWith(srcDir))
                performRules(rlist.getRules(), ctx, cu);
        }
    }
}
