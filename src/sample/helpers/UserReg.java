package sample.helpers;

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
        this.userPassword = userPassword;
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
