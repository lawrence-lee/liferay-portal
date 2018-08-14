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

package com.liferay.workspaceTestSite;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.liferay.petra.log4j.Log4JUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.ThreadUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.struts.StrutsActionRegistryUtil;
import com.liferay.workspaceTestSite.configuration.TestSiteConfiguration;
import com.liferay.workspaceTestSite.setup.Setup;
import com.liferay.workspaceTestSite.struts.TestSiteStrutsAction;

/**
 * @author Matthew Tambara
 * @author Vernon Singleton
 */	
@Component(
	configurationPid = "com.liferay.workspaceTestSite.configuration.TestSiteConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE
)
public class TestSite {

	@Activate
	@Modified
	protected void activate(ComponentContext componentContext) {
		deactivate();

		if (!_log.isInfoEnabled()) {
			Log4JUtil.setLevel("com.liferay.workspaceTestSite", "INFO", true);
		}

		TestSiteConfiguration testSiteConfiguration =
			ConfigurableUtil.createConfigurable(
				TestSiteConfiguration.class,
				componentContext.getProperties());
		
//		System.err.println("activate: using PortletLocalServiceUtil.getPortlets ...");
//		List<Portlet> ps = PortletLocalServiceUtil.getPortlets();
//		for (Portlet portlet : ps) {
//			String portletName = portlet.getPortletName();
//			String contextName = portlet.getContextName();
//			if (Pattern.compile(Pattern.quote("sample"), Pattern.CASE_INSENSITIVE).matcher(portletName).find() ||
//				Pattern.compile(Pattern.quote("sample"), Pattern.CASE_INSENSITIVE).matcher(contextName).find()) {
//				System.err.println("activate: portlet.getPortletName() = " + portlet.getPortletName() + " contextName = " + portlet.getContextName());
//			}
//		}

		// Wait for portlet wars to deploy ...
		// They need to be available before setting up sites and pages
		
		String jsonFile = testSiteConfiguration.jsonFile();
		System.out.println("activate: jsonFile = " + jsonFile);
		
		HashMap<String, String> portletMap = new HashMap<String, String>();
		
		try {
			
			byte[] bytes = Files.readAllBytes(Paths.get(jsonFile));
			String content = new String(bytes, StandardCharsets.UTF_8);
			JSONObject jsonObject = new JSONObject(content);
			JSONObject site = (JSONObject) jsonObject.get("site");		
			JSONArray pages = site.getJSONArray("pages");
			
			for (int p = 0; p < pages.length(); p++) {
			    JSONObject page = (JSONObject) pages.get(p);
			    JSONArray portlets = page.getJSONArray("portlets");
			    
			    for (int o = 0; o < portlets.length(); o++) {
			    	String portlet = portlets.get(o).toString();
//			    	System.out.println("activate: portlet = " + portlet);
			    	portletMap.put(portlet, "1");
			    }
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Map.Entry<String, String> portlet : portletMap.entrySet()) {
	    	_waitForDeployment(
				portlet.getKey(), System.currentTimeMillis(),
				testSiteConfiguration.timeout() * Time.SECOND);
	    }

	    Setup.setupTestSite(testSiteConfiguration);

		StrutsActionRegistryUtil.register(_PATH, new TestSiteStrutsAction());

		FutureTask<Void> futureTask = new FutureTask<>(
			new HandshakeServerCallable(testSiteConfiguration));

		_handshakeServerFuture = futureTask;

		String wd = System.getProperty("user.dir");
		System.err.println("activate: wd = " + wd);

		Map<String, String> env = System.getenv();
		// for (String envName : env.keySet()) 
		for (Map.Entry<String, String> envEntry : env.entrySet()) {
		    System.out.format("activate: %s=%s%n", envEntry.getKey(), envEntry.getValue());
		}

		Thread handshakeServerThread = new Thread(
			futureTask, "Handshake server thread");

		handshakeServerThread.setDaemon(true);

		handshakeServerThread.start();

	}

	@Deactivate
	protected void deactivate() {
		Future<Void> handshakeServerFuture = _handshakeServerFuture;

		if (handshakeServerFuture != null) {
			handshakeServerFuture.cancel(true);
		}
		
		StrutsActionRegistryUtil.unregister(_PATH);
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	private void _waitForDeployment(
		String waitingForName, long startTime, long timeout) {

		if (_log.isInfoEnabled()) {
			_log.info(
				"_waitForDeployment: of " +
					waitingForName + " ...");
		}
		
		while ((System.currentTimeMillis() - startTime) < timeout) {
			
			boolean found = false;
			
			if (_log.isDebugEnabled()) {
				_log.debug(
					"_waitForDeployment: of " +
						waitingForName + " ...");
			}
			
			List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();
			for (Portlet portlet : portlets) {
				String portletName = portlet.getPortletName();
				if (Pattern.compile(Pattern.quote("sample"), Pattern.CASE_INSENSITIVE).matcher(portletName).find()) {
					System.err.println("_waitForDeployment: portletName = " + portletName + " portlet.isActive() = " + portlet.isActive());
				}
				found = portletName.equals(waitingForName) && portlet.isActive();
				if (found) {
					break;
				}
			}

			if (found) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"_waitForDeployment: found " +
							waitingForName);
				}

				return;
			}
			else {
				try {
					Thread.sleep(750);
				}
				catch (InterruptedException ie) {
					if (_log.isWarnEnabled()) {
						_log.warn(ie.getMessage());
					}
				}
			}
		}

		_log.error("Timeout waiting for " + waitingForName);
		_log.error(ThreadUtil.threadDump());
	}

	private static final String _PATH = "/test/site";

	private static final Log _log = LogFactoryUtil.getLog(
		TestSite.class);

	private volatile Future<Void> _handshakeServerFuture;

	private static class HandshakeServerCallable implements Callable<Void> {

		@Override
		public Void call() throws IOException {
			try (ServerSocket serverSocket = new ServerSocket(
					_testSiteConfiguration.handshakeServerPort())) {

				serverSocket.setSoTimeout(250);

				while (!Thread.interrupted()) {
					try (Socket socket = serverSocket.accept();
						OutputStream outputStream = socket.getOutputStream()) {

						outputStream.write(
							"Test Site is ready".getBytes(
								Charset.defaultCharset()));
					}
					catch (SocketTimeoutException ste) {
					}
				}
			}

			return null;
		}

		private HandshakeServerCallable(
			TestSiteConfiguration testSiteConfiguration) {

			_testSiteConfiguration = testSiteConfiguration;
		}

		private final TestSiteConfiguration
			_testSiteConfiguration;

	}

}