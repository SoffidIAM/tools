package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

		File f = new File (file + File.separator + "codemirror/classes.js");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" + "\n"
				+ "// (C) 2013 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				);
		
		int[] bothTranslations = new int[]{Translate.TRANSLATE, Translate.DONT_TRANSLATE};
		int[] defaultTranslation = new int[]{Translate.DEFAULT};
		int[] noDump = new int[0];
		HashSet<String> classNames = new HashSet<String>();
		
		boolean first = true;
		for (ModelElement element: new LinkedList<ModelElement>( parser.getModelElements()))
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
					if (classNames.contains(javaClass))
						continue;
					classNames.add(javaClass);
					first = false;
					out.println ("'"+javaClass+"': {");
					HashSet<String> names = new HashSet<String>();
					boolean firstOp = true;
					if (javaClass.startsWith("java.util.List<") || 
							javaClass.startsWith("java.util.Collection<") ||
							javaClass.startsWith("java.util.Set<") )
					{
						String baseClass = mc.getChildClass().getJavaType(translation);
						out.println("\t'size': 'int',");
						out.print("\t'get': '"+baseClass+"'");
					} else  {
						if (javaClass.endsWith("[]"))
						{
							out.print("\t'length':'int'");
							firstOp = false;
							mc = mc.getChildClass();
						}
						for (ModelOperation op: mc.getOperations())
						{
							if (! names.contains(op.getName(translation)))
							{
								if (!firstOp)
									out.println (",");
								firstOp = false;
								names.add(op.getName());
								out.print("\t'"+op.getName()+"':'"+op.getReturnType(translation)+"'");
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
								out.print("\t'"+att.getName()+"':'"+att.getJavaType(translation)+"'");
							}
						}
					}
					out.println("\n},");
				}
			}
		}
		out.println("");
		
	}
}
