/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.controls;

import fr.fsm.FsmEditorApplication;
import fr.fsm.gui.views.FsmPane;
import fr.fsm.gui.views.StatePane;
import fr.fsm.gui.views.TransitionPane;
import fr.fsm.gui.views.statemachines.DragStateMachine;
import fr.fsm.gui.views.statemachines.TransitionStateMachine;
import fr.liienac.statemachine.event.*;
import fr.liienac.statemachine.geometry.Point;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * A class that control the drag of the grid pane
 *
 * @author SETTAI Yassine, SAKER Julien and KNAFF Lucas
 */
public class PanManager {

    /**
     *
     * @param gp
     * @param dsm
     */
    @SuppressWarnings("Convert2Lambda")
    public PanManager(FsmPane grid, DragStateMachine dragStateMachine) {
        grid.addEventHandler(MouseEvent.MOUSE_PRESSED, (final MouseEvent mouseEvent) -> {
            Point p1 = new Point(mouseEvent.getX(), mouseEvent.getY());
            dragStateMachine.handleEvent(new Press(p1, grid));
        });

        grid.addEventHandler(MouseEvent.MOUSE_DRAGGED, (final MouseEvent mouseEvent) -> {
            Point p1 = new Point(mouseEvent.getX(), mouseEvent.getY());
            dragStateMachine.handleEvent(new Move(p1, grid));
            mouseEvent.consume();
        });

        grid.addEventHandler(MouseEvent.MOUSE_RELEASED, (final MouseEvent mouseEvent) -> {
            Point p0 = new Point(mouseEvent.getX(), mouseEvent.getY());
            dragStateMachine.handleEvent(new Release(p0, grid));
        });

        //Create state
        grid.addEventHandler(MouseEvent.MOUSE_CLICKED, (final MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    StatePane state = new StatePane();
                    state.text.setText("Sd" + FsmEditorApplication.smt.setDefaultState(mouseEvent.getX() - StatePane.RADIUS, mouseEvent.getY() - StatePane.RADIUS));
                    new StateManager(state, new TransitionStateMachine(grid));
                    grid.getChildren().add(state);
                    new DragManager(state, new DragStateMachine());
                    state.setLayoutX(mouseEvent.getX() - state.getRadius());
                    state.setLayoutY(mouseEvent.getY() - state.getRadius());
                    state.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (event.getButton().equals(MouseButton.SECONDARY)) {
                                if (event.getClickCount() == 1) {
                                    grid.getChildren().remove(state);
                                    int index = FsmEditorApplication.smt.getIndex(state.getText());
                                    List<TransitionPane> trans = state.getTransitions();
                                    FsmEditorApplication.smt.removeState(state.text.getText());
                                    //System.out.println("Ceci est nom:"+state.text.getText());
                                    //System.out.println("Ceci est taille:"+Integer.toString(trans.size()));

                                    //System.out.println("mhhhhh");
                                    for (int i = 0; i < trans.size(); i++) {
                                        grid.getChildren().remove(trans.get(i));
                                        //trans.get(i).remove();
                                    }
                                    state.remove();
                                }
                            }
                        }
                    });

                }
            }
        });
    }

}
