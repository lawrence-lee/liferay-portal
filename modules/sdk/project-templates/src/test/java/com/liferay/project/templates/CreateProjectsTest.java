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

package com.liferay.project.templates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class CreateProjectsTest {

	@Before
	public void setupTests() throws Exception {
		if (_testDir.exists()) {
			IO.delete(_testDir);
			assertFalse(_testDir.exists());
		}

		File archetypesDir = new File("../../../tools/sdk/dist/");
		File[] archetypesJars = archetypesDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("com.liferay.project.templates");
			}
		});
		IO.copy(new File("../../../tools/sdk/dist/com.liferay.project.templates.mvcportlet-1.0.0.jar"), new File("bin/com.liferay.project.templates.mvcportlet-1.0.0.jar"));
		IO.copy(new File("../../../tools/sdk/dist/com.liferay.project.templates.portlet-1.0.0.jar"), new File("bin/com.liferay.project.templates.portlet-1.0.0.jar"));
		IO.copy(new File("../../../tools/sdk/dist/com.liferay.project.templates.service-1.0.0.jar"), new File("bin/com.liferay.project.templates.service-1.0.0.jar"));

		IO.copy(new File("../../../tools/sdk/dist/com.liferay.project.templates.mvcportlet-1.0.0.jar"), new File("classes/com.liferay.project.templates.mvcportlet-1.0.0.jar"));
		IO.copy(new File("../../../tools/sdk/dist/com.liferay.project.templates.portlet-1.0.0.jar"), new File("classes/com.liferay.project.templates.portlet-1.0.0.jar"));
		IO.copy(new File("../../../tools/sdk/dist/com.liferay.project.templates.service-1.0.0.jar"), new File("classes/com.liferay.project.templates.service-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortletProject() throws Exception {
		String[] args = {
			"--destination", _testDir.getPath(),
			"--name", "foo",
			"--template", "mvcportlet"
		};

		ProjectTemplates.main(args);

		File projectDir = new File(_testDir, "foo");

		assertTrue(projectDir.exists());

		File bndFile = new File(projectDir, "bnd.bnd");

		assertTrue(bndFile.exists());

		File gradlewFile = new File(projectDir, "gradlew");

		assertTrue(gradlewFile.exists());

		File portletFile = new File(projectDir, "src/main/java/foo/portlet/FooPortlet.java");

		assertTrue(portletFile.exists());

		contains(portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File buildGradleFile = new File(projectDir, "build.gradle");

		assertTrue(buildGradleFile.exists());

		contains(buildGradleFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		File viewJspFile = new File(projectDir, "/src/main/resources/META-INF/resources/view.jsp");

		assertTrue(viewJspFile.exists());

//		if (SysProps.verifyBuilds) {
//			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
//			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
//			GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
//		}
	}

	private void contains(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		contains(content, pattern);
	}

	private void contains(String content, String pattern) throws Exception {
		assertTrue(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}

	private static File _testDir = IO.getFile("build/test");
}
