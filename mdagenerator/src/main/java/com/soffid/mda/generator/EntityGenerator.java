package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import com.soffid.mda.parser.ModelAttribute;
import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.ModelElement;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class EntityGenerator<E> {
	final static String endl = "\n";
	
	private String modelPackage;
	private String modelDir;
	private Generator generator;
	private Parser parser;
	private boolean translated;
	
	final static String endComment = "*/";

	public void generate(Generator generator, Parser parser) throws IOException {
		this.generator = generator;
		this.parser = parser;

		translated = generator.isTranslatedOnly();
		
		if (generator.getCoreDir() == null)
			return;

		
		if (generator.isPlugin())
		{
			modelPackage = "com.soffid.iam.addons."+generator.getPluginName()+".model";
			modelDir = "com/soffid/iam/addons/"+generator.getPluginName()+"/model";
		}
		else
		{
			if (translated)
			{
				modelPackage = "com.soffid.iam.model";
				modelDir = "com/soffid/iam/model";
			}
			else
			{
				modelPackage = "es.caib.seycon.ng.model";
				modelDir = "es/caib/seycon/ng/model";
			}
		}
		for (ModelClass element: parser.getEntities())
		{
			generateEntity ((ModelClass) element);
			generateEntityDao ((ModelClass) element);
			generateEntityImpl((ModelClass) element);
			generateEntityDaoBase((ModelClass) element);
			generateEntityDaoImpl ((ModelClass) element);
			generateHibernateDescriptor((ModelClass) element);
			if (generator.isGenerateUml())
			{
				generateUml (element);
			}
		}
		generateSearchCriteria();
		generateSearchCriteriaConfiguration();
		generateSearchCriteriaParameter();
		generateSearchCriteriaProperties();
		generateTestBase();
		generateTestImpl();
	}

	private void generateEntityDaoImpl(ModelClass entity) throws FileNotFoundException, UnsupportedEncodingException {
		boolean sourceTemplate = false;
		String file;
		if ( entity.getSuperClass() == null ||
				!entity.getSuperClass().isEntity()) {
			file = generator.getCoreSrcDir();
			sourceTemplate = true;
		} else {
			file = generator.getCoreDir();
		}

		File f = new File (file + File.separator + entity.getPackageDir(translated) + entity.getName(translated)+"DaoImpl.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" + "\n"
				+ "// (C) 2013 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				);
		if (!entity.getPackagePrefix(translated).isEmpty())
			out.println ( "package " + entity.getPackage(translated) + ";" );


		out.println ( "/**" + "\n"
				+ " * DAO " + entity.getName(translated) + " implementation"+ "\n"
				+ Util.formatComments(entity.getComments())
				+ " " + endComment );
		out.println ( "public class " + entity.getDaoImplName(translated)
			+ " extends " + entity.getDaoBaseName(translated) + "\n"
			+ "{" + "\n"
			+ "}" );
		out.close();
	}

	private void generateEntityImpl(ModelClass entity) throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		if ( entity.hasNonStaticMethods()) {
			file = generator.getCoreSrcDir();
		} else {
			file = generator.getCoreDir();
		}

		File f = new File (file+ File.separator + entity.getPackageDir(translated) + entity.getName(translated)+"Impl.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println( "//" + "\n"
				+ "// (C) 2013 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				);
		if (entity.getPackage(translated) != null)
			out.println( "package " + entity.getPackage(translated) + ";" );

		out.println( "/**" + "\n"
				+ " * Entity " + entity.getName(translated) + " implementation"+ "\n"
				+ Util.formatComments(entity.getComments())
				+ " " + endComment );
		out.print( "public ");
		out.print( "class " + entity.getImplName(translated));
		out.print( " extends " + entity.getFullName(translated) );
		out.println( " {" );
		out.println( );
		//
		// Methods
		//
		for (ModelOperation op: entity.getOperations())
		{
			if (! op . isStatic() && ! op.isQuery()) {
				generateOperationComments(op, out);
				out.println( "\tpublic " + op.getPrettySpec (false) + " {" + "\n"
						+ "\t\t//TODO: Add custom implementaion" );
				if ( op.getReturnType() != "void")
					out.println( "\t\treturn null;" );
				out.println( "\t}" );

			}
		}
		out.println( "}" );
		out.close();
	}

	private void generateEntity(ModelClass element) throws FileNotFoundException, UnsupportedEncodingException {
		
		File f = new File (generator.getCoreDir() + File.separator + element.getFile (translated));
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());
		out.println("//");
		out.println("// (c) 2014 Soffid");
		out.println("//");
		out.println("//");
		out.println ();
		if (element.getPackage(translated) != null)
			out.println ("package "+element.getPackage (translated)+";");
		out.println ();
		out.println ("/**");
		out.println (" *  Entity "+element.getName(translated));
		out.print (Util.formatComments (element.getComments()));
		out.println (" " + endComment);
		out.println ();
		out.print ("public abstract class " + element.getName(translated));
		if (element.getSuperClass() != null)
		{
			out. println (" extends ");
			if (element.getSuperClass().isEntity())
				out.print (element.getSuperClass().getImplFullName(translated));
			else
				out.print (element.getSuperClass().getFullName(translated));
		}
		out.println (" {");
		out.println ();
		out.println ("\t/**\n");
		out.println ("\t * The serial version UID of this class. Needed for serialization.");
		out.println ("\t " + endComment);
		out.println ("\tprivate static final long serialVersionUID = " + element.getSerialVersion() + ";") ;
		
		for (ModelAttribute att: element.getAttributes())
		{
			out.println ("\t/**");
			out.println ("\t * Attribute " + att.getName(translated));
			out.print ( Util.formatComments(att.getComments(), "\t"));
			out.println ("\t " + endComment);
			if (att.getDataType().isCollection() && att.getDataType().getChildClass().isEntity() )
			{
				out.println ("\tprivate "+att.getDataType().getJavaType(translated)+" "+att.getName(translated)+" =  new java.util.HashSet<"
						+ att.getDataType().getChildClass().getJavaType(translated)+">();");
			} else {
				out.print ("\tprivate "+att.getDataType().getJavaType(translated)+" "+att.getName(translated));
				if (att.getDefaultValue() != null)
					out.print (" = "+att.getDefaultValue());
				out.println (";");
			}
			
			out.println ("\t/**");
			out.println ("\t * Gets value for attribute " + att.getName(translated));
			out.println ("\t " + endComment);
			out.println ("\tpublic "+att.getDataType().getJavaType(translated)+" "+att.getterName(translated)+"() {");
			out.println ("\t\treturn this."+att.getName(translated)+";");
			out.println ("\t}");
			out.println ("\t/**");
			out.println ("\t * Sets value for attribute " + att.getName(translated));
			out.println ("\t " + endComment);
			out.println ("\tpublic void "+att.setterName(translated)+"("+att.getDataType().getJavaType(translated)+" "+att.getName(translated)+") {");
			out.println ("\t\tthis."+att.getName(translated)+" = "+att.getName(translated)+";");
			out.println ("\t}");
		}
		
		// Methods
		
		for (ModelOperation op : element.getOperations())
		{
			if ( ! op.isStatic() && !op.isQuery())
			{
				generateOperationComments(op, out);
				out.println ("\t public abstract "+op.getPrettySpec(false)+";");
				out.println();
			}
		}
		
		// Equal method
		out.println( "\t/**" + "\n"
				+ "\t * Returns <code>true</code> if the argument is an "
		    + element.getName(translated) + " instance and all identifiers for this entity "+"\n"
		    + "\t * equal the identifiers of the argument entity. Returns <code>false</code> otherwise." + "\n"
		    + "\t " + endComment + "\n"
		    + "\tpublic boolean equals(Object object)" + "\n"
		    + "\t{");
		if (element.getSuperClass() != null && element.getSuperClass().isEntity()) {
			out.println( "\t\treturn super.equals(object);");
		} else {
			out.println( "\t\tif (this == object)" +"\n"
			+ "\t\t{" + "\n"
			+ "\t\t\treturn true;" + "\n"
			+ "\t\t}" + "\n"
			+ "\t\tif (!(object instanceof "+element.getName(translated)+"))"+"\n"
			+ "\t\t{" + "\n"
			+ "\t\t\treturn false;" + "\n"
			+ "\t\t}" + "\n"
			+ "\t\tfinal " + element.getName(translated) + " that = (" + element.getName(translated) + ")object;" + "\n"
			+ "\t\tif (this.id == null || that.getId() == null || !this.id.equals(that.getId())) " + "\n"
			+ "\t\t{" + "\n"
			+ "\t\t\treturn false;" + "\n"
			+ "\t\t}" + "\n"
			+ "\t\treturn true;" );
		}
	    out.println( "\t}" );

		// HashCode method

		out.println( "\t/**" + "\n"
			+ "\t * Returns a hash code based on this entity's identifiers." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic int hashCode()" + "\n"
			+ "\t{" );
		if (element.getSuperClass() != null && element.getSuperClass().isEntity()) {
			out.println( "\t\treturn super.hashCode();");
		} else {
	        out.println( "\t\tint hashCode = 0;" + "\n"
	        + "\t\thashCode = 29 * hashCode + (id == null ? 0 : id.hashCode());" + "\n"
	        + "\t\treturn hashCode;" );
		}
		out.println ( "\t}" );

		out.println ("}");
	}
	

	public void generateOperationComments (ModelOperation op, PrintStream out)
	{
		out.println ("\t/**");
		out.println ("\t * Operation "+op.getName(translated));
		out.print (Util.formatComments(op.getComments(), "\t"));
		for (ModelParameter param: op.getParameters())
		{
			out.println ("\t * @param "+param.getName(translated));
		}
		if (!op.getReturnParameter().getDataType().getJavaType(translated).equals ("void"))
		{
			out.print ("\t * @return");
			out.print (Util.formatComments(op.getReturnParameter().getComments(), "\t           "));
			out.println();
		}
		out.println ("\t*" + endComment);
	}
	
	private void generateDaoMethods  (Generator generator, PrintStream out, ModelClass entity, ModelClass subClass)
	{
		//
		// Methods
		//
		for (ModelOperation op: subClass.getOperations())
		{
			if (op.isStatic() || op.isQuery()) {
				generateOperationComments(op, out);
				out.println ("\tpublic " + op.getPrettySpec (false) + " " + op.getThrowsClause(translated) + " ;\n");
				boolean criteria = false;
				ModelClass criteriaClazz = null;
				if (op.getParameters().size() == 1)
				{
					ModelParameter param = op.getParameters().get(0);
					if (param.getDataType().isCriteria())
					{
						criteriaClazz = param.getDataType();
						criteria = true;
					}
				}
				if (op.isQuery() && ( !criteria || criteriaClazz == null ))
				{
					// Create find with CriteriaConfiguration
					out.println ("\t/**");
					out.println ("\t * CriteriaSearchConfiguration finder");
					out.println ("\t " + endComment);
					out.print ("\tpublic " + op.getReturnType(false) + " " + op.getName (translated) + "(final " +
							modelPackage + ".criteria.CriteriaSearchConfiguration criteria");
					for (ModelParameter param: op.getParameters())
					{
						out.print( ", " + param.getDataType().getJavaType(false) + " " + param.getName(translated));

					}
					out.println ( ")\n\t" + op.getThrowsClause(translated) + ";" );
				}

			}
		}


		//
		// Value Objects dependencies
		//
		for (ModelClass cl: subClass.getDepends())
		{
			if (cl.isValueObject())
			{
				
				out.println ( "\t/**" + "\n"
						+ "\t *  Copy data to {@link " + cl.getFullName(translated) + "} object " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic void to" + cl.getName (translated)
						+ "(" + subClass.getFullName(translated)
						+ " source, "
						+ cl.getFullName(translated)
						+ " target) ;" + "\n" );
				out.println ( "\t/**" + "\n"
						+ "\t *  Transforms to {@link " + cl.getFullName(translated) + "} object " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic " + cl.getFullName(translated)
						+ " to" + cl.getName (translated)
						+ "(" + subClass.getFullName(translated)
						+ " entity) ;" + "\n" );
				out.println ( "\t/**" + "\n"
						+ "\t *  Transforms to {@link " + cl.getFullName(translated) + "} list " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic java.util.List<" + cl.getFullName(translated) + "> "
						+ "to" + cl.getName(translated) + "List "
						+ "(java.util.Collection<" + subClass.getFullName(translated)
						+ "> entities) ;" + "\n" );

				out.println ( "\t/**" + "\n"
						+ "\t *  Copy data from {@link " + cl.getFullName(translated) + "} object " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic void "+ Util.firstLower(cl.getName(translated))
						+ "ToEntity ("
						+ cl.getFullName(translated)
						+ " source, "
						+ subClass.getFullName(translated)
						+ " target, boolean copyIfNull) ;" + "\n" );
				out.println ( "\t/**" + "\n"
						+ "\t *  Transforms from {@link " + cl.getFullName(translated) + "} object " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic " + subClass.getFullName(translated)
						+ " " + Util.firstLower(cl.getName(translated)) + "ToEntity ("
						+ cl.getFullName(translated)
						+ " instance) ;" + "\n" );
				out.println ( "\t/**" + "\n"
						+ "\t *  Transforms from {@link " + cl.getFullName(translated) + "} list " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic java.util.List<" + subClass.getFullName(translated) + "> "
						+ " " + Util.firstLower(cl.getName(translated)) + "ToEntityList "
						+ "(java.util.Collection<" + cl.getFullName(translated)
						+ "> instances) ;" + "\n" );
			}
		}

		out.println ( "\t/**" + "\n"
				+ "\t * Creates an instance of {@link " + subClass.getFullName(translated)
				+ "} ." + "\n"
				+ "\t " + endComment );
		out.println ( "\tpublic " + subClass.getFullName(translated) + " new"
				+ subClass.getName(translated)
				+ "();" + "\n" );

		//
		// Subclasses
		//
		int i = 0;
		for (ModelClass cl: subClass.getSpecializations() )
		{
			
			out.println ( "\n" + "\t// " + cl.getName(translated) + " methods" );
			out.println ("\t// Specialization " + (i++) + " of "+subClass.getName(translated));
			out.println ();
			generateDaoMethods(generator, out, entity, cl);
		}
	}

	public void generateEntityDao(ModelClass entity) throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File (generator.getCoreDir() + File.separator + entity.getPackageDir(translated) + entity.getName(translated)+"Dao.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" + "\n"
				+ "// (C) 2013 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				);
		if (!entity.getPackagePrefix(translated).isEmpty())
			out.println ( "package " + entity.getPackage(translated) + ";" );

		out.println ( "/**" + "\n"
				+ " * DAO for Entity " + entity.getName(translated) + "\n"
				+ Util.formatComments(entity.getComments())
				+ " " + endComment );
		out.println ( "public interface " + entity.getDaoName(translated));
		if (entity.getSuperClass() != null && entity.getSuperClass().isEntity()) {
			out.println ( " extends "
				+ entity.getSuperClass().getDaoFullName(translated)
				+ "{" + "\n"
				+ "}" );
		} else {
			ModelAttribute id = entity.getIdentifier();
			out.println ( "\n" + "{" );
			generateDaoMethods(generator, out, entity, entity);
			// create, update remove
			out.println ( "\t/**" + "\n"
					+ "\t * Adds an instance of {@link " + entity.getFullName(translated)
					+ "} andto the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void create ("+ entity.getFullName(translated)
					+ " entity);" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Updates an instance of {@link " + entity.getFullName(translated)
					+ "} at the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void update ("+ entity.getFullName(translated)
					+ " entity);" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Removes an instance of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void remove ("+ entity.getFullName(translated)
					+ " entity);" + "\n" );

			// load
			out.println ( "\t/**" + "\n"
					+ "\t * Loads an instance of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic "+ entity.getFullName(translated)
					+ " load("
					+ id.getDataType().getJavaType(translated) + " " + id.getName(translated) + ");" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Loads all instances of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );

			// create, update, remove lists
			out.println ( "\tpublic java.util.List<"+ entity.getFullName(translated)
					+ "> loadAll();" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Creates a collection of {@link " + entity.getFullName(translated)
					+ "} and adds it to the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void create (java.util.Collection<? extends "+ entity.getFullName(translated)
					+ "> entities);" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Updates a collection of {@link " + entity.getFullName(translated)
					+ "} in the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void update (java.util.Collection<? extends "+ entity.getFullName(translated)
					+ "> entities);" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Removes a collection of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void remove (java.util.Collection<? extends "+ entity.getFullName(translated)
					+ "> entities);" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Removes an instance of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void remove (" + id . getDataType().getJavaType(translated) + " id);" + "\n" );

			// Query

			out.println ( "\t/**" + "\n"
					+ "\t * Query of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t * parameter query HQL Query String" + "\n"
					+ "\t * parameter parameters HQL Parameters" + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic java.util.List<" + entity.getFullName(translated)
					+ "> query (String query, "+generator.getRootPkg()+".model.Parameter[] parameters);"
					+ "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Query of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t * parameter query HQL Query String" + "\n"
					+ "\t * parameter parameters HQL Parameters" + "\n"
					+ "\t * parameter maxResults max number of rows to return" + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic java.util.List<" + entity.getFullName(translated)
					+ "> query (String query, "+generator.getRootPkg()+".model.Parameter[] parameters, " + modelPackage + ".criteria.CriteriaSearchConfiguration criteria);"
					+ "\n" );
			out.println ( "}" );
		}
		out.close();
	}
 
	void generateTestBase () throws FileNotFoundException, UnsupportedEncodingException
	{
		File f = new File (generator.getCoreTestSrcDir() + "/com/soffid/test/AbstractHibernateTest.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" + "\n"
				+ "// (C) 2013 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				 );
		out.println ( "package com.soffid.test; " + "\n"
			+ "import org.hibernate.SessionFactory;" + "\n"
			+ "import org.hibernate.classic.Session;" + "\n"
			+ "import org.hibernate.dialect.HSQLDialect;" + "\n"
			+ "import org.springframework.beans.factory.access.BeanFactoryReference;" + "\n"
			+ "import org.springframework.context.ApplicationContext;" + "\n"
			+ "import "+generator.getRootPkg()+".model.CustomDialect;" + "\n"
			+ "import junit.framework.TestCase; " + "\n"
			+ "\n"
			+ "public abstract class AbstractHibernateTest extends TestCase" + "\n"
			+ "{" + "\n"
			+ "\tprivate BeanFactoryReference beanFactoryReference;" + "\n"
			+ "\tprotected ApplicationContext context;" + "\n"
			+ "\tprotected Session session;" + "\n"
			+ "\tprivate SessionFactory sf;" + "\n" + "\n"
			+ "\t@Override" + "\n"
			+ "\tprotected void setUp() throws Exception" + "\n"
			+ "\t{" + "\n"
			+ "\t\tsuper.setUp();" + "\n"
			+ "\t\tCustomDialect.dialectClass = HSQLDialect.class;" + "\n"
			+ "\t\torg.springframework.beans.factory.access.BeanFactoryLocator beanFactoryLocator =" + "\n"
			+ "\t\torg.springframework.context.access.ContextSingletonBeanFactoryLocator.getInstance(\"testBeanRefFactory.xml\");" + "\n"
			+ "\t\tthis.beanFactoryReference = beanFactoryLocator.useBeanFactory(\"beanRefFactory\");" + "\n"
			+ "\t\tcontext = (org.springframework.context.ApplicationContext)this.beanFactoryReference.getFactory();" + "\n"
			+ "\t\tsf = (SessionFactory) context.getBean(\"sessionFactory\");" + "\n"
			+ "\t\tsession = sf.openSession();" + "\n"
			+ "\t}" + "\n" + "\n"
			+ "\t@Override" + "\n"
			+ "\tprotected void tearDown() throws Exception" + "\n"
			+ "\t{" + "\n"
			+ "\t\tsession.close();" + "\n"
			+ "\t\tsf.close();" + "\n"
			+ "\t}" + "\n"
			+ "}"  );
		out.close();
	}

	void generateTestImpl () throws FileNotFoundException, UnsupportedEncodingException
	{
		File f = new File (generator.getCoreTestSrcDir() + "/com/soffid/test/HibernateTest.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());


		out.println( "//" + "\n"
				+ "// (C) 2013 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				);
		out.println( "package com.soffid.test; " + "\n"
			+ "import org.hibernate.SessionFactory;" + "\n"
			+ "import org.hibernate.classic.Session;" + "\n"
			+ "import org.hibernate.dialect.HSQLDialect;" + "\n"
			+ "import org.springframework.beans.factory.access.BeanFactoryReference;" + "\n"
			+ "import org.springframework.context.ApplicationContext;" + "\n"
			+ "import "+generator.getRootPkg()+".model.CustomDialect;" + "\n"
			+ "import junit.framework.TestCase; " + "\n"
			+ "\n"
			+ "public class HibernateTest extends AbstractHibernateTest" + "\n"
			+ "{" );

		int numTest = 1;
		for (ModelElement element: parser.getEntities())
		{
			if (element instanceof ModelClass)
			{
				ModelClass entity = (ModelClass) element; 
				for (ModelOperation op: entity.getOperations())
				{
					if (op.isQuery())
					{
						String sqlString;

						sqlString = op.getFinderQuery ();;
						if (!sqlString.isEmpty ())
						{
							out.println( "\tpublic void test" + numTest + "_" + entity.getName(translated)+"_" + op.getName(translated) + " () throws Exception" + "\n"
								+ "\t{" + "\n"
								+ "\t\torg.hibernate.Query q=session.createQuery(\""
									+ Util.formatString(sqlString)
									+ "\");" );
							for (ModelParameter param: op.getParameters())
							{
								out.print( "\t\tq.setParameter(\"" 
									+ param.getName(translated) + "\", " ); 
								String type = param.getDataType().getJavaType(false);
							  	if (type.equals( "Long")|| type.equals( "java.lang.Long"))
									out.print( "new Long(0)" );
							  	else if (type.equals("String") || type.equals( "java.lang.String"))
									out.print( "\"\"" );
							  	else if (type.equals("Integer") || type.equals( "java.lang.Integer"))
									out.print( "new Integer(0)");
							  	else if (type.equals("Boolean") || type.equals("java.lang.Boolean"))
									out.print( "Boolean.FALSE" );
							  	else if (type.equals( "Date" ) || type.equals( "java.util.Date") )
									out.print( "new java.util.Date()" );
							  	else if (type.equals( "int"))
									out.print( "0" );
							  	else if (type.equals("long"))
									out.print( "0L" );
							  	else if (type.equals ("boolean"))
									out.print( "false");
							  	else 
									out.print( "null" );
								out.println( ");" );
							}

							out.println( "\t}" + "\n" );
							numTest ++;
						}
					}
				}
			}

		}

		out.println( "\tpublic void testDummy() throws Exception" + "\n"
		    + "\t{" + "\n"
		    + "\t}" +"\n");
		out.println( "}" );
		out.close();
	}

	void generateDependency(ModelClass provider, PrintStream out) {
		if (provider != null && (provider.isEntity() || provider.isService())) {
			String fullName;
			String name;
			String varName;
			if (provider.isEntity()) {
				name = provider.getDaoName(translated);
				fullName = provider.getDaoFullName(translated);
				varName = provider.getVarName() + "Dao";
			} else {
				name = provider.getName(translated);
				fullName = provider.getFullName(translated);
				varName = provider.getVarName();
			}
			out.println ( "\t" + fullName + " " + varName + ";" + "\n" );
			out.println ( "\t/**" + "\n" + "\t * Sets reference to <code>" + varName
					+ "</code>." + "\n" + "\t " + endComment + "\n" + "\tpublic void set"
					+ name + " (" + fullName + " " + varName + ") {" );
			out.println ( "\t\tthis." + varName + " = " + varName + ";" );
			out.println ( "\t}" + "\n" );
			out.println ( "\t/**" + "\n" + "\t * Gets reference to <code>" + varName
					+ "</code>." + "\n" + "\t " + endComment + "\n" + "\tpublic "
					+ fullName + " get" + name + " () {" );
			out.println ( "\t\treturn " + varName + ";" );
			out.println ( "\t}" + "\n" );
		}
	}

	void generateDependencies (ModelClass entity, PrintStream out)
	{
		//
		// Generate clients
		//

		for (ModelClass provider: entity.getDepends()) {
			generateDependency(provider, out);

		}
		out.println ();
	}



	void generateFinderMethod  (PrintStream out, ModelClass entity,
			ModelOperation op) {
		String sqlString;

		// GUESS HQL STRING
		sqlString = op.getFinderQuery();
		if (sqlString.isEmpty ())
		{
			sqlString = "from " + entity.getFullName(translated);
			boolean first = true;
			for (ModelParameter param: op.getParameters()) {
				if (first) {
					sqlString += " where ";
					first = false;
				} else
					sqlString += " and ";
				sqlString += param.getName(translated);
				sqlString += "=:";
				sqlString += param.getName(translated);
			}
		} else {
			sqlString = Util.formatString(sqlString);
		}


		// Test if it is a Criteria Method
		boolean criteria = false;
		ModelParameter criteriaParam = null;
		ModelClass criteriaClazz = null;
		ModelParameter result = op.getReturnParameter();
		if (op.getParameters().size() == 1)
		{
			for (ModelParameter param: op.getParameters()) {
				if (param.getDataType().isCriteria()) {
					criteriaParam = param;
					criteriaClazz = param.getDataType();
					criteria = true;
				}
			}
		}

		if (criteria && criteriaClazz != null) {
			// CREATE FINDER METHOD
			generateOperationComments(op, out);

			out.println ( "\tpublic " + op.getPrettySpec (false) + " " + op.getThrowsClause(translated) + "\n" + "\t{" + "\n"
					+ "\t\ttry {" + "\n"
					+ "\t\t\t" + modelPackage + ".criteria.CriteriaSearch criteriaSearch = new " + modelPackage + ".criteria.CriteriaSearch("+ "\n"
					+ "\t\t\t\tsuper.getSession(false), " + entity.getFullName(false) + ".class);" + "\n"
					+ "\t\t\tcriteriaSearch.getConfiguration().setFirstResult(" + criteriaParam.getName(translated)+ ".getFirstResult());" + "\n"
					+ "\t\t\tcriteriaSearch.getConfiguration().setFetchSize(" + criteriaParam.getName(translated)+ ".getFetchSize());" + "\n"
					+ "\t\t\tcriteriaSearch.getConfiguration().setMaximumResultSize(" + criteriaParam.getName(false)+ ".getMaximumResultSize());" );
			int i = 0;
			for ( ModelAttribute att: criteriaClazz.getAttributes()) {
				i++;
				String tab = "";
				if (! att.getDataType().isPrimitive()) {
					out.println ( "\t\t\tif (" + criteriaParam.getName(false) + "." + att.getterName(false) + "() != null) {" );
					tab = "\t";
				}
				String comparator = att.getCriteriaComparator();
				String parameter = att.getCriteriaParameter();
				if (comparator == null || comparator.isEmpty())
					comparator = "LIKE_COMPARATOR";
				if (parameter == null || parameter.isEmpty())
					parameter = att.getName(translated);

				String suffix = "";
				String prefix = "";
				if (att.getDataType().getJavaType(translated).equals( "java.util.Calendar"))
					suffix = ".getTime()";
				if (att.getDataType().getJavaType(translated).equals("java.sql.Timestamp"))
				{
					prefix = "new java.util.Date(";
					suffix = ".getTime())";
				}

				out.println ( tab + "\t\t\t" + modelPackage + ".criteria.CriteriaSearchParameter param" + i + " =" + "\n"
						+ tab + "\t\t\t\tnew " + modelPackage + ".criteria.CriteriaSearchParameter(" + "\n"
						+ tab + "\t\t\t\t\t" + prefix  + criteriaParam.getName(translated) + "." + att.getterName(translated) + "()" + suffix + "," + "\n"
						+ tab + "\t\t\t\t\t\"" + parameter + "\", "
						+ modelPackage + ".criteria.CriteriaSearchParameter." + comparator + ");" + "\n"
						+ tab + "\t\t\tcriteriaSearch.addParameter(param"+i+");" );
				if (! att.getDataType().isPrimitive()) {
					out.println ( "\t\t\t}" );
				}
			}
			out.println ( "\t\t\tjava.util.List results = criteriaSearch.executeAsList();" + "\n"
					+ "\t\t\treturn ("+op.getReturnType(false)+") results;" + "\n"
					+ "\t\t}" + "\n"
					+ "\t\tcatch (org.hibernate.HibernateException ex)" + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\tthrow super.convertHibernateAccessException(ex);" + "\n"
					+ "\t\t}" + "\n"
					+ "\t}" );
		} else {
			// CREATE FINDER METHOD
			generateOperationComments(op, out);

			out.print ( "\tpublic " + op.getPrettySpec (false) + " " + op.getThrowsClause(translated) + "\n" + "\t{" + "\n"
				+ "\t\treturn " + op.getName(false) + "((" + modelPackage + ".criteria.CriteriaSearchConfiguration) null"
				+ "\t\t\t" );
			for (ModelParameter param: op.getParameters()) {
				out.print ( ", " + param.getName(false) );
			}
			out.println ( ");" + "\n"
					+ "\t}" );


			// Create find with CriteriaConfiguration
			out.println ( "\t/**" + "\n"
					+ "\t * CriteriaSearchConfiguration implementation" + "\n"
					+ "\t " + endComment );

			out.print ( "\tpublic " + op.getReturnType(false) + " " + op.getName (false) + "(final " + modelPackage + ".criteria.CriteriaSearchConfiguration criteria" );
			for (ModelParameter param: op.getParameters()) {
				out.print ( ", " + param.getDataType().getJavaType(false)+" " + param.getName(false) );

			}
			out.print ( ")" + "\n" + "\t"+op.getThrowsClause(translated) + "\n"
				+"\t{" + "\n"
				+ "\t\treturn " + op.getName(translated) + "(\"" + sqlString
				+ "\"," + "\n" + "\t\t\tcriteria" );
				for (ModelParameter param: op.getParameters()) {
					out.print ( ", " + param.getName(translated));
				}
			out.println ( ");" + "\n"
				+ "\t}" );

			// CREATE DEFAULT FINDER IMPLEMENTATION METHOD
			out.println ( "\t/**" + "\n"
					+ "\t * Internal implementation" + "\n"
					+ "\t " + endComment );

			out.print ( "\tpublic " + op.getReturnType(false) + " " + op.getName (false) + "(final java.lang.String queryString, " + modelPackage + ".criteria.CriteriaSearchConfiguration criteria");
			for (ModelParameter param: op.getParameters()) {
				out.print ( ", " + param.getDataType().getJavaType(false)+" " + param.getName(false));

			}
			out.println ( ")" + "\n" + "\t"+op.getThrowsClause(translated) + "\n"
					+"\t{" + "\n"
					+ "\t\ttry" + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\torg.hibernate.Query queryObject = super.getSession(false).createQuery(queryString);" );
			for (ModelParameter param: op.getParameters()) {
				out.println ( "\t\t\tqueryObject.setParameter(\"" + param.getName(translated)+"\", "+param.getName(translated)+ ");" );

			}
			out.println ( "\t\t\tif (criteria != null && criteria.getMaximumResultSize () != null) {" + "\n"
					+ "\t\t\t\tqueryObject.setMaxResults (criteria.getMaximumResultSize ().intValue()); " + "\n"
					+ "\t\t\t}" );
			if (! op.getReturnParameter().getDataType().isCollection()) {
				out.println ( "\t\t\tjava.util.Set results = new java.util.LinkedHashSet(queryObject.list());" + "\n"
						+ "\t\t\t" + result . getDataType().getJavaType(false) + " result = null;" + "\n"
						+ "\t\t\tif (results.size() > 1) {" + "\n"
						+ "\t\t\t\tthrow new org.springframework.dao.InvalidDataAccessResourceUsageException("+"\n"
						+ "\t\t\t\t\t\"More than one instance of '" + result.getDataType().getJavaType(false)
						+ "' was found when executing query --> '\" + queryString + \"'\");" + "\n"
						+ "\t\t\t}" + "\n"
						+ "\t\t\telse if (results.size() == 1)" + "\n"
						+ "\t\t\t{" + "\n"
						+ "\t\t\t\tresult = ("+ result.getDataType().getJavaType(false) + ") results.iterator().next();" + "\n"
						+ "\t\t\t}" + "\n"
						+ "\t\t\treturn result;" );
			} else {
				out.println ( "\t\t\tjava.util.List results = queryObject.list();" + "\n"
						+ "\t\t\treturn ("+op.getReturnType(false)+") results;" );
			}

			out.println ( "\t\t}" + "\n"
				+ "\t\tcatch (org.hibernate.HibernateException ex) " + "\n"
				+ "\t\t{" + "\n"
				+ "\t\t\tthrow super.convertHibernateAccessException(ex);" + "\n"
				+ "\t\t}" + "\n"
				+ "\t}" );
		}
	}


	void generateCopy (PrintStream out,
			String prefix,
			String srcName, ModelAttribute source,
			String targetName, ModelAttribute target) {
		String result;
		if (source.getDataType().getJavaType(translated).equals ( target.getDataType().getJavaType(translated)))
		{
			out.println ( prefix + targetName + "." + target.setterName(translated) + "("
					+ srcName + "." + source.getterName(translated) + "());" );
		} else if ( source.getDataType().getJavaType(translated).equals("java.util.Date") &&
				target.getJavaType(translated).equals("java.util.Calendar")) {
			out.println ( prefix + "if ("+srcName+"."+source.getterName(translated)+"() == null) {" + "\n"
					+ prefix + "\t"+targetName+"."+target.setterName(translated)+"(null);" + "\n"
					+ prefix + "} else {" +"\n"
					+ prefix + "\t"+targetName+"."+target.setterName(translated)+"(java.util.Calendar.getInstance());" + "\n"
					+ prefix + "\t"+targetName+"."+target.getterName(translated)+"().setTime("
						+ srcName+"."+source.getterName(translated)+"());"+ "\n"
					+ prefix + "}" );
		} else if ( target.getJavaType(translated).equals ("java.util.Date") &&
				source.getJavaType(translated).equals ("java.util.Calendar")) {
			out.println ( prefix + "if ("+srcName+"."+source.getterName(translated)+"() == null) {" + "\n"
					+ prefix + "\t"+targetName+"."+target.setterName(translated)+"(null);" + "\n"
					+ prefix + "} else {" +"\n"
					+ prefix + "\t"+targetName+"."+target.setterName(translated)+"("
						+ srcName+"."+source.getterName(translated)+"().getTime());"+ "\n"
					+ prefix + "}" );
		} else if ( target.getJavaType(translated).equals("java.lang.Boolean") &&
				source.getJavaType(translated).equals("boolean")) {
			out.println ( prefix + targetName+"."+target.setterName(translated)+"(new java.lang.Boolean("
						+ srcName+"."+source.getterName(translated)+"()));");
		} else if ( target.getJavaType(translated).equals("boolean") &&
				source.getJavaType(translated).equals("java.lang.Boolean")){
			out.println ( prefix + targetName+"."+target.setterName(translated)+"(java.lang.Boolean.TRUE.equals("
						+ srcName+"."+source.getterName(translated)+"()));");
		} else {
			out.println ( prefix + "// Incompatible types "+srcName+"."+source.getName(translated)
					+" and "+targetName+"."+target.getName(translated) );
		}
	}

	void generateCopyObject (PrintStream out,
			String srcName, ModelClass source,
			String targetName, ModelClass target) {
		ModelClass baseTarget = target;
		do
		{
			out.println ( "\t\t// Attributes for " + baseTarget.getName(translated) );
			for (ModelAttribute targetAttribute: baseTarget.getAttributes()) {
				ModelAttribute sourceAttribute = null;
				ModelClass baseClass = source;
				do
				{
					for (ModelAttribute att: baseClass.getAttributes())
					{
						if (att.getName(translated).equals ( targetAttribute.getName(translated)) )
						{
							sourceAttribute = att;
							break;
						}
					}
					baseClass = baseClass . getSuperClass();
				} while (sourceAttribute == null && baseClass != null);

				if (sourceAttribute == null)
					out.println ( "\t\t// Missing attribute " + targetAttribute.getName(translated) + " on entity" );
				else
				{
					generateCopy(out, "\t\t", srcName, sourceAttribute, targetName, targetAttribute);
				}
			}
			baseTarget = baseTarget.getSuperClass();
		} while (baseTarget != null);

	}

	void generateCopyEntity (PrintStream out,
			String srcName, ModelClass source,
			String targetName, ModelClass target) {
		ModelClass baseTarget = target;
		do
		{
			out.println ( "\t\t// Attributes for " + baseTarget.getName(translated) );
			ModelAttribute id = target.getIdentifier();
			for (ModelAttribute targetAttribute: baseTarget.getAttributes()) {
				if (targetAttribute != id && ! targetAttribute.getName(translated).isEmpty()) {
					ModelAttribute sourceAttribute = null;
					ModelClass baseClass = source;
					do
					{
						for (ModelAttribute att: baseClass.getAttributes())
						{
							if (att.getName(translated).equals (targetAttribute.getName(translated)))
							{
								sourceAttribute = att;
								break;
							}
						}
						baseClass = baseClass . getSuperClass();
					} while (sourceAttribute == null && baseClass != null);
					if (sourceAttribute == null) {
						out.println ( "\t\t// Missing attribute " + targetAttribute.getName(translated) + " on entity" );
					} else
					{
						String type = sourceAttribute.getJavaType(translated);
						if (sourceAttribute.getDataType().isPrimitive())
							generateCopy(out, "\t\t", srcName, sourceAttribute, targetName, targetAttribute);
						else
						{
							out.println ( "\t\tif (copyIfNull || " + srcName + "."
									+ sourceAttribute.getterName(translated) + "() != null)" + "\n"
									+ "\t\t{" );
							generateCopy(out, "\t\t\t", srcName, sourceAttribute, targetName, targetAttribute);
							out.println ( "\t\t}" );
						}
					}
				}
			}
			baseTarget = baseTarget.getSuperClass();
		} while (baseTarget != null);

	}

	void generateBusinessMethod (PrintStream out, ModelClass service, ModelOperation op) {
		out.println ( "\t/**" + "\n"
				+ "\t * @see " + service.getFullName(translated) + "#"
				+ op.getSpec(false) + "\n"
				+ "\t " + endComment );
		out.println ( "\tpublic " + op.getPrettySpec (false) );
		String throwsClause = op.getThrowsClause(translated);
		if (!throwsClause.isEmpty())
			out.println ( "\t\t" + throwsClause );
		out.println ( "\t{" );
		for (ModelParameter param: op.getParameters())
		{
			if (param.isRequired()) {
				if (param.getDataType().isCollection()) {
					out.println ( "\t\tif ("+param.getName(false)
							+ " == null || "
							+ param.getName(translated)
							+ ".isEmpty()) {" + "\n"
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(false) + " - "
							+ param.getName(translated)
							+ " cannot be empty\");" + "\n"
							+ "\t\t}" );
				} else if (param.getDataType().isArray()) {
					out.println ( "\t\tif ("+param.getName(false)
							+ " == null || "
							+ param.getName(false)
							+ ".length == 0) {" + "\n"
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(false) + " - "
							+ param.getName(translated)
							+ " cannot be null\");" + "\n"
							+ "\t\t}" );
				
				} else if (param.getDataType().isString()) {
					out.println ( "\t\tif ("+param.getName(false)
							+ " == null || "
							+ param.getName(false)
							+ ".trim().length() == 0) {" + "\n"
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(false) + " - "
							+ param.getName(translated)
							+ " cannot be null\");" + "\n"
							+ "\t\t}" );
				}
				else if (! param.getDataType().isPrimitive())
				{
					out.println ( "\t\tif ("+param.getName(false)
							+ " == null) {" + "\n"
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(false) + " - "
							+ param.getName(translated)
							+ " cannot be null\");" + "\n"
							+ "\t\t}" );
					ModelClass modelClass = param.getDataType();
					if ( modelClass != null && param.getDataType().isValueObject()) {
						for (ModelAttribute at: modelClass.getAttributes())
						{
							if (at.isRequired())
							{
								if (at.getDataType().isCollection()) {
									out.println ( "\t\tif ("+param.getName(translated)+"."+at.getterName(translated)
											+ "() == null || "
											+ param.getName(translated)+"."+at.getterName(translated)
											+ "().isEmpty()) {" + "\n"
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(false) + " - "
											+ param.getName(translated)+"." +at.getName(translated)
											+ " cannot be empty\");" + "\n"
											+ "\t\t}" );
								} else if (param.getDataType().isString()) {
									out.println ( "\t\tif ("+param.getName(translated)+"."+at.getterName(translated)
											+ "() == null || "
											+ param.getName(translated)+"."+at.getterName(translated)
											+ "().trim().length() == 0) {" + "\n"
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(false) + " - "
											+ param.getName(translated)+"." +at.getName(translated)
											+ " cannot be null\");" + "\n"
											+ "\t\t}" );
								} else if (! param.getDataType().isPrimitive())
								{
									out.println ( "\t\tif ("+param.getName(translated)+"."+ at.getterName(translated)
											+ "() == null ) {" + "\n"
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(false) + " - "
											+ param.getName(translated)+"." +at.getName(translated)
											+ " cannot be null\");" + "\n"
											+ "\t\t}" );
								}
							}
						}
					}
				}
			}
		}
		out.println ( "\t\ttry" + "\n"
				+ "\t\t{" );
		if (op.getReturnParameter().getDataType().isVoid())
			out.println ( "\t\t\t" + op.getImplCall(false) + ";" );
		else
			out.println ( "\t\t\treturn " + op.getImplCall(false) + ";" );
		out.println ( "\t\t}" + "\n"
				+ "\t\tcatch ("+generator.getRootPkg()+".exception.InternalErrorException __internalException)" + "\n"
				+ "\t\t{" + "\n"
				+ "\t\t\tthrow __internalException;" );
		for (ModelClass exception: op.getExceptions())
		{
			if (! exception.getFullName(translated).equals(""+generator.getRootPkg()+".exception.InternalErrorException"))
				out.println ( "\t\t}" + "\n"
						+ "\t\tcatch (" + exception.getFullName(translated) + " ex)" + "\n"
						+ "\t\t{" + "\n"
						+ "\t\t\tthrow ex;" );
		}
		out.println ( "\t\t}" + "\n"
				+ "\t\tcatch (Throwable th)" + "\n"
				+ "\t\t{" + "\n"
				+ "\t\t\torg.apache.commons.logging.LogFactory.getLog(" + service.getFullName(translated) + ".class)." + "\n"
				+ "\t\t\t\twarn (\"Error on " + service.getName(translated) + "." + op.getName(translated) + "\", th);" + "\n"
				+ "\t\t\tthrow new "+generator.getRootPkg()+".exception.InternalErrorException(" + "\n"
				+ "\t\t\t\t\"Error on " + service.getName(translated) + "." + op.getName(translated) + ": \"+th.toString(), th);" + "\n"
				+ "\t\t}" + "\n"
				+ "\t}" + "\n" );
		out.println ( "\tprotected abstract " + op.getImplSpec(false) + " throws Exception;" + "\n" );
	}

	void generateDaoBaseMethods  (PrintStream out,
			ModelClass entity,
			ModelClass subClass) {
		//
		// Methods
		//
		for (ModelOperation op: subClass.getOperations())
		{
			if (op.isStatic() || op.isQuery()) {
				if (op.isQuery()) {
					generateFinderMethod(out, entity, op);
				} else {
					// NOT Finder method
					generateBusinessMethod (out, entity, op);
				}
			}
		}


		//
		// Value Objects
		//
		for (ModelClass cl: subClass.getDepends())
		{
			if (cl != null && cl . isValueObject()) {
				// Copy to Value Object
				out.println ( "\t/**" + "\n"
						+ "\t *  Copy data to {@link " + cl.getFullName(translated) + "} object " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic void to" + cl.getName(translated)
						+ "(" + subClass.getFullName(translated)
						+ " source, "
						+ cl.getFullName(translated)
						+ " target) {" );
				generateCopyObject (out, "source", subClass, "target", cl);
				out.println ( "\t}" + "\n" );

				// Creates value object
				out.println ( "\t/**" + "\n"
						+ "\t *  Transforms to {@link " + cl.getFullName(translated) + "} object " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic " + cl.getFullName(translated)
						+ " to" + cl.getName(translated)
						+ "(" + subClass.getFullName(translated)
						+ " entity) {" + "\n"
						+ "\t\tfinal " + 	cl.getFullName(translated) + " target = new " + cl.getFullName(translated)+ "();" + "\n"
						+ "\t\tthis.to"+cl.getName(translated) + "(entity, target);" + "\n"
						+ "\t\treturn target;" + "\n"
						+ "\t}" + "\n" );

				// Creates value object list

				out.println ( "\t/**" + "\n"
						+ "\t *  Transforms to {@link " + cl.getFullName(translated) + "} list " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic java.util.List<" + cl.getFullName(translated) + "> "
						+ "to" + cl.getName(translated) + "List "
						+ "(java.util.Collection<" + subClass.getFullName(translated)
						+ "> instances) {" + "\n"
						+ "\t\tif (instances == null)" + "\n"
						+ "\t\t\treturn null;" + "\n"
						+ "\t\telse {" + "\n"
						+ "\t\t\tjava.util.LinkedList<"+ cl.getFullName(translated) + "> list =" + "\n"
						+ "\t\t\t\tnew java.util.LinkedList<" + cl.getFullName(translated) + ">();" + "\n"
						+ "\t\t\tfor (final " + subClass.getFullName(translated) + " instance: instances)" + "\n"
						+ "\t\t\t{" + "\n"
						+ "\t\t\t\tlist.add( to" + cl.getName(translated) + "(instance));" + "\n"
						+ "\t\t\t}" + "\n"
						+ "\t\t\treturn list;" + "\n"
						+ "\t\t}" + "\n"
						+ "\t}" );

				// Copy to entity
				out.println ( "\t/**" + "\n"
						+ "\t *  Copy data from {@link " + cl.getFullName(translated) + "} object " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic void "+ Util.firstLower(cl.getName(translated))
						+ "ToEntity ("
						+ cl.getFullName(translated)
						+ " source, "
						+ subClass.getFullName(translated)
						+ " target, boolean copyIfNull) {" );
				generateCopyEntity (out, "source", cl, "target", subClass);
				out.println ( "\t}" + "\n" );

				// Create entity from value object
				ModelAttribute entityPk = entity.getIdentifier();
				ModelAttribute voPk = null;
				for (ModelAttribute att: cl.getAttributes()) {
					if (att.getName(translated).equals(entityPk.getName(translated))) {
						voPk = att;
						break;
					}
				}
				if (voPk != null) {
					out.println ( "\t/**" + "\n"
							+ "\t *  Transforms from {@link " + cl.getFullName(translated) + "} object " + "\n"
							+ "\t " + endComment );
					out.println ( "\tpublic " + subClass.getFullName(translated)
							+ " " + Util.firstLower(cl.getName(translated)) + "ToEntity ("
							+ cl.getFullName(translated)
							+ " instance) {" + "\n"
							+ "\t\t"+subClass.getFullName(translated)+ " entity;"+"\n"
							+ "\t\tif (instance." + voPk.getterName(translated) + "() == null) " + "\n"
							+ "\t\t\tentity = new" + subClass.getName(translated) + "();" + "\n"
							+ "\t\telse" + "\n"
							+ "\t\t\tentity = load(instance." + voPk.getterName(translated) + "());" + "\n"
							+ "\t\t" + Util.firstLower(cl.getName(translated)) + "ToEntity(instance, entity, true);" + "\n"
							+ "\t\treturn entity;" + "\n"
							+ "\t}" + "\n" );
				}
				// Transforms list of objects to list of entities
				out.println ( "\t/**" + "\n"
						+ "\t *  Transforms from {@link " + cl.getFullName(translated) + "} list " + "\n"
						+ "\t " + endComment );
				out.println ( "\tpublic java.util.List<" + subClass.getFullName(translated) + "> "
						+ " " + Util.firstLower(cl.getName(translated)) + "ToEntityList "
						+ "(java.util.Collection<" + cl.getFullName(translated)
						+ "> instances) " + "\n"
						+ "\t{" + "\n"
						+ "\t\tif (instances == null)" + "\n"
						+ "\t\t\treturn null;" + "\n"
						+ "\t\tjava.util.LinkedList<" + subClass.getFullName(translated) + "> list =" + "\n"
						+ "\t\t\tnew java.util.LinkedList<"+subClass.getFullName(translated) + ">();" + "\n"
						+ "\t\tfor ("+cl.getFullName(translated)+" instance: instances)" + "\n"
						+ "\t\t{" + "\n"
						+ "\t\t\tlist.add (" + Util.firstLower(cl.getName(translated)) + "ToEntity(instance));" + "\n"
						+ "\t\t}" + "\n"
						+ "\t\treturn list;" + "\n"
						+ "\t}" + "\n" );
			}
		}

		out.println ( "\t/**" + "\n"
				+ "\t * Creates an instance of {@link " + subClass.getFullName(translated)
				+ "} ." + "\n"
				+ "\t " + endComment );
		out.println ( "\tpublic " + subClass.getFullName(translated) + " new"
				+ subClass.getName(translated)
				+ "()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn new " + subClass.getImplFullName(translated) + "();" + "\n"
				+ "\t}" + "\n" );

		// Specialized create, update and remove
		if (entity != subClass || ! entity.getSpecializations().isEmpty()) {
			out.println ( "\t/**" + "\n"
					+ "\t * Adds an instance of {@link " + subClass.getFullName(translated)
					+ "} andto the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void create" + subClass.getName(translated) + " ("+ subClass.getFullName(translated)
					+ " entity)" + "\n"
					+ "\t{" ) ;
			if (subClass == entity) {
					out.println ( "\t\tif (entity == null)" + "\n"
						+ "\t\t{" + "\n"
						+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
						+ "\t\t\t\t\"" + subClass.getDaoName(translated) + ".create - 'entity' can not be null\");" + "\n"
						+ "\t\t}" + "\n"
						+ "\t\tthis.getHibernateTemplate().save(entity);" + "\n"
						+ "\t\tthis.getHibernateTemplate().flush();" ) ;
//				generateHibernateListenerMethods("created", out);
				out.println ( "\t}" + "\n" );
			} else {
				out.println ( "\t\tthis.create" + subClass.getSuperClass().getName(translated) + "(entity);" + "\n"
					+ "\t}" + "\n" );
			}

			out.println ( "\t/**" + "\n"
					+ "\t * Updates an instance of {@link " + subClass.getFullName(translated)
					+ "} at the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void update" + subClass.getName(translated) + " ("+ subClass.getFullName(translated)
					+ " entity)" + "\n"
					+ "\t{" );
			if (subClass == entity) {
				out.println ( "\t\tif (entity == null)" + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
					+ "\t\t\t\t\"" + subClass.getDaoName(translated) + ".update - 'entity' can not be null\");" + "\n"
					+ "\t\t}" + "\n"
					+ "\t\tthis.getHibernateTemplate().update(entity);" + "\n"
					+ "\t\tthis.getHibernateTemplate().flush();" );
//				generateHibernateListenerMethods(rep, "updated", out);
				out.println ( "\t}" + "\n" );
			} else {
				out.println ( "\t\tthis.update" + subClass.getSuperClass().getName(translated) + "(entity);" + "\n"
					+ "\t}" + "\n" );
			}

			out.println ( "\t/**" + "\n"
					+ "\t * Removes an instance of {@link " + subClass.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void remove" + subClass.getName(translated) + " ("+ subClass.getFullName(translated)
					+ " entity)" + "\n"
					+ "\t{" );
			if (subClass == entity) {
				out.println ( "\t\tif (entity == null)" + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
					+ "\t\t\t\t\"" + subClass.getDaoName(translated) + ".remove - 'entity' can not be null\");" + "\n"
					+ "\t\t}" + "\n"
					+ "\t\tthis.getHibernateTemplate().delete(entity);" + "\n"
					+ "\t\tthis.getHibernateTemplate().flush();" );
//				generateHibernateListenerMethods( "deleted", out);
				out.println ( "\t}" + "\n" );
			} else {
				out.println ( "\t\tthis.remove" + subClass.getSuperClass().getName(translated) + "(entity);" + "\n"
					+ "\t}" + "\n" );
			}
		}

		//
		// Subclasses
		//
		for (ModelClass cl: subClass.getSpecializations())
		{
			out.println ( "\n" + "\t// " + cl.getName(translated) + " methods" + "\n" );
			generateDaoBaseMethods( out, entity, cl);
		}
	}


	void generateCasts(PrintStream out, String indent,
			String entityName, String method, ModelClass entity, ModelClass subClass)
	{
		if (entity == subClass) {
			out.print ( indent );
		}
		boolean anyChild = false;
		//
		// Subclasses
		//
		for (ModelClass cl: subClass.getSpecializations())
		{
			generateCasts (out,  indent, entityName, method, entity, cl);
			anyChild = true;
		}
		if (entity == subClass) {
			if (anyChild) {
				out.print ( "\n"
					+ indent + "\t");
			}
			out.println ( "this." + method + subClass.getName(translated) + " (" + entityName + ");" );
		} else {
			out.print ( "if (" + entityName + " instanceof " + subClass.getFullName(translated) + ")" + "\n"
					+ indent + "\tthis." + method + subClass.getName(translated) + " ((" + subClass.getFullName(translated) + ")"+ entityName + ");" + "\n"
					+ indent + "else " );
		}
	}


	public void generateEntityDaoBase(ModelClass entity) throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File (generator.getCoreDir() + File.separator + entity.getPackageDir(translated) + entity.getDaoBaseName(translated)+ ".java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());
		out.println("//");
		out.println("// (c) 2014 Soffid");
		out.println("//");
		out.println("//");

		if (entity.getPackage(translated) != null)
			out.println ( "package " + entity.getPackage(translated) + ";" );

		out.println ( "/**" + "\n"
				+ " * DAO Base for Entity " + entity.getName(translated) + "\n"
				+ Util.formatComments(entity.getComments())
				+ " " + endComment );
		out.println ( "public abstract class " + entity.getDaoBaseName(translated) );
		if (entity.getSuperClass() != null && entity.getSuperClass().isEntity()) {
			out.println ( " extends "
				+ entity.getSuperClass().getDaoImplFullName(translated)
				+ " implements "
				+ entity.getDaoFullName(translated) + "\n"
				+ "{" + "\n"
				+ "}" );
		} else {
			ModelAttribute id = entity.getIdentifier();
			out.println ( "\textends org.springframework.orm.hibernate3.support.HibernateDaoSupport" + "\n"
				+ "\timplements " + entity.getDaoFullName(translated)
				+ "\n" + "{" );
			generateDependencies( entity, out);
			generateDaoBaseMethods( out, entity, entity);

			// load by id

			out.println ( "\t/**" + "\n"
					+ "\t * Loads an instance of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic "+ entity.getFullName(translated)
					+ " load("
					+ id.getJavaType(translated) + " " + id.getName(translated) + ") {" );
			if (! id.getDataType().isPrimitive()) {
				out.println ( "\t\tif ("+ id.getName(translated) + " == null) {" + "\n"
					+ "\t\t\tthrow new IllegalArgumentException(\"" + id.getName(translated) + " cannot be null\");" + "\n"
					+ "\t\t}" );
			}

			out.println ( "\t\treturn (" + entity.getFullName(translated)
					+ ") this.getHibernateTemplate().get("
					+ entity.getImplFullName(translated) + ".class, "+id.getName(translated)+ ");" + "\n"
					+ "\t}" );

			// load all

			out.println ( "\t/**" + "\n"
					+ "\t * Loads all instances of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic java.util.List<"+ entity.getFullName(translated)
					+ "> loadAll() {" + "\n"
					+ "\t\treturn (java.util.List<" + entity.getFullName(translated)
					+ ">)" + "\n"
					+ "\t\t\tthis.getHibernateTemplate().loadAll("+entity.getImplFullName(translated)+".class);" + "\n"
					+ "\t};" + "\n" );

			// create, update, remove entity
			out.println ( "\t/**" + "\n"
					+ "\t * Adds an instance of {@link " + entity.getFullName(translated)
					+ "} andto the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void create ("+ entity.getFullName(translated)
					+ " entity)" + "\n"
					+ "\t{" );
			if (entity.getSpecializations().isEmpty() ) {
					out.println ( "\t\tif (entity == null)" + "\n"
						+ "\t\t{" + "\n"
						+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
						+ "\t\t\t\t\"" + entity.getDaoName(translated) + ".create - 'entity' can not be null\");" + "\n"
						+ "\t\t}" + "\n"
						+ "\t\tthis.getHibernateTemplate().save(entity);" + "\n"
						+ "\t\tthis.getHibernateTemplate().flush();" );
//					generateHibernateListenerMethods(rep, "created", out);
					out.println ( "\t}" + "\n" );
			} else {
				generateCasts(out, "\t\t", "entity", "create", entity, entity);
				out.println ( "\t}" + "\n" );
			}

			out.println ( "\t/**" + "\n"
					+ "\t * Updates an instance of {@link " + entity.getFullName(translated)
					+ "} at the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void update ("+ entity.getFullName(translated)
					+ " entity)" + "\n"
					+ "\t{" );
			if (entity.getSpecializations().isEmpty() ) {
					out.println ( "\t\tif (entity == null)" + "\n"
						+ "\t\t{" + "\n"
						+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
						+ "\t\t\t\t\"" + entity.getDaoName(translated) + ".update - 'entity' can not be null\");" + "\n"
						+ "\t\t}" + "\n"
						+ "\t\tthis.getHibernateTemplate().update(entity);" + "\n"
						+ "\t\tthis.getHibernateTemplate().flush();" );
//					generateHibernateListenerMethods(rep, "updated", out);
					out.println ( "\t}" + "\n" );
			} else {
				generateCasts(out, "\t\t", "entity", "update", entity, entity);
				out.println ( "\t}" + "\n" );
			}

			out.println ( "\t/**" + "\n"
					+ "\t * Removes an instance of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void remove ("+ entity.getFullName(translated)
					+ " entity)" + "\n"
					+ "\t{" );
			if (entity.getSpecializations().isEmpty() ) {
					out.println ( "\t\tif (entity == null)" + "\n"
						+ "\t\t{" + "\n"
						+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
						+ "\t\t\t\t\"" + entity.getDaoName(translated) + ".remove - 'entity' can not be null\");" + "\n"
						+ "\t\t}" + "\n"
						+ "\t\tthis.getHibernateTemplate().delete(entity);" + "\n"
						+ "\t\tthis.getHibernateTemplate().flush();" );
//					generateHibernateListenerMethods(rep, "deleted", out);
					out.println ( "\t}" + "\n" );
			} else {
				generateCasts(out, "\t\t", "entity", "remove", entity, entity);
				out.println ( "\t}" + "\n" );
			}

			// create ,update, remove entity list
			out.println ( "\t/**" + "\n"
					+ "\t * Creates a collection of {@link " + entity.getFullName(translated)
					+ "} and adds it to the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void create (java.util.Collection<? extends "+ entity.getFullName(translated)
					+ "> entities) {" + "\n"
					+ "\t\tif (entities == null)" + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
					+ "\t\t\t\t\"" + entity.getDaoName(translated) + ".create - 'entities' cannot be null\");" + "\n"
					+ "\t\t}" + "\n"
					+ "\t\tfor ("+ entity.getFullName(translated) + " entity: entities) { " + "\n"
					+ "\t\t\tcreate(entity);" + "\n"
					+ "\t\t}" + "\n"
					+ "\t}" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Updates a collection of {@link " + entity.getFullName(translated)
					+ "} in the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void update (java.util.Collection<? extends "+ entity.getFullName(translated)
					+ "> entities) {" + "\n"
					+ "\t\tif (entities == null)" + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
					+ "\t\t\t\t\"" + entity.getDaoName(translated) + ".update - 'entities' cannot be null\");" + "\n"
					+ "\t\t}" + "\n"
					+ "\t\tfor ("+ entity.getFullName(translated) + " entity: entities) { " + "\n"
					+ "\t\t\tupdate(entity);" + "\n"
					+ "\t\t}" + "\n"
					+ "\t}" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Removes a collection of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void remove (java.util.Collection<? extends "+ entity.getFullName(translated)
					+ "> entities) {" + "\n"
					+ "\t\tif (entities == null)" + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
					+ "\t\t\t\t\"" + entity.getDaoName(translated) + ".remove - 'entities' cannot be null\");" + "\n"
					+ "\t\t}" + "\n"
					+ "\t\tfor ("+ entity.getFullName(translated) + " entity: entities) { " + "\n"
					+ "\t\t\tremove(entity);" + "\n"
					+ "\t\t}" + "\n"
					+ "\t}" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Removes an instance of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic void remove (" + id . getJavaType(translated) + " id)" + "\n"
					+ "\t{" + "\n"
					+ "\t\tif (id == null) " + "\n"
					+ "\t\t{" + "\n"
					+ "\t\t\tthrow new IllegalArgumentException(" + "\n"
					+ "\t\t\t\t\"" + entity.getDaoName(translated)+ ".remove - 'id' can not be null\");" + "\n"
					+ "\t\t}" + "\n"
					+ "\t\t" + entity.getFullName(translated) + " entity = this.load(id);" + "\n"
					+ "\t\tif (entity != null)" + "\n"
					+ "\t\t\tthis.remove(entity);" + "\n"
					+ "\t}" + "\n" );

			// Queries

			out.println ( "\t/**" + "\n"
					+ "\t * Query of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t * parameter query HQL Query String" + "\n"
					+ "\t * parameter parameters HQL Parameters" + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic java.util.List<" + entity.getFullName(translated)
					+ "> query (String queryString, "+generator.getRootPkg()+".model.Parameter[] parameters)" + "\n"
					+ "\t{" + "\n"
					+ "\t\ttry {" + "\n"
					+ "\t\t\tjava.util.List results = new "+generator.getRootPkg()+".model.QueryBuilder().query(this," + "\n"
					+ "\t\t\t\tqueryString, parameters);" + "\n"
					+ "\t\t\treturn (java.util.List<" + entity.getFullName(translated)+ ">) results;" + "\n"
					+ "\t\t} catch (org.hibernate.HibernateException ex) {" + "\n"
					+ "\t\t\tthrow super.convertHibernateAccessException(ex);" + "\n"
					+ "\t\t}" + "\n"
					+ "\t}" + "\n" );

			out.println ( "\t/**" + "\n"
					+ "\t * Query of {@link " + entity.getFullName(translated)
					+ "} from the persistent store." + "\n"
					+ "\t * parameter query HQL Query String" + "\n"
					+ "\t * parameter parameters HQL Parameters" + "\n"
					+ "\t * parameter maxResults max number of rows to return" + "\n"
					+ "\t " + endComment );
			out.println ( "\tpublic java.util.List<" + entity.getFullName(translated)
					+ "> query (String queryString, "+generator.getRootPkg()+".model.Parameter[] parameters, " + modelPackage + ".criteria.CriteriaSearchConfiguration criteria)" + "\n"
					+ "\t{" + "\n"
					+ "\t\ttry {" + "\n"
					+ "\t\t\tjava.util.List results = new "+generator.getRootPkg()+".model.QueryBuilder().query(this," + "\n"
					+ "\t\t\t\tqueryString, parameters, criteria.getMaximumResultSize());" + "\n"
					+ "\t\t\treturn (java.util.List<" + entity.getFullName(translated)+ ">) results;" + "\n"
					+ "\t\t} catch (org.hibernate.HibernateException ex) {" + "\n"
					+ "\t\t\tthrow super.convertHibernateAccessException(ex);" + "\n"
					+ "\t\t}" + "\n"
					+ "\t}" + "\n" );
			out.println ( "}" );
		}
		out.close();
	}

	void generateSearchCriteria() throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File (generator.getCoreDir() + File.separator + modelDir + File.separator + "criteria" + File.separator + "CriteriaSearch.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" );
				out.println("// (C) 2013 Soffid" );
				out.println("//" );
				out.println("//" );
				out.println();
		out.println("package " + modelPackage + ".criteria;" );
		out.println("" );
		out.println("/**" );
		out.println(" * Implements a generic search mechanism based on the Hibernate Criteria API. The" );
		out.println(" * <code>CriteriaSearch</code> allows adding parameters which function as where clause. The" );
		out.println(" * parameters are analysed whether they should be considered or not. This depends both on the actual" );
		out.println(" * value of the parameter and on the configuration.<br>" );
		out.println(" * The <code>CriteriaSearch</code> is expected to be a general solution for a basic search with" );
		out.println(" * parameters connected by logical <b>and</b>. This search does <b>not</b> provide grouping." );
		out.println(" *" );
		out.println(" * @author Stefan Reichert" );
		out.println(" * @author Peter Friese" );
		out.println(" * @author Chad Brandon" );
		out.println(" * @see org.hibernate.Criteria" );
		out.println(" * @see org.hibernate.criterion.Expression" );
		out.println(" " + endComment );
		out.println("@SuppressWarnings({\"unchecked\"})" );
		out.println("public class CriteriaSearch" );
		out.println("{" );
		out.println("\tprivate CriteriaSearchConfiguration configuration;" );
		out.println("\tprivate org.hibernate.Criteria rootCriteria;" );
		out.println("\tprivate java.util.Map childCriteriaMap;" );
		out.println("\tprivate java.util.List orderList;" );
		out.println("\tprivate Class resultType;" );
		out.println("" );
		out.println("\tprivate static final class ParameterComparator" );
		out.println("\t\timplements java.util.Comparator" );
		out.println("\t{" );
		out.println("\t\tpublic int compare(final Object object1, final Object object2)" );
		out.println("\t\t{" );
		out.println("\t\t\tfinal CriteriaSearchParameter parameter1 = (CriteriaSearchParameter)object1;" );
		out.println("\t\t\tfinal CriteriaSearchParameter parameter2 = (CriteriaSearchParameter)object2;" );
		out.println("" );
		out.println("\t\t\tfinal int relevance1 = parameter1.getOrderRelevance();" );
		out.println("\t\t\tfinal int relevance2 = parameter2.getOrderRelevance();" );
		out.println("\t\t\tint result = 0;" );
		out.println("\t\t\tif (relevance1 > relevance2)" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\tresult = 1;" );
		out.println("\t\t\t}" );
		out.println("\t\t\telse if (relevance1 < relevance2)" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\tresult = -1;" );
		out.println("\t\t\t}" );
		out.println("\t\t\treturn result;" );
		out.println("" );
		out.println("\t\t}" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Constructor for CriteriaSearch. Creates a <code>CriteriaSearch</code> with a default" );
		out.println("\t * <code>CriteriaSearchConfiguration</code>." );
		out.println("\t *" );
		out.println("\t * @param session The Hibernate session." );
		out.println("\t * @param resultType The <code>Class</code> of the result." );
		out.println("\t " + endComment );
		out.println("\tpublic CriteriaSearch(org.hibernate.Session session, Class resultType)" );
		out.println("\t{" );
		out.println("\t\tthis.configuration = new CriteriaSearchConfiguration();" );
		out.println("\t\tthis.resultType = resultType;" );
		out.println("\t\tthis.rootCriteria = session.createCriteria(this.resultType);" );
		out.println("\t\tthis.childCriteriaMap = new java.util.HashMap();" );
		out.println("\t\tthis.orderList = new java.util.ArrayList();" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Executes a <code>HibernateQuery</code> using the currently defined" );
		out.println("\t * <code>CriteriaSearchParameter</code>s, and returns a java.util.Set" );
		out.println("\t * containing the query results." );
		out.println("\t *" );
		out.println("\t * @return result The result of the query." );
		out.println("\t * @throws org.hibernate.HibernateException" );
		out.println("\t " + endComment );
		out.println("\tpublic final java.util.Set executeAsSet()" );
		out.println("\t\tthrows org.hibernate.HibernateException" );
		out.println("\t{" );
		out.println("\t\t// add ordering" );
		out.println("\t\tif (this.orderList.size() > 0)" );
		out.println("\t\t{" );
		out.println("\t\t\tjava.util.Collections.sort(this.orderList, new ParameterComparator());" );
		out.println("\t\t\tfor (java.util.Iterator orderIterator = this.orderList.iterator(); orderIterator.hasNext();)" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\tCriteriaSearchParameter parameter = (CriteriaSearchParameter)orderIterator.next();" );
		out.println("\t\t\t\tint direction = parameter.getOrderDirection();" );
		out.println("\t\t\t\tif (direction == CriteriaSearchParameter.ORDER_ASC)" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tthis.rootCriteria.addOrder(org.hibernate.criterion.Order.asc(parameter.getParameterPattern()));" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\telse" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tthis.rootCriteria.addOrder(org.hibernate.criterion.Order.desc(parameter.getParameterPattern()));" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t}" );
		out.println("\t\t}" );
		out.println("" );
		out.println("\t\t// set the first result if configured" );
		out.println("\t\tif (this.configuration.getFirstResult() != null)" );
		out.println("\t\t{" );
		out.println("\t\t\tthis.rootCriteria.setFirstResult(this.configuration.getFirstResult().intValue());" );
		out.println("\t\t}" );
		out.println("" );
		out.println("\t\t// set the fetch size if configured" );
		out.println("\t\tif (this.configuration.getFetchSize() != null)" );
		out.println("\t\t{" );
		out.println("\t\t\tthis.rootCriteria.setFetchSize(this.configuration.getFetchSize().intValue());" );
		out.println("\t\t}" );
		out.println("" );
		out.println("\t\t// limit the maximum result if configured" );
		out.println("\t\tif (this.configuration.getMaximumResultSize() != null)" );
		out.println("\t\t{" );
		out.println("\t\t\tthis.rootCriteria.setMaxResults(this.configuration.getMaximumResultSize().intValue());" );
		out.println("\t\t}" );
		out.println("" );
		out.println("\t\t// Hibernate does not support a 'unique' identifier. As a search may contain outer joins," );
		out.println("\t\t// duplicates in the resultList are possible. We eliminate any duplicates here, creating a" );
		out.println("\t\t// distinctified resultSet (Suggestion from Hibernate itself; see www.hibernate.org's FAQ's)." );
		out.println("\t\treturn new java.util.LinkedHashSet(this.rootCriteria.list());" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Executes a <code>HibernateQuery</code> using the currently defined" );
		out.println("\t * <code>CriteriaSearchParameter</code>s, and returns a java.util.List" );
		out.println("\t * containing the query results." );
		out.println("\t *" );
		out.println("\t * @return result The result of the query." );
		out.println("\t * @throws org.hibernate.HibernateException" );
		out.println("\t " + endComment );
		out.println("\tpublic java.util.List executeAsList() throws org.hibernate.HibernateException" );
		out.println("\t{" );
		out.println("\t\treturn new java.util.ArrayList(this.executeAsSet());" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Adds a <code>CriteriaSearchParameter</code> to this search. The parameter is connected to" );
		out.println("\t * the search by logical <b>and</b>. It is not considered if the value is <code>null</code>." );
		out.println("\t * If the value is not <code>null</code> it is compared using the" );
		out.println("\t * <code>CriteriaSearchParameter.EQUALS_COMPARATOR</code>." );
		out.println("\t *" );
		out.println("\t * @param parameterValue The value of the parameter." );
		out.println("\t * @param parameterPattern The pattern of the parameter (dot-seperated path e.g. person.address.street)." );
		out.println("\t * @throws org.hibernate.HibernateException" );
		out.println("\t " + endComment );
		out.println("\tpublic void addParameter(Object parameterValue, String parameterPattern) throws org.hibernate.HibernateException" );
		out.println("\t{" );
		out.println("\t\taddParameter(new CriteriaSearchParameter(parameterValue, parameterPattern));" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Adds a <code>CriteriaSearchParameter</code> to this search. The parameter is connected to" );
		out.println("\t * the search by logical <b>and</b>. It is not considered if the value is <code>null</code> or" );
		out.println("\t * if the <code>String</code> empty. If the value is not <code>null</code> it is compared" );
		out.println("\t * using the <code>CriteriaSearchParameter.LIKE_COMPARATOR</code>." );
		out.println("\t *" );
		out.println("\t * @param parameterValue The value of the parameter." );
		out.println("\t * @param parameterPattern The pattern of the parameter (dot-seperated path e.g. person.address.street)." );
		out.println("\t * @throws org.hibernate.HibernateException" );
		out.println("\t " + endComment );
		out.println("\tpublic void addParameter(String parameterValue, String parameterPattern) throws org.hibernate.HibernateException" );
		out.println("\t{" );
		out.println("\t\taddParameter(new CriteriaSearchParameter(parameterValue, parameterPattern));" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Adds a <code>CriteriaSearchParameter</code> to this search. The parameter is connected to" );
		out.println("\t * the search by logical <b>and</b>." );
		out.println("\t *" );
		out.println("\t * @param parameter The <code>CriteriaSearchParameter</code> to add." );
		out.println("\t * @throws org.hibernate.HibernateException" );
		out.println("\t " + endComment );
		out.println("\tpublic void addParameter(CriteriaSearchParameter parameter) throws org.hibernate.HibernateException" );
		out.println("\t{" );
		out.println("\t\tif (considerParameter(parameter))" );
		out.println("\t\t{" );
		out.println("\t\t\t// parsing the pattern of the parameter" );
		out.println("\t\t\tString[] path = CriteriaSearchParameter.PATTERN.split(parameter.getParameterPattern());" );
		out.println("\t\t\tString parameterName = path[path.length - 1];" );
		out.println("\t\t\torg.hibernate.Criteria parameterCriteria = this.rootCriteria;" );
		out.println("" );
		out.println("\t\t\torg.hibernate.Criteria childEntityCriteria;" );
		out.println("\t\t\tif (path.length > 1)" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\t// We have a parameter affecting an attribute of an inner childEntity object so we need" );
		out.println("\t\t\t\t// to traverse to get the right criteria object" );
		out.println("\t\t\t\tchildEntityCriteria = this.rootCriteria;" );
		out.println("\t\t\t\t// Evaluating the proper criteria object for the defined parameter" );
		out.println("\t\t\t\tClass type = this.resultType;" );
		out.println("\t\t\t\tfor (int index = 0; index < (path.length - 1); index++)" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tfinal String childEntityName = path[index];" );
		out.println("\t\t\t\t\tfinal java.util.Collection embeddedValues = CriteriaSearchProperties.getEmbeddedValues(type);" );
		out.println("\t\t\t\t\tif (embeddedValues != null && embeddedValues.contains(childEntityName))" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t// - use the rest of the path as the parameter name" );
		out.println("\t\t\t\t\t\tfinal int number = path.length - index;" );
		out.println("\t\t\t\t\t\tfinal String[] restOfPath = new String[path.length - index];" );
		out.println("\t\t\t\t\t\tjava.lang.System.arraycopy(path, index, restOfPath, 0, number);" );
		out.println("\t\t\t\t\t\tparameterName = org.apache.commons.lang.StringUtils.join(restOfPath, \".\");" );
		out.println("\t\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\ttype = CriteriaSearchProperties.getNavigableAssociationEndType(type, childEntityName);" );
		out.println("\t\t\t\t\tchildEntityCriteria = locateCriteria(childEntityName, childEntityCriteria);" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tif (childEntityCriteria != null)" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\t// We now have the right criteria object" );
		out.println("\t\t\t\t\tparameterCriteria = childEntityCriteria;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t}" );
		out.println("\t\t\t// check the type parameter value to add" );
		out.println("\t\t\tif(parameter.getParameterValue() instanceof Object[])" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\taddExpression(" );
		out.println("\t\t\t\t\tparameterCriteria," );
		out.println("\t\t\t\t\tparameterName," );
		out.println("\t\t\t\t\t(Object[])parameter.getParameterValue()," );
		out.println("\t\t\t\t\tparameter.getComparatorID()," );
		out.println("\t\t\t\t\tparameter.getMatchMode());" );
		out.println("\t\t\t}" );
		out.println("\t\t\telse" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\taddExpression(" );
		out.println("\t\t\t\t\tparameterCriteria," );
		out.println("\t\t\t\t\tparameterName," );
		out.println("\t\t\t\t\tparameter.getParameterValue()," );
		out.println("\t\t\t\t\tparameter.getComparatorID()," );
		out.println("\t\t\t\t\tparameter.getMatchMode());" );
		out.println("\t\t\t}" );
		out.println("\t\t}" );
		out.println("\t\t// if the parameter is to be ordered, add it to the order list" );
		out.println("\t\tif (parameter.getOrderDirection() != CriteriaSearchParameter.ORDER_UNSET)" );
		out.println("\t\t{" );
		out.println("\t\t\tthis.orderList.add(parameter);" );
		out.println("\t\t}" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Decides whether a paramter is considered as a criteria for a search depending on the type and" );
		out.println("\t * value of the <code>parameterValue</code> and <code>searchIfIsNull</code>. A" );
		out.println("\t * <code>parameterValue</code> of the type <code>String</code> is considered" );
		out.println("\t * <code>null</code> if being a <code>nullPointer</code> or empty." );
		out.println("\t *" );
		out.println("\t * @param parameter The parameter to check." );
		out.println("\t " + endComment );
		out.println("\tprivate boolean considerParameter(CriteriaSearchParameter parameter)" );
		out.println("\t{" );
		out.println("\t\tif (parameter.getParameterValue() instanceof String)" );
		out.println("\t\t{" );
		out.println("\t\t\tString stringParameterValue = (String) parameter.getParameterValue();" );
		out.println("\t\t\treturn (parameter.isSearchIfIsNull()" );
		out.println("\t\t\t\t\t|| (stringParameterValue != null && stringParameterValue.length() > 0));" );
		out.println("\t\t}" );
		out.println("\t\tif (parameter.getParameterValue() instanceof Object[])" );
		out.println("\t\t{" );
		out.println("\t\t\tObject[] parameterValues = (Object[]) parameter.getParameterValue();" );
		out.println("\t\t\treturn (parameter.isSearchIfIsNull()" );
		out.println("\t\t\t\t\t|| (parameterValues != null && parameterValues.length > 0));" );
		out.println("\t\t}" );
		out.println("\t\treturn (parameter.isSearchIfIsNull() || (parameter.getParameterValue() != null));" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Adds an <code>Expression</code> to a <code>Criteria</code>." );
		out.println("\t *" );
		out.println("\t * @param criteria" );
		out.println("\t * @param parameterName" );
		out.println("\t * @param parameterValue" );
		out.println("\t * @param comparatorID" );
		out.println("\t * @param matchMode" );
		out.println("\t " + endComment );
		out.println("\tprivate void addExpression(" );
		out.println("\t\torg.hibernate.Criteria criteria," );
		out.println("\t\tString parameterName," );
		out.println("\t\tObject parameterValue," );
		out.println("\t\tint comparatorID," );
		out.println("\t\torg.hibernate.criterion.MatchMode matchMode)" );
		out.println("\t{" );
		out.println("\t\tif (parameterValue != null)" );
		out.println("\t\t{" );
		out.println("\t\t\tswitch (comparatorID)" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.LIKE_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tif ((matchMode != null) && (parameterValue instanceof String))" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.like(" );
		out.println("\t\t\t\t\t\t\tparameterName," );
		out.println("\t\t\t\t\t\t\t(String)parameterValue," );
		out.println("\t\t\t\t\t\t\tmatchMode));" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\telse" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.like(parameterName, parameterValue));" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.INSENSITIVE_LIKE_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tif ((matchMode != null) && (parameterValue instanceof String))" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.ilike(" );
		out.println("\t\t\t\t\t\t\tparameterName," );
		out.println("\t\t\t\t\t\t\t(String)parameterValue," );
		out.println("\t\t\t\t\t\t\tmatchMode));" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\telse" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.ilike(parameterName, parameterValue));" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.eq(parameterName, parameterValue));" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.GREATER_THAN_OR_EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.ge(parameterName, parameterValue));" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.GREATER_THAN_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.gt(parameterName, parameterValue));" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.LESS_THAN_OR_EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.le(parameterName, parameterValue));" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.LESS_THAN_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.lt(parameterName, parameterValue));" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.IN_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tif (parameterValue instanceof java.util.Collection)" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.in(parameterName, (java.util.Collection)parameterValue));" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.NOT_EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.ne(parameterName, parameterValue));" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t}" );
		out.println("\t\t}" );
		out.println("\t\telse" );
		out.println("\t\t{" );
		out.println("\t\t\tcriteria.add(org.hibernate.criterion.Expression.isNull(parameterName));" );
		out.println("\t\t}" );
		out.println("" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Adds an <code>Expression</code> to a <code>Criteria</code>. The given <code>parameterValues</code>" );
		out.println("\t * represents either an array of <code>String</code> or another object. The different values in the" );
		out.println("\t * array are added to a disjunction or conjuction which is connected with logical and to the other criteria of the" );
		out.println("\t * search." );
		out.println("\t *" );
		out.println("\t * @param criteria" );
		out.println("\t * @param parameterName" );
		out.println("\t * @param parameterValues" );
		out.println("\t * @param searchIfnull" );
		out.println("\t * @param comparatorID" );
		out.println("\t * @param matchMode" );
		out.println("\t " + endComment );
		out.println("\tprivate void addExpression(" );
		out.println("\t\torg.hibernate.Criteria criteria," );
		out.println("\t\tString parameterName," );
		out.println("\t\tObject[] parameterValues," );
		out.println("\t\tint comparatorID," );
		out.println("\t\torg.hibernate.criterion.MatchMode matchMode)" );
		out.println("\t{" );
		out.println("\t\tif (parameterValues != null)" );
		out.println("\t\t{" );
		out.println("\t\t\torg.hibernate.criterion.Disjunction disjunction = null;" );
		out.println("\t\t\torg.hibernate.criterion.Conjunction conjunction = null;" );
		out.println("\t\t\tswitch (comparatorID)" );
		out.println("\t\t\t{" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.LIKE_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tdisjunction = org.hibernate.criterion.Restrictions.disjunction();" );
		out.println("\t\t\t\t\tif ((matchMode != null) && (parameterValues instanceof String[]))" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tString[] stringParameterValues = (String[]) parameterValues;" );
		out.println("\t\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tif (stringParameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression.like(" );
		out.println("\t\t\t\t\t\t\t        parameterName, stringParameterValues[index], matchMode));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression" );
		out.println("\t\t\t\t\t\t\t        .isNull(parameterName));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\telse" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression.like(" );
		out.println("\t\t\t\t\t\t\t        parameterName, parameterValues[index]));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression" );
		out.println("\t\t\t\t\t\t\t            .isNull(parameterName));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.INSENSITIVE_LIKE_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tdisjunction = org.hibernate.criterion.Restrictions.disjunction();" );
		out.println("\t\t\t\t\tif ((matchMode != null) && (parameterValues instanceof String[]))" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tString[] stringParameterValues = (String[]) parameterValues;" );
		out.println("\t\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tif (stringParameterValues[index] != null) {" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression.ilike(" );
		out.println("\t\t\t\t\t\t\t        parameterName, stringParameterValues[index], matchMode));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t\telse {" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression" );
		out.println("\t\t\t\t\t\t\t        .isNull(parameterName));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\telse" );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression.ilike(" );
		out.println("\t\t\t\t\t\t\t        parameterName, parameterValues[index]));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\t    disjunction.add(org.hibernate.criterion.Expression" );
		out.println("\t\t\t\t\t\t\t            .isNull(parameterName));" );
		out.println("\t\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tdisjunction = org.hibernate.criterion.Restrictions.disjunction();" );
		out.println("\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.eq(parameterName," );
		out.println("\t\t\t\t\t\t\t        parameterValues[index]));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\telse {" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.isNull(parameterName));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.GREATER_THAN_OR_EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tdisjunction = org.hibernate.criterion.Restrictions.disjunction();" );
		out.println("\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.ge(parameterName," );
		out.println("\t\t\t\t\t\t\t        parameterValues[index]));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.isNull(parameterName));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.GREATER_THAN_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tdisjunction = org.hibernate.criterion.Restrictions.disjunction();" );
		out.println("\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.gt(parameterName," );
		out.println("\t\t\t\t\t\t\t        parameterValues[index]));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.isNull(parameterName));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.LESS_THAN_OR_EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tdisjunction = org.hibernate.criterion.Restrictions.disjunction();" );
		out.println("\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.le(parameterName," );
		out.println("\t\t\t\t\t\t\t        parameterValues[index]));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.isNull(parameterName));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.LESS_THAN_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tdisjunction = org.hibernate.criterion.Restrictions.disjunction();" );
		out.println("\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.lt(parameterName," );
		out.println("\t\t\t\t\t\t\t        parameterValues[index]));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tdisjunction.add(org.hibernate.criterion.Expression.isNull(parameterName));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.IN_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tcriteria.add(org.hibernate.criterion.Expression.in(parameterName, parameterValues));" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t\tcase CriteriaSearchParameter.NOT_EQUAL_COMPARATOR:" );
		out.println("\t\t\t\t{" );
		out.println("\t\t\t\t\tconjunction = org.hibernate.criterion.Restrictions.conjunction();" );
		out.println("\t\t\t\t\tfor (int index = 0; index < parameterValues.length; index++) " );
		out.println("\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\tif (parameterValues[index] != null) " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tconjunction.add(org.hibernate.criterion.Expression.ne(parameterName," );
		out.println("\t\t\t\t\t\t\t        parameterValues[index]));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t\telse " );
		out.println("\t\t\t\t\t\t{" );
		out.println("\t\t\t\t\t\t\tconjunction.add(org.hibernate.criterion.Expression.isNotNull(parameterName));" );
		out.println("\t\t\t\t\t\t}" );
		out.println("\t\t\t\t\t}" );
		out.println("\t\t\t\t\tbreak;" );
		out.println("\t\t\t\t}" );
		out.println("\t\t\t}" );
		out.println("" );
		out.println("\t\t\tif (disjunction != null) " );
		out.println("\t\t\t{" );
		out.println("\t\t\t\tcriteria.add(disjunction);" );
		out.println("\t\t\t}" );
		out.println("\t\t\tif (conjunction != null) " );
		out.println("\t\t\t{" );
		out.println("\t\t\t\tcriteria.add(conjunction);" );
		out.println("\t\t\t}" );
		out.println("\t\t}" );
		out.println("\t\telse" );
		out.println("\t\t{" );
		out.println("\t\t\tcriteria.add(org.hibernate.criterion.Expression.isNull(parameterName));" );
		out.println("\t\t}" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Locates a <code>Criteria</code> for a <code>childEntityName</code>. If a" );
		out.println("\t * <code>Criteria</code> exists for the <code>childEntityName</code>, it is returned. If" );
		out.println("\t * not, one is created and referenced in the <code>childCriteriaMap</code> under the" );
		out.println("\t * <code>childEntityName</code>." );
		out.println("\t *" );
		out.println("\t * @param childEntityName" );
		out.println("\t * @param parentCriteria" );
		out.println("\t * @return criteria The Criteria for the childEntityName." );
		out.println("\t * @throws org.hibernate.HibernateException" );
		out.println("\t " + endComment );
		out.println("\tprivate org.hibernate.Criteria locateCriteria(String childEntityName, org.hibernate.Criteria parentCriteria) throws org.hibernate.HibernateException" );
		out.println("\t{" );
		out.println("\t\tif (this.childCriteriaMap.containsKey(childEntityName))" );
		out.println("\t\t{" );
		out.println("\t\t\treturn (org.hibernate.Criteria) this.childCriteriaMap.get(childEntityName);" );
		out.println("\t\t}" );
		out.println("\t\torg.hibernate.Criteria childCriteria = parentCriteria.createCriteria(childEntityName);" );
		out.println("\t\tif (this.configuration.isForceEagerLoading())" );
		out.println("\t\t{" );
		out.println("\t\t\tparentCriteria.setFetchMode(childEntityName, org.hibernate.FetchMode.JOIN);" );
		out.println("\t\t}" );
		out.println("\t\tthis.childCriteriaMap.put(childEntityName, childCriteria);" );
		out.println("\t\treturn childCriteria;" );
		out.println("\t}" );
		out.println("" );
		out.println("\t/**" );
		out.println("\t * Returns the configuration of this search." );
		out.println("\t *" );
		out.println("\t * @return configuration" );
		out.println("\t " + endComment );
		out.println("\tpublic CriteriaSearchConfiguration getConfiguration()" );
		out.println("\t{" );
		out.println("\t\treturn this.configuration;" );
		out.println("\t}" );
		out.println("}" );
	}

	void generateSearchCriteriaParameter() throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File (generator.getCoreDir() + File.separator + modelDir + File.separator + "criteria" + File.separator + "CriteriaSearchParameter.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());


		out.println ( "//" + "\n"
				+ "// (C) 2013 Soffid" + "\n"
				+ "//" + "\n"
				+ "//" + "\n"
				+ "package " + modelPackage + ".criteria;" + "\n"
				+ "" + "\n"
				+ "import java.util.regex.Pattern;" + "\n"
				+ "" + "\n"
				+ "/**" + "\n"
				+ " * A <code>CriteriaSearchParameter</code> represents a parameter for a <code>CriteriaSearch</code>." + "\n"
				+ " * <br>" + "\n"
				+ " * <br>" + "\n"
				+ " * The <code>parameterValue</code> is the actual value to be searched for." + "\n"
				+ " * <br>" + "\n"
				+ " * <br>" + "\n"
				+ " * The <code>parameterPattern</code> describes the actual parameter which shall be considered for" + "\n"
				+ " * the search. It contains the dot-seperated path and the name of the parameter starting at the" + "\n"
				+ " * rootEntity of the actual <code>CriteriaSearch</code>. The pattern of a the street of an address" + "\n"
				+ " * of a person would look like <i>address.street </i> (assuming the entity structure to be" + "\n"
				+ " * <code>aPerson.getAddress().getStreet()</code>)." + "\n"
				+ " * <br>" + "\n"
				+ " * <br>" + "\n"
				+ " * Usually, if a parameter is <code>null</code> (or if the parameter is of type <code>String</code>" + "\n"
				+ " * and empty), it is not considered for a search. If <code>searchIfIsNull</code> is <code>true</code>" + "\n"
				+ " * it is explicitly searched for the parameter to be null (or empty if the parameter is of type" + "\n"
				+ " * <code>String</code>).<br>" + "\n"
				+ " * <br>" + "\n"
				+ " * The <code>comparatorID</code> defines the comparator for the parameter. For parameters of type" + "\n"
				+ " * <code>String</code> the default comparator is the <code>LIKE_COMPARATOR</code>. The" + "\n"
				+ " * <code>EQUAL_COMPARATOR</code> is default for other parameters." + "\n"
				+ " *" + "\n"
				+ " * @author Stefan Reichert" + "\n"
				+ " * @author Peter Friese" + "\n"
				+ " " + endComment + "\n"
				+ "public class CriteriaSearchParameter" + "\n"
				+ "{" + "\n"
				+ "" + "\n"
				+ "\tpublic static final Pattern PATTERN = Pattern.compile(\"\\\\.\");" + "\n"
				+ "" + "\n"
				+ "\tpublic static final int LIKE_COMPARATOR = 0;" + "\n"
				+ "\tpublic static final int INSENSITIVE_LIKE_COMPARATOR = 1;" + "\n"
				+ "\tpublic static final int EQUAL_COMPARATOR = 2;" + "\n"
				+ "\tpublic static final int GREATER_THAN_OR_EQUAL_COMPARATOR = 3;" + "\n"
				+ "\tpublic static final int GREATER_THAN_COMPARATOR = 4;" + "\n"
				+ "\tpublic static final int LESS_THAN_OR_EQUAL_COMPARATOR = 5;" + "\n"
				+ "\tpublic static final int LESS_THAN_COMPARATOR = 6;" + "\n"
				+ "\tpublic static final int IN_COMPARATOR = 7;" + "\n"
				+ "\tpublic static final int NOT_EQUAL_COMPARATOR = 8;" + "\n"
				+ "" + "\n"
				+ "\t/** Order unset " + endComment + "\n"
				+ "\tpublic static final int ORDER_UNSET = -1;" + "\n"
				+ "" + "\n"
				+ "\t/** Ascending order " + endComment + "\n"
				+ "\tpublic static final int ORDER_ASC = 0;" + "\n"
				+ "" + "\n"
				+ "\t/** Descending order " + endComment + "\n"
				+ "\tpublic static final int ORDER_DESC = 1;" + "\n"
				+ "" + "\n"
				+ "\t/** Order relevance not set " + endComment + "\n"
				+ "\tpublic static final int RELEVANCE_UNSET = -1;" + "\n"
				+ "" + "\n"
				+ "\tprivate Object parameterValue;" + "\n"
				+ "\tprivate String parameterPattern;" + "\n"
				+ "\tprivate boolean searchIfIsNull = false;" + "\n"
				+ "\tprivate org.hibernate.criterion.MatchMode matchMode = null;" + "\n"
				+ "\tprivate int comparatorID = CriteriaSearchParameter.EQUAL_COMPARATOR;" + "\n"
				+ "\tprivate int orderDirection = ORDER_UNSET;" + "\n"
				+ "\tprivate int orderRelevance = RELEVANCE_UNSET;" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter. Sets <code>searchIfIsNull</code> to" + "\n"
				+ "\t * <code>false</code> and uses the <code>EQUAL_COMPARATOR</code>." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(Object parameterValue, String parameterPattern)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, false, EQUAL_COMPARATOR);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter for a <code>String</code> parameter." + "\n"
				+ "\t * Sets <code>searchIfIsNull</code> to <code>false</code> and uses the" + "\n"
				+ "\t * <code>LIKE_COMPARATOR</code>." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(String parameterValue, String parameterPattern)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, false, LIKE_COMPARATOR);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter for a <code>String[]</code> parameter." + "\n"
				+ "\t * Sets <code>searchIfIsNull</code> to <code>false</code> and uses the" + "\n"
				+ "\t * <code>LIKE_COMPARATOR</code>." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(String[] parameterValue, String parameterPattern)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, false, LIKE_COMPARATOR);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter. Sets <code>searchIfIsNull</code> to <code>false</code>." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(Object parameterValue, String parameterPattern, int comparatorID)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, false, comparatorID);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tObject parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, searchIfnull, EQUAL_COMPARATOR);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tString parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, searchIfnull, LIKE_COMPARATOR);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tString[] parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, searchIfnull, LIKE_COMPARATOR);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t * @param comparatorID Indicates what comparator is to be used (e.g. like, =, <, ...)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tObject parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull," + "\n"
				+ "\t\tint comparatorID)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tsuper();" + "\n"
				+ "\t\tthis.parameterValue = parameterValue;" + "\n"
				+ "\t\tthis.parameterPattern = parameterPattern;" + "\n"
				+ "\t\tthis.searchIfIsNull = searchIfnull;" + "\n"
				+ "\t\tthis.comparatorID = comparatorID;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tObject parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\t this(parameterValue, parameterPattern, searchIfnull);" + "\n"
				+ "\t\t this.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tString parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, searchIfnull);" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tString[] parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, searchIfnull);" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param searchIfIsNull Indicates whether the query should contain an" + "\n"
				+ "\t *     <code>IS null</code> if the parameter is <code>null</code>." + "\n"
				+ "\t * @param comparatorID Indicates what comparator is to be used (e.g. like, =, <, ...)." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tString parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tboolean searchIfnull," + "\n"
				+ "\t\tint comparatorID," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, searchIfnull, comparatorID);" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param comparatorID Indicates what comparator is to be used (e.g. like, =, <, ...)." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tObject parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\tint comparatorID," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern, comparatorID);" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tObject parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern);" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tString parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern);" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Constructor for CriteriaSearchParameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The actual value of the parameter." + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t * @param matchMode The hibernate matchmode to be used in string comparisons." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic CriteriaSearchParameter(" + "\n"
				+ "\t\tString[] parameterValue," + "\n"
				+ "\t\tString parameterPattern," + "\n"
				+ "\t\torg.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis(parameterValue, parameterPattern);" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return The comparator to be used (e.g. like, =, <, ...)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic int getComparatorID()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn comparatorID;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Sets the comparator to be used (e.g. like, =, <, ...)." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param comparatorID The comprator ID." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic void setComparatorID(int comparatorID)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis.comparatorID = comparatorID;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic String getParameterPattern()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn parameterPattern;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Sets the pattern of this parameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterPattern The pattern of this parameter (dot-seperated path e.g. person.address.street)." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic void setParameterPattern(String parameterPattern)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis.parameterPattern = parameterPattern;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Parse the parameter pattern and return the last part of the name." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterPattern The parameter pattern." + "\n"
				+ "\t * @return The last part of the parameter pattern, i.e. the attribute name." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tprivate String parseParameterName(String parameterPattern)" + "\n"
				+ "\t{" + "\n"
				+ "\t\t// parsing the pattern of the parameter" + "\n"
				+ "\t\tString[] path = CriteriaSearchParameter.PATTERN.split(parameterPattern);" + "\n"
				+ "\t\treturn path[path.length - 1];" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return The last part of the parameter pattern, i.e. the attribute name." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic String getParameterName()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn parseParameterName(parameterPattern);" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return The value of this parameter." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic Object getParameterValue()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn parameterValue;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Sets the value of this parameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param parameterValue The value of this parameter." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic void setParameterValue(Object parameterValue)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis.parameterValue = parameterValue;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return Whether this parameter will be included in the search even if it is <code>null</code>." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic boolean isSearchIfIsNull()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn searchIfIsNull;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Defines whether parameter will be included in the search even if it is <code>null</code>." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param searchIfnull <code>true</code> if the parameter should be included in the search" + "\n"
				+ "\t *                     even if it is null, <code>false</code> otherwise." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic void setSearchIfIsNull(boolean searchIfnull)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis.searchIfIsNull = searchIfnull;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return The hibernate matchmode of this parameter." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic org.hibernate.criterion.MatchMode getMatchMode()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Sets the hibernate matchmode of this parameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param matchMode The hibernate matchmode." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic void setMatchMode(org.hibernate.criterion.MatchMode matchMode)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis.matchMode = matchMode;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return The order (ascending or descending) for this parameter." + "\n"
				+ "\t * @see ORDER_ASC" + "\n"
				+ "\t * @see ORDER_DESC" + "\n"
				+ "\t * @see ORDER_UNSET" + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic int getOrderDirection()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn orderDirection;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Sets the ordering for this parameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param orderDirection The ordering for this parameter." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic void setOrderDirection(int orderDirection)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis.orderDirection = orderDirection;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * @return The relevance for this parameter." + "\n"
				+ "\t * @see RELEVANCE_UNSET" + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic int getOrderRelevance()" + "\n"
				+ "\t{" + "\n"
				+ "\t\treturn orderRelevance;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "\t/**" + "\n"
				+ "\t * Sets the ordering relevance for this parameter." + "\n"
				+ "\t *" + "\n"
				+ "\t * @param order The ordering relevance for this parameter." + "\n"
				+ "\t " + endComment + "\n"
				+ "\tpublic void setOrderRelevance(int relevance)" + "\n"
				+ "\t{" + "\n"
				+ "\t\tthis.orderRelevance = relevance;" + "\n"
				+ "\t}" + "\n"
				+ "" + "\n"
				+ "}" );
		out.close();
	}

	void generateSearchCriteriaConfiguration() throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File (generator.getCoreDir() + File.separator + modelDir + File.separator + "criteria" + File.separator + "CriteriaSearchConfiguration.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());


		out.println ( "//" + "\n"
			+ "// (C) 2013 Soffid" + "\n"
			+ "//" + "\n"
			+ "//" + "\n"
			+ "\n"
			+ "package " + modelPackage + ".criteria;" + "\n"
			+ "" + "\n"
			+ "/**" + "\n"
			+ " * Contains configuration parameters for a <code>CriteriaSearch</code>." + "\n"
			+ " *" + "\n"
			+ " * @author Stefan Reichert" + "\n"
			+ " * @author Peter Friese" + "\n"
			+ " " + endComment + "\n"
			+ "public class CriteriaSearchConfiguration" + "\n"
			+ "{" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Constructs a new CriteriaSearchConfiguration instance with all fields set to" + "\n"
			+ "\t * either <code>null</code> or <code>false</code>." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic CriteriaSearchConfiguration()" + "\n"
			+ "\t{" + "\n"
			+ "\t\tthis.forceEagerLoading = false;" + "\n"
			+ "\t\tthis.firstResult = null;" + "\n"
			+ "\t\tthis.fetchSize = null;" + "\n"
			+ "\t\tthis.maximumResultSize = null;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * If a parameter refers to a childEntity and <code>forceEagerLoading</code> is" + "\n"
			+ "\t * <code>true</code>, the childEntity is always loaded. If <code>forceEagerLoading</code> is" + "\n"
			+ "\t * <code>false</code>, the loading depends on the persister-configuration of the parentEntity." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tprivate boolean forceEagerLoading;" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Returns whether or not eager loading is enabled." + "\n"
			+ "\t *" + "\n"
			+ "\t * @return <code>true</code> if eager loading is enabled, <code>false</code> otherwise" + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic boolean isForceEagerLoading()" + "\n"
			+ "\t{" + "\n"
			+ "\t\treturn forceEagerLoading;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Sets whether or not eager loading is to be enabled." + "\n"
			+ "\t *" + "\n"
			+ "\t * @param forceEagerLoading <code>true</code> if eager loading is to be enabled, <code>false</code> otherwise" + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic void setForceEagerLoading(boolean forceEagerLoading)" + "\n"
			+ "\t{" + "\n"
			+ "\t\tthis.forceEagerLoading = forceEagerLoading;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * The first result to retrieve." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tprivate java.lang.Integer firstResult;" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Gets the first result to retrieve." + "\n"
			+ "\t *" + "\n"
			+ "\t * @return the first result to retrieve" + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic java.lang.Integer getFirstResult()" + "\n"
			+ "\t{" + "\n"
			+ "\t\treturn this.firstResult;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Sets the first result to retrieve." + "\n"
			+ "\t *" + "\n"
			+ "\t * @param firstResult the first result to retrieve" + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic void setFirstResult(java.lang.Integer firstResult)" + "\n"
			+ "\t{" + "\n"
			+ "\t\tthis.firstResult = firstResult;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * The fetch size." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tprivate java.lang.Integer fetchSize;" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Gets the fetch size." + "\n"
			+ "\t *" + "\n"
			+ "\t * @return the fetch size" + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic java.lang.Integer getFetchSize()" + "\n"
			+ "\t{" + "\n"
			+ "\t\treturn this.fetchSize;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Sets the fetch size." + "\n"
			+ "\t *" + "\n"
			+ "\t * @param fetchSize the fetch size" + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic void setFetchSize(java.lang.Integer fetchSize)" + "\n"
			+ "\t{" + "\n"
			+ "\t\tthis.fetchSize = fetchSize;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * If <code>maximumResultSize</code> is not <code>null</code> it limits the maximum size of" + "\n"
			+ "\t * the resultList." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tprivate java.lang.Integer maximumResultSize;" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Gets the maximum size of the search result." + "\n"
			+ "\t *" + "\n"
			+ "\t * @return the maximum size of the search result." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic java.lang.Integer getMaximumResultSize()" + "\n"
			+ "\t{" + "\n"
			+ "\t\treturn this.maximumResultSize;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Sets the maxmimum size of the result." + "\n"
			+ "\t *" + "\n"
			+ "\t * @param maximumResultSize A number indicating how many results will be returned." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic void setMaximumResultSize(java.lang.Integer maximumResultSize)" + "\n"
			+ "\t{" + "\n"
			+ "\t\tthis.maximumResultSize = maximumResultSize;" + "\n"
			+ "\t}" + "\n"
			+ "}" );
	}

	void generateSearchCriteriaProperties() throws FileNotFoundException, UnsupportedEncodingException {
		File f = new File (generator.getCoreDir() + File.separator + modelDir + File.separator + "criteria" + File.separator + "CriteriaSearchProperties.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());


		out.println ( "//" + "\n"
			+ "// (C) 2013 Soffid" + "\n"
			+ "//" + "\n"
			+ "//" + "\n"
			+ "\n"
			+ "package " + modelPackage + ".criteria;" + "\n"
			+ "/**" + "\n"
			+ " * Stores the embedded values and asssociations of all entities in the system by type.  " + "\n"
			+ " * Is used to determine the appropriate parameter name when an embedded value's property " + "\n"
			+ " * is referenced as the attribute to search by (as opposed to an association)." + "\n"
			+ " * " + "\n"
			+ " * @author Chad Brandon" + "\n"
			+ " " + endComment + "\n"
			+ "@SuppressWarnings({\"unchecked\"})" + "\n"
			+ "public class CriteriaSearchProperties" + "\n"
			+ "{" + "\n"
			+ "\tprivate static final java.util.Map embeddedValuesByType = new java.util.HashMap();" + "\n"
			+ "\tprivate static final java.util.Map navigableAssociationEndsByType = new java.util.HashMap();" + "\n"
			+ "\t" + "\n"
			+ "\tstatic" + "\n"
			+ "\t{" );

		int num = 1;
		for (ModelElement element: parser.getEntities())
		{
			if (element instanceof ModelClass && ((ModelClass) element).isEntity() && ((ModelClass) element).isGenerated()) 
			{
				out.println ( "\t\tinitialize" + num + "();" );
				num++;
			}
		}
		out.println ( "\t}" + "\n"
			+ "\t" );
		num = 1;
		for (ModelElement element: parser.getEntities())
		{
			if (element instanceof ModelClass && ((ModelClass) element).isEntity() && ((ModelClass) element).isGenerated()) 
			{
				ModelClass entity = (ModelClass ) element;
				out.println ( "\tprivate static final void initialize" + (num++) + "()" + "\n"
				+ "\t{" + "\n"
				+ "\t\tembeddedValuesByType.put(" + "\n"
				+ "\t\t\t" + entity.getImplFullName(translated) + ".class," + "\n"
				+ "\t\t\tnull);" );
	
				List<ModelAttribute> associations = new LinkedList<ModelAttribute>();
	
				for (ModelAttribute att: entity.getAttributes())
				{
					if (att.getDataType().isEntity())
						associations.add(att);
				}
	
				if (associations.isEmpty()){
					out.println ( "\t\tnavigableAssociationEndsByType.put(" + "\n"
							+ "\t\t\t" + entity.getImplFullName(translated)+ ".class," + "\n"
							+ "\t\t\tnull);" );
	
				} else {
					out.println ( "\t\tnavigableAssociationEndsByType.put(" + "\n"
							+ "\t\t\t" + entity.getImplFullName(translated)+ ".class," + "\n"
							+ "\t\t\tjava.util.Arrays.asList(" + "\n"
							+ "\t\t\t\tnew AssociationType[] " + "\n"
							+ "\t\t\t\t{" );
					int i  = 1;
					for (ModelAttribute att: associations)
					{
						ModelClass type = att.getDataType();
						out.print ( "\t\t\t\t\tnew AssociationType(\"" + att.getName(translated) + "\", "
								+ type.getImplFullName(translated) + ".class)") ;
						if (i != associations.size())
							out.print ( ",");
						out.println ();
						i ++;
					}
					out.println ( "\t\t\t\t}" + "\n"
							+ "\t\t\t)" + "\n"
							+ "\t\t);" );
				}
				out.println ( "\t}" );
			}
		}
		out.println( "\t" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Attempts to get the embedded value list for the given type (or returns null" + "\n"
			+ "\t * if one doesn't exist)." + "\n"
			+ "\t * " + "\n"
			+ "\t * @param type the type of which to retrieve the value." + "\n"
			+ "\t * @return the collection of embedded value names." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic static java.util.Collection getEmbeddedValues(final Class type)" + "\n"
			+ "\t{" + "\n"
			+ "\t\treturn (java.util.Collection)embeddedValuesByType.get(type);" + "\n"
			+ "\t}" + "\n"
			+ "\t" + "\n"
			+ "\t/**" + "\n"
			+ "\t * Gets the type of the navigable association end given the <code>ownerType</code>" + "\n"
			+ "\t * and <code>name</code>" + "\n"
			+ "\t *" + "\n"
			+ "\t * @param ownerType the owner of the association." + "\n"
			+ "\t * @param name the name of the association end to find." + "\n"
			+ "\t * @return the type of the association end." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tpublic static Class getNavigableAssociationEndType(final Class ownerType, final String name)" + "\n"
			+ "\t{" + "\n"
			+ "\t\tfinal java.util.Collection ends = (java.util.Collection)navigableAssociationEndsByType.get(ownerType);" + "\n"
			+ "\t\tfinal AssociationType type = (AssociationType)org.apache.commons.collections.CollectionUtils.find(" + "\n"
			+ "\t\t\tends," + "\n"
			+ "\t\t\tnew org.apache.commons.collections.Predicate()" + "\n"
			+ "\t\t\t{" + "\n"
			+ "\t\t\t\tpublic boolean evaluate(final Object object)" + "\n"
			+ "\t\t\t\t{" + "\n"
			+ "\t\t\t\t\treturn ((AssociationType)object).name.equals(name);" + "\n"
			+ "\t\t\t\t}" + "\n"
			+ "\t\t\t});" + "\n"
			+ "\t\treturn type != null ? type.type : null;" + "\n"
			+ "\t}" + "\n"
			+ "" + "\n"
			+ "\t/**" + "\n"
			+ "\t * A private class storing the association name and type." + "\n"
			+ "\t " + endComment + "\n"
			+ "\tprotected static final class AssociationType" + "\n"
			+ "\t{" + "\n"
			+ "\t\tprotected AssociationType(final String name, final Class type)" + "\n"
			+ "\t\t{" + "\n"
			+ "\t\t\tthis.name = name;" + "\n"
			+ "\t\t\tthis.type = type;" + "\n"
			+ "\t\t}" + "\n"
			+ "\t\tprotected String name;" + "\n"
			+ "\t\tprotected Class type;" + "\n"
			+ "\t}" + "\n"
			+ "}" );
	}


	void generateHibernateDescriptor(ModelClass entity) throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		file = generator.getCoreResourcesDir();

		File f = new File (file+ File.separator + entity.getPackageDir(translated) + entity.getName(translated)+".hbm.xml");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.print ( "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n"
				+ "<!-- " + "\n"
				+ "   (c) Soffid 2012 " + "\n"
				+ "   This file is licensed under GPL v3 license terms " + "\n"
				+ "  -->" + "\n"
				+ "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"" +"\n"
				+ "\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">" + "\n"
				+ "<hibernate-mapping default-cascade='none' ");
		if (generator.isPlugin())
			out.print ( "auto-import='false' ");
		out.println ( ">" );
		if (entity.getSuperClass() != null) {
			out.print ( "\t<subclass extends='"+ entity.getSuperClass().getImplFullName(translated)+ "' "
					+ "name = '" + entity.getImplFullName(translated) + "' ");

		} else {
			out.print ( "\t<class name = '" + entity.getImplFullName(translated) + "' ") ;

		}

//		if (!entity.getTaggedValue("@andromda.hibernate.lazy").empty())
//			out.println ( "lazy='"+entity.getTaggedValue("@andromda.hibernate.lazy")+"' ";
		out.print ( "dynamic-insert='false' dynamic-update='false' " );
		String t = entity.getTableName ();
		if (t != null)
			out.print ( "table='"+t+"' ");
		String d = entity.getDiscriminatorValue();
		if (d != null)
			out.print ( "discriminator-value='" +d+"' ");
		out.println ( ">" );

		ModelAttribute pk = entity . getIdentifier();
		if ( entity.getSuperClass() == null)
		{
			ModelAttribute att = entity . getIdentifier();
			String g = ""+generator.getRootPkg()+".model.identity.IdentityGenerator";
			out.println ( "\t\t<id "
				+ "name='"+att.getName(translated)+"' "
				+ "type='"+att.getHibernateType(translated)+"' unsaved-value='null'>" + "\n"
				+ "\t\t\t<column name='" + att.getColumn()
				// + "' 'sql-type='"+ att.getSqlType()
				+ "'/>" + "\n"
				+ "\t\t\t<generator class='"+g+"'/>" + "\n"
				+ "\t\t</id>");
		}

		d = entity.getDiscriminatorColumn();
		if (d != null)
		{
			out.println ( "\t\t<discriminator column=\"" + d + "\" type=\"string\"/>" );
		}
		//
		// Attributes
		//
		for (ModelAttribute att : entity.getAttributes())
		{
			if (!att.getName(translated).isEmpty() && att != pk) {
				if (att.getDataType().isEntity()) {
					out.println ( "\t\t<many-to-one name='" + att.getName(translated)
							+ "' class='" + att.getDataType().getImplFullName(translated)
							+"' lazy='proxy' fetch='select' foreign-key='" +
								entity.getTableName()
								+ "_" +
								att.getColumn()+"'>" + "\n"
							+ "\t\t\t<column name='" + att.getColumn()
							+ "' not-null='" + (att.isRequired()? "true" : "false" )
							//+ "' sql-type='"+att.getSqlType()
							+"'/>" + "\n"
							+ "\t\t</many-to-one>" );
				} 
				else if (att.getDataType().isCollection() && att.getDataType().getChildClass().isEntity())
				{
					ModelClass foreignClass = att.getDataType().getChildClass();
					ModelAttribute foreignAtt = foreignClass.searchForeignKey(entity, att);
					if (foreignClass == null || foreignAtt == null) {
						// Nothing to do
					} else  {
						out.println ( "\t\t<set name='" + att.getName(translated)
								+"' lazy='true' fetch='select' inverse='true'>" + "\n"
								+ "\t\t\t<key foreign-key='" +
									foreignClass.getTableName()
									+ "_" +
									foreignAtt.getColumn()+"'>" + "\n"
								+ "\t\t\t\t<column name='" + foreignAtt.getColumn()
								//+ "' sql-type='"+foreignAtt.getSqlType()
								+"'/>" + "\n"
								+ "\t\t\t</key>" + "\n"
								+ "\t\t\t<one-to-many class='" + foreignClass.getImplFullName(translated) + "'/>" + "\n"
								+ "\t\t</set>" );
					}
				} else {
					out.println ( "\t\t<property name='" + att.getName(translated)
							+"' type='"+att.getHibernateType(translated)+"'>" );
					out.print ( "\t\t\t<column name='"+att.getColumn()+"' ");
					if (att.isRequired())
						out.print("not-null='true' ");
					else
						out.print("not-null='false' ");
					String length = att.getLength();
					if (length != null && !length.isEmpty())
						out.print ( "length='" + length + "' ");
					else if (att.getHibernateType(translated).equals("Blob"))
						out.print ( "length='128000' ");
					else if (att.getHibernateType(translated).equals( "Clob"))
						out.print ( "length='128000' ");
					// out.println ( " sql-type='"+att.getSqlType() + "' " ;
					out.println ( "/>" );
					out.println (  "\t\t</property>" );
				}
			}
		}
		if (entity.getSuperClass() != null) {
			out.println ( "\t</subclass>" );

		} else {
			out.println ( "\t</class>" ) ;
		}

		out.println ( "</hibernate-mapping>" );

	}

	void generateUml (ModelClass entity) throws IOException {
		generateDaoUml(entity);
		generateRelationshipUml(entity);
	}

	private void generateDaoUml(ModelClass entity) throws IOException {
		String file;
		file = generator.getUmlDir();
		String packageName = entity.getPackage(translated);

		file = file + File.separator + Util.packageToDir(packageName);

		file += entity.getName(translated);
		file += "-dao.svg";
		File f = new File (file);
		f.getParentFile().mkdirs();

		boolean generate = Util.isModifiedClass(entity, f);

		StringBuffer source = new StringBuffer();
		source.append ("@startuml" + endl +
				"skinparam class {"+endl+
				"BackgroundColor<<Entity>> Wheat"+endl+
				"BackgroundColor<<ValueObject>> Pink"+endl+
				"BackgroundColor<<Service>> LightBlue"+endl+
				"}" +endl);
		source.append (entity.generatePlantUml(translated, true, true));

		for (ModelAttribute foreignAtt: entity.getForeignKeys())
		{
			ModelClass foreignClass = foreignAtt.getModelClass();
			if (foreignClass.isEntity() && foreignClass != entity)
			{
				generate = generate || Util.isModifiedClass(foreignClass, f);
				source.append (foreignClass.generatePlantUml(translated, true, false));
				ModelAttribute att = entity.searchReverseForeignKey(foreignClass, foreignAtt);
				source.append (entity.getName(translated));
				if (foreignAtt.isRequired())
					source.append (" \"1 "+foreignAtt.getName(translated)+"\" ");
				else
					source.append (" \"0..1 "+foreignAtt.getName(translated)+"\" ");

				if (att == null)
					source.append (" <-- \"0..*\" ");
				else
					source.append (" -- \"0..* "+att.getName(translated)+"\" ");
				source.append (foreignClass.getName(translated))
					.append (endl);
			}
		}
		for (ModelAttribute att: entity.getAttributes())
		{
			if (att.getDataType().isEntity())
			{
				ModelClass foreignClass = att.getDataType();
				generate = generate || Util.isModifiedClass(foreignClass, f);
				source.append (att.getDataType().generatePlantUml(translated, true, false));
				ModelAttribute foreignAtt = foreignClass.searchReverseForeignKey(entity, att);
				source.append (entity.getName(translated));
				if (foreignAtt == null)
					source.append ("\"0..*\" -->");
				else
					source.append ("\"0..* "+foreignAtt.getName(translated)+"\" --");
				if (att.isRequired())
					source.append (" \"1 "+att.getName(translated)+"\" ");
				else
					source.append (" \"0..1 "+att.getName(translated)+"\" ");
				source.append(foreignClass.getName(translated));
				source.append(endl);
			}
		}
		for (ModelClass provider: entity.getDepends()) {
			generate = generate || Util.isModifiedClass(provider, f);
			if (provider.isEntity())
			{
				source.append( provider.generatePlantUml(translated, false, false) );
			}
			else if (provider.isValueObject())
			{
				source.append( provider.generatePlantUml(translated, true, false) );
			}
			else
			{
				source.append( provider.generatePlantUml(translated, false, false) );
			}
			source.append (entity.getName(translated) + " ..> "+provider.getName(translated)+endl);
		}
		source.append ("@enduml");

		if (generate)
		{
			System.out.println ( "Generating " + f.getPath() );
			SourceStringReader reader = new SourceStringReader(source.toString());
			reader.generateImage(new FileOutputStream(f), new FileFormatOption(FileFormat.SVG));
		}
	}

	private void addEntities (ModelClass entity, List<ModelClass> entities, int depth, StringBuffer source)
	{
		LinkedList<ModelClass> entitiesToParse = new LinkedList<ModelClass>();
		entitiesToParse.add(entity);
		entities.add(entity);
		source.append (entity.generatePlantUml(translated, depth > 0, false, "#8080ff"));
		while (depth > 0)
		{
			depth --;
			LinkedList <ModelClass> nextLevel = new LinkedList<ModelClass>();
			for (ModelClass e: entitiesToParse)
			{
				if (e.isGenerated())
				{
					for (ModelAttribute foreignAtt: e.getForeignKeys())
					{
						ModelClass foreignClass = foreignAtt.getModelClass();
						if (foreignClass.isEntity())
						{
							nextLevel.add (foreignClass);
						}
					}
					
					for (ModelAttribute att: e.getAttributes())
					{
						ModelClass foreignClass = att.getDataType();
						if (foreignClass.isEntity())
						{
							nextLevel.add (foreignClass);
						}
					}
				}
			}
			entitiesToParse.clear();
			for (ModelClass e: nextLevel)
			{
				if (! entities.contains(e))
				{
					entitiesToParse.add(e);
					entities.add(e);
					source.append (e.generatePlantUml(translated, depth >= 0, false));
				}
			}
		}
		
	}
	
	private void generateRelationshipUml(ModelClass entity) throws IOException {
		String file;
		file = generator.getUmlDir();
		String packageName = entity.getPackage(translated);

		file = file + File.separator + Util.packageToDir(packageName);

		file += entity.getName(translated);
		file += "-er.svg";
		File f = new File (file);
		f.getParentFile().mkdirs();

		boolean generate = Util.isModifiedClass(entity, f);
		LinkedList<ModelClass> entities = new LinkedList<ModelClass>();
		
		StringBuffer source = new StringBuffer();
		source.append ("@startuml" + endl +
				"skinparam class {"+endl+
				"BackgroundColor<<Entity>> Wheat"+endl+
				"BackgroundColor<<ValueObject>> Pink"+endl+
				"BackgroundColor<<Service>> LightBlue"+endl+
				"}" +endl);

		addEntities (entity, entities, 2, source);

		int threshold = 2 * entities.size() / 3;
		int current = 0;
		for (Iterator<ModelClass> it = entities.iterator(); it.hasNext();)
		{
			ModelClass e = it.next();
			generate = generate || Util.isModifiedClass(e, f);

			String separator = current > threshold ? "--" : "---";
			current ++;
			
			for (ModelAttribute foreignAtt: e.getForeignKeys())
			{
				ModelClass foreignClass = foreignAtt.getModelClass();
				generate = generate || Util.isModifiedClass(foreignClass, f);
				ModelAttribute myAtt = e.searchReverseForeignKey(foreignClass, foreignAtt);

				if (foreignClass.isEntity() && entities.contains(foreignClass) && foreignClass != e)
				{
					source.append (foreignClass.getName(translated));			
					if (myAtt == null)
						source.append (" \"0..*\" "+separator+">");
					else
						source.append (" \"0..* "+myAtt.getName(translated)+"\" "+separator);
					if (foreignAtt.isRequired())
						source.append (" \"1 "+foreignAtt.getName(translated)+"\" ");
					else
						source.append (" \"0..1 "+foreignAtt.getName(translated)+"\" ");
	
					source.append(e.getName(translated));
					source.append(endl);
				}
			}
			
			for (ModelAttribute att: e.getAttributes())
			{
				if (att.getDataType().isEntity() && entities.contains(att.getDataType()))
				{
					ModelClass foreignClass = att.getDataType();
					ModelAttribute foreignAtt = foreignClass.searchReverseForeignKey(e, att);
					source.append (e.getName(translated));
					if (foreignAtt == null)
						source.append (" \"0..*\" "+separator+">");
					else
						source.append (" \"0..* "+foreignAtt.getName(translated)+"\" "+separator);
					if (att.isRequired())
						source.append (" \"1 "+att.getName(translated)+"\" ");
					else
						source.append (" \"0..1 "+att.getName(translated)+"\" ");
					source.append(foreignClass.getName(translated));
					source.append(endl);
				}
			}
			it.remove();
		}
		source.append ("@enduml");
		
		if (generate)
		{
			System.out.println ( "Generating " + f.getPath() );
	
			SourceStringReader reader = new SourceStringReader(source.toString());
			reader.generateImage(new FileOutputStream(f), new FileFormatOption(FileFormat.SVG));
		}
	}
}
