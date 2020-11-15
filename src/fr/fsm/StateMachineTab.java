/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm;

import static fr.fsm.FsmEditorApplication.grid;
import fr.fsm.file.java.TransitionExistingException;
import fr.fsm.gui.views.StatePane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.*;
import javafx.util.Pair;

/**
 * A class that store a representation of a state machine. Is the core data of
 * the application and is used for save or open files.
 *
 * @author SAKER Julien, KNAFF Lucas and RONSAIN Antoine
 */
public class StateMachineTab {

    /**
     * Name of the state machine.
     */
    protected String name;

    /**
     * Array of the states.
     */
    protected ArrayList<String> states;

    /**
     * Hash table of Arrays of transitions per state.
     */
    protected Hashtable<String, ArrayList<Hashtable<String, String>>> transitions;

    /**
     * Tp drive the imports settings of java
     */
    protected Hashtable<String, Integer> imports;

    /**
     * Hash table of {@code Coord} per state.
     * <p>
     * why don't use Point2D ? or double[2] ?
     */
    protected HashMap<String, HashMap<String, Double>> coord;

    /**
     * To Drive the Default Name setting of a transition.
     * <p>
     * why don't use Point2D ? or double[2] ?
     */
    protected ArrayList<Pair<String, String>> nbTransName;

    /**
     * Constructor used to load data from open file.
     *
     * @param states {@code Array} of state
     * @param transitions {@code Hashtable} of transitions per state
     * @param imports ?
     * @param coord {@code Hashtable} of coordinate per state
     * @param name name of the state machine
     */
    public StateMachineTab(ArrayList<String> states, Hashtable<String, ArrayList<Hashtable<String, String>>> transitions, Hashtable<String, Integer> imports, HashMap<String, HashMap<String, Double>> coord, String name, ArrayList<Pair<String, String>> nbTransName) {
        this.name = name;
        this.states = states;
        this.transitions = transitions;
        this.imports = imports;
        this.coord = coord;
        this.nbTransName = nbTransName;
    }

    /**
     * Constructor used to create an empty {@code StateMachineTab}
     */
    public StateMachineTab() {
        this.name = "Untilted";
        this.states = new ArrayList<String>();
        this.transitions = new Hashtable<String, ArrayList<Hashtable<String, String>>>();
        this.imports = new Hashtable<String, Integer>();
        this.coord = new HashMap<String, HashMap<String, Double>>();
        this.nbTransName = new ArrayList<Pair<String, String>>();
    }

    /**
     *
     * @return
     */
    public ArrayList<Pair<String, String>> getNbTransName() {
        return nbTransName;
    }

    /**
     * Returns the name of the state machine.
     *
     * @return the {@code String} name of the state machine
     */
    public String getName() {
        return name;
    }

    /**
     * Change the name of the state machine
     *
     * @param nom new name of the state machine
     */
    public void setName(String nom) {
        name = nom;
    }

    /**
     * ?
     *
     * @return
     */
    public Hashtable<String, Integer> getImports() {
        return imports;
    }

    /**
     * Returns all the states.
     *
     * @return an {@code ArrayList<String>} that contains all the states
     */
    public ArrayList<String> getAllStates() {
        return states;
    }

    /**
     * Get the index of the state in the {@code ArrayList}.
     *
     * @param state the state we want the index
     * @return the {@code int} index of the state
     */
    public int getIndex(String state) {
        return states.indexOf(state);
    }

    /**
     * Change coordinate of a state.
     *
     * @param state the state to change coordinate
     * @param x X
     * @param y Y
     */
    public void setCoord(String state, double x, double y) {
        if (states.contains(state)) {
            HashMap hsh = new HashMap<String, Double>();
            hsh.put("x", x);
            hsh.put("y", y);
            coord.put(state, hsh);
        } else {
            throw new UnsupportedOperationException("Exception - State does not Exist");
        }
    }

    /**
     * Returns all the coordinate of the states.
     *
     * @return a {@code Hashmap} of coordinate per state
     */
    public HashMap<String, HashMap<String, Double>> getAllCoord() {
        return coord;
    }

