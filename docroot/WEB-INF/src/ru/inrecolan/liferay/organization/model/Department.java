package ru.inrecolan.liferay.organization.model;

public class Department {
	
	private String name;
	private String managerSN;
	
	public Department(String name, String managerSN) {
		this.name = name;
		this.managerSN = managerSN;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getManagerSN() {
		return managerSN;
	}

	public void setManagerSN(String managerSN) {
		this.managerSN = managerSN;
	}
}
