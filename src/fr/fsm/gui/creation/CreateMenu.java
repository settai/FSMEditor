/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.creation;

import fr.fsm.FsmEditorApplication;
import static fr.fsm.FsmEditorApplication.smt;
import fr.fsm.file.java.OpenJava;
import fr.fsm.file.java.SaveToJava;
import fr.fsm.file.smala.OpenSmala;
import fr.fsm.file.smala.SaveToSmala;
import fr.fsm.file.smt.StateMachineSave;
import fr.fsm.gui.views.FsmPane;
import fr.fsm.gui.views.StatePane;
import fr.optimisation.StateOpti;
import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Just to create the menu.
 *
 * @author lucas knaff
 */
public class CreateMenu {

    /**
     *
     * @param stage
     * @param grid
     * @return
     */
    @SuppressWarnings("Convert2Lambda")
    public static MenuBar createMenuBar(Stage stage, FsmPane grid) {
        //filechoser (pour save et open)
        FileChooser openner = new FileChooser();
        openner.setTitle("Open a file");
        openner.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Java code", "*.java"), new FileChooser.ExtensionFilter("Smala code", "*.sma"), new FileChooser.ExtensionFilter("State Machine save", "*.json"));
        openner.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser saver = new FileChooser();
        saver.setTitle("Save state machine as ...");
        saver.getExtensionFilters().addAll(openner.getExtensionFilters());
        saver.setInitialDirectory(new File(System.getProperty("user.home")));

        //menu
        MenuBar menubar = new MenuBar();
        Menu file = new Menu("File");
        MenuItem fileSave = new MenuItem("Save");
        fileSave.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        fileSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File addFile = saver.showSaveDialog(stage);
                if (addFile != null) {
                    String add = addFile.getAbsolutePath();
                    funSave(grid, add);
                }
            }
        });
        MenuItem fileNew = new MenuItem("New");
        fileNew.setAccelerator(KeyCombination.keyCombination("Shortcut+Alt+N"));
        fileNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Continue ?");
                alert.setHeaderText("Did you save your content before new ?");

                ButtonType cont = new ButtonType("Yes, continue");
                ButtonType sav = new ButtonType("No, save");
                ButtonType canc = new ButtonType("Cancel");

                alert.getButtonTypes().clear();
                alert.getButtonTypes().addAll(canc, cont, sav);

                Optional<ButtonType> option = alert.showAndWait();

                if (option.get() == cont) {
                    funNew(grid);
                } else if (option.get() == sav) {
                    fileSave.fire();
                }
            }
        });
        MenuItem fileOpen = new MenuItem("Open");
        fileOpen.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        fileOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File addFile = openner.showOpenDialog(stage);
                if (addFile != null) {
                    String add = addFile.getAbsolutePath();

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Continue ?");
                    alert.setHeaderText("Did you save your content before open ?");

                    ButtonType cont = new ButtonType("Yes, continue");
                    ButtonType sav = new ButtonType("No, save");
                    ButtonType canc = new ButtonType("Cancel");

                    alert.getButtonTypes().clear();
                    alert.getButtonTypes().addAll(canc, cont, sav);

                    Optional<ButtonType> option = alert.showAndWait();

                    if (option.get() == cont) {
                        funNew(grid);
                        funOpen(grid, add);
                    } else if (option.get() == sav) {
                        fileSave.fire();
                    }
                }
            }
        });
        file.getItems().addAll(fileNew, fileOpen, fileSave);

        Menu other = new Menu("Other");
        MenuItem otherHelp = new MenuItem("Help");
        otherHelp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WebView page = new WebView();
                page.getEngine().load(getClass().getClassLoader().getResource("help.html").toExternalForm());
                Stage helpPage = new Stage();
                Scene helpScene = new Scene(page);
                helpPage.setScene(helpScene);
                helpPage.show();
            }
        });
        MenuItem itemsRefreshView = new MenuItem("Refresh view (you may regret it)");
        itemsRefreshView.setAccelerator(KeyCombination.keyCombination("F5"));
        itemsRefreshView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!smt.getAllStates().isEmpty()) {
                    (new StateOpti(smt)).optimise(smt, grid);
                }
            }
        });
        other.getItems().addAll(otherHelp, itemsRefreshView);

        menubar.getMenus().addAll(file, other);
        return menubar;
    }

    private static void funNew(FsmPane grid) {
        smt.clean();
        Object[] copie = grid.getChildren().toArray();
        for (Object obj : copie) {
            if (obj.getClass().isInstance(new StatePane())) {
                StatePane s = (StatePane) obj;
                System.out.println(s.text.getText());
                s.remove();
                grid.getChildren().remove(s);
            }
        }
    }

    private static void funSave(FsmPane grid, String add) {
        if (add != null) {
            switch (add.substring(add.lastIndexOf(".") + 1)) {
                case "java":
                    (new SaveToJava(smt, add)).start();
                    break;
                case "sma":
                    (new SaveToSmala(smt, add)).start();
                    break;
                case "json": {
                    try {
                        StateMachineSave sms = new StateMachineSave(smt, add);
                        for (int i = 0; i < smt.getAllStates().size(); i++) {
                            System.out.println("Etat " + Integer.toString(i) + " : " + smt.getState(i));
                        }
                        sms.save(add);
                    } catch (Exception e) {
                        throw new UnsupportedOperationException("Exception : Fail Saving");
                    }
                    break;
                }
            }
        }
    }

    private static void funOpen(FsmPane grid, String add) {
        if (add != null) {
            switch (add.substring(add.lastIndexOf(".") + 1)) {
                case "java": {
                    try {
                        OpenJava javaOpener = new OpenJava(add);
                        smt = javaOpener.translate();
                        SmtToGui GuiCreator = new SmtToGui();
                        GuiCreator.setGUI(grid);
                    } catch (Exception ex) {
                        throw new UnsupportedOperationException("Exception - Open Failure");
                    }

                    break;
                }
                case "sma": {
                    try {
                        OpenSmala smalaOpener = new OpenSmala(add);
                        smt = smalaOpener.traduction();
                        SmtToGui GuiCreator = new SmtToGui();
                        GuiCreator.setGUI(grid);
                    } catch (Exception ex) {
                        throw new UnsupportedOperationException("Exception - Open Failure");
                    }

                    break;
                }
                case "json": {
                    try {
                        StateMachineSave save = new StateMachineSave(smt, add);
                        save.open(add);
                        SmtToGui GuiCreator = new SmtToGui();
                        GuiCreator.setGUI(grid);
                    } catch (Exception ex) {
                        Logger.getLogger(FsmEditorApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }

            }
        }
    }
}
