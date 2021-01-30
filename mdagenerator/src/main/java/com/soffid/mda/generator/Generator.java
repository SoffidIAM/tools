package com.soffid.mda.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;

import com.soffid.mda.parser.ModelClass;
import com.soffid.mda.parser.Parser;

public class Generator {
	String commonsDir;
	String coreDir;
	String coreSrcDir;
	String coreTestSrcDir;
	String coreResourcesDir;
	String coreTestResourcesDir;
	String syncDir;
	String syncResourcesDir;
	String xmlModuleDir; 
	String targetServer = "jboss3"; 
	String asyncCollectionClass = null;
	String pagedCollectionClass = null;
	
	public String getTargetServer() {
		return targetServer;
	}

	public void setTargetServer(String targetServer) {
		this.targetServer = targetServer;
	}
	
	public boolean isTargetTomee () 
	{
		return "tomee".equals(targetServer);
	}

	
	public boolean isTargetJboss3 () 
	{
		return "jboss3".equals(targetServer);
	}

	public boolean isHqlFullTest() {
		return hqlFullTest;
	}

	public void setHqlFullTest(boolean hqlFullTest) {
		this.hqlFullTest = hqlFullTest;
	}

	String jascutDir;
	String docDir;
	boolean generateUml = false;
	boolean translateEntities = false;
	boolean generateDeprecated = false;
	boolean hqlFullTest = true;
	boolean generateEjb = true;
	boolean manualSecurityCheck = true;

	public boolean isGenerateEjb() {
		return generateEjb;
	}

	public void setGenerateEjb(boolean generateEjb) {
		this.generateEjb = generateEjb;
	}

	public boolean isGenerateSync() {
		return generateSync;
	}

	public void setGenerateSync(boolean generateSync) {
		this.generateSync = generateSync;
	}

	boolean generateSync = true;
	public boolean isGenerateDeprecated() {
		return generateDeprecated;
	}

	public void setGenerateDeprecated(boolean generateDeprecated) {
		this.generateDeprecated = generateDeprecated;
	}

	public boolean isTranslateEntities() {
		return translateEntities;
	}

	public void setTranslateEntities(boolean translateEntities) {
		this.translateEntities = translateEntities;
	}

	public boolean isGenerateUml() {
		return generateUml;
	}

	public void setGenerateUml(boolean generateUml) {
		this.generateUml = generateUml;
	}

	public String getUmlDir() {
		return umlDir;
	}

	public void setUmlDir(String umlDir) {
		this.umlDir = umlDir;
	}

	String umlDir;
	
	public String getXmlModuleDir() {
		return xmlModuleDir;
	}

	public void setXmlModuleDir(String xmlModuleDir) {
		this.xmlModuleDir = xmlModuleDir;
	}

	public String getJascutDir() {
		return jascutDir;
	}

	public void setJascutDir(String jascutDir) {
		this.jascutDir = jascutDir;
	}

	public boolean isTranslatedOnly() {
		return translatedOnly;
	}

	public void setTranslatedOnly(boolean translatedOnly) {
		this.translatedOnly = translatedOnly;
	}

	boolean translatedOnly;

	public void configure (File dir)
	{
		commonsDir = new File (dir, "common").getPath();
		coreDir = new File (dir, "core").getPath ();
		coreSrcDir = new File (dir, "core-src").getPath ();
		coreTestSrcDir = new File (dir, "core-test").getPath ();
		coreResourcesDir = new File (dir, "core-resource").getPath ();
		coreTestResourcesDir = new File (dir, "core-test-resource").getPath ();
		syncDir = new File (dir, "sync").getPath ();
		syncResourcesDir = new File (dir, "sync-resource").getPath ();
		xmlModuleDir = new File (dir, "xmi").getPath();
		jascutDir = new File (dir, "jascut").getPath();
		umlDir = new File (dir, "uml").getPath();
		docDir = new File (dir, "doc").getPath();
	}
	
	public String getDocDir() {
		return docDir;
	}

	public void setDocDir(String docDir) {
		this.docDir = docDir;
	}

	public String getXmlModule() {
		return xmlModuleDir;
	}

	public void setXmlModule(String xmlModule) {
		this.xmlModuleDir = xmlModule;
	}

	public String getSyncResourcesDir() {
		return syncResourcesDir;
	}

	public void setSyncResourcesDir(String syncResourcesDir) {
		this.syncResourcesDir = syncResourcesDir;
	}

