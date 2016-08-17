/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.project.templates.internal.archetype;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;
import org.apache.maven.archetype.ArchetypeManager;
import org.apache.maven.archetype.DefaultArchetypeManager;
import org.apache.maven.archetype.common.ArchetypeArtifactManager;
import org.apache.maven.archetype.common.DefaultArchetypeArtifactManager;
import org.apache.maven.archetype.common.DefaultArchetypeFilesResolver;
import org.apache.maven.archetype.exception.UnknownArchetype;
import org.apache.maven.archetype.generator.ArchetypeGenerator;
import org.apache.maven.archetype.generator.DefaultArchetypeGenerator;
import org.apache.maven.archetype.generator.DefaultFilesetArchetypeGenerator;
import org.apache.maven.archetype.generator.FilesetArchetypeGenerator;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.velocity.DefaultVelocityComponent;

/**
 * @author Gregory Amerson
 */
public class Archetyper {

    private Field logger = null;
    private ArchetypeArtifactManager archetypeArtifactManager;
    private final ConsoleLogger consoleLogger = new ConsoleLogger(0, "console");

    private Field getLogger() throws Exception {
        if (logger == null) {
            logger = AbstractLogEnabled.class.getDeclaredField( "logger" );
            logger.setAccessible( true );
        }
        return logger;
    }

    public static void main( String[] args ) throws Exception {
    	ArchetypeGenerationResult result = new Archetyper().generateProject("mvcportlet", "foo", "com.liferay.foo", "Bar", "build");

        System.out.println(result.getCause());
    }

    public ArchetypeGenerationResult generateProject(String templateName, String artifactId, String packageName, String className, String outputDirectory) throws Exception {
    	ArchetypeGenerationRequest archetypeGenerationRequest = new ArchetypeGenerationRequest();

        archetypeGenerationRequest.setArchetypeArtifactId("com.liferay.project.templates." + templateName);

        archetypeGenerationRequest.setArchetypeGroupId("com.liferay");

        archetypeGenerationRequest.setArchetypeVersion("0"); // archetypeVersion ignored

        archetypeGenerationRequest.setArtifactId(artifactId);

        archetypeGenerationRequest.setGroupId(packageName);

        archetypeGenerationRequest.setVersion("1.0.0");

        archetypeGenerationRequest.setInteractiveMode(false);

        archetypeGenerationRequest.setPackage(packageName);

        archetypeGenerationRequest.setOutputDirectory(outputDirectory);

        Properties additionalProperties = new Properties();

        additionalProperties.put("package", packageName);

        additionalProperties.put("className", className);

        archetypeGenerationRequest.setProperties(additionalProperties);

        ArchetypeGenerationResult result = getArchetypeManager().generateProjectFromArchetype(archetypeGenerationRequest);

        return result;
    }

    private ArchetypeManager getArchetypeManager() throws Exception {
        DefaultArchetypeManager archetypeManager = new DefaultArchetypeManager();

        getLogger().set(archetypeManager, consoleLogger);

        ArchetypeGenerator archetypeGenerator = getArchetypeGenerator();

        Field generator = DefaultArchetypeManager.class.getDeclaredField("generator");

        generator.setAccessible(true);

        generator.set(archetypeManager, archetypeGenerator);

        return archetypeManager;
    }

    private ArchetypeGenerator getArchetypeGenerator() throws Exception {
        ArchetypeGenerator archetypeGenerator = new DefaultArchetypeGenerator();

        Field archetypeArtifactManagerField = DefaultArchetypeGenerator.class.getDeclaredField("archetypeArtifactManager");

        archetypeArtifactManagerField.setAccessible(true);

        ArchetypeArtifactManager archetypeArtifactManager = getArchetypeArtifactManager();

        archetypeArtifactManagerField.set(archetypeGenerator, archetypeArtifactManager);

        Field filesetGeneratorField = DefaultArchetypeGenerator.class.getDeclaredField("filesetGenerator" );

        filesetGeneratorField.setAccessible(true);

        FilesetArchetypeGenerator filesetGenerator = getFilesetArchetypeGenerator();

        filesetGeneratorField.set(archetypeGenerator, filesetGenerator);

        return archetypeGenerator;
    }

    private FilesetArchetypeGenerator getFilesetArchetypeGenerator() throws Exception
    {
        FilesetArchetypeGenerator filesetArchetypeGenerator = new DefaultFilesetArchetypeGenerator();

        Field archetypeArtifactManagerField = DefaultFilesetArchetypeGenerator.class.getDeclaredField("archetypeArtifactManager");

        archetypeArtifactManagerField.setAccessible(true);

        archetypeArtifactManagerField.set(filesetArchetypeGenerator, getArchetypeArtifactManager());

        getLogger().set(filesetArchetypeGenerator, consoleLogger);

        DefaultArchetypeFilesResolver defaultArchetypeFilesResolver = new DefaultArchetypeFilesResolver();

        Field archetypeFilesResolver = DefaultFilesetArchetypeGenerator.class.getDeclaredField("archetypeFilesResolver");

        archetypeFilesResolver.setAccessible(true);

        archetypeFilesResolver.set(filesetArchetypeGenerator, defaultArchetypeFilesResolver);

        Field velocity = DefaultFilesetArchetypeGenerator.class.getDeclaredField("velocity");

        velocity.setAccessible(true);

        DefaultVelocityComponent velocityComponent = new DefaultVelocityComponent();

        getLogger().set(velocityComponent, consoleLogger);

        Field properties = DefaultVelocityComponent.class.getDeclaredField("properties");

        properties.setAccessible(true);

        Properties velocityProps = new Properties();

        velocityProps.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");

        velocityProps.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        properties.set(velocityComponent, velocityProps);

        velocity.set(filesetArchetypeGenerator, velocityComponent);

        velocityComponent.initialize();

        return filesetArchetypeGenerator;
    }

    private ArchetypeArtifactManager getArchetypeArtifactManager() throws Exception {
        if (archetypeArtifactManager == null) {
            archetypeArtifactManager = new DefaultArchetypeArtifactManager() {
            	@Override
                public boolean exists(
                    String archetypeGroupId, String archetypeArtifactId, String archetypeVersion,
                    ArtifactRepository archetypeRepository, ArtifactRepository localRepository,
                    List<ArtifactRepository> remoteRepositories ) {

                    return true;
                }

                @Override
                public File getArchetypeFile(
                    String groupId, String artifactId, String version, ArtifactRepository archetypeRepository,
                    ArtifactRepository localRepository, List<ArtifactRepository> repositories ) throws UnknownArchetype {

                    return new File("/Users/greg/dev/repos/liferay/liferay-portal/tools/sdk/dist/" + artifactId + "-1.0.0.jar");
                }
            };

            getLogger().set(archetypeArtifactManager, consoleLogger);
        }

        return archetypeArtifactManager;
    }


}
