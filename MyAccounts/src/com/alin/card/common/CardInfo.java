package com.alin.card.common;

public class CardInfo {

	private String name = "";
	private String account = "";
	private String password = "";
	private String remark = "";

	public CardInfo() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "name = " + name + ", account = " + account + ", password = "
				+ password + ", remark = " + remark;
	}

}