    /**
     * Get the X axes of a state.
     *
     * @param state the state to get X
     * @return the {@code double} X axes of the state
     */
    public double getXCoord(String state) {
        return coord.get(state).get("x");
    }

    /**
     * Edit the X axis of a state.
     *
     * @param state the state to change X
     * @param x X
     */
    public void setXCoord(String state, double x) {
        if (states.contains(state)) {
            HashMap hsh = coord.get(state);
            hsh.replace("x", x);
        } else {
            throw new UnsupportedOperationException("Exception - State does not Exist");
        }
    }

    /**
     * Get the Y axes of a state.
     *
     * @param state the state to get Y
     * @return the {@code double} Y axes of the state
     */
    public double getYCoord(String state) {
        return coord.get(state).get("y");
    }

    /**
     * Edit the y axis of a state.
     *
     * @param state the state to change Y
     * @param y Y
     */
    public void setYCoord(String state, double y) {
        if (states.contains(state)) {
            HashMap hsh = coord.get(state);
            hsh.replace("y", y);
        } else {
            throw new UnsupportedOperationException("Exception - State does not Exist");
        }
    }

    /**
     * Return the name of the state {@code i}.
     *
     * @param i the index of the state
     * @return the {@code String} name of the state
     */
    public String getState(int i) {
        if (i < states.size()) {
            return states.get(i);
        } else {
            throw new ArrayIndexOutOfBoundsException("IndexOutOfRange");
        }
    }

    /**
     * Rename the state {@code i}.
     *
     * @param newname the new name of the state
     * @param i the index of the state
     */
    public void renameState(String newname, int i) {
        boolean is_StateExisting = (i < states.size());
        boolean is_NameNotUsed = (!states.contains(newname));
        boolean no_Change = (states.get(i).equals(newname));
        String old_name = states.get(i);
        for (int k = 0; k < nbTransName.size(); k++) {
            if (nbTransName.get(k).getKey().equals(old_name) || nbTransName.get(k).getValue().equals(old_name)) {
                Pair<String, String> p = nbTransName.get(k);
                nbTransName.remove(k);
                if (p.getKey().equals(old_name)) {
                    Pair<String, String> p2 = new Pair<String, String>(newname, p.getValue());
                    nbTransName.add(k, p2);
                } else {
                    Pair<String, String> p2 = new Pair<String, String>(p.getKey(), newname);
                    nbTransName.add(k, p2);
                }

            }
        }
        if (!no_Change) {
            if (is_StateExisting & is_NameNotUsed) {
                ArrayList<Hashtable<String, String>> valeur = transitions.get(states.get(i));
                states.set(i, newname);
                transitions.remove(old_name);
                transitions.put(newname, valeur);
                transitions.entrySet().forEach((entry) -> {
                    ArrayList<Hashtable<String, String>> tab = entry.getValue();
                    for (int j = 0; j < tab.size(); j++) {
                        if (tab.get(j).get("etat_dest").equals(old_name)) {
                            Hashtable hsh = new Hashtable();
                            hsh.put("etat_dest", newname);
                            hsh.put("type", tab.get(j).get("type"));
                            hsh.put("nom", tab.get(j).get("nom"));
                            hsh.put("action", tab.get(j).get("action"));
                            hsh.put("guard", tab.get(j).get("guard"));
                            entry.getValue().set(j, hsh);
                        }
                    }
                });
            }
            HashMap hsh = coord.get(old_name);
            coord.remove(old_name);
            coord.put(newname, hsh);
            //majDefaultNames();
        }
    }

