/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.controls;

import fr.fsm.gui.views.TransitionPane;
import fr.fsm.gui.views.TransitionText;
import fr.fsm.gui.views.statemachines.DragStateMachine;
import fr.fsm.gui.views.statemachines.TransitionStateMachine;
import fr.liienac.statemachine.event.Move;
import fr.liienac.statemachine.event.Press;
import fr.liienac.statemachine.event.Release;
import fr.liienac.statemachine.geometry.Point;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

/**
 * A class the control the replace replace transition and the position of the
 * text box
 *
 * @author SETTAI Yassine and SAKER Julien
 */
public class TransitionManager {

    private boolean isShiftdown = false;

    /**
     *
     * @param tp
     * @param dsm
     * @param tsm
     */
    @SuppressWarnings("Convert2Lambda")
    public TransitionManager(TransitionPane transition, DragStateMachine dragStateMachine, TransitionStateMachine transitionStateMachine) {
        TransitionText text = transition.getTransitionText();

        transition.addEventFilter(MouseEvent.MOUSE_PRESSED, (final MouseEvent mouseEvent) -> {
            if (mouseEvent.isShiftDown()) {
                isShiftdown = true;
                Point2D p = transition.localToParent(mouseEvent.getX(), mouseEvent.getY());
                transitionStateMachine.handleEvent(new Press(new Point(p.getX(), p.getY()), transition));
                mouseEvent.consume();
            }
        });

        transition.addEventFilter(MouseEvent.MOUSE_DRAGGED, (final MouseEvent mouseEvent) -> {
            if (isShiftdown) {
                Point2D p = transition.localToParent(mouseEvent.getX(), mouseEvent.getY());
                transitionStateMachine.handleEvent(new Move(new Point(p.getX(), p.getY()), transition));
                mouseEvent.consume();
            }
        });

        transition.setOnDragDetected(e -> transition.startFullDrag());

        transition.addEventFilter(MouseEvent.MOUSE_RELEASED, (final MouseEvent mouseEvent) -> {
            if (isShiftdown) {
                isShiftdown = false;
                Point2D p = transition.localToParent(mouseEvent.getX(), mouseEvent.getY());
                transitionStateMachine.handleEvent(new Release(new Point(p.getX(), p.getY()), transition));
                mouseEvent.consume();
            }
        });

        text.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                Point p0 = new Point(mouseEvent.getX(), mouseEvent.getY());
                dragStateMachine.handleEvent(new Press(p0, text));
                mouseEvent.consume();
            }
        });

        text.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                Point p0 = new Point(mouseEvent.getX(), mouseEvent.getY());
                dragStateMachine.handleEvent(new Move(p0, text));
                mouseEvent.consume();
            }
        });

        text.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                Point p0 = new Point(mouseEvent.getX(), mouseEvent.getY());
                dragStateMachine.handleEvent(new Release(p0, text));
                mouseEvent.consume();
            }
        });
    }
}
