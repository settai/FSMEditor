/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views.statemachines;

import fr.fsm.gui.views.FsmPane;
import fr.fsm.gui.views.StatePane;
import fr.fsm.gui.views.TransitionPane;
import fr.liienac.statemachine.StateMachine;
import fr.liienac.statemachine.event.*;
import javafx.geometry.Point2D;

/**
 * This class is an implementation of state machine that control the creation
 * and placement of an transition between states
 *
 * @author RONSAIN Antoine and SETTAI Yassine
 */
public class TransitionStateMachine extends StateMachine {

    private static TransitionPane transition;
    private Point2D p0;
    private FsmPane grid;

    /**
     *
     * @param gp
     */
    public TransitionStateMachine(FsmPane grid) {
        super();
        this.grid = grid;
    }

    /**
     *
     */
    public State idle = new State() {
        Transition press = new Transition<Press>() { //Create transition
            @Override
            protected void action() {
                p0 = new Point2D(evt.p.x, evt.p.y);
                if (evt.graphicItem instanceof StatePane) {
                    transition = grid.createTransition(p0, (StatePane) evt.graphicItem, TransitionStateMachine.this);
                } else if (evt.graphicItem instanceof TransitionPane) {
                    transition = (TransitionPane) evt.graphicItem;
                    transition.replace();
                }
            }

            @Override
            protected State goTo() {
                return placing;
            }
        };

        State.Transition hovering = new State.Transition<Hover>() {
            @Override
            protected void action() {
                if (transition != null) {
                    transition.setState2((StatePane) evt.graphicItem);
                }
            }
        };

    };

    /**
     *
     */
    public State placing = new State() {
        Transition move = new Transition<Move>() { //Place transition
            @Override
            protected void action() {
                Point2D p = new Point2D(evt.p.x, evt.p.y);
                transition.placing(p);
            }
        };

        State.Transition hovering = new State.Transition<Hover>() {
            @Override
            protected void action() {
                transition.setState2((StatePane) evt.graphicItem);
            }
        };

        Transition release = new Transition<Release>() {
            @Override
            protected void action() {
                Point2D p = new Point2D(evt.p.x, evt.p.y);
                grid.applyTransition(transition, p);
                transition = null;
            }

            @Override
            protected State goTo() {
                return idle;
            }
        };
    };
}
