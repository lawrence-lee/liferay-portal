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

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class SAU_ReadBlog3Test extends BaseTestCase {
	public void testSAU_ReadBlog3() throws Exception {
		selenium.open("/web/guest/home/");
		loadRequiredJavaScriptModules();

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible("link=Social Activity Blogs Test Page")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=Social Activity Blogs Test Page",
			RuntimeVariables.replace("Social Activity Blogs Test Page"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("Title3")
										.equals(selenium.getText("link=Title3"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=Title3", RuntimeVariables.replace("Title3"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible("_33_TabsBack")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("_33_TabsBack", RuntimeVariables.replace("\u00ab Back"));
		Thread.sleep(3000);

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("Joe Bloggs")
										.equals(selenium.getText(
								"//div[1]/a/span[2]"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("Joe Bloggs"),
			selenium.getText("//div[1]/a/span[2]"));
		assertEquals(RuntimeVariables.replace("exact:Rank: 1"),
			selenium.getText("//td/div[1]/div/div[1]"));
		assertEquals(RuntimeVariables.replace("Contribution Score: 6"),
			selenium.getText("//td/div[1]/div/div[2]"));
		assertEquals(RuntimeVariables.replace("Participation Score: 12"),
			selenium.getText("//td/div[1]/div/div[3]"));
		assertEquals(RuntimeVariables.replace("User's Blog Entries: 3"),
			selenium.getText("//td/div[3]"));
		assertEquals(RuntimeVariables.replace("User's Blog Entry Updates: 3"),
			selenium.getText("//td/div[4]"));
		assertEquals(RuntimeVariables.replace("SAU SAU"),
			selenium.getText("//tr[2]/td/div[1]/a/span[2]"));
		assertEquals(RuntimeVariables.replace("exact:Rank: 2"),
			selenium.getText("//tr[2]/td/div[1]/div/div[1]"));
		assertEquals(RuntimeVariables.replace("Contribution Score: 0"),
			selenium.getText("//tr[2]/td/div[1]/div/div[2]"));
		assertEquals(RuntimeVariables.replace("Participation Score: 6"),
			selenium.getText("//tr[2]/td/div[1]/div/div[3]"));
	}
}