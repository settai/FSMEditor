/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.fsm.file.java;

import fr.fsm.StateMachineTab;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * A class that is used to open a java code of a states machine and to analyse
 * it in order to extract the machine. Only need an address for the creator.
 *
 * Only one method which is translate and who send back the states machine as a
 * table.
 *
 * @author RONSAIN Antoine and KNAFF Lucas
 */
public class OpenJava {

    private final String file;
    private int nbGoTo = 0;
    private int nbAction = 0;
    private int nbState = 0;
    private int nbTransition = 0;
    private int countState = 0;
    private int nbGuard = 0;
    private boolean thereIsGoTo = false;
    private boolean thereIsAction = false;
    private boolean thereIsState = false;
    private boolean thereIsTransition = false;
    private boolean thereIsGuard = false;
    private boolean inAComment = false;
    private String comment = "";
    private String nameInputState;
    private String nameTransition;
    private String nameEvent;
    private String nameOutputState;
    private String nameAction;
    private String nameGuard;
    private final StateMachineTab smt = new StateMachineTab();

    /**
     *
     * @param string
     */
    public OpenJava(String file) {
        this.nameInputState = "default";
        this.nameTransition = "default";
        this.nameEvent = "default";
        this.nameOutputState = "default";
        this.nameAction = "default";
        this.nameGuard = "default";
        this.file = file;
    }

