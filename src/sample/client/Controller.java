package sample.client;


import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import sample.helpers.*;
import sample.helpers.classes.*;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class Controller implements Initializable {
    @FXML
    public Label userNotFound;
    public Pane loginPane;
    public AnchorPane registrationPane;
    public Button switchOn;
    public TextField serverHead;
    public TextField clientHead;
    public javafx.scene.control.ListView serverView;
    public javafx.scene.control.ListView clientView;
    public Pane cloudMenu;
    public TextField login;
    public PasswordField password;
    public AnchorPane regView;
    public TextField loginReg;
    public TextField passwordReg1;
    public TextField passwordReg2;
    public Label successfulReg;
    public Label unSuccessfulReg;
    public Label passNotEquals;
    public Label notSizeOnServer;

    private static final int MB_8 = 8_388_608;
    private static final long GB_2 = 2_147_483_648L;

    private Path path;
    private ObjectEncoderOutputStream oos;
    private ObjectDecoderInputStream ois;
    private String userLog;
    private Double serverFreeSize;

    public void getClientView() {
        Platform.runLater(() -> {
            clientHead.clear();
            clientView.getItems().clear();
            clientHead.appendText(path.getFileName().toString());
            clientView.getItems().addAll(path.toFile().list());
        });

    }

    public void getRegistrationField(ActionEvent actionEvent) {
        if (switchOn.getText().equals("login")) {
            getSwitch(false, true, "registration");
        } else {
            getSwitch(true, false, "login");
        }
    }

    private void getSwitch(boolean regPane, boolean logPane, String button) {
        registrationPane.setVisible(regPane);
        loginPane.setVisible(logPane);
        switchOn.setText(button);
    }

    public void addUser(ActionEvent actionEvent) {
        if (passwordReg1.getText().equals(passwordReg2.getText())) {
            try {
                oos.writeObject(new UserReg(loginReg.getText(), passwordReg1.getText()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        successfulReg.setVisible(false);
        unSuccessfulReg.setVisible(false);
        passNotEquals.setVisible(true);
    }

    public void getLogin(ActionEvent actionEvent) {
        userLog = login.getText();
        String passwordUser = password.getText();
        User user = new User(userLog, passwordUser);
        try {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) {
        try {
            if (Files.size(path.resolve(clientView.getSelectionModel().getSelectedItem().toString())) / GB_2
                    < serverFreeSize) {
                Path pathFile = path.resolve(clientView.getSelectionModel().getSelectedItem().toString());
                byte[] bytes = Files.readAllBytes(pathFile);
                long fileSize = pathFile.toFile().length();
                FileGet fileGet = new FileGet(pathFile, userLog);
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
                    oos.writeObject(fileGet);
                }
            } else notSizeOnServer.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void download(ActionEvent actionEvent) {
        try {
            oos.writeObject(new FileCome(serverView.getSelectionModel().getSelectedItem().toString(), userLog));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void read() {
        try {
            while (true) {
                MessageHelp mh = (MessageHelp) ois.readObject();
                switch (mh.getType()) {
                    case USERREG:
                        UserReg userReg = (UserReg) mh;
                        if (userReg.isSuccessfulReg()) {
                            unSuccessfulReg.setVisible(false);
                            passNotEquals.setVisible(false);
                            successfulReg.setVisible(true);
                        } else {
                            successfulReg.setVisible(false);
                            passNotEquals.setVisible(false);
                            unSuccessfulReg.setVisible(true);
                        }
                        break;
                    case LISTVIEW:
                        ServerListView listView = (ServerListView) mh;
                        Platform.runLater(() -> {
                            serverHead.clear();
                            serverView.getItems().clear();
                            serverHead.appendText(listView.getPath());
                            serverFreeSize = listView.getServerFreeSize();
                            serverView.getItems().addAll(listView.getFiles());
                        });
                        break;
                    case FILEGET:
                        FileGet fileGet = (FileGet) mh;
                        if (fileGet.getFilePart()==1){
                            Files.write(path.resolve(fileGet.getFileName()), fileGet.getBytes());
                            if (fileGet.isFileFinish()){
                                getClientView();
                            }
                            break;
                        }
                        if (fileGet.isFileFinish()) {
                            Files.write(path.resolve(fileGet.getFileName()), fileGet.getBytes(),StandardOpenOption.APPEND);
                            getClientView();
                        } else {
                            Files.write(path.resolve(fileGet.getFileName()), fileGet.getBytes(),StandardOpenOption.APPEND);
                        }
                        break;
                    case USER:
                        User user = (User) mh;
                        if (user.GetConnect()) {
                            regView.setVisible(false);
                            cloudMenu.setVisible(true);
                            path = Paths.get("src", "sample", "client", "user");
                            getClientView();
                            getServerView();
                            break;
                        }
                        userNotFound.setVisible(true);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getServerView() {
        ServerListView listView = new ServerListView(userLog);
        try {
            oos.writeObject(listView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(ActionEvent actionEvent) {
        try {
            oos.writeObject(new Delete(userLog, serverView.getSelectionModel().getSelectedItem().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            ois = new ObjectDecoderInputStream(socket.getInputStream(), MB_8+500_000);
            oos = new ObjectEncoderOutputStream(socket.getOutputStream());
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

