package sample.client;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import sample.helpers.*;
import sample.helpers.ListView;


import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            if (Files.size(path.resolve(clientView.getSelectionModel().getSelectedItem().toString())) < serverFreeSize) {
                oos.writeObject(new FileGet(path.resolve(clientView.getSelectionModel().getSelectedItem().toString())
                        , userLog));
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
                        ListView listView = (ListView) mh;
                        Platform.runLater(() -> {
                            serverHead.clear();
                            serverView.getItems().clear();
                            serverHead.appendText(listView.getPath());
                            serverView.getItems().addAll(listView.getFiles());
                        });
                        break;
                    case FILEGET:
                        FileGet fileGet = (FileGet) mh;
                        Files.write(path.resolve(fileGet.getFileName()), fileGet.getBytes());
                        getClientView();
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
        ListView listView = new ListView(userLog);
        try {
            oos.writeObject(listView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            ois = new ObjectDecoderInputStream(socket.getInputStream());
            oos = new ObjectEncoderOutputStream(socket.getOutputStream());
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

