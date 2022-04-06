package sample.helpers.classesFunctions;

import io.netty.channel.ChannelHandlerContext;
import sample.helpers.MessageFunctions;
import sample.helpers.classes.FileCome;
import sample.helpers.classes.FileGet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileComeFunctions implements MessageFunctions {

    private static final int MB_8 = 8_388_608;
    public Path path = Paths.get("src", "sample", "server");

    @Override
    public void accept(ChannelHandlerContext chx, Object o) {
        FileCome fileCome = (FileCome) o;
        try {
            Path pathFile = path.resolve(fileCome.getUserName()).resolve(fileCome.getFileName());
            byte[] bytes = Files.readAllBytes(pathFile);
            long fileSize = pathFile.toFile().length();
            FileGet fileGet = new FileGet(pathFile, fileCome.getUserName());
            while (!fileGet.isFileFinish()) {
                byte[] bytesSent;
                if (fileSize <= MB_8) {
                    bytesSent = new byte[(int) fileSize];
                    fileGet.setFileFinish(true);
                }
                else {
                    bytesSent = new byte[MB_8];
                }
                for (int i = 0; i < bytesSent.length; i++) {
                    bytesSent[i] = bytes[i + MB_8 * fileGet.getFilePart()];
                }
                fileGet.setBytes(bytesSent);
                fileGet.setFilePart();
                fileSize -= MB_8;
                chx.writeAndFlush(fileGet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
