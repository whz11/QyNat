package com.qingyou.qynat.gui;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.assets.JFoenixResources;
import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RequiredFieldValidator;
import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.gui.client.QyNatClient;
import com.qingyou.qynat.gui.gui.DragListener;
import com.qingyou.qynat.gui.gui.MainController;
import com.qingyou.qynat.gui.handler.QyNatClientHandler;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author whz
 * @date 2021/7/17 21:34
 **/
public class Main extends Application {

    private static final String FX_LABEL_FLOAT_TRUE = "-fx-label-float:true;";
    private static final String ERROR = "error";
    public static TextArea javafxTextArea;
    private static final String SALES_DEPARTMENT = "Sales Depa";
    private static final String IT_SUPPORT = "IT adsfasdfasfdasdfsadfsadfasdf Support";
    private static final String ACCOUNTS_DEPARTMENT = "Accounts Department";
    private static final String TAB_0 = "Tab 0";
    private static final String TAB_01 = "Tab 01";
    private static final String msg = TAB_0;
    private final SecureRandom random = new SecureRandom();

    @Override
    public void start(Stage stage) throws Exception {

        QyNatClient client = new QyNatClient();

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
        JFXTextField passwordText = new JFXTextField();
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
                    client.connect(serverAddress, serverPort, password, remotePort, proxyAddress, proxyPort);
                } else {
                    button.getStyleClass().remove("button-raised-off");
                    button.getStyleClass().add("button-raised-on");
                    button.setText("CONNECT");
                    timeline.setCycleCount(1);
                    System.exit(0);
                }

            } catch (Exception qe) {
                qe.printStackTrace();
            }
        });
        pane.getChildren().add(buttonBox);
        final VBox pane1 = new VBox();
        pane1.setSpacing(30);
        pane1.setStyle("-fx-background-color:WHITE;-fx-padding:40;");

        javafxTextArea = new TextArea();
        javafxTextArea.setPromptText("JavaFX Text Area");
        javafxTextArea.setMaxHeight(300);
        javafxTextArea.setMinHeight(300);
        javafxTextArea.setPrefHeight(300);
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
        String text = ((JFXTextField) (((HBox) (pane.getChildren().get(index))).getChildren().get(1))).getText().trim();
        if (index != 0 && ("".equals(text) || text.length() == 0)) {
            getDialog(stage, getLabel(pane, index));
            throw new QyNatException("未输入");
        } else if (index == 1 || index == 2) {
            if (!text.contains(":")) {
                getDialog(stage, getLabel(pane, index));
                throw new QyNatException("输入格式有误");
            }
        }
        return text;
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
        javafxTextArea.setText(javafxTextArea.getText() + "\n" + df.format(LocalDateTime.now()) + "\n" + text + "\n");
    }

    public void updateTextAreaContent(String text) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javafxTextArea.setText(javafxTextArea.getText() + "\n" + df.format(LocalDateTime.now()) + " " + text);
        System.out.println(df.format(LocalDateTime.now()));
    }

    public static void main(String[] args) {
        launch(args);
    }


}