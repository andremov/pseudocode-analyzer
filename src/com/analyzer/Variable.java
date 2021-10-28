package com.analyzer;

public class Variable extends EquationObject {
    public String base;
    public int exponent;
    public int coefficient;

    public Variable(int coefficient) {
        this.coefficient = coefficient;
        this.base = "";
        this.exponent = 1;
    }

    public Variable(int coefficient, String base) {
        this.coefficient = coefficient;
        this.base = base;
        this.exponent = 1;
    }

    public Variable(int coefficient, String base, int exponent) {
        this.coefficient = coefficient;
        this.base = base;
        this.exponent = exponent;
    }

    @Override
    public String toString() {
        if (base.equals("")) {
            return coefficient+"";
        } else {
            return (coefficient == 1 ? "" : coefficient) + base + (exponent == 1 ? "" : "^" + exponent);
        }
    }
}