	public void setCoreSrcDir(String coreSrcDir) {
		this.coreSrcDir = coreSrcDir;
	}

	String pluginName;
	private String basePackage;
	private String defaultException = "es.caib.seycon.ng.exception.InternalErrorException";
	
	public String getDefaultException() {
		return defaultException;
	}

	public boolean isPlugin ()
	{
		return pluginName != null;
	}
	
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	
	public String getCommonsDir() {
		return commonsDir;
	}

	public void setCommonsDir(String commonsDir) {
		this.commonsDir = commonsDir;
	}

	public String getCoreDir() {
		return coreDir;
	}

	public void setCoreDir(String coreDir) {
		this.coreDir = coreDir;
	}

	public String getSyncDir() {
		return syncDir;
	}

	public void setSyncDir(String syncDir) {
		this.syncDir = syncDir;
	}

	public void generate (Parser parser) throws IOException
	{
		parser.setDefaultException(getDefaultException());
		parser.setTranslateOnly (this.isTranslatedOnly());
		parser.setTranslateEntities(translateEntities);
		new EntityGenerator().generate (this, parser);
		new ServiceGenerator().generate(this, parser);
		new ValueObjectGenerator().generate (this, parser);
		new SpringGenerator().generate (this, parser);
		new SqlGenerator().generate (this, parser);
		new CodeMirrorGenerator().generate (this, parser);
		new XmiGenerator().generate (this, parser);
		new JascutGenerator().generate (this, parser);
		new DocGenerator().generate(this, parser);
	}

	public String getCoreTestSrcDir() {
		return coreTestSrcDir;
	}

	public void setCoreTestSrcDir(String coreTestSrcDir) {
		this.coreTestSrcDir = coreTestSrcDir;
	}

	public String getCoreTestResourcesDir() {
		return coreTestResourcesDir;
	}

	public void setCoreTestResourcesDir(String coreTestResourcesDir) {
		this.coreTestResourcesDir = coreTestResourcesDir;
	}

	public String getCoreSrcDir() {
		return coreSrcDir;
	}

	public String getCoreResourcesDir() {
		return coreResourcesDir;
	}

	public void setCoreResourcesDir(String coreResourcesDir) {
		this.coreResourcesDir = coreResourcesDir;
	}

	public String getRootPkg() {
		if (translatedOnly)
			return "com.soffid.iam";
		else
			return "es.caib.seycon.ng";
	}

	public String getModelPackage(int scope) {
		if (basePackage != null)
		{
			return basePackage+".model";
		}
		else if (isPlugin())
		{
			return "com.soffid.iam.addons."+getPluginName()+".model";
		}
		else
		{
			return getSharedModelPackage(scope);
		}
	}

	public String getSharedModelPackage(int scope) {
		if (basePackage != null)
		{
			return basePackage+".model";
		}
		else if (scope == Translate.SERVICE_SCOPE &&
				isTranslatedOnly() ||
			scope == Translate.ALTSERVICE_SCOPE &&
				! isTranslatedOnly() ||
			scope == Translate.ENTITY_SCOPE &&
				isTranslateEntities() ||
			scope == Translate.TRANSLATE)
		{
			return "com.soffid.iam.model";
		}
		else
		{
			return "es.caib.seycon.ng.model";
		}
	}

	public String getModelDir() {
		if (basePackage != null)
		{
			return Util.packageToDir(basePackage)+"model";
		}
		else if (isPlugin())
		{
			return  "com/soffid/iam/addons/"+getPluginName()+"/model";
		}
		else
		{
			if (isTranslateEntities() || isTranslatedOnly())
			{
				return  "com/soffid/iam/model";
			}
			else
			{
				return  "es/caib/seycon/ng/model";
			}
		}
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setDefaultException(String defaultException) {
		this.defaultException  = defaultException;
		
	}

	public String getAsyncCollectionClass() {
		return asyncCollectionClass;
	}

	public void setAsyncCollectionClass(String asyncCollectionClass) {
		this.asyncCollectionClass = asyncCollectionClass;
	}

	public boolean isManualSecurityCheck() {
		return manualSecurityCheck;
	}

	public void setManualSecurityCheck(boolean manualSecurityCheck) {
		this.manualSecurityCheck = manualSecurityCheck;
	}

	public String getPagedCollectionClass() {
		return pagedCollectionClass;
	}

	public void setPagedCollectionClass(String pagedCollectionClass) {
		this.pagedCollectionClass = pagedCollectionClass;
	}

}
