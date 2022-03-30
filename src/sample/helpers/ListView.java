package sample.helpers;


import java.nio.file.Path;
import java.util.List;


public class ListView implements MessageHelp {
    private List<String> files;
    private String path;
    private String user;
    private Double ServerFreeSize;

    public void setServerFreeSize(Double serverFreeSize) {
        ServerFreeSize = serverFreeSize;
    }

    public Double getServerFreeSize() {
        return ServerFreeSize;
    }

    public String getUser() {
        return user;
    }

    public ListView(String user) {
        this.user = user;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getFiles() {
        return files;
    }

    public String getPath() {
        return path;
    }

    @Override
    public MessageType getType() {
        return MessageType.LISTVIEW;
    }
}
