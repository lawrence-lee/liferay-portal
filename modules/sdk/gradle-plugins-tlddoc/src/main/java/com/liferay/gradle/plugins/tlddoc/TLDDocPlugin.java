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

package com.liferay.gradle.plugins.tlddoc;

import com.liferay.gradle.plugins.tlddoc.tasks.TLDDocTask;
import com.liferay.gradle.plugins.tlddoc.tasks.ValidateSchemaTask;
import com.liferay.gradle.util.GradleUtil;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Andrea Di Giorgi
 */
public class TLDDocPlugin implements Plugin<Project> {

	public static final String CONFIGURATION_NAME = "tldDoc";

	public static final String TLDDOC_TASK_NAME = "tldDoc";

	public static final String VALIDATE_TLD_TASK_NAME = "validateTLD";

	@Override
	public void apply(Project project) {
		Configuration tldDocConfiguration = addConfigurationTLDDoc(project);

		ValidateSchemaTask validateTLDTask = addTaskValidateTLD(project);

		addTaskTLDDoc(project, validateTLDTask);

		configureTasksTLDDoc(project, tldDocConfiguration);
	}

	protected ValidateSchemaTask addTaskValidateTLD(Project project) {
		ValidateSchemaTask validateSchemaTask = GradleUtil.addTask(
			project, VALIDATE_TLD_TASK_NAME, ValidateSchemaTask.class);

		validateSchemaTask.setSource(source);
		validateSchemaTask.include("**/*.tld");

		return validateSchemaTask;
	}

	protected Configuration addConfigurationTLDDoc(final Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					addDependenciesTLDDoc(project);
				}

			});

		configuration.setDescription(
			"Configures Tag Library Documentation Generator for this project.");
		configuration.setVisible(false);

		return configuration;
	}

	protected void addDependenciesTLDDoc(Project project) {
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "taglibrarydoc", "tlddoc", "1.3");
	}

	protected TLDDocTask addTaskTLDDoc(
		Project project, ValidateSchemaTask validateTLDTask) {

		TLDDocTask tldDocTask = GradleUtil.addTask(
			project, TLDDOC_TASK_NAME, TLDDocTask.class);

		tldDocTask.dependsOn(validateTLDTask);
		tldDocTask.setDescription("Runs Tag Library Documentation Generator.");

		return tldDocTask;
	}

	protected void configureTasksTLDDoc(
		Project project, final Configuration tldDocConfiguration) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			TLDDocTask.class,
			new Action<TLDDocTask>() {

				@Override
				public void execute(TLDDocTask tldDocTask) {
					configureTaskTLDDocClasspath(
						tldDocTask, tldDocConfiguration);
				}

			});
	}

	protected void configureTaskTLDDocClasspath(
		TLDDocTask tldDocTask, FileCollection fileCollection) {

		tldDocTask.setClasspath(fileCollection);
	}

}