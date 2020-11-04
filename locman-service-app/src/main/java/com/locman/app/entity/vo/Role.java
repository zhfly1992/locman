package com.locman.app.entity.vo;

public class Role {

	private String	roleId;			// 岗位（角色）id
	private String	roleName;		// 岗位（角色）名称
	private String	organizedName;	// 组织名称
	private String	organizeId;



	public String getRoleId() {
		return roleId;
	}



	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}



	public String getRoleName() {
		return roleName;
	}



	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}



	public String getOrganizedName() {
		return organizedName;
	}



	public void setOrganizedName(String organizedName) {
		this.organizedName = organizedName;
	}



	public String getOrganizeId() {
		return organizeId;
	}



	public void setOrganizeId(String organizeId) {
		this.organizeId = organizeId;
	}

}
