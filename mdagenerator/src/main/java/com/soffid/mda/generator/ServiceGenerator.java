package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soffid.mda.parser.AbstractModelAttribute;
import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.ModelOperation;
import com.soffid.mda.parser.ModelParameter;
import com.soffid.mda.parser.Parser;

public class ServiceGenerator {

	private Generator generator;
	private Parser parser;
	private String pkg;
	private String rootPkg;

	final static String endl = "\n";
	
	public void generate(Generator generator, Parser parser) throws IOException {
		this.generator = generator;
		this.parser = parser;
		if (generator.getBasePackage() != null)
			rootPkg = generator.getBasePackage();
		else if (generator.isTranslated())
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
		for (AbstractModelClass service: parser.getServices()) {
			if (! parser.isTranslateOnly()) {
				generateInterface (service, Translate.SERVICE_SCOPE);
				generateBase(service, Translate.SERVICE_SCOPE);
			}
			if (service.isTranslated() || parser.isTranslateOnly())
			{
				generateInterface (service, Translate.ALTSERVICE_SCOPE);
				generateBase(service, Translate.ALTSERVICE_SCOPE);
				generateBaseProxy(service, Translate.ALTSERVICE_SCOPE);
			}
			if (generator.isGenerateUml())
			{
				generateUml(service, Translate.SERVICE_SCOPE);
				generateUmlUseCase(service, Translate.SERVICE_SCOPE);
			}
			if (generator.isGenerateEjb() && ! service.isInternal() && !service.isServerOnly())
			{
				if (!parser.isTranslateOnly()) {
					generateEjbHome(service, Translate.SERVICE_SCOPE);
					generateEjbInterface (service, Translate.SERVICE_SCOPE);
					generateEjbBean(service, Translate.SERVICE_SCOPE);
				}
				if (service.isTranslated() || parser.isTranslateOnly())
				{
					generateEjbHome(service, Translate.ALTSERVICE_SCOPE);
					generateEjbInterface (service, Translate.ALTSERVICE_SCOPE);
					generateEjbBean(service, Translate.ALTSERVICE_SCOPE);
				}
			}
		}

		if (generator.isGenerateEjb() && generator.isTargetJboss3())
		{
			generateEjbJarXml();
			generateJbossXml();
		}
		else
		{
			generateOpenEjbXml();
		}
		generateServiceLocator();
		generateAltServiceLocator();
		if (generator.isGenerateEjb())
		{
			generateEjbLocator(Translate.SERVICE_SCOPE);
			generateEjbLocator(Translate.ALTSERVICE_SCOPE);
		}
		if (generator.isGenerateSync())
		{
			if (!generator.isTransaltedOnly()) {
				generateRemoteServiceLocator(false);
				generateRemoteServiceLocatorProxy(false);
			}
			generateRemoteServiceLocatorProxy(true);
			generateRemoteServiceLocator(true);
			generateRemoteServicePublisher();
		}
	}
	
	
	void generateRemoteServicePublisher () throws FileNotFoundException, UnsupportedEncodingException
	{
		String file;
		file = generator.getSyncDir();

		file = file + "/" + Util.packageToDir(pkg) + "remote/PublisherInterface.java";
		File f = new File (file);
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating "+f.getPath());

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
		out = new SmartPrintStream(f, "UTF-8");

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

		for (AbstractModelClass service: parser.getServices()) {
			String path = service.getServerPath();
			String role = service.getServerRole();
			if (! service.isInternal() && ! path.isEmpty()) {
				out.println ( "\t\tbean = locator.getService(\"" + service.getSpringBeanName(generator, Translate.SERVICE_SCOPE) + "\");" + endl
					+ "\t\tif (bean != null)" + endl
					+ "\t\t\tpublisher.publish(bean, \"" + path + (generator.isTranslated() ? "-en": "") + "\", \"" + role + "\");" + endl );
				if (service.isTranslated())
				{
					out.println ( "\t\tbean = locator.getService(\"" + service.getSpringBeanName(generator, Translate.ALTSERVICE_SCOPE) + "\");" + endl
							+ "\t\tif (bean != null)" + endl
							+ "\t\t\tpublisher.publish(bean, \"" + path + (generator.isTranslated() ? "": "-en") + "\", \"" + role + "\");" + endl );
				}
			}
		}
		out.println ( "\t}" + endl
				+ "}" );
		out.close ();
	}

