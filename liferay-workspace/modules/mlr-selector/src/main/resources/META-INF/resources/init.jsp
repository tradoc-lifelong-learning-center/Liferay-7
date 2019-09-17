<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="mil.tjaglcs.mlrselector.portlet.MlrSelectorConfiguration" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
MlrSelectorConfiguration mlrSelectorConfiguration =
		(MlrSelectorConfiguration)
		renderRequest.getAttribute(MlrSelectorConfiguration.class.getName());
	String publicationName = StringPool.BLANK;
	String issueDisplay = StringPool.BLANK;
	if (Validator.isNotNull(mlrSelectorConfiguration)) {
		publicationName = portletPreferences.getValue("publicationName", mlrSelectorConfiguration.publicationName());
		issueDisplay = portletPreferences.getValue("issueDisplay", mlrSelectorConfiguration.issueDisplay());
	}
%>