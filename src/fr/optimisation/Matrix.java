/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.optimisation;

import java.util.Collection;

/**
 * A class that implements {@code Matrix} as {@code Vector[]} and several
 * operations
 *
 * @author KNAFF Lucas
 */
public class Matrix implements Cloneable {

    /**
     * Data of the {@code Matrix}.
     * <p>
     * Data is organised in line (ex: {@code data[0]} is a {@code Vector} with
     * the first line of the {@code Matrix})
     */
    private Vector[] data;

    /**
     * Dimension of the {@code Matrix}:
     * <ul>
     * <li>n is the number of line</li>
     * <li>m is the number of column</li>
     * </ul>
     */
    private int n, m;

    /**
     * Create an empty {@code Matrix} with {@code n} lines and {@code m}
     * columns.
     *
     * @param n the number of line
     * @param m the number of column
     */
    public Matrix(int n, int m) {
        data = new Vector[n];
        for (int i = 0; i < n; i++) {
            data[i] = new Vector(m);
        }
        this.n = n;
        this.m = m;
    }

    /**
     * Construct a new {@code Matrix} from data {@code vectmat}
     *
     * @param vectmat a {@code Vector[]} that contains the data of the new
     * {@code Matrix}.
     */
    public Matrix(Vector[] vectmat) {
        this(vectmat.length, vectmat[0].length());
        data = vectmat.clone();
    }

    /**
     * Construct a new {@code Matrix} from data {@code mat}
     *
     * @param mat a {@code double[][]} that contains the data of the new
     * {@code Matrix}.
     */
    public Matrix(double[][] mat) {
        data = new Vector[mat.length];
        for (int i = 0; i < mat.length; i++) {
            data[i] = new Vector(mat[i]);
        }
        this.n = data.length;
        this.m = data[0].length();
    }

