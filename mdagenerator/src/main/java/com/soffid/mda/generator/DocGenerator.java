package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.soffid.mda.parser.AbstractModelAttribute;
import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class DocGenerator {
	private Generator generator;
	private Parser parser;

	public void generate (Generator g, Parser parser) throws IOException
	{
		this.generator = g;
		this.parser = parser;
		generateRoot ();
		generateCss ();
		generateEntities ();
		generateServices ();
		generateActors ();
		generateValueObjects ();
	}

	private void generateEntities() throws FileNotFoundException , UnsupportedEncodingException {
		File f = new File (generator.getUmlDir()+File.separator+"entities.html");
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		p.println("<?xml version='1.0' encoding='utf-8'?>");
		p.println("<html><head><link rel=\"stylesheet\" href=\"style.css\">");
		if (generator.isPlugin())
			p.println("<title>Plugin "+generator.getPluginName()+ " description</title>");
		else
			p.println("<title>Soffid core description</title>");
		p.println("</head><body>");
		p.println("<h1>List of entities</h1>");
		String currentPkg = null;
		for (ClassName cn: sortClasses (parser.getEntities(), false))
		{
			if (currentPkg == null || ! currentPkg.equals(cn.pkg))
			{
				if (currentPkg != null)
					p.println ("</ul>");
				p.println("<h2>Package "+cn.pkg+"</h2>");
				p.println("<ul class='navigation'>");
				currentPkg = cn.pkg;
			}
			generateClassReference(p, cn.mc, cn.scope);
			generateEntity (cn);
			generateEntityDao (cn);
		}
		if (currentPkg == null)
			p.println("</ul>");
		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateServices() throws FileNotFoundException , UnsupportedEncodingException {
		File f = new File (generator.getUmlDir()+File.separator+"services.html");
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		p.println("<?xml version='1.0' encoding='utf-8'?>");
		p.println("<html><head><link rel=\"stylesheet\" href=\"style.css\">");
		if (generator.isPlugin())
			p.println("<title>Plugin "+generator.getPluginName()+ " description</title>");
		else
			p.println("<title>Soffid core description</title>");
		p.println("</head><body>");
		p.println("<h1>List of services</h1>");
		String currentPkg = null;
		for (ClassName cn: sortClasses (parser.getServices(), false))
		{
			if (currentPkg == null || ! currentPkg.equals(cn.pkg))
			{
				if (currentPkg != null)
					p.println ("</ul>");
				p.println("<h2>Package "+cn.pkg+"</h2>");
				p.println("<ul class='navigation'>");
				currentPkg = cn.pkg;
			}
			generateClassReference(p, cn.mc, cn.scope);
			generateService (cn);
		}
		if (currentPkg == null)
			p.println("</ul>");
		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateActors() throws FileNotFoundException , UnsupportedEncodingException {
		File f = new File (generator.getUmlDir()+File.separator+"roles.html");
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		p.println("<?xml version='1.0' encoding='utf-8'?>");
		p.println("<html><head><link rel=\"stylesheet\" href=\"style.css\">");
		if (generator.isPlugin())
			p.println("<title>Plugin "+generator.getPluginName()+ " description</title>");
		else
			p.println("<title>Soffid core description</title>");
		p.println("</head><body>");
		p.println("<h1>List of entities</h1>");
		String currentPkg = null;
		for (ClassName cn: sortClasses (parser.getActors(), false))
		{
			if (currentPkg == null || ! currentPkg.equals(cn.pkg))
			{
				if (currentPkg != null)
					p.println ("</ul>");
				p.println("<h2>Package "+cn.pkg+"</h2>");
				p.println("<ul class='navigation'>");
				currentPkg = cn.pkg;
			}
			generateClassReference(p, cn.mc, cn.scope);
			generateActor (cn);
		}
		if (currentPkg == null)
			p.println("</ul>");
		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateValueObjects() throws FileNotFoundException , UnsupportedEncodingException {
		File f = new File (generator.getUmlDir()+File.separator+"valueobjects.html");
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");

		generateHeader(f, p);;
		p.println("<h1>List of value objects</h1>");
		String currentPkg = null;
		LinkedList<ModelClass> list = new LinkedList<ModelClass>();
		list.addAll(parser.getValueObjects());
		list.addAll(parser.getEnumerations());
		for (ClassName cn: sortClasses (list, false))
		{
			if (currentPkg == null || ! currentPkg.equals(cn.pkg))
			{
				if (currentPkg != null)
					p.println ("</ul>");
				p.println("<h2>Package "+cn.pkg+"</h2>");
				p.println("<ul class='navigation'>");
				currentPkg = cn.pkg;
			}
			generateClassReference(p, cn.mc, cn.scope);
			if (cn.mc.isValueObject())
				generateValueObject (cn);
			if (cn.mc.isEnumeration())
				generateEnumeration (cn);
		}
		if (currentPkg == null)
			p.println("</ul>");
		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}


	private void generateFooter(SmartPrintStream p) {
		p.print("<p class='genfooter'>"+ DateFormat.getDateInstance(DateFormat.SHORT).format(new Date())+"</p>");
	}

	private void generateClassReference(SmartPrintStream p, AbstractModelClass mc, int scope) {
		String pp = mc.getPackage(scope);
		pp = pp.replaceAll("\\.", "/");
		if (mc.isRole())
			p.println("<li><a href='"+pp+"/"+mc.getName(scope)+".html'>"+mc.getRoleName()+"</a>");
		else
			p.println("<li><a href='"+pp+"/"+mc.getName(scope)+".html'>"+mc.getName(scope)+"</a>");
		if (mc.isEntity())
			p.println(" <a href='"+pp+"/"+mc.getDaoName(scope)+".html'>[DAO]</a>");
		p.println ("</li>");
	}

	private void generateClassReference(SmartPrintStream p, AbstractModelClass source, AbstractModelClass mc, int scope) {
		if (mc.isGenerated())
		{
			p.println("<a href='"+generateRef(source, mc, scope)+"'>"+mc.getName(scope)+"</a>");
			if (mc.isEntity())
			{
				p.println(" <a href='"+generateRef (getDocFile(source, scope), getDaoDocFile(mc, scope))+"'>[DAO]</a>");
			}
		}
		else
			p.println(mc.getName(scope));
	}

	private void generateDaoReference(SmartPrintStream p, AbstractModelClass source, AbstractModelClass mc, int scope) {
		if (mc.isGenerated())
		{
			if (mc.isEntity())
				p.println("<a href='"+generateRef (getDocFile(source, scope), getDaoDocFile(mc, scope))+"'>"+mc.getName(scope)+"</a>");
			else
				p.println("<a href='"+generateRef(source, mc, scope)+"'>"+mc.getName(scope)+"</a>");
		}
		else
			p.println(mc.getName(scope));
	}

	private void generateDaoReference(SmartPrintStream p, AbstractModelClass mc, int scope) {
		String pp = mc.getPackage(scope);
		pp = pp.replaceAll("\\.", "/");
		p.println("<li><a href='"+pp+"/"+mc.getName(scope)+".html'>"+mc.getName(scope)+"</a>");
		if (mc.isEntity())
			p.println("<li><a href='"+pp+"/"+mc.getName(scope)+".html'>"+mc.getName(scope)+"</a>");
		else
			p.println("<li><a href='"+pp+"/"+mc.getDaoName(scope)+".html'>"+mc.getDaoName(scope)+"</a>");
		p.println ("</li>");
	}

	private List<ClassName>  sortClasses(List<ModelClass> entities, boolean addTranslated) {
		List<ClassName> cn = new LinkedList<ClassName>();
		for (AbstractModelClass mc: entities)
		{
			cn.add ( new ClassName (mc, Translate.TRANSLATE) );
		}
		Collections.sort(cn, new Comparator<ClassName> () {

			public int compare(ClassName o1, ClassName o2) {
				int c = o1.pkg.compareTo(o2.pkg);
				if (c == 0)
					c = o1.name.compareTo(o2.name);
				
				return c;
			}
			
		});
		return cn;
	}

	private void generateCss() throws IOException {
		File f = new File (getStylePath());
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		OutputStream out = new FileOutputStream(f);
		InputStream in = getClass().getClassLoader().getResourceAsStream("com/soffid/mda/style.css");
		byte b [] = new byte [2048];
		int read;
		while ( (read = in.read(b)) > 0)
		{
			out.write (b, 0, read);
		}
		out.close ();
	}

	private String getStylePath() {
		return generator.getUmlDir()+File.separator+"style.css";
	}

	private void generateRoot() throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File (generator.getUmlDir()+File.separator+"index.html");
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		if (generator.isPlugin())
			p.println("<h1>Plugin "+generator.getPluginName()+ " model</h1>");
		else
			p.println("<h1>Soffid core model</h1>");
		p.println("</head><body>");
		p.println("<ul class='navigation'>");
		p.println("<li><a href='entities.html'>Entities</a></li>");
		p.println("<li><a href='valueobjects.html'>Value Objects</a></li>");
		p.println("<li><a href='services.html'>Services</a></li>");
		p.println("<li><a href='roles.html'>Roles</a></li>");
		p.println("</ul>");
		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateEntity(ClassName cn) throws FileNotFoundException , UnsupportedEncodingException {
		
		AbstractModelClass mc = cn.mc;
		int scope = cn.scope;
		File f = new File (getDocFile(mc, scope));
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Entity "+mc.getFullName(scope));
		p.println(" <a target='_new' href='"+mc.getName(scope)+"-erc.svg'>[Tables]</a>");
		p.println(" <a href='"+mc.getDaoName(scope)+".html'>[DAO]</a>");
		p.println("</h1>");
		generateImage (p, mc.getName(scope)+"-er.svg");

		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		p.println ("<li><div class='property'>TableName</div><div class='property-value'>"+Util.formatHtml(mc.getTableName())+"</div></div></div>");
		if (mc.getDiscriminatorColumn() != null && mc.getDiscriminatorColumn().length() > 0)
			p.println ("<li><div class='property'>Discriminator column</div><div class='property-value'>"+Util.formatHtml(mc.getDiscriminatorColumn()+"</div></div></li>"));
		if (mc.getDiscriminatorValue() != null && mc.getDiscriminatorValue().length() > 0)
			p.println ("<li><div class='property'>Discriminator value</div><div class='property-value'>"+Util.formatHtml(mc.getDiscriminatorValue()+"</div></div></li>"));
		p.println ("</ul>");
		
		
		p.println("<h2 class='entitySection'>Attributes</h2>");
		p.println("<ul class='attributes'>");
		
		for (AbstractModelAttribute att: mc.getAttributes())
		{
			generateAttribute(mc, scope, p, att, true);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Indexes</h2>");
		p.println("<ul class='attributes'>");
		for (AbstractModelClass ic: parser.getIndexes())
		{
			if (ic.getIndexEntity() == mc)
			{
				p.println ("<li><div class='attribute-name'>"+Util.formatHtml(ic.getIndexName())+"</div>");
				if (ic.getIndexColumns() != null)
				{
					p.print("<div class='attribute-type'>");
					for (String c: ic.getIndexColumns())
					{
						p.println (Util.formatHtml(c)+" ");
					}
					p.print("</div>");
				}
				if (ic.isIndexUnique())
					p.println("<span class='attribute-identifier'>Unique index</span>");
				p.println("</li>");
			}
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Entity methods</h2>");
		p.println("<ul class='methods'>");
		
		for (ModelOperation op: mc.getOperations())
		{
			if (! op.isQuery() && ! op.isStatic() && !op.isDeprecated())
				generateMethod(null, scope, p, op);
		}
		p.println ("</ul>");

		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateValueObject(ClassName cn) throws FileNotFoundException , UnsupportedEncodingException {
		
		AbstractModelClass mc = cn.mc;
		int scope = cn.scope;
		File f = new File (getDocFile(mc, scope));
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>ValueObject "+mc.getFullName(scope)+"</h1>");
		
		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Attributes</h2>");
		p.println("<ul class='attributes'>");
		
		for (AbstractModelAttribute att: mc.getAttributes())
		{
			generateAttribute(mc, scope, p, att);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Methods</h2>");
		p.println("<ul class='methods'>");
		
		for (ModelOperation op: mc.getOperations())
		{
			if (! op.isQuery() && ! op.isStatic() && !op.isDeprecated())
				generateMethod(null, scope, p, op);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Related entities</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getProvides())
		{
			if (mc2.isEntity())
			{
				p.println ("<li>");
				generateClassReference(p, mc, mc2, scope);
				p.println ("</li>");
			}
		}
		p.println ("</ul>");

		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateEnumeration(ClassName cn) throws FileNotFoundException , UnsupportedEncodingException {
		
		AbstractModelClass mc = cn.mc;
		int scope = cn.scope;
		File f = new File (getDocFile(mc, scope));
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Enumeration "+mc.getFullName(scope)+"</h1>");
		
		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Attributes</h2>");
		p.println("<ul class='attributes'>");
		
		for (AbstractModelAttribute att: mc.getAttributes())
		{
			generateConstantAttribute(mc, scope, p, att);
		}
		p.println ("</ul>");

		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateImage(SmartPrintStream p, String file) {
		p.println ("<div class='uml'><a href='"+file+"' target='_new'><img src='"+file+"'></a></div>");
	}

	private void generateHeader(File f, SmartPrintStream p) {
		p.println("<?xml version='1.0' encoding='utf-8'?>");
		p.println("<html><head><link rel=\"stylesheet\" href=\""+generateRef(f.getPath(), getStylePath())+"\">");
		if (generator.isPlugin())
			p.println("<title>Plugin "+generator.getPluginName()+ " description</title>");
		else
			p.println("<title>Soffid core description</title>");
		p.println("</head><body>");
		String index = generator.getUmlDir()+File.separator+"index.html";
		p.println("<p class='topline'><a href='"+generateRef(f.getPath(), index)+"'>Index</a></p>");
	}

	private void generateEntityDao(ClassName cn) throws FileNotFoundException , UnsupportedEncodingException {		
		AbstractModelClass mc = cn.mc;
		int scope = cn.scope;
		File f = new File (getDaoDocFile(mc, scope));
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Entity DAO "+mc.getDaoFullName(scope)+" <a href='"+mc.getName(scope)+".html'>[Entity]</a></h1>");
		
		String image = generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(scope))+mc.getName(scope)+".html";

		generateImage (p, mc.getName(scope)+"-dao.svg");

		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>DAO methods</h2>");
		p.println("<ul class='methods'>");
		
		for (ModelOperation op: mc.getOperations())
		{
			if ((op.isQuery() || op.isStatic()) && !op.isDeprecated())
				generateMethod(null, scope, p, op);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>DAO Dependencies</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getDepends())
		{
			if (mc2.isEntity())
			{
				p.println("<li>");
				generateDaoReference(p, mc, mc2, scope);
				p.println("</li>");
			}
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Managed value objects</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getDepends())
		{
			if (mc2.isValueObject())
			{
				p.println ("<li>");
				generateClassReference(p, mc, mc2, scope);
				p.println ("</li>");
			}
		}
		p.println ("</ul>");
		p.println("<h2 class='entitySection'>Service Dependencies</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getDepends())
		{
			if (mc2.isService())
			{
				p.println ("<li>");
				generateClassReference(p, mc, mc2, scope);
				p.println ("</li>");
			}
		}
		p.println ("</ul>");


		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateService(ClassName cn) throws FileNotFoundException , UnsupportedEncodingException {		
		AbstractModelClass mc = cn.mc;
		int scope = cn.scope;
		File f = new File (getDocFile(mc, scope));
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Service "+mc.getFullName(scope)+"<a target='_new' href='"+mc.getName(scope)+"-uc.svg'>[UseCase]</a></h1>");
		
		String image = generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(scope))+mc.getName(scope)+".html";

		generateImage (p, mc.getName(scope)+".svg");

		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Spring bean name </div><div class='property-value'>"+Util.formatHtml(mc.getBaseName(scope))+"</div></div></div>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		if (mc.isServerOnly())
			p.println ("<li><div class='property'>EJB bean name </div><div class='property-value'> - Sync server only -</div></div></div>");
		else if (mc.isInternal())
			p.println ("<li><div class='property'>EJB bean name </div><div class='property-value'> - Not allowed -</div></div></div>");
		else
			p.println ("<li><div class='property'>EJB bean name </div><div class='property-value'>soffid/ejb/" + mc.getFullName(scope) +"</div></div></div>");
		if (!mc.getActors().isEmpty())
		{
			p.println ("<li><div class='property'>Actors</div><div class='property-value'>");
			for (AbstractModelClass actor: mc.getActors())
			{
				generateClassReference(p, mc, actor, scope);
			}
			p.println ("</div></div></div>");
		}
		p.println ("</ul>");
		
		p.println("<h2 class='entitySection'>Service methods</h2>");
		p.println("<ul class='methods'>");
		
		for (ModelOperation op: mc.getOperations())
		{
			if (!op.isDeprecated()) {
				generateMethod(null, scope, p, op);
			}
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>DAO Dependencies</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getDepends())
		{
			if (mc2.isEntity())
			{
				p.println("<li>");
				generateDaoReference(p, mc, mc2, scope);
				p.println("</li>");
			}
		}
		p.println ("</ul>");

		p.println ("</ul>");
		p.println("<h2 class='entitySection'>Service Dependencies</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getDepends())
		{
			if (mc2.isService())
			{
				p.println ("<li>");
				generateClassReference(p, mc, mc2, scope);
				p.println ("</li>");
			}
		}
		p.println ("</ul>");


		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateActor(ClassName cn) throws FileNotFoundException , UnsupportedEncodingException {		
		AbstractModelClass mc = cn.mc;
		int scope = cn.scope;
		File f = new File (getDocFile(mc, scope));
		f.getParentFile().mkdirs();

//		System.out.println ("Generating "+f.getPath());

		SmartPrintStream p = new SmartPrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Role "+mc.getRoleName()+"</h1>");
		
		String image = generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(scope))+mc.getName(scope)+".html";

		p.println("<h2 class='entitySection'>Services</h2>");
		p.println("<ul class='methods'>");
		
		for (AbstractModelClass service: parser.getServices())
		{
			if (service.getActors().contains(mc))
			{
				p.println("<li><div class='operation'><div class='operation-header'><div class='operation-name'>");
				generateClassReference(p, mc, service, scope);
				p.print("</div> <div class='operation-description'>");
				p.print("All service operations");
				p.print ("</div></li>");
			} else {
				for (ModelOperation op: service.getOperations())
				{
					if (!op.isDeprecated()) {
						if (op.getActors().contains (mc))
						{
							generateMethod(mc, scope, p, op);
						}
					}
				}
			}
		}
		p.println ("</ul>");

		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}


	private void generateAttribute(AbstractModelClass mc, int scope,
			SmartPrintStream p, AbstractModelAttribute att) {
		generateAttribute(mc, scope, p, att, false);
	}
	private void generateAttribute(AbstractModelClass mc, int scope,
			SmartPrintStream p, AbstractModelAttribute att, boolean columnName) {
		p.println("<li><span class='attribute-name'>");
		p.print(att.getName(scope));
		p.print("</span> <span class='attribute-type'>");
		AbstractModelClass dataType = att.getDataType();
		generateDataType(mc, scope, p, dataType);
		p.print("</span> ");
		p.print("<span> ");
		if (columnName && att.getColumn() != null)
			p.print(att.getColumn()+" ");
		if (att.isIdentifier())
			p.print ("<span class='attribute-identifier'>Identifier</span>");
		else if (att.isRequired() && ! att.getDataType().isCollection())
			p.print ("<span class='attribute-required'>Required</span>");
		else
			p.print ("<span class='attribute-optional'>Optional</span>");
		p.print("</span> ");
		p.print("<span class='attribute-description'>");
		p.print(Util.formatHtml(att.getComments()));
		p.print("</span> ");
		p.println ("</li>");
	}

	private void generateConstantAttribute(AbstractModelClass mc, int scope,
			SmartPrintStream p, AbstractModelAttribute att) {
		p.println("<li><span class='attribute-name'>");
		p.print(att.getName(scope));
		p.print("</span> <span class='attribute-type'>");
		AbstractModelClass dataType = att.getDataType();
		generateDataType(mc, scope, p, dataType);
		p.print("</span> ");
		p.print ("<span class='attribute-required'>"+ Util.formatHtml(att.getConstantValue())+"</span>");
		p.print("<span class='attribute-description'>");
		p.print(Util.formatHtml(att.getComments()));
		p.print("</span> ");
		p.println ("</li>");
	}


	private void generateDataType(AbstractModelClass mc, int scope,
			SmartPrintStream p, AbstractModelClass dataType) {
		if (dataType.isCollection())
		{
			AbstractModelClass ch = dataType.getChildClass();
			if (ch != null )
			{
				p.println (dataType.getRawType()+"&lt;");
				generateDataType(mc, scope, p, ch);
				p.println ("&gt;");
			}
			else
			{
				p.println (dataType.getJavaType(scope));
			}
		}
		else  {
			if (dataType.isValueObject() || dataType.isEntity() || dataType.isEnumeration() || dataType.isService())
			{
				p.println ("<a href='"+generateRef (mc, dataType, scope)+"'>"
						+ dataType.getFullName( scope )+"</a>");
			} else {
				p.println (dataType.getJavaType(scope));
			}
		}
	}

	private void generateMethod(AbstractModelClass referrer, int scope,
			SmartPrintStream p, ModelOperation op) {
		AbstractModelClass mc = op.getModelClass();
		p.println("<li><div class='operation'><div class='operation-header'><div class='operation-name'>");
		if (referrer != null)
		{
			generateClassReference(p, referrer, mc, scope);
			p.print(".");
		}
		else
			referrer = mc;
		p.print(op.getName(scope));
		p.print("</div> <div class='operation-description'>");
		p.print(Util.formatHtml(op.getComments()));
		if (!op.getActors().isEmpty())
		{
			p.print("</div> <div class='operation-actors'>Actors: ");
			for (AbstractModelClass actor: op.getActors())
			{
				generateClassReference(p, referrer, actor, scope);
			}
		}
		p.print("</div><div class='hql'>");
		p.print(Util.formatHtml(op.getFinderQuery()));
		p.print ("</div></div><div class='params'>");
		for (ModelParameter param: op.getParameters())
		{
			p.print("<div class='param'><div class='param-name'>"+param.getName(scope)+"</div><div class='param-type'>");
			generateDataType(referrer, scope, p, param.getDataType());
			p.print("</div><div class='param-description'>");
			p.print(Util.formatHtml(param.getComments()));
			p.print("</div>");
		}
		p.print("</div><div class='return'><div class='return-name'>Returns</div><div class='return-type'>");
		generateDataType(referrer, scope, p, op.getReturnParameter().getDataType());
		p.print ("</div></li>");
	}

	
	private String getDocFile(AbstractModelClass mc, int scope) {
		return generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(scope))+mc.getName(scope)+".html";
	}

	private String getDaoDocFile(AbstractModelClass mc, int scope) {
		return generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(scope))+mc.getDaoName(scope)+".html";
	}

	public String generateRef(AbstractModelClass mc, AbstractModelClass ch, int scope) {
		return generateRef (getDocFile(mc, scope), getDocFile(ch, scope));
	}

	private String generateRef(String docFile, String docFile2) {
		StringBuffer ref = new StringBuffer();
		int lastSlash = docFile.length();
		do
		{
			lastSlash = docFile.lastIndexOf(File.separatorChar, lastSlash-1);
			if (lastSlash < 0)
				return docFile2;
			if (lastSlash < docFile2.length() && 
					docFile2.substring(0, lastSlash).equals(docFile.substring(0, lastSlash)))
				break;
			ref.append("../");
		} while (true);
		ref.append (docFile2.substring(lastSlash+1).replace(File.separatorChar, '/'));
		return ref.toString();
	}
}


class ClassName {
	String pkg;
	String name;
	AbstractModelClass mc;
	int scope;
	
	public ClassName (AbstractModelClass mc, int scope)
	{
		this.mc = mc;
		this.scope = scope;
		pkg = mc.getPackage(scope);
		name = mc.getName(scope);
	}
}