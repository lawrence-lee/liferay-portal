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

import java.io.File;
import java.io.IOException;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Lawrence Lee
 */
public class GradleRunnerTestRule implements TestRule {

	public static File findParentFile(
		File dir, String[] fileNames, boolean checkParents) {

		if (dir == null) {
			return null;
		}
		else if (Objects.equals(".", dir.toString()) || !dir.isAbsolute()) {
			try {
				dir = dir.getCanonicalFile();
			}
			catch (Exception e) {
				dir = dir.getAbsoluteFile();
			}
		}

		for (String fileName : fileNames) {
			File file = new File(dir, fileName);

			if (file.exists()) {
				return dir;
			}
		}

		if (checkParents) {
			return findParentFile(dir.getParentFile(), fileNames, checkParents);
		}

		return null;
	}

	public static File getGradleWrapper(File dir) {
		File gradleRoot = findParentFile(
			dir,
			new String[] {_GRADLEW_UNIX_FILE_NAME, _GRADLEW_WINDOWS_FILE_NAME},
			true);

		if (gradleRoot != null) {
			if (isWindows()) {
				return new File(gradleRoot, _GRADLEW_WINDOWS_FILE_NAME);
			}

			return new File(gradleRoot, _GRADLEW_UNIX_FILE_NAME);
		}

		return null;
	}

	public static boolean isWindows() {
		String osName = System.getProperty("os.name");

		osName = osName.toLowerCase();

		return osName.contains("windows");
	}

	@Override
	public Statement apply(Statement statement, Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				try {
					_stopGradleDaemonProcess();
					statement.evaluate();
				}
				catch (Throwable th) {
					throw new RuntimeException(th);
				}
				finally {
					_stopGradleDaemonProcess();
				}
			}

		};
	}

	private static String _getGradleExecutable(File dir) {
		File gradlew = getGradleWrapper(dir);
		String executable = "gradle";

		try {
			if ((gradlew != null) && gradlew.exists()) {
				if (!gradlew.canExecute()) {
					gradlew.setExecutable(true);
				}

				executable = gradlew.getCanonicalPath();
			}
			else {
				throw new NoSuchElementException(
					"Gradle wrapper not found and Gradle is not installed");
			}
		}
		catch (Throwable th) {
			throw new RuntimeException(th);
		}

		return executable;
	}

	private static void _stopGradleDaemonProcess() {
		try {
			File projectDir = new File(".");

			String executable = _getGradleExecutable(projectDir);

			ProcessBuilder processBuilder = new ProcessBuilder();

			processBuilder.directory(projectDir);

			processBuilder.command(executable, "--stop");

			processBuilder.start();

			System.out.println("*****STOPPING GRADLE DAEMON****");
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private static final String _GRADLEW_UNIX_FILE_NAME = "gradlew";

	private static final String _GRADLEW_WINDOWS_FILE_NAME = "gradlew.bat";

}