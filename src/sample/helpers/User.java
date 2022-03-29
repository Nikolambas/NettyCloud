package sample.helpers;


import java.nio.file.Path;

public class User implements MessageHelp {
    private String login;
    private String password;
    private boolean getConnect;

    public User(String login, String password) {
        getConnect = false;
        this.login = login;
        this.password = password;
    }

    public boolean GetConnect() {
        return getConnect;
    }

    public void setGetConnect(boolean getConnect) {
        this.getConnect = getConnect;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public MessageType getType() {
        return MessageType.USER;
    }
}