    /**
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws TransitionExistingException
     */
    public StateMachineTab translate() throws FileNotFoundException, IOException, TransitionExistingException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] tabLine = line.split("\\s*([^a-zA-Z0-9(){}*/_]+)\\s*");
            getInState(tabLine);
            if (thereIsState) {
                getTransition(tabLine);
                if (thereIsTransition) {
                    getGoTo(tabLine);
                    getAction(tabLine);
                    if (thereIsAction) {
                        getComment(tabLine);
                        nameAction = comment;
                    }
                    getGuard(tabLine);
                    if (thereIsGuard) {
                        getComment(tabLine);
                        nameGuard = comment;

                    }
                }
            }
        }
        br.close();
        return getSMT();
    }

    //@SuppressWarnings("unchecked")
    private void getInState(String[] tabLine) {
        if ((nbState == 0) && thereIsState) {
            thereIsState = false;
        }
        int i;
        for (i = 0; i < tabLine.length; i++) {
            if ("public".equals(tabLine[i])) {
                try {
                    if ("State".equals(tabLine[i + 1])) {
                        if ("new".equals(tabLine[i + 3])) {
                            if ("State()".equals(tabLine[i + 4])) {
                                nameInputState = tabLine[i + 2];
                                nameOutputState = tabLine[i + 2];
                                thereIsState = true;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void getTransition(String[] tabLine) {
        if ((nbTransition == 0) && thereIsTransition) {
            thereIsTransition = false;
            try {
                smt.setState(nameInputState, (countState / 2) * 200 + 50, ((countState) % 2) * 200 + 50);
                countState++;
            } catch (Exception e) {
            }
            try {

                smt.setState(nameOutputState, (countState / 2) * 200 + 50, ((countState) % 2) * 200 + 50);
                countState++;
            } catch (Exception e) {
            }
            try {
                smt.setTransition(nameInputState, nameOutputState, nameTransition, nameEvent, nameAction, nameGuard);
            } catch (TransitionExistingException e) {
                //nothing, it already exists
            }
            nameAction = "default";
            nameGuard = "default";
            nameOutputState = nameInputState;
        }
        int i;
        for (i = 0; i < tabLine.length; i++) {
            if ("{".equals(tabLine[i])) {
                nbState++;
            }
            if ("}".equals(tabLine[i])) {
                nbState--;
            }
            if ("Transition".equals(tabLine[i])) {
                try {
                    if ("new".equals(tabLine[i + 2])) {
                        if ("Transition".equals(tabLine[i + 3])) {
                            nameTransition = tabLine[i + 1];
                            nameEvent = tabLine[i + 4];
                            thereIsTransition = true;
                        }
                    }
                } catch (Exception e) {
                }

            }
        }
    }

    private void getGoTo(String[] tabLine) {
        int i;
        for (i = 0; i < tabLine.length; i++) {
            if ("{".equals(tabLine[i])) {
                nbTransition++;
                nbState++;
            }
            if ("}".equals(tabLine[i])) {
                nbTransition--;
                nbState--;
            }
            if (!thereIsGoTo) {
                if ("public".equals(tabLine[i])) {
                    try {
                        if ("State".equals(tabLine[i + 1])) {
                            if ("goTo()".equals(tabLine[i + 2])) {
                                thereIsGoTo = true;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            if (thereIsGoTo) {
                if ("{".equals(tabLine[i])) {
                    nbGoTo++;
                }
                if ("}".equals(tabLine[i])) {
                    nbGoTo--;
                    if (nbGoTo == 0) {
                        thereIsGoTo = false;
                    }
                }
                if ("return".equals(tabLine[i])) {
                    try {
                        nameOutputState = tabLine[i + 1];

                    } catch (Exception e) {
                    }

                }

            }
        }
    }

    private void getAction(String[] tabLine) {
        int i;
        for (i = 0; i < tabLine.length; i++) {
            if ("{".equals(tabLine[i])) {
                nbTransition++;
                nbState++;
            }
            if ("}".equals(tabLine[i])) {
                nbTransition--;
                nbState--;
            }
            if (!thereIsAction) {
                if ("public".equals(tabLine[i])) {
                    try {
                        if ("void".equals(tabLine[i + 1])) {
                            if ("action()".equals(tabLine[i + 2])) {
                                thereIsAction = true;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            if (thereIsAction) {
                if ("{".equals(tabLine[i])) {
                    nbAction++;
                }
                if ("}".equals(tabLine[i])) {
                    nbAction--;
                    if (nbAction == 0) {
                        thereIsAction = false;
                    }
                }

            }
        }

    }

    private void getGuard(String[] tabLine) {
        int i;
        for (i = 0; i < tabLine.length; i++) {
            if ("{".equals(tabLine[i])) {
                nbTransition++;
                nbState++;
            }
            if ("}".equals(tabLine[i])) {
                nbTransition--;
                nbState--;
            }
            if (!thereIsGuard) {
                if ("public".equals(tabLine[i])) {
                    try {
                        if ("boolean".equals(tabLine[i + 1])) {
                            if ("guard()".equals(tabLine[i + 2])) {
                                thereIsGuard = true;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            if (thereIsGuard) {
                if ("{".equals(tabLine[i])) {
                    nbGuard++;
                }
                if ("}".equals(tabLine[i])) {
                    nbGuard--;
                    if (nbGuard == 0) {
                        thereIsGuard = false;
                    }

                }
            }

        }
    }

    private void getComment(String[] tabLine) {
        boolean patternTest;
        int i;
        for (i = 0; i < tabLine.length; i++) {
            if (!inAComment) {
                patternTest = Pattern.matches("\\/\\/.*", tabLine[i]);
                if (patternTest) {
                    try {
                        comment = "";
                        for (int j = i; j < tabLine.length; j++) {
                            comment = comment + " " + tabLine[j];
                        }

                    } catch (Exception e) {
                    }
                }
                patternTest = Pattern.matches("\\/\\*.*", tabLine[i]);
                if (patternTest) {
                    comment = "";
                    inAComment = true;
                }

            }
            if (inAComment) {
                patternTest = Pattern.matches("\\*\\/.*", tabLine[i]);
                if (patternTest) {
                    comment = comment + "*/";
                    inAComment = false;

                } else {
                    comment = comment + " " + tabLine[i];
                }
            }
        }

    }

    /**
     *
     * @return
     */
    public StateMachineTab getSMT() {
        return smt;
    }
}
