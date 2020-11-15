/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.creation;

import fr.fsm.FsmEditorApplication;
import fr.fsm.gui.controls.DragManager;
import fr.fsm.gui.controls.StateManager;
import fr.fsm.gui.views.FsmPane;
import fr.fsm.gui.views.StatePane;
import fr.fsm.gui.views.TransitionPane;
import fr.fsm.gui.views.statemachines.DragStateMachine;
import fr.fsm.gui.views.statemachines.TransitionStateMachine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * A class that is used to generate the GUI according to the StateMachineTab
 * architected previously
 *
 * @author SAKER Julien
 */
public class SmtToGui {

    /**
     *
     */
    protected HashMap<String, StatePane> states;

    /**
     *
     */
    @SuppressWarnings("Convert2Diamond")
    public SmtToGui() {
        this.states = new HashMap<String, StatePane>();
    }

    @SuppressWarnings("Convert2Lambda")
    private void setStatePane(String etat, FsmPane grid) {
        Double x = FsmEditorApplication.smt.getXCoord(etat);
        Double y = FsmEditorApplication.smt.getYCoord(etat);
        StatePane state = new StatePane();
        state.setText(etat);
        new StateManager(state, new TransitionStateMachine(grid));
        grid.getChildren().add(state);
        states.put(etat, state);
        new DragManager(state, new DragStateMachine());
        state.setLayoutX(x);
        state.setLayoutY(y);
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

    private void setTransitionPaneAndText(String es, String ef, String nom, String type, FsmPane grid) {
        Double startX = FsmEditorApplication.smt.getXCoord(es);
        Double startY = FsmEditorApplication.smt.getYCoord(es);
        Double endX = FsmEditorApplication.smt.getXCoord(ef);
        Double endY = FsmEditorApplication.smt.getYCoord(ef);
        TransitionPane trans = grid.createTransition(new Point2D(startX, startY), states.get(es), new TransitionStateMachine(grid));
        //TransitionPane trans = new TransitionPane();
        //new TransitionManager(trans); 
        //trans.setState1(states.get(es));
        //grid.getChildren().add(trans);
        //trans.updateLayout();
        trans.setState2(states.get(ef));
        trans.updateLayout();
        if (es.equals(ef)) {
            states.get(es).add(trans);
            trans.self(new Point2D(endX, endY));
        } else {
            states.get(es).add(trans);
            states.get(ef).add(trans);
            trans.updateArrow();
        }
        trans.getTransitionText().setAction(FsmEditorApplication.smt.getTransitionAction(es, ef));
        trans.getTransitionText().setGuard(FsmEditorApplication.smt.getTransitionGuard(es, ef));
        trans.getTransitionText().setName(FsmEditorApplication.smt.getTransitionName(es, ef));
        trans.getTransitionText().setEvent(FsmEditorApplication.smt.getTransitionEvent(es, ef));
    }

    /**
     *
     * @param grid
     */
    public void setGUI(FsmPane grid) {
        for (int i = 0; i < FsmEditorApplication.smt.getAllStates().size(); i++) {
            this.setStatePane(FsmEditorApplication.smt.getState(i), grid);
        }
        states.entrySet().forEach((entry) -> {
            ArrayList<Hashtable<String, String>> tran = FsmEditorApplication.smt.getTransitionsEtats(entry.getKey());
            for (int j = 0; j < tran.size(); j++) {
                String es = entry.getKey();
                String ef = tran.get(j).get("etat_dest");
                String nom = tran.get(j).get("nom");
                String type = tran.get(j).get("type");
                this.setTransitionPaneAndText(es, ef, nom, type, grid);
            }
        });

    }
}
