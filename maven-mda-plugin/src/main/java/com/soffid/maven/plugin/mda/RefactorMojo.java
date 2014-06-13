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

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.filter.TypeArtifactFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import com.soffid.mda.generator.Generator;
import com.soffid.mda.parser.Parser;

import jascut.Engine;
import jascut.xml.RuleList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.print.attribute.standard.MediaSize.Engineering;

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
	 * @parameter default-value="${project.basedir/target/translated}" 
	 */
	private String targetDir;

	/**
	 * jascut xml file
	 * 
	 * @parameter default-value="${project.basedir}/src/jascut.xml" expression="${jascutFile}"
	 */
	private String jascutFile ;

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

			
			RuleList rl = RuleList.read(jascutFile);
			e.perform(rl);
		} catch (Throwable e) {
			throw new MojoExecutionException("Cannot generate source code: "+e.toString(), e);
		}
	}

}
