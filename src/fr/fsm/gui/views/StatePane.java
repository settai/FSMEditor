/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views;

import fr.fsm.FsmEditorApplication;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 * A javaFx component that represent a state
 *
 * @author KNAFF Lucas, SAKER Julien and SETTAI Yassine
 */
public class StatePane extends TranslatableHomotethicPane {

    /**
     *
     */
    public final static int RADIUS = 35;
    private final static String TEXTSTYLE = "-fx-background-color: -fx-control-inner-background;-fx-background-insets: 0; -fx-padding: 1 3 1 3;";

    private final ArrayList<TransitionPane> transitions;
    private final StackPane layout;
    private final Circle circle;

    /**
     *
     */
    public TextField text;

    /**
     * Create a state machine
     */
    @SuppressWarnings({"Convert2Diamond", "Convert2Lambda"})
    public StatePane() {
        super();

        transitions = new ArrayList<TransitionPane>();
        layout = new StackPane();
        circle = new Circle(RADIUS);
        circle.setStroke(Color.BLACK);
        circle.setStrokeType(StrokeType.INSIDE);
        circle.setFill(Color.WHITE);

        text = new TextField();
        text.setPrefWidth(RADIUS * 3 / 2);
        text.setMaxWidth(RADIUS * 3 / 2);
        //text.setMouseTransparent(true);

        text.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    getScene().getRoot().requestFocus();
                }
            }
        });

        text.setAlignment(Pos.CENTER);
        text.setStyle(TEXTSTYLE);
        text.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (FsmEditorApplication.smt.getIndex(oldValue) != -1) {
                    FsmEditorApplication.smt.renameState(newValue, FsmEditorApplication.smt.getIndex(oldValue));

                    Pattern p = Pattern.compile("^Sd([0-9]+)$");
                    Matcher m1 = p.matcher(newValue);
                    Matcher m2 = p.matcher(oldValue);
                    if ((!m1.find()) && m2.find()) {
                        FsmEditorApplication.smt.majDefaultNames();
                    }
                }
            }
        });

        this.layoutXProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                FsmEditorApplication.smt.setXCoord(text.getText(), newValue.doubleValue());
            }
        });
        this.layoutYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                FsmEditorApplication.smt.setYCoord(text.getText(), newValue.doubleValue());
            }
        });

        layout.getChildren().addAll(circle, text);
        super.getChildren().add(layout);

    }

    /**
     * Translate the state machine by dx and dy
     *
     * @param dx
     * @param dy
     */
    @Override
    public void translate(double dx, double dy) {
        super.translate(dx, dy); //To change body of generated methods, choose Tools | Templates.
        for (TransitionPane t : transitions) {
            t.updateArrow(dx, dy);
        }
    }

    /**
     *
     * @param trans
     */
    public void add(TransitionPane trans) {
        transitions.add(trans);
    }

    /**
     *
     * @return
     */
    public int getRadius() {
        return RADIUS;
    }

    /**
     *
     * @return
     */
    public Circle getCircle() {
        return circle;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return text.getText();
    }

    /**
     *
     * @param value
     */
    public void setText(String value) {
        text.setText(value);
    }

    /**
     *
     * @return
     */
    public List<TransitionPane> getTransitions() {
        return transitions;
    }

    /**
     * Remove a state
     */
    public void remove() {

        for (TransitionPane t : (List<TransitionPane>) transitions.clone()) {
            t.remove();
        }
        transitions.clear();
        layout.getChildren().removeAll(circle, text);
        getChildren().remove(layout);
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
     * Override the method to set limit of box translation on X axis
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
