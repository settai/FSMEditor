/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.optimisation;

import java.util.ArrayList;

/**
 * The core of the package, used to call different method to optimise f:
 * <p>
 * search {@code min f(X)} where {@code f} is {@code Vector -> double}
 *
 * @author KNAFF Lucas
 */
public class Core {

    /**
     * The function to optimism
     */
    private Function f;

    /**
     * Gradian of the function
     */
    private Function df;

    /**
     * Start point
     */
    private Vector x0;

    /**
     * Precision of the solution
     */
    private double eps;

    /**
     * Approximation of the invert of the hessian (for quasi-newton method)
     */
    private Matrix W;

    /**
     * Dimension of the problem (length of X)
     */
    private int dim;

    /**
     * Number of iteration done
     */
    public int k;

    /**
     * Construct the core object with required parameters.
     *
     * @param f the function to optimise
     * @param df the gradian of the function to optimise
     * @param x0 the start point of algorithms
     * @param eps the precision of the solution (default: 1E-4)
     * @param W0 the start approximation {@code Matrix} of the invert of the
     * hessian (default: In)
     */
    public Core(Function f, Function df, Vector x0, double eps, Matrix W0) {
        this.f = f;
        this.df = df;
        this.x0 = x0;
        this.dim = x0.length();
        this.eps = eps;
        this.W = W0;
    }

    /**
     * Construct the core object with required parameters.
     *
     * @param f the function to optimise
     * @param df the gradian of the function to optimise
     * @param x0 the start point of algorithms
     */
    public Core(Function f, Function df, Vector x0) {
        this(f, df, x0, 0.005, Matrix.I(x0.length()));
    }

    /**
     * A quasi-newton algorithm to find the solution.
     * <p>
     * In this method, the hessian of the function is approximated by :
     * <p>
     * {@code Hk+1 = Hk + (Yk * Yk.T)/(Yk.T * Sk) - (Hk * Sk * Sk.T * Hk)/(Sk * Hk * Sk.T)}.
     *
     * @return the {@code Vector(dim)} result
     */
    public Vector bfgs() {
        Vector dk;
        double alpha;
        Vector nextX;

        System.out.println("demarrage de l'optimisation");
        Vector x = x0.clone();
        Vector dfx = df.exec(x);
        int k = 0;

        while (dfx.norme() > eps * eps) {
            System.out.println("iteration " + k);

            System.out.println("determination de la direction ...");
            dk = W.mul(dfx).scal(-1); //dk = -W*dfx
            System.out.println("direction choisie: " + dk);

            System.out.println("calcul du pas de Wolfe ...");
            alpha = wolf(dk, x);
            System.out.println("pas choisi: " + alpha);

            nextX = x.add(dk.scal(alpha)); //nextX = X + alpha*dk
            k++;
            W = nextW(dk.scal(alpha), x, nextX);
            x = nextX.clone();
            dfx = df.exec(x);

            System.out.println("dfx :" + dfx + "\n");
        }
        return x;
    }

