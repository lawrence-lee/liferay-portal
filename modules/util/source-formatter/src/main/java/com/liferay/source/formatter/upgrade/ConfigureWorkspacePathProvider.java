/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.source.formatter.upgrade;

import static com.liferay.code.upgrade.providers.WorkspaceFunctions.isWorkspacePath;
import static com.liferay.lugbot.api.Constants.LUGBOT_YAML_PATH;
import static com.liferay.lugbot.api.util.GitFunctions.commitChanges;
import static com.liferay.lugbot.api.util.LogFunctions.logError;

import com.liferay.ide.upgrade.problems.core.internal.MarkdownParser;
import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactory;
import com.liferay.lugbot.api.LugbotConfig;
import com.liferay.lugbot.api.ProposalDTO;
import com.liferay.lugbot.api.ReportDTO;
import com.liferay.lugbot.api.ReportProvider;
import com.liferay.lugbot.api.UpgradeProvider;
import com.liferay.lugbot.api.util.GitFunctions;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.errors.GitAPIException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

/**
 * @author Gregory Amerson
 */
public class ConfigureWorkspacePathProvider implements ReportProvider, UpgradeProvider {

	@Activate
	public ConfigureWorkspacePathProvider(@Reference MarkdownConverterFactory markdownConverterFactory) {
		_markdownConverterFactory = markdownConverterFactory;
	}

	@Override
	public List<String> computePossibleUpgrades(Path repoPath, LugbotConfig lugbotConfig) {
		Path workspacePath = repoPath;

		if (lugbotConfig.tasks.workspacePath != null) {
			workspacePath = repoPath.resolve(
				lugbotConfig.tasks.workspacePath
			).normalize();
		}

		if (isWorkspacePath(workspacePath)) {
			_logger.info("{} already is a configured workspace, no possible upgrades.", workspacePath);

			return Collections.emptyList();
		}

		if (lugbotConfig.tasks.workspacePath == null) {
			return Collections.singletonList("ConfigureWorkspacePath;" + TYPE + ";" + _PROVIDER_ACTION);
		}

		return Collections.emptyList();
	}

	@Override
	public List<String> possibleReports(Path repoPath, LugbotConfig lugbotConfig) {
		return computePossibleUpgrades(repoPath, lugbotConfig);
	}

	@Override
	public Optional<ProposalDTO> provideUpgrade(Path repoPath, LugbotConfig lugbotConfig, String upgradeName) {
		Path workspacePath = repoPath;

		if (lugbotConfig.tasks.workspacePath != null) {
			workspacePath = repoPath.resolve(
				lugbotConfig.tasks.workspacePath
			).normalize();
		}

		if (isWorkspacePath(workspacePath)) {
			_logger.warn("{} is already a workspace, no proposal will be generated.", workspacePath);

			return Optional.empty();
		}

		if (!Files.exists(repoPath.resolve("liferay-workspace"))) {
			try {
				Path lugbotYaml = repoPath.resolve(LUGBOT_YAML_PATH);

				List<String> newLines = new ArrayList<>();

				if (Files.exists(lugbotYaml)) {
					newLines.addAll(Files.readAllLines(lugbotYaml));
				}

				newLines.add("#tasks:");
				newLines.add("#  mode: pr");
				newLines.add("#  workspacePath: path/to/liferay-workspace/");

				String newContent = newLines.stream(
				).collect(
					Collectors.joining(System.lineSeparator())
				);

				Files.createDirectories(lugbotYaml.getParent());

				Files.write(lugbotYaml, newContent.getBytes());

				String message = "Added workspacePath to Lugbot configuration file";

				commitChanges(repoPath, message, lugbotConfig);

				String body = MarkdownParser.getSection(
					_markdownConverterFactory, "docs/providers/ConfigureWorkspacePath.markdown", "#add-workspacePath");

				return Optional.of(
					new ProposalDTO(
						upgradeName, _PROVIDER_ACTION, "required", message, body,
						GitFunctions.getCurrentBranchName(repoPath)));
			}
			catch (GitAPIException | IOException e) {
				logError(_logger, e, "Unable to update Lugbot yaml config file to include a workspace path");
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<ReportDTO> report(Path repoPath, Path reportsPath, LugbotConfig lugbotConfig, String reportName) {
		if (!Objects.equals(reportName, _PROVIDER_ACTION)) {
			Optional.empty();
		}

		Path workspacePath = repoPath;

		ReportDTO reportDTO = new ReportDTO();

		reportDTO.name = _REPORT_NAME;
		reportDTO.title = "configured with a valid Blade workspace";

		if (lugbotConfig.tasks.workspacePath != null) {
			workspacePath = repoPath.resolve(
				lugbotConfig.tasks.workspacePath
			).normalize();
		}

		if (isWorkspacePath(workspacePath)) {
			reportDTO.status = "skipped";

			_logger.warn("{} is already a workspace, no proposal will be generated.", workspacePath);

			return Optional.of(reportDTO);
		}

		if (!Files.exists(repoPath.resolve("liferay-workspace"))) {
			reportDTO.status = "failed";
			reportDTO.summary.add("Verifies that Lugbot config is configured with a valid Blade workspace.");
			reportDTO.file = repoPath.resolve(
				"liferay-workspace"
			).toString();

			return Optional.of(reportDTO);
		}

		reportDTO.status = "skipped";

		return Optional.of(reportDTO);
	}

	private static final String _PROVIDER_ACTION = "Adding a workspacePath setting to the Lugbot yaml file";

	private static final String _REPORT_NAME = "config_workspace_report";

	@Reference(service = LoggerFactory.class)
	private Logger _logger;

	private final MarkdownConverterFactory _markdownConverterFactory;

}