package com.revature.sheila.proj.bankapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;



@SuppressWarnings("unchecked")
public class BankAppService<T> {
	final static Logger logger = Logger.getLogger(BankAppService.class);

	
	public final FileManipulation<T> fileManipulation= new FileManipulation<>();
	public static final String topLevelFolderPath = "bank/registered-users";
	public static final String registeredUsersFileName = "users.txt";
	public static final String existingAccountNumbersFile = "existing-acc.txt";
	public static final String allAccountsFileName = "all-acc.txt";
	
	public List<T> getDeserializedList(String filePath){
		List<T> deSerializedObject = fileManipulation.readFromFile(filePath);
		return deSerializedObject;
	}
	
	public String register(String username, String password) {
		File userFile = createFileIfNotExists(topLevelFolderPath, registeredUsersFileName);
		
		List<RegisteredUser> registeredUsers = (List<RegisteredUser>) getDeserializedList(userFile.getPath());
		
		for (RegisteredUser ru : registeredUsers) {
			if (username.equalsIgnoreCase(ru.getUsername())) {
				return "User already exists. Try logging in.";
			}
		}

		// if it comes here, meaning username is not registered. So, registering user
		RegisteredUser newUser = new RegisteredUser(username, password);
		registeredUsers.add(newUser);

		// since we need to have functionality to add and remove user, we will go ahead and freshly insert
		//everything in the file
		
		((FileManipulation<RegisteredUser>)fileManipulation).writeToFile(registeredUsers, userFile.getPath());

		return "Successfully registered. Please log in";
	}

