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
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Base source code from MDZIP file.
 * 
 * @author <a href="gbuades@soffid.com">Gabriel Buades</a>
 * @goal mda
 * @phase generate-sources
 * @requiresProject
 * @requiresDependencyResolution runtime
 */
public class MdaMojo extends AbstractMojo {
	/**
	 * Generate sync files
	 * 
	 * @parameter
	 */
	private boolean generateMeta = false;


	/**
	 * Generate sync files
	 * 
	 * @parameter
	 */
	private boolean generateSync = false;

	/**
	 * Generate common files
	 * 
	 * @parameter
	 */
	private boolean generateCommon = false;

	/**
	 * Generate plugin files
	 * 
	 * @parameter
	 */
	private String pluginName = null;

	/**
	 * Generate core files
	 * 
	 * @parameter
	 */
	private boolean generateCore = false;

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
	 * Generates the code.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		LinkedList<String> params = new LinkedList<String>();
		
		if (!generateCommon && ! generateCore && ! generateSync)
		{
			getLog().warn("No target specifiy. Configure generateCommon or generateCore parameters");
			return;
		}
		
		try {
			String program = null;
			for (Artifact artifact: (List<Artifact>) pluginArtifacts )
			{
				if (artifact.getGroupId().equals ("com.soffid.tools")
						&& artifact.getArtifactId().equals("mdacompiler"))
				{
					resolver.resolve(artifact, remoteRepositories, localRepository);
					program = artifact.getFile().getPath();
				}
			}
			
			if (program == null)
				throw new MojoExecutionException("Cannot find dependency to com.soffid.tools:mdacompiler");
			
			
			System.out.println ("MDACOMPILER: "+ program);
			params.add (program);
			
			if (generateSync)
			{
				params.add ("-sync");
				params.add (project.getBasedir().getPath());
			}

			if (generateCommon)
			{
				params.add ("-common");
				params.add (project.getBasedir().getPath());
			}

			
			if (generateCore)
			{
				params.add ("-core");
				params.add (project.getBasedir().getPath());
			}
			
			if (generateMeta)
			{
				params.add ("-meta");
			}
			
			if (pluginName != null && !pluginName.isEmpty())
			{
				params.add ("-plugin");
				params.add (pluginName);
			}

			HashSet<Artifact> models = new HashSet<Artifact>();
			
			ArtifactResolutionResult arr = resolver.resolveTransitively(
					project.getDependencyArtifacts(), project.getArtifact(),
					localRepository, remoteRepositories, source, null);

			Set<Artifact> artifacts = new HashSet<Artifact>();
			for (Artifact child : (Set<Artifact>)project.getDependencyArtifacts()) {
				if (child.getType().equals("mdzip") && !models.contains(child))
				{
					models.add(child);
					params.add (child.getFile().getPath());
				}
			}

			params.add ("-modules");
			
			
			for (Artifact child : (Set<Artifact>) arr.getArtifacts()) {
				if (child.getType().equals("mdzip") && !models.contains(child))
				{
					models.add(child);
					params.add (child.getFile().getPath());
				}
			}
			
			Runtime.getRuntime().exec(new String[] {"chmod", "a+x", program}).waitFor();
			getLog().info("Executing "+params.toString());
			Process proc = Runtime.getRuntime().exec(params.toArray(new String[0]));
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			while ( (line = in.readLine()) != null)
			{
				getLog().info(line);
			}
			in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			while ( (line = in.readLine()) != null)
			{
				getLog().warn(line);
			}
			int result = proc.waitFor();
			if (result != 0)
				throw new MojoExecutionException("Error reported from mdacompiler: " + result);
		} catch (ArtifactResolutionException e) {
			throw new MojoExecutionException("Cannot get required artifact", e);
		} catch (ArtifactNotFoundException e) {
			throw new MojoExecutionException("Cannot get required artifact", e);
		} catch (InterruptedException e) {
			throw new MojoExecutionException("Process interrupted", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot generate source code", e);
		}
	}

}
