/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views.statemachines;

import fr.fsm.gui.views.TranslatableHomotethicPane;
import fr.fsm.gui.views.FsmPane;
import fr.liienac.statemachine.StateMachine;
import fr.liienac.statemachine.event.*;
import fr.liienac.statemachine.geometry.Point;

/**
 * This class is a implementation of state machine that control the drag of
 * elements Fsm pane
 *
 * @author SETTAI Yassine
 */
public class DragStateMachine extends StateMachine {

    private Point p0;
    private TranslatableHomotethicPane item;
    //private final double seuil = 1;

    /**
     *
     */
    public State idle = new State() {
        Transition press = new Transition<Press>() {
            @Override
            protected void action() {
                item = (TranslatableHomotethicPane) evt.graphicItem;
                p0 = evt.p;
            }

            @Override
            protected State goTo() {
                return dragging;
            }
        };
    };

    /**
     *
     */
    public State dragging = new State() {
        Transition move = new Transition<Move>() {
            @Override
            protected void action() {
                double dx = evt.p.x - p0.x;
                double dy = evt.p.y - p0.y;
                if (item.canTranslateX(dx)) {
                    item.translate(dx, 0);
                }
                if (item.canTranslateY(dy)) {
                    item.translate(0, dy);
                }
            }
        };

        Transition release = new Transition<Release>() {
            @Override
            protected void action() {

            }

            @Override
            protected State goTo() {
                return idle;
            }
        };

        @Override
        public void enter() {
            item.setColor(FsmPane.BLACK);
        }

        @Override
        public void leave() {
            item.setColor(FsmPane.WHITE);
            item = null;
        }
    };
}
