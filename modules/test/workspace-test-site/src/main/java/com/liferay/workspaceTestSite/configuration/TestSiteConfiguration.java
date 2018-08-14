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

package com.liferay.workspaceTestSite.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Shuyang Zhou
 * @author Vernon Singleton
 */
@ExtendedObjectClassDefinition(category = "infrastructure")
@Meta.OCD(
	id = "com.liferay.workspaceTestSite.configuration.TestSiteConfiguration",
	localization = "content/Language",
	name = "workspace-test-site-configuration-name"
)
public interface TestSiteConfiguration {

	@Meta.AD(deflt = "8239", required = false)
	public int handshakeServerPort();

	@Meta.AD(deflt = "", required = false)
	public String jsonFile();

	@Meta.AD(deflt = "", required = false)
	public String testContext();

	@Meta.AD(deflt = "600", required = false)
	public long timeout();

}