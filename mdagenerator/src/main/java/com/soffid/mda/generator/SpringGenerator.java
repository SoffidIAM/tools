package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import com.soffid.mda.parser.AbstractModelClass;
import com.soffid.mda.parser.Parser;

public class SpringGenerator {

	private Generator generator;
	private Parser parser;
	private boolean translated;
	
	final static String endl = "\n";

	public void generate(Generator generator, Parser parser) throws FileNotFoundException, UnsupportedEncodingException {
		this.generator = generator;
		this.parser = parser;
		this.translated = generator.isTranslatedOnly();

		generateApplicationContext(false, false);
		generateApplicationContext(false, true);
		if (! generator.isPlugin())
		{
			generateApplicationContext(true, false);
			generateApplicationContext(true, true);
		}
	}
	
	void generateApplicationContext (boolean sync, boolean  test) throws FileNotFoundException, UnsupportedEncodingException {
		boolean console = ! sync && ! generator.isPlugin();
		String file;
		if (! sync)
		{
			file = generator.getCoreResourcesDir();
			if (test)
			{
				file = generator.getCoreTestResourcesDir ();
				if (generator.isPlugin())
					file = file + "/plugin-test-applicationContext.xml";
				else
					file = file + "/console-test-applicationContext.xml";
			}
			else
			{
				file = generator.getCoreResourcesDir();
				if (generator.isPlugin())
					file += "/plugin-applicationContext.xml";
				else
					file += "/console-applicationContext.xml";
			}
		}
		else 
		{
			if (test)
			{
				file = generator.getSyncResourcesDir() + "/sync-test-applicationContext.xml";
			}
			else
			{
				file = generator.getSyncResourcesDir() + "/sync-applicationContext.xml";
			}
		}

		File f = new File (file );
		f.getParentFile().mkdirs();
		PrintStream out = new PrintStream(f, "UTF-8");

		System.out.println ("Generating "+f.getPath());

		out .println ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl
				+ "<!DOCTYPE beans PUBLIC \"-//SPRING//DTD BEAN//EN\" \"http://www.springframework.org/dtd/spring-beans.dtd\">" + endl
				+ "" + endl
				+ "<beans>" + endl
				+ "" );
		if (console)
		{
			out.println(
				  "\t<!-- ========================= GENERAL DEFINITIONS ========================= -->" + endl
				+ "" + endl
				+ "\t<!-- Message source for this context, loaded from localized \"messages_xx\" files -->" + endl
				+ "\t<bean id=\"messageSource\" class=\"org.springframework.context.support.ResourceBundleMessageSource\">" + endl
				+ "\t\t<property name=\"basename\"><value>messages</value></property>" + endl
				+ "\t</bean>" + endl
				+ "" + endl
				+ "\t<!-- Hibernate SessionFactory -->" + endl
				+ "\t<bean id=\"sessionFactory\" class=\"es.caib.seycon.ng.spring.CustomLocalSessionFactoryBean\">" + endl
				+ "\t\t<property name=\"dataSource\"><ref bean=\"dataSource\"/></property>" + endl
				+ "\t\t<property name=\"lobHandler\" ref=\"lobHandler\"/>" + endl
				+ "\t\t<property name=\"mappingResources\">" + endl
				+ "\t\t\t<list>" );
			for (AbstractModelClass entity: parser.getEntities()) {
				out.println ( "\t\t\t\t<value>" + Util.packageToDir(entity.getPackagePrefix())
					+ entity.getName(translated) + ".hbm.xml</value>" );

			}

			out.println ( "\t\t\t</list>" + endl
				+ "\t\t</property>" + endl
				+ "\t\t<property name=\"hibernateProperties\">" + endl
				+ "\t\t\t<props>" + endl
				+ "\t\t\t\t<prop key=\"hibernate.show_sql\">false</prop>" + endl
				+ "\t\t\t\t<prop key=\"hibernate.dialect\">es.caib.seycon.ng.model.CustomDialect</prop>" + endl
				+ "\t\t\t\t<prop key=\"hibernate.jdbc.batch_size\">0</prop>" );
			if (test)
				out.println ( "\t\t\t\t<prop key=\"hibernate.hbm2ddl.auto\">update</prop>" );
			out.println ( "\t\t\t\t<prop key=\"hibernate.cache.use_query_cache\">false</prop>" + endl
				+ "\t\t\t\t<prop key=\"hibernate.cache.provider_class\">org.hibernate.cache.EhCacheProvider</prop>" + endl
				+ "\t\t\t\t<prop key=\"hibernate.cache.query_cache_factory\">org.hibernate.cache.StandardQueryCacheFactory</prop>" + endl
				+ "\t\t\t</props>" + endl
				+ "\t\t</property>" + endl
				+ "\t</bean>" + endl
				+ "" + endl
				+ "\t<bean id=\"lobHandler\" class=\"org.springframework.jdbc.support.lob.DefaultLobHandler\" lazy-init=\"true\">" + endl
				+ "	</bean>" + endl
				+ "" + endl
				+ "\t<!-- The Hibernate interceptor -->" + endl
				+ "\t<bean id=\"hibernateInterceptor\" class=\"org.springframework.orm.hibernate3.HibernateInterceptor\">" + endl
				+ "\t\t<property name=\"sessionFactory\"><ref bean=\"sessionFactory\"/></property>" + endl
				+ "\t\t<property name=\"flushModeName\"><value>FLUSH_COMMIT</value></property>" + endl
				+ "\t</bean>" + endl
				+ "" );
		}
		else if (! sync)
		{
			out.println(
				  "\t<!-- ========================= HIBERNATE PROPERTIES ========================= -->" + endl
				+ "" + endl
				+ "\t<bean id=\"hibernate-addon-" + generator.pluginName + "\" class=\"es.caib.seycon.ng.spring.AddonHibernateBean\">" + endl
				+ "\t\t<property name=\"mappingResources\">" + endl
				+ "\t\t\t<list>" );
			for (AbstractModelClass entity: parser.getEntities()) {
				out.println ( "\t\t\t\t<value>" + Util.packageToDir(entity.getPackagePrefix())
					+ entity.getName(translated) + ".hbm.xml</value>" );

			}

			out.println ( "\t\t\t</list>" + endl
				+ "\t\t</property>" );
			if (test)
			{
				out.println ( "\t\t<property name=\"hibernateProperties\">" + endl
					+ "\t\t\t<props>" + endl
					+ "\t\t\t\t<prop key=\"hibernate.hbm2ddl.auto\">update</prop>" + endl
					+ "\t\t\t</props>" + endl
					+ "\t\t</property>" );
			}
			out	.println ( "\t</bean>" + endl
				+ "" );

		}

