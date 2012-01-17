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

package com.liferay.portalweb.portal.controlpanel.socialactivity.blogentry.changedefaultvalue.blogsentryparticipationlimit;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class SubscribeParticipationLimitValueTest extends BaseTestCase {
	public void testSubscribeParticipationLimitValue()
		throws Exception {
		selenium.open("/web/guest/home/");
		loadRequiredJavaScriptModules();

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isVisible("link=Control Panel")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.clickAt("link=Control Panel",
			RuntimeVariables.replace("Control Panel"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();

		for (int second = 0;; second++) {
			if (second >= 90) {
				fail("timeout");
			}

			try {
				if (selenium.isElementPresent("link=Social Activity")) {
					break;
				}
			}
			catch (Exception e) {
			}

			Thread.sleep(1000);
		}

		selenium.click(RuntimeVariables.replace("link=Social Activity"));
		selenium.waitForPageToLoad("30000");
		loadRequiredJavaScriptModules();
		selenium.click("//li[4]/div/div/div[2]/a[1]/span");
		selenium.clickAt("SUBSCRIBE_participationLimitValue",
			RuntimeVariables.replace(""));
		selenium.select("SUBSCRIBE_participationLimitValue",
			RuntimeVariables.replace("label=2"));
		selenium.clickAt("//select[@id='SUBSCRIBE_participationLimitValue']/option[3]",
			RuntimeVariables.replace("SUBSCRIBE_participationLimitValue"));
		selenium.select("SUBSCRIBE_participationLimitPeriod",
			RuntimeVariables.replace("label=times"));
		selenium.clickAt("//select[@id='SUBSCRIBE_participationLimitPeriod']/option[2]",
			RuntimeVariables.replace("SUBSCRIBE_participationLimitPeriod"));
		selenium.clickAt("//input[@value='Save']",
			RuntimeVariables.replace("Save"));

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
		assertEquals(RuntimeVariables.replace("Back to Liferay"),
			selenium.getText("link=Back to Liferay"));
		selenium.clickAt("link=Back to Liferay",
			RuntimeVariables.replace("Back to Liferay"));
	}
}