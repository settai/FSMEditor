/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm;

import fr.fsm.gui.controls.PanManager;
import fr.fsm.gui.controls.ZoomManager;
import fr.fsm.gui.creation.CreateMenu;
import fr.fsm.gui.views.FsmPane;
import fr.fsm.gui.views.statemachines.DragStateMachine;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author SETTAI Yassine
 */
public class FsmEditorApplication extends Application {

    /**
     * A complicated tab that stores data and represents the state machine.
     */
    public static StateMachineTab smt = new StateMachineTab();

    /**
     *
     */
    public static FsmPane grid;

    /**
     * Do we really need doc about this ?
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        //Create containers of ou application
        Group root = new Group();
        Group group = new Group();
        Pane container = new Pane();
        this.grid = new FsmPane(stage);

        new PanManager(grid, new DragStateMachine());
        new ZoomManager(grid);

        group.getChildren().add(grid);
        container.getChildren().add(group);

        //Create menubar
        MenuBar menubar = CreateMenu.createMenuBar(stage, grid);

        root.getChildren().addAll(menubar, container);

        // Création de la scène
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Editeur FMS");
        stage.show();

        container.setLayoutY(menubar.getHeight());
        menubar.toFront();
        menubar.minWidthProperty().bind(scene.widthProperty());
    }
}
