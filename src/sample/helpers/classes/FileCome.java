package sample.helpers.classes;

import sample.helpers.MessageHelp;
import sample.helpers.MessageType;

import static sample.helpers.MessageType.FILECOME;

public class FileCome implements MessageHelp {
    private String fileName;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public FileCome(String fileName, String userName) {
        this.fileName = fileName;
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public MessageType getType() {
        return FILECOME;
    }
}
