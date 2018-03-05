package com.revature.sheila.proj.bankapp;

import java.io.Serializable;

public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UserInfo userInfo;
	private UserInfo jointUser;
	private long accountNumber;
	private String accountType;
	private double balance;
	private boolean approved;
	private boolean deleted;
	private boolean isJointAccount;

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public UserInfo getJointUser() {
		return jointUser;
	}

	public void setJointUser(UserInfo jointUser) {
		this.jointUser = jointUser;
	}

	public boolean isJointAccount() {
		return isJointAccount;
	}

	public void setJointAccount(boolean isJointAccount) {
		this.isJointAccount = isJointAccount;
	}

	@Override
	public String toString() {
		return String.format(
				"Account [userInfo=%s, jointUser=%s, accountNumber=%s, accountType=%s, balance=%s, approved=%s, deleted=%s, isJointAccount=%s]",
				userInfo, jointUser, accountNumber, accountType, balance, approved, deleted, isJointAccount);
	}

}
