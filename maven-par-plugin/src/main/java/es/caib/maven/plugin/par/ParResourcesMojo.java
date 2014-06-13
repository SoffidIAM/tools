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

import java.io.File;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.resources.ResourcesMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Build a PAR from the current project.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: ParResourcesMojo.java,v 1.1 2008-05-20 12:23:03 u07286 Exp $
 * @goal resources
 * @phase process-resources
 * @requiresProject
 * @requiresDependencyResolution runtime
 */
public class ParResourcesMojo
    extends ResourcesMojo
{
    /**
     * Generates the PAR.
     *
     * @todo Add license files in META-INF directory.
     */
    public void execute()
        throws MojoExecutionException
    {
    	System.out.println ("Executing prepare");
    	super.execute ();
    }

}
