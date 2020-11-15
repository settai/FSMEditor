/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.liienac.statemachine.event;

/**
 *
 * @author ST
 */
import fr.liienac.statemachine.geometry.Point;

public class Hover<Item> extends PositionalEvent<Item> {

    /**
     * Constructor for mouse
     */
    public Hover(Point p_, Item s_) {
        super(p_, s_);
    }

    /**
     * Constructor for multitouch without orientation
     */
    public Hover(int cursorid_, Point p_, Item s_) {
        super(cursorid_, p_, s_);
    }

    /**
     * Constructor for multitouch without orientation
     */
    public Hover(int cursorid_, Point p_, Item s_, float angRad) {
        super(cursorid_, p_, s_, angRad);
    }
}
