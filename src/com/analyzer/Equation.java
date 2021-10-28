package com.analyzer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Equation extends EquationObject {
    List<EquationObject> equationObjectList;

    public Equation() {
        this.equationObjectList = new ArrayList<>();
    }

    public void addEquationObject(EquationObject obj) {
        this.equationObjectList.add(obj);
    }

    public void removeTrailingSymbol() {
        if (this.equationObjectList.size() == 0) {
            return;
        }
        EquationObject firstObj = equationObjectList.get(0);
        EquationObject lastObj = equationObjectList.get(equationObjectList.size() - 1);

        if (firstObj instanceof Symbol && ((Symbol) firstObj).symbolType == Symbol.ADDITION) {
            this.equationObjectList.remove(0);
        }
        if (lastObj instanceof Symbol && ((Symbol) lastObj).symbolType == Symbol.ADDITION) {
            this.equationObjectList.remove(this.equationObjectList.size() - 1);
        }
    }

    public void minimize() {
        int lastLength = toString().length() + 1;

        while (lastLength != toString().length()) {
            lastLength = toString().length();

            for (EquationObject equationObject : equationObjectList) {
                if (equationObject instanceof Equation) {
                    ((Equation) equationObject).minimize();
                }
            }
        }

        for (int i = 0; i < equationObjectList.size(); i++) {
            if (equationObjectList.get(i) instanceof Equation) {
                Equation eqItem = (Equation) equationObjectList.get(i);
                equationObjectList.remove(i);
                equationObjectList.addAll(i, eqItem.equationObjectList);
            }
        }

        lastLength = toString().length() + 1;
        while (lastLength != toString().length()) {
            lastLength = toString().length();

            for (int i = 0; i < equationObjectList.size(); i++) {
                if (equationObjectList.get(i) instanceof Symbol) {
                    Symbol curObj = (Symbol) equationObjectList.get(i);
                    if (curObj.symbolType == Symbol.ADDITION) {
                        if (equationObjectList.get(i - 1) instanceof Variable && equationObjectList.get(i + 1) instanceof Variable) {
                            Variable prevObj = (Variable) equationObjectList.get(i - 1);
                            Variable nextObj = (Variable) equationObjectList.get(i + 1);
                            if (prevObj.canAdd(nextObj)) {
                                Variable result = prevObj.add(nextObj);
                                equationObjectList.remove(i - 1);
                                equationObjectList.remove(i - 1);
                                equationObjectList.remove(i - 1);
                                equationObjectList.add(i - 1, result);
                                i -= 2;
                            }
                        }
                    } else if (curObj.symbolType == Symbol.MULTIPLICATION) {
                        if (equationObjectList.get(i - 1) instanceof Variable && equationObjectList.get(i + 1) instanceof Variable) {
                            Variable prevObj = (Variable) equationObjectList.get(i - 1);
                            Variable nextObj = (Variable) equationObjectList.get(i + 1);
                            if (prevObj.canMultiply(nextObj)) {
                                Variable result = prevObj.multiply(nextObj);
                                equationObjectList.remove(i - 1);
                                equationObjectList.remove(i - 1);
                                equationObjectList.remove(i - 1);
                                equationObjectList.add(i - 1, result);
                                i -= 2;
                            }
                        } else if (equationObjectList.get(i - 1) instanceof Variable && equationObjectList.get(i + 1) instanceof Symbol) {
                            Variable mainVar = (Variable) equationObjectList.get(i - 1);
                            i--;
                            equationObjectList.remove(i); // remove var
                            equationObjectList.remove(i); // remove mult symbol
                            equationObjectList.remove(i); // remove open parentheses
                            int closeParenthesesIndex = i;
                            while (closeParenthesesIndex < equationObjectList.size()) {
                                if (equationObjectList.get(closeParenthesesIndex) instanceof Symbol && ((Symbol) equationObjectList.get(closeParenthesesIndex)).symbolType == Symbol.PARENTHESES_CLOSED) {
                                    break;
                                }
                                closeParenthesesIndex++;
                            }
                            equationObjectList.remove(closeParenthesesIndex); // remove close parentheses
                            for (int j = i; j < closeParenthesesIndex; j++) {
                                if (equationObjectList.get(j) instanceof Variable) {
                                    Variable otherVar = (Variable) equationObjectList.get(j);
                                    if (otherVar.canMultiply(mainVar)) {
                                        equationObjectList.remove(j);
                                        equationObjectList.add(j, otherVar.multiply(mainVar));
                                    } else {
                                        equationObjectList.add(j - 1, mainVar);
                                        equationObjectList.add(j, new Symbol(Symbol.MULTIPLICATION));
                                        j += 2;
                                    }
                                }
                            }
                        }
                    } else if (curObj.symbolType == Symbol.PARENTHESES_OPEN) {
                        if (equationObjectList.get(i + 1) instanceof Variable && equationObjectList.get(i + 2) instanceof Symbol) {
                            Symbol lastObj = (Symbol) equationObjectList.get(i + 2);
                            if (lastObj.symbolType == Symbol.PARENTHESES_CLOSED) {
                                equationObjectList.remove(i);
                                equationObjectList.remove(i + 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void sort() {
        ArrayList<Equation> unsortables = new ArrayList<>();
        ArrayList<Variable> sortables = new ArrayList<>();

        while (equationObjectList.size() > 0) {
            Variable var = (Variable) equationObjectList.get(0);
            equationObjectList.remove(0);
            if (equationObjectList.size() == 0) {
                sortables.add(var);
                break;
            }
            Symbol sym = (Symbol) equationObjectList.get(0);
            equationObjectList.remove(0);
            if (sym.symbolType == Symbol.ADDITION) {
                sortables.add(var);
            } else {
                Equation unsortableEquation = new Equation();
                unsortableEquation.addEquationObject(var);
                unsortableEquation.addEquationObject(sym);
                while (sym.symbolType == Symbol.MULTIPLICATION) {
                    var = (Variable) equationObjectList.get(0);
                    unsortableEquation.addEquationObject(var);
                    equationObjectList.remove(0);
                    if (equationObjectList.size() == 0) {
                        break;
                    }
                    sym = (Symbol) equationObjectList.get(0);
                    unsortableEquation.addEquationObject(sym);
                    equationObjectList.remove(0);
                }
                unsortables.add(unsortableEquation);
            }
        }

        sortables.sort(new SortVariables());

        this.equationObjectList.addAll(unsortables);
        for (EquationObject sortable : sortables) {
            this.equationObjectList.add(new Symbol(Symbol.ADDITION));
            this.equationObjectList.add(sortable);
        }
    }

    @Override
    public String toString() {
        this.removeTrailingSymbol();
        StringBuilder res = new StringBuilder();

        for (EquationObject equationObject : equationObjectList) {
            res.append(equationObject);
        }

        return res.toString();
    }
}

class SortVariables implements Comparator<Variable> {
    @Override
    public int compare(Variable o1, Variable o2) {
        if (o1.base.equals("") && !o2.base.equals("")) {
            return 1;
        } else if (!o1.base.equals("") && o2.base.equals("")) {
            return -1;
        } else {
            if (o1.base.equals(o2.base)) {
                return o2.exponent - o1.exponent;
            } else {
                return o2.base.compareTo(o1.base);
            }
        }
    }
}