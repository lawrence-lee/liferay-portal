/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.project.templates;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.junit.Test;

import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;

/**
 * @author Vernon Singleton
 */

public class PortletTest extends BrowserDriverManagingTesterBase {
	
	// Private Constants
	private String[] ARCHETYPE_LIBRARIES = { "SampleMvc", "sample-war-mvc-portlet" };
	private String[] CSS_CLASSES = { "portlet-body", "portlet-body" };
	private String[] XTRA_XPATHS = { "/p/*", "/p" };
	
	@Test
	public void runTest() {

		BrowserDriver browserDriver = getBrowserDriver();
		WaitingAsserter waitingAsserter = getWaitingAsserter();

		for (int i = 0; i < ARCHETYPE_LIBRARIES.length; i++) {

			String archetypeLibrary = ARCHETYPE_LIBRARIES[i];
			String archetypeLibraryLowerCase = ARCHETYPE_LIBRARIES[i].toLowerCase(Locale.ENGLISH);
			String xpath = "//div[contains(@class,'" + CSS_CLASSES[i] + "')]" + XTRA_XPATHS[i];
			
			System.out.println("runArchetypePortletsTest: ... archetypeLibraryLowerCase = " + archetypeLibraryLowerCase);

			try {

				browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + "/web/workspace-test-site/" + archetypeLibraryLowerCase);
				
//				browserDriver.captureCurrentBrowserState();
//				try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(System.getenv("TMPDIR") + "captured-browser-state/"), "chrome_*.html")) {
//			        dirStream.forEach(path -> System.err.println(path));
//			    }
				
				waitingAsserter.assertTextPresentInElement("Hello from " + archetypeLibrary + "!", xpath);

			}
			catch (AssertionError e) {
				throw new AssertionError(archetypeLibraryLowerCase + "-portlet failed:", e);
			}
			catch (Exception e) {
				throw new AssertionError(archetypeLibraryLowerCase + "-portlet tester threw an error:", e);
			}
		}
	}

	@Override
	protected void doSetUp() {
		// do not bother logging in ...
		// super.doSetUp();
	}

}
