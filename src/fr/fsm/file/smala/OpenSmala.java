/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.file.smala;

import fr.fsm.StateMachineTab;
import fr.fsm.file.java.TransitionExistingException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A class that is used to open a smala code of a states machine and to analyse
 * it in order to extract the machine. Only need an address for the creator.
 *
 * Only one method which is translate and who send back the states machine as a
 * table.
 *
 * @author RONSAIN Antoine
 */
public class OpenSmala {

    private String file;
    private int countState;
    private StateMachineTab smt = new StateMachineTab();
    private int numtransition = 0;
    private String nameTransition = "t";

    /**
     *
     * @param string
     */
    public OpenSmala(String file) {
        this.file = file;
    }

    /**
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws TransitionExistingException
     */
    public StateMachineTab traduction() throws FileNotFoundException, IOException, TransitionExistingException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] tabLine = line.split("\\s*[^a-zA-Z0-9{}-]+\\s*");
            for (int i = 0; i < tabLine.length; i++) {
                if ((i + 4 < tabLine.length) || (i + 7 < tabLine.length)) {
                    if ("-".equals(tabLine[i + 1])) {
                        if ("shape".equals(tabLine[i + 3])) {
                            //System.out.println("Je suis parti de l'etat " + tabLine[i] + " pour arriver " + tabLine[i + 2] + " grace a la transition de type  " + tabLine[i + 4] + " de nom inconnu");
                            try {
                                System.out.println(tabLine[i + 5]);
                                if ("name".equals(tabLine[i + 5])) {
                                    if ("of".equals(tabLine[i + 6])) {
                                        if ("transition".equals(tabLine[i + 7])) {
                                            nameTransition = tabLine[i + 8];
                                        }
                                    }

                                }
                            } catch (Exception e) {
                            }
                            try {
                                smt.setState(tabLine[i], (countState / 2) * 200 + 50, ((countState) % 2) * 200 + 50);
                                countState++;
                            } catch (Exception e) {
                            }
                            try {
                                smt.setState(tabLine[i + 2], (countState / 2) * 200 + 50, ((countState) % 2) * 200 + 50);
                                countState++;
                            } catch (Exception e) {
                            }
                            try {
                                if (("t".equals(nameTransition))) {
                                    smt.setTransition(tabLine[i], tabLine[i + 2], nameTransition + numtransition, tabLine[i + 4], "", "");
                                    numtransition++;
                                } else {
                                    smt.setTransition(tabLine[i], tabLine[i + 2], nameTransition, tabLine[i + 4], "", "");
                                    nameTransition = "t";
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        }

        return smt;
    }

    /**
     *
     * @return
     */
    public StateMachineTab getSMT() {
        return smt;
    }
}
