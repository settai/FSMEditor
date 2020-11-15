/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.gui.views;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;

/**
 * Specification of Pane that implements IHomothetic et ITranslatable
 *
 * @author SETTAI Yassine
 */
public class TranslatableHomotethicPane extends Pane implements IHomothetic, ITranslatable {

    // Modèle stockant le facteur de zoom 
    final DoubleProperty scale = new SimpleDoubleProperty(1.0);
    private final static String STYLE = "-fx-border-color: blue;"; //border
    // Les transformations sont accumulées dans une seule matrice
    private final Affine transforms;

    /**
     *
     */
    public TranslatableHomotethicPane() {
        super();

        // initialiser la matrice des transformations et l'assigner à ce composant
        transforms = new Affine();
        getTransforms().add(transforms);
        //setStyle(STYLE);
    }

    /**
     * Accesseur de la property gérant le facteur de zoom
     *
     * @return poignée vers la property gérant le facteur de zoom
     */
    public final DoubleProperty scaleProperty() {
        return scale;
    }

    @Override
    public final double getScale() {
        return scale.get();
    }

    @Override
    public void setScale(double newScale) {
        setScaleX(newScale);
        setScaleY(newScale);
        scale.set(newScale);
    }

    @Override
    public void setScale(double newScale, double pivotX, double pivotY) {
        appendScale(newScale / getScale(), pivotX, pivotY);

    }

    @Override
    public void appendScale(double deltaScale, double pivotX, double pivotY) {
        transforms.appendScale(deltaScale, deltaScale, pivotX, pivotY);
        scaleProperty().set(getScale() * deltaScale);
    }

    @Override
    public void translate(double dx, double dy) {
        //transforms.appendTranslation(dx,dy);
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);
    }

    /**
     *
     * @param style
     */
    public void setColor(String style) {

    }

    /**
     *
     * @param dx
     * @return
     */
    public boolean canTranslateX(double dx) {
        return true;
    }

    /**
     *
     * @param dy
     * @return
     */
    public boolean canTranslateY(double dy) {
        return true;
    }
}
