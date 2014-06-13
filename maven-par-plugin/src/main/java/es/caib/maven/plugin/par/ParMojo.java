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

/**
 * Build a PAR from the current project.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: ParMojo.java,v 1.1 2008-01-31 12:01:29 u07286 Exp $
 * @goal par
 * @phase package
 * @requiresProject
 * @requiresDependencyResolution runtime
 */
public class ParMojo
    extends AbstractParMojo
{
    /**
     * Directory containing the classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    /**
     * Directory containing the classes.
     *
     * @parameter expression="${project.basedir/target/jbpm}"
     * @required
     */
    private File jpdlDirectory;
    /**
     * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
     *
     * @parameter
     */
    private String classifier;

    protected String getClassifier()
    {
        return classifier;
    }

    /**
     * @return type of the generated artifact
     */
    protected String getType()
    {
        return "par";
    }

    /**
     * Return the main classes directory, so it's used as the root of the par.
     */
    protected File getClassesDirectory()
    {
        return classesDirectory;
    }

	protected File getProcessDirectory() {
		// TODO Auto-generated method stub
		return jpdlDirectory;
	}
}
