package ru.inrecolan.liferay.organization.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import ru.inrecolan.liferay.organization.model.Department;
import ru.inrecolan.liferay.organization.model.Manager;


import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;

public class OrganizationChartAction extends MVCPortlet {
	
	public static final String MANAGER_SCREEN_NAME = "managerScreenName";
	public static final String MANAGER_WITH_EMPLOYEES = "managerWithEmployess";
	public static final String MANAGER_ATTR = "manager";
	
	public static final String MAIN_MANAGER_COLOR_INDEX = "4";
	
	private long companyId = 0L;
	
	Log _log = LogFactoryUtil.getLog(this.getClass());	
	
	@Override
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws IOException, PortletException {
		
		// Получаем идентификатор организации
		ThemeDisplay themeDisplay = 
			     (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		companyId = themeDisplay.getCompanyId();
		
		// Получаем главного менеджера
		Manager manager = getMainManager();
		
		// Передаём список менеджеров на страницу
		renderRequest.setAttribute(MANAGER_WITH_EMPLOYEES, manager);
		renderRequest.setAttribute("employeeIndex", MAIN_MANAGER_COLOR_INDEX);
		
		super.doView(renderRequest, renderResponse);
	}
	
	// Возвращает главного менеджера и его подчинённых
	private Manager getMainManager() {
		Manager manager = null;
		List<User> users = getEmployes(StringPool.BLANK);
		if(users != null) {
			if(users.size() > 0) {
				User user = users.get(0);
				if(user != null) {
					manager = getManagerByScreenName(user.getScreenName());
				}
			}
		}
		return manager;
	}
	
	// Получаем менеджера и его подчинённых по экранному имени
	private Manager getManagerByScreenName(String screenName) {
		Manager manager = null;
		
		if(screenName != null) {
			User user = getUser(screenName);
			if(user != null) {
				manager = new Manager(user);
				manager.setEmployees(getEmployes(screenName));
				
				// Получаем отдел
				manager.setDepartment(getDepartment(user));
			}
		}
		return manager;
	}
		
	// Возвращает пользователя портала по его экранному имени
	private User getUser(String screenName) {
		User user = null;
		try {
			user = UserLocalServiceUtil.getUserByScreenName(companyId, screenName);
		} catch (PortalException e) {
			_log.error("Ошибка получения пользователя по экранному имени: " + e.getMessage());
		} catch (SystemException e) {
			_log.error("Ошибка получения пользователя по экранному имени: " + e.getMessage());
		}
		return user;
	}
	
	// Возвращает список пользователей для указанного менеджера
	private List<User> getEmployes(String managerScreenName) {
		List<User> employes = new ArrayList<User>();
		
		for (User user : getUsers()) {
			String managerAttr = getManagerSN(user);
			if(managerScreenName.equals(managerAttr)) {
				employes.add(user);
			}
		}
		return employes;
	}
	
	// Возвращает менеджера для указанного пользователя портала
	private String getManagerSN(User user) {
		String managerAttr = 
				(String) user.getExpandoBridge().getAttribute(MANAGER_ATTR);
		return managerAttr;
	}
	
	// Возвращает отдел, к которому относится пользователь
	private Department getDepartment(User user) {
		Department department = null;
		// Получаем отдел
		String departmentName = 
				(String)user.getExpandoBridge().getAttribute("department");
		if(departmentName != null) {
			if(!departmentName.isEmpty()) {
				
				// Получаем менеджера
				String managerSN = getManagerSN(user);
				Manager manager = getManagerByScreenName(managerSN);
				
				if(isMainManager(manager.getManager())) {
					// У главного руководителя нет отдела
					department = new Department(departmentName, user.getScreenName());
				} else {
					// Запоминаем отдел, в котором работает менеджер
					department = new Department(departmentName, managerSN);
				}
			}
		}
		return department;
	}
	
	// Определяет, является ли указанный пользователь главным руководителем
	private boolean isMainManager(User user) {
		boolean isMainManager = false;
		// Получаем имя менеджера
		String managerAttr = getManagerSN(user);
		
		if(managerAttr == null) {
			isMainManager = true;
		} else {
			if(managerAttr.isEmpty()) {
				isMainManager = true;
			}
		}
		return isMainManager;
	}
	
	// Возвращает список всех пользователей портала
	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();

		try {
			users = UserLocalServiceUtil.getUsers(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			
			// Получаем пользователя по умолчанию
			User defaultUser = UserLocalServiceUtil.getDefaultUser(companyId);
			// Удаляем его
			users.remove(defaultUser);
			
		} catch (SystemException e) {
			_log.error("Ошибка получения списка пользователей: " + e.getMessage());
		} catch (PortalException e) {
			_log.error("Ошибка получения количества пользователей или " +
					"пользователя по умолчанию:" + e.getMessage());
		}
		
		return users; 
	}
	
	// Отображает детальную информацию о сотрудниках
	public void details(ActionRequest actionRequest, ActionResponse actionResponse) 
			throws IOException, PortletException {
		
	    String employeeSN = actionRequest.getParameter("employeeSN");
	    
	    // Получаем менеджера
	    Manager manager = getManagerByScreenName(employeeSN);
		// Передаём список менеджеров на страницу
	    actionRequest.setAttribute(MANAGER_WITH_EMPLOYEES, manager);
	    
		
	    actionRequest.setAttribute("employeeIndex", 
	    		actionRequest.getParameter("employeeIndex"));
	    actionResponse.setRenderParameter("jspPage", "/html/details.jsp");
	}
	
	public void home(ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {
		
		// Получаем главного менеджера
		Manager manager = getMainManager();
		
		// Передаём список менеджеров на страницу
		actionRequest.setAttribute(MANAGER_WITH_EMPLOYEES, manager);
		actionRequest.setAttribute("employeeIndex", MAIN_MANAGER_COLOR_INDEX);	
		
		actionResponse.setRenderParameter("jspPage", "/html/view.jsp");
	}
}
