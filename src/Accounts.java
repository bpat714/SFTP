import java.net.Socket;

public class Accounts {
    private String userID;
    private String password;
    private String account;

    public Accounts(String userID, String password, String account){
        this.userID = userID;
        this.password = password;
        this.account = account;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getAccount(){
        return account;
    }

    public void setAccount(String account){
        this.account = account;
    }
}