	private File createFileIfNotExists(String folder, String fileName) {
		String[] fileNames = fileName.split("/");
		for(String file: fileNames){
			if(!file.contains(".txt")){
				folder = folder + "/" + file;
				File folderTocreate = new File(folder);
				if(!folderTocreate.exists()){
					folderTocreate.mkdir();
				}
			}
			else{
				fileName = file;
			}
		}
		File userFile = new File(folder + "/" + fileName);

		if (!userFile.exists()) {
			try {
				userFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return userFile;
	}
	
	public RegisteredUser login(String username, String password){
		File userFile = createFileIfNotExists(topLevelFolderPath, registeredUsersFileName);
		
		List<RegisteredUser> registeredUsers = (List<RegisteredUser>) getDeserializedList(userFile.getPath());
		for (RegisteredUser ru : registeredUsers) {
			if (username.equalsIgnoreCase(ru.getUsername()) && password.equals(ru.getPassword())) {
				return ru;
			}
		}
		return null;
	}
	
	public boolean hasExistingAccount(String username) {
		File userAccountFile = new File(topLevelFolderPath + "/" + username.toLowerCase() + "/CHECKING.txt");
		if (userAccountFile.exists()) {
			return true;
		}
		return false;
	}
	

	public String applyToOpenSingleAccount(UserInfo userInfo, double startingBalance){
		if(login(userInfo.getRegisteredUser().getUsername(), userInfo.getRegisteredUser().getPassword())==null){
			return "You Should register before applying for an account";
		}
		
		File userAccountFile = new File(topLevelFolderPath + "/" + userInfo.getRegisteredUser().getUsername().toLowerCase() + "/CHECKING.txt");
		if(userAccountFile.exists()){
			return "You already have an existing account with us. You cannot apply for another account with you as primary account holder";
		}
		
		String accountFileName = "applied.txt";
		Account acc = new Account();
		//acc.setAccountNumber(accountNumber);
		acc.setAccountType("CHECKING");
		acc.setBalance(startingBalance);
		acc.setUserInfo(userInfo);
		acc.setDeleted(false);
		acc.setApproved(false);
		acc.setJointAccount(false);
		acc.setJointUser(null);
		File accountFile = createFileIfNotExists(topLevelFolderPath, accountFileName);
		
		List<Account> appliedApplicationsForApproval = (List<Account>) getDeserializedList(accountFile.getPath());
		for(Account accounts : appliedApplicationsForApproval){
			String existingAppliedUsername = accounts.getUserInfo().getRegisteredUser().getUsername();
			String appliedUsername = userInfo.getRegisteredUser().getUsername();
			if(appliedUsername.equals(existingAppliedUsername)){
				return "You have an existing application pending for approval, cannot apply for new one until it is approved or denyed.";
			}
		}
		appliedApplicationsForApproval.add(acc);
		((FileManipulation<Account>)fileManipulation).writeToFile(appliedApplicationsForApproval, accountFile.getPath());
		
		return "Successfully Applied for Checking account. Please wait until it gets approved.";
	}
	
	public String applyToOpenJointAccount(UserInfo userInfo, UserInfo jointUser, double startingBalance){
		if(login(userInfo.getRegisteredUser().getUsername(), userInfo.getRegisteredUser().getPassword())==null){
			return "You Should register before applying for an account";
		}
		
		File userAccountFile = new File(topLevelFolderPath + "/" + userInfo.getRegisteredUser().getUsername().toLowerCase() + "/CHECKING.txt");
		if(userAccountFile.exists()){
			return "You already have an existing account with us. You cannot apply for another account with you as primary account holder";
		}
		
		String accountFileName = "applied.txt";
		Account acc = new Account();
		//acc.setAccountNumber(accountNumber);
		acc.setAccountType("CHECKING");
		acc.setBalance(startingBalance);
		acc.setUserInfo(userInfo);
		acc.setDeleted(false);
		acc.setApproved(false);
		if(jointUser != null){
			acc.setJointAccount(true);
			acc.setJointUser(jointUser);
		}
		File accountFile = createFileIfNotExists(topLevelFolderPath, accountFileName);
		
		List<Account> appliedApplicationsForApproval = (List<Account>) getDeserializedList(accountFile.getPath());
		for(Account accounts : appliedApplicationsForApproval){
			String existingAppliedUsername = accounts.getUserInfo().getRegisteredUser().getUsername();
			String appliedUsername = userInfo.getRegisteredUser().getUsername();
			if(appliedUsername.equals(existingAppliedUsername)){
				return "You have an existing application pending for approval, cannot apply for new one until it is approved or denyed.";
			}
		}
		appliedApplicationsForApproval.add(acc);
		((FileManipulation<Account>)fileManipulation).writeToFile(appliedApplicationsForApproval, accountFile.getPath());
		
		return "Successfully Applied for Joint Checking account. Please wait until it gets approved.";
	}
	
	private long getIdValue() {
		long accNumber = 0L;
		File existingAccounts = createFileIfNotExists(topLevelFolderPath, existingAccountNumbersFile);

		List<Long> accountNumbers = (List<Long>) getDeserializedList(existingAccounts.getPath());
		if(accountNumbers.isEmpty()){
			accNumber = 10001;
		}else{
			Collections.sort(accountNumbers);
			long lastValue = accountNumbers.get(accountNumbers.size()-1);
			accNumber = lastValue + 1;
		}
		return accNumber;
	}

	public boolean removeRegisteredUser(String username, String password) {
		File userFile = createFileIfNotExists(topLevelFolderPath, registeredUsersFileName);
		
		List<RegisteredUser> registeredUsers = (List<RegisteredUser>) getDeserializedList(userFile.getPath());
		int pos = 0;
		boolean isfound = false;
		for (RegisteredUser ru : registeredUsers) {
			if (username.equals(ru.getUsername())) {
				isfound = true;
				break;
			}
			pos++;
		}
		if(isfound){
			registeredUsers.remove(pos);
			((FileManipulation<RegisteredUser>)fileManipulation).writeToFile(registeredUsers, userFile.getPath());
		}
		
		return isfound;
	}
	
	private void upadateAllAccountsInfo(Account account, long accountNumber, String username, String accountFileForUser) {
		File allAccountFile = createFileIfNotExists(topLevelFolderPath, allAccountsFileName);
		List<AllAccounts> appliedApplicationsForApproval = (List<AllAccounts>) getDeserializedList(allAccountFile.getPath());
		
		AllAccounts allAccounts = new AllAccounts();
		allAccounts.setAccountNumber(accountNumber);
		allAccounts.setAccountType(account.getAccountType());
		allAccounts.setUsername(username);
		allAccounts.setPath(accountFileForUser);
		
		appliedApplicationsForApproval.add(allAccounts);
		((FileManipulation<AllAccounts>)fileManipulation).writeToFile(appliedApplicationsForApproval, (topLevelFolderPath+"/"+allAccountsFileName));
	}
	
	private void updateExistingAccountsFile(long accountNumber) {
		File existingAccountFile = createFileIfNotExists(topLevelFolderPath, existingAccountNumbersFile);
		List<Long> appliedApplicationsForApproval = (List<Long>) getDeserializedList(existingAccountFile.getPath());
		appliedApplicationsForApproval.add(accountNumber);
		((FileManipulation<Long>)fileManipulation).writeToFile(appliedApplicationsForApproval, (topLevelFolderPath+"/"+existingAccountNumbersFile));
	}
	
	
	
	/* -----------------Bank transactions------------------------ */
	protected Account deposit(double d, Account a){
		double currentBalance = a.getBalance();
		currentBalance += d;
		a.setBalance(currentBalance);
		logger.info("Amount: " + d + " deposited to accountNumber: " + a.getAccountNumber() + ", currentBalance is: " + currentBalance);
		return a;
	}
	
	protected boolean isValidAmt(double d){
		return (d>0) ? true : false;
	}
	
	protected Account withdraw(double d, Account a){
		double currentBalance = a.getBalance();
		currentBalance -= d;
		a.setBalance(currentBalance);
		logger.info("Amount: " + d + " withdrawn from accountNumber: " + a.getAccountNumber() + ", currentBalance is: " + currentBalance);
		return a;
	}
	
	protected List<Account> transfer(double d, Account fromAccount, Account toAccount){
		List<Account> result = new ArrayList<>();
		double fromAccRemainingBalance = fromAccount.getBalance() - d;
		fromAccount.setBalance(fromAccRemainingBalance);
		
		double toAccRemainingBalance = fromAccount.getBalance() + d;
		toAccount.setBalance(toAccRemainingBalance);
		
		
		result.add(fromAccount); //fromAccount at pos 0
		result.add(toAccount); //toAccount at pos 1
		logger.info("Amount: " + d + " has been transfered from accountNumber: " + fromAccount.getAccountNumber() + "to accountNumber: " + toAccount);
		
		return result;
	}

	public String depositWrap(String username, double d) {
		if(isValidAmt(d)){
			String accountFile = username.toLowerCase() + "/CHECKING.txt";
			File thisAccountFile = createFileIfNotExists(topLevelFolderPath, accountFile);
			List<Account> appliedApplicationsForApproval = (List<Account>) getDeserializedList(thisAccountFile.getPath());
			
			//assume there is only one account per registered user
			Account a = appliedApplicationsForApproval.get(0);
			if(!a.isDeleted()){
				a = deposit(d, a);
				((FileManipulation<Account>)fileManipulation).writeToFile(Arrays.asList(a), thisAccountFile.getPath());
				return "Amount successfully deposited. CurrentBalance is: " + a.getBalance();
			}
			return "Account is already deleted!";
		}
		return "Deposit amount is not Valid!";
	}
	
	
	public String transferWrap(double d, String fromUsername, String toUsername) {
		if(isValidAmt(d)){
			
			String accountFileToUser = toUsername.toLowerCase() + "/CHECKING.txt";
			File thisAccountFileToUser = createFileIfNotExists(topLevelFolderPath, accountFileToUser);
			List<Account> appliedApplicationsForApprovalToUser = (List<Account>) getDeserializedList(thisAccountFileToUser.getPath());
			if(appliedApplicationsForApprovalToUser.isEmpty()){
				return "Account to transfer does not exists. Please verify the info and retry.";
			}
			
			String accountFile = fromUsername.toLowerCase() + "/CHECKING.txt";
			File thisAccountFile = createFileIfNotExists(topLevelFolderPath, accountFile);
			List<Account> appliedApplicationsForApproval = (List<Account>) getDeserializedList(thisAccountFile.getPath());
			
			//assume there is only one account per registered user
			Account fromAcc = appliedApplicationsForApproval.get(0);
			Account toAcc = appliedApplicationsForApprovalToUser.get(0);
			if((!fromAcc.isDeleted() && fromAcc.getBalance() >= d) && !toAcc.isDeleted()){
				List<Account> transferList = transfer(d, fromAcc, toAcc);
				fromAcc = transferList.get(0);
				toAcc = transferList.get(1);
				((FileManipulation<Account>)fileManipulation).writeToFile(Arrays.asList(fromAcc), thisAccountFile.getPath());
				((FileManipulation<Account>)fileManipulation).writeToFile(Arrays.asList(toAcc), thisAccountFileToUser.getPath());
				return "Amount successfully transfered.";
			}
			return "Either fromAccount balance is less than transfer request amount or accounts may be deleted!";
		}
		return "Transfer amount is not Valid!";
	}
	
	
	public String withdrawWrap(String username, double d) {
		if(isValidAmt(d)){
			String accountFile = username.toLowerCase() + "/CHECKING.txt";
			File thisAccountFile = createFileIfNotExists(topLevelFolderPath, accountFile);
			List<Account> appliedApplicationsForApproval = (List<Account>) getDeserializedList(thisAccountFile.getPath());
			
			//assume there is only one account per registered user
			Account a = appliedApplicationsForApproval.get(0);
			if(!a.isDeleted() && a.getBalance() >= d){
				a = withdraw(d, a);
				((FileManipulation<Account>)fileManipulation).writeToFile(Arrays.asList(a), thisAccountFile.getPath());
				return "Amount successfully withdrawn. CurrentBalance is: " + a.getBalance();
			}
			return "Either account balance is less than withdraw request amount or account is deleted!";
		}
		return "Deposit amount is not Valid!";
	}
	
	
	/*---------------Bank Employee Actions ----------------------*/
	public String approveOrDenyAccountOpening(int index, boolean isApproved){
		String accountFileName = "applied.txt";
		File accountFile = createFileIfNotExists(topLevelFolderPath, accountFileName);

		List<Account> appliedApplicationsForApproval = (List<Account>) getDeserializedList(accountFile.getPath());
		Account account = appliedApplicationsForApproval.get(index - 1);
		
		if(isApproved){
			long accountNumber = getIdValue();
			account.setAccountNumber(accountNumber);
			account.setApproved(true);
			account.setJointAccount(false);
			
			String username = account.getUserInfo().getRegisteredUser().getUsername();
			//String profileFileName = username + "/" + "profile.txt";
			String accountFileForUser = username.toLowerCase() + "/" + account.getAccountType()+".txt";
			File userAccountFile=createFileIfNotExists(topLevelFolderPath, accountFileForUser);
			//((FileManipulation<Account>)fileManipulation).writeToFile(Arrays.asList(account), userAccountFile.getPath());
			((FileManipulation<Account>)fileManipulation).writeToFile(Arrays.asList(account), userAccountFile.getPath());

			updateExistingAccountsFile(accountNumber);
			upadateAllAccountsInfo(account, accountNumber, username, accountFileForUser);
			
			appliedApplicationsForApproval.remove(index - 1);
			((FileManipulation<Account>)fileManipulation).writeToFile(appliedApplicationsForApproval, accountFile.getPath());
			return "Successfully approved account";

		}
		return "Successfully Denied account";

	}
	
	public String viewPendingAccountOpeiningRequest(){
		String accountFileName = "applied.txt";
		File accountFile = createFileIfNotExists(topLevelFolderPath, accountFileName);
		
		List<Account> appliedApplicationsForApproval = (List<Account>) getDeserializedList(accountFile.getPath());
		if(appliedApplicationsForApproval.isEmpty()){
			return "No pending applications to approve.";
		}
		
		int i = 0;
		System.out.println("------------------------Pending Approvals--------------------");
		for(Account accounts : appliedApplicationsForApproval){
			System.out.println(++i + ". " + accounts.toString());
		}
		System.out.println("--------------------------------------------------------");
		return "Please select index number to approve/deny application opening request. or press -1 to view main menu.";
	}
	
	public List<AllAccounts> viewAllAccounts(){
		File allAccountFile = createFileIfNotExists(topLevelFolderPath, allAccountsFileName);
		List<AllAccounts> allAccounts = (List<AllAccounts>) getDeserializedList(allAccountFile.getPath());
		
		int i = 0;
		System.out.println("------------------------All Accounts--------------------");
		for(AllAccounts accounts : allAccounts){
			System.out.println(++i + ". " + accounts.toString());
		}
		System.out.println("--------------------------------------------------------");
		return allAccounts;
	}
	
	public Account loadAccount(int index, List<AllAccounts> allAccounts){
		AllAccounts accounts = allAccounts.get(index - 1);
		File thisAccountFile = createFileIfNotExists(topLevelFolderPath, accounts.getUsername()+"/CHECKING.txt");
		List<Account> loadedAccount = (List<Account>) getDeserializedList(thisAccountFile.getPath());
		return loadedAccount.get(0);
	}
	
	public String cancelAccount(Account a){
		if(a.getBalance() > 0d){
			return "Please withdraw all amount before cancelling the account";
		}
	
		//marking deleted in Account file
		String username = a.getUserInfo().getRegisteredUser().getUsername();
		File accountFile = createFileIfNotExists(topLevelFolderPath, username.toLowerCase()+"/CHECKING.txt");
		List<Account> referenceAccounts = (List<Account>) getDeserializedList(accountFile.getPath());
		Account reference = referenceAccounts.get(0);
		reference.setDeleted(true);
		
		((FileManipulation<Account>)fileManipulation).writeToFile(referenceAccounts, (topLevelFolderPath + "/" + username.toLowerCase()+"/CHECKING.txt"));

		//renaming file to CHECKING_DELETED
		accountFile.renameTo(new File(topLevelFolderPath + "/" + username.toLowerCase()+"/CHECKING_DELETED_" + reference.getAccountNumber() +".txt"));
		accountFile.delete();
		//removing from all-acc.txt file
		File allAccountFile = createFileIfNotExists(topLevelFolderPath, allAccountsFileName);
		List<AllAccounts> allAccounts = (List<AllAccounts>) getDeserializedList(allAccountFile.getPath());
		
		int i = 0;
		for(AllAccounts accounts : allAccounts){
			if(accounts.getAccountNumber() == a.getAccountNumber()){
				break;
			}
			i++;
		}
		
		allAccounts.remove(i);
		((FileManipulation<AllAccounts>)fileManipulation).writeToFile(allAccounts, (topLevelFolderPath+"/"+allAccountsFileName));

		return "Successfully cancelled the account";

	}

	protected Account getAccountForUser(String username) {
		File accountFile = createFileIfNotExists(topLevelFolderPath, username.toLowerCase()+"/CHECKING.txt");
		List<Account> referenceAccounts = (List<Account>) getDeserializedList(accountFile.getPath());
		Account reference = referenceAccounts.get(0);
		return reference;
	}

	public void displayUserProfile(Account a) {
		System.out.println("-----------User Profile-----------");
		System.out.println("Username: " + a.getUserInfo().getRegisteredUser().getUsername());
		System.out.println("Name: " + a.getUserInfo().getFirstName() +" "+ a.getUserInfo().getLastName());
		System.out.println("Email: " + a.getUserInfo().getEmail());
		System.out.println("Address: " + a.getUserInfo().getAddress());
		System.out.println("--------------------------------------------------------");
	}

	public void displayBalance(Account a) {
		System.out.println("---------Account Information--------");
		System.out.println("Account Number: " + a.getAccountNumber());
		System.out.println("Account Type: " + a.getAccountType());
		System.out.println("Account Balance: " + a.getBalance());
		System.out.println("--------------------------------------------------------");
	}

	public boolean isValidBankAdmin(String username, String password) {
		if(username.equals("bankadmin") && password.equals("admin123")){
			return true;
		}
		return false;
	}

	public boolean isValidBankEmployee(String username, String password) {
		if(username.equals("bankemp") && password.equals("emp123") || isValidBankAdmin(username, password)){
			return true;
		}
		return false;
	}
}
