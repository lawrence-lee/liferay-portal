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

import aQute.lib.io.IO;

import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.ProjectTemplatesArgs;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.velocity.DefaultVelocityComponent;

/**
 * @author Gregory Amerson
 */
public class Archetyper {

	public Archetyper() {
		_logger = new AbstractLogger(0, "archetyper") {

			@Override
			public void debug(String message, Throwable throwable) {
			}

			@Override
			public void error(String message, Throwable throwable) {
			}

			@Override
			public void fatalError(String message, Throwable throwable) {
			}

			@Override
			public Logger getChildLogger(String name) {
				return this;
			}

			@Override
			public void info(String message, Throwable throwable) {
			}

			@Override
			public void warn(String message, Throwable throwable) {
			}

		};
	}

	public ArchetypeGenerationResult generateProject(
			ProjectTemplatesArgs projectTemplatesArgs, String outputDirectory)
		throws Exception {

		ArchetypeGenerationRequest archetypeGenerationRequest =
			new ArchetypeGenerationRequest();

		String artifactId = projectTemplatesArgs.getName();
		String className = projectTemplatesArgs.getClassName();
		String hostBundleSymbolicName = projectTemplatesArgs.getHostBundleSymbolicName();
		String hostBundleVersion = projectTemplatesArgs.getHostBundleVersion();
		String packageName = projectTemplatesArgs.getPackageName();
		String service = projectTemplatesArgs.getService();
		String templateName = projectTemplatesArgs.getTemplate();

		archetypeGenerationRequest.setArchetypeArtifactId(
			"com.liferay.project.templates." + templateName);
		archetypeGenerationRequest.setArchetypeGroupId("com.liferay");

		// archetypeVersion is ignored

		archetypeGenerationRequest.setArchetypeVersion("0");
		archetypeGenerationRequest.setArtifactId(artifactId);
		archetypeGenerationRequest.setGroupId(packageName);
		archetypeGenerationRequest.setInteractiveMode(false);
		archetypeGenerationRequest.setVersion("1.0.0");
		archetypeGenerationRequest.setOutputDirectory(outputDirectory);
		archetypeGenerationRequest.setPackage(packageName);

		Properties additionalProperties = new Properties();

		_safePut(additionalProperties, "className", className);
		_safePut(additionalProperties, "hostBundleSymbolicName", hostBundleSymbolicName);
		_safePut(additionalProperties, "hostBundleVersion", hostBundleVersion);
		_safePut(additionalProperties, "package", packageName);
		_safePut(additionalProperties, "serviceClass", service);

		archetypeGenerationRequest.setProperties(additionalProperties);

		ArchetypeGenerationResult result =
			getArchetypeManager().generateProjectFromArchetype(
				archetypeGenerationRequest);

		return result;
	}

	private static void _safePut(Properties properties, String name, String value) {
		if (value != null) {
			properties.put(name, value);
		}
	}

	private static File _getJarFile() throws Exception {
		ProtectionDomain protectionDomain =
			ProjectTemplates.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		return new File(url.toURI());
	}

	private ArchetypeArtifactManager getArchetypeArtifactManager()
		throws Exception {

		if (_archetypeArtifactManager == null) {
			_archetypeArtifactManager = new DefaultArchetypeArtifactManager() {

				@Override
				public ClassLoader getArchetypeJarLoader(File archetypeFile) throws UnknownArchetype {
					try
			        {
			            URL[] urls = new URL[1];

			            urls[0] = archetypeFile.toURI().toURL();

			            return new URLClassLoader( urls, null );
			        }
			        catch ( MalformedURLException e )
			        {
			            throw new UnknownArchetype( e );
			        }
				}

				@Override
				public boolean exists(
					String archetypeGroupId, String archetypeArtifactId,
					String archetypeVersion,
					ArtifactRepository archetypeRepository,
					ArtifactRepository localRepository,
					List<ArtifactRepository> remoteRepositories) {

					return true;
				}

				@Override
				public File getArchetypeFile(
					String groupId, String artifactId, String version,
					ArtifactRepository archetypeRepository,
					ArtifactRepository localRepository,
					List<ArtifactRepository> repositories) throws UnknownArchetype {

					File archetypeJarFile = null;

					try {
						File file = _getJarFile();

						if (file.isDirectory()) {
							Path jarDirPath = file.toPath();

							final Path archetypeJarPath = jarDirPath.resolve(
								artifactId + ".jar");

							archetypeJarFile = archetypeJarPath.toFile();
						}
						else {
							try (JarFile jarFile = new JarFile(file)) {
								Enumeration<JarEntry> enumeration =
									jarFile.entries();

								while (enumeration.hasMoreElements()) {
									JarEntry jarEntry =
										enumeration.nextElement();

									if (jarEntry.isDirectory()) {
										continue;
									}

									String name = jarEntry.getName();

									archetypeJarFile = Files.createTempFile(
										TEMP_ARCHETYPE_PREFIX, null).toFile();

									if (name.startsWith(artifactId)) {
										IO.copy(
											jarFile.getInputStream(jarEntry),
											archetypeJarFile);
										break;
									}
								}
							}
						}
					} catch (Exception e) {
					}

					return archetypeJarFile;
				}

			};

			getLoggerField().set(_archetypeArtifactManager, _logger);
		}

		return _archetypeArtifactManager;
	}

