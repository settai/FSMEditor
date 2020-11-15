/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.file.java;

/**
 *
 * @author julien
 */
public class TransitionExistingException extends Exception {

    /**
     *
     * @param string
     */
    public TransitionExistingException(String exception__State_not_exists) {
        System.out.println("Exception : Tranistion already exists");
    }

}
