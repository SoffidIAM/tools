package es.caib.maven.plugin.sar;

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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import es.caib.maven.plugin.sar.SarMojo;

/**
 * Test for {@link ParMojo}
 *
 * @version $Id: SarMojoTest.java,v 1.1 2008-05-20 12:22:51 u07286 Exp $
 */
public class SarMojoTest
    extends AbstractMojoTestCase
{
    private File testPom = new File( getBasedir(), "src/test/resources/unit/sar-basic-test/pom.xml" );

    protected void setUp()
        throws Exception
    {

        // required for mojo lookups to work
        super.setUp();

    }

    /**
     * tests the proper discovery and configuration of the mojo
     *
     * @throws Exception
     */
    public void testParTestEnvironment()
        throws Exception
    {

        //File pom = new File( getBasedir(), "src/test/resources/unit/clean/pom.xml" );

        SarMojo mojo = (SarMojo) lookupMojo( "sar", testPom );

        assertNotNull( mojo );

        assertEquals( "foo", mojo.getProject().getGroupId() );
    }

}
