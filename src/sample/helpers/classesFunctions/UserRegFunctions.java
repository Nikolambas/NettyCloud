package sample.helpers.classesFunctions;

import io.netty.channel.ChannelHandlerContext;
import sample.helpers.MessageFunctions;
import sample.helpers.classes.UserReg;

import java.io.File;
import java.sql.*;

public class UserRegFunctions implements MessageFunctions {
    @Override
    public void accept(ChannelHandlerContext chx, Object o) {
        try {
            UserReg userReg = (UserReg) o;
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Program Files" +
                    "/apache-maven-3.6.3-src/apache-maven-3.6.3/Netty/src/sample/server/user.db");
            Statement statement = connection.createStatement();
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
                chx.writeAndFlush(userReg);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