	void generateRemoteServiceLocatorProxy (boolean translated) throws FileNotFoundException, UnsupportedEncodingException
	{
		String file;
		file = generator.getCommonsDir();
		
		String packageName = (translated ? "com.soffid.iam": "es.caib.seycon.ng");
		String basePackage = packageName;
		
		if (!"com.soffid.iam".equals(generator.getBasePackage()))
		{
			packageName = generator.getBasePackage();
		}

		if (generator.isPlugin())
		{
			packageName = packageName + ".addons."+generator.getPluginName();
		}
		packageName =  packageName + ".remote";
		file = file + "/"+Util.packageToDir (packageName);
		File f = new File (file + "/RemoteServiceLocatorProxy.java");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating "+f.getPath());

		String commonPkg = translated ? "com.soffid.iam" : "es.caib.seycon.ng";
		commonPkg = "es.caib.seycon.ng";
		
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
				+ "/**" + endl
				+ " * Locates and provides all available application services." + endl
				+ " */" + endl
				+ "public interface RemoteServiceLocatorProxy" + endl
				+ "{" + endl
				+ "" + endl
				+ "\tObject getService(String name);" + endl
				+ "}" );
		out.close ();
	}


	void generateRemoteServiceLocator (boolean translated) throws FileNotFoundException, UnsupportedEncodingException
	{
		String file;
		file = generator.getCommonsDir();
		
		String packageName = (translated ? "com.soffid.iam": "es.caib.seycon.ng");
		String basePackage = packageName;
		
		if (!"com.soffid.iam".equals(generator.getBasePackage()))
		{
			packageName = generator.getBasePackage();
		}

		if (generator.isPlugin())
		{
			packageName = packageName + ".addons."+generator.getPluginName();
		}
		packageName =  packageName + ".remote";
		file = file + "/"+Util.packageToDir (packageName);
		File f = new File (file + "/RemoteServiceLocator.java");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating "+f.getPath());

		String commonPkg = translated ? "com.soffid.iam" : "es.caib.seycon.ng";
//		commonPkg = "es.caib.seycon.ng";
		
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
				+ "import "+generator.getDefaultException()+";" + endl
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
				+ "\tString tenant = null;" + endl
				+ "\tpublic static RemoteServiceLocatorProxy serviceLocatorProxy = null;" + endl
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
				+ "\tpublic String getTenant() {" + endl
				+ "\t\treturn tenant;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\tpublic void setTenant(String tenant) {" + endl
				+ "\t\tthis.tenant = tenant;" + endl
				+ "\t}" + endl
				+ "" + endl
				+ "\t" + endl
				+ "\tprivate static int roundRobin = 0;" + endl
				+ "\t" + endl
				+ "\tpublic Object getRemoteService (String serviceName) throws IOException, "+generator.getDefaultException()+" {" + endl
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
				+ "	            else" + endl);
		if (generator.isTargetTomee())
			out.print("	                return factory.getInvoker(m.getHttpURL(serviceName), tenant, authToken);" + endl);
		else
			out.print("	                return factory.getInvoker(m.getHttpURL(serviceName), authToken);" + endl);
		out.print("\t\t\t} catch (Exception e) {" + endl
//				+ "\t\t\t\tlog.warn(\"Unable to locate server at \" + m.getServerURL(), e);" + endl
				+ "\t\t\t\tlastException = e;" + endl
				+ "\t\t\t}" + endl
				+ "\t\t}" + endl
				+ "\t\tthrow new IOException (\"Unable to locate remote service \"+serviceName, lastException);" + endl
				+ "\t}" + endl
				+ " " + endl
				+ " " );

		for (AbstractModelClass service: parser.getServices()) {
			String path = service.getServerPath();
			if (! service.isInternal() && ! path.isEmpty()) {
				int scope = translated ? Translate.SERVICE_SCOPE : Translate.ALTSERVICE_SCOPE;
				out.println ( "	/**" + endl
				+ "	 * Gets the remote service " + service.getName(scope) + "." + endl
				+ "	 *" + endl
				+ "	 * @return Remote object" + endl
				+ "	 **/" + endl
				+ "\tpublic " + service.getFullName(scope) + " get" + service.getName(scope) + "( ) throws IOException, "+generator.getDefaultException()+" {" + endl
				+ "\t\tif (serviceLocatorProxy != null && server == null)\n"
				+ "\t\t\treturn ("+service.getFullName(scope)+") serviceLocatorProxy.getService(\""+ service.getSpringBeanName(generator, scope)+"\");\n"
				+ "\t\telse\n"
				+ "\t\t\treturn ( " + service.getFullName(scope) + " ) getRemoteService (\"" + path + (translated ? "-en": "")+ "\");" + endl
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
		if (generator.getBasePackage() != null)
		{
			packageName = generator.getBasePackage();
			file = file + "/"+Util.packageToDir(packageName);
			className = "ServiceLocator";
		}
		else if (generator.isPlugin())
		{
			file =  file + "/com/soffid/iam/addons/" + generator.getPluginName();
			packageName = "com.soffid.iam.addons." + generator.getPluginName();
			className = Util.firstUpper(generator.getPluginName())+"ServiceLocator";
		}
		else
		{
			if (generator.isTranslated())
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
		String commonPkg = generator.isTranslated() ? "com.soffid.iam" : "es.caib.seycon.ng";
		if (generator.getBasePackage() != null)
			commonPkg = generator.getBasePackage();

		File f = new File (file+"/"+className+".java");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating "+f.getPath());

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

		for (AbstractModelClass service: parser.getServices ()) {
			out.println ( "\t/**" + endl
				+ "\t * Gets an instance of {@link "+ service.getFullName(Translate.SERVICE_SCOPE)+"}." + endl
				+ "\t */" + endl
				+ "\tpublic final "+ service.getFullName(Translate.SERVICE_SCOPE)+" get"+ service.getName(Translate.SERVICE_SCOPE)+"()" + endl
				+ "\t{" + endl
				+ "\t\treturn ("+ service.getFullName(Translate.SERVICE_SCOPE)+")" + endl
				+ "\t\t\tgetContext().getBean(\""+ service.getSpringBeanName(generator, Translate.SERVICE_SCOPE)+"\");" + endl
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

		out.close();

	}

	void generateAltServiceLocator () throws FileNotFoundException, UnsupportedEncodingException {

		String file;
		file = generator.getCoreDir();

		String packageName;
		String className;
		if (generator.isPlugin())
			return;
		
		String altClassName;
		if (!"com.soffid.iam".equals(generator.getBasePackage()))
		{
			return;
		}
		
		if (generator.isTranslated())
		{
			file = file + "/es/caib/seycon/ng";
			packageName = "es.caib.seycon.ng";
			altClassName = "com.soffid.iam.ServiceLocator";
		}
		else
		{
			file = file + "/com/soffid/iam";
			packageName = "com.soffid.iam";
			altClassName = "es.caib.seycon.ng.ServiceLocator";
		}
		className = "ServiceLocator";
		String commonPkg = generator.isTranslated() ? "com.soffid.iam" : "es.caib.seycon.ng";

		File f = new File (file+"/"+className+".java");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

//		System.out.println ("Generating ALT SERVICE LOCATOR "+f.getPath());

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

		for (AbstractModelClass service: parser.getServices ()) {
			if ( service.isTranslated())
			{
				out.println ( "\t/**" + endl
					+ "\t * Gets an instance of {@link "+ service.getFullName(Translate.SERVICE_SCOPE)+"}." + endl
					+ "\t */" + endl
					+ "\tpublic final "+ service.getFullName(Translate.ALTSERVICE_SCOPE)+" get"+ service.getName(Translate.ALTSERVICE_SCOPE)+"()" + endl
					+ "\t{" + endl
					+ "\t\treturn ("+service.getFullName(Translate.ALTSERVICE_SCOPE)+") "+altClassName+".instance().getService(\""+ service.getSpringBeanName(generator, Translate.ALTSERVICE_SCOPE)+"\");" + endl
					+ "\t}" + endl
					+ "" );
			} else {
				out.println ( "\t/**" + endl
						+ "\t * Gets an instance of {@link "+ service.getFullName(Translate.ALTSERVICE_SCOPE)+"}." + endl
						+ "\t */" + endl
						+ "\tpublic final "+ service.getFullName(Translate.ALTSERVICE_SCOPE)+" get"+ service.getName(Translate.ALTSERVICE_SCOPE)+"()" + endl
						+ "\t{" + endl
						+ "\t\treturn ("+service.getFullName(Translate.ALTSERVICE_SCOPE)+") "+altClassName+".instance().getService(\""+ service.getSpringBeanName(generator, Translate.SERVICE_SCOPE)+"\");" + endl
						+ "\t}" + endl
						+ "" );
			}
		}
		if (! generator.isPlugin())
		{
			out.println( "\t/**" + endl
				+ "\t * Gets an instance of the given service." + endl
				+ "\t */" + endl
				+ "\tpublic final Object getService(String serviceName)" + endl
				+ "\t{" + endl
				+ "\t\treturn "+altClassName+".instance().getService(serviceName);" + endl
				+ "\t}" + endl
				+ "" );
		}
		
		out.println ( "\tpublic org.springframework.context.ApplicationContext getContext()" + endl
		+ "\t{" + endl
		+ "\t\treturn "+altClassName+".instance().getContext();" + endl
		+ "\t}" + endl
		+ "" );

		out.println ( "\t/**" + endl
				+ "\t * Shuts down the ServiceLocator and releases any used resources." + endl
				+ "\t */" + endl
				+ "\tpublic void shutdown()" + endl
				+ "\t{" + endl
				+ "\t\t"+altClassName+".instance().shutdown();" + endl
				+ "\t}" + endl
				+ "" );
		out.println ( "}" );

		out.close();

	}

	void generateEjbLocator (int scope) throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		file = generator.getCommonsDir();

		String packageName;
		if (generator.isPlugin())
		{
			file = file + "/com/soffid/iam/addons/";
			packageName = "com.soffid.iam.addons.";
			file = file + generator.getPluginName();
			packageName = packageName + generator.getPluginName();
			return;
		}
		else
		{
			if (!"com.soffid.iam".equals(generator.getBasePackage()))
			{
				packageName = generator.getBasePackage();
				file = file + "/" + packageName.replace('.', '/');
				
			}
			else if (Translate.mustTranslate(scope, generator))
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
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");

		
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

		for (AbstractModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				out.println( "\t/**" + endl
					+ "\t * Gets an instance of {@link "+ service.getEjbInterfaceFullName(scope) + "}." + endl
					+ "\t */" + endl
					+ "\tpublic static "+ service.getEjbInterfaceFullName(scope)+" get"+ service.getName(scope)+"()" + endl
					+ "\t\tthrows javax.naming.NamingException, javax.ejb.CreateException" + endl
					+ "\t{" + endl);
				if (generator.isTargetJboss3())
					out.println( "\t\t" + service.getEjbHomeFullName(scope) + " home = ("
						+ service.getEjbHomeFullName(scope) +  ") " + endl
						+ "\t\t\tnew javax.naming.InitialContext()." + endl
							+ "\t\t\t\tlookup("+ service.getEjbHomeFullName(scope) + ".JNDI_NAME);" + endl
						+ "\t\treturn home.create();" + endl
						+ "\t}" + endl
						+ "" );
				else
					out.println( "\t\treturn ("
							+ service.getEjbInterfaceFullName(scope) +  ") " + endl
							+ "\t\t\tnew javax.naming.InitialContext()." + endl
								+ "\t\t\t\tlookup("+ service.getEjbHomeFullName(scope) + ".JNDI_NAME);" + endl
							+ "\t}" + endl
							+ "" );
			}
		}
		out.println ( "}" );

		out.close();
	}

	void generateEjbJarXml () throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		File f = new File (generator.getCoreResourcesDir() + "/META-INF/ejb-jar.xml");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");


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

		for (AbstractModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{

				int scope = Translate.SERVICE_SCOPE;
				do
				{
					out.println ( "\t\t<session>" + endl
							+ "\t\t\t<description>" + endl
							+ "\t\t\t\t<![CDATA[" + endl
							+ "\t\t\t\t" + Util.formatXmlComments(service.getComments(), "\t\t\t\t\t") + endl
							+ "\t\t\t\t]]>" + endl
							+ "\t\t\t</description>" + endl
							+ "\t\t\t<ejb-name>" + service.getEjbName(scope) + "</ejb-name>" + endl
							+ "\t\t\t<local-home>" + service.getEjbHomeFullName(scope) + "</local-home>" + endl
							+ "\t\t\t<local>" + service.getEjbInterfaceFullName(scope) + "</local>" + endl
							+ "\t\t\t<ejb-class>" + service.getBeanFullName(scope) + "</ejb-class>" + endl
							+ "\t\t\t<session-type>" + (service.isStateful() ? "Stateful": "Stateless") + "</session-type>" + endl
							+ "\t\t   " + endl
							+ "\t\t\t<transaction-type>Container</transaction-type>" + endl
							+ "\t\t</session>" + endl
							+ "" );
					if (scope == Translate.ALTSERVICE_SCOPE || ! service.isTranslated())
						break;
					scope = Translate.ALTSERVICE_SCOPE;
				} while (true);
			}
		}
		out.println ( "\t</enterprise-beans>" + endl
			+ "" + endl
			+ "\t<assembly-descriptor>" + endl
			+ "" );

		for (AbstractModelClass actor: parser.getActors ())
		{
			out .println ( "\t\t<security-role>" + endl
					+ "\t\t\t<description><![CDATA["
					+ Util.formatXmlComments(actor.getComments(), "\t\t\t\t") + "]]></description>" + endl
					+ "\t\t\t<role-name>" + actor.getRoleName() + "</role-name>" + endl
					+ "\t\t</security-role>" + endl
					);
		}

		for (AbstractModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				Set<AbstractModelClass> allActors = service.getAllActors();
				if (!allActors.isEmpty())
				{
					int scope = Translate.SERVICE_SCOPE;
					do
					{
						out.println ( "\t\t<method-permission>" + endl
								+ "\t\t\t<description><![CDATA[Create method security constraint]]></description>" ) ;
						// Create methods
						if (generator.isManualSecurityCheck())
						{
							boolean unchecked = false;
							for (AbstractModelClass actor: allActors)
							{
								if (actor.getRoleName().equals( "anonymous")) unchecked  = true; 
							}
							if (unchecked)
								out.println ( "\t\t\t<unchecked/>" );
							else
								out.println ( "\t\t\t<role-name>*</role-name>" );
						} else {
							for (AbstractModelClass actor: allActors)
							{
								if (actor.getRoleName().equals( "anonymous"))
									out.println ( "\t\t\t<unchecked/>" );
								else
									out.println ( "\t\t\t<role-name>" + actor.getRoleName() + "</role-name>" );
							}
						}
						out.println ( "\t\t\t<method>" + endl
								+ "\t\t\t\t<description><![CDATA[Creates the " + service.getName(scope) + " Session EJB]]></description>" + endl
								+ "\t\t\t\t<ejb-name>" + service.getEjbName(scope) + "</ejb-name>" + endl
								+ "\t\t\t\t<method-intf>LocalHome</method-intf>" + endl
								+ "\t\t\t\t<method-name>create</method-name>" + endl
								+ "\t\t\t</method>" + endl
								+ "\t\t\t<method>" + endl
							+ "\t\t\t\t<description><![CDATA[Removes the " + service.getName(scope) + " Session EJB]]></description>" + endl
							+ "\t\t\t\t<ejb-name>" + service.getEjbName(scope) + "</ejb-name>" + endl
							+ "\t\t\t\t<method-intf>Local</method-intf>" + endl
							+ "\t\t\t\t<method-name>remove</method-name>" + endl
							+ "\t\t\t</method>" + endl
							+ "\t\t</method-permission>" + endl
							+ "" );
						if (scope == Translate.ALTSERVICE_SCOPE || ! service.isTranslated())
							break;
						scope = Translate.ALTSERVICE_SCOPE;
					} while (true);
				}

				for (ModelOperation op: service.getOperations())
				{
					Collection<AbstractModelClass> actors = op.getActors();
					if (!actors.isEmpty())
					{
						int scope = Translate.SERVICE_SCOPE;
						do
						{
							out.println ( "\t\t<method-permission>" + endl
									+ "\t\t\t<description><![CDATA[" + op.getSpec(scope) + " security constraint]]></description>" );
							if (generator.isManualSecurityCheck())
							{
								boolean unchecked = false;
								for (AbstractModelClass actor: allActors)
								{
									if (actor.getRoleName().equals( "anonymous")) unchecked  = true; 
								}
								if (unchecked)
									out.println ( "\t\t\t<unchecked/>" );
								else
									out.println ( "\t\t\t<role-name>*</role-name>" );
							} else {
								for (AbstractModelClass actor: allActors)
								{
									if (actor.getRoleName().equals( "anonymous"))
										out.println ( "\t\t\t<unchecked/>" );
									else
										out.println ( "\t\t\t<role-name>" + actor.getRoleName() + "</role-name>" );
								}
							}
							out.println ( "\t\t\t<method>" + endl
									+ "\t\t\t\t<description><![CDATA[" + Util.formatXmlComments(op.getComments(), "\t\t\t\t\t") + "]]></description>" + endl
							+ "\t\t\t\t<ejb-name>"+ service.getEjbName(scope) + "</ejb-name>" + endl
							+ "\t\t\t\t<method-intf>Local</method-intf>" + endl
							+ "\t\t\t\t<method-name>" + op.getName(scope) + "</method-name>" );

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
							if (scope == Translate.ALTSERVICE_SCOPE || ! service.isTranslated())
								break;
							scope = Translate.ALTSERVICE_SCOPE;
						} while (true);
					}
				}
			}
		}

		for (AbstractModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				for (ModelOperation op: service.getOperations())
				{
					int scope = Translate.SERVICE_SCOPE;
					do
					{
						out.println ( "\t\t<container-transaction>" + endl
								+ "\t\t\t<method>" + endl
								+ "\t\t\t\t<ejb-name>" + service.getEjbName(scope) + "</ejb-name>" + endl
								+ "\t\t\t\t<method-name>" + op.getName(scope)+ "</method-name>" + endl
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
						if (scope == Translate.ALTSERVICE_SCOPE || ! service.isTranslated())
							break;
						scope = Translate.ALTSERVICE_SCOPE;
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
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");
//		System.out.println ("Generating "+f.getPath());


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

		for (AbstractModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				int scope = Translate.SERVICE_SCOPE;
				do
				{
					out.println ( "\t\t<session>" + endl
						+ "\t\t\t<ejb-name>" + service.getEjbName(scope) + "</ejb-name>" + endl
						+ "\t\t\t<local-jndi-name>soffid/ejb/" + service.getFullName(scope) + "</local-jndi-name>" + endl
						+ "\t\t</session>" + endl
						+ "" );
					if (scope == Translate.ALTSERVICE_SCOPE || ! service.isTranslated())
						break;
					scope = Translate.ALTSERVICE_SCOPE;
				} while (true);
			}
		}
		out.println ( "   </enterprise-beans>" + endl
				+ "" + endl
				+ "</jboss>" );
		out.close();

	}

	void generateOpenEjbXml () throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		File f = new File (generator.getCoreResourcesDir() + "/META-INF/openejb-jar.xml");
		f.getParentFile().mkdirs();
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");
//		System.out.println ("Generating "+f.getPath());


		out.println ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl
				+ "<openejb-jar>" + endl
				+ "" );

		for (AbstractModelClass service: parser.getServices()) {
			if (!service.isInternal() && ! service.isServerOnly())
			{
				if (parser.isTranslateOnly() ) {
					int scope = Translate.ALTSERVICE_SCOPE;
					out.println ( "\t<ejb-deployment ejb-name=\"" + service.getEjbName(scope) + "\">" + endl
							+ "\t\t<jndi name=\"soffid.ejb." + service.getFullName(scope) + "\"/>" + endl
							+ "\t</ejb-deployment>" + endl
							+ "" );
					
				} else {
					int scope = Translate.SERVICE_SCOPE;
					do
					{
						out.println ( "\t<ejb-deployment ejb-name=\"" + service.getEjbName(scope) + "\">" + endl
								+ "\t\t<jndi name=\"soffid.ejb." + service.getFullName(scope) + "\"/>" + endl
								+ "\t</ejb-deployment>" + endl
								+ "" );
						if (scope == Translate.ALTSERVICE_SCOPE || ! service.isTranslated())
							break;
						scope = Translate.ALTSERVICE_SCOPE;
					} while (true);
					
				}
			}
		}
		out.println ( "" + endl
				+ "</openejb-jar>" );
		out.close();

	}


	void generateInterface (AbstractModelClass service, int scope) throws FileNotFoundException, UnsupportedEncodingException {
		String file;
		if (service.isInternal())
		{
			file = generator.getCoreDir();
		}
		else {
			file = generator.getCommonsDir();
		}
		String packageName = service.getPackage(scope);

		file = file + File.separator + Util.packageToDir(packageName);

		file += service.getName(scope);
		file += ".java";
		File f = new File (file);
		f.getParentFile().mkdirs();
		
		SmartPrintStream out = new SmartPrintStream(f, "UTF-8");


		out.println ( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (!packageName.isEmpty())
			out.println ( "package " + packageName + ";" );

		out.println ( "/**" + endl
				+ " * Service " + service.getName(scope) + endl
				+ Util.formatComments(service.getComments())
				+ " */" );
		out.print ( "public interface " + service.getName(scope) );
		boolean first = true;
		if (service.getSuperClass() != null) {
			out.print ( " extends " + service.getSuperClass().getFullName(scope) );
			first = false;
		}

		out.println ( " {" );
		String serverPath = service.getServerPath() ;
		if (!serverPath.isEmpty())
		{
			if (Translate.mustTranslate(service, scope))
				serverPath = serverPath + "-en";
			out.println ( "\tpublic final static String REMOTE_PATH = \""
					+ serverPath + "\";" + endl );
		}
		out.println ( "\tpublic final static String SERVICE_NAME = \""
				+ service.getSpringBeanName(generator, scope) + "\";" + endl );

		for (ModelOperation op: service.getOperations())
		{
			boolean found = false;
			String spec = op.getPrettySpec(scope);

			out.println ( "\t/**" + endl
					+ "\t * Operation " + op.getName(scope) + endl
					+ Util.formatComments(op.getComments(), "\t") );

			for (ModelParameter param: op.getParameters()) {
				out.print ( "\t * @param "
					+ param.getName(scope)
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
			out.println ( "\t" + op.getPrettySpec (scope) + endl
					+ "\t\t\t" + op.getThrowsClause(Translate.SERVICE_SCOPE) + ";" + endl );
		}
		out.println ( "}" );
		out.close();
	}

	void generateTransactionAnnotation (ModelOperation op, SmartPrintStream out)
	{
		Transactional trans = op.getTransactional();

		if (trans == null)
		{
			out.println ( "\t@Transactional(rollbackFor={java.lang.Exception.class})" );
		}
		else 
		{
			out.println ( "\t// Trasaction attribute " );
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
					AbstractModelClass mc = (AbstractModelClass) parser.getElement(cl);
					out.print(mc.getFullName(Translate.SERVICE_SCOPE));
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
					AbstractModelClass mc = (AbstractModelClass) parser.getElement(cl);
					out.print(mc.getFullName(Translate.SERVICE_SCOPE));
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
				out.print("readOnly=true");
				separator=", ";
			}
			out.println (")");
		}

	}


	void generateNullChecks (SmartPrintStream out, ModelOperation op, int scope)
	{
		for (ModelParameter param: op.getParameters())
		{
			if (param.isRequired()) {
				if (param.getDataType().isCollection()) {
					out.println ( "\t\tif ("+param.getName(scope)
							+ " == null ) {" + endl
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(scope) + " - "
							+ param.getName(scope)
							+ " cannot be empty\");" + endl
							+ "\t\t}" );
				} else if (param.getDataType().isArray()) {
						out.println ( "\t\tif ("+param.getName(scope)
								+ " == null ) {" + endl
								+ "\t\t\tthrow new IllegalArgumentException(\""
								+ op.getFullSpec(scope) + " - "
								+ param.getName(scope)
								+ " cannot be empty\");" + endl
								+ "\t\t}" );
				} else if (param.getDataType().isString()) {
					out.println ( "\t\tif ("+param.getName(scope)
							+ " == null || "
							+ param.getName(scope)
							+ ".trim().length() == 0) {" + endl
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(scope) + " - "
							+ param.getName(scope)
							+ " cannot be null\");" + endl
							+ "\t\t}" );
				}
				else if (! param.getDataType().isPrimitive())
				{
					out.println ( "\t\tif ("+param.getName(scope)
							+ " == null) {" + endl
							+ "\t\t\tthrow new IllegalArgumentException(\""
							+ op.getFullSpec(scope) + " - "
							+ param.getName(scope)
							+ " cannot be null\");" + endl
							+ "\t\t}" );
					AbstractModelClass modelClass = param.getDataType();
					if ( modelClass != null && modelClass.isValueObject()) {
						for (AbstractModelAttribute at: modelClass.getAttributes())
						{
							if (at.isRequired())
							{
								if (at.getDataType().isCollection()) {
									out.println ( "\t\tif ("+param.getName(scope)+"."+at.getterName(scope)
											+ "() == null || "
											+ param.getName(scope)+"."+at.getterName(scope)
											+ "().isEmpty()) {" + endl
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(scope) + " - "
											+ param.getName(scope)+"." +at.getName(scope)
											+ " cannot be empty\");" + endl
											+ "\t\t}" );
								} else if (at.getDataType().isString()) {
									out.println ( "\t\tif ("+param.getName(scope)+"."+at.getterName(scope)
											+ "() == null || "
											+ param.getName(scope)+"."+at.getterName(scope)
											+ "().trim().length() == 0) {" + endl
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(scope) + " - "
											+ param.getName(scope)+"." +at.getName(scope)
											+ " cannot be null\");" + endl
											+ "\t\t}" );
								}
								else if (! at.getDataType().isPrimitive())
								{
									out.println ( "\t\tif ("+param.getName(scope)+"."+ at.getterName(scope)
											+ "() == null ) {" + endl
											+ "\t\t\tthrow new IllegalArgumentException(\""
											+ op.getFullSpec(scope) + " - "
											+ param.getName(scope)+"." +at.getName(scope)
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

	void generateOperationBase (AbstractModelClass service, ModelOperation op, int scope, SmartPrintStream out)
	{
		out.println ( "\t/**" + endl
				+ "\t * @see " + service.getFullName(scope) + "#"
				+ "\t * @see " + service.getFullName(Translate.DONT_TRANSLATE) + "#"
				+ op.getSpec(scope) + endl
				+ "\t */" );

		generateTransactionAnnotation(op, out);

		out.println ( "\tpublic " + op.getPrettySpec (scope) );
		String throwsClause = op.getThrowsClause(Translate.SERVICE_SCOPE);
		if (throwsClause.isEmpty())
			out.println ( "\t\tthrows "+generator.getDefaultException()+"" );
		else
			out.println ( "\t\t" + throwsClause );
		out.println ( "\t{" );

		generateNullChecks (out, op, scope);
		
		if (generator.isTargetTomee())
		{
			out.println ( "\t\tObject[] __r = (Object[]) java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Object>() {"+endl
					+"\t\t\tpublic Object run() {"+endl
					+"\t\t\t\ttry {");
			if (op.getReturnParameter().getDataType().isVoid())
				out.println ( "\t\t\t\t\t" + op.getImplCall(scope) + ";" +endl+
						"\t\t\t\t\treturn null;");
			else
				out.println ( "\t\t\t\t\treturn new Object[] {" + op.getImplCall(scope) + "};" );
			out.print ("\t\t\t\t} catch (Throwable th) {"+endl
					+"\t\t\t\t\treturn new Object[] {null,th};"+endl
					+"\t\t\t\t}"+endl
					+"\t\t\t}"+endl
					+"\t\t});"+endl);
			if (op.getReturnParameter().getDataType().isVoid())
				out.print("\t\tif (__r == null) return;"+endl);
			else
			{
				String x = op.getReturnType(scope);
				int i = x.indexOf("<");
				if (i >= 0) x = x.substring(0, i);
				if (x.equals("boolean"))
					out.println("\t\tif (__r.length == 1 ) \n\t\t\treturn ((Boolean) __r[0]).booleanValue();");
				else if (x.equals("int"))
					out.println("\t\tif (__r.length == 1 ) \n\t\t\treturn ((Integer) __r[0]).intValue();");
				else if (x.equals("long"))
					out.println("\t\tif (__r.length == 1 ) \n\t\t\treturn ((Long) __r[0]).longValue();");
				else
				{
					out.println("\t\tif (__r.length == 1 ) \n\t\t\treturn ("+op.getReturnType(scope)+") __r[0];");
				}
			}
			for (AbstractModelClass exception: op.getExceptions())
			{
				if (! exception.getFullName(Translate.SERVICE_SCOPE) .equals (""+generator.getDefaultException()+""))
					out.println("\t\tif (__r[1] instanceof "+exception.getFullName(Translate.SERVICE_SCOPE) +") "+endl
							+"\t\t\tthrow ("+exception.getFullName(Translate.SERVICE_SCOPE)+") __r[1];");
			}
			out.println ( 
					"\t\tif (__r[1] instanceof "+ generator.getDefaultException()  +") "+endl
					+ "\t\t\tthrow ("+generator.getDefaultException()+") __r[1];" + endl
					+ "\t\torg.apache.commons.logging.LogFactory.getLog(" + service.getFullName(scope) + ".class)." + endl
					+ "\t\t\twarn (\"Error on " + service.getName(scope) + "." + op.getName(scope) + "\", (Throwable) __r[1]);" + endl
					+ "\t\tthrow new "+generator.getDefaultException()+"(" + endl
					+ "\t\t\t\"Unexpected error on " + service.getName(scope) + "." + op.getName(scope) + "\", (Throwable) __r[1]);" + endl
					+ "\t}" + endl );
			
		}
		else
		{
			out.println ( "\t\ttry" + endl
					+ "\t\t{" );
			if (op.getReturnParameter().getDataType().isVoid())
				out.println ( "\t\t\t" + op.getImplCall(scope) + ";" );
			else
				out.println ( "\t\t\treturn " + op.getImplCall(scope) + ";" );
			out.println ( "\t\t}" + endl
					+ "\t\tcatch ("+generator.getDefaultException()+" __internalException)" + endl
					+ "\t\t{" + endl
					+ "\t\t\tthrow __internalException;" );
			for (AbstractModelClass exception: op.getExceptions())
			{
				if (! exception.getFullName(Translate.SERVICE_SCOPE) .equals (""+generator.getDefaultException()+""))
					out.println ( "\t\t}" + endl
							+ "\t\tcatch (" + exception.getFullName(Translate.SERVICE_SCOPE) + " ex)" + endl
							+ "\t\t{" + endl
							+ "\t\t\tthrow ex;" );
			}
			out.println ( "\t\t}" + endl
					+ "\t\tcatch (Throwable th)" + endl
					+ "\t\t{" + endl
					+ "\t\t\torg.apache.commons.logging.LogFactory.getLog(" + service.getFullName(scope) + ".class)." + endl
					+ "\t\t\t\twarn (\"Error on " + service.getName(scope) + "." + op.getName(scope) + "\", th);" + endl
					+ "\t\t\tthrow new "+generator.getDefaultException()+"(" + endl
					+ "\t\t\t\t\"Error on " + service.getName(scope) + "." + op.getName(scope) + ": \"+th.toString(), th);" + endl
					+ "\t\t}" + endl
					+ "\t}" + endl );
		}
		out.println ( "\tprotected abstract " + op.getImplSpec(scope) + " throws Exception;" + endl );
	}

	void generateBase(AbstractModelClass service, int scope) throws FileNotFoundException, UnsupportedEncodingException {
		String className = service.getBaseName(scope);
		String file;
		if (service.isServerOnly())
		{
			file = generator.getSyncDir();
		}
		else {
			file = generator.getCoreDir();
		}
		
		String packageName = service.getPackage(scope);

		file = file + File.separator + Util.packageToDir(packageName) + File.separator + className + ".java";

		File f = new File (file);
		f.getParentFile().mkdirs();
//		System.out.println ( "Generating " + f.getPath() );

		SmartPrintStream out = new SmartPrintStream (f);
		
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
			+ " * Spring Service base class for <code>" + service.getFullName(scope) + "</code>," + endl
			+ " * provides access to all services and entities referenced by this service. " + endl
			+ " * </p>" + endl
			+ " * " + endl
			+ " * see " + service.getFullName(scope) + endl
			+ " */" );
		
		boolean isAbstract = true;
			
		if (isAbstract)
			out.println ( "public abstract class " + className );
		else
			out.println ( "public class " + className );
		if (service.getSuperClass() != null)
			out.println ( " extends " + service.getSuperClass().getBaseFullName(scope) );
		out.println ( "\timplements " + service.getFullName(scope) ) ;
		out.println ( " {" );

		if (!parser.isTranslateOnly() && 
				(service.isTranslatedImpl()? 
						scope == Translate.SERVICE_SCOPE:
						scope == Translate.ALTSERVICE_SCOPE))
		{
			int reverseScope = service.isTranslatedImpl() ?
					Translate.ALTSERVICE_SCOPE: Translate.SERVICE_SCOPE;
			String name = service.getName(reverseScope);
			String fullName  = service.getFullName(reverseScope);
			String varName = Util.firstLower(name);
			outputDependency(out, fullName, name, varName);
		}
		else
		{
			//
			// Generate clients
			//

			for (AbstractModelClass provider: service.getDepends()) {
				if (provider != null) {
					String fullName;
					String name;
					String varName;
					if (provider . isEntity())
					{
						name = provider.getDaoName(scope);
						fullName = provider.getDaoFullName(scope);
						varName = provider.getVarName() + "Dao";
						outputDependency(out, fullName, name, varName);
						if (generator.translateEntities && generator.generateDeprecated &&
								!name.equals (provider.getDaoName(scope)))
						{
							name = provider.getDaoName(scope);
							fullName = provider.getDaoFullName(scope);
							varName = provider.getVarName() + "Dao";
							outputDependency(out, fullName, name, varName);
						}
					} else {
						name = provider.getName(scope);
						fullName  = provider.getFullName(scope);
						varName = provider.getVarName();
						outputDependency(out, fullName, name, varName);
					}
				}

			}
			
		}
		out. println ( );

		///////////////////////////////////////
		// Generate OPERATIONS

		for (ModelOperation op: service.getOperations())
		{
			generateOperationBase(service, op, scope, out);
		}



		if (generator.isGenerateEjb())
		{
			out .println( "\t/**" + endl
					+ "\t * Gets the current <code>principal</code> if one has been set," + endl
					+ "\t * otherwise returns <code>null</code>." + endl
					+ "\t *" + endl
					+ "\t * @return the current principal" + endl
					+ "\t */" + endl
					+ "\tprotected java.security.Principal getPrincipal()" + endl
					+ "\t{" + endl
					+ "\t\treturn "+rootPkg+".PrincipalStore.get();" + endl
					+ "\t}" + endl);
		}
		out.println( "}" );
		out.close();
	}


	void generateBaseProxy(AbstractModelClass service, int scope) throws FileNotFoundException, UnsupportedEncodingException {
		String className = service.getBaseName(scope) + "Proxy";
		String file;
		if (service.isServerOnly())
		{
			file = generator.getSyncDir();
		}
		else {
			file = generator.getCoreDir();
		}
		
		String packageName = service.getPackage(scope);

		file = file + File.separator + Util.packageToDir(packageName) + File.separator + className + ".java";

		File f = new File (file);
		f.getParentFile().mkdirs();
//		System.out.println ( "Generating " + f.getPath() );

		SmartPrintStream out = new SmartPrintStream (f);
		
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
			+ " * Spring Service base class for <code>" + service.getFullName(scope) + "</code>," + endl
			+ " * provides access to all services and entities referenced by this service. " + endl
			+ " * </p>" + endl
			+ " * " + endl
			+ " * see " + service.getFullName(scope) + endl
			+ " */" );
		
		boolean isAbstract = false;
			
		out.println ( "public class " + className );
		if (service.getSuperClass() != null)
			out.println ( " extends " + service.getSuperClass().getBaseFullName(scope) );
		out.println ( "\timplements " + service.getFullName(scope) ) ;
		out.println ( " {" );

		int reverseScope = service.isTranslatedImpl() ?
				Translate.ALTSERVICE_SCOPE: Translate.SERVICE_SCOPE;
		String name = service.getName(reverseScope);
		String fullName  = service.getFullName(reverseScope);
		String varName = Util.firstLower(name);
		outputDependency(out, fullName, name, varName);

		out. println ( );

		///////////////////////////////////////
		// Generate OPERATIONS

		for ( AbstractModelClass c = service; c != null; c = c.getSuperClass())
		{
			
			for (ModelOperation op: c.getOperations())
			{
				generateOperationBaseProxy(service, c, op, scope, false, out);
				if (c != service)
					generateOperationBaseProxy(service, c, op, scope, true, out);
			}
		}



		if (generator.isGenerateEjb())
		{
			out .println( "\t/**" + endl
					+ "\t * Gets the current <code>principal</code> if one has been set," + endl
					+ "\t * otherwise returns <code>null</code>." + endl
					+ "\t *" + endl
					+ "\t * @return the current principal" + endl
					+ "\t */" + endl
					+ "\tprotected java.security.Principal getPrincipal()" + endl
					+ "\t{" + endl
					+ "\t\treturn "+rootPkg+".PrincipalStore.get();" + endl
					+ "\t}" + endl);
		}
		out.println( "}" );
		out.close();
	}


	void generateOperationBaseProxy (AbstractModelClass baseClass, AbstractModelClass service, ModelOperation op, int scope, boolean voidHandle, SmartPrintStream out)
	{
		out.println ( "\t/**" + endl
				+ "\t * @see " + service.getFullName(scope) + "#"
				+ "\t * @see " + service.getFullName(Translate.DONT_TRANSLATE) + "#"
				+ op.getSpec(scope) + endl
				+ "\t */" );

		generateTransactionAnnotation(op, out);

		if (voidHandle)
		{
			out.println ( "\tprotected " + op.getImplSpec(scope) + " throws Exception" );
			out.println ("\t{");
			if (!op.getReturnParameter().getDataType().isVoid())
				out.print ( "\t\treturn null;" );
			out.println ("\t}");
		} else { 
			out.println ( "\tpublic " + op.getPrettySpec (scope) );
			
			String throwsClause = op.getThrowsClause(Translate.SERVICE_SCOPE);
			if (throwsClause.isEmpty())
				out.println ( "\t\tthrows "+generator.getDefaultException()+"" );
			else
				out.println ( "\t\t" + throwsClause );
			out.println ( "\t{" );

			int reverseScope = scope == Translate.ALTSERVICE_SCOPE ? Translate.SERVICE_SCOPE: Translate.ALTSERVICE_SCOPE;
			out.print("\t\t");
			if (!op.getReturnParameter().getDataType().isVoid())
				out.print ( "return " );

			ModelParameter result = op.getReturnParameter();
			String invocationSuffix = "";
			if (scope == Translate.ALTSERVICE_SCOPE)
			{
				if (result.getDataType().isArray() && result.getDataType().getChildClass() != null &&
						result.getDataType().getChildClass().isTranslated() && result.getDataType().getChildClass().isValueObject())
				{
					AbstractModelClass childclass = result.getDataType().getChildClass();
					out.print ( childclass.getFullName(scope) + ".to" + childclass.getName(scope) + "Array (" + endl + "\t\t\t\t" );
					invocationSuffix = ")";
				}
				else if (result.getDataType().isCollection() && result.getDataType().getChildClass() != null &&
						result.getDataType().getChildClass().isTranslated() && result.getDataType().getChildClass().isValueObject())
				{
					AbstractModelClass childclass = result.getDataType().getChildClass();
					if (result.getDataType().isFuture() && generator.getAsyncCollectionClass() != null)
					{
						out.print ( childclass.getFullName(scope) + ".to" + childclass.getName(scope) + "AsyncList (" + endl + "\t\t\t\t" );						
					}
					else
					{
						out.print ( childclass.getFullName(scope) + ".to" + childclass.getName(scope) + "List (" + endl + "\t\t\t\t" );
					}
					invocationSuffix = ")";
				}
				else if (result.getDataType().isTranslated() && result.getDataType().isValueObject())
				{
					out.print ( result.getDataType().getJavaType(scope) + ".to" + result.getDataType().getName(scope) + "(" + endl + "\t\t\t\t" );
					invocationSuffix = ")";
				}
			}
			out.print ( "get" + baseClass.getName(reverseScope) + "()." + op.getName(reverseScope) + "(" );
			boolean first = true;
			for (ModelParameter param: op.getParameters()) {
					if (first)
						first = false;
					else
						out.print ( ", " + endl
								+ "\t\t\t\t");
					
					if (param.getDataType().isCollection() && param.getDataType().getChildClass() != null &&
						param.getDataType().getChildClass().isTranslated() && param.getDataType().getChildClass().isValueObject())
					{
						out.print ( param.getDataType().getChildClass().getFullName(reverseScope)+ ".to" + 
							param.getDataType().getChildClass().getName(reverseScope) + "List ("
							+  param.getName(reverseScope) + ")" );
					}
					else if (param.getDataType().isTranslated() && param.getDataType().isValueObject())
					{
						out.print (  param.getDataType().getJavaType(reverseScope) + ".to" + 
									param.getDataType().getName(reverseScope)+ "("+ param.getName(scope) + ")" );
					}
					else if (param.getDataType().isTranslated() && param.getDataType().isEnumeration())
					{
						out.print (  param.getDataType().getJavaType(reverseScope) + ".fromString" + 
									"("+ param.getName(scope) + ".toString())" );
					}
					else
						out.print ( param.getName(scope) );

			}
			out.println (invocationSuffix+");" + endl
					+ "\t}"+endl);
		}

	}

	private void outputDependency(SmartPrintStream out, String fullName,
			String name, String varName) {
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

	void generateEjbInterface (AbstractModelClass service, int scope) throws FileNotFoundException, UnsupportedEncodingException {

		if (service.isInternal() || service.isServerOnly())
			return;

		String packageName = service.getPackage(scope);

		String file;
		file = generator.getCommonsDir() + File.separator+ Util.packageToDir(packageName) + 
				"ejb/" + service.getName(scope) + ".java";

		File f = new File (file);
		f.getParentFile().mkdirs();
//		System.out.println ( "Generating " + f.getPath() );

		SmartPrintStream out = new SmartPrintStream (f);

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
				+ " * EJB " + service.getName(scope) + endl
				+ Util.formatComments(service.getComments())
				+ " */" );
		out.println ( "public interface " + service.getName(scope) + endl);
		if (generator.isTargetJboss3())
				out.println( "\textends javax.ejb.EJBLocalObject" );
		out.println ( " {" );
		out.println ( );
		for (ModelOperation op: service.getOperations())
		{
			if (!op.getActors().isEmpty() || ! service.getActors().isEmpty())
				out.println ( "\t" + op.getPrettySpec (scope) + endl + "\t" + op.getThrowsClause(Translate.SERVICE_SCOPE) + ";" + endl );
		}
		out.println ( "}" );
		out.close();
	}


	void generateEjbHome (AbstractModelClass service, int scope) throws FileNotFoundException, UnsupportedEncodingException {

		if (service.isInternal() || service.isServerOnly())
			return;

		String packageName = service.getPackage(scope);

		String file;
		file = generator.getCommonsDir() + "/" + Util.packageToDir(packageName) + "ejb/" + service.getName(scope) + "Home.java";

		File f = new File (file);
		f.getParentFile().mkdirs();
//		System.out.println ( "Generating " + f.getPath() );

		SmartPrintStream out = new SmartPrintStream (f);

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
				+ " * EJB Home " + service.getName(scope) + endl
				+ Util.formatComments(service.getComments())
				+ " */" );
		out.println ( "public interface " + service.getName(scope) + "Home" + endl
				+ "\textends javax.ejb.EJBLocalHome" );
		out.println ( " {" + endl
				+ "\t/**" + endl
				+ "\t * The logical JDNI name" +endl
				+ "\t */" + endl
				+ "\tpublic static final String COMP_NAME=\"java:comp/ejb/" + service.getName(scope) + "\";"
				+ endl + endl
				+ "\t/**" + endl
				+ "\t * The physical JDNI name" +endl
				+ "\t */" + endl
				+ "\tpublic static final String JNDI_NAME=\"openejb:/local/soffid.ejb." + service.getFullName(scope) + "\";"
				+ endl + endl);
		if (generator.isTargetJboss3())
			out.println (
				  "\tpublic "+ packageName  + ".ejb." + service.getName(scope) + " create()" + endl
				+ "\t\tthrows javax.ejb.CreateException;" + endl + endl);
		out.println( "}" );
		out.close();
	}


	void generateEjbBean(AbstractModelClass service, int scope) throws FileNotFoundException, UnsupportedEncodingException {

		int inverseScope = scope == Translate.SERVICE_SCOPE ? Translate.ALTSERVICE_SCOPE : Translate.SERVICE_SCOPE;
		
		String className= service.getBeanName(scope);

		String packageName = service.getPackage(scope);

		String file = generator.getCoreDir() + "/" + Util.packageToDir(packageName ) + "ejb/" + className + ".java";

		File f = new File (file);
		f.getParentFile().mkdirs();
//		System.out.println ( "Generating " + f.getPath() );

		SmartPrintStream out = new SmartPrintStream (f);

		out.println ( "//" + endl
				+ "// (C) 2013 Soffid" + endl
				+ "//" + endl
				+ "//" + endl
				);
		if (!packageName.isEmpty())
			out.println ( "package " + packageName + ".ejb;" );

		out.println ( "/**" + endl
			+ " * @see <code>" + service.getFullName(scope) + "</code>," + endl
			+ " * @see <code>" + service.getFullName(Translate.DONT_TRANSLATE) + "</code>," + endl
			+ " */" );
		if (generator.isTargetTomee())
		{
			if (service.isStateful())
			{
				out.println("@javax.ejb.Stateful(name=\""+service.getEjbName(scope)+"\")");
			}
			else
				out.println("@javax.ejb.Stateless(name=\""+service.getEjbName(scope)+"\")");
			if (generator.isTargetTomee())
				out.println("@javax.ejb.Local("+service.getEjbInterfaceFullName(scope)+".class)");
			out.println("@javax.ejb.TransactionManagement(value=javax.ejb.TransactionManagementType.CONTAINER)");
			out.println("@javax.ejb.TransactionAttribute(value=javax.ejb.TransactionAttributeType.SUPPORTS)");
		}
		out.print ( "public class " + className );
		if (service.isStateful() && generator.isTargetTomee())
			out.println ( " extends org.springframework.ejb.support.AbstractStatefulSessionBean" );
		else
			out.println ( " extends org.springframework.ejb.support.AbstractStatelessSessionBean" );
		if (generator.isTargetTomee())
		{
			out.println ("  implements "+service.getEjbInterfaceFullName(scope));
		}
		String svcName = service.getLocalServiceName(scope);
		out.println ( "{" + endl
				+ "\tprivate " + service.getFullName(scope) + " " + svcName + ";" + endl );

		for (ModelOperation op: service.getOperations())
		{
			if (!op.getActors().isEmpty() || ! service.getActors().isEmpty())
			{
				out.println ( "\t/**" + endl
						+ "\t * @see " + service.getFullName(scope) + "#"
						+ op.getSpec(scope) + endl
						+ "\t */" );
				// Generate authorization tags
				Collection<AbstractModelClass> actors = op.getActors();
				if (! generator.isManualSecurityCheck())
				{
					if (!actors.isEmpty())
					{
						StringBuffer annotation = new StringBuffer ("\t@javax.annotation.security.RolesAllowed({");
						String closeAnnotation = "})";
						boolean first = true;
						for (AbstractModelClass actor: actors)
						{
							if (actor.getRoleName().equals ("anonymous") || actor.getRoleName().equals("*"))
							{
								annotation = new StringBuffer("\t@javax.annotation.security.PermitAll");
								closeAnnotation = "";
								break;
							}
							else
							{
								if (first) first = false;
								else annotation.append(", ");
								annotation.append ("\"").append(Util.hardTrim(actor.getRoleName())).append("\"");
								
							}
						}
						annotation.append(closeAnnotation);
						out.println (annotation);
					}
				}
				else
				{
					out.println("\t@javax.annotation.security.PermitAll");
				}
				// Generate method body
				out.println ( "\tpublic " + op.getPrettySpec (scope) );
				String throwsClause = op.getThrowsClause(Translate.SERVICE_SCOPE);
				if (!throwsClause.isEmpty())
					out.println ( "\t\t" + throwsClause );
				out.println ( "\t{" );
				out.println ( "\t\t"+rootPkg+".PrincipalStore.set(super.getSessionContext().getCallerPrincipal());" );
				// Add role checks
				if (generator.isManualSecurityCheck())
				{
					boolean unchecked = op.getActors().isEmpty();
					for (AbstractModelClass actor: op.getActors())
					{
						if (actor.getRoleName().equals ("anonymous") || actor.getRoleName().equals("*"))
							unchecked = true;
					}
					if ( ! unchecked )
					{
//						out.println ("\t\tcom.soffid.iam.common.security.SoffidPrincipal soffidPrincipal =");
//						out.println ("\t\t\t(com.soffid.iam.common.security.SoffidPrincipal) super.getSessionContext().getCallerPrincipal();");
						Iterator<AbstractModelClass> it = op.getActors().iterator();
						AbstractModelClass actor = it.next();
						String securityPackage  = generator.getBasePackage() == null ? 
								"com.soffid.iam":
								generator.getBasePackage();
						out.print ("\t\tif (! "+ securityPackage+".utils.Security.isUserInRole(\""+ actor.getRoleName()+ "\")");
						while (it.hasNext())
						{
							actor = it.next();
							out.print ("&&\n\t\t\t ! "+ securityPackage +".utils.Security.isUserInRole(\""+ actor.getRoleName()+ "\")");
						}
						out.println (")");
						out.print ("\t\t\tthrow new SecurityException(\"Unable to execute " + service.getName(scope)+"."+op.getName(scope)+
								". Required roles: [");
						it = op.getActors().iterator();
						actor = it.next();
						out.print (actor.getRoleName());
						while (it.hasNext())
						{
							actor = it.next();
							out.print (", "+ actor.getName());
						}
						out.println ("]\");");
					}
				}

				///////////////////////
				// Add null checks
//				generateNullChecks(out, op, scope);
				out.print ( "\t\ttry" + endl
						+ "\t\t{" + endl
						+ "\t\t\t" );
				if (!op.getReturnParameter().getDataType().isVoid())
					out.print ( "return " );

				ModelParameter result = op.getReturnParameter();
				String invocationSuffix = "";
				out.print ( "this." + svcName + "." + op.getName(scope) + "(" );
				boolean first = true;
				for (ModelParameter param: op.getParameters()) {
						if (first)
							first = false;
						else
							out.print ( ", " );
						out.print ( param.getName(scope) );

				}
				out.println ( ")" + invocationSuffix + "; " + endl
						+ "\t\t}" + endl
						+ "\t\tcatch (Exception exception)" + endl
						+ "\t\t{" + endl
						+ "\t\t\tfinal Throwable cause = getRootCause(exception);" );
				for (AbstractModelClass ex: op.getExceptions()) {
					out.println ( "\t\t\tif (cause instanceof " + ex.getFullName(Translate.SERVICE_SCOPE) + ")" + endl
							+ "\t\t\t\tthrow (" + ex.getFullName(Translate.SERVICE_SCOPE) + ") cause;" );
				}
				out.println ( "\t\t\tif (exception instanceof RuntimeException)" + endl
					+ "\t\t\t\tthrow (RuntimeException)exception;" + endl
					+ "\t\t\tthrow new javax.ejb.EJBException (exception);" + endl
					+ "\t\t}" + endl
					+ "\t}" );
			}
		}

		if (generator.isTargetJboss3())
		{
			// Generate init method
			out.println ( "\t/**" + endl
					+ "\t * Every Spring Session EJB needs to" + endl
					+ "\t * call this to instantiate the Spring" + endl
					+ "\t * Business Object." + endl
					+ "\t *" + endl
					+ "\t * @see org.springframework.ejb.support.AbstractStatelessSessionBean#onEjbCreate()" + endl
					+ "\t */" +endl
					+ "\tprotected void onEjbCreate()" + endl
					+ "\t{" + endl
					+ "\t\tthis." + svcName  + " = (" + service.getFullName(scope)+ ")" + endl
					+ "\t\tgetBeanFactory().getBean(\"" + service.getSpringBeanName(generator, scope) + "\");" + endl
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
					+ "\torg.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog (getClass());" + endl);

		} else {

			out.println ( "\t/**" + endl
					+ "\t * Initizlizes been" + endl
					+ "\t *" + endl
					+ "\t * @see org.springframework.ejb.support.AbstractStatelessSessionBean#onEjbCreate()" + endl
					+ "\t */" + endl);
			if (! service.isStateful())
			{
				out.println("\t@Override" + endl);
				out.println("\t@javax.annotation.PostConstruct" + endl
						+ "\tpublic void ejbCreate() throws javax.ejb.CreateException" + endl
						+ "\t{" + endl
						+ "\t\tsuper.ejbCreate();" + endl
						+ "\t}" + endl+endl);
			}
			else
			{
				out.println("\t@javax.annotation.PostConstruct" + endl
						+ "\tpublic void ejbCreate() throws javax.ejb.CreateException" + endl
						+ "\t{" + endl
						+ "\t\tonEjbCreate();" + endl
						+ "\t}" + endl+endl);
			}
			out.println(
					"\tprotected void onEjbCreate()" + endl
					+ "\t{" + endl);
			if (service.isStateful())
			{
				out.println("\t\tloadBeanFactory();");
			}
			out.println("\t\tthis." + svcName  + " = (" + service.getFullName(scope)+ ")" + endl
					+ "\t\tgetBeanFactory().getBean(\"" + service.getSpringBeanName(generator, scope) + "\");" + endl
					+ "\t}" + endl);
			if (service.isStateful())
			{
				out.println ( "\t" + endl
						+ "\t/**" + endl
						+ "\t/**" + endl
						+ "\t * Every Session bean needs to implement this method" + endl
						+ "\t *" + endl
						+ "\t * @see javax.ejb.SessionBean#ejbPassivate()" + endl
						+ "\t */" +endl
						+ "\tpublic void ejbPassivate()" + endl
						+ "\t{" + endl
						+ "\t}" + endl);

				out.println ( "\t" + endl
						+ "\t/**" + endl
						+ "\t/**" + endl
						+ "\t * Every Session bean needs to implement this method" + endl
						+ "\t *" + endl
						+ "\t * @see javax.ejb.SessionBean#ejbActivate()" + endl
						+ "\t */" +endl
						+ "\tpublic void ejbActivate()" + endl
						+ "\t{" + endl
						+ "\t}" + endl);
			}
			
			out.println("\t" + endl
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
					+ "\torg.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog (getClass());" + endl);
		}
		out.println ( 
			  "\t/**" + endl
			+ "\t * Finds the root cause of the parent exception" + endl
			+ "\t * by traveling up the exception tree." + endl
			+ "\t */" 
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

	void generateUml (AbstractModelClass service, int scope) throws IOException {
		String file;
		file = generator.getUmlDir();
		String packageName = service.getPackage(scope);

		file = file + File.separator + Util.packageToDir(packageName);

		file += service.getName(scope);
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
		source.append (service.generatePlantUml(service,scope, false, true));

		boolean generate = Util.isModifiedClass(service, f);
		for (AbstractModelClass provider: service.getDepends()) {
			generate = generate || Util.isModifiedClass(provider, f);
			if (provider.isEntity())
			{
				source.append( provider.generatePlantUml(service,scope, true, false) );
				for (AbstractModelClass vo: provider.getDepends())
				{
					if (vo.isValueObject())
					{
						generate = generate || Util.isModifiedClass(vo, f);
						source.append (vo.generatePlantUml(service, scope, true, false));
						source.append (provider.getName(scope) + " ..> "+vo.getName(scope)+endl);
					}
				}
			}
			else 
			{
				source.append( provider.generatePlantUml(service,scope, false, false) );
			}
			source.append (service.getName(scope) + " ..> "+provider.getName(scope)+endl);
		}
		source.append ("@enduml");

		if (generate)
		{
//			System.out.println ( "Generating " + f.getPath() );
			SourceStringReader reader = new SourceStringReader(source.toString());
			reader.generateImage(new FileOutputStream(f), new FileFormatOption(FileFormat.SVG));
		}
	}

	void generateUmlUseCase (AbstractModelClass service, int scope) throws IOException {
		String file;
		file = generator.getUmlDir();
		String packageName = service.getPackage(scope);

		file = file + File.separator + Util.packageToDir(packageName);

		file += service.getName(scope);
		file += "-uc.svg";
		File f = new File (file);
		
		if ( Util.isModifiedClass(service, f))
		{
			f.getParentFile().mkdirs();
	
//			System.out.println ( "Generating " + f.getPath() );
	
	
			StringBuffer source = new StringBuffer();
			source.append ("@startuml" + endl +
					"left to right direction"+endl+
					"skinparam backgroundColor white"+endl+
					"skinparam packageStyle rect"+endl+
					"("+service.getName(scope)+")"+ endl );
			
			boolean left = true;
			for (AbstractModelClass actor: service.getAllActors())
			{
				actor.left = left;
				source.append ("actor "+actor.getName(scope) +endl);
				left = !left;
			}
			
			source.append ("rectangle "+service.getName()+ " {"+ endl);
			for (ModelOperation op: service.getOperations()) {
				Collection<AbstractModelClass> actors = op.getActors();
				source.append ("usecase ").append(op.getName(scope)).append("\n");
				for (AbstractModelClass actor: actors)
				{
					if (actor.left)
						source.append (actor.getName(scope) + " -- ("+op.getName(scope)+")"+endl);
					else
						source.append ("("+op.getName(scope)+ ") -- "+actor.getName(scope) +endl);
				}
			}
			source.append ("}"+endl);
			source.append ("@enduml");
			
			SourceStringReader reader = new SourceStringReader(source.toString());
			
			reader.generateImage(new FileOutputStream(f), new FileFormatOption(FileFormat.SVG));
		}
	}
}
