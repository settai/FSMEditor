/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views;

import fr.fsm.FsmEditorApplication;
import static java.lang.Double.min;
import static java.lang.Math.sqrt;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;

/**
 * A javaFx component that represent a transition (Arrow + Text)
 *
 * @author SETTAI Yassine
 */
public class TransitionPane extends TranslatableHomotethicPane {

    private Arrow arrow;
    private StatePane state1, state2;
    private TransitionText text;
    private final static String textStyle = "-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 1 3 1 3;";

    /**
     * Create empty transition
     */
    public TransitionPane() {
        super();
        arrow = new Arrow();
        text = new TransitionText(this);
        getChildren().addAll(arrow, text);
        text.toFront();
    }

    /**
     * Create a transition with a start point
     *
     * @param startX
     * @param startY
     */
    public TransitionPane(double startX, double startY) {
        super();
        arrow = new Arrow(startX, startY, startX, startY);
        arrow.setControlX1(startX);
        arrow.setControlY1(startY);
        arrow.setControlX2(startX);
        arrow.setControlY2(startY);
        text = new TransitionText(this);
        getChildren().addAll(arrow, text);
        text.toFront();
    }

    /**
     * Create transition with a start and end points
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public TransitionPane(double startX, double startY, double endX, double endY) {
        super();
        arrow = new Arrow(startX, startY, endX, endY);
        text = new TransitionText(this);
        getChildren().addAll(arrow, text);
        text.toFront();
    }

    /**
     *
     * @return
     */
    public TransitionText getTransitionText() {
        return text;
    }

    /**
     *
     * @param x
     */
    public void setStartX(double x) {
        arrow.setStartX(x);
    }

    /**
     *
     * @param y
     */
    public void setStartY(double y) {
        arrow.setStartY(y);
    }

    /**
     *
     * @param x
     */
    public void setEndX(double x) {
        arrow.setEndX(x);
    }

    /**
     *
     * @param y
     */
    public void setEndY(double y) {
        arrow.setEndY(y);
    }

    /**
     *
     * @param x
     */
    public void setControlX1(double x) {
        arrow.setControlX1(x);
    }

    /**
     *
     * @param y
     */
    public void setControlY1(double y) {
        arrow.setControlY1(y);
    }

    /**
     *
     * @param x
     */
    public void setControlX2(double x) {
        arrow.setControlX2(x);
    }

    /**
     *
     * @param y
     */
    public void setControlY2(double y) {
        arrow.setControlY2(y);
    }

    /**
     *
     * @return
     */
    public StatePane getState1() {
        return state1;
    }

    /**
     *
     * @param state1
     */
    public void setState1(StatePane state1) {
        this.state1 = state1;
    }

    /**
     *
     * @return
     */
    public StatePane getState2() {
        return state2;
    }

    /**
     *
     * @param state2
     */
    public void setState2(StatePane state2) {
        this.state2 = state2;
    }

    /**
     * Update the arrow start and end position to match state movement
     */
    public void updateArrow() {
        if (state2 != null && state1 != state2) {
            Point2D centre1 = localToParent(arrow.getStartX(), arrow.getStartY());
            Point2D control = localToParent(arrow.getControlX2(), arrow.getControlY2());
            double r1 = 0;
            if (state1 != null) {
                Circle c1 = state1.getCircle();
                r1 = c1.getRadius();
                centre1 = state1.localToParent(c1.getCenterX() + c1.getRadius(), c1.getCenterY() + r1);
            }
            Circle c2 = state2.getCircle();
            Point2D centre2 = state2.localToParent(c2.getCenterX() + c2.getRadius(), c2.getCenterY() + c2.getRadius());

            Point2D p1 = getCircleLineIntersectionPoint(centre1, control, centre1, r1); // get the interaction point between the circle and the line
            setStartX(p1.getX());
            setStartY(p1.getY());
            Point2D p2 = getCircleLineIntersectionPoint(centre2, control, centre2, c2.getRadius()); // get the interaction point between the circle and the line
            setEndX(p2.getX());
            setEndY(p2.getY());

            Point2D ctrl = p1.midpoint(p2);
            setControlX1(ctrl.getX());
            setControlY1(ctrl.getY());
            setControlX2(ctrl.getX());
            setControlY2(ctrl.getY());
            updateLayout();
        }
    }

