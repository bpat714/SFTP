/**
 * Code is taken from Computer Networking: A Top-Down Approach Featuring 
 * the Internet, second edition, copyright 1996-2002 J.F Kurose and K.W. Ross, 
 * All Rights Reserved.
 **/

import org.w3c.dom.ls.LSResourceResolver;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.*;
import java.net.*;
import java.util.*;

class SFTPServer {

	//Constants
	private String loggedInUserID;
	private String loggedInAccount;
	private loginStatus LS = loginStatus.LOGGED_OUT;
	private String currentDir = System.getProperty("user.dir");
	private String requestedDir = currentDir;
	private String renameFile = null;

	private List<Accounts> userData = Arrays.asList(
			new Accounts("main", "main", "admin"),
			new Accounts("rob", "bor", "admin"),
			new Accounts("joey", "yeoj", "guest")
	);

	private List<String> superUser = Arrays.asList("main");
	private List<String> superAccount = Arrays.asList("admin");

	Map<String, Accounts> userMap = new HashMap<>();

	public SFTPServer(int port) throws Exception
	{
		// Variables
		String clientInput;
		String command;
		String response;
		String parameters;
		loadUserData();
		boolean active = true;

		ServerSocket welcomeSocket = new ServerSocket(port);
		Socket connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

		while(active) {
			clientInput = inFromClient.readLine();
			command = clientInput.substring(0, Math.min(clientInput.length(), 4));

			if(command.equals("DONE")){
				active = false;
				response = "+";
			} else {
				try {
					parameters = clientInput.substring(5);
				} catch (Exception e){
					outToClient.writeBytes("-unknown command\n");
					continue;
				}
				if(command.equals("USER")) {
					response = USERCommand(parameters);
				} else if(command.equals("ACCT")){
					response = ACCTCommand(parameters);
				} else if(command.equals("PASS")) {
					response = PASSCommand(parameters);
				} else if (command.equals("TYPE")){
					response = TYPECommand(parameters);
				} else if (command.equals("CDIR")){
					response = CDIRCommand(parameters);
				} else if (command.equals("KILL")) {
					response = KILLCommand(parameters);
				} else if (command.equals("NAME")){
					response = NAMECommand(parameters);
				} else if (command.equals("TOBE")) {
					response = TOBECommand(parameters);
				} else {
					response = "-unknown command\n";
				}
			}
			outToClient.writeBytes(response + "\n");
		}
		welcomeSocket.close();
	}

	public void loadUserData() {
		for (Accounts accounts : userData){
			userMap.put(accounts.getUserID(), accounts);
		}
	}


	public String USERCommand(String userID) {

		if (superUser.contains(userID)){
			loggedInUserID = userID;
			loggedInAccount = userMap.get(userID).getAccount();
			LS = loginStatus.LOGGED_IN;
			return "!" + userID + " logged in";
		} else if (userMap.containsKey(userID)){
			loggedInUserID = userID;
			LS = loginStatus.USERNAME_PROVIDED;
			return "User-id valid, send account and password";
		} else {
			return "-Invalid user-id, try again";
		}
	}

	public String ACCTCommand(String account) {
		if (LS == loginStatus.LOGGED_OUT|| !userMap.get(loggedInUserID).getAccount().equals(account)) {
			return "-Invalid account, try again";
		}
		if (LS == loginStatus.LOGGED_IN) {
			return "!Account valid, logged-in";
		}
		if (LS == loginStatus.USERNAME_PROVIDED || LS == loginStatus.USER_PASSWORD_PROVIDED) {
			loggedInAccount = account;
			if (superAccount.contains(account)) {
				LS = loginStatus.LOGGED_IN;
				return "!Account valid, logged-in";
			} else {
				LS = loginStatus.USER_ACCOUNT_PROVIDED;
				return "+Account valid, send password";
			}
		}
		if (LS == loginStatus.USER_PASSWORD_PROVIDED) {
			loggedInAccount = account;
			LS = loginStatus.LOGGED_IN;
			return "!Account valid, logged-in";
		}
		return "-Invalid account, try again";
	}

	public String PASSCommand(String password) {
		if(LS == loginStatus.LOGGED_IN){
			return "!Logged in";
		}
		if(LS == loginStatus.LOGGED_OUT ||  !userMap.get(loggedInUserID).getPassword().equals(password)) {
			return "-Wrong password, try again";
		}
		if(LS == loginStatus.USERNAME_PROVIDED || LS == loginStatus.USER_PASSWORD_PROVIDED){
			LS = loginStatus.USER_PASSWORD_PROVIDED;
			return "+Send account";
		}
		if(LS == loginStatus.USER_ACCOUNT_PROVIDED) {
			LS = loginStatus.LOGGED_IN;
			return "!Logged in";
		}
		return "-Wrong password, try again";
	}

	public String TYPECommand(String type){
		if(LS == loginStatus.LOGGED_IN){
			if(type.equals("A")){
				return "+Using Ascii Mode";
			} else if (type.equals("B")) {
				return "+Using Binary Mode";
			} else if (type.equals("C")) {
				return "+Using Continuous Mode";
			} else {
				return "-Type not valid";
			}
		} else {
			return "-Please login";
		}
	}

	public String CDIRCommand(String newDir) {
		if (LS == loginStatus.LOGGED_OUT) {
			return "-Can't connect to directory because: User credentials missing";
		} else {
			try {
				File fileLocation = new File(newDir);
				File[] files = fileLocation.listFiles();
			} catch (NullPointerException e) {
				return "-Can't connect to directory because: Directory doesn't exist";
			}

			if (LS == loginStatus.LOGGED_IN) {
				currentDir = newDir;
				requestedDir = newDir;
				return "!Changed working directory to " + newDir;
			} else {
				requestedDir = newDir;
				return "+Directory ok, send account/password";
			}
		}
	}

	public  String KILLCommand(String fileSpec) {
		if (LS == loginStatus.LOGGED_IN) {
			File fileLocation = new File(currentDir + '/' + fileSpec); //local files only.
			//File[] files = fileLocation.listFiles();
			if(fileLocation.exists()) {
				if(fileLocation.delete()) {
					return "+" + fileSpec + " deleted";
				} else {
					return "-Not deleted because of an unknown error";
				}
			} else {
				return  "-Not deleted because file doesn't exist";
			}
		} else {
			return "-Not deleted because client is not logged in";
		}
	}

	public  String NAMECommand(String fileSpec){
		if (LS == loginStatus.LOGGED_IN) {
			File fileLocation = new File(currentDir + '/' + fileSpec); //local files only.
			if (fileLocation.exists()) {
				renameFile = fileSpec;
				return "+File exists";
			} else {
				renameFile = null;
				return "-Can't find" + fileSpec;
			}
		} else {
			return "-Client is not logged in";
		}
	}

	public String TOBECommand(String fileSpec){
		if (LS == loginStatus.LOGGED_IN) {
			if(renameFile == null) {
				return "-File wasn't renamed because of unknown error";
			}
			File fileLocation = new File(currentDir + '/' + renameFile); //local files only.
			File newLocation = new File(currentDir + '/' + fileSpec); //local files only.
			if(fileLocation.renameTo(newLocation)) {
				return "+" + renameFile + " renamed to " + fileSpec;
			}
			return "-File wasn't renamed due to an unknown error";
		} else {
			return "-Please Log In";
		}
	}
}

