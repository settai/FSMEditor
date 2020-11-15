/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.optimisation;

import fr.fsm.StateMachineTab;
import fr.fsm.gui.creation.SmtToGui;
import fr.fsm.gui.views.FsmPane;
import fr.fsm.gui.views.StatePane;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author SAKER Julien
 */
public class StateOpti {

    private Function[] constraints;
    private Function f;
    private Function df;
    private int nbState;
    private Vector X0;

    private void init(Matrix T, double d, Vector X0) {
        this.X0 = X0.clone();
        int n = T.getN();
        nbState = n;
        double[][] h = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i == j) {
                    h[i][i] = T.getCol(i).sum() + T.getLig(i).sum() - 2 * T.getLig(i).get(i);
                } else {
                    h[i][j] = -1 * T.getLig(i).get(j);
                    h[j][i] = h[i][j];
                }
            }
        }
        Matrix H = new Matrix(h);
        f = XY -> {
            Vector X = XY.subVect(IntStream.range(0, n).boxed().collect(Collectors.toList()));
            Vector Y = XY.subVect(IntStream.range(n, 2 * n).boxed().collect(Collectors.toList()));
            return new Vector(new double[]{0.5 * X.mul(H).mul(X) + 0.5 * Y.mul(H).mul(Y) + Math.pow(X.get(0) - 300, 2) + Math.pow(Y.get(0) - 300, 2)});
        };
        df = XY -> {
            Vector X = XY.subVect(IntStream.range(0, n).boxed().collect(Collectors.toList()));
            Vector HX = H.mul(X);
            Vector Y = XY.subVect(IntStream.range(n, 2 * n).boxed().collect(Collectors.toList()));
            Vector HY = H.mul(Y);
            Vector res = new Vector(XY.length());
            for (int i = 0; i < nbState; i++) {
                res.edit()[i] = HX.get(i);
                res.edit()[n + i] = HY.get(i);
            }
            res.edit()[0] += 2 * (X.get(0) - 300);
            res.edit()[n] += 2 * (Y.get(0) - 300);
            return res;
        };

        constraints = new Function[n * (n - 1) / 2];
        int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int i2 = Integer.valueOf(i); // ???
                int j2 = Integer.valueOf(j);
                constraints[k] = X -> new Vector(new double[]{-Math.abs(X.get(i2) - X.get(j2)) - Math.abs(X.get(n + i2) - X.get(n + j2)) + d});
                k++;
            }
        }
        System.out.println(H);
    }

    /**
     *
     * @param T
     * @param d
     */
    public StateOpti(Matrix T, double d) {
        Vector x0 = new Vector(2 * nbState);
        for (int i = 0; i < nbState; i++) {
            x0.edit()[i] = 100 * i;
            x0.edit()[nbState + i] = (i + 1) / 2 * 2 - i;
        }
        init(T, d, x0);
    }

    /**
     *
     * @param smt
     */
    public StateOpti(StateMachineTab smt) {
        int n = smt.getAllStates().size();
        Matrix T = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (smt.getTransitionName(smt.getState(i), smt.getState(j)) != null || smt.getTransitionName(smt.getState(j), smt.getState(i)) != null) {
                    T.edit()[i].edit()[j] = 1;
                }
            }
        }
        Vector x0 = new Vector(2 * n);
        for (int i = 0; i < n; i++) {
            x0.edit()[i] = smt.getXCoord(smt.getState(i));
            x0.edit()[n + i] = smt.getYCoord(smt.getState(i));
        }
        init(T, 200, x0);
    }

    /**
     *
     * @return
     */
    public Vector optimise() {
        Core res = new Core(f, df, X0);
        return res.bfgs2(constraints);
    }

    /**
     *
     * @param smt
     * @param grid
     */
    public void optimise(StateMachineTab smt, FsmPane grid) {
        Core res = new Core(f, df, X0);
        Vector X = res.bfgs2(constraints);

        Object[] copie = grid.getChildren().toArray();
        for (Object obj : copie) {
            if (obj.getClass().isInstance(new StatePane())) {
                StatePane s = (StatePane) obj;
                System.out.println(s.text.getText());
                s.remove();
                grid.getChildren().remove(s);
            }
        }

        for (int i = 0; i < nbState; i++) {
            smt.setCoord(smt.getState(i), X.get(i), X.get(nbState + i));
        }
        (new SmtToGui()).setGUI(grid);
        System.out.println(X);
    }
}
