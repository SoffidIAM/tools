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
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:maven="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.soffid.tools</groupId>
  <artifactId>maven-par-plugin</artifactId>
  <packaging>maven-plugin</packaging>
  <name>Maven Par Plugin</name>
  <version>1.0.1</version>
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
  <build>
    <plugins>
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
  <reporting>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-javadoc-plugin</artifactId>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jxr-plugin</artifactId>
      </plugin>
    </plugins>
  </reporting>
  <licenses>
    <license>
      <name>GNU GPL Version 3.0</name>
      <url>http://www.gnu.org/licenses/gpl.html</url>
    </license>
  </licenses>
  <pluginRepositories>
    <pluginRepository>
      <id>soffid-open</id>
      <url>http://www.soffid.com/maven</url>
    </pluginRepository>
  </pluginRepositories>
  <repositories>
    <repository>
      <id>central</id>
      <url>http://repo1.maven.org/maven2</url>
    </repository>
    <repository>
      <id>soffid-open</id>
      <url>http://www.soffid.com/maven</url>
    </repository>
  </repositories>
  <distributionManagement>
    <repository>
      <uniqueVersion>true</uniqueVersion>
      <id>soffid-open</id>
      <name>Soffid Maven repository</name>
      <url>${soffid.deploy.url}</url>
    </repository>
    <site>
      <id>soffid-open</id>
      <name>Soffid Maven site repository</name>
      <url>${soffid.deploy.site.url}</url>
    </site>
  </distributionManagement>
</project>
