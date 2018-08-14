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

package com.liferay.workspaceTestSite.setup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Bundle;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.workspaceTestSite.configuration.TestSiteConfiguration;

/**
 * @author Vernon Singleton
 */
public class Setup {

	public static void setupTestSite(
		TestSiteConfiguration testSiteConfiguration) {

		long companyId = 0L;
		long userId = 0L;

		try {
			for (long id : PortalUtil.getCompanyIds()) {
				_setupPermissionChecker(id);
				Company company = CompanyLocalServiceUtil.getCompanyById(id);

				if ("Liferay".equals(company.getName())) {
					companyId = id;
					userId = company.getDefaultUser().getUserId();
				}
			}
			
			System.err.println("wts setupTestSite: companyId = " + companyId);
			System.err.println("wts setupTestSite: userId = " + userId);

			Group group = _getGroupForSite(companyId, userId);

			long groupId = group.getGroupId();

			_addAllUsersToSite(groupId);
			
			String jsonFile = testSiteConfiguration.jsonFile();
			
			try {
				
				byte[] bytes = Files.readAllBytes(Paths.get(jsonFile));
				String content = new String(bytes, StandardCharsets.UTF_8);
				JSONObject jsonObject = new JSONObject(content);
				JSONObject site = (JSONObject) jsonObject.get("site");		
				JSONArray pages = site.getJSONArray("pages");
				
				for (int p = 0; p < pages.length(); p++) {
				    JSONObject page = (JSONObject) pages.get(p);
				    String pageName = page.getString("name");
				    System.out.println("activate: pageName = " + pageName);
				    
				    JSONArray jsonArrayOfPortlets = page.getJSONArray("portlets");

				    List<Portlet> portlets = new LinkedList<>();
			    	for (int i = 0; i < jsonArrayOfPortlets.length(); i += 1) {
			    		String portlet = jsonArrayOfPortlets.get(i).toString();
						portlets.add(_createPortlet(portlet, pageName));
			    	}
			    	
			    	PortalPage portalPage = new PortalPage(pageName, portlets);

					_setupPage(userId, groupId, portalPage, pageName);

				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	private static void _addAllUsersToSite(long groupId) throws Exception {
		List<User> users = UserLocalServiceUtil.getUsers(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		ArrayList<Long> userIdList = new ArrayList<>();

		for (User user : users) {
			if (!user.isDefaultUser()) {
				userIdList.add(user.getUserId());
			}
		}

		long[] userIds = new long[userIdList.size()];

		for (int i = 0; i < userIds.length; i++) {
			userIds[i] = userIdList.get(i);
		}

		UserLocalServiceUtil.addGroupUsers(groupId, userIds);
	}

	private static void _addPortlet(
			LayoutTypePortlet layoutTypePortlet, long userId, int columnNumber,
			String portletId)
		throws PortalException {

		String columnNumberLabel = Integer.toString(columnNumber);

		// Liferay 6.2 changed the expected value for the String-based column
		// number. Previous versions didn't require the "column-" prefix.

		columnNumberLabel = "column-" + columnNumber;

		// NOTE: In Liferay 6.1.x the following call was to setPortletIds()
		// but that method was removed in 6.2.x

		layoutTypePortlet.addPortletId(
			userId, portletId, columnNumberLabel, -1);

		layoutTypePortlet.resetModes();
		layoutTypePortlet.resetStates();
	}

	private static Portlet _createPortlet(String portletName, String pageName) {
		return new Portlet(portletName, pageName, false);
	}

	private static Group _getGroupForSite(long companyId, long userId)
		throws Exception {

		Group group;
		String name = "Workspace Test Site";

		try {
			group = GroupLocalServiceUtil.getGroup(companyId, name);
		}
		catch (NoSuchGroupException nsge) {
			group = ServiceUtil.addActiveOpenGroup(userId, name);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Setting up site name=[" + group.getName() +
					"] publicLayouts=[" + group.hasPublicLayouts() + "]");
		}

		return group;
	}

	private static Layout _getPortalPageLayout(
			long userId, long groupId, String portalPageName)
		throws Exception {

		Layout portalPageLayout = null;

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, true);
		for (Layout layout : layouts) {
			if (layout.getName(Locale.US).equals(portalPageName)) {
				System.out.println("_getPortalPageLayout: found layout using LayoutLocalServiceUtil.getLayouts(groupId, true) ...");
				portalPageLayout = layout;
			}
		}

		layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);

		for (Layout layout : layouts) {
			if (layout.getName(Locale.US).equals(portalPageName)) {
				System.out.println("_getPortalPageLayout: found layout using LayoutLocalServiceUtil.getLayouts(groupId, false) ...");
				portalPageLayout = layout;
			}
		}

		if (portalPageLayout == null) {
			long parentLayoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;
			String type = LayoutConstants.TYPE_PORTLET;
			String friendlyURL = "/" + StringUtil.toLowerCase(portalPageName);

			portalPageLayout = ServiceUtil.addLayout(
				userId, groupId, false, parentLayoutId, portalPageName,
				portalPageName, portalPageName, type, false, friendlyURL);
			System.out.println("_getPortalPageLayout: added layout using ServiceUtil.addLayout ...");
		}

		return portalPageLayout;
	}

	private static void _setupPage(
			long userId, long groupId, PortalPage portalPage,
			String pageName)
		throws Exception {

		Bundle[] bundles = BundleUtil.getBundles();

		String portalPageName = portalPage.getPageName();

		if (_log.isDebugEnabled()) {
			_log.debug("setupPage: portalPageName = " + portalPageName);
		}

		List<Portlet> portlets = portalPage.getPortlets();
		Layout portalPageLayout = _getPortalPageLayout(
			userId, groupId, portalPageName);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)portalPageLayout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(
			userId, portalPage.getLayoutTemplateId(), false);

		int columnNumber = 1;

		List<String> addedPortletIds = new ArrayList<>();

		for (Portlet portlet : portlets) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"setupPage: searching for servetContextName = " +
						pageName);
			}

			long bundleId = 0L;
			int bundleState = Bundle.UNINSTALLED;

			for (Bundle bundle : bundles) {
				String symbolicName = bundle.getSymbolicName();

				// TODO remove bundle symbolicName magic.

				if (symbolicName.startsWith(pageName)) {
					bundleId = bundle.getBundleId();
					bundleState = bundle.getState();
				}
			}

			String portletName = portlet.getPortletName();

			if (_log.isDebugEnabled()) {
				_log.debug("setupPage:  final bundleId = " + bundleId);
			}

			if (bundleId > 0) {
				if (bundleState == Bundle.ACTIVE) {
					_addPortletsToPage(portletName, portlet, pageName, layoutTypePortlet, userId, columnNumber, addedPortletIds, false);
				}
				else {
					_log.error(
						"Unable to add portletName=[" + portletName +
							"] since bundle =[" + pageName +
								"] is not active.");
				}
			}
			else {
				_log.info("There is no BundleName=[" + pageName + "] that is deployed ...");
				_addPortletsToPage(portletName, portlet, pageName, layoutTypePortlet, userId, columnNumber, addedPortletIds, true);
			}

			// columnNumber++;

		}