    /**
     * A quasi-newton algorithm to find the solution.
     * <p>
     * In this method, the hessian of the function is approximated by :
     * <p>
     * {@code Hk+1 = Hk + (Yk * Yk.T)/(Yk.T * Sk) - (Hk * Sk * Sk.T * Hk)/(Sk * Hk * Sk.T)}.
     *
     * @param constraints a {@code Function[]} that contains all constraints of
     * the problem. Constraints have to be independent.
     * <p>
     * In math, is represented as {@code gi(x) <= 0}
     * @return the {@code Vector(dim)} result
     */
    public Vector bfgs(Function[] constraints) {
        Vector dk;
        double alpha;
        Vector nextX;
        Matrix majLambda;
        Matrix subA;
        boolean isAdmissible;

        System.out.println(x0);
        System.out.println("demarrage de l'optimisation\n");
        Vector x = x0.clone();
        Vector dfx = df.exec(x);
        int k = 0;

        Function[] dg = new Function[constraints.length];
        for (int i = 0; i < dg.length; i++) {
            dg[i] = getGrad(constraints[i]);
        }
        Matrix A = gradMatrix(dg, x);

        ArrayList<Integer> cstActive = new ArrayList<Integer>();
        Vector Lambda = new Vector(A.edit().length);

        while ((dfx.add(Lambda.mul(A))).norme() > eps) { //condition KKT
            A = gradMatrix(dg, x);
            dfx = df.exec(x);
            System.out.println("iteration " + k);

            System.out.println("determination de la direction ...");
            System.out.println("gradiant en x:" + dfx);
            dk = (dfx).scal(-1); //dk = -W*dfx
            if (!cstActive.isEmpty()) {
                subA = A.subMatrixLig(cstActive);
                majLambda = subA.mul(subA.T()).invert().mul(subA);
                Vector tmp = majLambda.mul(dk);
                dk = dk.sub(tmp.mul(subA));

            }
            System.out.println("direction choisie: " + dk);

            System.out.println("calcul du pas ...");
            //alpha = wolf(dk, x);
            alpha = wolf(dk, x);
            // is x+alpha*dk admissible ?
            isAdmissible = true;
            for (Function g : constraints) {
                isAdmissible = isAdmissible && (g.exec(x.add(dk.scal(alpha))).get(0) <= 0);
            }
            //prise en compte des contraintes :
            if (!isAdmissible) {
                ArrayList<Integer> new_cstActive = new ArrayList<Integer>();
                for (int i = 0; i < A.edit().length; i++) {
                    Vector ai = A.getLig(i);
                    double tmp = -(constraints[i].exec(x).get(0)) / ai.mul(dk); //intersection entre dk et la contrainte gi
                    if ((0 < tmp && tmp < alpha) && !cstActive.contains(i)) {
                        new_cstActive.clear();
                        new_cstActive.add(i);
                        alpha = tmp;
                    } else if (tmp == alpha) {
                        cstActive.add(i);
                    }
                }
                cstActive.addAll(new_cstActive);
            }

            System.out.println("pas choisi: " + alpha);

            //maj des lambda et relachement de contrainte
            Lambda = new Vector(Lambda.length());
            if (!cstActive.isEmpty()) {
                subA = A.subMatrixLig(cstActive);
                majLambda = subA.mul(subA.T()).invert().mul(subA);
                Vector sousLambda = majLambda.mul(dfx.scal(-1));
                for (int i = 0; i < cstActive.size(); i++) {
                    Lambda.edit()[cstActive.get(i)] = sousLambda.get(i);
                }
                for (int i = 0; i < Lambda.length(); i++) {
                    if (Lambda.get(i) <= 0) {
                        cstActive.remove(Integer.valueOf(i)); //sinon supprime l'element d'indice i au lieu de supprimer i
                        Lambda.edit()[i] = 0;
                    }
                }
            }

            System.out.println("contraintes actives :" + cstActive);
            System.out.println("Lambda : " + Lambda);
            System.out.println("A : \n" + A);

            nextX = x.add(dk.scal(alpha)); //nextX = X + alpha*d
            k++;
            W = nextW(dk.scal(alpha), x, nextX);
            x = nextX;

            System.out.println("f(x) = " + f.exec(x).get(0));
            System.out.println("KKT: " + dfx.add(Lambda.mul(A)) + dfx.add(Lambda.mul(A)).norme() + "\n");
        }
        isAdmissible = true;
        for (Function g : constraints) {
            isAdmissible = isAdmissible && (g.exec(x).get(0) <= 0);
        }
        System.out.println(isAdmissible);
        return x;
    }

