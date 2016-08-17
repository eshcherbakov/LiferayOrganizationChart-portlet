package ru.inrecolan.liferay.organization.model;

import java.util.List;

import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;

public class Manager {

	private User manager;
	
	// Подчиненённые менеджера
	private List<User> employees;
	private Department department;
	
	public Manager(User manager) {
		this.manager = manager;
	}
	
	public List<User> getEmployees() {
		return employees;
	}
	public void setEmployees(List<User> employees) {
		this.employees = employees;
	}

	public User getManager() {
		return manager;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

}