    /**
     *
     */
    public void majDefaultNames() {
        //Hashtable<Integer, Integer> tab = new Hashtable<>();
        Pattern p = Pattern.compile("^Sd([0-9]+)$");
        int ind = 0;
        for (int i = 0; i < grid.getChildren().size(); i++) {
            try {
                StatePane s = (StatePane) grid.getChildren().get(i);
                String name = s.getText();
                Matcher m = p.matcher(name);
                if (m.find()) {
                    int val = Integer.parseInt(name.substring(2));
                    s.setText("Sd" + ind);
                    ind++;
                }
            } catch (NumberFormatException ex) {
            } catch (ClassCastException e) {
            }
        }
        //print
        coord.forEach(new BiConsumer<String, HashMap<String, Double>>() {
            @Override
            public void accept(String t, HashMap<String, Double> u) {
                System.out.println(t + ": x=" + u.get("x") + ", y=" + u.get("y"));
            }
        });
    }

    /**
     * Add a new state in the {@code StateMachineTab} with a default name.
     *
     * @param x X coordinate of the state
     * @param y Y coordinate of the state
     * @return the {@code int} index of the state in the {
     * @ArrayList}
     */
    public int setDefaultState(double x, double y) {
        Pattern p = Pattern.compile("^Sd([0-9]+)$");
        int ind = 0;
        for (int i = 0; i < states.size(); i++) {
            Matcher m = p.matcher(states.get(i));
            if (m.find()) {
                ind += 1;
            }
        }
        states.add("Sd" + ind);
        transitions.put("Sd" + ind, new ArrayList<Hashtable<String, String>>());
        setCoord(states.get(states.size() - 1), x, y);
        return ind;
    }

    /**
     * Add a new state in the {@code StateMachineTab} with a name
     *
     * @param name name of the state
     * @param x X coordinate of the state
     * @param y Y coordinate of the state
     */
    public void setState(String name, double x, double y) {
        if (!states.contains(name)) {
            states.add(name);
            transitions.put(name, new ArrayList<Hashtable<String, String>>());
            setCoord(name, x, y);
        } else {
            throw new UnsupportedOperationException("Exception - Already Existing");
        }
    }

    /**
     * Remove a state
     *
     * @param e name of the state
     */
    public void removeState(String e) {
        if (states.contains(e)) {
            states.remove(e);
            ArrayList<Hashtable<String, String>> tran = getTransitionsEtats(e);
            for (int i = 0; i < tran.size(); i++) {
                imports.replace(tran.get(i).get("type"), imports.get(tran.get(i).get("type")) - 1);
            }
            for (int i = 0; i < nbTransName.size(); i++) {
                if (nbTransName.get(i).getKey().equals(e) || nbTransName.get(i).getValue().equals(e)) {
                    nbTransName.remove(i);
                    nbTransName.add(i, new Pair<String, String>("None", "None"));
                }
            }
            transitions.remove(e);
            for (Map.Entry<String, ArrayList<Hashtable<String, String>>> entry : transitions.entrySet()) {
                ArrayList<Hashtable<String, String>> tab = entry.getValue();
                for (int j = 0; j < tab.size(); j++) {
                    if (tab.get(j).get("etat_dest").equals(e)) {
                        System.out.println("STp pq :" + tab.get(j).get("type"));
                        imports.replace(tab.get(j).get("type"), imports.get(tab.get(j).get("type")) - 1);
                        entry.getValue().remove(j);
                    }
                }
            }
            coord.remove(e);

            // Print
            Pattern p = Pattern.compile("^Sd([0-9]+)$");
            majDefaultNames();
            for (int i = 0; i < states.size(); i++) {
                System.out.println("Etat == " + states.get(i));
                //System.out.println("X = " + coord.get(states.get(i)).get("x")+" Y = "+ coord.get(states.get(i)).get("y"));
                //System.out.println("X = " + getXCoord(states.get(i))+" Y = "+ getYCoord(states.get(i)));
                for (Map.Entry<String, ArrayList<Hashtable<String, String>>> entry : transitions.entrySet()) {
                    ArrayList<Hashtable<String, String>> tab = entry.getValue();
                    String es = entry.getKey();
                    for (int j = 0; j < tab.size(); j++) {
                        System.out.println("Etat Source :" + es + " Etat Dest :" + tab.get(j).get("etat_dest") + " Nom : " + tab.get(j).get("nom"));
                    }
                }

            }
            System.out.println();
            //End print
        } else {
            throw new UnsupportedOperationException("Exception - State not exists");
        }
    }

