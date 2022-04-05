package sample.helpers.classesFunctions;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import sample.helpers.MessageFunctions;
import sample.helpers.classes.FileGet;
import sample.helpers.classes.ServerListView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileGetFunctions implements MessageFunctions {

    public Path path = Paths.get("src", "sample", "server");
    private static final int BYTES = 8_388_608;

    @Override
    public void accept(ChannelHandlerContext chx, Object o) {
        FileGet fileGet = (FileGet) o;
        try {
            if (fileGet.isFileFinish()){
                Files.write(path.resolve(fileGet.getUser()).resolve(fileGet.getFileName()), fileGet.getBytes());
                chx.writeAndFlush(getServerView(fileGet.getUser()));
            }else {
                Files.write(path.resolve(fileGet.getUser()).resolve(fileGet.getFileName()), fileGet.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ServerListView getServerView(String user) {
        ServerListView serverListView = new ServerListView(user);
        double size = 0;
        try {
            serverListView.setFiles(Files.list(path.resolve(user))
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList()));

            try (Stream<Path> walk = Files.walk(path.resolve(user))) {
                size = walk
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try {
                                return Files.size(p);
                            } catch (IOException e) {
                                System.out.printf("Невозможно получить размер файла %s%n%s", p, e);
                                return 0L;
                            }
                        })
                        .sum();
            } catch (IOException e) {
                System.out.printf("Ошибка при подсчёте размера директории %s", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverListView.setPath(path.resolve(user).getFileName().toString() +
                "                     File size on server: " + String.format("%.2f", (size / 1073741824)) + "g on 2.00g");
        serverListView.setServerFreeSize(2.00 - (size / 1073741824));
        return serverListView;
    }
}
