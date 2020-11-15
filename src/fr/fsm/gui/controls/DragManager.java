/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.controls;

import fr.fsm.gui.views.StatePane;
import fr.fsm.gui.views.statemachines.DragStateMachine;
import fr.liienac.statemachine.event.Move;
import fr.liienac.statemachine.event.Press;
import fr.liienac.statemachine.event.Release;
import fr.liienac.statemachine.geometry.Point;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * A class that control the drag of Fsm's pane
 *
 * @author SETTAI Yassine and KNAFF Lucas
 */
public class DragManager {

    private final DragStateMachine dragStateMachine;

    /**
     *
     * @param sp
     * @param dsm
     */
    @SuppressWarnings("Convert2Lambda")
    public DragManager(StatePane state, DragStateMachine drageStateMachine) {
        this.dragStateMachine = drageStateMachine;
        //circle = node.getCircle();

        state.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                Point p0 = new Point(mouseEvent.getX(), mouseEvent.getY());
                drageStateMachine.handleEvent(new Press(p0, state));
                mouseEvent.consume();
            }
        });

        state.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                Point p0 = new Point(mouseEvent.getX(), mouseEvent.getY());
                drageStateMachine.handleEvent(new Move(p0, state));
                mouseEvent.consume();
            }
        });

        state.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                Point p0 = new Point(mouseEvent.getX(), mouseEvent.getY());
                drageStateMachine.handleEvent(new Release(p0, state));
                mouseEvent.consume();
            }
        });

    }
}
