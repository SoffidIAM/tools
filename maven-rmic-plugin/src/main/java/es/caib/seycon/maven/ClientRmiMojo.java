package es.caib.seycon.maven;



/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Build an EJB (and optional client) from the current project.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: ClientRmiMojo.java,v 1.1 2008-01-16 12:52:37 u07286 Exp $
 * @goal client-jar
 * @phase package
 */
public class ClientRmiMojo
    extends AbstractMojo
{
    // TODO: will null work instead?
    private static final String[] DEFAULT_INCLUDES = new String[]{"**/Stub.class", "**/Skel.class"};

    private static final String[] DEFAULT_EXCLUDES =
        new String[]{};

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * The directory for the generated EJB.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     * @todo use File instead
     */
    private String basedir;

    /**
     * Directory that resources are copied to during the build.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private String outputDirectory;

    /**
     * The name of the EJB file to generate.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String jarName;

    /**
     * Classifier to add to the artifact generated. If given, the artifact will
     * be an attachment instead.
     *
     * @parameter
     * @required
     */
    private String classifier;

    /**
     * Whether the client jar should be generated or not. Default
     * is false.
     *
     * @parameter
     * @todo boolean instead
     */
    private String generateClient = Boolean.TRUE.toString();

    /**
     * Excludes.
     *
     * <br/>Usage:
     * <pre>
     * &lt;clientIncludes&gt;
     *   &lt;clientInclude&gt;**&#47;*Ejb.class&lt;&#47;clientInclude&gt;
     *   &lt;clientInclude&gt;**&#47;*Bean.class&lt;&#47;clientInclude&gt;
     * &lt;&#47;clientIncludes&gt;
     * </pre>
     * <br/>Attribute is used only if client jar is generated.
     * <br/>Default exclusions: **&#47;*Bean.class, **&#47;*CMP.class, **&#47;*Session.class, **&#47;package.html
     *
     * @parameter
     */
    private List clientExcludes;

    /**
     * Includes.
     *
     * <br/>Usage:
     * <pre>
     * &lt;clientIncludes&gt;
     *   &lt;clientInclude&gt;**&#47;*&lt;&#47;clientInclude&gt;
     * &lt;&#47;clientIncludes&gt;
     * </pre>
     * <br/>Attribute is used only if client jar is generated.
     * <br/>Default value: **&#47;**
     *
     * @parameter
     */
    private List clientIncludes;

    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The client Jar archiver.
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     */
    private JarArchiver clientJarArchiver;

    /**
     * The maven project's helper.
     *
     * @parameter expression="${component.org.apache.maven.project.MavenProjectHelper}"
     * @required
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    /**
     * The maven archiver to use.
     *
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Generates an ejb jar and optionnaly an ejb-client jar.
     *
     * @todo Add license files in META-INF directory.
     */
    public void execute()
        throws MojoExecutionException
    {


            String[] excludes = DEFAULT_EXCLUDES;
            String[] includes = DEFAULT_INCLUDES;

            if ( clientIncludes != null && !clientIncludes.isEmpty() )
            {
                includes = (String[]) clientIncludes.toArray( EMPTY_STRING_ARRAY );
            }

            if ( clientExcludes != null && !clientExcludes.isEmpty() )
            {
                excludes = (String[]) clientExcludes.toArray( EMPTY_STRING_ARRAY );
            }

            File clientJarFile = getJarFile(basedir, jarName, classifier);

            if ( getLog().isInfoEnabled() )
            {
                getLog().info( "Building jar " + clientJarFile.getName() );
            }

            MavenArchiver clientArchiver = new MavenArchiver();

            clientArchiver.setArchiver( clientJarArchiver );

            clientArchiver.setOutputFile( clientJarFile );

            try
            {
                clientArchiver.getArchiver().addDirectory( new File( outputDirectory ), includes, excludes );

                // create archive
                clientArchiver.createArchive( project, archive );

            }
            catch ( ArchiverException e )
            {
                throw new MojoExecutionException(
                    "There was a problem creating the archive: " + e.getMessage(), e );
            }
            catch ( ManifestException e )
            {
                throw new MojoExecutionException(
                    "There was a problem creating the archive: " + e.getMessage(), e );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException(
                    "There was a problem creating the archive: " + e.getMessage(), e );
            }
            catch ( DependencyResolutionRequiredException e )
            {
                throw new MojoExecutionException(
                    "There was a problem creating the archive: " + e.getMessage(), e );
            }

            // TODO: shouldn't need classifer
            projectHelper.attachArtifact( project, "jar", classifier, clientJarFile );
    }

    /**
     * Returns the EJB Jar file to generate, based on an optional classifier.
     *
     * @param basedir    the output directory
     * @param finalName  the name of the ear file
     * @param classifier an optional classifier
     * @return the EJB file to generate
     */
    private static File getJarFile( String basedir, String finalName, String classifier )
    {
        if ( classifier == null )
        {
            classifier = "";
        }
        else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) )
        {
            classifier = "-" + classifier;
        }

        return new File( basedir, finalName + classifier + ".jar" );
    }

}