    /**
     * A static method to create the identity matrix of dimension {@code dim}.
     *
     * @param dim the dimension of the new {@code Matrix}
     * @return a new square {@code Matrix (dim, dim)} full of 0, and with 1 on
     * the diagonal.
     */
    public static Matrix I(int dim) {
        double[][] res = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            res[i][i] = 1;
        }
        return new Matrix(res);
    }

    /**
     *
     * @return
     */
    @Override
    public Matrix clone() {
        return new Matrix(data.clone());
    }

    /**
     * Returns a {@code Vector[]} to edit the {@code Matrix}.
     * <p>
     * Use : {@code v.edit()[i] = u} to edit the {@code i-eme} line of the
     * {@code Matrix}
     *
     * @return the {@code Vector[] data} of the {@code Matrix}
     */
    public Vector[] edit() {
        return data;
    }

    /**
     * Returns the number of line of the {@code Matrix}
     *
     * @return {@code n}, the number of line of the {@code Matrix}
     */
    public int getN() {
        return n;
    }

    /**
     * Returns the number of column of the {@code Matrix}
     *
     * @return {@code m}, the number of column of the {@code Matrix}
     */
    public int getM() {
        return m;
    }

    /**
     * Returns a copy of the {@code j-eme} column of the {@code Matrix}
     *
     * @param j the number of the column to return
     * @return a {@code Vector} that contains a copy of the {@code j-eme} column
     * of the {@code Matrix}
     */
    public Vector getCol(int j) {
        Vector res = new Vector(n);
        for (int i = 0; i < n; i++) {
            res.edit()[i] = data[i].get(j);
        }
        return res;
    }

    /**
     * Returns a copy of the {@code i-eme} line of the {@code Matrix}
     *
     * @param i the number of the line to return
     * @return a {@code Vector} that contains a copy of the {@code i-eme} line
     * of the {@code Matrix}
     */
    public Vector getLig(int i) {
        return data[i].clone();
    }

    /**
     * Returns the transpose of the {@code Matrix}.
     * <p>
     * In math, is equivalent of {@code this.T}
     *
     * @return A {@code Matrix} that is the transpose of the {@code Matrix}.
     */
    public Matrix T() {
        Matrix res = new Matrix(m, n);
        for (int i = 0; i < res.n; i++) {
            res.data[i] = this.getCol(i);
        }
        return res;
    }

    /**
     * Returns a {@code Matrix} that is a submatrix composed of the lines of the
     * {@code Matrix} specified in {@code ligToKeep}
     *
     * @param ligToKeep an object that extends
     * {@code Collection<? extends Integer>} that contains the index of the
     * lines of the {@code Matrix} to keep. Can be an {@code ArrayList<Integer>}
     * for exemple.
     * @return a new {@code Matrix} submatrix
     */
    public Matrix subMatrixLig(Collection<? extends Integer> ligToKeep) {
        return subMatrixLig(ligToKeep.toArray(new Integer[0]));
    }

    /**
     * Returns a {@code Matrix} that is a submatrix composed of the lines of the
     * {@code Matrix} specified in {@code ligToKeep}
     *
     * @param elements an {@code Integer[]} that contains the index of the lines
     * of the {@code Matrix} to keep
     * @return a new {@code Matrix} submatrix
     */
    public Matrix subMatrixLig(Integer... elements) {
        if (elements.length == 0) {
            return new Matrix(0, 0);
        }
        Matrix res = new Matrix(elements.length, this.m);
        for (int i = 0; i < elements.length; i++) {
            res.edit()[i] = this.getLig(elements[i]);
        }
        return res;
    }

    /**
     * A multiplication of this {@code Matrix} and an other {@code Vector}.
     * <p>
     * In math, is equivalent of {@code  this * other}
     *
     * @param other the other {@code Matrix} to apply the operation
     * @return A new {@code Matrix} that is the result
     */
    public Vector mul(Vector other) {
        Vector res = new Vector(n);
        for (int i = 0; i < n; i++) {
            res.edit()[i] = getLig(i).mul(other);
        }
        return res;
    }

    /**
     * A multiplication of this {@code Matrix} and an other.
     * <p>
     * In math, is equivalent of {@code  this * other}
     *
     * @param other the other {@code Matrix} to apply the operation
     * @return A new {@code Matrix} that is the result
     */
    public Matrix mul(Matrix other) {
        Matrix res = new Matrix(n, other.m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < other.m; j++) {
                res.data[i].edit()[j] = this.getLig(i).mul(other.getCol(j));
            }
        }
        return res;
    }

    /**
     * A general function to contract add, sub and term to term multiplication
     * and division
     *
     * @param other the other {@code Matrix} to apply the operation
     * @param op a character that represents the operation. Can be
     * {@code +, -, *, /}
     * @return A new {@code Matrix} that is the result of {@code this op other}
     */
    public Matrix operator(Matrix other, char op) {
        Vector[] res = new Vector[n];
        for (int i = 0; i < n; i++) {
            res[i] = data[i].operator(other.data[i], op);
        }
        return new Matrix(res);
    }

    /**
     * Addition of this {@code Matrix} and an other
     *
     * @param other the other {@code Matrix} to apply the operation
     * @return A new {@code Matrix} that is the result
     */
    public Matrix add(Matrix other) {
        return operator(other, '+');
    }

    /**
     * Sub of this {@code Matrix} and an other
     *
     * @param other the other {@code Matrix} to apply the operation
     * @return A new {@code Matrix} that is the result
     */
    public Matrix sub(Matrix other) {
        return operator(other, '-');
    }

    /**
     * Scalar multiplication
     *
     * @param a the scalar
     * @return A new {@code Matrix} that is the result
     */
    public Matrix scal(double a) {
        Matrix res = this.clone();
        for (int i = 0; i < n; i++) {
            res.data[i] = res.data[i].scal(a);
        }
        return res;
    }

    /**
     *
     * @return a string that contains all the data of the {@code Matrix}
     */
    @Override
    public String toString() {
        String res = "[";
        for (int i = 0; i < n; i++) {
            res += data[i].toString() + "\n";
        }
        res += "]";
        return res;
    }

    /**
     * A method found online to invert a {@code Matrix}
     *
     * @return the invert of {@code this}
     */
    public Matrix invert() {
        double[][] a = new double[n][n];
        for (int i = 0; i < n; i++) {
            a[i] = data[i].clone().edit();
        }

        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i) {
            b[i][i] = 1;
        }

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    b[index[j]][k]
                            -= a[index[j]][i] * b[index[i]][k];
                }
            }
        }

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return new Matrix(x);
    }

// Method to carry out the partial-pivoting Gaussian
// elimination.  Here index[] stores pivoting order.
    /**
     * Method to carry out the partial-pivoting Gaussian elimination. Here
     * index[] stores pivoting order.
     *
     * @param a the matrix
     * @param index stores pivoting order
     */
    public void gaussian(double a[][], int index[]) {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i) {
            index[i] = i;
        }

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) {
                    c1 = c0;
                }
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[index[i]][j] / a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l) {
                    a[index[i]][l] -= pj * a[index[j]][l];
                }
            }
        }
    }
}
