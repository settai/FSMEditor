/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

/**
 * A javaFx component that represent an arrow
 *
 * @author SETTAI Yassine
 */
public class Arrow extends Group {

    private final CubicCurve line;
    private final static String STYLE = "-fx-border-color: red;";

    /**
     *
     */
    public Arrow() {
        this(new CubicCurve(), new Line(), new Line());
    }

    /**
     * Creation of arrow with start and end position
     */
    public Arrow(double startX, double startY, double endX, double endY) {
        this(new CubicCurve(), new Line(), new Line());
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
    }

    private static final double ARROWLENGTH = 20;
    private static final double ARROWWIDTH = 7;

    private Arrow(CubicCurve line, Line arrow1, Line arrow2) {
        super(line, arrow1, arrow2);
        this.line = line;

        line.setStroke(Color.BLACK);
        line.setFill(Color.TRANSPARENT);

        InvalidationListener updater = o -> {
            double ex = getEndX();
            double ey = getEndY();
            double sx = getControlX2();
            double sy = getControlY2();

            arrow1.setEndX(ex);
            arrow1.setEndY(ey);
            arrow2.setEndX(ex);
            arrow2.setEndY(ey);

            if (ex == sx && ey == sy) {
                // arrow parts of length 0
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            } else {
                double factor = ARROWLENGTH / Math.hypot(sx - ex, sy - ey);
                double factorO = ARROWWIDTH / Math.hypot(sx - ex, sy - ey);

                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                // part ortogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;

                arrow1.setStartX(ex + dx - oy);
                arrow1.setStartY(ey + dy + ox);
                arrow2.setStartX(ex + dx + oy);
                arrow2.setStartY(ey + dy - ox);
            }
        };

        // add updater to properties
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        controlX2Property().addListener(updater);
        controlY2Property().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);

        updater.invalidated(null);
    }

    /**
     *
     * @param value
     */
    public final void setStartX(double value) {
        line.setStartX(value);
    }

    /**
     *
     * @return
     */
    public final double getStartX() {
        return line.getStartX();
    }

    /**
     *
     * @return
     */
    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    /**
     *
     * @param value
     */
    public final void setStartY(double value) {
        line.setStartY(value);
    }

    /**
     *
     * @return
     */
    public final double getStartY() {
        return line.getStartY();
    }

    /**
     *
     * @return
     */
    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    /**
     *
     * @param value
     */
    public final void setEndX(double value) {
        line.setEndX(value);
    }

    /**
     *
     * @return
     */
    public final double getEndX() {
        return line.getEndX();
    }

    /**
     *
     * @return
     */
    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    /**
     *
     * @param value
     */
    public final void setEndY(double value) {
        line.setEndY(value);
    }

    /**
     *
     * @return
     */
    public final double getEndY() {
        return line.getEndY();
    }

    /**
     *
     * @return
     */
    public final DoubleProperty controlX1Property() {
        return line.controlX1Property();
    }

    /**
     *
     * @param value
     */
    public final void setControlX1(double value) {
        line.setControlX1(value);
    }

    /**
     *
     * @return
     */
    public final DoubleProperty controlY1Property() {
        return line.controlY1Property();
    }

    /**
     *
     * @return
     */
    public final double getControlX1() {
        return line.getControlX1();
    }

    /**
     *
     * @param value
     */
    public final void setControlY1(double value) {
        line.setControlY1(value);
    }

    /**
     *
     * @return
     */
    public final double getControlY1() {
        return line.getControlY1();
    }

    /**
     *
     * @return
     */
    public final DoubleProperty controlX2Property() {
        return line.controlX2Property();
    }

    /**
     *
     * @param value
     */
    public final void setControlX2(double value) {
        line.setControlX2(value);
    }

    /**
     *
     * @return
     */
    public final DoubleProperty controlY2Property() {
        return line.controlY2Property();
    }

    /**
     *
     * @return
     */
    public final double getControlX2() {
        return line.getControlX2();
    }

    /**
     *
     * @param value
     */
    public final void setControlY2(double value) {
        line.setControlY2(value);
    }

    /**
     *
     * @return
     */
    public final double getControlY2() {
        return line.getControlY2();
    }

    /**
     *
     * @return
     */
    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }

    /**
     *
     * @return
     */
    public CubicCurve getLine() {
        return line;
    }

}