		LayoutLocalServiceUtil.updateLayout(portalPageLayout);

//		if (_log.isDebugEnabled()) {
//			_log.debug("setupPage: groupId = " + groupId);
//			_log.debug(
//				"setupPage: portalPageLayout.getLayoutId() = " +
//					portalPageLayout.getLayoutId());
//			_log.debug("setupPage: themeId = portlettck_WAR_potlettcktheme");
//			_log.debug(
//				"setupPage: portalPageLayout.getColorSchemetId() = " +
//					portalPageLayout.getColorSchemeId());
//			_log.debug("setupPage: css = ");
//			_log.debug(
//				"setupPage: LayoutLocalServiceUtil.updateLookAndFeel ...");
//		}
//
//		LayoutSetLocalServiceUtil.updateLookAndFeel(
//			groupId, true, "portlettck_WAR_portlettcktheme",
//			portalPageLayout.getColorSchemeId(), null);

		int totalAddedPortlets = addedPortletIds.size();

		if (totalAddedPortlets == 0) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Setting up page name=[" + portalPageName +
						"] without any portlets");
			}
		}
		else if (totalAddedPortlets == 1) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Setting up page name=[" + portalPageName +
						"] with portlet=" +
							addedPortletIds.toString());
			}
		}
		else {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Setting up page name=[" + portalPageName +
						"] with portlets=" +
							addedPortletIds.toString());
			}
		}
	}
	
	private static void _addPortletsToPage(String portletName, Portlet portlet, String pageName, LayoutTypePortlet layoutTypePortlet, long userId, int columnNumber, List<String> addedPortletIds, boolean usePortletName) {
		
		// Apparently newer style portlets using the @Component annotation gets a portletId as follows ...
		String portletId = portletName + portlet.getInstanceToken();
		System.err.println("_addPortletsToPage: portletId = " + portletId);
		com.liferay.portal.kernel.model.Portlet p = PortletLocalServiceUtil.getPortletById(portletId);
		
		if (p == null) {
//			System.err.println("_setupPage: could not get Portlet by portletId = " + portletId + " ... p == null");
			
			// But a normal old WAR gets a portletId with _WAR_ in it as below
			if (portlet.getArtifactType() == Portlet.ArtifactType.WAB) {
				portletId = portletName + portlet.getInstanceToken();
			}
			else {
				String noDashPortletName = portletName.replaceAll(
					"[-]", "");

				String name = pageName;
				if (usePortletName) {
					name = portletName;
				}
				String warContext = "_WAR_" + name;

				portletId =
					noDashPortletName +
						warContext.replaceAll("[.]", "") +
							portlet.getInstanceToken();
			}
		} else {
			// newer style portlets using the @Component annotation ... we already have the portletId
			String instanceId = p.getInstanceId();
			System.err.println("_addPortletsToPage: instanceId = " + instanceId);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"adding portletId=[" + portletId +
					"] portletName=[" + portletName + "] ...");
		}

		try {
			_addPortlet(
				layoutTypePortlet, userId, columnNumber, portletId);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// not needed for Liferay 7
		// storePortletPreferences(portalPageLayout, portletId);

		addedPortletIds.add(portletId);
	}

	/**
	 * This method sets up the {@link PermissionChecker} {@link ThreadLocal}
	 * prior to performing additional test setup.
	 */
	private static void _setupPermissionChecker(long companyId)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug("setupPermissionChecker: companyId = " + companyId);
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			Role administratorRole = RoleLocalServiceUtil.getRole(
				companyId, RoleConstants.ADMINISTRATOR);

			User administratorUser = UserLocalServiceUtil.getRoleUsers(
				administratorRole.getRoleId()).get(0);

			try {
				permissionChecker = PermissionCheckerFactoryUtil.create(
					administratorUser);

				PermissionThreadLocal.setPermissionChecker(permissionChecker);
			}
			catch (Exception e) {
				throw new SystemException(e);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(Setup.class);

}