	private ArchetypeGenerator getArchetypeGenerator() throws Exception {
		ArchetypeGenerator archetypeGenerator =
		new DefaultArchetypeGenerator() {

			@Override
			public void generateArchetype(ArchetypeGenerationRequest request, File archetypeFile,
					ArchetypeGenerationResult result) {

				super.generateArchetype(request, archetypeFile, result);

				if (archetypeFile.getName().startsWith(TEMP_ARCHETYPE_PREFIX)) {
					archetypeFile.delete();
				}
			}

		};

		ArchetypeArtifactManager archetypeArtifactManager =
			getArchetypeArtifactManager();

		setField(
			DefaultArchetypeGenerator.class, "archetypeArtifactManager",
			archetypeGenerator, archetypeArtifactManager);

		FilesetArchetypeGenerator filesetGenerator =
			getFilesetArchetypeGenerator();

		setField(
			DefaultArchetypeGenerator.class, "filesetGenerator",
			archetypeGenerator, filesetGenerator);

		return archetypeGenerator;
	}

	private ArchetypeManager getArchetypeManager() throws Exception {
		DefaultArchetypeManager archetypeManager =
			new DefaultArchetypeManager();

		getLoggerField().set(archetypeManager, _logger);

		ArchetypeGenerator archetypeGenerator = getArchetypeGenerator();

		setField(
			DefaultArchetypeManager.class, "generator", archetypeManager,
			archetypeGenerator);

		return archetypeManager;
	}

	private Field getField(Class<?> clazz, String name) throws Exception {
		Field field = clazz.getDeclaredField(name);

		field.setAccessible(true);

		return field;
	}

	private FilesetArchetypeGenerator getFilesetArchetypeGenerator()
		throws Exception {

		FilesetArchetypeGenerator filesetArchetypeGenerator =
			new DefaultFilesetArchetypeGenerator();

		setField(
			DefaultFilesetArchetypeGenerator.class, "archetypeArtifactManager",
			filesetArchetypeGenerator, getArchetypeArtifactManager());

		getLoggerField().set(filesetArchetypeGenerator, _logger);

		DefaultArchetypeFilesResolver defaultArchetypeFilesResolver =
			new DefaultArchetypeFilesResolver();

		setField(
			DefaultFilesetArchetypeGenerator.class, "archetypeFilesResolver",
			filesetArchetypeGenerator, defaultArchetypeFilesResolver);

		DefaultVelocityComponent velocityComponent =
			new DefaultVelocityComponent();

		getLoggerField().set(velocityComponent, _logger);

		Properties velocityProps = new Properties();

		velocityProps.setProperty(
			RuntimeConstants.RESOURCE_LOADER, "classpath");

		velocityProps.setProperty(
			"classpath.resource.loader.class",
			ClasspathResourceLoader.class.getName());

		setField(
			DefaultVelocityComponent.class, "properties", velocityComponent,
			velocityProps);

		setField(
			DefaultFilesetArchetypeGenerator.class, "velocity",
			filesetArchetypeGenerator, velocityComponent);

		velocityComponent.initialize();

		return filesetArchetypeGenerator;
	}

	private Field getLoggerField() throws Exception {
		if (_loggerField == null) {
			_loggerField = getField(AbstractLogEnabled.class, "logger");
		}

		return _loggerField;
	}

	private void setField(Class<?> clazz, String name, Object obj, Object value)
		throws Exception {

		getField(clazz, name).set(obj, value);
	}

	private static final String TEMP_ARCHETYPE_PREFIX = "temp-archetype";

	private ArchetypeArtifactManager _archetypeArtifactManager;
	private final Logger _logger;
	private Field _loggerField;

}