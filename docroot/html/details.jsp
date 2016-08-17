<%@include file="/html/init.jsp" %>

<div id="organizationChart">
	<%
	Manager manager = 
		(Manager)renderRequest.getAttribute(OrganizationChartAction.MANAGER_WITH_EMPLOYEES);
	if(manager != null) {
		User employee = manager.getManager();
		%>
			<%@ include file="/html/logotip.jspf" %>
		
			<%@ include file="/html/breadcrumb.jspf" %>
			
			<%@ include file="/html/manager.jspf" %>
		
		<div id="employees">
		<%

			List<User> employees = manager.getEmployees();
			if(employees != null) {
				for(int i = 0; i < employees.size(); i++) {
					employee = employees.get(i); 
					%>
						<portlet:actionURL var="detailsURL" name="details">
							<portlet:param name="employeeSN" value="<%= employee.getScreenName() %>" />
							<portlet:param name="employeeIndex" value="<%= String.valueOf(index) %>" />
						</portlet:actionURL>
						
						<div class="worker employee employee<%= index %>">
							<a href="<%= detailsURL %>">
									
								<div class="portrait">
									<img src="<%= employee.getPortraitURL(themeDisplay) %>" />
								</div>							
								<%@ include file="/html/fullInfo.jspf" %>
								<div class="space">
								</div>													
							</a>
						</div>
					<%
				}
			}
		%>
		</div>		
		<%
	}
	%>

</div>