    /**
     * Remove a state.
     *
     * @param i the index of the state
     */
    public void removeState(int i) {
        if (i < states.size()) {
            String name = states.get(i);
            coord.remove(name);
            states.remove(states.get(i));
            ArrayList<Hashtable<String, String>> tran = getTransitionsEtats(name);
            for (int k = 0; k < tran.size(); k++) {
                imports.replace(tran.get(k).get("type"), imports.get(tran.get(k).get("type")) - 1);
            }
            for (int k = 0; k < nbTransName.size(); k++) {
                if (nbTransName.get(k).getKey().equals(name) || nbTransName.get(k).getValue().equals(name)) {
                    nbTransName.remove(k);
                    nbTransName.add(k, new Pair<String, String>("None", "None"));
                }
            }
            transitions.remove(name);
            for (Map.Entry<String, ArrayList<Hashtable<String, String>>> entry : transitions.entrySet()) {
                ArrayList<Hashtable<String, String>> tab = entry.getValue();
                for (int j = 0; j < tab.size(); j++) {
                    if (tab.get(j).get("etat_dest").equals(name)) {
                        imports.replace(tab.get(j).get("type"), imports.get(tab.get(j).get("type")) - 1);
                        entry.getValue().remove(j);
                    }
                }
            }
            majDefaultNames();
        } else {
            throw new UnsupportedOperationException("Exception - State not exists");
        }
    }

    /**
     * Returns all the transitions.
     *
     * @return a {@code Hashtable} with Arrays of transitions per state.
     */
    public Hashtable<String, ArrayList<Hashtable<String, String>>> getAllTransitions() {
        return transitions;
    }

    /**
     * Returns the list of transitions of a state.
     *
     * @param e the state
     * @return an {@code ArrayList} of the transitions
     */
    public ArrayList<Hashtable<String, String>> getTransitionsEtats(String e) {
        return transitions.get(e);
    }

