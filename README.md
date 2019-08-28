# SFTP

Client and Server Implementation for SFTP (Simple File Transfer Protocol)

IMPORTANT

- The default server directory is C:/
- ALL file (NAME, KILL, TOBE) actions work for local paths in the current directory only
- When sending files to the server, ensure that the folder is not protected administrative privileges

Testing

- Open the project in IntelliJ IDEA
- Run the project in the terminal (main class in SFTPProtocolTest.java)
  - Will create both server and client instances
- Type the commands in the terminal as specified in the documentation, excluding NULLS
- Press ENTER to send the command to the server
- Before attempting to rerun the program, ensure to use the DONE command to safely disconnect the server and client

Spaces

- For each command, ensure there is a single space between each 4 letter command and the parameters
- Additionally, there should be a single space between each parameter

Expected Command Output

- For each command, the output should respond in the same way specified in the STFP protocol provided
- Additionally, if the command and parameters are not in the correct format, then an &quot;-Invalid command&quot; will be returned
- Finally, if the user is not logged in and is attempting a command which requires login, then a corresponding reply will be sent asking the user to login

# TEST CASES

USER

- &quot;USER main&quot; to test for a superuser which doesn&#39;t require password or account
- &quot;USER rob&quot; to test for a superAccount which doesn&#39;t require password but does require account
- &quot;USER joey&quot; which is not a superuser or a superAccount which means it requires both password and account
- Any other &quot;USER&quot; command will return with error message

ACCT

- &quot;USER rob&quot; to specify which user we want to login, and then
  - &quot;ACCT admin&quot; to specify he is superAccount and hence doesn&#39;t require a password and so it should log-in the user
- &quot;USER joey&quot; to test a non-super user/account
  - &quot;ACCT guest&quot; to specify he is nor a superuser or superAccount there will require both password and account
  - So it will ask for the password in order to log them in
- If password was already supplied then the user will be logged in
- Is incorrect account names are provided then the server will reply with account name doesn&#39;t not exist and the account name must be respecified

PASS

- &quot;User joey&quot; to specify to the server we want to login a non-super user/account
  - &quot;PASS yoej&quot; to specify to the server the users password – this would log-in the user if the account was provided or else it will ask for the account
- If an incorrect password is provided this will cause the server to reply that the password is incorrect and must be reentered
- &quot;User rob&quot; only requires account but if password was provided before (&quot;PASS bor&quot;) then the server will ask for the missing credentials =\&gt; account

TYPE

- &quot;USER main&quot; is the quickest way to login =\&gt; super user
- &quot;TYPE A&quot;, &quot;TYPE B&quot; or &quot;TYPE C&quot; will cause the specified file transfer type to be selected
- Requires further implementation on the client side

CDIR

- &quot;USER main&quot; is the quickest way to login =\&gt; super user
- &quot;CDIR src&quot; will change the current working directory to src folder in the project

KILL

- create a file in src folder or use one of the dummy files (WebServer.java, UDPServer.java)
- &quot;USER main&quot; is the quickest way to login =\&gt; super user
- &quot;CDIR src&quot; to change working directory
- &quot;KILL Webserver.java&quot; or &quot;KILL UDPServer.java&quot; to delete the file

NAME/TOBE

- create a file in src folder or use one of the dummy files (WebServer.java, UDPServer.java)
- &quot;USER main&quot; is the quickest way to login =\&gt; super user
- &quot;CDIR src&quot; to change working directory
- &quot;NAME Webserver.java&quot; or &quot;NAME UDPServer.java&quot;
- &quot;TOBE rename.java&quot; to rename the file
