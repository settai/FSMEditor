/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views;

import fr.fsm.FsmEditorApplication;
import fr.fsm.file.java.TransitionExistingException;
import fr.fsm.gui.controls.TransitionManager;
import fr.fsm.gui.views.statemachines.DragStateMachine;
import fr.fsm.gui.views.statemachines.TransitionStateMachine;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * A Fsm Pane that represent the grid of the application
 *
 * @author SETTAI Yassine and SAKER Julien
 */
public class FsmPane extends TranslatableHomotethicPane {

    private final static String STYLE = "-fx-background-color: white;"; //whitesmoke

    /**
     *
     */
    public final static String WHITE = "-fx-stroke: white;";

    /**
     *
     */
    public final static String BLACK = "-fx-stroke: grey;";
    private final static int WIDTH = 600;
    private final static int HEIGHT = 600;
    private final static int GRID_OFFSET = 25;
    private Line[] columnes = new Line[WIDTH];
    private Line[] lines = new Line[HEIGHT];
    private final Stage stage; //to get the window size

    /**
     *
     */
    protected final Group grid = new Group();

    /**
     *
     * @param stage
     */
    @SuppressWarnings("Convert2Lambda")
    public FsmPane(Stage stage) {
        super();
        this.stage = stage;

        super.getChildren().add(grid);
        //grid.setMouseTransparent(true);

        setStyle(STYLE);
        setMinSize(WIDTH, HEIGHT);

        for (int i = 0; i < WIDTH; i += GRID_OFFSET) {
            columnes[i / GRID_OFFSET] = new Line(i, 0, i, HEIGHT);
            grid.getChildren().add(columnes[i / GRID_OFFSET]);
            columnes[i / GRID_OFFSET].setStyle(WHITE);
        }
        for (int i = 0; i < HEIGHT; i += GRID_OFFSET) {
            lines[i / GRID_OFFSET] = new Line(0, i, WIDTH, i);
            grid.getChildren().add(lines[i / GRID_OFFSET]);
            lines[i / GRID_OFFSET].setStyle(WHITE);
        }
        scaleProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println(newValue);
                for (int i = 0; i < WIDTH / GRID_OFFSET; i++) {
                    columnes[i].setStrokeWidth(1 / newValue.doubleValue());
                }
                for (int i = 0; i < HEIGHT / GRID_OFFSET; i++) {
                    lines[i].setStrokeWidth(1 / newValue.doubleValue());
                }
            }

        });
    }

    /**
     * Create a transition and set the state 1
     *
     * @param p
     * @param state
     * @param transitionStateMachine
     * @return
     */
    public TransitionPane createTransition(Point2D p, StatePane state, TransitionStateMachine transitionStateMachine) {
        TransitionPane transition = new TransitionPane(p.getX(), p.getY());
        new TransitionManager(transition, new DragStateMachine(), transitionStateMachine);
        transition.setState1(state);
        grid.getChildren().add(transition);
        transition.toFront();
        transition.updateLayout();
        return transition;
    }

    /**
     * apply the transition and set state 2
     *
     * @param transition
     * @param p
     */
    public void applyTransition(TransitionPane transition, Point2D p) {
        StatePane state1 = transition.getState1();
        StatePane state2 = transition.getState2();

        if (state2 != null) {
            state1.add(transition);
            if (state2 != state1) {
                state2.add(transition);
                transition.updateArrow();
            } else if (state1 == state2) {
                transition.self(p);
            }
            try {
                System.out.println(state1.text.toString());
                String es = state1.text.getText();
                String ef = state2.text.getText();
                //String nom = transition.getTransitionText().getName();
                String event = transition.getTransitionText().getEvent();
                String action = transition.getTransitionText().getAction();
                String guard = transition.getTransitionText().getGuard();
                FsmEditorApplication.smt.setTransition(es, ef, event, action, guard);
                transition.getTransitionText().setName(FsmEditorApplication.smt.getTransitionName(state1.getText(), state2.getText()));
            } catch (TransitionExistingException ex) {
                grid.getChildren().remove(transition);
            }
        } else {
            grid.getChildren().remove(transition);
        }
    }

    /**
     *
     * @param style
     */
    @Override
    public void setColor(String style) {
        for (int i = 0; i < WIDTH / GRID_OFFSET; i++) {
            columnes[i].setStyle(style);
        }
        for (int i = 0; i < HEIGHT / GRID_OFFSET; i++) {
            lines[i].setStyle(style);
        }
    }

    /**
     *
     * @param dx
     * @return
     */
    @Override
    public boolean canTranslateX(double dx) {
        final Scene scene = getScene();
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final Point2D p0 = localToScene(0.0, 0.0);
        final Point2D plim = localToScene(WIDTH, HEIGHT);
        final double x = sceneCoord.getX() + p0.getX();
        final double w = sceneCoord.getX() + plim.getX();
        final double W = stage.getWidth();

        return ((x < 0 && dx >= 0) || (w > W && dx <= 0));
    }

    /**
     *
     * @param dy
     * @return
     */
    @Override
    public boolean canTranslateY(double dy) {
        final Scene scene = getScene();
        final Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
        final Point2D p0 = localToScene(0.0, 0.0);
        final Point2D parentCoord = this.getParent().localToScene(0, 0);
        final Point2D plim = localToScene(WIDTH, HEIGHT);
        final double y = sceneCoord.getY() + p0.getY() - parentCoord.getY() - 25;
        final double h = sceneCoord.getY() + plim.getY();
        final double H = stage.getHeight();

        return ((y < 0 && dy >= 0) || (h > H && dy <= 0));
    }
}
