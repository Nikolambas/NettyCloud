package sample.helpers.classesFunctions;

import io.netty.channel.ChannelHandlerContext;
import sample.helpers.MessageFunctions;
import sample.helpers.classes.User;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class UserFunctions implements MessageFunctions {

    @Override
    public void accept(ChannelHandlerContext chx, Object o) {
        User user = (User) o;
        String userlog = user.getLogin();
        String password = user.getPassword();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Program Files" +
                    "/apache-maven-3.6.3-src/apache-maven-3.6.3/Netty/src/sample/server/user.db");
            Statement statement = connection.createStatement();
            ResultSet resultSet1 = statement.executeQuery("SELECT * FROM users WHERE userlogin = '"
                    + userlog + "';");
            while (resultSet1.next()) {
                String pas = resultSet1.getString(2);
                if (pas.equals(password)) {
                    user.setGetConnect(true);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        chx.writeAndFlush(user);
    }
}
