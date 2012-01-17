/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portalweb.portal.controlpanel.socialactivity.blogentry.changedefaultvalue.socialactivityblogentrylimit;

import com.liferay.portalweb.portal.BaseTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class SocialActivityBlogEntryLimitTests extends BaseTests {
	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTestSuite(ConfigureCPSABlogEntryTest.class);
		testSuite.addTestSuite(AddSocialActivityBTPageTest.class);
		testSuite.addTestSuite(AddUSPortletTest.class);
		testSuite.addTestSuite(ConfigureUSPortletTest.class);
		testSuite.addTestSuite(AddSABlogsPortletTest.class);
		testSuite.addTestSuite(AddSABlogsEntryTest.class);
		testSuite.addTestSuite(AddSABlogsEntry2Test.class);
		testSuite.addTestSuite(AddSABlogsEntry3Test.class);
		testSuite.addTestSuite(EditBlogEntryTest.class);
		testSuite.addTestSuite(EditBlogEntry2Test.class);
		testSuite.addTestSuite(EditBlogEntry3Test.class);
		testSuite.addTestSuite(AddNewUserTest.class);
		testSuite.addTestSuite(SAU_LoginUserTest.class);
		testSuite.addTestSuite(SAU_ReadBlog1Test.class);
		testSuite.addTestSuite(SAU_ReadBlog2Test.class);
		testSuite.addTestSuite(SAU_ReadBlog3Test.class);
		testSuite.addTestSuite(SAU_Reply1BlogTest.class);
		testSuite.addTestSuite(SAU_Vote1BlogTest.class);
		testSuite.addTestSuite(SAU_Reply2BlogTest.class);
		testSuite.addTestSuite(SAU_Vote2BlogTest.class);
		testSuite.addTestSuite(SAU_Reply3BlogTest.class);
		testSuite.addTestSuite(SAU_Vote3BlogTest.class);
		testSuite.addTestSuite(SAU_LogoutUserTest.class);
		testSuite.addTestSuite(LoginTest.class);
		testSuite.addTestSuite(TearDownBlogsEntryTest.class);
		testSuite.addTestSuite(TearDownUserTest.class);
		testSuite.addTestSuite(TearDownPageTest.class);

		return testSuite;
	}
}