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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import com.soffid.mda.generator.Generator;
import com.soffid.mda.parser.Parser;

/**
 * Base source code from MDZIP file.
 * 
 * @author <a href="gbuades@soffid.com">Gabriel Buades</a>
 * @goal mda2
 * @phase package
 * @requiresProject
 * @requiresDependencyResolution runtime
 */
public class Mda2Mojo extends AbstractMojo {
	/**
	 * Generate Documentation
	 * 
	 * @parameter
	 */
	private boolean generateDoc = false;
	/**
	 * Generate sync server remote service locator
	 * 
	 * @parameter
	 */
	private boolean generateSync = true;
	/**
	 * Generate EJB stub
	 * 
	 * @parameter
	 */
	private boolean generateEjb = true;
	/**
	 * Use translated versions
	 * 
	 * @parameter
	 */
	private boolean translate = true;
	/**
	 * Check HQL Parameters
	 * 
	 * @parameter
	 */
	private boolean hqlFullTest = true;
	/**
	 * Use translated versions for Entities
	 * 
	 * @parameter
	 */
	private boolean translateEntities = true;
	/**
	 * Generate deprecated (translated) methods
	 * 
	 * @parameter
	 */
	private boolean generateDeprecated = false;
	/**
	 * Generate default internal exception error
	 * 
	 * @parameter
	 */
	private String defaultException = "es.caib.seycon.ng.exception.InternalErrorException";

	/**
	 * Directory where sync server java files are to be generated
	 * 
	 * @parameter
	 */
	private String syncDir;

	/**
	 * Directory where sync server resources to be generated
	 * 
	 * @parameter
	 */
	private String syncResourcesDir;


	/**
	 * Directory where core java files will be generated
	 * 
	 * @parameter
	 */
	private String coredir;

	/**
	 * Directory where core java source files will be generated
	 * 
	 * @parameter
	 */
	private String coreSrcDir;


	/**
	 * Directory where core resource files will be generated
	 * 
	 * @parameter
	 */
	private String coreResourceDir;


	/**
	 * Directory where core java files will be generated
	 * 
	 * @parameter
	 */
	private String coreTestDir;

	/**
	 * Directory where core test resources will be generated
	 * 
	 * @parameter
	 */
	private String coreTestResourcesDir;

	/**
	 * Directory where xmi files will be generated
	 * 
	 * @parameter
	 */
	private String xmiDir;

	/**
	 * Directory where common java files will be generated
	 * 
	 * @parameter
	 */
	private String commonsDir;

	/**
	 * Base package name for classes
	 * 
	 * @parameter
	 */
	private String basePackage;

	/**
	 * Directory where documentation files will be generated
	 * 
	 * @parameter
	 */
	private String docDir;

	/**
	 * Generate plugin files
	 * 
	 * @parameter
	 */
	private String pluginName = null;

	/**
	 * Target application server
	 * 
	 * @parameter
	 */
	private String targetServer = null;


	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

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
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
     * @required
     * @readonly
     */
    private ZipArchiver zipArchiver;
   
	/**
	 * Generates the code.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		try 
		{
			Parser p = new Parser ();
			p.setTranslateOnly (translate);
			p.setDefaultException(defaultException);
			p.setTranslateEntities(translateEntities);
			File classesDir = new File(project.getBuild().getOutputDirectory());
			getLog().info("Scanning "+ classesDir.getPath());
			List<URL> urls = new LinkedList<URL>();
			urls.add(classesDir.toURI().toURL());
			for (Object object : project.getCompileClasspathElements()) {
			      String path = (String) object;
			      urls.add(new File(path).toURI().toURL());
			    }
			ClassLoader cl = new URLClassLoader(
					urls.toArray(new URL[0]),
					Thread.currentThread().getContextClassLoader());
			
			Thread.currentThread().setContextClassLoader(cl);
			Generator g = new Generator ();
			g.setTranslatedOnly(translate);
			g.setTranslateEntities(translateEntities);
			try 
			{
				p.parse(classesDir);
				getLog().info("Generating into "+project.getBuild().getDirectory());
				g.configure(new File  (project.getBuild().getDirectory()));
				if (commonsDir != null)
					g.setCommonsDir(commonsDir);
				if (coredir != null)
					g.setCoreDir(coredir);
				if (coreResourceDir != null)
					g.setCoreResourcesDir(coreResourceDir);
				if (coreSrcDir != null)
					g.setCoreSrcDir(coreSrcDir);
				if (coreTestResourcesDir != null)
					g.setCoreTestResourcesDir(coreTestResourcesDir);
				if (coreTestDir != null)
					g.setCoreTestSrcDir(coreTestDir);
				if (pluginName != null)
					g.setPluginName(pluginName);
				if (syncDir != null)
					g.setSyncDir(syncDir);
				if (syncResourcesDir != null)
					g.setSyncResourcesDir(syncResourcesDir);
				if (xmiDir != null)
					g.setXmlModule(xmiDir);
				if (docDir != null)
					g.setUmlDir(docDir);
				if (targetServer != null)
					g.setTargetServer(targetServer);
				g.setDefaultException(defaultException);
				g.setGenerateUml(generateDoc);
				g.setTranslatedOnly(translate);
				g.setTranslateEntities(translateEntities);
				g.setGenerateDeprecated(generateDeprecated);
				g.setHqlFullTest(hqlFullTest);
				g.setGenerateEjb(generateEjb);
				g.setGenerateSync(generateSync);
				g.setBasePackage (basePackage);
				g.generate(p);
			} finally {
				Thread.currentThread().setContextClassLoader(cl.getParent());
			}
			attachArtifact(g.getCommonsDir(), "common");
			attachArtifact(g.getCoreDir(), "core");
			attachArtifact(g.getCoreResourcesDir(), "core-resource");
			attachArtifact(g.getCoreSrcDir(), "core-src");
			attachArtifact(g.getCoreTestSrcDir(), "test");
			attachArtifact(g.getCoreTestResourcesDir(), "test-resource");
			attachArtifact(g.getSyncDir(), "syncserver");
			attachArtifact(g.getSyncResourcesDir(), "syncserver-resource");
		} catch (Throwable e) {
			throw new MojoExecutionException("Cannot generate source code: "+e.toString(), e);
		}
	}

	private void attachArtifact(String directory, String classifier) throws ArchiverException, IOException {
		File dirFile = new File(directory);
		getLog().info("Attaching "+dirFile.getPath());
		if (dirFile.isDirectory())
		{
			File destFile = new File (project.getBuild().getOutputDirectory()+File.separator+project.getArtifactId()+"-"+classifier+".zip");
			zipArchiver.reset ();
			zipArchiver.addDirectory(dirFile);
			zipArchiver.setDestFile(destFile);
			zipArchiver.createArchive();
			
			Artifact artifact = artifactFactory.createArtifactWithClassifier(
					project.getGroupId(), project.getArtifactId(), project.getVersion(), 
					"zip", classifier);
			artifact.setFile(destFile);
			artifact.setRelease(true);
			project.addAttachedArtifact(artifact);
			
		}
	}

}