		if (!generator.isPlugin())
		{

			out.println(
				  "\t<!-- ========================= IDENTITY GENERATOR ========================= -->" + endl
				+ "" );

			if (sync)
				out.println ( "\t<bean id=\"identity-generator-sync\" class=\"es.caib.seycon.ng.sync.identity.IdentityGeneratorBean\">" );
			else
				out.println ( "\t<bean id=\"identity-generator\" class=\"es.caib.seycon.ng.model.identity.IdentityGeneratorBean\">" );

			out.println ( "\t\t<property name=\"tableName\">" + endl
				+ "\t\t\t<value>SC_SEQUENCE</value>" + endl
				+ "\t\t</property>" + endl
				+ "\t\t<property name=\"dataSource\"><ref bean=\"dataSource\"/></property>" );
			if (test)
				out.println ( "\t\t<property name=\"createTable\">" + endl
					+ "\t\t\t<value>true</value>" + endl
					+ "\t\t</property>" );
			out.println ( "\t</bean>" + endl
				+ "" );
		}

		out.println ( "\t<!-- ========================= HIBERNATE DAOs ========================= -->" );

		if (! sync )
		{
			for (AbstractModelClass entity: parser.getEntities()) {
				out.println ( "\t<!-- " + entity.getName(translated) + " Entity Proxy with inner " + entity.getName(translated)+" Entity implementation -->" + endl
					+ "\t<bean id=\"" + entity.getSpringBeanName(generator, false) + "\" class=\"org.springframework.aop.framework.ProxyFactoryBean\">" + endl
					+ "\t\t<property name=\"target\">" + endl
					+ "\t\t\t<bean class=\"" + entity.getDaoImplFullName(translated) + "\">" + endl
					+ "\t\t\t\t<property name=\"sessionFactory\"><ref bean=\"sessionFactory\"/></property>" );
				AbstractModelClass superClass = entity;
				do
				{
					for (AbstractModelClass dep: superClass.getDepends()) {
						if (dep != null)
						{
							if (dep.isEntity())
								out.println ( "\t\t\t\t<lookup-method name=\"get" + dep.getDaoName(translated) + "\" " +
									"bean=\"" + dep.getSpringBeanName(generator, false) + "\"/>" );
							else if (dep.isService())
								out.println ( "\t\t\t\t<lookup-method name=\"get" + dep.getName(translated) + "\" " +
									"bean=\"" + dep.getSpringBeanName(generator, false) + "\"/>" );
						}
					}
					superClass = superClass.getSuperClass();
				} while (superClass != null && superClass.isEntity());

				out.println( "\t\t\t</bean>" + endl
					+ "\t\t</property>" + endl
					+ "\t\t<property name=\"proxyInterfaces\">" + endl
					+ "\t\t\t<value>" + entity.getDaoFullName(translated)+ "</value>" + endl
					+ "\t\t</property>" + endl
					+ "\t\t<property name=\"interceptorNames\">" + endl
					+ "\t\t\t<list>" + endl
					+ "\t\t\t\t<value>hibernateInterceptor</value>" + endl
					+ "\t\t\t\t<value>daoInterceptor-*</value>" + endl
					+ "\t\t\t\t<value>" + entity.getSpringBeanName(generator,false) + "Interceptor-*</value>" + endl
					+ "\t\t\t</list>" + endl
					+ "\t\t</property>" + endl
					+ "\t</bean>" + endl
					+ "" );
			}
		}


