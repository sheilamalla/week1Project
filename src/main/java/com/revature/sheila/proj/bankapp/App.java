package com.revature.sheila.proj.bankapp;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("rawtypes")
public class App implements Closeable {

	final static BankAppService service = new BankAppService();
	final static Scanner scan = new Scanner(System.in);
	final static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		while(true){
			displayMenu();
			System.out.println("Select 1 if you are customer, 2 if you are Bank employee or 3 if you are Bank Administration.");
			String selection;
			selection = scan.next();
			switch (selection) {
			case "1":
				launchCustomerMenu();
				break;
			case "2":
				launchBankEmployeeMenu();
				break;
			case "3":
				launchBankAdminMenu();
				break;
			case "4":
				System.out.println("***Closing application***");
				System.exit(0);
			default:
				System.out.println("Please enter a valid option."); 
				break;
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static void launchBankAdminMenu() {
		//Note: hardcoded username"bankadmin", password: "admin123" to use as admin.
		System.out.print("Enter username: ");
		String username = scan.next();
		System.out.print("Enter password: ");
		String password = scan.next();
		
		List<AllAccounts> allAccounts = new ArrayList<>();
		Account account = null;
		
		if(service.isValidBankAdmin(username, password)){
			boolean loggedIn=true;
			while(loggedIn){
				showMenuForBankAdmin();
				System.out.println("Enter an valid option from above:");
				String selectedService = scan.next();
				switch(selectedService){
				case "1":
					String statusR = service.viewPendingAccountOpeiningRequest();
					System.out.println(statusR);
					if(statusR.equals("No pending applications to approve.")){
						break;
					}
					int appIndex = scan.nextInt();
					if(appIndex == -1){
						break;
					}
					System.out.println("Press 1 to approve, 0 to Deny");
					int a = scan.nextInt();
					boolean isApproved = a==0 ? false : true;
					String appStatus = service.approveOrDenyAccountOpening(appIndex, isApproved);
					System.out.println(appStatus);
					break;
				case "2":
					allAccounts = service.viewAllAccounts();
					break;
				case "3":
					System.out.println("Enter index of the account to load it.");
					scan.nextLine();
					int index = scan.nextInt();
					if(allAccounts.size() < index){
						System.out.println("Invalid Index!");
						break;
					}
					account = service.loadAccount(index, allAccounts);
					if(account != null){
						System.out.println("Account Successfully Loaded!");
					}
					break;
				case "4":
					if(account == null){
						System.out.println("Please load an account to see Personal information");
					}else{
						service.displayUserProfile(account);
					}
					break;
				case "5":
					if(account == null){
						System.out.println("Please load an account to see Account balance");
					}else{
						service.displayBalance(account);
					}
					break;
				case "6":
					if(account == null){
						System.out.println("Please load an account to Perform action on it.");
					}else{
						System.out.print("Enter Amount to Deposit: ");
						double amt = 0d;
						try {
							amt = scan.nextDouble();
						} catch (InputMismatchException ex) {
							System.out.println("Invalid amount Entered!");
							break;
						}
						String status = service.depositWrap(account.getUserInfo().getRegisteredUser().getUsername(), amt);
						account = service.getAccountForUser(account.getUserInfo().getRegisteredUser().getUsername());
						System.out.println(status);
					}
					break;
					
				case "7":
					if(account == null){
						System.out.println("Please load an account to Perform action on it.");
					}else{
						System.out.print("Enter Amount to Withdraw: ");
						double amt1 = 0d;
						try {
							amt1 = scan.nextDouble();
						} catch (InputMismatchException ex) {
							System.out.println("Invalid amount Entered!");
							break;
						}
						String withDrawstatus = service.withdrawWrap(account.getUserInfo().getRegisteredUser().getUsername(), amt1);
						account = service.getAccountForUser(account.getUserInfo().getRegisteredUser().getUsername());
						System.out.println(withDrawstatus);
					}
					break;
				case "8":
					if(account == null){
						System.out.println("Please load an account to Perform action on it.");
					}else{
						System.out.print("Enter username of account to Transfer to: "); // this is just for simplification for now.
						scan.nextLine();
						String usernameToTransfer = scan.next();
						System.out.print("Enter Amount to Transfer: ");
						double amt1 = 0d;
						try {
							amt1 = scan.nextDouble();
						} catch (InputMismatchException ex) {
							System.out.println("Invalid amount Entered!");
							break;
						}
						String transferStatus = service.transferWrap(amt1, account.getUserInfo().getRegisteredUser().getUsername(), usernameToTransfer);
						account = service.getAccountForUser(account.getUserInfo().getRegisteredUser().getUsername());
						System.out.println(transferStatus);
					}
					break;
				case "9":
					if(account == null){
						System.out.println("Please load an account to Perform action on it.");
					}else{
						String statusC = service.cancelAccount(account);
						System.out.println(statusC);
						allAccounts = service.viewAllAccounts();
						account = null;
					}
					break;
				case "10":
					loggedIn = false;
					break;
				default:
					System.out.println("Please enter a valid option."); 
					break;
				}
			}
		}else{
			System.out.println("You Should be a Bank admin to use this section of app.");
		}
		
	}

	@SuppressWarnings("unchecked")
	private static void launchBankEmployeeMenu() {
		//Note: hardcoded username"bankemp", password: "emp123" to use as employee.
		System.out.print("Enter username: ");
		String username = scan.next();
		System.out.print("Enter password: ");
		String password = scan.next();
		List<AllAccounts> allAccounts = new ArrayList<>();
		Account account = null;

		if (service.isValidBankEmployee(username, password)) {
			boolean loggedIn=true;
			while(loggedIn){
				showMenuForBankEmployee();
				System.out.println("Enter an valid option from above:");
				String selectedService = scan.next();
				switch(selectedService){
				case "1":
					String statusR = service.viewPendingAccountOpeiningRequest();
					System.out.println(statusR);
					if(statusR.equals("No pending applications to approve.")){
						break;
					}
					int appIndex = scan.nextInt();
					if(appIndex == -1){
						break;
					}
					System.out.println("Press 1 to approve, 0 to Deny");
					int a = scan.nextInt();
					boolean isApproved = a==0 ? false : true;
					String appStatus = service.approveOrDenyAccountOpening(appIndex, isApproved);
					System.out.println(appStatus);
					break;
				case "2":
					allAccounts = service.viewAllAccounts();
					break;
				case "3":
					System.out.println("Enter index of the account to load it.");
					scan.nextLine();
					int index = scan.nextInt();
					if(allAccounts.size() < index){
						System.out.println("Invalid Index!");
						break;
					}
					account = service.loadAccount(index, allAccounts);
					if(account != null){
						System.out.println("Account Successfully Loaded!");
					}
					break;
				case "4":
					if(account == null){
						System.out.println("Please load an account to see Personal information");
					}else{
						service.displayUserProfile(account);
					}
					break;
				case "5":
					if(account == null){
						System.out.println("Please load an account to see Account balance");
					}else{
						service.displayBalance(account);
					}
					break;
				case "6":
					loggedIn = false;
					break;
				default:
					System.out.println("Please enter a valid option."); 
					break;
				}
			}
		} else {
			System.out.println("You Should be a Bank employee to use this section of app.");
		}
	}

	private static void launchCustomerMenu() {
		RegisteredUser loggedInUser = null;
		String selectedService;
		while (loggedInUser == null) {
			displayInitialMenu();
			System.out.println("Enter an valid option from above:");
			selectedService = scan.next();
			switch (selectedService) {
			case "1":
				System.out.print("Enter username: ");
				String username = scan.next();
				System.out.print("Enter password: ");
				String password = scan.next();
				// "if registered show success message"
				String registerationStatus = service.register(username, password);
				System.out.println(registerationStatus);
				break;

			case "2":
				System.out.println(
						"**********************************Entering loggin information**********************************");
				System.out.print("Enter your username: ");
				String username1 = scan.next();
				System.out.print("Enter your password: ");
				String password1 = scan.next();
				loggedInUser = service.login(username1, password1);
				if (loggedInUser == null)
					System.out.println("Invalid login!");
				else
					System.out.println("***********logged in successfully***********");
				break;

			default:
				System.out.println("Please select a valid option.");
			}
		}

		boolean check = true;
		while (check) {
			if (loggedInUser != null && !service.hasExistingAccount(loggedInUser.getUsername())) {
				displayApplyApplicationMenu();
				System.out.println("Enter an valid option from above:");
				scan.nextLine();
				selectedService = scan.next();
				switch (selectedService) {

				case "1":

					System.out.println(
							"**********************************Applying for Single Account**********************************");
					UserInfo userInfo = userInformationToApply(loggedInUser);
					String applyStatus = service.applyToOpenSingleAccount(userInfo, 0);
					System.out.println(applyStatus);
					if (applyStatus
							.equals("Successfully Applied for Checking account. Please wait until it gets approved.")) {
						check = false;
					}
					break;

				case "2":
					System.out.println(
							"**********************************Applying for Joint Account**********************************");
					UserInfo userInfo1 = userInformationToApply(loggedInUser);
					UserInfo jointUser = new UserInfo();
					Scanner scan1 = new Scanner(System.in);
					System.out.println("Enter information about joint(second) user below:");
					System.out.print("Enter your first name: ");
					String firstName2 = scan1.next();
					System.out.println("Enter your last name: ");
					String lastName2 = scan1.next();
					System.out.println("Enter your email: ");
					String email2 = scan1.next();
					System.out.println("Enter your address: ");
					String address2 = scan1.next();
					// setting user info for
					jointUser.setFirstName(firstName2);
					jointUser.setLastName(lastName2);
					jointUser.setEmail(email2);
					jointUser.setAddress(address2);
					jointUser.setRegisteredUser(loggedInUser);
					String applyJointStatus = service.applyToOpenJointAccount(userInfo1, jointUser, 0);
					System.out.println(applyJointStatus);
					if (applyJointStatus.equals(
							"Successfully Applied for Joint Checking account. Please wait until it gets approved.")) {
						check = false;
					}
					break;
				default:
					System.out.println("Please select a valid option.");
				}
			} else if(loggedInUser != null) {
				displayAccountMenu();
				System.out.println("Enter an valid option from above:");
				Account acc = service.getAccountForUser(loggedInUser.getUsername());
				selectedService = scan.next();
				switch (selectedService) {
				case "1":
					service.displayUserProfile(acc);
					break;
				case "2":
					service.displayBalance(acc);
					break;
				case "3":
					System.out.print("Enter Amount to Deposit: ");
					double amt = 0d;
					try {
						amt = scan.nextDouble();
					} catch (InputMismatchException ex) {
						System.out.println("Invalid amount Entered!");
						break;
					}
					String status = service.depositWrap(loggedInUser.getUsername(), amt);
					System.out.println(status);
					break;
				case "4":
					System.out.print("Enter Amount to Withdraw: ");
					double amt1 = 0d;
					try {
						amt1 = scan.nextDouble();
					} catch (InputMismatchException ex) {
						System.out.println("Invalid amount Entered!");
						break;
					}
					String withDrawstatus = service.withdrawWrap(loggedInUser.getUsername(), amt1);
					System.out.println(withDrawstatus);
					break;
				case "5":
					System.out.println(
							"Only bank admin can perform transferring for now. Please contact customer service.");
					break;
				case "6":
					scan.nextLine();
					loggedInUser = null;
					check = false;
					break;
							
				default:
					System.out.println("Please select a valid option.");
					break;
				}
			}
		}
		
	}

	private static void displayMenu() {
		System.out.println("*******Banking Application*************");
		System.out.println("1. Customer");
		System.out.println("2. Bank Employee");
		System.out.println("3. Bank Admin");
		System.out.println("4. Exit");
	}

	private static UserInfo userInformationToApply(RegisteredUser loggedInUser) {
		UserInfo userInfo = new UserInfo();
		System.out.println("Enter your first name: ");
		String firstName = scanner.next();
		System.out.println("Enter your last name: ");
		String lastName = scanner.next();
		System.out.println("Enter your email: ");
		String email = scanner.next();
		scanner.nextLine();
		System.out.println("Enter your address: ");
		String address = scanner.nextLine();
		// setting user info for
		userInfo.setFirstName(firstName);
		userInfo.setLastName(lastName);
		userInfo.setEmail(email);
		userInfo.setAddress(address);
		userInfo.setRegisteredUser(loggedInUser);
		return userInfo;
	}

	private static void displayInitialMenu() {

		System.out.println("1. Register");
		System.out.println("2. Login");
	}

	private static void displayApplyApplicationMenu() {
		System.out.println("1. Apply for single account");
		System.out.println("2. Apply for joint account");
	}

	private static void displayAccountMenu() {
		System.out.println("1. View profile");
		System.out.println("2. Account Information");
		System.out.println("3. Deposit");
		System.out.println("4. Withdraw");
		System.out.println("5. Transfer money between accounts");
		System.out.println("6. Logout");
	}
	
	private static void showMenuForBankEmployee() {
		System.out.println("1. View Pending Approvals");
		System.out.println("2. View All Accounts");
		System.out.println("3. Load an Account to view details");
		System.out.println("4. View Personal information for loaded account");
		System.out.println("5. View Account Balance for loaded account");
		System.out.println("6. Logout");
	}
	
	private static void showMenuForBankAdmin() {
		System.out.println(" 1. View Pending Approvals");
		System.out.println(" 2. View All Accounts");
		System.out.println(" 3. Load an Account to view details");
		System.out.println(" 4. View Personal information for loaded account");
		System.out.println(" 5. View Account Balance for loaded account");
		System.out.println(" 6. Deposit");
		System.out.println(" 7. Withdraw");
		System.out.println(" 8. Transfer money between accounts");
		System.out.println(" 9. Cancel loaded account");
		System.out.println("10. Logout");

	}

	@Override
	public void close() throws IOException {
		scan.close();
		scanner.close();

	}
}