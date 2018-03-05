package com.revature.sheila.proj.bankapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BankAppServiceTest {

	@SuppressWarnings("rawtypes")
	private static final BankAppService bankAppService = new BankAppService();
	private static final RegisteredUser registerUser = new RegisteredUser("Sheila", "Malla6");
	
	private static final RegisteredUser registerJointUser = new RegisteredUser("test", "p@ssM1264");
	private static UserInfo testUserInfo = new UserInfo();
	private static UserInfo testJointUser = new UserInfo();

	static{
		testUserInfo.setAddress("123 Park Street");
		testUserInfo.setEmail("ase@ed.com");
		testUserInfo.setFirstName("Par");
		testUserInfo.setLastName("Towson");
		
		testUserInfo.setAddress("123 Park Street");
		testUserInfo.setEmail("asmpee@ed.com");
		testUserInfo.setFirstName("sam");
		testUserInfo.setLastName("Towson");
	}
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testRegistrationSuccess(){
		assertEquals("Successfully registered. Please log in", bankAppService.register("testAcc", "p@ssM1264"));
		bankAppService.removeRegisteredUser("testAcc", "p@ssM1264");
	}
	
	@Test
	public void testExistingUserForRegistration(){
		bankAppService.register("testAcc", "p@ssM1264"); //registering user to test
		assertEquals("User already exists. Try logging in.", bankAppService.register("testAcc", "p@ssM1264"));
		bankAppService.removeRegisteredUser("testAcc", "p@ssM1264"); // removing registered user in previous line

	}
	
	@Test
	public void testLoginWithValidCredentials(){
		assertTrue(bankAppService.login("sheilamalla6", "sheilamalla")!=null);
	}
	
	@Test
	public void testLoginWithInvalidCredentials(){
		assertFalse(bankAppService.login("Sheiela", "p@ssM1264") != null);
	}
	
	@Ignore
	public void testApplyToOpenSingleAccountWithRegisteredUser(){
		testUserInfo.setRegisteredUser(registerUser);
		assertEquals("Successfully Applied for Checking account.", bankAppService.applyToOpenSingleAccount(testUserInfo, 0.0d));
	}
	
	@Test
	public void testApplyToOpenSingleAccountWithNotRegisteredUser(){
		testUserInfo.setRegisteredUser(new RegisteredUser("Sabidh", "Shakya"));
		assertEquals("You Should register before applying for an account", bankAppService.applyToOpenSingleAccount(testUserInfo, 0.0d));
	}
	
	@Ignore
	public void testApplyToOpenSingleAccountWhenOneIsPending(){
		testUserInfo.setRegisteredUser(registerUser);
		assertEquals("You have an existing application pending for approval, cannot apply for new one until it is approved or denyed.", bankAppService.applyToOpenSingleAccount(testUserInfo, 0.0d));
	}
	
	@Test
	public void testApplyToOpenSingleAccountWhenAlreadyHaveAnExistingAccount(){
		testUserInfo.setRegisteredUser(new RegisteredUser("sheilamalla6", "sheilamalla"));
		assertEquals("You already have an existing account with us. You cannot apply for another account with you as primary account holder", bankAppService.applyToOpenSingleAccount(testUserInfo, 0.0d));
	}

	
	@Ignore
	public void testApplyToOpenJointAccountWithRegisteredUser(){
		testUserInfo.setRegisteredUser(registerUser);
		testJointUser.setRegisteredUser(registerJointUser);
		assertEquals("Successfully Applied for Checking account.", bankAppService.applyToOpenJointAccount(testUserInfo, testJointUser, 0.0d));
	}
	
	@Ignore
	public void testApplyToOpenJointAccountWithNotRegisteredUser(){
		testUserInfo.setRegisteredUser(new RegisteredUser("Sabidha", "Shakyaaa"));
		testUserInfo.setRegisteredUser(registerUser);
		testJointUser.setRegisteredUser(registerJointUser);
		assertEquals("You Should register before applying for an account", bankAppService.applyToOpenJointAccount(testUserInfo, testJointUser, 0.0d));
	}
	
	@Ignore
	public void testApplyToOpenJointAccountWhenOneIsPending(){
		testUserInfo.setRegisteredUser(registerUser);
		testJointUser.setRegisteredUser(registerJointUser);
		assertEquals("You have an existing application pending for approval, cannot apply for new one until it is approved or denyed.", bankAppService.applyToOpenJointAccount(testUserInfo, testJointUser, 0.0d));
	}
	
	@Ignore
	public void testApprovePendingApprovals(){
		bankAppService.viewPendingAccountOpeiningRequest();
		
		assertEquals("Successfully approved account", bankAppService.approveOrDenyAccountOpening(1, true));
		
	}
	
	

	/*******************************************************************
	 * Test Deposit/withdraw validation
	 ******************************************************************/
	@Test
	public void testValidAmount(){
		assertTrue(bankAppService.isValidAmt(3.0d));
		assertTrue(bankAppService.isValidAmt(0.4d));

	}
	
	@Test
	public void testInValidAmount(){
		assertFalse(bankAppService.isValidAmt(0.0d));
		assertFalse(bankAppService.isValidAmt(-3.0d));

	}
	
	@Ignore
	public void testWithdrawWrapSuccess(){
		assertEquals("Amount successfully withdrawn. CurrentBalance is: 10.0", bankAppService.withdrawWrap(registerUser.getUsername(),10d));
	}
	
	
	@Test
	public void testWithdraFromAccountBalance(){
		Account dummyAccount = new Account();
		dummyAccount.setBalance(30.0d);
		
		Account returnedAcc = bankAppService.withdraw(20.0, dummyAccount);
		assertEquals(10d, returnedAcc.getBalance(), 0);
	}
	
	@Test
	public void testDepositToAccountBalance(){
		Account dummyAccount = new Account();
		dummyAccount.setBalance(10.0d);
		
		Account returnedAcc = bankAppService.deposit(20.0, dummyAccount);
		assertEquals(30d, returnedAcc.getBalance(), 0);
	}
	
	@Ignore
	public void testWithdrawWrapWithInsufficientAccountBalance(){
		assertEquals("Either account balance is less than withdraw request amount or account is deleted!", bankAppService.withdrawWrap(registerUser.getUsername(),10d));
	}
	
	@Test
	public void testWithdrawWrapWithNegativeAmount(){
		assertEquals("Deposit amount is not Valid!", bankAppService.withdrawWrap(registerUser.getUsername(),-10d));
	}
	
}
