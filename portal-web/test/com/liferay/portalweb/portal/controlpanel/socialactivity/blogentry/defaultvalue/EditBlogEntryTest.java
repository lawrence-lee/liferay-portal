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

package com.liferay.portalweb.portal.controlpanel.socialactivity.blogentry.defaultvalue;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class EditBlogEntryTest extends BaseTestCase {
	public void testEditBlogEntry() throws Exception {
		selenium.open("/web/guest/home");
		loadRequiredJavaScriptModules();
		selenium.clickAt("//nav/ul/li[2]/a/span",
			RuntimeVariables.replace("Social Activity Blogs Test Page"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		assertEquals(RuntimeVariables.replace("Edit"),
			selenium.getText("//span/a/span"));
		selenium.clickAt("//span/a/span", RuntimeVariables.replace("Edit"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible(
							"//td[@id='cke_contents__33_editor']/iframe")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.selectFrame("//td[@id='cke_contents__33_editor']/iframe");
		selenium.type("//body", RuntimeVariables.replace("Content Edit"));
		selenium.selectFrame("relative=top");
		selenium.clickAt("//input[@value='Publish']",
			RuntimeVariables.replace("Publish"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace(
							"Your request completed successfully.")
										.equals(selenium.getText(
								"//section/div/div/div/div"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace(
				"Your request completed successfully."),
			selenium.getText("//section/div/div/div/div"));
		assertEquals(RuntimeVariables.replace("Content Edit"),
			selenium.getText("//p"));
		assertEquals(RuntimeVariables.replace("Joe Bloggs"),
			selenium.getText("//td/div[1]/a/span[2]"));
		assertEquals(RuntimeVariables.replace("exact:Rank: 1"),
			selenium.getText("//td/div/div/div[1]"));
		assertEquals(RuntimeVariables.replace("Contribution Score: 7"),
			selenium.getText("//td/div/div/div[2]"));
		assertEquals(RuntimeVariables.replace("Participation Score: 6"),
			selenium.getText("//td/div/div/div[3]"));
		assertEquals(RuntimeVariables.replace("User's Blog Entries: 1"),
			selenium.getText("//td/div[3]"));
		assertEquals(RuntimeVariables.replace("User's Blog Entry Updates: 1"),
			selenium.getText("//td/div[4]"));
		assertEquals(RuntimeVariables.replace("SAU SAU"),
			selenium.getText("//tr[2]/td/div[1]/a/span[2]"));
		assertEquals(RuntimeVariables.replace("exact:Rank: 2"),
			selenium.getText("//tr[2]/td/div[1]/div/div[1]"));
		assertEquals(RuntimeVariables.replace("Contribution Score: 0"),
			selenium.getText("//tr[2]/td/div[1]/div/div[2]"));
		assertEquals(RuntimeVariables.replace("Participation Score: 6"),
			selenium.getText("//tr[2]/td/div[1]/div/div[3]"));
		assertTrue(selenium.isElementPresent("//tr[2]/td/div[3]"));
		assertTrue(selenium.isElementPresent("//tr[2]/td/div[4]"));
		assertTrue(selenium.isElementPresent("//tr[2]/td/div[5]"));
	}
}