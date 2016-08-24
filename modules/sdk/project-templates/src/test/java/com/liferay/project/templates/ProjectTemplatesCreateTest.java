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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ProjectTemplatesCreateTest {

	@Before
	public void setUp() throws Exception {
		if (_testDir.exists()) {
			IO.delete(_testDir);
			assertFalse(_testDir.exists());
		}

		File testLibFolder = new File("test-lib");

		File[] archetypesJars = testLibFolder.listFiles(
			new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("com.liferay.project.templates");
				}

			});

		for (File archetypeJar : archetypesJars) {
			IO.copy(archetypeJar, new File("bin/" + archetypeJar.getName()));
			IO.copy(
				archetypeJar, new File("classes/" + archetypeJar.getName()));
		}
	}

	@Test
	public void testCreateMVCPortletProject() throws Exception {
		String[] args = {
			"--destination", _testDir.getPath(), "--name", "foo", "--template",
			"mvcportlet"
		};

		ProjectTemplates.main(args);

		File projectDir = new File(_testDir, "foo");

		assertTrue(projectDir.exists());

		File bndFile = new File(projectDir, "bnd.bnd");

		assertTrue(bndFile.exists());

		File gradlewFile = new File(projectDir, "gradlew");

		assertTrue(gradlewFile.exists());

		File portletFile = new File(
			projectDir, "src/main/java/foo/portlet/FooPortlet.java");

		assertTrue(portletFile.exists());

		contains(
			portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File buildGradleFile = new File(projectDir, "build.gradle");

		assertTrue(buildGradleFile.exists());

		contains(buildGradleFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		File viewJspFile = new File(
			projectDir, "/src/main/resources/META-INF/resources/view.jsp");

		assertTrue(viewJspFile.exists());
	}

	@Test
	public void testCreateActivator() throws Exception {
		String[] args = {
			"--destination", _testDir.getPath(), "--name", "bar-activator", "--template",
			"activator"
		};

		ProjectTemplates.main(args);

		File projectDir = new File(_testDir, "bar-activator");

		assertTrue(projectDir.exists());

		File bndFile = new File(projectDir, "bnd.bnd");

		assertTrue(bndFile.exists());

		File gradlewFile = new File(projectDir, "gradlew");

		assertTrue(gradlewFile.exists());

		File activatorFile = new File(projectDir, "src/main/java/bar/activator/BarActivator.java");

		assertTrue(activatorFile.exists());

		contains(activatorFile, ".*^public class BarActivator implements BundleActivator.*$");

		BuildTask buildtask = executeGradleRunner(projectDir, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectDir, "bar.activator-1.0.0.jar");
	}

	@Test
	public void testCreateGradleFragment() throws Exception {
		String[] args = {
				"--destination", _testDir.getPath(), "--host-bundle-symbolic-name", "com.liferay.login.web",
				"--host-bundle-version", "1.0.0", "--template", "fragment", "--name", "loginHook"
		};

		ProjectTemplates.main(args);

		File projectDir = new File(_testDir, "loginhook");

		assertTrue(projectDir.exists());

		File bndfile = new File(projectDir, "bnd.bnd");

		contains(
			bndfile,
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		File buildfile = new File(projectDir, "build.gradle");

		contains(buildfile,
			".*^apply plugin: \"com.liferay.plugin\".*");


		BuildTask buildtask = executeGradleRunner(projectDir, "build");
		verifyGradleRunnerOutput(buildtask);
		verifyBuildOutput(projectDir, "loginhook-1.0.0.jar");

	}

	@Test
	public void testCreateGradleMVCPortletProject() throws Exception {
		String[] args = {
			"--destination", _testDir.getPath(), "--template", "mvcportlet", "--name", "foo"
		};

		ProjectTemplates.main(args);

		File projectDir = new File(_testDir, "foo");

		assertTrue(projectDir.exists());

		checkFileExists(projectDir + "/bnd.bnd");

		checkFileExists(projectDir + "/gradlew");

		checkFileExists(projectDir + "/gradlew.bat");

		File classfile = new File(projectDir, "/src/main/java/foo/portlet/FooPortlet.java");

		contains(classfile,
			".*^public class FooPortlet extends MVCPortlet.*$");
		File buildfile = new File(projectDir, "build.gradle");

		contains(
			buildfile,
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectDir + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectDir + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildtask = executeGradleRunner(projectDir, "build");
		verifyGradleRunnerOutput(buildtask);
		verifyBuildOutput(projectDir, "foo-1.0.0.jar");
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPackage()
		throws Exception {

		String[] args = {
			"--destination", _testDir.getPath(), "--template", "mvcportlet", "--package-name",
			"com.liferay.test", "--name",  "foo"
		};

		ProjectTemplates.main(args);

		File projectDir = new File(_testDir, "foo");

		assertTrue(projectDir.exists());

		checkFileExists(projectDir + "/bnd.bnd");

		File classfile = new File(projectDir, "/src/main/java/com/liferay/test/portlet/FooPortlet.java");

		contains(
			classfile,
			".*^public class FooPortlet extends MVCPortlet.*$");

		File buildfile = new File(projectDir, "build.gradle");

		contains(
			buildfile,
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
				projectDir + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
				projectDir + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildtask = executeGradleRunner(projectDir, "build");
		verifyGradleRunnerOutput(buildtask);
		verifyBuildOutput(projectDir, "com.liferay.test-1.0.0.jar");

	}

	/*@Test
	public void testCreateGradleMVCPortletProjectWithPortletSuffix() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "mvcportlet", "portlet-portlet"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/portlet-portlet";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		contains(
			checkFileExists(projectPath + "/src/main/java/portlet/portlet/portlet/PortletPortlet.java"),
			".*^public class PortletPortlet extends MVCPortlet.*$");

		contains(
			checkFileExists(projectPath + "/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "portlet.portlet-1.0.0.jar");
		}
	}

	@Test
	public void testCreateGradlePortletProject() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "portlet", "-c", "Foo",
			"gradle.test"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/gradle.test";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/src/main/java/gradle/test/portlet/FooPortlet.java"),
			new String[] {
				"^package gradle.test.portlet;.*",
				".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*",
				".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "gradle.test-1.0.0.jar");
		}
	}

	@Test
	public void testCreateGradleServiceBuilderDashes() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicebuilder", "-p",
			"com.liferay.backend.integration", "backend-integration"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/backend-integration";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include \"backend-integration-api\", " +
			"\"backend-integration-service\"");

		contains(
			checkFileExists(projectPath + "/backend-integration-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*",
				".*com.liferay.backend.integration.exception,\\\\.*",
				".*com.liferay.backend.integration.model,\\\\.*",
				".*com.liferay.backend.integration.service,\\\\.*",
				".*com.liferay.backend.integration.service.persistence.*"
			});

		contains(
			checkFileExists(
				projectPath + "/backend-integration-service/bnd.bnd"),
				".*Liferay-Service: true.*");

		if (SysProps.verifyBuilds) {
			BuildTask buildServiceTask = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildServiceTask);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/backend-integration-api",
					"backend.integration-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/backend-integration-service",
					"backend.integration-service-1.0.0.jar");
		}
	}

	@Test
	public void testCreateGradleServiceBuilderDefault() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicebuilder", "-p",
			"com.liferay.docs.guestbook", "guestbook"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/guestbook";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include \"guestbook-api\", \"guestbook-service\"");

		contains(
			checkFileExists(projectPath + "/guestbook-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*",
				".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*",
				".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		contains(
			checkFileExists(projectPath + "/guestbook-service/bnd.bnd"),
				".*Liferay-Service: true.*");

		contains(
			checkFileExists(projectPath + "/guestbook-service/build.gradle"),
				".*compile project\\(\":guestbook-api\"\\).*");

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/guestbook-api", "guestbook-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/guestbook-service", "guestbook-service-1.0.0.jar");
		}
	}

	@Test
	public void testCreateGradleServiceBuilderDots() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicebuilder", "-p",
			"com.liferay.docs.guestbook", "com.liferay.docs.guestbook"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/com.liferay.docs.guestbook";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include \"com.liferay.docs.guestbook.api\", " +
			"\"com.liferay.docs.guestbook.svc\"");

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*",
				".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*",
				".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.svc/bnd.bnd"),
				".*Liferay-Service: true.*");

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook.api",
					"com.liferay.docs.guestbook-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook.svc",
					"com.liferay.docs.guestbook-service-1.0.0.jar");
		}
	}

	@Test
	public void testCreateGradleService() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "service", "-s",
			"com.liferay.portal.kernel.events.LifecycleAction", "-c",
			"FooAction", "servicepreaction"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/servicepreaction";

		checkFileExists(projectPath + "/build.gradle");

		File file = new File(projectPath + "/src/main/java/servicepreaction/FooAction.java");

		contains(
			checkFileExists(file.getPath()),
			new String[] {
				"^package servicepreaction;.*",
				".*^import com.liferay.portal.kernel.events.LifecycleAction;$.*",
				".*service = LifecycleAction.class.*",
				".*^public class FooAction implements LifecycleAction \\{.*"
			});

		List<String> lines = new ArrayList<String>();
		String line = null;

		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((line = reader.readLine()) !=null) {
				lines.add(line);
				if (line.equals("import com.liferay.portal.kernel.events.LifecycleAction;")) {
					lines.add("import com.liferay.portal.kernel.events.LifecycleEvent;");
					lines.add("import com.liferay.portal.kernel.events.ActionException;");
				}

				if (line.equals("public class FooAction implements LifecycleAction {")) {
					String s = new StringBuilder()
					           .append("@Override\n")
					           .append("public void processLifecycleEvent(LifecycleEvent lifecycleEvent)\n")
					           .append("throws ActionException {\n")
					           .append("System.out.println(\"login.event.pre=\" + lifecycleEvent);\n")
					           .append("}\n")
					           .toString();
					lines.add(s);
				}
			}
		}

		try(Writer writer = new FileWriter(file)) {
			for(String string : lines){
				writer.write(string + "\n");
			}
		}

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "servicepreaction-1.0.0.jar");
		}
	}

	@Test
	public void testCreateGradleServiceWrapper() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicewrapper", "-s",
			"com.liferay.portal.kernel.service.UserLocalServiceWrapper",
			"serviceoverride"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/serviceoverride";

		checkFileExists(projectPath + "/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/src/main/java/serviceoverride/Serviceoverride.java"),
			new String[] {
				"^package serviceoverride;.*",
				".*^import com.liferay.portal.kernel.service.UserLocalServiceWrapper;$.*",
				".*service = ServiceWrapper.class.*",
				".*^public class Serviceoverride extends UserLocalServiceWrapper \\{.*",
				".*public Serviceoverride\\(\\) \\{.*"
			});

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "serviceoverride-1.0.0.jar");
		}
	}

	@Test
	public void testCreateOnExistFolder() throws Exception {
		String[] args = {
			"create", "-d", "generated", "-t", "activator", "exist"
		};

		File existFile = IO.getFile("generated/exist/file.txt");

		if(!existFile.exists()) {
			IO.getFile("generated/exist").mkdirs();
			existFile.createNewFile();
			assertTrue(existFile.exists());
		}

		new bladenofail().run(args);

		String projectPath = "generated/exist";

		checkFileDoesNotExists(projectPath+"/bnd.bnd");
	}

	@Test
	public void testCreateGradleSymbolicName() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-p", "foo.bar", "barfoo"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/barfoo";

		checkFileExists(projectPath + "/build.gradle");

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			".*Bundle-SymbolicName: barfoo.*");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "barfoo-1.0.0.jar");
		}
	}

	@Test
	public void testCreateProjectAllDefaults() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "hello-world-portlet"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/hello-world-portlet";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/src/main/java/hello/world/portlet/portlet/" +
				"HelloWorldPortlet.java");

		contains(
			portletFile,
			".*^public class HelloWorldPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.portlet-1.0.0.jar");
		}
	}

	@Test
	public void testCreateProjectWithRefresh() throws Exception {
		String[] args = {
				"create", "-d", "generated/test", "hello-world-refresh"
			};

		new bladenofail().run(args);

		String projectPath = "generated/test/hello-world-refresh";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/src/main/java/hello/world/refresh/portlet/" +
				"HelloWorldRefreshPortlet.java");

		contains(
			portletFile,
			".*^public class HelloWorldRefreshPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.refresh-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceGradleFragment() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/extensions", "-t",
			"fragment", "-h", "com.liferay.login.web", "-H", "1.0.0", "loginHook"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/extensions";

		checkFileExists(projectPath + "/loginHook");

		contains(
			checkFileExists(projectPath + "/loginHook/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		checkFileExists(projectPath + "/loginHook/build.gradle");

		lacks(
			checkFileExists(projectPath + "/loginHook/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/loginHook", "loginhook-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceGradlePortletProject() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps", "-t",
			"portlet", "-c", "Foo", "gradle.test"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/apps";

		checkFileExists(projectPath + "/gradle.test/build.gradle");

		checkFileDoesNotExists(projectPath + "/gradle.test/gradlew");

		contains(
			checkFileExists(
				projectPath + "/gradle.test/src/main/java/gradle/test/portlet/FooPortlet.java"),
			new String[] {
				"^package gradle.test.portlet;.*",
				".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*",
				".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		lacks(
			checkFileExists(projectPath + "/gradle.test/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/gradle.test", "gradle.test-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectApiPath()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules/nested/path",
			"-t", "servicebuilder", "-p", "com.liferay.sample", "sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		assertTrue(
			new File("generated/test/workspace/modules/nested/path").mkdirs());

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/nested/path";

		checkFileExists(projectPath + "/sample/build.gradle");

		checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/sample/sample-service/build.gradle"),
				".*compile project\\(\":modules:nested:path:sample:sample-api\"\\).*");

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-service", "sample-service-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDashes()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"servicebuilder", "-p", "com.sample", "workspace-sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/workspace-sample/build.gradle");

		checkFileDoesNotExists(
			projectPath + "/workspace-sample/settings.gradle");

		checkFileExists(
			projectPath + "/workspace-sample/workspace-sample-api/build.gradle");

		checkFileExists(
			projectPath + "/workspace-sample/workspace-sample-service/build.gradle");

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-api",
					"workspace.sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-service",
					"workspace.sample-service-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDefault()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"servicebuilder", "-p", "com.liferay.sample", "sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/sample/build.gradle");

		checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/sample/sample-service/build.gradle"),
				".*compile project\\(\":modules:sample:sample-api\"\\).*");

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-service", "sample-service-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDots()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"servicebuilder", "-p", "com.sample", "workspace.sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/workspace.sample/build.gradle");

		checkFileDoesNotExists(
			projectPath + "/workspace.sample/settings.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/com.sample.api/build.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/com.sample.svc/build.gradle");

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace.sample/com.sample.api",
					"workspace.sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace.sample/com.sample.svc",
					"workspace.sample-service-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceProjectAllDefaults() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps", "foo"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/apps";

		checkFileExists(projectPath + "/foo");

		checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/foo/src/main/java/foo/portlet/FooPortlet.java");

		contains(
			portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(
			projectPath + "/foo/build.gradle");

		lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");
		}
	}

	@Test
	public void testCreateWorkspaceProjectWithRefresh() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps",
			"foo-refresh"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath =
				"generated/test/workspace/modules/apps/foo-refresh";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = checkFileExists(
				projectPath +
				"/src/main/java/foo/refresh/portlet/FooRefreshPortlet.java");

		contains(
			portletFile,
			".*^public class FooRefreshPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(
			projectPath + "/build.gradle");

		lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "foo.refresh-1.0.0.jar");
		}

	}

	@Test
	public void testListTemplates() throws Exception {
		String[] args = {"create", "-l"};

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);

		blade blade = new bladenofail(ps);

		blade.run(args);

		String templateList = new String(output.toByteArray());

		for (String templateName : ProjectTemplates.getTemplates()) {
			assertTrue(templateList.contains(templateName));
		}
	}

	@Test
	public void testWrongTemplateTyping() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "activatorXXX", "wrong-activator"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/wrong-activator";

		checkFileDoesNotExists(projectPath);
	}*/

	private File checkFileDoesNotExists(String path) {
		File file = IO.getFile(path);

		assertFalse(file.exists());

		return file;
	}

	private File checkFileExists(String path) {
		File file = IO.getFile(path);

		assertTrue(file.exists());

		return file;
	}

	private void contains(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		contains(content, pattern);
	}

	private void contains(File file, String[] patterns) throws Exception {
		String content = new String(IO.read(file));

		for (String pattern : patterns) {
			contains(content, pattern);
		}
	}

	private void contains(String content, String pattern) throws Exception {
		assertTrue(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}

	private void lacks(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		assertFalse(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}

	private static BuildTask executeGradleRunner(File projectDir, String... taskPath) {
		BuildResult buildResult = GradleRunner.create()
									.withProjectDir(projectDir)
									.withArguments(taskPath)
									.build();

		BuildTask buildtask = null;

		for (BuildTask task : buildResult.getTasks()) {
			if (task.getPath().endsWith(taskPath[taskPath.length - 1])) {
				buildtask = task;
				break;
			}
		}

		return buildtask;
	}

	private static void verifyGradleRunnerOutput (BuildTask buildtask) {
		assertNotNull(buildtask);

		assertEquals(buildtask.getOutcome(), TaskOutcome.SUCCESS);
	}

	private static void verifyBuildOutput(File projectDir, String fileName) {
		File buildOutput = new File(projectDir, "build/libs/" + fileName);

		assertTrue(buildOutput.exists());
	}

	private static final File _testDir = IO.getFile("build/test");

}