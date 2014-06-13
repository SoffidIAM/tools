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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.soffid.maven.plugin.mda.MdaMojo;


/**
 * Test for {@link ParMojo}
 *
 * @version $Id: SarMojoTest.java,v 1.1 2008-05-20 12:22:51 u07286 Exp $
 */
public class MdaMojoIntegrationTest
    extends AbstractMojoTestCase
{

    /**
     * tests the proper discovery and configuration of the mojo
     *
     * @throws Exception
     */
    public void testMdaExecution()
        throws Exception
    {
    	Process process = Runtime.getRuntime().exec(new String[] { "mvn", "-f",  "src/test/resources/unit/mda-full-test/pom.xml", "install"});
		String line;
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while ( (line = in.readLine()) != null)
		{
			System.out.println(line);
		}
		in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while ( (line = in.readLine()) != null)
		{
			System.out.println(line);
		}
    	assertEquals(0, process.waitFor());
    }
}
