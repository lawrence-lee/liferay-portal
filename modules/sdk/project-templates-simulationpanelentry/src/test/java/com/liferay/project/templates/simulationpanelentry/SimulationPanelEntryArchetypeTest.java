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

package com.liferay.project.templates.simulationpanelentry;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.maven.cli.MavenCli;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class SimulationPanelEntryArchetypeTest {
	@Before
	public void setUp() throws Exception {
		Path testPath = Paths.get("build/test");

		if (testPath.toFile().exists()) {

			Files.walkFileTree(testPath, new SimpleFileVisitor<Path>() {
			   @Override
			   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			       Files.delete(file);
			       return FileVisitResult.CONTINUE;
			   }

			   @Override
			   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			       Files.delete(dir);
			       return FileVisitResult.CONTINUE;
			   }
			});
		}
	}

	@Test
	public void testSimulationPanelEntryArchetype() throws Exception {
		String[] args = {
			"archetype:generate",
			"-B",
			"-DarchetypeArtifactId=com.liferay.project.templates.simulationpanelentry",
			"-DarchetypeGroupId=com.liferay",
			"-DarchetypeVersion=1.0.0",
			"-DgroupId=com.test",
			"-DartifactId=simulation-panel-foo",
			"-Dpackage=com.test.foo",
			"-DclassName=FooSimulation",
			"-DprojectType=standalone"
		};

		MavenCli mavenCli = new MavenCli();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();

		int retcode = mavenCli.doMain(args, "build/test", new PrintStream(output), new PrintStream(errorOutput));

		assertEquals(new String(errorOutput.toByteArray()), 0, retcode);

		output = new ByteArrayOutputStream();
		errorOutput = new ByteArrayOutputStream();

		args = new String[] {
			"-e",
			"package"
		};

		retcode = mavenCli.doMain(args, "build/test/simulation-panel-foo", new PrintStream(output), new PrintStream(errorOutput));

		assertEquals(new String(errorOutput.toByteArray()), 0, retcode);
	}
}
