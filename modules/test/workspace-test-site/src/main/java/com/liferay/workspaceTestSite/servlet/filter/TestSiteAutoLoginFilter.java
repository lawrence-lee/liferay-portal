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

package com.liferay.workspaceTestSite.servlet.filter;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 * @author Vernon Singleton
 */
@Component(
	immediate = true,
	property = {
		"servlet-context-name=", "servlet-filter-name=Test Site Auto Login Filter",
		"url-pattern=/*"
	},
	service = Filter.class
)
public class TestSiteAutoLoginFilter extends BasePortalFilter {

	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		// The portlet TCK has two tests named GetRemoteUserNullTestPortlet. One
		// tests an action request and the other tests a render request. Those
		// two tests assume that the current user is not authenticated. This
		// filter skips automatic authentication as a workaround for those two
		// tests.

		HttpSession httpSession = request.getSession();

		if (Boolean.TRUE.equals(httpSession.getAttribute(_SKIP_LOGIN))) {
			processFilter(
				TestSiteAutoLoginFilter.class.getName(), request, response,
				filterChain);

			return;
		}

		String[] portletIds = request.getParameterValues("portletName");

		if (portletIds != null) {
			for (String portlet : portletIds) {
				if (portlet.endsWith("GetRemoteUserNullTestPortlet")) {
					httpSession.setAttribute(_SKIP_LOGIN, Boolean.TRUE);

					processFilter(
						TestSiteAutoLoginFilter.class.getName(), request,
						response, filterChain);

					return;
				}
			}
		}

		User user = _userLocalService.fetchUserByEmailAddress(
			_portal.getCompanyId(request), "testSite@liferay.com");

		if (user != null) {
			request.setAttribute(WebKeys.USER_ID, user.getUserId());
		}

		processFilter(
			TestSiteAutoLoginFilter.class.getName(), request, response,
			filterChain);
	}

	@Reference(unbind = "-")
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	private static final String _SKIP_LOGIN = "SKIP_LOGIN";

	@Reference
	private Portal _portal;

	private UserLocalService _userLocalService;

}