    /**
     * Get the event type of a transition.
     *
     * @param es input state of the transition
     * @param ef output state of the transition
     * @return the {@code String} event of the transition
     */
    public String getTransitionEvent(String es, String ef) {
        boolean flag = false;
        int i = 0;
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                flag = true;
            } else {
                i++;
            }
        }
        if (flag) {
            return transitions.get(es).get(i).get("type");
        } else {
            return null;
        }
    }

    /**
     * Returns the action of a transition.
     *
     * @param es input state
     * @param ef output state
     * @return the {@code String} action of a transition
     */
    public String getTransitionAction(String es, String ef) {
        boolean flag = false;
        int i = 0;
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                flag = true;
            } else {
                i++;
            }
        }
        if (flag) {
            return transitions.get(es).get(i).get("action");
        } else {
            return null;
        }
    }

    /**
     * Returns the guard of a transition.
     *
     * @param es input state
     * @param ef output state
     * @return the {@code String} guard of a transition
     */
    public String getTransitionGuard(String es, String ef) {
        boolean flag = false;
        int i = 0;
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                flag = true;
            } else {
                i++;
            }
        }
        if (flag) {
            return transitions.get(es).get(i).get("guard");
        } else {
            return null;
        }
    }

    /**
     * Returns the name of a transition.
     *
     * @param es input state
     * @param ef output state
     * @return the {@code String} name of a transition
     */
    public String getTransitionName(String es, String ef) {
        boolean flag = false;
        int i = 0;
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                flag = true;
            } else {
                i++;
            }
        }
        if (flag) {
            return transitions.get(es).get(i).get("nom");
        } else {
            return null;
        }
    }

    /**
     * Change the guard of a transition.
     *
     * @param es input state
     * @param ef output state
     * @param guard the guard
     */
    public void setTransitionGuard(String es, String ef, String guard) {
        int i = 0;
        boolean flag = false;
        try {
            transitions.get(es);
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Exception - State(s) do not exist");
        }
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                transitions.get(es).get(i).remove("guard");
                transitions.get(es).get(i).put("guard", guard);
                flag = true;
            }
            i++;
        }
        if (!flag) {
            throw new UnsupportedOperationException("Exception - Transition does not exist");
        }
    }

    /**
     * Change the action of a transition.
     *
     * @param es input state
     * @param ef output state
     * @param action the action
     */
    public void setTransitionAction(String es, String ef, String action) {
        int i = 0;
        boolean flag = false;
        try {
            transitions.get(es);
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Exception - State(s) do not exist");
        }
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                transitions.get(es).get(i).remove("action");
                transitions.get(es).get(i).put("action", action);
                flag = true;
            }
            i++;
        }
        if (!flag) {
            throw new UnsupportedOperationException("Exception - Transition does not exist");
        }
    }

    /**
     * Change the name of a transition.
     *
     * @param es input state
     * @param ef output state
     * @param name the name
     */
    public void setTransitionName(String es, String ef, String name) {
        int i = 0;
        boolean flag = false;
        try {
            transitions.get(es);
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Exception - State(s) do not exist");
        }
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                transitions.get(es).get(i).remove("nom");
                transitions.get(es).get(i).put("nom", name);
                flag = true;
            }
            i++;
        }
        if (!flag) {
            throw new UnsupportedOperationException("Exception - Transition does not exist");
        }
    }

    /**
     * Change the event of a transition.
     *
     * @param es input state
     * @param ef output state
     * @param event the event
     */
    public void setTransitionEvent(String es, String ef, String event) {
        int i = 0;
        boolean flag = false;
        try {
            transitions.get(es);
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Exception - State(s) do not exist");
        }
        while (i < transitions.get(es).size() && !flag) {
            if (transitions.get(es).get(i).get("etat_dest").equals(ef)) {
                imports.replace(transitions.get(es).get(i).get("type"), imports.get(transitions.get(es).get(i).get("type")) - 1);
                transitions.get(es).get(i).remove("type");
                transitions.get(es).get(i).put("type", event);
                try {
                    imports.replace(transitions.get(es).get(i).get("type"), imports.get(transitions.get(es).get(i).get("type")) + 1);
                } catch (Exception ex) {
                    imports.put(transitions.get(es).get(i).get("type"), 1);
                }
                flag = true;
            }
            i++;
        }
        if (!flag) {
            throw new UnsupportedOperationException("Exception - Transition does not exist");
        }
    }

    /**
     * Edit a transition.
     *
     * @param es input state
     * @param ef output state
     * @param name name of the transition
     * @param type event of the transition
     * @param action action of the transition
     * @param guard guard of the transition
     * @throws TransitionExistingException if transition already exist
     */
    public void setTransition(String es, String ef, String type, String action, String guard) throws TransitionExistingException {
        int index = 0;
        boolean foundIndex = false;
        System.out.println(nbTransName.size());
        for (int i = 0; i < nbTransName.size(); i++) {
            System.out.println(nbTransName.get(i).getKey());
            if (nbTransName.get(i).getKey().equals("None") && nbTransName.get(i).getValue().equals("None")) {
                index = i;
                System.out.println(i);
                foundIndex = true;
            }
        }
        if (foundIndex) {
            nbTransName.remove(index);
            nbTransName.add(index, new Pair<String, String>(es, ef));
        } else {
            nbTransName.add(new Pair<String, String>(es, ef));
            index = nbTransName.size() - 1;
        }
        String defaultName = "t" + Integer.toString(index);
        if (states.contains(es) & states.contains(ef)) {
            ArrayList<Hashtable<String, String>> tab = transitions.get(es);
            int i = 0;
            boolean flag = true;
            while (i < tab.size() && flag) {
                if (tab.get(i).get("etat_dest").equals(ef)) {
                    flag = false;
                }
                i++;
            }
            if (flag) {
                Hashtable hsh = new Hashtable();
                hsh.put("etat_dest", ef);
                hsh.put("type", type);
                hsh.put("nom", defaultName);
                hsh.put("action", action);
                hsh.put("guard", guard);
                transitions.get(es).add(hsh);
                if (!imports.contains(type)) {
                    imports.put(type, 1);
                } else {
                    imports.replace(type, imports.get(type) + 1);
                }
            } else {
                throw new TransitionExistingException("Exception - State not exists"); //le but de cette exception est de pouvoir catch l'expression pour Lucas et de lui laisser la main sur la façon de gérer la GUI
            }
        } else {
            throw new UnsupportedOperationException("Exception - State(s) not exists");
        }
    }

    /**
     *
     * @param es
     * @param ef
     * @param name
     * @param type
     * @param action
     * @param guard
     * @throws TransitionExistingException
     */
    public void setTransition(String es, String ef, String name, String type, String action, String guard) throws TransitionExistingException {
        Pattern p = Pattern.compile("^t[0-9]+$");
        Matcher m = p.matcher(name);
        try {
            int ind = Integer.parseInt(name.substring(1));
            if (m.find()) {
                if (ind < nbTransName.size()) {
                    nbTransName.remove(ind);
                    nbTransName.add(ind, new Pair<String, String>(es, ef));
                } else {
                    while (ind > nbTransName.size()) {
                        nbTransName.add(new Pair<String, String>("None", "None"));
                    }
                    nbTransName.add(ind, new Pair<String, String>(es, ef));
                }
            }
        } catch (Exception ex) {
        }

        //print
        nbTransName.forEach(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(Pair<String, String> t) {
                System.out.println(".accept()" + t.getKey() + t.getValue());
            }

        });

        if (states.contains(es) & states.contains(ef)) {
            ArrayList<Hashtable<String, String>> tab = transitions.get(es);
            int i = 0;
            boolean flag = true;
            while (i < tab.size() && flag) {
                if (tab.get(i).get("etat_dest").equals(ef)) {
                    flag = false;
                }
                i++;
            }
            if (flag) {
                Hashtable hsh = new Hashtable();
                hsh.put("etat_dest", ef);
                hsh.put("type", type);
                hsh.put("nom", name);
                hsh.put("action", action);
                hsh.put("guard", guard);
                transitions.get(es).add(hsh);
                if (!imports.contains(type)) {
                    imports.put(type, 1);
                } else {
                    imports.replace(type, imports.get(type) + 1);
                }
            } else {
                throw new TransitionExistingException("Exception - State not exists"); //le but de cette exception est de pouvoir catch l'expression pour Lucas et de lui laisser la main sur la façon de gérer la GUI
            }
        } else {
            throw new UnsupportedOperationException("Exception - State(s) not exists");
        }
    }

    /**
     * Delete a transition.
     *
     * @param es input state
     * @param ef output state
     */
    public void removeTransition(String es, String ef) {
        if (states.contains(es) && states.contains(ef)) {
            ArrayList<Hashtable<String, String>> tab = transitions.get(es);
            for (int i = 0; i < tab.size(); i++) {
                if (tab.get(i).get("etat_dest").equals(ef)) {
                    imports.replace(tab.get(i).get("type"), imports.get(tab.get(i).get("type")) - 1);
                    transitions.get(es).remove(i);
                }
            }
            for (int i = 0; i < nbTransName.size(); i++) {
                if (nbTransName.get(i).getKey().equals(es) && nbTransName.get(i).getValue().equals(ef)) {
                    nbTransName.remove(i);
                    nbTransName.add(i, new Pair<String, String>("None", "None"));
                }

            }
        } else {
            throw new UnsupportedOperationException("Exception - State(s) not exists");
        }
    }

    /**
     * Reset/Clean the {@code StateMachineTab}
     */
    public void clean() {
        name = "Untilted";
        states = new ArrayList<String>();
        transitions = new Hashtable<String, ArrayList<Hashtable<String, String>>>();
        imports = new Hashtable<String, Integer>();
        coord = new HashMap<String, HashMap<String, Double>>();
        nbTransName = new ArrayList<Pair<String, String>>();
    }
}
