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
public class SAU_SubscribeVoteCommentOnBETest extends BaseTestCase {
	public void testSAU_SubscribeVoteCommentOnBE() throws Exception {
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
				if (selenium.isElementPresent("link=0 Comments")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertTrue(selenium.isElementPresent("link=0 Comments"));
		selenium.clickAt("link=0 Comments",
			RuntimeVariables.replace("0 Comments"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isElementPresent("link=Be the first.")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=Be the first.",
			RuntimeVariables.replace("Be the first."));
		assertTrue(selenium.isElementPresent("//textarea"));
		selenium.type("//textarea", RuntimeVariables.replace("SAU"));
		assertTrue(selenium.isElementPresent("//div/span[1]/span/input"));
		selenium.clickAt("//div/span[1]/span/input",
			RuntimeVariables.replace("Reply"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace(
							"Your request processed successfully.")
										.equals(selenium.getText(
								"//div/div/div[3]/div/div[2]/div[1]"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace(
				"Your request processed successfully."),
			selenium.getText("//div/div/div[3]/div/div[2]/div[1]"));
		assertEquals(RuntimeVariables.replace("Unsubscribe from Comments"),
			selenium.getText("//span[2]/a/span"));
		assertEquals(RuntimeVariables.replace("SAU SAU"),
			selenium.getText("//div/a/span[2]"));
		assertEquals(RuntimeVariables.replace("SAU"),
			selenium.getText("//form/div/div/div/div[3]/div/div[1]"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("\u00ab Back")
										.equals(selenium.getText(
								"link=\u00ab Back"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=\u00ab Back",
			RuntimeVariables.replace("\u00ab Back"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isElementPresent("link=1 Comment")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=1 Comment", RuntimeVariables.replace("1 Comment"));
		Thread.sleep(5000);

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible(
							"xPath=(//div[@class='aui-rating-label-element'])[2]")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("Average (0 Votes)"),
			selenium.getText(
				"xPath=(//div[@class='aui-rating-label-element'])[2]"));
		selenium.clickAt("//a[4]", RuntimeVariables.replace("4 Stars"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("\u00ab Back")
										.equals(selenium.getText(
								"link=\u00ab Back"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=\u00ab Back",
			RuntimeVariables.replace("\u00ab Back"));
		Thread.sleep(5000);

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("1 Comment")
										.equals(selenium.getText(
								"link=1 Comment"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		assertEquals(RuntimeVariables.replace("1 Comment"),
			selenium.getText("link=1 Comment"));
		assertEquals(RuntimeVariables.replace("Joe Bloggs"),
			selenium.getText("//td/div[1]/a/span[2]"));
		assertEquals(RuntimeVariables.replace("exact:Rank: 1"),
			selenium.getText("//td/div/div/div[1]"));
		assertEquals(RuntimeVariables.replace("Contribution Score: 7"),
			selenium.getText("//td/div/div/div[2]"));
		assertEquals(RuntimeVariables.replace("Participation Score: 5"),
			selenium.getText("//td/div/div/div[3]"));
		assertEquals(RuntimeVariables.replace("User's Blog Entries: 1"),
			selenium.getText("//td/div[3]"));
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