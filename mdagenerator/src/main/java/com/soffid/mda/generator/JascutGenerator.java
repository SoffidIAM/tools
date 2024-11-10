package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.soffid.mda.parser.AbstractModelAttribute;
import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.ModelElement;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class JascutGenerator {

	private Generator generator;
	private Parser parser;
	
	final static String endl = "\n";

	public void generate(Generator generator, Parser parser) throws FileNotFoundException, UnsupportedEncodingException {
		this.generator = generator;
		this.parser = parser;
		generateModel();
	}

	private void generateModel(String fileName, List<ModelClass> list) throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File(generator.getJascutDir()+"/"+fileName+"-config.xml");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating "+f.getPath());
		 
		out.println ("<?xml version = '1.0' encoding = 'UTF-8' ?>" + endl
					+"<refactorings xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
					+"xmlns='jascut'>");
		out.println ("\t<description>Soffid generated translator</description>");

		for (AbstractModelClass cl: list)
		{
			translateMethods (fileName, cl, out);
		}

		for (AbstractModelClass cl: list)
		{
			translateClass (cl, out);
		}


		if (fileName.equals ("entity"))
		{
			LinkedList<ModelClass> svc = new LinkedList<ModelClass>(parser.getServices());
			svc.addAll(parser.getEntities());
			// Translate entity dependencies
			for (ModelClass service: svc)
			{
				for (AbstractModelClass dep: service.getDepends())
				{
					if (dep.isEntity() && ! service.isServerOnly())
					{
						String spec1 = dep.isEntity() ? dep.getDaoName(Translate.DONT_TRANSLATE): dep.getName(Translate.DONT_TRANSLATE);
						String spec2 = dep.isEntity() ? dep.getDaoName(Translate.TRANSLATE): dep.getName(Translate.TRANSLATE);
						if (!spec1.equals(spec2))
						{
							String className = service.getBaseFullName(Translate.DONT_TRANSLATE);
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>get"+spec1+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA[]]></args-orig>");
							out.println("\t\t<method-new>get"+spec2+"</method-new>");
							out.println("\t</rename-method>");
						}
					}
				}
			}

			String modelPackage1 = generator.getModelPackage(Translate.DONT_TRANSLATE); 
			String modelPackage2 = generator.getModelPackage(Translate.TRANSLATE);
			
			out.println ("\t<rename-type><type-orig>"+modelPackage1+".criteria.CriteriaSearchConfiguration</type-orig>"
					+ "<type-new>"+modelPackage2+".criteria.CriteriaSearchConfiguration</type-new></rename-type>");
			out.println ("\t<rename-type><type-orig>"+modelPackage1+".criteria.CriteriaSearch</type-orig>"
					+ "<type-new>"+modelPackage2+".criteria.CriteriaSearch</type-new></rename-type>");
			out.println ("\t<rename-type><type-orig>"+modelPackage1+".criteria.CriteriaSearchParameter</type-orig>"
					+ "<type-new>"+modelPackage2+".criteria.CriteriaSearchParameter</type-new></rename-type>");
			
		} 
		else if (fileName.equals ("vo"))
		{
			for (AbstractModelClass vo: list)
			{
				for (AbstractModelClass dep: vo.getProvides())
				{
					if (dep.isEntity())
					{
						// Translate to value object
						String spec1 = "to"+vo.getName(Translate.DONT_TRANSLATE);
						String spec2 = "to"+vo.getName(Translate.TRANSLATE);
						if (!spec1.equals(spec2))
						{
							out.println ("<!-- Renaming entity "+dep.getDaoFullName(Translate.DONT_TRANSLATE)+" -->");
							String className = dep.getDaoFullName(Translate.PREVIOUS_SCOPE);
							// toVO(entity)
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+spec1+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA["+dep.getFullName(Translate.PREVIOUS_SCOPE)+"]]></args-orig>");
							out.println("\t\t<method-new>"+spec2+"</method-new>");
							out.println("\t</rename-method>");
							// toVO (entity, vo)
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+spec1+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA["+dep.getFullName(Translate.PREVIOUS_SCOPE)+","+vo.getFullName(Translate.DONT_TRANSLATE)+"]]></args-orig>");
							out.println("\t\t<method-new>"+spec2+"</method-new>");
							out.println("\t</rename-method>");
							// toVOList (list)
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+spec1+"List</method-orig>");
							out.println("\t\t<args-orig><![CDATA[java.util.Collection<"+dep.getFullName(Translate.PREVIOUS_SCOPE)+">]]></args-orig>");
							out.println("\t\t<method-new>"+spec2+"List</method-new>");
							out.println("\t</rename-method>");
						}
						// Translate to entity
						spec1 = Util.firstLower(vo.getName(Translate.DONT_TRANSLATE))+"ToEntity";
						spec2 = Util.firstLower(vo.getName(Translate.TRANSLATE))+"ToEntity";
						if (!spec1.equals(spec2))
						{
							String className = dep.getDaoFullName(Translate.PREVIOUS_SCOPE);
							// voToEntity (vo)
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+spec1+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA["+vo.getFullName(Translate.DONT_TRANSLATE)+"]]></args-orig>");
							out.println("\t\t<method-new>"+spec2+"</method-new>");
							out.println("\t</rename-method>");
							// voToEntity(vo, entity, copyIfNull)
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+spec1+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA["+vo.getFullName(Translate.DONT_TRANSLATE)+","+dep.getFullName(Translate.PREVIOUS_SCOPE)+",boolean]]></args-orig>");
							out.println("\t\t<method-new>"+spec2+"</method-new>");
							out.println("\t</rename-method>");
							// voToEntityList
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+spec1+"List</method-orig>");
							out.println("\t\t<args-orig><![CDATA[java.util.Collection<"+vo.getFullName(Translate.PREVIOUS_SCOPE)+">]]></args-orig>");
							out.println("\t\t<method-new>"+spec2+"List</method-new>");
							out.println("\t</rename-method>");
						}
					}
				}
			}
		}
		

		out.println();
		out.println ("</refactorings>");
		out.close ();
	}


	private void generateModelSyncServer(String fileName, List<ModelClass> list) throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File(generator.getJascutDir()+"/"+fileName+"-syncserver-config.xml");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating "+f.getPath());
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		 
		out.println ("<?xml version = '1.0' encoding = 'UTF-8' ?>" + endl
					+"<refactorings xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
					+"xmlns='jascut'>");
		out.println ("\t<description>Soffid generated translator</description>");

		if (fileName.equals ("entity"))
		{
			// Translate entity dependencies
			for (ModelClass service: parser.getServices())
			{
				for (AbstractModelClass dep: service.getDepends())
				{
					if (dep.isEntity() && service.isServerOnly())
					{
						String spec1 = dep.isEntity() ? dep.getDaoName(Translate.DONT_TRANSLATE): dep.getName(Translate.DONT_TRANSLATE);
						String spec2 = dep.isEntity() ? dep.getDaoName(Translate.TRANSLATE): dep.getName(Translate.TRANSLATE);
						if (!spec1.equals(spec2))
						{
							String className = service.getBaseFullName(Translate.DONT_TRANSLATE);
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>get"+spec1+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA[]]></args-orig>");
							out.println("\t\t<method-new>get"+spec2+"</method-new>");
							out.println("\t</rename-method>");
						}
					}
				}
			}
		}
		out.println();
		out.println ("</refactorings>");
		out.close ();
	}

	private void generateModel() throws FileNotFoundException, UnsupportedEncodingException {
		generateModel ("service", parser.getServices());
		generateModel ("entity", parser.getEntities());
		generateModelSyncServer ("entity", parser.getEntities());
		generateModel ("vo", parser.getValueObjects());
	}

	private void translateClass(AbstractModelClass cl, SmartPrintStream out) {
		String name = cl.getFullName(Translate.DONT_TRANSLATE);
		String transName = cl.getFullName(Translate.TRANSLATE);
		if (!name.equals(transName))
		{
			out.println ("\t<rename-type><type-orig>"+cl.getFullName(Translate.DONT_TRANSLATE)+"</type-orig>"
					+ "<type-new>"+cl.getFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
			if (cl.isEntity())
			{
				out.println("\t<rename-method>");
				out.println("\t\t<type>"+cl.getDaoFullName(Translate.DONT_TRANSLATE)+"</type>");
				out.println("\t\t<method-orig>new"+cl.getName(Translate.DONT_TRANSLATE)+"</method-orig>");
				out.println("\t\t<args-orig><![CDATA[]]></args-orig>");
				out.println("\t\t<method-new>new"+cl.getName(Translate.TRANSLATE)+"</method-new>");
				out.println("\t</rename-method>");
				out.println ("\t<rename-type><type-orig>"+cl.getDaoFullName(Translate.DONT_TRANSLATE)+"</type-orig>"
						+ "<type-new>"+cl.getDaoFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getPackage(Translate.DONT_TRANSLATE)+"."+cl.getDaoBaseName(Translate.DONT_TRANSLATE)+"</type-orig>"
						+ "<type-new>"+cl.getPackage(Translate.TRANSLATE)+"."+cl.getDaoBaseName(Translate.TRANSLATE)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getDaoImplFullName(Translate.DONT_TRANSLATE)+"</type-orig>"
						+ "<type-new>"+cl.getDaoImplFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getImplFullName(Translate.DONT_TRANSLATE)+"</type-orig>"
						+ "<type-new>"+cl.getImplFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
			}
			if (cl.isService())
			{
				out.println ("\t<rename-type><type-orig>"+cl.getBaseFullName(Translate.DONT_TRANSLATE)+"</type-orig>4"
						+ "<type-new>"+cl.getBaseFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getImplFullName(Translate.DONT_TRANSLATE)+"</type-orig>"
						+ "<type-new>"+cl.getImplFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getEjbHomeFullName(Translate.DONT_TRANSLATE)+"</type-orig>"
						+ "<type-new>"+cl.getEjbHomeFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
				out.println ("\t<rename-type><type-orig>"+cl.getEjbInterfaceFullName(Translate.DONT_TRANSLATE)+"</type-orig>"
						+ "<type-new>"+cl.getEjbInterfaceFullName(Translate.TRANSLATE)+"</type-new></rename-type>");
			}
		}
		
	}

	private void translateMethods(String fileName, AbstractModelClass cl, SmartPrintStream out) {
		if (cl.isService() && cl.isServerOnly())
			return;
		// Translates getters & setters
		{
			for (AbstractModelAttribute att: cl.getAttributes())
			{
				String name = att.getName(Translate.DONT_TRANSLATE);
				String transName = att.getName(Translate.TRANSLATE);
				if (!name.equals(transName))
				{
					out.println("\t<rename-method>");
					out.println("\t\t<type>"+cl.getFullName(Translate.DONT_TRANSLATE)+"</type>");
					out.println("\t\t<method-orig>"+att.getterName(Translate.DONT_TRANSLATE)+"</method-orig>");
					out.println("\t\t<args-orig><![CDATA[]]></args-orig>");
					out.println("\t\t<method-new>"+att.getterName(Translate.TRANSLATE)+"</method-new>");
					out.println("\t</rename-method>");
	
					out.println("\t<rename-method>");
					out.println("\t\t<type>"+cl.getFullName(Translate.DONT_TRANSLATE)+"</type>");
					out.println("\t\t<method-orig>"+att.setterName(Translate.DONT_TRANSLATE)+"</method-orig>");
					out.println("\t\t<args-orig><![CDATA["+att.getDataType().getJavaType(Translate.DONT_TRANSLATE)+"]]></args-orig>");
					out.println("\t\t<method-new>"+att.setterName(Translate.TRANSLATE)+"</method-new>");
					out.println("\t</rename-method>");
				}
			}
			out.println();
			// Translates getters & setters
			for (ModelOperation op: cl.getOperations())
			{
				String spec1 = op.getName(Translate.DONT_TRANSLATE);
				String spec2 = op.getName(Translate.TRANSLATE);
				if (!spec1.equals(spec2))
				{
					String className = cl.getFullName(Translate.DONT_TRANSLATE);
					if (cl.isEntity() && (op.isQuery() || op.isStatic()))
						className = cl.getDaoFullName(Translate.DONT_TRANSLATE);
					int scope = cl.isService() ? Translate.SERVICE_SCOPE: Translate.DONT_TRANSLATE; 
					out.println("\t<rename-method>");
					out.println("\t\t<type>"+className+"</type>");
					out.println("\t\t<method-orig>"+op.getName(Translate.DONT_TRANSLATE)+"</method-orig>");
					out.println("\t\t<args-orig><![CDATA["+op.getArguments(Translate.PREVIOUS_SCOPE)+"]]></args-orig>");
					out.println("\t\t<method-new>"+op.getName(Translate.TRANSLATE)+"</method-new>");
					out.println("\t</rename-method>");
					if (cl.isEntity() && ! op.isQuery() && op.isStatic() || cl.isService())
					{
						className = cl.getBaseFullName(Translate.DONT_TRANSLATE);
						out.println("\t<rename-method>");
						out.println("\t\t<type>"+className+"</type>");
						out.println("\t\t<method-orig>handle"+Util.firstUpper(op.getName(Translate.DONT_TRANSLATE))+"</method-orig>");
						out.println("\t\t<args-orig><![CDATA["+op.getArguments(Translate.PREVIOUS_SCOPE)+"]]></args-orig>");
						out.println("\t\t<method-new>handle"+Util.firstUpper(op.getName(Translate.TRANSLATE))+"</method-new>");
						out.println("\t</rename-method>");
					}
					if (cl.isEntity() && op.isQuery())
					{
						boolean criteria = false;
						if (op.getParameters().size() == 1)
						{
							ModelParameter param = op.getParameters().get(0);
							if (param.getDataType().isCriteria())
							{
								criteria = true;
							}
						}
						if (!criteria)
						{
							String param = generator.getModelPackage(Translate.DONT_TRANSLATE)+".criteria.CriteriaSearchConfiguration";
							if (op.getParameters().size() > 0)
							{
								param = param + "," + op.getArguments(Translate.DONT_TRANSLATE);
							}
							className = cl.getDaoFullName(Translate.DONT_TRANSLATE);
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+op.getName(Translate.DONT_TRANSLATE)+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA["+
									param+"]]></args-orig>");
							out.println("\t\t<method-new>"+op.getName(Translate.TRANSLATE)+"</method-new>");
							out.println("\t</rename-method>");
							// Now method with included query string (on base class)
							param = "java.lang.String,"+generator.getModelPackage(Translate.DONT_TRANSLATE)+".criteria.CriteriaSearchConfiguration";
							if (op.getParameters().size() > 0)
							{
								param = param + "," + op.getArguments(scope);
							}
							className = cl.getPackagePrefix(Translate.DONT_TRANSLATE)+cl.getDaoBaseName(Translate.DONT_TRANSLATE);
							out.println("\t<rename-method>");
							out.println("\t\t<type>"+className+"</type>");
							out.println("\t\t<method-orig>"+op.getName(Translate.DONT_TRANSLATE)+"</method-orig>");
							out.println("\t\t<args-orig><![CDATA["+
									param+"]]></args-orig>");
							out.println("\t\t<method-new>"+op.getName(Translate.TRANSLATE)+"</method-new>");
							out.println("\t</rename-method>");
						}
					}
				}
			}

		}
		// Translates dependencies

		for (AbstractModelClass dep: cl.getDepends())
		{
			if (dep.isEntity()  ?  fileName.equals("entity") && generator.isTranslateEntities() && ! generator.isTranslated() : 
				dep.isService() ? fileName.equals ("service") :
				false)
			{
				String spec1 = dep.isEntity() ? dep.getDaoName(Translate.DONT_TRANSLATE): dep.getName(Translate.DONT_TRANSLATE);
				String spec2 = dep.isEntity() ? dep.getDaoName(Translate.TRANSLATE): dep.getName(Translate.TRANSLATE);
				if (!spec1.equals(spec2))
				{
					
					String className = cl.getBaseFullName(Translate.DONT_TRANSLATE) ;
					out.println("\t<rename-method>");
					out.println("\t\t<type>"+className+"</type>");
					out.println("\t\t<method-orig>get"+spec1+"</method-orig>");
					out.println("\t\t<args-orig><![CDATA[]]></args-orig>");
					out.println("\t\t<method-new>get"+spec2+"</method-new>");
					out.println("\t</rename-method>");
				}
			}
		}
	}

}
