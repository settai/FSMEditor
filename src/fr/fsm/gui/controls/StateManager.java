/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.controls;

import fr.fsm.gui.views.StatePane;
import fr.fsm.gui.views.statemachines.TransitionStateMachine;
import fr.liienac.statemachine.event.Hover;
import fr.liienac.statemachine.event.Move;
import fr.liienac.statemachine.event.Press;
import fr.liienac.statemachine.event.Release;
import fr.liienac.statemachine.geometry.Point;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

/**
 * A class that control the creation of transition between states
 *
 * @author RONSAIN Antoine and SETTAI Yassine
 */
public class StateManager {

    private boolean isShiftdown = false;

    /**
     *
     * @param sp
     * @param tsm
     */
    public StateManager(StatePane state, TransitionStateMachine transitionStateMachine) {
        state.addEventFilter(MouseEvent.MOUSE_PRESSED, (final MouseEvent mouseEvent) -> {
            if (mouseEvent.isShiftDown()) {
                isShiftdown = true;
                Point2D p = state.localToParent(mouseEvent.getX(), mouseEvent.getY());
                transitionStateMachine.handleEvent(new Press(new Point(p.getX(), p.getY()), state));
                mouseEvent.consume();
            }
        });

        state.addEventFilter(MouseEvent.MOUSE_DRAGGED, (final MouseEvent mouseEvent) -> {
            if (isShiftdown) {
                Point2D p = state.localToParent(mouseEvent.getX(), mouseEvent.getY());
                transitionStateMachine.handleEvent(new Move(new Point(p.getX(), p.getY()), state));
                mouseEvent.consume();
            }
        });

        state.setOnDragDetected(e -> state.startFullDrag()); //set replace drag with full drag

        state.addEventFilter(MouseEvent.MOUSE_RELEASED, (final MouseEvent mouseEvent) -> {
            if (isShiftdown) {
                isShiftdown = false;
                Point2D p = state.localToParent(mouseEvent.getX(), mouseEvent.getY());
                transitionStateMachine.handleEvent(new Release(new Point(p.getX(), p.getY()), state));
                mouseEvent.consume();
            }
        });

        state.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, (final MouseEvent mouseEvent) -> {
            transitionStateMachine.handleEvent(new Hover(new Point(mouseEvent.getX(), mouseEvent.getY()), state));
        });

        //Handler state.remove();
    }
}
