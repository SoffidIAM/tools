<?xml version="1.0"?>
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.soffid.tools</groupId>
  <artifactId>maven-sar-plugin</artifactId>
  <packaging>maven-plugin</packaging>
  <name>Maven SAR Plugin</name>
  <version>1.0</version>
	
  <prerequisites>
    <maven>2.0.6</maven>
  </prerequisites>
  
  <dependencies>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-plugin-api</artifactId>
      <version>2.0.6</version>
    </dependency>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-project</artifactId>
      <version>2.0.7</version>
    </dependency>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-archiver</artifactId>
      <version>2.3</version>
    </dependency>
    <dependency>
      <groupId>org.codehaus.plexus</groupId>
      <artifactId>plexus-archiver</artifactId>
      <version>1.0-alpha-9</version>
      <exclusions>
        <exclusion>
          <groupId>org.codehaus.plexus</groupId>
          <artifactId>plexus-container-default</artifactId>
        </exclusion>
        <exclusion>
          <groupId>org.codehaus.plexus</groupId>
          <artifactId>plexus-component-api</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
    <dependency>
      <groupId>org.codehaus.plexus</groupId>
      <artifactId>plexus-io</artifactId>
      <version>1.0-alpha-1</version>
      <exclusions>
        <exclusion>
          <groupId>org.codehaus.plexus</groupId>
          <artifactId>plexus-container-default</artifactId>
        </exclusion>
        <exclusion>
          <groupId>org.codehaus.plexus</groupId>
          <artifactId>plexus-component-api</artifactId>
        </exclusion>
      </exclusions>
    </dependency>        
    <dependency>
      <groupId>commons-lang</groupId>
      <artifactId>commons-lang</artifactId>
      <version>2.1</version>
    </dependency>
    <dependency>
      <groupId>org.codehaus.plexus</groupId>
      <artifactId>plexus-utils</artifactId>
      <version>1.4.9</version>
    </dependency>
    <dependency>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-resources-plugin</artifactId>
      <version>2.2</version>
    </dependency>
    <dependency>
      <groupId>org.apache.maven.shared</groupId>
      <artifactId>maven-plugin-testing-harness</artifactId>
      <version>1.1</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <licenses xmlns:maven="http://maven.apache.org/POM/4.0.0"><license><name>GNU GPL Version 3.0</name><url>http://www.gnu.org/licenses/gpl.html</url></license></licenses><build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.3.1</version>  
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-source-plugin</artifactId>
        <version>2.0.4</version>
      </plugin>
      <plugin>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>1.0-alpha-3</version>
        <executions>
          <execution>
            <goals>
              <goal>enforce</goal>
            </goals>
            <id>ensure-no-container-api</id>
            <configuration>
              <rules>
                <bannedDependencies>
                  <excludes>
                    <exclude>org.codehaus.plexus:plexus-component-api</exclude>
                  </excludes>
                  <message>The new containers are not supported. You probably added a dependency that is missing the exclusions.</message>
                </bannedDependencies>
              </rules>
              <fail>true</fail>
            </configuration>
          </execution>
        </executions>
      </plugin>      

      <!-- Testing the result of the it pom.xml -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.3</version>
        <configuration>
          <excludes>
            <exclude>**/*IntegrationTest.java</exclude>
          </excludes>
        </configuration>
      </plugin>
    </plugins>
  </build>

  <properties>
  	<gforge.project>bpm</gforge.project>
  </properties><pluginRepositories xmlns:maven="http://maven.apache.org/POM/4.0.0"><pluginRepository><id>soffid-open</id><url>http://www.soffid.com/maven</url></pluginRepository></pluginRepositories><repositories xmlns:maven="http://maven.apache.org/POM/4.0.0"><repository><id>central</id><url>http://repo1.maven.org/maven2</url></repository><repository><id>soffid-open</id><url>http://www.soffid.com/maven</url></repository></repositories><distributionManagement xmlns:maven="http://maven.apache.org/POM/4.0.0"><repository><uniqueVersion>true</uniqueVersion><id>soffid-open</id><name>Soffid Maven repository</name><url>${soffid.deploy.url}</url></repository><site><id>soffid-open</id><name>Soffid Maven site repository</name><url>${soffid.deploy.site.url}</url></site></distributionManagement>

</project>
