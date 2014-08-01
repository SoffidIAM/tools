package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
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
			generateClassReference(p, cn.mc, cn.translated);
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

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
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
			generateClassReference(p, cn.mc, cn.translated);
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

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
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
			generateClassReference(p, cn.mc, cn.translated);
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

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");

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
			generateClassReference(p, cn.mc, cn.translated);
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


	private void generateFooter(PrintStream p) {
		p.print("<p class='genfooter'>"+ DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date())+"</p>");
	}

	private void generateClassReference(PrintStream p, AbstractModelClass mc, boolean translated) {
		String pp = mc.getPackage(translated);
		pp = pp.replaceAll("\\.", "/");
		if (mc.isRole())
			p.println("<li><a href='"+pp+"/"+mc.getName(translated)+".html'>"+mc.getRoleName()+"</a>");
		else
			p.println("<li><a href='"+pp+"/"+mc.getName(translated)+".html'>"+mc.getName(translated)+"</a>");
		if (mc.isEntity())
			p.println(" <a href='"+pp+"/"+mc.getDaoName(translated)+".html'>[DAO]</a>");
		p.println ("</li>");
	}

	private void generateClassReference(PrintStream p, AbstractModelClass source, AbstractModelClass mc, boolean translated) {
		if (mc.isGenerated())
		{
			p.println("<a href='"+generateRef(source, mc, translated)+"'>"+mc.getName(translated)+"</a>");
			if (mc.isEntity())
			{
				p.println(" <a href='"+generateRef (getDocFile(source, translated), getDaoDocFile(mc, translated))+"'>[DAO]</a>");
			}
		}
		else
			p.println(mc.getName(translated));
	}

	private void generateDaoReference(PrintStream p, AbstractModelClass source, AbstractModelClass mc, boolean translated) {
		if (mc.isGenerated())
		{
			if (mc.isEntity())
				p.println("<a href='"+generateRef (getDocFile(source, translated), getDaoDocFile(mc, translated))+"'>"+mc.getName(translated)+"</a>");
			else
				p.println("<a href='"+generateRef(source, mc, translated)+"'>"+mc.getName(translated)+"</a>");
		}
		else
			p.println(mc.getName(translated));
	}

	private void generateDaoReference(PrintStream p, AbstractModelClass mc, boolean translated) {
		String pp = mc.getPackage(translated);
		pp = pp.replaceAll("\\.", "/");
		p.println("<li><a href='"+pp+"/"+mc.getName(translated)+".html'>"+mc.getName(translated)+"</a>");
		if (mc.isEntity())
			p.println("<li><a href='"+pp+"/"+mc.getName(translated)+".html'>"+mc.getName(translated)+"</a>");
		else
			p.println("<li><a href='"+pp+"/"+mc.getDaoName(translated)+".html'>"+mc.getDaoName(translated)+"</a>");
		p.println ("</li>");
	}

	private List<ClassName>  sortClasses(List<ModelClass> entities, boolean addTranslated) {
		List<ClassName> cn = new LinkedList<ClassName>();
		for (AbstractModelClass mc: entities)
		{
			cn.add ( new ClassName (mc, false) );
			if (addTranslated && mc.isTranslated())
				cn.add ( new ClassName (mc, true) );
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

		System.out.println ("Generating "+f.getPath());

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

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
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
		boolean translated = cn.translated;
		File f = new File (getDocFile(mc, translated));
		f.getParentFile().mkdirs();

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Entity "+mc.getFullName(translated)+" <a href='"+mc.getDaoName(translated)+".html'>[DAO]</a></h1>");
		generateImage (p, mc.getName(translated)+"-er.svg");

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
			generateAttribute(mc, translated, p, att);
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
			if (! op.isQuery() && ! op.isStatic())
				generateMethod(null, translated, p, op);
		}
		p.println ("</ul>");

		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateValueObject(ClassName cn) throws FileNotFoundException , UnsupportedEncodingException {
		
		AbstractModelClass mc = cn.mc;
		boolean translated = cn.translated;
		File f = new File (getDocFile(mc, translated));
		f.getParentFile().mkdirs();

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>ValueObject "+mc.getFullName(translated)+"</h1>");
		
		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Attributes</h2>");
		p.println("<ul class='attributes'>");
		
		for (AbstractModelAttribute att: mc.getAttributes())
		{
			generateAttribute(mc, translated, p, att);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Methods</h2>");
		p.println("<ul class='methods'>");
		
		for (ModelOperation op: mc.getOperations())
		{
			if (! op.isQuery() && ! op.isStatic())
				generateMethod(null, translated, p, op);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Related entities</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getProvides())
		{
			if (mc2.isEntity())
			{
				p.println ("<li>");
				generateClassReference(p, mc, mc2, translated);
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
		boolean translated = cn.translated;
		File f = new File (getDocFile(mc, translated));
		f.getParentFile().mkdirs();

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Enumeration "+mc.getFullName(translated)+"</h1>");
		
		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>Attributes</h2>");
		p.println("<ul class='attributes'>");
		
		for (AbstractModelAttribute att: mc.getAttributes())
		{
			generateConstantAttribute(mc, translated, p, att);
		}
		p.println ("</ul>");

		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}

	private void generateImage(PrintStream p, String file) {
		p.println ("<div class='uml'><a href='"+file+"' target='_new'><img src='"+file+"'></a></div>");
	}

	private void generateHeader(File f, PrintStream p) {
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
		boolean translated = cn.translated;
		File f = new File (getDaoDocFile(mc, translated));
		f.getParentFile().mkdirs();

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Entity DAO "+mc.getDaoFullName(translated)+" <a href='"+mc.getName(translated)+".html'>[Entity]</a></h1>");
		
		String image = generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(translated))+mc.getName(translated)+".html";

		generateImage (p, mc.getName(translated)+"-dao.svg");

		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>DAO methods</h2>");
		p.println("<ul class='methods'>");
		
		for (ModelOperation op: mc.getOperations())
		{
			if (op.isQuery() || op.isStatic())
				generateMethod(null, translated, p, op);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>DAO Dependencies</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getDepends())
		{
			if (mc2.isEntity())
			{
				p.println("<li>");
				generateDaoReference(p, mc, mc2, translated);
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
				generateClassReference(p, mc, mc2, translated);
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
				generateClassReference(p, mc, mc2, translated);
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
		boolean translated = cn.translated;
		File f = new File (getDocFile(mc, translated));
		f.getParentFile().mkdirs();

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Service "+mc.getFullName(translated)+"<a target='_new' href='"+mc.getName(translated)+"-uc.svg'>[UseCase]</a></h1>");
		
		String image = generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(translated))+mc.getName(translated)+".html";

		generateImage (p, mc.getName(translated)+".svg");

		p.println ("<ul class='properties'>");
		p.println ("<li><div class='property'>Spring bean name </div><div class='property-value'>"+Util.formatHtml(mc.getBaseName(translated))+"</div></div></div>");
		p.println ("<li><div class='property'>Description</div><div class='property-value'>"+Util.formatHtml(mc.getDescription())+"</div></div></div>");
		if (mc.isServerOnly())
			p.println ("<li><div class='property'>EJB bean name </div><div class='property-value'> - Sync server only -</div></div></div>");
		else if (mc.isInternal())
			p.println ("<li><div class='property'>EJB bean name </div><div class='property-value'> - Not allowed -</div></div></div>");
		else
			p.println ("<li><div class='property'>EJB bean name </div><div class='property-value'>soffid/ejb/" + mc.getFullName(translated) +"</div></div></div>");
		if (!mc.getActors().isEmpty())
		{
			p.println ("<li><div class='property'>Actors</div><div class='property-value'>");
			for (AbstractModelClass actor: mc.getActors())
			{
				generateClassReference(p, mc, actor, translated);
			}
			p.println ("</div></div></div>");
		}
		p.println ("</ul>");
		
		p.println("<h2 class='entitySection'>Service methods</h2>");
		p.println("<ul class='methods'>");
		
		for (ModelOperation op: mc.getOperations())
		{
			generateMethod(null, translated, p, op);
		}
		p.println ("</ul>");

		p.println("<h2 class='entitySection'>DAO Dependencies</h2>");
		p.println("<ul class='dependencies'>");
		
		for (AbstractModelClass mc2: mc.getDepends())
		{
			if (mc2.isEntity())
			{
				p.println("<li>");
				generateDaoReference(p, mc, mc2, translated);
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
				generateClassReference(p, mc, mc2, translated);
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
		boolean translated = cn.translated;
		File f = new File (getDocFile(mc, translated));
		f.getParentFile().mkdirs();

		System.out.println ("Generating "+f.getPath());

		PrintStream p = new PrintStream(f, "UTF-8");
		
		generateHeader(f, p);
		p.println("<h1>Role "+mc.getRoleName()+"</h1>");
		
		String image = generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(translated))+mc.getName(translated)+".html";

		p.println("<h2 class='entitySection'>Services</h2>");
		p.println("<ul class='methods'>");
		
		for (AbstractModelClass service: parser.getServices())
		{
			if (service.getActors().contains(mc))
			{
				p.println("<li><div class='operation'><div class='operation-header'><div class='operation-name'>");
				generateClassReference(p, mc, service, translated);
				p.print("</div> <div class='operation-description'>");
				p.print("All service operations");
				p.print ("</div></li>");
			} else {
				for (ModelOperation op: service.getOperations())
				{
					if (op.getActors().contains (mc))
					{
						generateMethod(mc, translated, p, op);
					}
				}
			}
		}
		p.println ("</ul>");

		generateFooter(p);
		p.print("</body></html>");
		p.close ();
	}


	private void generateAttribute(AbstractModelClass mc, boolean translated,
			PrintStream p, AbstractModelAttribute att) {
		p.println("<li><span class='attribute-name'>");
		p.print(att.getName(translated));
		p.print("</span> <span class='attribute-type'>");
		AbstractModelClass dataType = att.getDataType();
		generateDataType(mc, translated, p, dataType);
		p.print("</span> ");
		if (att.isIdentifier())
			p.print ("<span class='attribute-identifier'>Identifier</span>");
		else if (att.isRequired() && ! att.getDataType().isCollection())
			p.print ("<span class='attribute-required'>Required</span>");
		else
			p.print ("<span class='attribute-optional'>Optional</span>");
		p.print("<span class='attribute-description'>");
		p.print(Util.formatHtml(att.getComments()));
		p.print("</span> ");
		p.println ("</li>");
	}

	private void generateConstantAttribute(AbstractModelClass mc, boolean translated,
			PrintStream p, AbstractModelAttribute att) {
		p.println("<li><span class='attribute-name'>");
		p.print(att.getName(translated));
		p.print("</span> <span class='attribute-type'>");
		AbstractModelClass dataType = att.getDataType();
		generateDataType(mc, translated, p, dataType);
		p.print("</span> ");
		p.print ("<span class='attribute-required'>"+ Util.formatHtml(att.getConstantValue())+"</span>");
		p.print("<span class='attribute-description'>");
		p.print(Util.formatHtml(att.getComments()));
		p.print("</span> ");
		p.println ("</li>");
	}


	private void generateDataType(AbstractModelClass mc, boolean translated,
			PrintStream p, AbstractModelClass dataType) {
		if (dataType.isCollection())
		{
			AbstractModelClass ch = dataType.getChildClass();
			if (ch != null )
			{
				p.println (dataType.getRawType()+"&lt;");
				generateDataType(mc, translated, p, ch);
				p.println ("&gt;");
			}
			else
			{
				p.println (dataType.getJavaType(translated));
			}
		}
		else  {
			if (dataType.isValueObject() || dataType.isEntity() || dataType.isEnumeration() || dataType.isService())
			{
				p.println ("<a href='"+generateRef (mc, dataType, translated)+"'>"
						+ dataType.getFullName()+"</a>");
			} else {
				p.println (dataType.getJavaType(translated));
			}
		}
	}

	private void generateMethod(AbstractModelClass referrer, boolean translated,
			PrintStream p, ModelOperation op) {
		AbstractModelClass mc = op.getModelClass();
		p.println("<li><div class='operation'><div class='operation-header'><div class='operation-name'>");
		if (referrer != null)
		{
			generateClassReference(p, referrer, mc, translated);
			p.print(".");
		}
		else
			referrer = mc;
		p.print(op.getName(translated));
		p.print("</div> <div class='operation-description'>");
		p.print(Util.formatHtml(op.getComments()));
		if (!op.getActors().isEmpty())
		{
			p.print("</div> <div class='operation-actors'>Actors: ");
			for (AbstractModelClass actor: op.getActors())
			{
				generateClassReference(p, referrer, actor, translated);
			}
		}
		p.print("</div><div class='hql'>");
		p.print(Util.formatHtml(op.getFinderQuery()));
		p.print ("</div></div><div class='params'>");
		for (ModelParameter param: op.getParameters())
		{
			p.print("<div class='param'><div class='param-name'>"+param.getName(translated)+"</div><div class='param-type'>");
			generateDataType(referrer, translated, p, param.getDataType());
			p.print("</div><div class='param-description'>");
			p.print(Util.formatHtml(param.getComments()));
			p.print("</div>");
		}
		p.print("</div><div class='return'><div class='return-name'>Returns</div><div class='return-type'>");
		generateDataType(referrer, translated, p, op.getReturnParameter().getDataType());
		p.print ("</div></li>");
	}

	
	private String getDocFile(AbstractModelClass mc, boolean translated) {
		return generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(translated))+mc.getName(translated)+".html";
	}

	private String getDaoDocFile(AbstractModelClass mc, boolean translated) {
		return generator.getUmlDir()+File.separator+Util.packageToDir(mc.getPackage(translated))+mc.getDaoName(translated)+".html";
	}

	public String generateRef(AbstractModelClass mc, AbstractModelClass ch, boolean translated) {
		return generateRef (getDocFile(mc, translated), getDocFile(ch, translated));
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
	boolean translated;
	
	public ClassName (AbstractModelClass mc, boolean translated)
	{
		this.mc = mc;
		this.translated = translated;
		pkg = mc.getPackage(translated);
		name = mc.getName(translated);
	}
}