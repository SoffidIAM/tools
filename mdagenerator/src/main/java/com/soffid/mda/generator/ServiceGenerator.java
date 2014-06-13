package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soffid.mda.parser.ModelAttribute;
import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class ServiceGenerator {

	private Generator generator;
	private Parser parser;
	private boolean translated;
	private String pkg;
	private String rootPkg;

	final static String endl = "\n";
	
	public void generate(Generator generator, Parser parser) throws IOException {
		this.generator = generator;
		this.parser = parser;
		this.translated = generator.isTranslatedOnly();
		if (generator.isTranslatedOnly())
		{
			rootPkg = "com.soffid.iam";
		}
		else
		{
			rootPkg = "es.caib.seycon.ng";
		}

		if (generator.isPlugin())
		{
			pkg = "com.soffid.iam.addons." + generator.getPluginName();
		}
		else
		{
			pkg = rootPkg;
		}
		for (ModelClass service: parser.getServices()) {
			boolean translated2 = this.translated;
			do
			{
				if (translated2 == translated)
				{
					generateInterface (service, translated);
					generateBase(service, translated);
					if (generator.isGenerateUml())
					{
						generateUml(service, translated);
						generateUmlUseCase(service, translated);
					}
				}
				if (! service.isInternal() && !service.isServerOnly())
				{
					generateEjbHome(service, translated2);
					generateEjbInterface (service, translated2);
					generateEjbBean(service, translated2);
				}
				if (translated2 || ! service.isTranslated())
					break;
				translated2 = true;
			} while (true);
		}

		generateEjbJarXml();
		generateJbossXml();
		generateServiceLocator();
		generateEjbLocator(false);
		generateEjbLocator(true);
		generateRemoteServiceLocator();
		generateRemoteServicePublisher();

	}
	
	
	void generateRemoteServicePublisher () throws FileNotFoundException, UnsupportedEncodingException
	{
		String file;
		file = generator.getSyncDir();

		file = file + "/" + Util.packageToDir(pkg) + "remote/PublisherInterface.java";
		File f = new File (file);
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" + endl
			+ "// (C) 2013 Soffid" + endl
			+ "//" + endl
			+ "//" + endl
			+ endl
				+ "package "+pkg+".remote;" + endl
				+ "" + endl
				+ "" + endl
				+ "/**" + endl
				+ " * Locates and provides all available application services." + endl
				+ " */" + endl
				+ "public interface PublisherInterface" + endl
				+ "{" + endl
				+ "\tvoid publish (Object bean, String path, String role) throws Exception;" + endl
				+ "}" );
		out.close();

		f = new File (generator.getSyncDir() + "/" + Util.packageToDir(pkg)+"remote/RemoteServicePublisher.java");
		f.getParentFile().mkdirs();
		out = new PrintStream(f, "UTF-8");

		out.println ( "//" + endl
			+ "// (C) 2013 Soffid" + endl
			+ "//" + endl
			+ "//" + endl
			+ endl
				+ "package "+pkg+".remote;" + endl
				+ "" + endl
				+ "" + endl
				+ "/**" + endl
				+ " * Locates and provides all available application services." + endl
				+ " */" + endl
				+ "public class RemoteServicePublisher" + endl
				+ "{" + endl
				+ "\tpublic void publish ("+pkg+".ServiceLocator locator, PublisherInterface publisher) throws Exception" + endl
				+ "\t{" + endl
				+ "\t\tObject bean;" );

		for (ModelClass service: parser.getServices()) {
			String path = service.getServerPath();
			String role = service.getServerRole();
			if (! service.isInternal() && ! path.isEmpty()) {
				out.println ( "\t\tbean = locator.getService(\"" + service.getSpringBeanName(generator, false) + "\");" + endl
					+ "\t\tif (bean != null)" + endl
					+ "\t\t\tpublisher.publish(bean, \"" + path + "\", \"" + role + "\");" + endl );
			}
		}
		out.println ( "\t}" + endl
				+ "}" );
		out.close ();
	}

	void generateRemoteServiceLocator () throws FileNotFoundException, UnsupportedEncodingException
	{
		String file;
		file = generator.getCommonsDir();

		String packageName = pkg + ".remote";
		file = file + "/"+Util.packageToDir (pkg)+"remote";
		File f = new File (file + "/RemoteServiceLocator.java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		String commonPkg = translated ? "com.soffid.iam" : "es.caib.seycon.ng";

		out.println ( "//" + endl
			+ "// (C) 2013 Soffid" + endl
			+ "//" + endl
			+ "//" + endl
			+ endl
				+ "package " + packageName + ";" + endl
				+ "" + endl
				+ "import java.io.FileNotFoundException;" + endl
				+ "import java.io.IOException;" + endl
				+ "import java.util.Arrays;" + endl
				+ "" + endl
				+ "" + endl
				+ "import "+commonPkg+".config.Config;" + endl
				+ "import "+commonPkg+".exception.InternalErrorException;" + endl
				+ "import "+commonPkg+".remote.RemoteInvokerFactory;" + endl
				+ "import "+commonPkg+".remote.URLManager;" + endl
				+ "" + endl
				+ "/**" + endl
				+ " * Locates and provides all available application services." + endl
				+ " */" + endl
				+ "public class RemoteServiceLocator" + endl
				+ "{" + endl
//				+ "\torg.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(RemoteServiceLocator.class);" + endl
				+ "" + endl
				+ "\tString server = null;" + endl
				+ "\tString authToken = null;" + endl
				+ "\t" + endl
				+ "\tpublic RemoteServiceLocator()" + endl
				+ "\t{" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\tpublic RemoteServiceLocator(String server)" + endl
				+ "\t{" + endl
				+ "\t	setServer (server);" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\tpublic String getServer() {" + endl
				+ "\t\treturn server;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\tpublic void setServer(String server) {" + endl
				+ "\t\tthis.server = server;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\tpublic String getAuthToken() {" + endl
				+ "\t\treturn authToken;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\tpublic void setAuthToken(String authToken) {" + endl
				+ "\t\tthis.authToken = authToken;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t" + endl
				+ "\tprivate static int roundRobin = 0;" + endl
				+ "\t" + endl
				+ "\tpublic Object getRemoteService (String serviceName) throws IOException, InternalErrorException {" + endl
				+ "\t\tObject robj;" + endl
				+ "\t\tRemoteInvokerFactory factory = new RemoteInvokerFactory();" + endl
				+ "\t\t" + endl
				+ "\t\tConfig config = Config.getConfig();" + endl
				+ "" + endl
				+ "\t\tString list[];" + endl
				+ "\t\tif (server == null) {" + endl
				+ "\t\t\tlist = config.getServerList().split(\"[, ]+\");" + endl
				+ "\t\t} else {" + endl
				+ "\t\t\tlist = new String[] {server} ;" + endl
				+ "\t\t}" + endl
				+ "\t\t\t" + endl
				+ "\t\troundRobin ++;" + endl
				+ "\t\tException lastException  = null;" + endl
				+ "\t\tfor (int i = 0; i < list.length; i++) {" + endl
				+ "\t\t\tURLManager m = null;" + endl
				+ "\t\t\ttry {" + endl
				+ "\t\t\t\tm = new URLManager(list[ (i+roundRobin) % list.length ]);" + endl
				+ "\t\t\t\tif (authToken == null)" + endl
				+ "	                return factory.getInvoker(m.getHttpURL(serviceName));" + endl
				+ "	            else" + endl
				+ "	                return factory.getInvoker(m.getHttpURL(serviceName), authToken);" + endl
				+ "\t\t\t} catch (Exception e) {" + endl
//				+ "\t\t\t\tlog.warn(\"Unable to locate server at \" + m.getServerURL(), e);" + endl
				+ "\t\t\t\tlastException = e;" + endl
				+ "\t\t\t}" + endl
				+ "\t\t}" + endl
				+ "\t\tthrow new IOException (\"Unable to locate remote service \"+serviceName, lastException);" + endl
				+ "\t}" + endl
				+ " " + endl
				+ " " );

		for (ModelClass service: parser.getServices()) {
			String path = service.getServerPath();
			if (! service.isInternal() && ! path.isEmpty()) {
				out.println ( "	/**" + endl
				+ "	 * Gets the remote service " + service.getName(translated) + "." + endl
				+ "	 *" + endl
				+ "	 * @return Remote object" + endl
				+ "	 **/" + endl
				+ "\tpublic " + service.getFullName(translated) + " get" + service.getName(translated) + "( ) throws IOException, InternalErrorException {" + endl
				+ "\t\treturn ( " + service.getFullName(translated) + " ) getRemoteService (\"" + path + "\");" + endl
				+ "\t}" + endl
				+ "\t" );
			}
		}
		out.println ( "" + endl
				+ "}" );
		out.close ();
	}


	void generateServiceLocator () throws FileNotFoundException, UnsupportedEncodingException {

		String file;
		file = generator.getCoreDir();

		String packageName;
		String className;
		if (generator.isPlugin())
		{
			file =  file + "/com/soffid/iam/addons/" + generator.getPluginName();
			packageName = "com.soffid.iam.addons." + generator.getPluginName();
			className = Util.firstUpper(generator.getPluginName())+"ServiceLocator";
		}
		else
		{
			if (generator.isTranslatedOnly())
			{
				file = file + "/com/soffid/iam";
				packageName = "com.soffid.iam";
			}
			else
			{
				file = file + "/es/caib/seycon/ng";
				packageName = "es.caib.seycon.ng";
			}
			className = "ServiceLocator";
		}
		String commonPkg = translated ? "com.soffid.iam" : "es.caib.seycon.ng";

		File f = new File (file+"/"+className+".java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out.println ( "//" + endl
			+ "// (C) 2013 Soffid" + endl
			+ "//" + endl
			+ "//" + endl
			+ endl
			+ "package " + packageName + ";" + endl
			+ "" + endl
			+ "/**" + endl
			+ " * Locates and provides all available application services." + endl
			+ " */" );
		out.println ( "public class "+className );
		out.println ( "{" + endl
			+ "" + endl
			+ "\tprivate "+className+"()" + endl
			+ "\t{" + endl
			+ "\t\t// shouldn't be instantiated" + endl
			+ "\t}" + endl
			+ "" + endl
			+ "\t/**" + endl
			+ "\t * The shared instance of this ServiceLocator." + endl
			+ "\t */" + endl
			+ "\tprivate final static "+className+" instance = new "+className+"();" + endl
			+ "" + endl
			+ "\t/**" + endl
			+ "\t * Gets the shared instance of this Class" + endl
			+ "\t *" + endl
			+ "\t * @return the shared service locator instance." + endl
			+ "\t */" + endl
			+ "\tpublic static final "+className+" instance()" + endl
			+ "\t{" + endl
			+ "\t\treturn instance;" + endl
			+ "\t}" + endl
			+ "" );
		if ( ! generator.isPlugin())
		{
			out.println (
				"\t/**" + endl
				+ "\t * The bean factory reference instance." + endl
				+ "\t */" + endl
				+ "\tprivate org.springframework.beans.factory.access.BeanFactoryReference beanFactoryReference;" + endl
				+ "\t" + endl
				+ "\t/**" + endl
				+ "\t * The bean factory reference location." + endl
				+ "\t */" + endl
				+ "\tprivate String beanFactoryReferenceLocation;" + endl
				+ "\t" + endl
				+ "\t/**" + endl
				+ "\t * The bean factory reference id." + endl
				+ "\t */" + endl
				+ "\tprivate String beanRefFactoryReferenceId;" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Initializes the Spring application context from" + endl
				+ "\t * the given <code>beanFactoryReferenceLocation</code>.  If <code>null</code>" + endl
				+ "\t * is specified for the <code>beanFactoryReferenceLocation</code>" + endl
				+ "\t * then the default application context will be used." + endl
				+ "\t *" + endl
				+ "\t * @param beanFactoryReferenceLocation the location of the beanRefFactory reference." + endl
				+ "\t */" + endl
				+ "\tpublic synchronized void init(final String beanFactoryReferenceLocation, final String beanRefFactoryReferenceId)" + endl
				+ "\t{" + endl
				+ "\t\tthis.beanFactoryReferenceLocation = beanFactoryReferenceLocation;" + endl
				+ "\t\tthis.beanRefFactoryReferenceId = beanRefFactoryReferenceId;" + endl
				+ "\t\tthis.beanFactoryReference = null;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Initializes the Spring application context from" + endl
				+ "\t * the given <code>beanFactoryReferenceLocation</code>.  If <code>null</code>" + endl
				+ "\t * is specified for the <code>beanFactoryReferenceLocation</code>" + endl
				+ "\t * then the default application context will be used." + endl
				+ "\t *" + endl
				+ "\t * @param beanFactoryReferenceLocation the location of the beanRefFactory reference." + endl
				+ "\t */" + endl
				+ "\tpublic synchronized void init(final String beanFactoryReferenceLocation)" + endl
				+ "\t{" + endl
				+ "\t\tthis.beanFactoryReferenceLocation = beanFactoryReferenceLocation;" + endl
				+ "\t\tthis.beanFactoryReference = null;" + endl
				+ "\t}" + endl
				+ "\t/**" + endl
				+ "\t * The default bean reference factory location." + endl
				+ "\t */" + endl
				+ "\tprivate final String DEFAULT_BEAN_REFERENCE_LOCATION = \"beanRefFactory.xml\";" + endl
				+ "\t" + endl
				+ "\t/**" + endl
				+ "\t * The default bean reference factory ID." + endl
				+ "\t */" + endl
				+ "\tprivate final String DEFAULT_BEAN_REFERENCE_ID = \"beanRefFactory\";" + endl
				+ "" + endl
				+ "\t/**" + endl
				+ "\t * Gets the Spring ApplicationContext." + endl
				+ "\t */" + endl
				+ "\tpublic synchronized org.springframework.context.ApplicationContext getContext()" + endl
				+ "\t{" + endl
				+ "\t\tif (this.beanFactoryReference == null)" + endl
				+ "\t\t{" + endl
				+ "\t\t\tif (this.beanFactoryReferenceLocation == null)" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\tthis.beanFactoryReferenceLocation = DEFAULT_BEAN_REFERENCE_LOCATION;" + endl
				+ "\t\t\t}" + endl
				+ "\t\t\tif (this.beanRefFactoryReferenceId == null)" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\tthis.beanRefFactoryReferenceId = DEFAULT_BEAN_REFERENCE_ID;" + endl
				+ "\t\t\t}" + endl
				+ "\t\t\torg.springframework.beans.factory.access.BeanFactoryLocator beanFactoryLocator =" + endl
				+ "\t\t\t\torg.springframework.context.access.ContextSingletonBeanFactoryLocator.getInstance(" + endl
				+ "\t\t\t\t\tthis.beanFactoryReferenceLocation);" + endl
				+ "\t\t\tthis.beanFactoryReference = beanFactoryLocator.useBeanFactory(this.beanRefFactoryReferenceId);" + endl
				+ "\t\t}" + endl
				+ "\t\treturn (org.springframework.context.ApplicationContext)this.beanFactoryReference.getFactory();" + endl
				+ "\t}" + endl
				+ "" );
			out.println ( "\t/**" + endl
					+ "\t * Shuts down the ServiceLocator and releases any used resources." + endl
					+ "\t */" + endl
					+ "\tpublic synchronized void shutdown()" + endl
					+ "\t{" + endl
					+ "\t\tif (this.beanFactoryReference != null)" + endl
					+ "\t\t{" + endl
					+ "\t\t\tthis.beanFactoryReference.release();" + endl
					+ "\t\t\tthis.beanFactoryReference = null;" + endl
					+ "\t\t}" + endl
					+ "\t}" + endl
					+ "" );
		} else {
			out.println (
				"\t/**" + endl
				+ "\t * Gets the Spring ApplicationContext." + endl
				+ "\t */" + endl
				+ "\tpublic synchronized org.springframework.context.ApplicationContext getContext()" + endl
				+ "\t{" + endl
				+ "\t\treturn "+commonPkg+".ServiceLocator.instance().getContext();" + endl
				+ "\t}" + endl
				+ "" );
			out.println ( "\t/**" + endl
					+ "\t * Shuts down the ServiceLocator and releases any used resources." + endl
					+ "\t */" + endl
					+ "\tpublic synchronized void shutdown()" + endl
					+ "\t{" + endl
					+ "\t\t"+commonPkg+".ServiceLocator.instance().shutdown();" + endl
					+ "\t}" + endl
					+ "" );
		}

		for (ModelClass service: parser.getServices ()) {
			out.println ( "\t/**" + endl
				+ "\t * Gets an instance of {@link "+ service.getFullName(translated)+"}." + endl
				+ "\t */" + endl
				+ "\tpublic final "+ service.getFullName(translated)+" get"+ service.getName(translated)+"()" + endl
				+ "\t{" + endl
				+ "\t\treturn ("+ service.getFullName(translated)+")" + endl
				+ "\t\t\tgetContext().getBean(\""+ service.getSpringBeanName(generator, false)+"\");" + endl
				+ "\t}" + endl
				+ "" );
		}
		if (! generator.isPlugin())
		{
			out.println( "\t/**" + endl
				+ "\t * Gets an instance of the given service." + endl
				+ "\t */" + endl
				+ "\tpublic final Object getService(String serviceName)" + endl
				+ "\t{" + endl
				+ "\t\treturn getContext().getBean(serviceName);" + endl
				+ "\t}" + endl
				+ "" );
		}
		out.println ( "}" );


	}

	void generateEjbLocator (boolean translated) throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		file = generator.getCommonsDir();

		String packageName;
		if (generator.isPlugin())
		{
			if (translated)
			{
				file = file + "/com/soffid/iam/addons/";
				packageName = "com.soffid.iam.addons.";
			}
			else
			{
				file = file + "/es/caib/seycon/ng/addons/";
				packageName = "es.caib.seycon.ng.addons.";
			}
			file = file + generator.getPluginName();
			packageName = packageName + generator.getPluginName();
			return;
		}
		else
		{
			if (translated)
			{
				file = file + "/com/soffid/iam";
				packageName = "com.soffid.iam";
			}
			else
			{
				file = file + "/es/caib/seycon/ng";
				packageName = "es.caib.seycon.ng";
			}
		}

		String name = "EJBLocator";
		File f = new File (file+File.separator+ name + ".java");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		
		out.println ( "//" + endl
			+ "// (C) 2013 Soffid" + endl
			+ "//" + endl
			+ "//" + endl
			+ endl
			+ "package " + packageName + ";" + endl
			+ "" + endl
			+ "/**" + endl
			+ " * Locates and provides all available application services." + endl
			+ " */" );
		if (generator.isPlugin())
			out.println ( "public class " + name + " extends com.soffid.iam.ServiceLocator" );
		else
			out.println ( "public class " + name );
		out.println ( "{" + endl
			+ "" );

		for (ModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				out.println( "\t/**" + endl
					+ "\t * Gets an instance of {@link "+ service.getEjbInterfaceFullName(translated) + "}." + endl
					+ "\t */" + endl
					+ "\tpublic static "+ service.getEjbInterfaceFullName(translated)+" get"+ service.getName(translated)+"()" + endl
					+ "\t\tthrows javax.naming.NamingException, javax.ejb.CreateException" + endl
					+ "\t{" + endl
					+ "\t\t" + service.getEjbHomeFullName(translated) + " home = ("
						+ service.getEjbHomeFullName(translated) +  ") " + endl
					+ "\t\t\tnew javax.naming.InitialContext()." + endl
						+ "\t\t\t\tlookup("+ service.getEjbHomeFullName(translated) + ".JNDI_NAME);" + endl
					+ "\t\treturn home.create();" + endl
					+ "\t}" + endl
					+ "" );
			}
		}
		out.println ( "}" );


	}

	void generateEjbJarXml () throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		File f = new File (generator.getCoreResourcesDir() + "/META-INF/ejb-jar.xml");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");


		out.println ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl
			+ "<!DOCTYPE ejb-jar PUBLIC \"-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN\"" + endl
			+ "\t\t\t\t\t\t \"http://java.sun.com/dtd/ejb-jar_2_0.dtd\">" + endl
			+ "" + endl
			+ "<ejb-jar>" + endl
			+ "" + endl
			+ "\t<description><![CDATA[No Description.]]></description>" + endl
			+ "\t<display-name>Generated by Soffid generator</display-name>" + endl
			+ "" + endl
			+ "\t<enterprise-beans>" + endl
			+ "" );

		for (ModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{

				boolean translated = false;
				do
				{
					out.println ( "\t\t<session>" + endl
							+ "\t\t\t<description>" + endl
							+ "\t\t\t\t<![CDATA[" + endl
							+ "\t\t\t\t" + Util.formatXmlComments(service.getComments(), "\t\t\t\t\t") + endl
							+ "\t\t\t\t]]>" + endl
							+ "\t\t\t</description>" + endl
							+ "\t\t\t<ejb-name>" + service.getEjbName(translated) + "</ejb-name>" + endl
							+ "\t\t\t<local-home>" + service.getEjbHomeFullName(translated) + "</local-home>" + endl
							+ "\t\t\t<local>" + service.getEjbInterfaceFullName(translated) + "</local>" + endl
							+ "\t\t\t<ejb-class>" + service.getBeanFullName(translated) + "</ejb-class>" + endl
							+ "\t\t\t<session-type>Stateless</session-type>" + endl
							+ "\t\t   " + endl
							+ "\t\t\t<transaction-type>Container</transaction-type>" + endl
							+ "\t\t</session>" + endl
							+ "" );
					if (translated || ! service.isTranslated())
						break;
					translated = true;
				} while (true);
			}
		}
		out.println ( "\t</enterprise-beans>" + endl
			+ "" + endl
			+ "\t<assembly-descriptor>" + endl
			+ "" );

		for (ModelClass actor: parser.getActors ())
		{
			out .println ( "\t\t<security-role>" + endl
					+ "\t\t\t<description><![CDATA["
					+ Util.formatXmlComments(actor.getComments(), "\t\t\t\t") + "]]></description>" + endl
					+ "\t\t\t<role-name>" + actor.getRoleName() + "</role-name>" + endl
					+ "\t\t</security-role>" + endl
					);
		}

		for (ModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				Set<ModelClass> allActors = service.getAllActors();
				if (!allActors.isEmpty())
				{
					boolean translated = false;
					do
					{
						out.println ( "\t\t<method-permission>" + endl
								+ "\t\t\t<description><![CDATA[Create method security constraint]]></description>" ) ;
						// Create methods
						for (ModelClass actor: allActors)
						{
							if (actor.getRoleName().equals( "anonymous"))
								out.println ( "\t\t\t<unchecked/>" );
							else
								out.println ( "\t\t\t<role-name>" + actor.getRoleName() + "</role-name>" );
						}
						out.println ( "\t\t\t<method>" + endl
								+ "\t\t\t\t<description><![CDATA[Creates the " + service.getName(translated) + " Session EJB]]></description>" + endl
								+ "\t\t\t\t<ejb-name>" + service.getEjbName(translated) + "</ejb-name>" + endl
								+ "\t\t\t\t<method-intf>LocalHome</method-intf>" + endl
								+ "\t\t\t\t<method-name>create</method-name>" + endl
								+ "\t\t\t</method>" + endl
								+ "\t\t\t<method>" + endl
							+ "\t\t\t\t<description><![CDATA[Removes the " + service.getName(translated) + " Session EJB]]></description>" + endl
							+ "\t\t\t\t<ejb-name>" + service.getEjbName(translated) + "</ejb-name>" + endl
							+ "\t\t\t\t<method-intf>Local</method-intf>" + endl
							+ "\t\t\t\t<method-name>remove</method-name>" + endl
							+ "\t\t\t</method>" + endl
							+ "\t\t</method-permission>" + endl
							+ "" );
						if (translated || ! service.isTranslated())
							break;
						translated = true;
					} while (true);
				}

				for (ModelOperation op: service.getOperations())
				{
					Set<ModelClass> actors = op.getActors();
					if (!actors.isEmpty())
					{
						Boolean translated = false;
						do
						{
							out.println ( "\t\t<method-permission>" + endl
									+ "\t\t\t<description><![CDATA[" + op.getSpec(translated) + " security constraint]]></description>" );
							for (ModelClass actor: actors)
							{
								if (actor.getRoleName().equals ("anonymous"))
								{
									out.println ( "\t\t\t<unchecked/>" );
									break;
								}
								else
									out.println ( "\t\t\t<role-name>" + actor.getRoleName() + "</role-name>" );
							}
							out.println ( "\t\t\t<method>" + endl
									+ "\t\t\t\t<description><![CDATA[" + Util.formatXmlComments(op.getComments(), "\t\t\t\t\t") + "]]></description>" + endl
							+ "\t\t\t\t<ejb-name>"+ service.getEjbName(translated) + "</ejb-name>" + endl
							+ "\t\t\t\t<method-intf>Local</method-intf>" + endl
							+ "\t\t\t\t<method-name>" + op.getName(translated) + "</method-name>" );

							if ( ! op.getParameters().isEmpty()) {
								out.println ( "\t\t\t\t<method-params>" );
								for (ModelParameter param: op.getParameters())
								{
									out.println ( "\t\t\t\t\t<method-param>"+
											param.getDataType().getRawType()
										+ "</method-param>" );
								}
								out.println ( "\t\t\t\t</method-params>" );
							}
							out.println ( "\t\t\t</method>" + endl
									+ "\t\t</method-permission>" + endl
									+ "" );
							if (translated || ! service.isTranslated())
								break;
							translated = true;
						} while (true);
					}
				}
			}
		}

		for (ModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				for (ModelOperation op: service.getOperations())
				{
					boolean translated = false;
					do
					{
						out.println ( "\t\t<container-transaction>" + endl
								+ "\t\t\t<method>" + endl
								+ "\t\t\t\t<ejb-name>" + service.getEjbName(translated) + "</ejb-name>" + endl
								+ "\t\t\t\t<method-name>" + op.getName(translated)+ "</method-name>" + endl
								+ "\t\t\t\t<method-params>" );
						for (ModelParameter param: op.getParameters())
						{
								out.println ( "\t\t\t\t\t<method-param>"
									+ param.getDataType().getRawType()
									+ "</method-param>" );
						}
						out.println ( "\t\t\t\t</method-params>" + endl
								+ "\t\t\t</method>" + endl
								+ "\t\t\t<trans-attribute>Supports</trans-attribute>" + endl
								+ "\t\t</container-transaction>" + endl
								+ "" );
						if (translated || ! service.isTranslated())
							break;
						translated = true;
					} while (true);
				}
			}
		}
		out.println ( "\t</assembly-descriptor>" + endl
			+ "" + endl
			+ "</ejb-jar>" );
		out.close ();
	}



	void generateJbossXml () throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		File f = new File (generator.getCoreResourcesDir() + "/META-INF/jboss.xml");
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");
		System.out.println ("Generating "+f.getPath());


		out.println ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl
				+ "<!DOCTYPE jboss PUBLIC \"-//JBoss//DTD JBOSS 3.0//EN\" " + endl
				+ "                       \"http://www.jboss.org/j2ee/dtd/jboss_3_2.dtd\">" + endl
				+ "" + endl
				+ "<jboss>" + endl
				+ "" + endl
				+ "\t<security-domain>java:/jaas/seycon</security-domain>" + endl
				+ "\t" + endl
				+ "\t<enterprise-beans>" + endl
				+ "" );

		for (ModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				boolean translated = false;
				do
				{
					out.println ( "\t\t<session>" + endl
						+ "\t\t\t<ejb-name>" + service.getEjbName(translated) + "</ejb-name>" + endl
						+ "\t\t\t<local-jndi-name>soffid/ejb/" + service.getFullName(translated) + "</local-jndi-name>" + endl
						+ "\t\t</session>" + endl
						+ "" );
					if (translated || ! service.isTranslated())
						break;
					translated = true;
				} while (true);
			}
		}
		out.println ( "   </enterprise-beans>" + endl
				+ "" + endl
				+ "</jboss>" );

	}


	void generateInterface (ModelClass service, boolean translated) throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		if (service.isInternal())
		{
			file = generator.getCoreDir();
		}
		else {
			file = generator.getCommonsDir();
		}
		String packageName = service.getPackage(translated);

		file = file + File.separator + Util.packageToDir(packageName);

		file += service.getName(translated);
		file += ".java";
		File f = new File (file);
		f.getParentFile().mkdirs();
		
		PrintStream out = new PrintStream(f, "UTF-8");


		out.println ( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (!packageName.isEmpty())
			out.println ( "package " + packageName + ";" );

		out.println ( "/**" + endl
				+ " * Service " + service.getName(translated) + endl
				+ Util.formatComments(service.getComments())
				+ " */" );
		out.print ( "public interface " + service.getName(translated) );
		boolean first = true;
		if (service.getSuperClass() != null) {
			out.print ( " extends " + service.getSuperClass().getFullName(translated) );
			first = false;
		}

		out.println ( " {" );
		String serverPath = service.getServerPath() ;
		if (!serverPath.isEmpty())
			out.println ( "\tpublic final static String REMOTE_PATH = \""
			+ serverPath + "\";" + endl );
		out.println ( "\tpublic final static String SERVICE_NAME = \""
				+ service.getSpringBeanName(generator, translated) + "\";" + endl );

		for (ModelOperation op: service.getOperations())
		{
			boolean found = false;
			String spec = op.getPrettySpec(translated);

			out.println ( "\t/**" + endl
					+ "\t * Operation " + op.getName(translated) + endl
					+ Util.formatComments(op.getComments(), "\t") );

			for (ModelParameter param: op.getParameters()) {
				out.print ( "\t * @param "
					+ param.getName(translated)
					+ " " );
				String cmt = Util.formatComments( param.getComments(), "\t           ");
				if (cmt.length () > 15)
					out.println ( cmt.substring(15) );
				else
					out.println ();
			}

			ModelParameter returnParam = op.getReturnParameter();
			if (returnParam != null && ! returnParam.getDataType().isVoid ())
			{
				out.print ( "\t * @return " );
				String cmt = Util.formatComments( returnParam.getComments(), "\t           ");
				if (cmt.length () > 15)
					out.print ( cmt.substring(15));
				else
					out.println ();
			}
			out.println ( "\t */" );
			out.println ( "\t" + op.getPrettySpec (translated) + endl
					+ "\t\t\t" + op.getThrowsClause(this.translated) + ";" + endl );
		}
		out.println ( "}" );
		out.close();
	}

	void generateTransactionAnnotation (ModelOperation op, PrintStream out)
	{
		Transactional trans = op.getTransactional();

		if (trans == null)
		{
			out.println ( "\t@Transactional(rollbackFor={java.lang.Exception.class})" );
		}
		else 
		{
			out.println ( "\t// Trasaction attribute " + trans.toString()  );
			Propagation propagation = trans.propagation();

			out.print ("\t@Transactional(isolation=org.springframework.transaction.annotation.Isolation.");
			out.print(trans.isolation().toString());
			out.print(",\n\t\tpropagation=org.springframework.transaction.annotation.Propagation.");
			out.print(trans.propagation().toString());
			String separator = ", \n\t\t";
			if (trans.noRollbackFor() != null && trans.noRollbackFor().length > 0)
			{
				out.print(separator);
				separator = "noRollbackFor={";
				for (Class cl: trans.noRollbackFor())
				{
					out.print(separator);
					ModelClass mc = (ModelClass) parser.getElement(cl);
					out.print(mc.getFullName(true));
					out.print(".class");
					separator = ",";
				}
				out.print("}");
				separator=", ";
			}
			if (trans.rollbackFor() != null && trans.rollbackFor().length > 0)
			{
				out.print(separator);
				separator = "rollbackFor={";
				for (Class cl: trans.rollbackFor())
				{
					out.print(separator);
					ModelClass mc = (ModelClass) parser.getElement(cl);
					out.print(mc.getFullName(true));
					out.print(".class");
					separator = ",";
				}
				out.print("}");
				separator=", ";
			}
			if (trans.rollbackForClassName() != null && trans.rollbackForClassName().length > 0)
			{
				out.print(separator);
				separator = "rollbackForClassName={";
				for (String cl: trans.rollbackForClassName())
				{
					out.print(separator);
					out.print('"');
					out.print(Util.formatString(cl));
					out.print('"');
					separator = ",";
				}
				out.print("}");
				separator=", ";
			}
			if (trans.noRollbackForClassName() != null && trans.noRollbackForClassName().length > 0)
			{
				out.print(separator);
				separator = "noRollbackForClassName={";
				for (String cl: trans.noRollbackForClassName())
				{
					out.print(separator);
					out.print('"');
					out.print(Util.formatString(cl));
					out.print('"');
					separator = ",";
				}
				out.print("}");
				separator=", ";
			}
			if (trans.readOnly())
			{
				out.print(separator);
				separator = "readOnly=true";
				separator=", ";
			}
			out.println (")");
		}

	}


	void generateNullChecks (PrintStream out, ModelOperation op, boolean translated)
	{
		for (ModelParameter param: op.getParameters())
		{
			if (param.isRequired()) {
				if (param.getDataType().isCollection()) {
					out.println ( "\t\tif ("+param.getName(translated)
							+ " == null ) {" + endl
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(translated) + " - "
							+ param.getName(translated)
							+ " cannot be empty\");" + endl
							+ "\t\t}" );
				} else if (param.getDataType().isArray()) {
						out.println ( "\t\tif ("+param.getName(translated)
								+ " == null ) {" + endl
								+ "\t\t\tthrow new IllegalArgumentException(\""
								+ op.getFullSpec(translated) + " - "
								+ param.getName(translated)
								+ " cannot be empty\");" + endl
								+ "\t\t}" );
				} else if (param.getDataType().isString()) {
					out.println ( "\t\tif ("+param.getName(translated)
							+ " == null || "
							+ param.getName(translated)
							+ ".trim().length() == 0) {" + endl
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(translated) + " - "
							+ param.getName(translated)
							+ " cannot be null\");" + endl
							+ "\t\t}" );
				}
				else if (! param.getDataType().isPrimitive())
				{
					out.println ( "\t\tif ("+param.getName(translated)
							+ " == null) {" + endl
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(translated) + " - "
							+ param.getName(translated)
							+ " cannot be null\");" + endl
							+ "\t\t}" );
					ModelClass modelClass = param.getDataType();
					if ( modelClass != null && modelClass.isValueObject()) {
						for (ModelAttribute at: modelClass.getAttributes())
						{
							if (at.isRequired())
							{
								if (at.getDataType().isCollection()) {
									out.println ( "\t\tif ("+param.getName(translated)+"."+at.getterName(translated)
											+ "() == null || "
											+ param.getName(translated)+"."+at.getterName(translated)
											+ "().isEmpty()) {" + endl
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(translated) + " - "
											+ param.getName(translated)+"." +at.getName(translated)
											+ " cannot be empty\");" + endl
											+ "\t\t}" );
								} else if (at.getDataType().isString()) {
									out.println ( "\t\tif ("+param.getName(translated)+"."+at.getterName(translated)
											+ "() == null || "
											+ param.getName(translated)+"."+at.getterName(translated)
											+ "().trim().length() == 0) {" + endl
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(translated) + " - "
											+ param.getName(translated)+"." +at.getName(translated)
											+ " cannot be null\");" + endl
											+ "\t\t}" );
								}
								else if (! at.getDataType().isPrimitive())
								{
									out.println ( "\t\tif ("+param.getName(translated)+"."+ at.getterName(translated)
											+ "() == null ) {" + endl
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(translated) + " - "
											+ param.getName(translated)+"." +at.getName(translated)
											+ " cannot be null\");" + endl
											+ "\t\t}" );
								}
							}

						}
					}
				}
			}
		}
	}

	void generateOperationBase (ModelClass service, ModelOperation op, boolean translated, PrintStream out)
	{
		out.println ( "\t/**" + endl
				+ "\t * @see " + service.getFullName(translated) + "#"
				+ op.getSpec(translated) + endl
				+ "\t */" );

		generateTransactionAnnotation(op, out);

		out.println ( "\tpublic " + op.getPrettySpec (translated) );
		String throwsClause = op.getThrowsClause(this.translated);
		if (throwsClause.isEmpty())
			out.println ( "\t\tthrows "+rootPkg+".exception.InternalErrorException" );
		else
			out.println ( "\t\t" + throwsClause );
		out.println ( "\t{" );
		generateNullChecks (out, op, translated);
		out.println ( "\t\ttry" + endl
				+ "\t\t{" );
		if (op.getReturnParameter().getDataType().isVoid())
			out.println ( "\t\t\t" + op.getImplCall(translated) + ";" );
		else
			out.println ( "\t\t\treturn " + op.getImplCall(translated) + ";" );
		out.println ( "\t\t}" + endl
				+ "\t\tcatch ("+rootPkg+".exception.InternalErrorException __internalException)" + endl
				+ "\t\t{" + endl
				+ "\t\t\tthrow __internalException;" );
		for (ModelClass exception: op.getExceptions())
		{
			if (! exception.getFullName(this.translated) .equals (rootPkg+".exception.InternalErrorException"))
				out.println ( "\t\t}" + endl
						+ "\t\tcatch (" + exception.getFullName(this.translated) + " ex)" + endl
						+ "\t\t{" + endl
						+ "\t\t\tthrow ex;" );
		}
		out.println ( "\t\t}" + endl
				+ "\t\tcatch (Throwable th)" + endl
				+ "\t\t{" + endl
				+ "\t\t\torg.apache.commons.logging.LogFactory.getLog(" + service.getFullName(translated) + ".class)." + endl
				+ "\t\t\t\twarn (\"Error on " + service.getName(translated) + "." + op.getName(translated) + "\", th);" + endl
				+ "\t\t\tthrow new "+rootPkg+".exception.InternalErrorException(" + endl
				+ "\t\t\t\t\"Error on " + service.getName(translated) + "." + op.getName(translated) + ": \"+th.toString(), th);" + endl
				+ "\t\t}" + endl
				+ "\t}" + endl );
		out.println ( "\tprotected abstract " + op.getImplSpec(translated) + " throws Exception;" + endl );

	}

	void generateBase(ModelClass service, boolean translated) throws FileNotFoundException {
		if (translated)
			return;

		String className = service.getBaseName(translated);
		String file;
		if (service.isServerOnly())
		{
			file = generator.getSyncDir();
		}
		else {
			file = generator.getCoreDir();
		}
		
		String packageName = service.getPackage(translated);

		file = file + File.separator + Util.packageToDir(packageName) + File.separator + className + ".java";

		File f = new File (file);
		f.getParentFile().mkdirs();
		System.out.println ( "Generating " + f.getPath() );

		PrintStream out = new PrintStream (f);
		
		out.println ( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (!packageName.isEmpty())
			out.println ( "package " + packageName + ";" );

		out.println ( "import org.springframework.transaction.annotation.Transactional;" );

		out.println ( "/**" + endl
			+ " * <p> " + endl
			+ " * Spring Service base class for <code>" + service.getFullName(translated) + "</code>," + endl
			+ " * provides access to all services and entities referenced by this service. " + endl
			+ " * </p>" + endl
			+ " * " + endl
			+ " * see " + service.getFullName(translated) + endl
			+ " */" );
		out.println ( "public abstract class " + className );
		if (service.getSuperClass() != null)
			out.println ( " extends " + service.getSuperClass().getBaseFullName(translated) );
		out.println ( "\timplements " + service.getFullName(translated) ) ;
		out.println ( " {" );

		//
		// Generate clients
		//

		for (ModelClass provider: service.getDepends()) {
			if (provider != null) {
				String fullName;
				String name;
				String varName;
				if (provider . isEntity())
				{
					name = provider.getDaoName(translated);
					fullName = provider.getDaoFullName(translated);
					varName = provider.getVarName() + "Dao";
				} else {
					name = provider.getName(translated);
					fullName  = provider.getFullName(translated);
					varName = provider.getVarName();
				}
				out.println ( "\t"
						+ "private " + fullName
						+ " " + varName + ";"
						+ endl
						);
				out.println ( "\t/**" + endl
						+ "\t * Sets reference to <code>"
						+ varName
						+ "</code>." + endl
						+ "\t */" + endl
						+ "\tpublic void set"
						+ name
						+ " (" + fullName
						+ " " + varName
						+ ") {" );
				out.println ( "\t\tthis." + varName
						+ " = " + varName
						+ ";" );
				out.println ( "\t}" + endl );
				out.println ( "\t/**" + endl
						+ "\t * Gets reference to <code>"
						+ varName
						+ "</code>." + endl
						+ "\t */" + endl
						+ "\tpublic "+fullName+" get"
						+ name
						+ " () {" );
				out.println ( "\t\treturn " + varName
						+ ";" );
				out.println ( "\t}" + endl );
			}

		}
		out. println ( );

		///////////////////////////////////////
		// Generate OPERATIONS

		for (ModelOperation op: service.getOperations())
		{
			generateOperationBase(service, op, translated, out);
		}



		out .println( "\t/**" + endl
				+ "\t * Gets the current <code>principal</code> if one has been set," + endl
				+ "\t * otherwise returns <code>null</code>." + endl
				+ "\t *" + endl
				+ "\t * @return the current principal" + endl
				+ "\t */" + endl
				+ "\tprotected java.security.Principal getPrincipal()" + endl
				+ "\t{" + endl
				+ "\t\treturn "+rootPkg+".PrincipalStore.get();" + endl
				+ "\t}" + endl
				+ "}" );
		out.close();
	}

	void generateEjbInterface (ModelClass service, boolean translated) throws FileNotFoundException {

		if (service.isInternal() || service.isServerOnly())
			return;

		String packageName = service.getPackage(translated);

		String file;
		file = generator.getCommonsDir() + File.separator+ Util.packageToDir(packageName) + 
				"ejb/" + service.getName(translated) + ".java";

		File f = new File (file);
		f.getParentFile().mkdirs();
		System.out.println ( "Generating " + f.getPath() );

		PrintStream out = new PrintStream (f);

		out.println ( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (!packageName.isEmpty())
			out.println ( "package " + packageName + ".ejb;" );
		else
			out.println ( "package ejb;" );

		out.println ( "/**" + endl
				+ " * EJB " + service.getName(translated) + endl
				+ Util.formatComments(service.getComments())
				+ " */" );
		out.println ( "public interface " + service.getName(translated) + endl
				+ "\textends javax.ejb.EJBLocalObject" );
		out.println ( " {" );
		out.println ( );
		for (ModelOperation op: service.getOperations())
		{
			if (!op.getActors().isEmpty() || ! service.getActors().isEmpty())
				out.println ( "\t" + op.getPrettySpec (translated) + endl + "\t" + op.getThrowsClause(this.translated) + ";" + endl );
		}
		out.println ( "}" );
		out.close();
	}


	void generateEjbHome (ModelClass service, boolean translated) throws FileNotFoundException {

		if (service.isInternal() || service.isServerOnly())
			return;

		String packageName = service.getPackage(translated);

		String file;
		file = generator.getCommonsDir() + "/" + Util.packageToDir(packageName) + "ejb/" + service.getName(translated) + "Home.java";

		File f = new File (file);
		f.getParentFile().mkdirs();
		System.out.println ( "Generating " + f.getPath() );

		PrintStream out = new PrintStream (f);

		out.println ( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (!packageName.isEmpty())
			out.println ( "package " + packageName + ".ejb;" );
		else
			out.println ( "package ejb;" );

		out.println ( "/**" + endl
				+ " * EJB Home " + service.getName(translated) + endl
				+ Util.formatComments(service.getComments())
				+ " */" );
		out.println ( "public interface " + service.getName(translated) + "Home" + endl
				+ "\textends javax.ejb.EJBLocalHome" );
		out.println ( " {" + endl
				+ "\t/**" + endl
				+ "\t * The logical JDNI name" +endl
				+ "\t */" + endl
				+ "\tpublic static final String COMP_NAME=\"java:comp/ejb/" + service.getName(translated) + "\";"
				+ endl + endl
				+ "\t/**" + endl
				+ "\t * The physical JDNI name" +endl
				+ "\t */" + endl
				+ "\tpublic static final String JNDI_NAME=\"soffid/ejb/" + service.getFullName(translated) + "\";"
				+ endl + endl
				+ "\tpublic "+ packageName  + ".ejb." + service.getName(translated) + " create()" + endl
				+ "\t\tthrows javax.ejb.CreateException;" + endl
				+ endl
				+ "}" );
		out.close();
	}


	void generateEjbBean(ModelClass service, boolean translated) throws FileNotFoundException {

		String className= service.getBeanName(translated);

		String packageName = service.getPackage(translated);

		String file = generator.getCoreDir() + "/" + Util.packageToDir(packageName ) + "ejb/" + className + ".java";

		File f = new File (file);
		f.getParentFile().mkdirs();
		System.out.println ( "Generating " + f.getPath() );

		PrintStream out = new PrintStream (f);

		out.println ( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (!packageName.isEmpty())
			out.println ( "package " + packageName + ".ejb;" );

		out.println ( "/**" + endl
			+ " * @see <code>" + service.getFullName(translated) + "</code>," + endl
			+ " */" );
		out.print ( "public class " + className );
		out.println ( " extends org.springframework.ejb.support.AbstractStatelessSessionBean" );
		String svcName = service.getLocalServiceName(false);
		out.println ( "{" + endl
				+ "\tprivate " + service.getFullName(false) + " " + svcName + ";" + endl );

		for (ModelOperation op: service.getOperations())
		{
			if (!op.getActors().isEmpty() || ! service.getActors().isEmpty())
			{
				out.println ( "\t/**" + endl
						+ "\t * @see " + service.getFullName(translated) + "#"
						+ op.getSpec(translated) + endl
						+ "\t */" );
				out.println ( "\tpublic " + op.getPrettySpec (translated) );
				String throwsClause = op.getThrowsClause(this.translated);
				if (!throwsClause.isEmpty())
					out.println ( "\t\t" + throwsClause );
				out.println ( "\t{" );
				out.println ( "\t\t"+rootPkg+".PrincipalStore.set(super.getSessionContext().getCallerPrincipal());" );
				///////////////////////
				// Add null checks
				if (translated)
				{
					generateNullChecks(out, op, translated);
				}
				out.print ( "\t\ttry" + endl
						+ "\t\t{" + endl
						+ "\t\t\t" );
				if (!op.getReturnParameter().getDataType().isVoid())
					out.print ( "return " );

				ModelParameter result = op.getReturnParameter();
				String invocationSuffix = "";
				if (!this.translated && translated)
				{
					if (result.getDataType().isCollection() && result.getDataType().getChildClass() != null &&
							result.getDataType().getChildClass().isTranslated() && result.getDataType().getChildClass().isValueObject())
					{
						ModelClass childclass = result.getDataType().getChildClass();
						out.print ( childclass.getFullName(translated) + ".to" + childclass.getName(translated) + "List (" + endl + "\t\t\t\t" );
						invocationSuffix = ")";
					}
					else if (result.getDataType().isTranslated() && result.getDataType().isValueObject())
					{
						out.print ( result.getDataType().getJavaType(translated) + ".to" + result.getDataType().getName(translated) + "(" + endl + "\t\t\t\t" );
						invocationSuffix = ")";
					}
				}
				out.print ( "this." + svcName + "." + op.getName(false) + "(" );
				boolean first = true;
				for (ModelParameter param: op.getParameters()) {
						if (first)
							first = false;
						else
							out.print ( ", " );
						if (!this.translated && translated && 
							param.getDataType().isCollection() && param.getDataType().getChildClass() != null &&
							param.getDataType().getChildClass().isTranslated() && param.getDataType().getChildClass().isValueObject())
						{
							out.print ( param.getDataType().getChildClass().getFullName(!translated)+ ".to" + 
								param.getDataType().getChildClass().getName(!translated) + "List ("
								+  param.getName(translated) + ")" );
						}
						else if (!this.translated && translated && param.getDataType().isTranslated() && param.getDataType().isValueObject())
						{
							out.print (  param.getDataType().getJavaType(!translated) + ".to" + param.getDataType().getName(!translated)+ "("+ param.getName(translated) + ")" );
						}
						else
							out.print ( param.getName(translated) );

				}
				out.println ( ")" + invocationSuffix + "; " + endl
						+ "\t\t}" + endl
						+ "\t\tcatch (Exception exception)" + endl
						+ "\t\t{" + endl
						+ "\t\t\tfinal Throwable cause = getRootCause(exception);" );
				for (ModelClass ex: op.getExceptions()) {
					out.println ( "\t\t\tif (cause instanceof " + ex.getFullName(this.translated) + ")" + endl
							+ "\t\t\t\tthrow (" + ex.getFullName(this.translated) + ") cause;" );
				}
				out.println ( "\t\t\tif (cause instanceof Exception)" + endl
					+ "\t\t\t\tthrow new javax.ejb.EJBException ((Exception)cause);" + endl
					+ "\t\t\tthrow new javax.ejb.EJBException (exception);" + endl
					+ "\t\t}" + endl
					+ "\t}" );
			}
		}

		out.println ( "\t/**" + endl
				+ "\t * Every Spring Session EJB needs to" + endl
				+ "\t * call this to instantiate the Spring" + endl
				+ "\t * Business Object." + endl
				+ "\t *" + endl
				+ "\t * @see org.springframework.ejb.support.AbstractStatelessSessionBean#onEjbCreate()" + endl
				+ "\t */" + endl
				+ "\tprotected void onEjbCreate()" + endl
				+ "\t{" + endl
				+ "\t\tthis." + svcName  + " = (" + service.getFullName(false)+ ")" + endl
				+ "\t\tgetBeanFactory().getBean(\"" + service.getSpringBeanName(generator, false) + "\");" + endl
				+ "\t}" + endl
				+ "\t" + endl
				+ "\t/**" + endl
				+ "\t * Override default BeanFactoryLocator implementation to" + endl
				+ "\t * provide singleton loading of the application context Bean factory." + endl
				+ "\t *" + endl
				+ "\t * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)" + endl
				+ "\t */" + endl
				+ "\tpublic void setSessionContext(javax.ejb.SessionContext sessionContext)" + endl
				+ "\t{" + endl
				+ "\t\tsuper.setSessionContext(sessionContext);" + endl
				+ "\t\tsuper.setBeanFactoryLocator(" + endl
				+ "\t\torg.springframework.context.access.ContextSingletonBeanFactoryLocator.getInstance(\"beanRefFactory.xml\"));" + endl
				+ "\t\tsuper.setBeanFactoryLocatorKey(\"beanRefFactory\");" + endl
				+ "\t}" + endl
				+ endl
				+ "\torg.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog (getClass());" + endl
				+ "\t/**" + endl
				+ "\t * Finds the root cause of the parent exception" + endl
				+ "\t * by traveling up the exception tree." + endl
				+ "\t */" + endl
				+ "\tprivate static Throwable getRootCause(Throwable throwable)" + endl
				+ "\t{" + endl
				+ "\t\tif (throwable != null)" + endl
				+ "\t\t{" + endl
				+ "\t\t\t// Reflectively get any exception causes." + endl
				+ "\t\t\ttry" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\tThrowable targetException = null;" + endl
				+ "\t\t\t\t// java.lang.reflect.InvocationTargetException" + endl
				+ "\t\t\t\tString exceptionProperty = \"targetException\";" + endl
				+ "\t\t\t\tif (org.apache.commons.beanutils.PropertyUtils.isReadable(throwable, exceptionProperty))" + endl
				+ "\t\t\t\t{" + endl
				+ "\t\t\t\t\ttargetException = (Throwable)org.apache.commons.beanutils.PropertyUtils.getProperty(throwable, exceptionProperty);" + endl
				+ "\t\t\t\t}" + endl
				+ "\t\t\t\telse" + endl
				+ "\t\t\t\t{" + endl
				+ "\t\t\t\t\texceptionProperty = \"causedByException\";" + endl
				+ "\t\t\t\t\t//javax.ejb.EJBException" + endl
				+ "\t\t\t\t\tif (org.apache.commons.beanutils.PropertyUtils.isReadable(throwable, exceptionProperty))" + endl
				+ "\t\t\t\t\t{" + endl
				+ "\t\t\t\t\t\ttargetException = (Throwable)org.apache.commons.beanutils.PropertyUtils.getProperty(throwable, exceptionProperty);" + endl
				+ "\t\t\t\t\t}" + endl
				+ "\t\t\t\t}" + endl
				+ "\t\t\t\tif (targetException != null)" + endl
				+ "\t\t\t\t{" + endl
				+ "\t\t\t\t\tthrowable = targetException;" + endl
				+ "\t\t\t\t}" + endl
				+ "\t\t\t}" + endl
				+ "\t\t\tcatch (Exception exception)" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\t// just print the exception and continue" + endl
				+ "\t\t\t\texception.printStackTrace();" + endl
				+ "\t\t\t}" + endl
				+ "\t\t\tif (throwable.getCause() != null)" + endl
				+ "\t\t\t{" + endl
				+ "\t\t\t\tthrowable = throwable.getCause();" + endl
				+ "\t\t\t\tthrowable = getRootCause(throwable);" + endl
				+ "\t\t\t}" + endl
				+ "\t\t}" + endl
				+ "\t\treturn throwable;" + endl
				+ "\t}" );
		out.println ( "}" );
		out.close();
	}

	void generateUml (ModelClass service, boolean translated) throws IOException {
		String file;
		file = generator.getUmlDir();
		String packageName = service.getPackage(translated);

		file = file + File.separator + Util.packageToDir(packageName);

		file += service.getName(translated);
		file += ".svg";
		File f = new File (file);
		f.getParentFile().mkdirs();

		StringBuffer source = new StringBuffer();
		source.append ("@startuml" + endl +
				"skinparam backgroundColor white"+endl+
				"skinparam class {"+endl+
				"BackgroundColor<<Entity>> Wheat"+endl+
				"BackgroundColor<<ValueObject>> Pink"+endl+
				"BackgroundColor<<Service>> LightBlue"+endl+
				"}" +endl );
		source.append (service.generatePlantUml(translated, false, true));

		boolean generate = Util.isModifiedClass(service, f);
		for (ModelClass provider: service.getDepends()) {
			generate = generate || Util.isModifiedClass(provider, f);
			if (provider.isEntity())
			{
				source.append( provider.generatePlantUml(translated, true, false) );
				for (ModelClass vo: provider.getDepends())
				{
					if (vo.isValueObject())
					{
						generate = generate || Util.isModifiedClass(vo, f);
						source.append (vo.generatePlantUml(translated, true, false));
						source.append (provider.getName(translated) + " ..> "+vo.getName(translated)+endl);
					}
				}
			}
			else 
			{
				source.append( provider.generatePlantUml(translated, false, false) );
			}
			source.append (service.getName(translated) + " ..> "+provider.getName(translated)+endl);
		}
		source.append ("@enduml");

		if (generate)
		{
			System.out.println ( "Generating " + f.getPath() );
	
			SourceStringReader reader = new SourceStringReader(source.toString());
			reader.generateImage(new FileOutputStream(f), new FileFormatOption(FileFormat.SVG));
		}
	}

	void generateUmlUseCase (ModelClass service, boolean translated) throws IOException {
		String file;
		file = generator.getUmlDir();
		String packageName = service.getPackage(translated);

		file = file + File.separator + Util.packageToDir(packageName);

		file += service.getName(translated);
		file += "-uc.svg";
		File f = new File (file);
		
		if ( Util.isModifiedClass(service, f))
		{
			f.getParentFile().mkdirs();
	
			System.out.println ( "Generating " + f.getPath() );
	
	
			StringBuffer source = new StringBuffer();
			source.append ("@startuml" + endl +
					"left to right direction"+endl+
					"skinparam backgroundColor white"+endl+
					"skinparam packageStyle rect"+endl+
					"("+service.getName(translated)+")"+ endl );
			
			boolean left = true;
			for (ModelClass actor: service.getAllActors())
			{
				actor.left = left;
				source.append ("actor "+actor.getName(translated) +endl);
				left = !left;
			}
			
			source.append ("rectangle "+service.getName()+ " {"+ endl);
			for (ModelOperation op: service.getOperations()) {
				Set<ModelClass> actors = op.getActors();
				source.append ("usecase ").append(op.getName(translated)).append("\n");
				for (ModelClass actor: actors)
				{
					if (actor.left)
						source.append (actor.getName(translated) + " -- ("+op.getName(translated)+")"+endl);
					else
						source.append ("("+op.getName(translated)+ ") -- "+actor.getName(translated) +endl);
				}
			}
			source.append ("}"+endl);
			source.append ("@enduml");
			
			SourceStringReader reader = new SourceStringReader(source.toString());
			reader.generateImage(new FileOutputStream(f), new FileFormatOption(FileFormat.SVG));
		}
	}
}
