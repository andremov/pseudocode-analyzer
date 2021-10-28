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

    public boolean canAdd(Variable otherVariable) {
         return this.base.equals(otherVariable.base) && this.exponent == otherVariable.exponent;
    }

    public Variable add(Variable otherVariable) {
        return new Variable(this.coefficient+otherVariable.coefficient, this.base, this.exponent);
    }

    public boolean canMultiply(Variable otherVariable) {
        return this.base.equals(otherVariable.base) || this.base.equals("") || otherVariable.base.equals("");
    }

    public Variable multiply(Variable otherVariable) {
        int newCoefficient = this.coefficient * otherVariable.coefficient;
        String newBase = this.base.equals("")? otherVariable.base : this.base;
        int newExponent = this.base.equals("") || otherVariable.base.equals("")? this.exponent * otherVariable.exponent : this.exponent + otherVariable.exponent;
        return new Variable(newCoefficient, newBase, newExponent);
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
