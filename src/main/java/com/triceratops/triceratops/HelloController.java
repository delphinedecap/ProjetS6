package com.triceratops.triceratops;

import com.triceratops.triceratops.utils.NavigationUtils;
import com.triceratops.triceratops.utils.ToggleNode;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

import static com.triceratops.triceratops.MFXResourcesLoader.loadURL;


public class HelloController {
    private final Stage stage;
    private double xOffset;
    private double yOffset;

    /*

        Agrandir & réduire

     */
    private Cursor cursorEvent = Cursor.DEFAULT;
    private boolean isFullScreen = false;
    private double saveX;
    private double saveY;
    private double saveWitdh;
    private double saveHeight;
    private final int MARGE = 10;


    public HBox navBar;

    private final ToggleGroup toggleGroup;

    @FXML
    public AnchorPane rootPane;
    @FXML
    public HBox windowHeader;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private MFXFontIcon minimizeIcon;
    @FXML
    public MFXFontIcon maximize;


    @FXML
    private StackPane contentPane;


    public HelloController(Stage stage){
        this.stage = stage;
        this.toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
    }

    public void initialize() {
        /*
            WindowsHeader
         */
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            // Création d'une transition de mise à l'échelle vers zéro
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.4), rootPane);
            scaleTransition.setToX(0);
            scaleTransition.setToY(0);

            // Création d'une transition de fondu
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), rootPane);
            fadeTransition.setToValue(0); // Fondu vers 0 (invisible)

            // Définition de l'action à effectuer à la fin de la transition
            fadeTransition.setOnFinished(finishedEvent -> {
                // Minimiser la fenêtre une fois que la transition est terminée
                Platform.exit();
            });

            // Démarrer les transitions
            scaleTransition.play();
            fadeTransition.play();
        });

        maximize.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(isFullScreen){
                stage.setX(saveX);
                stage.setY(saveY);
                stage.setWidth(saveWitdh);
                stage.setHeight(saveHeight);
                isFullScreen = false;
                rootPane.setStyle("");
            }else {
                saveX = stage.getX();
                saveY = stage.getY();

                saveWitdh = stage.getWidth();
                saveHeight = stage.getHeight();

                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();

                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
                isFullScreen = true;
                rootPane.setStyle("-fx-background-radius: 0;");
            }
        });
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));

        //Move windows
        windowHeader.setOnMousePressed(event -> {
            if(Cursor.DEFAULT.equals(cursorEvent)){
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        windowHeader.setOnMouseDragged(event -> {
            if(Cursor.DEFAULT.equals(cursorEvent)){
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        // Gestion du redimensionnement de la fenêtre avec la souris
        rootPane.setOnMouseMoved(event -> {

            double mouseEventX = event.getX(),
                    mouseEventY = event.getY(),
                    sceneWidth = stage.getWidth(),
                    sceneHeight = stage.getHeight();

            if (mouseEventX < MARGE && mouseEventY < MARGE) {
                cursorEvent = Cursor.NW_RESIZE;
            } else if (mouseEventX < MARGE && mouseEventY > sceneHeight - MARGE) {
                cursorEvent = Cursor.SW_RESIZE;
            } else if (mouseEventX > sceneWidth - MARGE && mouseEventY < MARGE) {
                cursorEvent = Cursor.NE_RESIZE;
            } else if (mouseEventX > sceneWidth - MARGE && mouseEventY > sceneHeight - MARGE) {
                cursorEvent = Cursor.SE_RESIZE;
            } else if (mouseEventX < MARGE) {
                cursorEvent = Cursor.W_RESIZE;
            } else if (mouseEventX > sceneWidth - MARGE) {
                cursorEvent = Cursor.E_RESIZE;
            } else if (mouseEventY < MARGE) {
                cursorEvent = Cursor.N_RESIZE;
            } else if (mouseEventY > sceneHeight - MARGE) {
                cursorEvent = Cursor.S_RESIZE;
            }else{
                cursorEvent = Cursor.DEFAULT;
            }
            rootPane.setCursor(cursorEvent);
        });
        rootPane.setOnMouseDragged(mouseEvent -> {

            int border = 4;
            double startX = 0;
            double startY = 0;

            double mouseEventX = mouseEvent.getSceneX(),
                    mouseEventY = mouseEvent.getSceneY();

            if (!Cursor.DEFAULT.equals(cursorEvent)) {
                if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
                    double minHeight = stage.getMinHeight() > (border*2) ? stage.getMinHeight() : (border*2);
                    if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent) || Cursor.NE_RESIZE.equals(cursorEvent)) {
                        if (stage.getHeight() > minHeight || mouseEventY < 0) {
                            stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
                            stage.setY(mouseEvent.getScreenY());
                        }
                    } else {
                        if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                            stage.setHeight(mouseEventY + startY);
                        }
                    }
                }

                if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
                    double minWidth = stage.getMinWidth() > (border*2) ? stage.getMinWidth() : (border*2);
                    if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)) {
                        if (stage.getWidth() > minWidth || mouseEventX < 0) {
                            stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                            stage.setX(mouseEvent.getScreenX());
                        }
                    } else {
                        if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                            stage.setWidth(mouseEventX + startX);
                        }
                    }
                }
            }
        });

        initializeLoader();

    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();
        loader.addView(MFXLoaderBean.of("Accueil", loadURL("fxml/home.fxml")).setBeanToNodeMapper(() -> createToggle("Accueil")).setDefaultRoot(true).get());
        loader.addView(MFXLoaderBean.of("Simulateur", loadURL("fxml/simulateur.fxml")).setBeanToNodeMapper(() -> createToggle("Simulateur")).get());
        loader.addView(MFXLoaderBean.of("Vendre", loadURL("fxml/vendre.fxml")).setBeanToNodeMapper(() -> createToggle("Vendre")).get());
        loader.addView(MFXLoaderBean.of("Acheter", loadURL("fxml/acheter.fxml")).setBeanToNodeMapper(() -> createToggle("Acheter")).get());
        //loader.addView(MFXLoaderBean.of("Simulateur", loadURL("fxml/simu.fxml")).setBeanToNodeMapper(() -> createToggle("Simulateur")).get());

        loader.addView(MFXLoaderBean.of("A propos", loadURL("fxml/about.fxml")).setBeanToNodeMapper(() -> createToggle("A propos")).get());

        NavigationUtils nav = new NavigationUtils(contentPane);

        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = beans.stream()
                    .map(bean -> {
                        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                        //System.out.println(toggle.getClass());
                        toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                        nav.add(bean.getViewName(), new ToggleNode(bean.getRoot(), toggle));
                        if (bean.isDefaultView()) {
                            contentPane.getChildren().setAll(bean.getRoot());
                            toggle.setSelected(true);
                        }
                        return toggle;
                    })
                    .toList();
            navBar.getChildren().setAll(nodes);
        });
        loader.start();
    }


    private ToggleButton createToggle(String text) {
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        return toggleNode;
    }

}