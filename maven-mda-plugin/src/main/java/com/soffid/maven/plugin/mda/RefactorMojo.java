package com.soffid.maven.plugin.mda;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import jascut.Engine;
import jascut.xml.RenameTypeXml;
import jascut.xml.Rule;
import jascut.xml.RuleList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.xml.sax.SAXException;

/**
 * Refactor objects
 * 
 * @author <a href="gbuades@soffid.com">Gabriel Buades</a>
 * @goal refactor
 * @requiresProject
 * @requiresDependencyResolution compile
 * @execute phase="generate-sources"
 */
public class RefactorMojo extends AbstractMojo {
	/**
	 * Directory target files will be generated
	 * 
	 * @parameter default-value="${project.basedir/target/translated}" expression="${targetDir}"
	 */
	private String targetDir;

	/**
	 * jascut xml file
	 * 
	 * @parameter expression="${jascutFile}" 
	 */
	private String jascutFile ;

	/**
	 * jascut xml file
	 * 
	 * @parameter expression="${jascutFiles}"
	 */
	private String jascutFiles ;

	
	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The sub project.
	 * 
	 * @parameter expression="${executedProject}"
	 * @required
	 * @readonly
	 */
	private MavenProject executedProject;

	/**
	 * The archive configuration to use.
	 * 
	 * See <a
	 * href="http://maven.apache.org/shared/maven-archiver/index.html">the
	 * documentation for Maven Archiver</a>.
	 * 
	 * @parameter
	 */
	private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

	/** @component */
	private org.apache.maven.artifact.factory.ArtifactFactory artifactFactory;

	/** @component */
	private org.apache.maven.artifact.resolver.ArtifactResolver resolver;

	/** @parameter default-value="${localRepository}" */
	private org.apache.maven.artifact.repository.ArtifactRepository localRepository;

	/** @parameter default-value="${project.remoteArtifactRepositories}" */
	private java.util.List remoteRepositories;

	/** @component */
	private ArtifactMetadataSource source;

	/**
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	protected final MavenProject getProject() {
		return project;
	}

	/**
	 * @parameter default-value="${plugin.artifacts}"
	 */
	private java.util.List pluginArtifacts;
   
	/**
	 * Generates the code.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		try 
		{
			
			getProject().getBuildPlugins();
			
			getLog().info("Processing "+jascutFile);

			Properties props = new Properties ();
			
			StringBuffer b = new StringBuffer();
			for (Object object : project.getCompileClasspathElements()) {
				if (b.length() > 0)
				{
					b.append (File.pathSeparatorChar);
				}
				b.append (object.toString());
			}
			props.put("classpath", b.toString());
			b = new StringBuffer();
			for (Object src: executedProject.getCompileSourceRoots())
			{
				if (b.length() > 0)
					b.append (File.pathSeparatorChar);
				b.append (src.toString());
			}
			props.put("sourceDir", b.toString());
			getLog().info ("Source dir = "+b.toString());
			props.put("outputDir", targetDir);
			
			Engine e = new Engine(props);

			
			RuleList rl;
			if (jascutFile != null && jascutFile.trim().length() > 0 )
				rl = RuleList.read(jascutFile);
			else
			{
				rl = new RuleList();
				rl.setRules(new LinkedList<Rule>());
			}
			
			if (jascutFiles != null && jascutFiles.trim().length () > 0)
			{
				for (String jf: jascutFiles.split(File.pathSeparator))
				{
					RuleList rl2 = RuleList.read(jf);
					rl.getRules().addAll(rl2.getRules());
				}
			}

			e.perform(rl);
			
			doClassRenames (rl);
		} catch (Throwable e) {
			throw new MojoExecutionException("Cannot generate source code: "+e.toString(), e);
		}
	}

	private void doClassRenames(RuleList rl) throws SAXException, IOException, ParserConfigurationException {
		for (Rule rule: rl.getRules())
		{
			if (rule instanceof RenameTypeXml)
			{
				RenameTypeXml renameRule = (RenameTypeXml) rule;
				String typeOrig = renameRule.getTypeOrig();
				String typeNew = renameRule.getTypeNew();
				
				File sourceFile = classToFile (typeOrig);
				if (sourceFile.canRead())
				{
					System.out.println ("Renaming "+typeOrig+" to "+typeNew);
					StringBuffer t = new StringBuffer();
					Reader r = new FileReader(sourceFile);
					char buffer[] = new char[2048];
					do
					{
						int len  = r.read(buffer);
						if (len <= 0) break;
						t.append(buffer, 0, len);
					} while ( true );
					r.close ();
					String text = t.toString();
					String sourcePackage = getPackageName (typeOrig);
					String targetPackage = getPackageName (typeNew);
					String sourceName = getClassName(typeOrig);
					String targetName = getClassName(typeNew);
					String sourcePackagePattern = sourcePackage.replaceAll("\\.", "\\\\.");
					String targetePackagePattern = targetPackage.replaceAll("\\.", "\\\\.");
					text = text.replaceAll("package\\s+"+sourcePackagePattern+"\\s*;",
							"package "+targetePackagePattern+";\n\n"+
									"import "+sourcePackagePattern+".*;");
					text = text.replaceAll("\\b"+sourceName+"\\b", targetName);
	
					File targetFile = classToFile (typeNew);
					targetFile.getParentFile().mkdirs();
					FileWriter fw = new FileWriter(targetFile);
					fw.write(text);
					fw.close ();
					
					sourceFile.delete();
				}
			}
		}
	}

	private String getPackageName(String type) {
		int i = type.lastIndexOf('.');
		return type.substring(0, i);
	}

	private String getClassName(String type) {
		int i = type.lastIndexOf('.');
		return type.substring(i+1);
	}


	private File classToFile(String typeOrig) {
		return  new File(targetDir+ File.separator + typeOrig.replace('.', File.separatorChar) + ".java");
	}

}
