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
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY@parameter
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
 * @goal sql
 * @phase generate-sources
 * @requiresProject
 * @requiresDependencyResolution runtime
 */
public class SqlMojo extends AbstractMojo {

	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}" description="Internal project" name="project"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The archive configuration to use.
	 * 
	 * @parameter description="Maven archiver" name="archive"
	 */
	private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

	/** @component */
	private org.apache.maven.artifact.factory.ArtifactFactory artifactFactory;

	/** @component */
	private org.apache.maven.artifact.resolver.ArtifactResolver resolver;

	/**
	 * Local repository
	 *  
	 * @parameter default-value="${localRepository}" description="local repository" */
	private org.apache.maven.artifact.repository.ArtifactRepository localRepository;

	/**
	 * Remote repository
	 *  
	 * @parameter default-value="${project.remoteArtifactRepositories}" description="remote repositories"*/
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
	 * Artifacts
	 * 
	 * @parameter default-value="${plugin.artifacts}" description="plugin artifacts"
	 */
	private java.util.List pluginArtifacts;

	/**
	 * Generates the code.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void execute() throws MojoExecutionException {
		LinkedList<String> params = new LinkedList<String>();
		
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
			
			params.add ("-sql");
			params.add (project.getBasedir().getPath());


			HashSet<Artifact> models = new HashSet<Artifact>();
			
			ArtifactResolutionResult arr = resolver.resolveTransitively(
					project.getDependencyArtifacts(), project.getArtifact(),
					localRepository, remoteRepositories, source, null);

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
