package com.analyzer;

public class Symbol extends EquationObject {

    static public int ADDITION = 0;
    static public int MULTIPLICATION = 1;
    static public int PARENTHESES_OPEN = 2;
    static public int PARENTHESES_CLOSED = 3;
    int symbolType;

    public Symbol(int type) {
        this.symbolType = type;
    }

    @Override
    public String toString() {
        return (new String[]{"+", "*", "(", ")"})[symbolType];
    }
}
