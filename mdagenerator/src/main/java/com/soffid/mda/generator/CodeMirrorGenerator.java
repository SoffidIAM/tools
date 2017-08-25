package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import com.soffid.mda.parser.AbstractModelAttribute;
import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.ModelAttribute;
import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.ModelElement;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class CodeMirrorGenerator<E> {
	final static String endl = "\n";
	
	private String modelPackage;
	private String modelDir;
	private Generator generator;
	private Parser parser;

	
	final static String endComment = "*/";

	public void generate(Generator generator, Parser parser) throws IOException {
		this.generator = generator;
		this.parser = parser;

		if (generator.getCoreResourcesDir() == null)
			return;


		generateResources();
	}

	private void generateResources() throws FileNotFoundException, UnsupportedEncodingException {
		String file = generator.getCoreResourcesDir();

		File f;
		if (generator.isPlugin())
			f = new File (file + File.separator + "web/js/codemirror/java-classes-"+generator.getPluginName()+".js");
		else
			f = new File (file + File.separator + "web/js/codemirror/java-classes.js");

		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" + "\n"
				+ "// (C) 2017 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				);
		
		int[] bothTranslations = new int[]{Translate.TRANSLATE, Translate.DONT_TRANSLATE};
		int[] defaultTranslation = new int[]{Translate.DEFAULT};
		int[] noDump = new int[0];
		HashSet<String> classNames = new HashSet<String>();
		
		HashMap<String, Set <String> > packages = new HashMap<String, Set<String> > ();
		
		boolean first = true;
		LinkedList<ModelElement> elementList = new LinkedList<ModelElement>( parser.getModelElements());
		elementList.add(new ModelClass(parser, Date.class));
		elementList.add(new ModelClass(parser, Calendar.class));
		for (ModelElement element: elementList)
		{
			if (element instanceof ModelClass)
			{
				ModelClass mc = (ModelClass) element;
				int translations[];
				if (mc.isService() || mc.isValueObject())
					translations  = bothTranslations;
				else if (mc.isEntity())
					translations = noDump;
				else
					translations = defaultTranslation;
				for (int translation: translations)
				{
					String javaClass = mc.getJavaType(translation);
					if (javaClass == null || classNames.contains(javaClass))
						continue;
					classNames.add(javaClass);
					first = false;
					out.println ("CodeMirrorJavaTypes[\""+javaClass+"\"]={");
					HashSet<String> names = new HashSet<String>();
					boolean firstOp = true;
					if (javaClass.startsWith("java.util.List<") || 
							javaClass.startsWith("java.util.Collection<") ||
							javaClass.startsWith("java.util.Set<") )
					{
						String baseClass = mc.getChildClass() == null ? "java.lang.String" :
							mc.getChildClass().getJavaType(translation);
						out.println("\t\"size\": \"int\",");
						out.print("\t\"get\": \""+baseClass+"\"");
					} else  {
						if (javaClass.endsWith("[]"))
						{
							out.print("\t\"length\":\"int\"");
							firstOp = false;
							mc = mc.getChildClass();
						}
						
						registerPackage(packages, mc, translation);
						
						for (ModelOperation op: mc.getOperations())
						{
							if (! names.contains(op.getName(translation)))
							{
								if (!firstOp)
									out.println (",");
								firstOp = false;
								names.add(op.getName());
								out.print("\t\""+op.getName()+"\":\""+op.getReturnType(translation)+"\"");
							}
						}
						for (AbstractModelAttribute att: mc.getAllAttributes())
						{
							if (! names.contains(att.getName(translation)))
							{
								if (!firstOp)
									out.println (",");
								firstOp = false;
								names.add(att.getName());
								out.print("\t\""+att.getName()+"\":\""+att.getJavaType(translation)+"\"");
							}
						}
					}
					out.println("\n};");
				}
			}
		}
		String slName;
		if (generator.isPlugin())
		{
			slName = "com.soffid.iam.addons." + generator.getPluginName()+".ServiceLocator";
			out.println("CodeMirrorJavaTypes[\""+slName+"\"]={");
			for ( ModelClass service: parser.getServices())
			{
				out.println("\t\"get"+service.getName()+"\":\""+service.getFullName()+"\",");
			}
			out.println("\t\"instance\":\"java.lang.Object\",");
			out.println("\t\"getService\":\"java.lang.Object\"");
			out.println("};");
		}
		else
		{
			slName = "es.caib.seycon.ng.ServiceLocator";
			out.println("CodeMirrorJavaTypes[\""+slName+"\"]={");
			for ( ModelClass service: parser.getServices())
			{
				out.println("\t\"get"+service.getName(Translate.DONT_TRANSLATE)+"\":\""+service.getFullName(Translate.DONT_TRANSLATE)+"\",");
			}
			out.println("\t\"instance\":\"java.lang.Object\",");
			out.println("\t\"getService\":\"java.lang.Object\"");
			out.println("};");
			slName = "com.soffid.iam.ServiceLocator";
			out.println("CodeMirrorJavaTypes[\""+slName+"\"]={");
			for ( ModelClass service: parser.getServices())
			{
				out.println("\t\"get"+service.getName(Translate.TRANSLATE)+"\":\""+service.getFullName(Translate.TRANSLATE)+"\",");
			}
			out.println("\t\"instance\":\"java.lang.Object\",");
			out.println("\t\"getService\":\"java.lang.Object\"");
			out.println("};");
		}
		
		
		for (String pkg : packages.keySet())
		{
			out.println("CodeMirrorJavaPackages[\""+pkg+"\"]=[");
			first = true;
			for ( String className: packages.get(pkg) )
			{
				if (!first) out.print(",");
				first = false;
				out.print("\n\t\""+className+"\"");
			}
			out.println ("];"); 
		}
	}

	private void registerPackage(HashMap<String, Set<String>> packages,
			ModelClass mc, int translation) {
		String split[] = mc.getJavaType(translation).split("\\.");
		
		String pkgName = "";
		for (int i = 0;  i < split.length; i++)
		{
			Set<String> list = packages.get(pkgName);
			if (list == null)
			{
				list = new HashSet<String>();
				packages.put(pkgName, list);
			}
			String className = i == 0 ? split[i] : pkgName + "." + split[i];
			list.add(split[i]);
			pkgName = className;
		}
	}
}
