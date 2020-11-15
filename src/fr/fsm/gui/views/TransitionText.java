/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views;

import fr.fsm.FsmEditorApplication;
import static fr.fsm.gui.views.StatePane.RADIUS;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A javaFx component that contain related to the transition
 *
 * @author SETTAI Yasine and SAKER Julien
 */
public class TransitionText extends TranslatableHomotethicPane {

    ComboBox event;
    private final TextField name;
    private final TextField guard;
    private final TextField action;

    /**
     *
     */
    protected TransitionPane trans;
    private final static String TEXTSTYLE = "-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 1 3 1 3;";

    /**
     *
     * @param tp
     */
    @SuppressWarnings("Convert2Lambda")
    public TransitionText(TransitionPane trans) {
        super();
        this.trans = trans;
        String events[] = {"Click", "Move", "Press", "Release"};

        VBox layout = new VBox();

        HBox eventBox = new HBox();
        eventBox.setAlignment(Pos.CENTER_LEFT);
        Text onText = new Text("on ");
        event = new ComboBox(FXCollections.observableArrayList(events));
        event.getSelectionModel().select("Click");
        event.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                StatePane state1 = trans.getState1();
                StatePane state2 = trans.getState2();
                FsmEditorApplication.smt.setTransitionEvent(state1.text.getText(), state2.text.getText(), newValue);
            }
        });
        eventBox.getChildren().addAll(onText, event);

        HBox nameBox = new HBox();
        nameBox.setAlignment(Pos.CENTER_LEFT);
        Text nameText = new Text("name ");
        name = new TextField();
        name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                StatePane state1 = trans.getState1();
                StatePane state2 = trans.getState2();
                FsmEditorApplication.smt.setTransitionName(state1.text.getText(), state2.text.getText(), newValue);
            }
        });
        nameBox.getChildren().addAll(nameText, name);

        HBox guardBox = new HBox();
        guardBox.setAlignment(Pos.CENTER_LEFT);
        Text ifText = new Text("if ");
        guard = new TextField();
        guard.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                StatePane state1 = trans.getState1();
                StatePane state2 = trans.getState2();
                FsmEditorApplication.smt.setTransitionGuard(state1.text.getText(), state2.text.getText(), newValue);
            }
        });
        guardBox.getChildren().addAll(ifText, guard);

        HBox actionBox = new HBox();
        actionBox.setAlignment(Pos.CENTER_LEFT);
        Text doText = new Text("| do ");
        action = new TextField();
        action.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                StatePane state1 = trans.getState1();
                StatePane state2 = trans.getState2();
                FsmEditorApplication.smt.setTransitionAction(state1.getText(), state2.getText(), newValue);
            }

        });
        actionBox.getChildren().addAll(doText, action);

        name.setStyle(TEXTSTYLE);
        event.setStyle(TEXTSTYLE);
        guard.setStyle(TEXTSTYLE);
        action.setStyle(TEXTSTYLE);

        layout.getChildren().addAll(nameBox, eventBox, guardBox, actionBox);
        getChildren().add(layout);
    }

    /**
     *
     * @return
     */
    public String getGuard() {
        return guard.getText();
    }

    /**
     *
     * @return
     */
    public String getEvent() {
        return (String) event.getSelectionModel().selectedItemProperty().getValue();
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name.getText();
    }

    /**
     *
     * @return
     */
    public String getAction() {
        return action.getText();
    }

    /**
     *
     * @param value
     */
    public void setName(String value) {
        name.setText(value);
    }

    /**
     *
     * @param value
     */
    public void setGuard(String value) {
        guard.setText(value);
    }

    /**
     *
     * @param value
     */
    public void setAction(String value) {
        action.setText(value);
    }

    /**
     *
     * @param value
     */
    public void setEvent(String value) {
        ArrayList<String> tab = new ArrayList();
        tab.add("Click");
        tab.add("Move");
        tab.add("Press");
        tab.add("Release");
        int ind = tab.indexOf(value);
        if (ind > -1) {
            event.getSelectionModel().select(value);
        }
    }

    /**
     * Override the method to set limit of box translation on X axis
     *
     * @param dx
     * @return
     */
    @Override
    public boolean canTranslateX(double dx) {
        double x = localToParent(0, 0).getX();
        double w = localToParent(2 * RADIUS, 2 * RADIUS).getX() - x;
        double W = ((TranslatableHomotethicPane) this.getParent()).getWidth();
        return (x + dx >= 0 && x + dx + w <= W);
    }

    /**
     * Override the method to set limit of box translation on Y axis
     *
     * @param dy
     * @return
     */
    @Override
    public boolean canTranslateY(double dy) {
        double y = localToParent(0, 0).getY();
        double h = localToParent(2 * RADIUS, 2 * RADIUS).getY() - y;
        double H = ((TranslatableHomotethicPane) this.getParent()).getHeight();
        return (y + dy >= 0 && y + dy + h <= H);
    }
}
