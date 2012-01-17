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
public class SAU_Reply3BlogTest extends BaseTestCase {
	public void testSAU_Reply3Blog() throws Exception {
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
				if (RuntimeVariables.replace("Add Comment")
										.equals(selenium.getText(
								"//fieldset/div/span[1]/a/span"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("//fieldset/div/span[1]/a/span",
			RuntimeVariables.replace("Add Comment"));
		selenium.type("//textarea", RuntimeVariables.replace("User3"));
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
		assertEquals(RuntimeVariables.replace("User3"),
			selenium.getText("//div[3]/div[3]/div/div[1]"));

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

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("3 Comments")
										.equals(selenium.getText(
								"link=3 Comments"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=3 Comments",
			RuntimeVariables.replace("3 Comments"));

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (RuntimeVariables.replace("Unsubscribe from Comments")
										.equals(selenium.getText(
								"//a[@id='_33_suxb']/span"))) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("//a[@id='_33_suxb']/span",
			RuntimeVariables.replace("Unsubscribe from Comments"));

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
		assertEquals(RuntimeVariables.replace("Subscribe to Comments"),
			selenium.getText("//span[2]/a/span"));

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
		Thread.sleep(3000);
	}
}