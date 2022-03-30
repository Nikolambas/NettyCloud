package sample.helpers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserReg implements MessageHelp {
    private String userLog;
    private String userPassword;
    private boolean successfulReg;

    public void setSuccessfulReg(boolean successfulReg) {
        this.successfulReg = successfulReg;
    }

    public boolean isSuccessfulReg() {
        return successfulReg;
    }

    public UserReg(String userLog, String userPassword) {
        successfulReg = false;
        this.userLog = userLog;
        this.userPassword = hashPassword(userPassword);
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

    public String getUserLog() {
        return userLog;
    }

    public String getUserPassword() {
        return userPassword;
    }

    @Override
    public MessageType getType() {
        return MessageType.USERREG;
    }
}
