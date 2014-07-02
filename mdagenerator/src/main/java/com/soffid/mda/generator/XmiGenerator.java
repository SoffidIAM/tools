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
import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.ModelElement;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class XmiGenerator {

	private Generator generator;
	private Parser parser;
	private Package pkg;
	private String prefix;
	private int idGenerator;
	
	final static String endl = "\n";

	public void generate(Generator generator, Parser parser) throws FileNotFoundException, UnsupportedEncodingException {
		this.generator = generator;
		this.parser = parser;
		this.pkg = new Package ();
		preparePackages ();
		idGenerator = 1;
		generateModel();
	}

	private void generateModel() throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File(generator.getXmlModule() + "/" +
				( generator.isPlugin() ? generator.getPluginName() : "core" ) +
				".xmi");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());
		prefix = generator.isPlugin() ? generator.getPluginName() : "core";
		
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		 
		out.println ("<?xml version = '1.0' encoding = 'UTF-8' ?>" + endl
					+"<XMI xmi.version = '1.2' xmlns:UML = 'org.omg.xmi.namespace.UML' timestamp = '" + df.format(new Date())
						+"'>" +endl 
                    + "<XMI.header>" +endl 
                    + "<XMI.documentation>" +endl 
                    + "<XMI.exporter>Soffid MDA Generator</XMI.exporter>" +endl 
                    + "<XMI.exporterVersion>Unknown</XMI.exporterVersion>" +endl 
                    + "</XMI.documentation>" +endl 
                    + "<XMI.metamodel xmi.name='UML' xmi.version='1.4'/></XMI.header>" +endl 
                    + "<XMI.content>" +endl 
                    + "<UML:Model xmi.id = '"+prefix+"-root'" +endl 
                    + "name = '" + (generator.isPlugin() ? generator.getPluginName() + " Addon": "Soffid model") +"'" +endl 
                    + " isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false'>"
                    + "<UML:Namespace.ownedElement>");
		
		generateStereotype (out, "ValueObject");
		generateStereotype (out, "Service");
		generateStereotype (out, "Entity");
		generateStereotype (out, "Enumeration");
		
		generateSubPackages (out, pkg, prefix);

		out.println ("</UML:Namespace.ownedElement>" + endl 
					+"</UML:Model>" + endl 
					+"</XMI.content>" + endl
					+"</XMI>");
		
		
	}

	private void generateSubPackages(PrintStream out, Package masterPkg, String prefix) {
		for (Package pkg: masterPkg.packages)
		{
			String name = prefix+"."+pkg.name;
			out.println ("<UML:Package xmi.id='pkg."+name+"' name='"+pkg.name+"' " +
					"isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' >" );
			out.println ("<UML:Namespace.ownedElement>");
			generateSubPackages (out, pkg, name);
			generateClasses (out, pkg);
			out.println ("</UML:Namespace.ownedElement>");
			out.println ("</UML:Package>");
		}
	}

	private void generateClasses(PrintStream out, Package masterPkg) {
		for (AbstractModelClass element: masterPkg.classes)
		{
			if (element.isEntity())
			{
				generateEntity(out, element);
			} 
			else if (element.isService())
			{
				generateService(out, element);
			} 
			else if (element.isValueObject())
			{
				generateValueObject(out, element);
			} 
			else if (element.isEnumeration())
			{
				generateEnumeration(out, element );
			}
			else if (element.isRole())
			{
				generateActor(out, element);
			} else {
				generateGenericClass(out, element);
			}
		}
	}

	private void generateGenericClass(PrintStream out, AbstractModelClass element) {
		out.println ("<UML:Class xmi.id='"+element.getXmlId()+"' name='"+element.getName()+"' " +
				"visibility='public' isActive='false' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' >" );
		out.println ("</UML:Class>");
	}

	private void generateActor(PrintStream out, AbstractModelClass element) {
		StringBuffer associations = new StringBuffer();
		
		out.println ("<UML:Actor xmi.id='"+element.getXmlId()+"' name='"+element.getName()+"' " +
				"visibility='public' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' >" );

		LinkedList<String> ids = new LinkedList<String>();
		for (AbstractModelClass service: parser.getServices())
		{
			int i = 0;
			for (ModelOperation op: service.getOperations())
			{
				 
				for (AbstractModelClass user: service.getAllActors())
				{
					if (user == element)
						ids.add("method."+service.getFullName()+"."+op.getName()+"_"+i);
				}
				i ++ ;
			}
//			for (ModelClass user: service.getAllActors())
//			{
//				if (user == element)
//					ids.add(service.getXmlId());
//			}
		}
		
		int i = 0;
		String newId = newId();
		for (String dep :ids)
		{
			out.println ("<UML:ModelElement.clientDependency><UML:Dependency xmi.idref='"+newId+"_"+i+"' /></UML:ModelElement.clientDependency>");
			i++;
		}
		out.println ("<UML:Namespace.ownedElement>");
		for (String dep: ids)
		{
			out.println ("<UML:Dependency xml.id='"+newId+"_"+i+"' isSpecification = 'false' >");
			out.println ("<UML:Dependency.client><UML:Class xmi.idref='"+element.getXmlId()+"' /></UML:Dependency.client>");
			out.println ("<UML:Dependency.supplier><UML:Class xmi.idref='"+dep+"' /></UML:Dependency.supplier>");
			out.println ("</UML:Dependency>");
		}
		out.println ("</UML:Namespace.ownedElement>");

		out.println ("</UML:Actor>");
	}

	private void generateEntity(PrintStream out, AbstractModelClass element) {
		StringBuffer associations = new StringBuffer();
		
		out.println ("<UML:Class xmi.id='"+element.getXmlId()+"' name='"+element.getName()+"' " +
				"visibility='public' isActive='false' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' >" );
		out.println ("<UML:ModelElement.stereotype>");
		out.println ("<UML:Stereotype xmi.idref='Stereotype.Entity'/>");
		out.println ("</UML:ModelElement.stereotype>");
		out.println ("<UML:Classifier.feature>");
		generateAttritbutes (out, element);
		generateOperations(out, element);
		generateAssociations (out, element, associations);
		out.println ("</UML:Classifier.feature>");
		generateDependenciess(out, element);
		out.println ("</UML:Class>");
		out.println (associations);
	}

	private void generateEnumeration(PrintStream out, AbstractModelClass element) {
		StringBuffer associations = new StringBuffer();
		
		out.println ("<UML:Class xmi.id='"+element.getXmlId()+"' name='"+element.getName()+"' " +
				"visibility='public' isActive='false' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' >" );
		out.println ("<UML:ModelElement.stereotype>");
		out.println ("<UML:Stereotype xmi.idref='Stereotype.Enumeration'/>");
		out.println ("</UML:ModelElement.stereotype>");
		out.println ("<UML:Classifier.feature>");
		generateAttritbutes (out, element);
		generateOperations(out, element);
		generateAssociations (out, element, associations);
		out.println ("</UML:Classifier.feature>");
		generateDependenciess(out, element);
		out.println ("</UML:Class>");
		out.println (associations);
	}

	private void generateValueObject(PrintStream out, AbstractModelClass element) {
		StringBuffer associations = new StringBuffer();
		
		out.println ("<UML:Class xmi.id='"+element.getXmlId()+"' name='"+element.getName()+"' " +
				"visibility='public' isActive='false' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' >" );
		out.println ("<UML:ModelElement.stereotype>");
		out.println ("<UML:Stereotype xmi.idref='Stereotype.ValueObjecty'/>");
		out.println ("</UML:ModelElement.stereotype>");
		out.println ("<UML:Classifier.feature>");
		generateAttritbutes (out, element);
		generateOperations(out, element);
		generateAssociations (out, element, associations);
		out.println ("</UML:Classifier.feature>");
		generateDependenciess(out, element);
		out.println ("</UML:Class>");
		out.println (associations);
	}

	private void generateService(PrintStream out, AbstractModelClass element) {
		StringBuffer associations = new StringBuffer();
		
		out.println ("<UML:Class xmi.id='"+element.getXmlId()+"' name='"+element.getName()+"' " +
				"visibility='public' isActive='false' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' >" );
		out.println ("<UML:ModelElement.stereotype>");
		out.println ("<UML:Stereotype xmi.idref='Stereotype.Service'/>");
		out.println ("</UML:ModelElement.stereotype>");
		out.println ("<UML:Classifier.feature>");
		generateAttritbutes (out, element);
		generateOperations(out, element);
		generateAssociations (out, element, associations);
		out.println ("</UML:Classifier.feature>");
		generateDependenciess(out, element);
		out.println ("</UML:Class>");
		out.println (associations);
	}

	private void generateAssociations(PrintStream out, AbstractModelClass entity,
			StringBuffer associations) 
	{
		for (AbstractModelAttribute att :entity.getAttributes())
		{
			if (att.getDataType().isEntity())
			{
				// Search foreign attribute
				AbstractModelClass foreign = att.getDataType();
				AbstractModelAttribute foreignAtt = null;

				for (AbstractModelAttribute f: att.getDataType().getAttributes())
				{
					if (f.getForeignKey() != null && f.getForeignKey().equals(att.getColumn()))
					{
						foreignAtt = f;
						break;
					}
				}
				associations.append ("<UML:Association xmi.id='assoc."+entity.getFullName()+"."+att.getName()+"' "+
					"isSpecification = 'false' isRoot='false' isLeaf='false' isAbstract='false'>" )
					.append ("<UML:Association.connection>")
					.append ("<UML:AssociationEnd xmi.id='assoc.left."+entity.getFullName()+"."+att.getName()+"' "+
							"name='"+att.getName()+"' "+
							"isSpecification = 'false' isNavigable='true' visibility='public' ordering='unordered' aggregation='none' targetScope='instance' changeability='changeable'>" )
					.append ("<UML:AssociationEnd.multiplicity>" )
					.append ("<UML:Multiplicity xmi.id = '"+newId()+"'>")
					.append ("<UML:Multiplicity.range>" )
					.append ("<UML:MultiplicityRange xmi.id = '"+newId()+"' lower = '"+(att.isRequired()? "1": "0")+"' upper = '1'/>" )
					.append ("</UML:Multiplicity.range>" )
					.append ("</UML:Multiplicity>" )
					.append ("</UML:AssociationEnd.multiplicity> ")
					.append ("<UML:AssociationEnd.participant><UML:Class xmi.idref='"+foreign.getXmlId()+"'/></UML:AssociationEnd.participant>")
					.append ("</UML:AssociationEnd>")
					
					.append ("<UML:AssociationEnd xmi.id='assoc.rigth."+entity.getFullName()+"."+att.getName()+"' "+
							"name='"+(foreignAtt == null ? "" : foreignAtt.getName())+"' "+
							"isSpecification = 'false' isNavigable='"+(foreignAtt == null ? "false": "true")+"' visibility='public' ordering='unordered' aggregation='none' targetScope='instance' changeability='changeable'>" )
					.append ("<UML:AssociationEnd.multiplicity>" )
					.append ("<UML:Multiplicity xmi.id = '"+newId()+"'>")
					.append ("<UML:Multiplicity.range>" )
					.append ("<UML:MultiplicityRange xmi.id = '"+newId()+"' lower = '0' upper = '-1'/>" )
					.append ("</UML:Multiplicity.range>" )
					.append ("</UML:Multiplicity>" )
					.append ("</UML:AssociationEnd.multiplicity> ")
					.append ("<UML:AssociationEnd.participant><UML:Class xmi.idref='"+entity.getXmlId()+"'/></UML:AssociationEnd.participant>")
					.append ("</UML:AssociationEnd>")
					.append ("</UML:Association.connection>")
					.append ("</UML:Association>");
			}
		}
	}

	private void generateAttritbutes(PrintStream out, AbstractModelClass element) {
		for (AbstractModelAttribute att :element.getAttributes())
		{
			out.println ("<UML:Attribute xmi.id='attr."+element.getFullName()+"."+att.getName()+"' name='"+att.getName()+"' " +
					"visibility='public' isSpecification = 'false' ownerScope='instance' changeability='changeable' targetScope='instance' >" );
			
			String lower;
			String upper;
			String classId;
			if (att.getDataType().isCollection())
			{
				lower = "0";
				upper = "-1";
				classId = att.getDataType().getChildClass().getXmlId();
			} else if (att.isRequired())
			{
				lower = "1";
				upper = "1";
				classId = att.getDataType().getXmlId();
			}
			else
			{
				lower = "0";
				upper = "1";
				classId = att.getDataType().getXmlId();
			}
			out.println ("<UML:StructuralFeature.multiplicity>" +
                        "<UML:Multiplicity xmi.id = '"+newId()+"'>" +
                        "<UML:Multiplicity.range>" +
                        "<UML:MultiplicityRange xmi.id = '"+newId()+"' lower = '"+lower+"' upper = '"+upper+"'/>" +
                        "</UML:Multiplicity.range>" +
                        "</UML:Multiplicity>" +
                      	"</UML:StructuralFeature.multiplicity> ");
			out.println ("<UML:StructuralFeature.type>" +
                    "<UML:Class xmi.idref = '"+classId+"' />" +
                  	"</UML:StructuralFeature.type> ");
			out.println ("</UML:Attribute>");
		}
	}

	private void generateOperations(PrintStream out, AbstractModelClass element) {
		int i = 0;
		for (ModelOperation op :element.getOperations())
		{
			i ++;
			out.println ("<UML:Operation xmi.id='method."+element.getFullName()+"."+op.getName()+"_"+i+"' name='"+op.getName()+"' " +
					"visibility='public' isSpecification = 'false' ownerScope='instance' isQuery='"+ op.isQuery()+ "' concurrency='sequential' isRoot='false' isLeaf='false' isAbstract='false' >" );
			
			out.println ("<UML:BehavioralFeature.parameter>" );
			for (ModelParameter param: op.getParameters())
			{
				out.println ("<UML:Parameter xmi.id='"+newId()+"' name='"+param.getName()+"' isSpecification='false' kind='in'>");
				out.println ("<UML:Parameter.type><UML:Class xmi.idref = '"+param.getDataType().getXmlId()+"' /></UML:Parameter.type>");
				out.println ("</UML:Parameter>");
			}

			out.println ("<UML:Parameter xmi.id='"+newId()+"' name='return' isSpecification='false' kind='return'>");
			out.println ("<UML:Parameter.type><UML:Class xmi.idref = '"+op.getReturnParameter().getDataType().getXmlId()+"' /></UML:Parameter.type>");
			out.println ("</UML:Parameter>");

			out.println ("</UML:BehavioralFeature.parameter>" );
			out.println ("</UML:Operation>");
		}
	}

	private void generateDependenciess(PrintStream out, AbstractModelClass element) {
		int i = 0;
		String newId = newId();
		for (AbstractModelClass dep :element.getDepends())
		{
			out.println ("<UML:ModelElement.clientDependency><UML:Dependency xmi.idref='"+newId+"_"+i+"' /></UML:ModelElement.clientDependency>");
			i++;
		}
		out.println ("<UML:Namespace.ownedElement>");
		for (AbstractModelClass dep :element.getDepends())
		{
			out.println ("<UML:Dependency xml.id='"+newId+"_"+i+"' isSpecification = 'false' >");
			out.println ("<UML:Dependency.client><UML:Class xmi.idref='"+element.getXmlId()+"' /></UML:Dependency.client>");
			out.println ("<UML:Dependency.supplier><UML:Class xmi.idref='"+dep.getXmlId()+"' /></UML:Dependency.supplier>");
			out.println ("</UML:Dependency>");
		}
		out.println ("</UML:Namespace.ownedElement>");
	}

	private void generateStereotype(PrintStream out, String name) {
		out.println ("<UML:Stereotype xmi.id='Stereotype."+name+"' name='"+name+"' "+endl
				+"isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false' />" );
	}

	private void preparePackages() {
		for (ModelElement element: parser.getModelElements())
		{
			if (element instanceof AbstractModelClass)
			{
				String packageName = ((AbstractModelClass) element).getPackage();
				Package currentPkg = pkg;
				if (packageName != null)
				{
					for (String packagePart: packageName.split("\\."))
					{
						boolean found = false;
						for (Package p: currentPkg.packages)
						{
							if (p.name.equals (packagePart))
							{
								found = true;
								currentPkg = p;
								break;
							}
						}
						if (! found )
						{
							Package p = new Package ();
							p.name = packagePart;
							currentPkg.packages.add(p);
							currentPkg = p;
						}
					}
				}
				currentPkg.classes.add((ModelClass) element);
			}
		}
	}
	
	private String newId () 
	{
		return "soffid-id-"+this.idGenerator ++;
	}

}

class Package {
	String name;
	
	List<Package> packages = new LinkedList<Package>();
	
	List<ModelClass> classes = new LinkedList<ModelClass>();
			
}
