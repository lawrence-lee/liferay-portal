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

package com.liferay.project.templates.rest.builder.internal;

import com.beust.jcommander.Parameter;
import com.liferay.project.templates.extensions.ProjectTemplatesArgsExt;

/**
 * @author Javier de Arcos
 */
public class RESTBuilderProjectTemplatesArgs
	implements ProjectTemplatesArgsExt {

	public String getExtraModules() {
		return _extraModules;
	}

	@Override
	public String getTemplateName() {
		return "rest-builder";
	}

	public void setExtraModules(String extraModules) {
		_extraModules = extraModules;
	}

	@Parameter(
		description = "Specify if client and test integration modules should be created.",
		names = "--extraModules"
	)
	private String _extraModules = "false";

}