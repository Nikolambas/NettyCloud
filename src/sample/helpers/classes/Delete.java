package sample.helpers.classes;

import sample.helpers.MessageHelp;
import sample.helpers.MessageType;

public class Delete implements MessageHelp {
    private String user;
    private String fileName;

    public Delete(String user, String fileName) {
        this.user = user;
        this.fileName = fileName;
    }

    public String getUser() {
        return user;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public MessageType getType() {
        return MessageType.DELETE;
    }
}
