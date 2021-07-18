package com.qingyou.qynat.gui.gui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTooltip;
import com.qingyou.qynat.gui.Main;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static io.datafx.controller.flow.container.ContainerAnimations.SWIPE_LEFT;

@ViewController(value = "/fxml/Main.fxml", title = "Material Design Example")
public final class MainController {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;

    @FXML
    private StackPane titleBurgerContainer;
    @FXML
    private JFXHamburger titleBurger;

    @FXML
    private StackPane optionsBurger;
    @FXML
    private JFXRippler optionsRippler;
    @FXML
    private JFXDrawer drawer;

    private JFXPopup toolbarPopup;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() throws Exception {
        // init the title hamburger icon
        final JFXTooltip burgerTooltip = new JFXTooltip("Open drawer");

//        drawer.setOnDrawerOpening(e -> {
//            final Transition animation = titleBurger.getAnimation();
//            burgerTooltip.setText("Close drawer");
//            animation.setRate(1);
//            animation.play();
//        });
//        drawer.setOnDrawerClosing(e -> {
//            final Transition animation = titleBurger.getAnimation();
//            burgerTooltip.setText("Open drawer");
//            animation.setRate(-1);
//            animation.play();
//        });
//        titleBurgerContainer.setOnMouseClicked(e -> {
//            if (drawer.isClosed() || drawer.isClosing()) {
//                drawer.open();
//            } else {
//                drawer.close();
//            }
//        });

//
//        JFXTooltip.setVisibleDuration(Duration.millis(3000));
//        JFXTooltip.install(titleBurgerContainer, burgerTooltip, Pos.BOTTOM_CENTER);

        // create the inner flow and content
//        context = new ViewFlowContext();
//        // set the default controller
//        Flow innerFlow = new Flow(ButtonController.class);
//
//        final FlowHandler flowHandler = innerFlow.createHandler(context);
//        context.register("ContentFlowHandler", flowHandler);
//        context.register("ContentFlow", innerFlow);

    }

    public static final class InputController {
        @FXML
        private JFXListView<?> toolbarPopupList;

        // close application
        @FXML
        private void submit() {
            if (toolbarPopupList.getSelectionModel().getSelectedIndex() == 1) {
                Platform.exit();
            }
        }
    }
}
