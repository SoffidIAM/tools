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
	String jascutDir;
	String docDir;
	boolean generateUml = false;
	
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
		parser.setTranslateOnly (this.isTranslatedOnly());
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
}
