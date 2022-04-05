package sample.helpers.classes;


import sample.helpers.MessageHelp;
import sample.helpers.MessageType;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User implements MessageHelp {
    private String login;
    private String password;
    private boolean getConnect;

    public User(String login, String password) {
        getConnect = false;
        this.login = login;
        this.password = hashPassword(password);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
