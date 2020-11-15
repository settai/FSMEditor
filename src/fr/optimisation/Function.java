/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.optimisation;

/**
 * An interface that represents math function
 *
 * @author KNAFF Lucas
 */
public interface Function {

    /**
     * Execution of the function.
     * <p>
     * The function is {@code Vector -> Vector}
     *
     * @param X the input of the function
     * @return f(X)
     */
    public Vector exec(Vector X);
}
