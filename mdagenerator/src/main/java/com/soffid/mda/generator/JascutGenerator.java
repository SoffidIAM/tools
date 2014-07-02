package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.soffid.mda.parser.AbstractModelAttribute;
import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.ModelElement;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class JascutGenerator {

	private Generator generator;
	private Parser parser;
	private Package pkg;
	private String prefix;
	private int idGenerator;
	
	final static String endl = "\n";

	public void generate(Generator generator, Parser parser) throws FileNotFoundException, UnsupportedEncodingException {
		this.generator = generator;
		this.parser = parser;
		generateModel();
	}

	private void generateModel() throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File(generator.getJascutDir()+"/config.xml");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		 
		out.println ("<?xml version = '1.0' encoding = 'UTF-8' ?>" + endl
					+"<refactorings xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
					+"xmlns='jascut'>");
		out.println ("\t<description>Soffid generated translator</description>");

		for (AbstractModelClass cl: parser.getServices())
		{
			translateMethods (cl, out);
		}
		out.println();
		for (AbstractModelClass cl: parser.getEntities())
		{
			translateMethods (cl, out);
		}
		out.println();
		for (AbstractModelClass cl: parser.getValueObjects())
		{
			translateMethods (cl, out);
		}
		out.println();
		
		for (ModelElement el: parser.getModelElements())
		{
			if (el instanceof AbstractModelClass)
			{
				AbstractModelClass cl = (AbstractModelClass) el;
				translateClass (cl, out);
			}
		}

		out.println ("</refactorings>");
		
	}

	private void translateClass(AbstractModelClass cl, PrintStream out) {
		String name = cl.getFullName(false);
		String transName = cl.getFullName(true);
		if (!name.equals(transName))
		{
			out.println ("\t<rename-type><type-orig>"+cl.getFullName(false)+"</type-orig><type-new>"+cl.getFullName(true)+"</type-new></rename-type>");
			if (cl.isEntity())
			{
				out.println ("\t<rename-type><type-orig>"+cl.getDaoFullName(false)+"</type-orig><type-new>"+cl.getDaoFullName(true)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getPackage(false)+"."+cl.getDaoBaseName(false)+"</type-orig><type-new>"+cl.getPackage(true)+"."+cl.getDaoBaseName(true)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getDaoImplFullName(false)+"</type-orig><type-new>"+cl.getDaoImplFullName(true)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getImplFullName(false)+"</type-orig><type-new>"+cl.getImplFullName(true)+"</type-new></rename-type>");
			}
			if (cl.isService())
			{
				out.println ("\t<rename-type><type-orig>"+cl.getBaseFullName(false)+"</type-orig><type-new>"+cl.getBaseFullName(true)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getImplFullName(false)+"</type-orig><type-new>"+cl.getImplFullName(true)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getEjbHomeFullName(false)+"</type-orig><type-new>"+cl.getEjbHomeFullName(true)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getEjbInterfaceFullName(false)+"</type-orig><type-new>"+cl.getEjbInterfaceFullName(true)+"</type-new></rename-type>");
			}
		}
		
	}

	private void translateMethods(AbstractModelClass cl, PrintStream out) {
		// Translates getters & setters
		for (AbstractModelAttribute att: cl.getAttributes())
		{
			String name = att.getName(false);
			String transName = att.getName(true);
			if (!name.equals(transName))
			{
				out.println("\t<rename-method>");
				out.println("\t\t<type>"+cl.getFullName(false)+"</type>");
				out.println("\t\t<method-orig>"+att.getterName(false)+"</method-orig>");
				out.println("\t\t<args-orig><![CDATA[]]></args-orig>");
				out.println("\t\t<method-new>"+att.getterName(true)+"</method-new>");
				out.println("\t</rename-method>");

				out.println("\t<rename-method>");
				out.println("\t\t<type>"+cl.getFullName(false)+"</type>");
				out.println("\t\t<method-orig>"+att.setterName(false)+"</method-orig>");
				out.println("\t\t<args-orig><![CDATA["+att.getDataType().getJavaType(false)+"]]></args-orig>");
				out.println("\t\t<method-new>"+att.setterName(true)+"</method-new>");
				out.println("\t</rename-method>");
			}
		}
		out.println();
		// Translates getters & setters
		for (ModelOperation op: cl.getOperations())
		{
			String spec1 = op.getName(false);
			String spec2 = op.getName(true);
			if (!spec1.equals(spec2))
			{
				String className = cl.getFullName(false);
				if (cl.isEntity() && (op.isQuery() || op.isStatic()))
					className = cl.getDaoFullName(false);
				out.println("\t<rename-method>");
				out.println("\t\t<type>"+className+"</type>");
				out.println("\t\t<method-orig>"+op.getName(false)+"</method-orig>");
				out.println("\t\t<args-orig><![CDATA["+op.getArguments(false)+"]]></args-orig>");
				out.println("\t\t<method-new>"+op.getName(true)+"</method-new>");
				out.println("\t</rename-method>");
				if (cl.isEntity() && (op.isQuery() || op.isStatic()) || cl.isService())
				{
					className = cl.getBaseFullName(false);
					out.println("\t<rename-method>");
					out.println("\t\t<type>"+className+"</type>");
					out.println("\t\t<method-orig>handle"+Util.firstUpper(op.getName(false))+"</method-orig>");
					out.println("\t\t<args-orig><![CDATA["+op.getArguments(false)+"]]></args-orig>");
					out.println("\t\t<method-new>handle"+Util.firstUpper(op.getName(true))+"</method-new>");
					out.println("\t</rename-method>");
				}
			}
		}
	}

}
