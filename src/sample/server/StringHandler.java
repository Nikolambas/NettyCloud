package sample.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import sample.helpers.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class StringHandler extends SimpleChannelInboundHandler<Object> {
    public Path path = Paths.get("src", "sample", "server");
    Connection connection;
    Statement statement;

    {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Program Files" +
                    "/apache-maven-3.6.3-src/apache-maven-3.6.3/Netty/src/sample/server/user.db");
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception " + cause.toString());

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected ");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageHelp a = (MessageHelp) msg;
        switch (a.getType()) {
            case USERREG:
                UserReg userReg = (UserReg) msg;
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE userlogin = '"
                        + userReg.getUserLog() + "';");
                String pass = null;
                while (resultSet.next()) {
                    pass = resultSet.getString(2);
                }
                if (pass == null) {
                    statement.executeUpdate("INSERT INTO users (userlogin,password) " +
                            "VALUES ('" + userReg.getUserLog() + "','" + userReg.getUserPassword() + "');");
                    File file = new File("C:/Program Files/apache-maven-3.6.3-src" +
                            "/apache-maven-3.6.3/Netty/src/sample/server/" + userReg.getUserLog());
                    file.mkdirs();
                    userReg.setSuccessfulReg(true);
                }
                ctx.writeAndFlush(userReg);
                break;
            case FILEGET:
                FileGet fileGet = (FileGet) msg;
                Files.write(path.resolve(fileGet.getUser()).resolve(fileGet.getFileName()), fileGet.getBytes());
                ctx.writeAndFlush(getServerView(fileGet.getUser()));
                break;
            case FILECOME:
                FileCome fileCome = (FileCome) msg;
                ctx.writeAndFlush(new FileGet(path.resolve(fileCome.getUserName()).resolve(fileCome.getFileName())
                        , fileCome.getFileName()));
                break;
            case LISTVIEW:
                ListView listView = (ListView) msg;
                ctx.writeAndFlush(getServerView(listView.getUser()));
                break;
            case USER:
                User user = (User) msg;
                String userlog = user.getLogin();
                String password = user.getPassword();
                ResultSet resultSet1 = statement.executeQuery("SELECT * FROM users WHERE userlogin = '"
                        + userlog + "';");
                while (resultSet1.next()) {
                    String pas = resultSet1.getString(2);
                    if (pas.equals(password)) {
                        user.setGetConnect(true);
                    }
                }
                ctx.writeAndFlush(user);
                break;
        }
    }

    private ListView getServerView(String user) {
        ListView listView = new ListView(user);
        double size = 0;
        try {
            listView.setFiles(Files.list(path.resolve(user))
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
        listView.setPath(path.resolve(user).getFileName().toString() +
                "                     File size on server: " +String.format("%.2f", (size / 1073741824)) + "g on 2.00g");
        return listView;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