    /**
     *
     * @param constraints
     * @return
     */
    public Vector bfgs2(Function[] constraints) {
        Vector dk;
        double alpha;
        Vector nextX;
        Matrix H;
        boolean isAdmissible;

        Matrix majLambda;
        Matrix subA;

        System.out.println("demarrage de l'optimisation\n");
        Vector x = x0.clone();
        Vector dfx = df.exec(x);
        int k = 0;

        Function[] dg = new Function[constraints.length];
        for (int i = 0; i < dg.length; i++) {
            dg[i] = getGrad(constraints[i]);
        }
        Matrix A = gradMatrix(dg, x);

        ArrayList<Integer> cstActive = new ArrayList<Integer>();
        Vector Lambda = new Vector(A.edit().length);

        while ((dfx.add(Lambda.mul(A))).norme() > eps) { //condition KKT
            A = gradMatrix(dg, x);
            dfx = df.exec(x);
            System.out.println("iteration " + k);

            System.out.println("determination de la direction ...");
            System.out.println("gradiant en x:" + dfx);
            /*H = W.clone();
            for (int i: cstActive){
                Vector ai = A.getLig(i);
                H = H.sub(H.mul(ai).TMul(ai).mul(H).scal(ai.mul(H).mul(ai))); //H = H - H*ai.T*ai*H/(ai*H*ai.T) (ai etant une ligne de A)
            }
            dk = H.mul(dfx).scal(-1); //dk = -W*dfx*/
            dk = (dfx).scal(-1); //dk = -W*dfx
            if (!cstActive.isEmpty()) {
                subA = A.subMatrixLig(cstActive);
                majLambda = subA.mul(subA.T()).invert().mul(subA);
                Vector tmp = majLambda.mul(dk);
                dk = dk.sub(tmp.mul(subA));

            }
            System.out.println("direction choisie: " + dk);

            System.out.println("calcul du pas ...");
            alpha = wolfe_rec(0, Double.POSITIVE_INFINITY, 1, constraints, dk, x);
            //Is x+alpha*dk admissible ?
            isAdmissible = true;
            for (Function g : constraints) {
                isAdmissible = isAdmissible && (g.exec(x.add(dk.scal(alpha))).get(0) <= 0);
            }
            System.out.println("debug " + isAdmissible);
            //update constrains :
            cstActive.clear();
            for (int i = 0; i < constraints.length; i++) {
                System.out.println("debug2 " + constraints[i].exec(x.add(dk.scal(alpha))).get(0));
                if (Math.pow(constraints[i].exec(x.add(dk.scal(alpha))).get(0), 2) < 0.05) {
                    cstActive.add(i);
                }
            }
            System.out.println("pas choisi: " + alpha);

            //maj des lambda et relachement de contrainte
            Lambda = new Vector(Lambda.length());
            if (!cstActive.isEmpty()) {
                Matrix tmp = A.subMatrixLig(cstActive);
                majLambda = tmp.mul(tmp.T()).invert().mul(tmp);
                Vector sousLambda = majLambda.mul(dfx.scal(-1));
                for (int i = 0; i < cstActive.size(); i++) {
                    Lambda.edit()[cstActive.get(i)] = sousLambda.get(i);
                }
                for (int i = 0; i < Lambda.length(); i++) {
                    if (Lambda.get(i) <= 0) {
                        cstActive.remove(Integer.valueOf(i)); //sinon supprime l'element d'indice i au lieu de supprimer i
                        Lambda.edit()[i] = 0;
                    }
                }
            }

            System.out.println("contraintes actives :" + cstActive);
            System.out.println("Lambda : " + Lambda);

            nextX = x.add(dk.scal(alpha)); //nextX = X + alpha*d
            k++;
            W = nextW(dk.scal(alpha), x, nextX);
            x = nextX;

            System.out.println("f(x) = " + f.exec(x).get(0));
            System.out.println("KKT: " + dfx.add(Lambda.mul(A)) + dfx.add(Lambda.mul(A)).norme() + "\n");
        }
        //System.out.println("Terminer en "+k+" operation, f(x) = " + f.exec(x).get(0));
        return x;
    }

    /**
     * Returns a matrix which each line is {@code df(X)}
     *
     * @param dfs
     * @param X
     * @return
     */
    private Matrix gradMatrix(Function[] dfs, Vector X) {
        Matrix res = new Matrix(dfs.length, dim);
        for (int i = 0; i < dfs.length; i++) {
            res.edit()[i] = dfs[i].exec(X);
        }
        return res;
    }

    /**
     * Return an approximation of the grad
     *
     * @param f the {@code Function}
     * @return a new {@code Function} that approximate the grad
     */
    private Function getGrad(Function f) {
        return x -> {
            Vector res = new Vector(x.length());
            Vector x_h;
            double h = Math.pow(2, -20);
            for (int i = 0; i < x.length(); i++) {
                x_h = x.clone();
                x_h.edit()[i] += h;
                res.edit()[i] = (f.exec(x_h).get(0) - f.exec(x).get(0)) / h;
            }
            return res;
        };
    }

    /**
     * The function to calculate Wk+1 of BFGS method
     *
     * @param sk equals to {@code alpha*dk}
     * @param x the current x
     * @param nextX the next x calculated (equals to {@code x + alpha * dk})
     * @return the {@code Matrix Wk+1}
     */
    private Matrix nextW(Vector sk, Vector x, Vector nextX) {
        Vector yk = df.exec(nextX).sub(df.exec(x));
        double tmp = 1. / sk.mul(yk);
        Matrix tmp2 = Matrix.I(dim).sub(sk.TMul(yk).scal(tmp));
        Matrix tmp3 = Matrix.I(dim).sub(yk.TMul(sk).scal(tmp));
        return tmp2.mul(W).mul(tmp3).add(sk.TMul(sk).scal(tmp));
    }

