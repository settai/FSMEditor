/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.file.smt;

import fr.fsm.FsmEditorApplication;
import fr.fsm.StateMachineTab;
import fr.fsm.file.java.TransitionExistingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author SAKER Julien
 */
public class StateMachineSave extends StateMachineTab {

    /**
     *
     */
    protected String ficpath;

    /**
     *
     */
    protected HashMap<Integer, Pair<String, String>> nbTransGet;

    /**
     *
     * @param smt
     * @param string
     */
    public StateMachineSave(StateMachineTab smt, String add) {
        super(smt.getAllStates(), smt.getAllTransitions(), smt.getImports(), smt.getAllCoord(), smt.getName(), smt.getNbTransName());
        this.ficpath = add + name + ".json";
        this.nbTransGet = new HashMap<Integer, Pair<String, String>>();
    }

    /**
     *
     */
    public void setNbTransName() {
        int max = 0;
        for (Map.Entry<Integer, Pair<String, String>> entry : nbTransGet.entrySet()) {
            if (entry.getKey() > max) {
                max = entry.getKey();
                System.out.println("max = " + max);
            }
        }
        for (int i = 0; i <= max; i++) {
            try {
                String es = nbTransGet.get(i).getKey();
                String ef = nbTransGet.get(i).getValue();
                nbTransName.add(new Pair<String, String>(es, ef));
            } catch (Exception ex) {
                nbTransName.add(new Pair<String, String>("None", "None"));
            }
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
    public void setTransitions(String es, String ef, String name, String type, String action, String guard) throws TransitionExistingException {
        Pattern p = Pattern.compile("t");
        Matcher m = p.matcher(name);
        try {
            int ind = Integer.parseInt(name.substring(1));

            if (m.find()) {
                nbTransGet.put(ind, new Pair<String, String>(es, ef));
            }
        } catch (Exception ex) {
        }

        if (states.contains(es)) {
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
     *
     * @param filepath
     */
    public void save(String filepath) {
        try {
            ficpath = filepath;
            String[] tabpath = filepath.split("/");
            setName(tabpath[tabpath.length - 1].split("\\.")[0]);
            File ff = new File(ficpath); // définir l'arborescence
            ff.createNewFile();
            FileWriter ffw = new FileWriter(ff);
            ffw.write("{\n\t\"nom\" : \"" + getName() + "\",");
            ffw.write("\n\t\"tab\" : [\n\t");
            for (int i = 0; i < states.size(); i++) {
                if (i > 0) {
                    ffw.write(",\n\t");
                }
                ffw.write("{\n\t\t\"etat\" : \"" + states.get(i) + "\",");
                ffw.write("\n\t\t\"coordX\" : \"" + coord.get(states.get(i)).get("x") + "\","); //pas de getteur pour l'instant d'abord je teste après je mate le code de yassine mdr
                ffw.write("\n\t\t\"coordY\" : \"" + coord.get(states.get(i)).get("y") + "\",");
                ffw.write("\n\t\t\"transitions\" : [");
                ArrayList<Hashtable<String, String>> tab = transitions.get(states.get(i));
                for (int j = 0; j < tab.size(); j++) {
                    if (j > 0) {
                        ffw.write(",");
                    }
                    ffw.write("\n\t\t\t{\"etatDest\" : \"" + tab.get(j).get("etat_dest") + "\"");
                    System.out.println(tab.get(j).get("etat_dest") + " : " + tab.get(j).get("type"));
                    ffw.write(",\n\t\t\t\"type\" : \"" + tab.get(j).get("type") + "\"");
                    ffw.write(",\n\t\t\t\"nom\" : \"" + tab.get(j).get("nom") + "\"");
                    ffw.write(",\n\t\t\t\"action\" : \"" + tab.get(j).get("action") + "\"");
                    ffw.write(",\n\t\t\t\"guard\" : \"" + tab.get(j).get("guard") + "\"}");
                }
                ffw.write("]\n\t}");
            }
            ffw.write("]\n}");
            ffw.close(); // fermer le fichier à la fin des traitements
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param filepath
     * @throws TransitionExistingException
     */
    public void open(String filepath) throws TransitionExistingException {
        ficpath = filepath;
        JSONParser jsonP = new JSONParser();
        try {

            JSONObject jsonO = (JSONObject) jsonP.parse(new FileReader(ficpath));

            name = (String) jsonO.get("nom");

            JSONArray tab = (JSONArray) jsonO.get("tab");
            for (Iterator it = tab.iterator(); it.hasNext();) {
                JSONObject etat = (JSONObject) it.next();
                String X = (String) etat.get("coordX");
                String Y = (String) etat.get("coordY");
                setState((String) etat.get("etat"), Double.parseDouble(X), Double.parseDouble(Y));
                JSONArray trans = (JSONArray) etat.get("transitions");
                for (Iterator itt = trans.iterator(); itt.hasNext();) {
                    System.out.println("i :" + (String) etat.get("etat"));
                    JSONObject transition = (JSONObject) itt.next();
                    System.out.println((String) transition.get("etatDest"));
                    setTransitions((String) etat.get("etat"), (String) transition.get("etatDest"), (String) transition.get("nom"), (String) transition.get("type"), (String) transition.get("action"), (String) transition.get("guard"));
                    System.out.println("hah");
                }

            }
            setNbTransName();
            FsmEditorApplication.smt = (StateMachineTab) this;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
