package sample.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileGet implements MessageHelp {
    private byte[] bytes;
    private String fileName;
    private long fileSize;
    private String user;


    public String getUser() {
        return user;
    }

    public FileGet(Path path, String user) {
        try {
            this.user = user;
            bytes = Files.readAllBytes(path);
            this.fileName = path.getFileName().toString();
            this.fileSize = bytes.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public MessageType getType() {
        return MessageType.FILEGET;
    }
}
