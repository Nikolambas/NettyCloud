package sample.helpers.classes;

import sample.helpers.MessageHelp;
import sample.helpers.MessageType;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileGet implements MessageHelp {
    private byte[] bytes;
    private String fileName;
    private String user;
    private boolean isFileFinish;
    private int filePart;


    public String getUser() {
        return user;
    }

    public FileGet(Path path,String user) {
        this.user = user;
        this.fileName = path.getFileName().toString();
        this.filePart=0;
    }

    public boolean isFileFinish() {
        return isFileFinish;
    }

    public int getFilePart() {
        return filePart;
    }

    public void setFileFinish(boolean fileFinish) {
        isFileFinish = fileFinish;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setFilePart() {
        this.filePart++;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public MessageType getType() {
        return MessageType.FILEGET;
    }
}