    /**
     * Wolfe method to calculate a good {@code alpha} that minimize
     * {@code f(X + alpha * dk)}
     *
     * @param dk direction to take
     * @param x current x
     * @return a good alpha
     */
    private double wolf(Vector dk, Vector x) {
        double alpha = 1;
        double alpha_inf = 0, alpha_sup = Double.POSITIVE_INFINITY;
        double w1 = 0.1, w2 = 0.9;
        double dfx_dk = df.exec(x).mul(dk);
        double fx = f.exec(x).get(0);
        boolean finish = false;
        while (!finish) {
            //System.out.println(alpha_inf);
            if (f.exec(x.add(dk.scal(alpha))).get(0) <= fx + w1 * alpha * dfx_dk) {
                if (df.exec(x.add(dk.scal(alpha))).mul(dk) > w2 * dfx_dk) {
                    finish = true;
                } else {
                    alpha_inf = alpha;
                    alpha = Double.isInfinite(alpha_sup) ? 2 * alpha_inf : (alpha_inf + alpha_sup) / 2;
                }
            } else {
                alpha_sup = alpha;
                alpha = (alpha_inf + alpha_sup) / 2;
            }
            if (alpha_sup - alpha_inf < eps) {
                finish = true;
            }
        }
        return alpha;
    }

    private double wolfe_rec(double a_i, double a_s, double a, Function[] constraints, Vector dk, Vector x) {
        double w1 = 0.1, w2 = 0.9, prec = 10;
        int max_rec = 3;
        double dfx_dk = df.exec(x).mul(dk);
        double fx = f.exec(x).get(0);
        boolean finish = false;
        boolean aAdmissible = true;
        boolean a_iAdmissible = true;
        boolean a_sAdmissible = true;
        while (!finish) {
            aAdmissible = true;
            a_iAdmissible = true;
            a_sAdmissible = true;
            for (Function g : constraints) {
                aAdmissible = (aAdmissible) && (g.exec(x.add(dk.scal(a))).get(0) <= 0);
                //System.out.println(g.exec(x));
                a_iAdmissible = (a_iAdmissible) && (g.exec(x.add(dk.scal(a_i))).get(0) <= 0);
                a_sAdmissible = (a_sAdmissible) && (g.exec(x.add(dk.scal(a_s))).get(0) <= 0);
            }
            //System.out.println(a + ":" + aAdmissible + ", " + a_i + ":" + a_iAdmissible + ", " + a_s + ":" + a_sAdmissible);

            if (aAdmissible) {
                if (f.exec(x.add(dk.scal(a))).get(0) <= fx + w1 * a * dfx_dk) {
                    if (df.exec(x.add(dk.scal(a))).mul(dk) > w2 * dfx_dk) {
                        finish = true;
                    } else {
                        a_i = a;
                        a = Double.isInfinite(a_s) ? 2 * a_i : (a + a_s) / 2;
                    }
                } else {
                    a_s = a;
                    a = (a_i + a_s) / 2;
                }
                if (a_s - a_i < eps) {
                    finish = true;
                }
            } else {
                if (a > Math.pow(2, max_rec) || (!a_iAdmissible && !a_sAdmissible && (a_s - a_i < prec))) {
                    finish = true;
                } else {
                    double a1 = wolfe_rec(a_i, a, (a_i + a) / 2, constraints, dk, x);
                    double a2 = wolfe_rec(a, a_s, Double.isInfinite(a_s) ? 2 * a : (a + a_s) / 2, constraints, dk, x);
                    boolean a1Admissible = true, a2Admissible = true;
                    for (Function g : constraints) {
                        a1Admissible = a1Admissible && (g.exec(x.add(dk.scal(a1))).get(0) <= 0);
                        a2Admissible = a2Admissible && (g.exec(x.add(dk.scal(a2))).get(0) <= 0);
                    }
                    if (f.exec(x.add(dk.scal(a1))).get(0) > f.exec(x.add(dk.scal(a2))).get(0)) {
                        a = a2Admissible ? a2 : a1;
                    } else {
                        a = a1Admissible ? a1 : a2;
                    }

                    finish = true;
                }
            }
        }
        return a;
    }
}
