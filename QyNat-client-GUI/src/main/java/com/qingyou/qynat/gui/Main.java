package com.qingyou.qynat.gui;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RequiredFieldValidator;
import com.qingyou.qynat.client.client.QyNatClient;
import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.gui.listener.DragListener;
import com.qingyou.qynat.gui.handler.QyNatClientHandler;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.xml.stream.events.Characters;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author whz
 * @date 2021/7/17 21:34
 **/
public class Main extends Application {

    private static final String ERROR = "error";
    public static TextArea javafxTextArea;
    private final String pattenIp = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)" +
            ":([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$";
    private final String init = "                                  \n" +
            "_|       _|    _|_|    _|_|_|_|_|  \n" +
            "_|_|    _|  _|     _|       _|      \n" +
            "_|  _|  _|  _|_|_|_|      _|      \n" +
            "_|    _|_|  _|      _|      _|      \n" +
            "_|       _|  _|      _|      _|      \n" +
            "                                  \n" +
            "                                  ";

    @Override
    public void start(Stage stage) {


        final HBox root = new HBox();
        root.setMinWidth(1000);
        root.setMaxWidth(1000);
        root.setPrefWidth(1000);
        final VBox pane = new VBox();
        pane.setSpacing(30);
        pane.setStyle("-fx-background-color:WHITE;-fx-padding:40;");

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        JFXButton label = new JFXButton("password:");
        label.setDisable(true);
        label.setMinWidth(100);
        label.setPrefWidth(200);
        label.setMaxWidth(15);
        label.getStyleClass().add("button-flat");
        hBox.getChildren().add(label);
        JFXPasswordField passwordText = new JFXPasswordField();
        passwordText.setPromptText("password");
        hBox.getChildren().add(passwordText);
        pane.getChildren().add(hBox);
        pane.getChildren().add(getValidationField("server      :", "x.x.x.x:port"));
        pane.getChildren().add(getValidationField("proxy       :", "proxyAddress:port"));
        pane.getChildren().add(getValidationField("mapping :", "remotePort"));

        JFXProgressBar jfxBar = new JFXProgressBar();
        jfxBar.setPrefWidth(500);
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(jfxBar.secondaryProgressProperty(), 0),
                        new KeyValue(jfxBar.progressProperty(), 0)),
                new KeyFrame(
                        Duration.seconds(0.5),
                        new KeyValue(jfxBar.secondaryProgressProperty(), 1)),
                new KeyFrame(
                        Duration.seconds(1),
                        new KeyValue(jfxBar.progressProperty(), 1)));

        timeline.setCycleCount(1);
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        JFXButton button = new JFXButton("CONNECT");
        buttonBox.getChildren().add(button);
        button.getStyleClass().add("button-raised-on");
        button.setOnAction(e -> {
            buttonAction(button, stage, pane, timeline);
        });
        pane.getChildren().add(buttonBox);
        final VBox pane1 = new VBox();
        pane1.setSpacing(30);
        pane1.setStyle("-fx-background-color:WHITE;-fx-padding:40;");

        javafxTextArea = new TextArea();
        javafxTextArea.setPromptText("");
        javafxTextArea.setMaxHeight(300);
        javafxTextArea.setMinHeight(300);
        javafxTextArea.setPrefHeight(300);
        updateTextAreaContentStatic(init);
        pane1.getChildren().add(javafxTextArea);
        root.getChildren().add(pane);
        root.getChildren().add(pane1);


        pane.getChildren().add(jfxBar);

        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(true);
        decorator.setGraphic(new SVGGlyph(""));

        stage.setTitle("QY-NAT JFoenix");


        Scene scene = new Scene(decorator, 700, 400);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm(),
                JFoenixResources.load("css/jfoenix-design.css").toExternalForm(),
                Objects.requireNonNull(Main.class.getResource("/css/jfoenix-main-demo.css")).toExternalForm());
        stage.setScene(scene);
        new DragListener(stage).enableDrag(decorator);
        stage.show();

    }

    public void buttonAction(JFXButton button, Stage stage, VBox pane, Timeline timeline) {
        try {
            if (button.getText().equals("CONNECT")) {
                String password = getText(stage, pane, 0);
                String serverAddr = getText(stage, pane, 1);
                String proxyAddr = getText(stage, pane, 2);
                String remotePort = getText(stage, pane, 3);
                String serverPort = serverAddr.split(":")[1];
                String serverAddress = serverAddr.split(":")[0];
                String proxyPort = proxyAddr.split(":")[1];
                String proxyAddress = proxyAddr.split(":")[0];
                System.out.println(serverAddr + "::" + serverPort);
                button.getStyleClass().remove("button-raised-on");
                button.getStyleClass().add("button-raised-off");
                button.setText("CLOSE");
                timeline.play();
                new Thread(() -> {
                    QyNatClient client = new QyNatClient();
                    try {
                        client.connect(serverAddress, serverPort, remotePort, password,
                                proxyAddress, proxyPort, QyNatClientHandler.class);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            } else {
                button.getStyleClass().remove("button-raised-off");
                button.getStyleClass().add("button-raised-on");
                button.setText("CONNECT");
                timeline.setCycleCount(1);
                System.exit(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDialog(Stage stage, String text) {
        updateTextAreaContentStatic(text + "未输入或格式有误！");
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setBody(new Label(text + "未输入或格式有误！"));
        JFXAlert<Void> alert = new JFXAlert<>(stage);
        alert.setTitle("alert");
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.setContent(layout);
        alert.initModality(Modality.NONE);
        alert.showAndWait();
    }

    public String getText(Stage stage, VBox pane, int index) throws QyNatException {
        if (index == 0) {
            return ((JFXPasswordField) (((HBox) (pane.getChildren().get(index))).getChildren().get(1))).getText().trim();
        }
        String text = ((JFXTextField) (((HBox) (pane.getChildren().get(index))).getChildren().get(1))).getText().trim();
        if ("".equals(text) || text.length() == 0) {
            getDialog(stage, getLabel(pane, index));
            throw new QyNatException("未输入");
        } else if (index == 1 || index == 2) {
            if (!isIp(text) && !text.startsWith("localhost")) {
                getDialog(stage, getLabel(pane, index));
                throw new QyNatException("输入格式有误");
            }
        } else if (index == 3) {
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (!Character.isDigit(ch) && ch != '.' && ch != ':') {
                    getDialog(stage, getLabel(pane, index));
                    throw new QyNatException("输入格式有误");
                }
            }
        }
        return text;
    }

    public boolean isIp(String text) {
        if (text != null && !text.isEmpty()) {
            return text.matches(pattenIp);
        }
        return false;
    }

    public String getLabel(VBox pane, int index) {
        return ((JFXButton) (((HBox) (pane.getChildren().get(index))).getChildren().get(0))).getText().trim();
    }

    public HBox getValidationField(String label, String text) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        JFXButton button = new JFXButton(label);
        button.getStyleClass().add("button-flat");
        button.setMinWidth(100);
        button.setPrefWidth(200);
        button.setMaxWidth(15);
        button.setDisable(true);
        hBox.getChildren().add(button);
        JFXTextField validationField = new JFXTextField();
        if ("proxyAddress:port".equals(text)) {
            validationField.setText("localhost:8080");
        }
        validationField.setPromptText(text);
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage(label.replace(":", "").trim() + " Input Required");
        FontIcon warnIcon = new FontIcon();
        warnIcon.getStyleClass().add(ERROR);
        validator.setIcon(warnIcon);
        validationField.getValidators().add(validator);
        validationField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                validationField.validate();
            }
        });
        hBox.getChildren().add(validationField);
        return hBox;
    }

    public static void updateTextAreaContentStatic(String text) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javafxTextArea.appendText("\n" + df.format(LocalDateTime.now()) + "\n" + text + "\n");
    }


    public static void main(String[] args) {
        launch(args);
    }


}