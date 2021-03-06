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
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Build a JAR of the test classes for the current project.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: TestParMojo.java,v 1.1 2008-01-31 12:01:29 u07286 Exp $
 * @goal test-jar
 * @phase package
 * @requiresProject
 * @requiresDependencyResolution test
 */
public class TestParMojo
    extends AbstractParMojo
{

    /**
     * Set this to <code>true</code> to bypass unit tests entirely.
     * Its use is <b>NOT RECOMMENDED</b>, but quite convenient on occasion.
     *
     * @parameter expression="${maven.test.skip}"
     */
    private boolean skip;

    /**
     * Directory containing the test classes.
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testClassesDirectory;

    /**
     * Directory containing the test classes.
     *
     * @parameter expression="${project.basedir/src/test/jpdl/simple}"
     * @required
     */
    private File testJPDLDirectory;


    protected String getClassifier()
    {
        return "tests";
    }

    /**
     * @return type of the generated artifact
     */
    protected String getType()
    {
        return "test-jar";
    }

    /**
     * Return the test-classes directory, to serve as the root of the tests jar.
     */
    protected File getClassesDirectory()
    {
        return testClassesDirectory;
    }

    /**
     * Return the test-classes directory, to serve as the root of the tests jar.
     */
    protected File getProcessDirectory()
    {
        return testJPDLDirectory;
    }
    
	public void execute()
        throws MojoExecutionException
    {
        if ( skip )
        {
            getLog().info( "Skipping packaging of the test-jar" );
        }
        else
        {
            super.execute();
        }
    }
}
