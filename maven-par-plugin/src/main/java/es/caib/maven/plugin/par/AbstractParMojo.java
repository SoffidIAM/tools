package es.caib.maven.plugin.par;

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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;

/**
 * Base class for creating a par from project classes.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: AbstractParMojo.java,v 1.2 2008-05-20 12:23:03 u07286 Exp $
 */
public abstract class AbstractParMojo
    extends AbstractMojo
{

    private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html", "**/CVS", "**/.svn" };

    private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

    /**
     * List of files to include. Specified as fileset patterns.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * List of files to exclude. Specified as fileset patterns.
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * Directory containing the generated PAR.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Name of the generated PAR.
     *
     * @parameter alias="parName" expression="${par.finalName}" default-value="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * The Jar archiver.
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     */
    private JarArchiver jarArchiver;

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

    /**
     * Path to the default MANIFEST file to use. It will be used if
     * <code>useDefaultManifestFile</code> is set to <code>true</code>.
     *
     * @parameter expression="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
     * @required
     * @readonly
     * @since 2.2
     */
    private File defaultManifestFile;

    /**
     * Set this to <code>true</code> to enable the use of the <code>defaultManifestFile</code>.
     *
     * @parameter expression="${par.useDefaultManifestFile}" default-value="false"
     *
     * @since 2.2
     */
    private boolean useDefaultManifestFile;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Whether creating the archive should be forced.
     *
     * @parameter expression="${par.forceCreation}" default-value="false"
     */
    private boolean forceCreation;

    /**
     * Return the specific output directory to serve as the root for the archive.
     */
    protected abstract File getClassesDirectory();
    protected abstract File getProcessDirectory();

    protected final MavenProject getProject()
    {
        return project;
    }

    /**
     * Overload this to produce a par with another classifier, for example a test-par.
     */
    protected abstract String getClassifier();

    /**
     * Overload this to produce a test-par, for example.
     */
    protected abstract String getType();

    protected static File getParFile( File basedir, String finalName, String classifier )
    {
        if ( classifier == null )
        {
            classifier = "";
        }
        else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) )
        {
            classifier = "-" + classifier;
        }

        return new File( basedir, finalName + classifier + ".par" );
    }

    /**
     * Default Manifest location. Can point to a non existing file.
     * Cannot return null.
     */
    protected File getDefaultManifestFile()
    {
        return defaultManifestFile;
    }


    /**
     * Generates the PAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public File createArchive()
        throws MojoExecutionException
    {
    	
        File parFile = getParFile( outputDirectory, finalName, getClassifier() );
    	getLog().info("Creating par file "+ parFile.getPath());

        MavenArchiver archiver = new MavenArchiver();

        archiver.setArchiver( jarArchiver );

        archiver.setOutputFile( parFile );

        archive.setForced( forceCreation );

        try
        {
            File contentDirectory = getProcessDirectory();
            if ( !contentDirectory.exists() )
            {
                getLog().warn( "PAR will be empty - no content was marked for inclusion!" );
            }
            else
            {
            	
                archiver.getArchiver().addDirectory( contentDirectory );
            }

            archiver.getArchiver().addDirectory( getClassesDirectory(), "classes/", getIncludes(), getExcludes() );

            File existingManifest = getDefaultManifestFile();

            if ( useDefaultManifestFile && existingManifest.exists() && archive.getManifestFile() == null )
            {
                getLog().info( "Adding existing MANIFEST to archive. Found under: " + existingManifest.getPath() );
                archive.setManifestFile( existingManifest );
            }

            archiver.createArchive( project, archive );

            return parFile;
        }
        catch ( Exception e )
        {
            // TODO: improve error handling
            throw new MojoExecutionException( "Error assembling par", e );
        }
    }

    /**
     * Generates the PAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public void execute()
        throws MojoExecutionException
    {
        File jarFile = createArchive();

        String classifier = getClassifier();
        if ( classifier != null )
        {
            projectHelper.attachArtifact( getProject(), getType(), classifier, jarFile );
        }
        else
        {
            getProject().getArtifact().setFile( jarFile );
        }
    }

    private String[] getIncludes()
    {
        if ( includes != null && includes.length > 0 )
        {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }

    private String[] getExcludes()
    {
        if ( excludes != null && excludes.length > 0 )
        {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }
}
