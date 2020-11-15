/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.optimisation;

import java.util.Collection;

/**
 * A class that implements {@code Vector} as {@code double[]} with several
 * operations like addition or multiplication
 *
 * @author RONSAIN Antoine
 */
public class Vector implements Cloneable {

    /**
     * Data of the {@code Vector}
     */
    private double[] data;

    /**
     * Length of the {@code Vector}
     */
    private int dim;

    /**
     * Construct an empty {@code Vector}
     *
     * @param dim the length of the {@code Vector}
     */
    public Vector(int dim) {
        this.dim = dim;
        data = new double[dim];
    }

    /**
     * Construct a {@code Vector} from a {@code double[]} data
     *
     * @param vect the {@code double[]} data
     */
    public Vector(double[] vect) {
        this(vect.length);
        data = vect.clone();
    }

    /**
     *
     * @return a new allocation of this {@code Vector}
     */
    @Override
    public Vector clone() {
        return new Vector(data.clone());
    }

    /**
     *
     * @return the length of the {@code Vector}
     */
    public int length() {
        return dim;
    }

    /**
     * get the index {@code i} of the {@code Vector}
     *
     * @param i index to get
     * @return a double that equals {@code data[i]}
     */
    public double get(int i) {
        return data[i];
    }

    /**
     * Returns a {@code double[]} to edit the {@code Vector}.
     * <p>
     * Use : {@code v.edit()[i] = 2} to edit the {@code i} index of the
     * {@code Vector}
     *
     * @return the {@code double[] data} of the {@code Vector}
     */
    public double[] edit() {
        return data;
    }

    /**
     * Returns a {@code Vector} that is a subvector composed of the indexes of
     * the {@code Vector} specified in {@code elements}
     *
     * @param elements an {@code Integer[]} that contains the index of the
     * values of the {@code Vector} to keep
     * @return a new {@code Vector} subvector
     */
    public Vector subVect(Integer... elements) {
        Vector res = new Vector(elements.length);
        for (int i = 0; i < elements.length; i++) {
            res.data[i] = data[elements[i]];
        }
        return res;
    }

    /**
     * Returns a {@code Vector} that is a subvector composed of the indexes of
     * the {@code Vector} specified in {@code indexToKeep}
     *
     * @param indexToKeep an object that extends
     * {@code Collection<? extends Integer>} that contains the index of the
     * values of the {@code Vector} to keep. Can be an
     * {@code ArrayList<Integer>} for exemple.
     * @return
     */
    public Vector subVect(Collection<? extends Integer> indexToKeep) {
        return subVect(indexToKeep.toArray(new Integer[0]));
    }

    /**
     * A general function to contract add, sub and term to term multiplication
     * and division
     *
     * @param other the other {@code Vector} to apply the operation
     * @param op a character that represents the operation. Can be
     * {@code +, -, *, /}
     * @return A new {@code Vector} that is the result of {@code this op other}
     */
    public Vector operator(Vector other, char op) {
        double[] res = new double[dim];
        for (int i = 0; i < dim; i++) {
            switch (op) {
                case '+':
                    res[i] = data[i] + other.data[i];
                    break;
                case '-':
                    res[i] = data[i] - other.data[i];
                    break;
                case '*':
                    res[i] = data[i] * other.data[i];
                    break;
                case '/':
                    res[i] = data[i] / other.data[i];
                    break;
            }
        }
        return new Vector(res);
    }

    /**
     * Addition of this {@code Vector} and an other
     *
     * @param other the other {@code Vector} to apply the operation
     * @return A new {@code Vector} that is the result
     */
    public Vector add(Vector other) {
        return operator(other, '+');
    }

    /**
     * Sub of this {@code Vector} and an other
     *
     * @param other the other {@code Vector} to apply the operation
     * @return A new {@code Vector} that is the result
     */
    public Vector sub(Vector other) {
        return operator(other, '-');
    }

    /**
     * Term to term multiplication of this {@code Vector} and an other
     *
     * @param other the other {@code Vector} to apply the operation
     * @return A new {@code Vector} that is the result
     */
    public Vector termMul(Vector other) {
        return operator(other, '*');
    }

    /**
     * Term to term division of this {@code Vector} and an other
     *
     * @param other the other {@code Vector} to apply the operation
     * @return A new {@code Vector} that is the result
     */
    public Vector termDiv(Vector other) {
        return operator(other, '/');
    }

    /**
     * Scalar multiplication
     *
     * @param a the scalar
     * @return A new {@code Vector} that is the result
     */
    public Vector scal(double a) {
        Vector res = this.clone();
        for (int i = 0; i < dim; i++) {
            res.data[i] *= a;
        }
        return res;
    }

    /**
     * A multiplication of this {@code Vector} and an other.
     * <p>
     * In math, is equivalent of {@code  <this|other>}
     *
     * @param other the other {@code Vector} to apply the operation
     * @return A new {@code double} that is the result
     */
    public double mul(Vector other) {
        double res = 0;
        for (int i = 0; i < dim; i++) {
            res += data[i] * other.data[i];
        }
        return res;
    }

    /**
     * A multiplication of this {@code Vector} and an other.
     * <p>
     * In math, is equivalent of {@code  this * other.T}
     *
     * @param other the other {@code Vector} to apply the operation
     * @return A new {@code Matrix} that is the result
     */
    public Matrix TMul(Vector other) {
        double[][] res = new double[dim][other.dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < other.dim; j++) {
                res[i][j] = data[i] * other.data[j];
            }
        }
        return new Matrix(res);
    }

    /**
     * A multiplication of this {@code Vector} and an other.
     * <p>
     * In math, is equivalent of {@code  this.T * other}
     *
     * @param other the other {@code Matrix} to apply the operation
     * @return A new {@code Vector} that is the result
     */
    public Vector mul(Matrix other) {
        Vector res = new Vector(other.getM());
        for (int i = 0; i < res.dim; i++) {
            res.data[i] = this.mul(other.getCol(i));
        }
        return res;
    }

    /**
     * Returns the sum of all terms of the {@code Vector}
     *
     * @return the sum of all terms of the {@code Vector}
     */
    public double sum() {
        double res = 0;
        for (int i = 0; i < dim; i++) {
            res += data[i];
        }
        return res;
    }

    /**
     * Returns the norm 2 of the {@code Vector}
     * <p>
     * In math, is equivalent of {@code ||this||^2} or {@code <this|this>}
     *
     * @return the norm 2 of the {@code Vector}
     */
    public double norme() {
        return this.mul(this);
    }

    /**
     *
     * @return a string that contains all the data of the {@code Vector}
     */
    @Override
    public String toString() {
        String res = "[";
        for (int i = 0; i < dim; i++) {
            res += data[i] + "  ";
        }
        res += "]";
        return res;
    }
}