		if (console)
		{
			out.println ( "\t<!-- ========================= TRANSACTION ANNOTATION ========================= -->" + endl
				+ "" + endl
				+ "\t<!-- Service Transactional Interceptor -->" + endl
				+ "\t<bean id=\"serviceTransactionInterceptor\" class=\"org.springframework.transaction.interceptor.TransactionInterceptor\">" + endl
				+ "\t\t<property name=\"transactionManager\"><ref bean=\"transactionManager\"/></property>" + endl
				+ "\t\t<property name=\"transactionAttributeSource\">" + endl
				+ "\t\t\t<bean class=\"org.springframework.transaction.annotation.AnnotationTransactionAttributeSource\"/>" + endl
				+ "\t\t</property>" + endl
				+ "\t</bean>" + endl
				+ "" );
		}

		out.println ( "\t<!-- ========================= Start of SERVICE DEFINITIONS ========================= -->" );

		for (AbstractModelClass service: parser.getServices()) {
			if ( (sync ? service.isServerOnly(): ! service.isServerOnly()))
			{
				out.println ( "\t<!-- " + service.getSpringBeanName(generator,false) + " Service Proxy with inner " + service.getName(translated) + " Service Implementation -->" );
				if (service.isStateful())
				{
					out.println ( "\t<bean id=\"" + service.getSpringBeanName(generator,false) + "Target\" "+ 
							"class=\"" + service.getImplFullName() +"\" singleton=\"false\">" );
					generateBeanInjections(out, service);
					out.println ( "\t</bean>" );
					out.println ();
				}

				out.println ("\t<bean id=\"" + service.getSpringBeanName(generator,false) + "\" class=\"org.springframework.aop.framework.ProxyFactoryBean\">" );
				if (service.isStateful())
				{
					out.println ("\t\t<property name=\"singleton\"><value>false</value></property>");
					out.println("\t\t<property name=\"target\"><ref bean=\""+service.getSpringBeanName(generator,false)+"Target\"/></property>");
					out.println("\t\t<property name=\"targetName\" value=\""+service.getSpringBeanName(generator,false)+"Target\"/>");
				} else {
					out.print("\t\t<property name=\"target\">" + endl +
							"\t\t\t<bean class=\"" + service.getImplFullName() +"\">" );
					generateBeanInjections(out, service);

					out.println ( "\t\t\t</bean>" );
					out.println ( "\t\t</property>");
				}
				
				out.println ( "\t\t<property name=\"proxyInterfaces\">" + endl
					+ "\t\t\t<value>" + service.getFullName() + "</value>" + endl
					+ "\t\t</property>" + endl
					+ "\t\t<property name=\"interceptorNames\">" + endl
					+ "\t\t\t<list>" + endl
					+ "\t\t\t\t<value>serviceTransactionInterceptor</value>" + endl
					+ "\t\t\t\t<value>hibernateInterceptor</value>" + endl
					+ "\t\t\t\t<value>serviceInterceptor-*</value>" + endl
					+ "\t\t\t\t<value>" + service.getSpringBeanName(generator, false) + "Interceptor-*</value>" + endl
					+ "\t\t\t</list>" + endl
					+ "\t\t</property>" + endl
					+ "\t</bean>" + endl
					+ "" );
				
			}
		}
		out.println ( "\t<!-- ========================= End of SERVICE DEFINITIONS ========================= -->" + endl
				+ "" + endl
				+ "</beans>" );
	}

	private void generateBeanInjections(PrintStream out,
			AbstractModelClass service) {
		AbstractModelClass current = service;
		do {
			for (AbstractModelClass provider: current.getDepends()) {
				if (provider != null && provider.isEntity())
				{
					out.println ( "\t\t\t\t<property name=\"" + Util.firstLower(provider.getDaoName(translated))
						+ "\"><ref bean=\"" + provider.getSpringBeanName(generator, false)
						+ "\"/></property>" );
				}
				else if (provider != null && provider.isService())
				{
					out.println ( "\t\t\t\t<lookup-method name=\"get" + provider.getName(translated)
						+ "\" bean=\"" + provider.getSpringBeanName(generator, false)
						+ "\"/>" );
				}
			}
			current = current.getSuperClass();
		} while (current != null);
	}


}