    /**
     * Update the arrow between the start point and the current point (used for
     * placing)
     *
     * @param bp
     */
    public void updateArrow(Point2D bp) {
        Point2D p = localToParent(arrow.getStartX(), arrow.getStartY());
        if (state1 != null) {
            Circle c = state1.getCircle();
            Point2D centre = state1.localToParent(c.getCenterX() + c.getRadius(), c.getCenterY() + c.getRadius());
            p = TransitionPane.getCircleLineIntersectionPoint(centre, bp, centre, c.getRadius());    // get the interaction point between the circle and the line
        }
        Point2D ctrl = bp.midpoint(p);
        setStartX(p.getX());
        setStartY(p.getY());
        setControlX1(ctrl.getX());
        setControlY1(ctrl.getY());
        setControlX2(ctrl.getX());
        setControlY2(ctrl.getY());
        setEndX(bp.getX());
        setEndY(bp.getY());
        updateLayout();
    }

    /**
     * update the self arrow by translating
     *
     * @param dx
     * @param dy
     */
    void updateArrow(double dx, double dy) {
        super.translate(dx, dy);
        updateArrow();
    }

    /**
     * Change the Layout position based on the min position to match perfectly
     * the layout for events
     */
    public void updateLayout() {
        double minX = min(arrow.getStartX(), min(arrow.getControlX1(), min(arrow.getControlX2(), arrow.getEndX())));
        double minY = min(arrow.getStartY(), min(arrow.getControlY1(), min(arrow.getControlY2(), arrow.getEndY())));
        setLayoutX(minX);
        setLayoutY(minY);
        setStartX(arrow.getStartX() - minX);
        setStartY(arrow.getStartY() - minY);
        setEndX(arrow.getEndX() - minX);
        setEndY(arrow.getEndY() - minY);
        setControlX1(arrow.getControlX1() - minX);
        setControlY1(arrow.getControlY1() - minY);
        setControlX2(arrow.getControlX2() - minX);
        setControlY2(arrow.getControlY2() - minY);
    }

    /**
     * get the interaction point between the circle and the line
     *
     * @param pointA
     * @param pointB
     * @param center
     * @param radius
     * @return
     */
    public static Point2D getCircleLineIntersectionPoint(Point2D pointA,
            Point2D pointB, Point2D center, double radius) {
        double baX = pointB.getX() - pointA.getX();
        double baY = pointB.getY() - pointA.getY();
        double caX = center.getX() - pointA.getX();
        double caY = center.getY() - pointA.getY();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return null;
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point2D p1 = new Point2D(pointA.getX() - baX * abScalingFactor1, pointA.getY()
                - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return p1;
        }
        Point2D p2 = new Point2D(pointA.getX() - baX * abScalingFactor2, pointA.getY()
                - baY * abScalingFactor2);;

        if (p1.distance(pointB) > p2.distance(pointB)) {
            return p2;
        } else {
            return p1;
        }
    }

    /**
     *
     * @return
     */
    public CubicCurve getArrow() {
        return arrow.getLine();
    }

    /**
     * Translate the control point to incubating the line
     *
     * @param dx
     * @param dy
     */
    @Override
    public void translate(double dx, double dy) {
        setControlX1(arrow.getControlX1() + dx);
        setControlY1(arrow.getControlY1() + dy);
        setControlX2(arrow.getControlX2() + dx);
        setControlY2(arrow.getControlY2() + dy);
    }

    /**
     * make a self arrow on state1 (= state2)
     *
     * @param p
     */
    public void self(Point2D p) {
        double r = state1.getCircle().getRadius();
        Point2D start = state1.localToParent(r - r * sqrt(2) / 2, r - r * sqrt(2) / 2);
        Point2D end = state1.localToParent(r + r * sqrt(2) / 2, r - r * sqrt(2) / 2);
        Point2D ctrl1 = state1.localToParent(r - r * sqrt(2) / 2, -r * 1.5);
        Point2D ctrl2 = state1.localToParent(r + r * sqrt(2) / 2, -r * 1.5);

        setStartX(start.getX());
        setStartY(start.getY());
        setControlX1(ctrl1.getX());
        setControlY1(ctrl1.getY());
        setControlX2(ctrl2.getX());
        setControlY2(ctrl2.getY());
        setEndX(end.getX());
        setEndY(end.getY());
        updateLayout();
    }

    /**
     * Placing the arrow on the current position
     *
     * @param p
     */
    public void placing(Point2D p) {
        if (state1.contains(state1.parentToLocal(p))) {
            self(p);
        } else {
            updateArrow(p);
        }
    }

    /**
     * Remove a transition
     */
    public void remove() {
        if (state1 != null && state2 != null) {
            state1.getTransitions().remove(this);
            state2.getTransitions().remove(this);
        }
        getChildren().removeAll(arrow, text);
    }

    /**
     * Replace a transition
     */
    public void replace() {
        if (state1 != null && state2 != null) {
            state1.getTransitions().remove(this);
            state2.getTransitions().remove(this);
        }
        FsmEditorApplication.smt.removeTransition(state1.getText(), state2.getText());
        state2 = null;
    }
}
