<%@page import="com.liferay.portal.model.Company"%>
<%@page import="com.liferay.portal.model.Phone"%>
<%@page import="ru.inrecolan.liferay.organization.model.Manager"%>
<%@page import="ru.inrecolan.liferay.organization.action.OrganizationChartAction"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.RenderRequest"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
	int index = 3;
	String employeeIndex = (String)renderRequest.getAttribute("employeeIndex");
	if(employeeIndex != null) {
		index = Integer.parseInt(employeeIndex) % 5;
	}
%>
