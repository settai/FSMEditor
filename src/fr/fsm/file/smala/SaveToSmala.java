/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.file.smala;

import fr.fsm.StateMachineTab;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A class that is used to save a smala code of a states machine. Only need an
 * address and a states machine table for the creator.
 *
 * Only one method which is start and who send back the code in smala at the
 * address you asked for.
 *
 * @author RONSAIN Antoine
 */
public class SaveToSmala {

    private StateMachineTab tab;
    private String address;

    /**
     *
     * @param smt
     * @param string
     */
    public SaveToSmala(StateMachineTab tab, String add) {
        this.tab = tab;
        this.address = add;
    }

    /**
     *
     */
    public void start() {
        try {

            ArrayList<String> etats;
            ArrayList<Hashtable<String, String>> trans;
            Hashtable<String, Integer> imp;

            File ff = new File(address); // définir l'arborescence
            ff.createNewFile();
            FileWriter ffw = new FileWriter(ff);

            etats = tab.getAllStates();
            imp = tab.getImports();

            ffw.write("use core \n" + "use base\n" + "use display\n" + "use gui\n \n");
            ffw.write("/*\n" + "* Lieu d'explication de la machine à états\n" + "* Zone de commentaires libres\n" + "*/\n");

            ffw.write("\n" + "_define_\n");
            String[] tabLine = address.split("\\s*[^a-zA-Z1-9]+\\s*");
            ffw.write(tabLine[tabLine.length - 2] + " () { \n \t");
            ffw.write("FSM " + tabLine[tabLine.length - 2].toLowerCase() + " { \n \t \t");

            for (int i = 0; i < etats.size(); i++) {
                ffw.write("State ");
                ffw.write(etats.get(i));
                ffw.write("{ /* implement the reaction of the state */ } \n  \n \t \t");
            }

            for (int i = 0; i < etats.size(); i++) {
                trans = tab.getTransitionsEtats(etats.get(i));
                for (int j = 0; j < trans.size(); j++) {
                    if (!trans.get(j).get("etat_dest").equals(etats.get(i))) {
                        ffw.write(etats.get(i) + " -> " + trans.get(j).get("etat_dest"));
                        ffw.write(" (shape." + trans.get(j).get("type") + ")");
                        ffw.write("\t/* name of transition : " + trans.get(j).get("nom") + " */");
                        ffw.write(" \n \t  \t");
                    } else {
                        ffw.write("\t/*" + trans.get(j).get("etat_dest") + " possede une transition sur lui meme \n");
                        ffw.write("\t nommee " + trans.get(j).get("nom") + " avec un evt de type : " + trans.get(j).get("type") + " */ \n");
                    }
                }
            }
            System.out.println("close");
            ffw.write("\n \t} \n}");
            ffw.close(); // fermer le fichier à la fin des traitements
        } catch (IOException e) {
        }
    }
}
