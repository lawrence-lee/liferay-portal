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
public class SAU_Vote2BlogTest extends BaseTestCase {
	public void testSAU_Vote2Blog() throws Exception {
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
				if (RuntimeVariables.replace("2 Comments")
										.equals(selenium.getText(
								"link=2 Comments"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=2 Comments",
			RuntimeVariables.replace("2 Comments"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible(
							"xPath=(//div[@class='aui-rating-label-element'])")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("Average (1 Vote)"),
			selenium.getText(
				"xPath=(//div[@class='aui-rating-label-element'])[2]"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isElementPresent("//a[3]")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("//a[3]", RuntimeVariables.replace("3 Stars"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible("link=\u00ab Back")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=\u00ab Back",
			RuntimeVariables.replace("\u00ab Back"));
		Thread.sleep(3000);

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("Joe Bloggs")
										.equals(selenium.getText(
								"//td/div[1]/a/span[2]"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("Joe Bloggs"),
			selenium.getText("//td/div[1]/a/span[2]"));
		assertEquals(RuntimeVariables.replace("exact:Rank: 1"),
			selenium.getText("//td/div[1]/div/div[1]"));
		assertEquals(RuntimeVariables.replace("Contribution Score: 42"),
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
		assertEquals(RuntimeVariables.replace("Participation Score: 42"),
			selenium.getText("//tr[2]/td/div[1]/div/div[3]"));
		assertEquals(RuntimeVariables.replace("User's Comments: 2"),
			selenium.getText("//tr[2]/td/div[3]"));
		assertEquals(RuntimeVariables.replace("User's Votes: 2"),
			selenium.getText("//tr[2]/td/div[4]"));
		assertEquals(RuntimeVariables.replace(
				"User's Cancelled Subscriptions: 2"),
			selenium.getText("//tr[2]/td/div[5]"));
		assertEquals(RuntimeVariables.replace("User's Subscriptions: 2"),
			selenium.getText("//tr[2]/td/div[6]"));
	}
}