/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.file.java;

import fr.fsm.FsmEditorApplication;
import fr.fsm.StateMachineTab;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A class that is used to save a java code of a states machine. Only need an
 * address and a states machine table for the creator.
 *
 * Only one method which is start and who send back the code in java at the
 * address you asked for.
 *
 * @author RONSAIN Antoine and SAKER Julien
 */
public class SaveToJava {

    private final StateMachineTab tab;
    private final String address;

    /**
     *
     * @param smt
     * @param string
     */
    public SaveToJava(StateMachineTab tab, String add) {
        this.tab = tab;
        this.address = add;
    }

    /**
     *
     */
    public void start() {
        try {

            boolean patternTest1;
            boolean patternTest2;
            ArrayList<String> states;
            ArrayList<Hashtable<String, String>> trans;
            Hashtable<String, Integer> imp;

            File ff = new File(address); // définir l'arborescence
            ff.createNewFile();
            FileWriter ffw = new FileWriter(ff);

            states = tab.getAllStates();
            imp = tab.getImports();

            ffw.write("import fr.liienac.statemachine.StateMachine;\nimport fr.liienac.statemachine.StateMachine.State;\n");
            for (Map.Entry<String, Integer> entry : imp.entrySet()) {
                if (entry.getValue() > 0) {
                    ffw.write("import fr.liienac.statemachine.event." + entry.getKey() + "\n");
                }
            }
            ffw.write("\n");
            ffw.write("public class " + FsmEditorApplication.smt.getName() + " extends StateMachine { \n \t");

            /*for (int i =0; i<states.size(); i++){
                ffw.write("protected State ");
                ffw.write(states.get(i));
                ffw.write(" = null; \n \t");
            }*/
            ffw.write("\n");

            for (int i = 0; i < states.size(); i++) {
                ffw.write("\tpublic State ");
                ffw.write(states.get(i));
                ffw.write("= new State() { \n");
                trans = tab.getTransitionsEtats(states.get(i));
                for (int j = 0; j < trans.size(); j++) {
                    List<String> liste = new LinkedList<>();
                    if (!trans.get(j).get("action").isEmpty()) {
                        liste.add("action");
                    }
                    if (!trans.get(j).get("etat_dest").equals(states.get(i))) {
                        liste.add("goto");
                    }
                    if (!trans.get(j).get("guard").isEmpty()) {
                        liste.add("guard");
                    }
                    String typeMeth = String.join("+", liste);
                    System.out.println(typeMeth);
                    ffw.write("\t \tTransition ");
                    ffw.write(trans.get(j).get("nom"));
                    ffw.write(" = new Transition<");
                    ffw.write(trans.get(j).get("type"));
                    switch (typeMeth) {
                        case "action+goto":
                            ffw.write("> () { \n \t \t \tpublic void action() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("action"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            patternTest2 = Pattern.matches("\\/\\*.*", trans.get(j).get("action"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("action"));
                            }
                            ffw.write("; \n \t \t \t} \n");
                            ffw.write(" \n \t \t \tpublic State goTo() { \n \t \t \t \treturn ");
                            ffw.write(trans.get(j).get("etat_dest"));
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                        case "action":
                            ffw.write("> () { \n \t \t \tpublic void action() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("action"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            patternTest2 = Pattern.matches("\\/\\*.*", trans.get(j).get("action"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("action"));
                            }
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                        case "action+guard":
                            ffw.write("> () { \n \t \t \tpublic void action() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("action"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            patternTest2 = Pattern.matches("\\/\\*.*", trans.get(j).get("action"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("action"));
                            }
                            ffw.write("; \n \t \t \t} \n");
                            ffw.write(" \n \t \t \tpublic boolean guard() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("guard"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            patternTest2 = Pattern.matches("\\ \\/\\*.*", trans.get(j).get("guard"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("guard"));
                            }
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                        case "action+goto+guard":
                            ffw.write("> () { \n \t \t \tpublic void action() { \n \t \t \t \t");
                            System.out.println(trans.get(j).get("action"));
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("action"));
                            System.out.println(patternTest1);
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            patternTest2 = Pattern.matches("\\ \\/\\*.*", trans.get(j).get("action"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("action"));
                            }
                            ffw.write("; \n \t \t \t} \n");
                            ffw.write(" \n \t \t \tpublic State goTo() { \n \t \t \t \treturn ");
                            ffw.write(trans.get(j).get("etat_dest"));
                            ffw.write("; \n \t \t \t} \n");
                            ffw.write(" \n \t \t \tpublic boolean guard() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("guard"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            patternTest2 = Pattern.matches("\\ \\/\\*.*", trans.get(j).get("guard"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("guard"));
                            }
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                        case "goto":
                            ffw.write("> () { \n \t \t \tpublic State goTo() { \n \t \t \t \treturn ");
                            ffw.write(trans.get(j).get("etat_dest"));
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                        case "guard":
                            ffw.write("> () {\n \t \t \tpublic boolean guard() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("etat_dest"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            patternTest2 = Pattern.matches("\\ \\/\\*.*", trans.get(j).get("etat_dest"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("guard"));
                            }
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                        case "goto+guard":
                            ffw.write("> () { \n \t \t \tpublic State goTo() { \n \t \t \t \treturn ");
                            ffw.write(trans.get(j).get("etat_dest"));
                            ffw.write("; \n \t \t \t} \n");
                            ffw.write(" \n \t \t \tpublic boolean guard() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("guard"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            patternTest2 = Pattern.matches("\\ \\/\\*.*", trans.get(j).get("guard"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("guard"));
                            }
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                        default:
                            ffw.write("> () { \n \t \t \tpublic void action() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("action"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            patternTest2 = Pattern.matches("\\ \\/\\*.*", trans.get(j).get("action"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("action"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("action"));
                            }
                            ffw.write("; \n \t \t \t} \n");
                            ffw.write(" \n \t \t \tpublic State goTo() { \n \t \t \t \treturn ");
                            ffw.write(trans.get(j).get("etat_dest"));
                            ffw.write("; \n \t \t \t} \n");
                            ffw.write(" \n \t \t \tpublic boolean guard() { \n \t \t \t \t");
                            patternTest1 = Pattern.matches("\\ \\/\\/.*", trans.get(j).get("guard"));
                            if (patternTest1) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            patternTest2 = Pattern.matches("\\ \\/\\*.*", trans.get(j).get("guard"));
                            if (patternTest2) {
                                ffw.write(trans.get(j).get("guard"));
                            }
                            if (!(patternTest2 || patternTest1)) {
                                ffw.write("//" + trans.get(j).get("guard"));
                            }
                            ffw.write("; \n \t \t \t} \n \t \t}; \n");
                            break;
                    }
                }
                if (trans.isEmpty()) {
                    ffw.write("\t \t//Aucune Transition n'est définie");
                    ffw.write("\n");
                }

                ffw.write("\t}; \n \n");
            }

            ffw.write("}");
            ffw.close(); // fermer le fichier à la fin des traitements
        } catch (IOException e) {
            System.out.println("Read/Write access is needed !");
        }
    }

}
