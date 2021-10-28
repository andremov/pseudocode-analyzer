package com.analyzer;

import java.util.ArrayList;
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
        EquationObject lastObj = equationObjectList.get(equationObjectList.size()-1);

        if (firstObj instanceof Symbol && ((Symbol) firstObj).symbolType == Symbol.ADDITION) {
            this.equationObjectList.remove(0);
        }
        if (lastObj instanceof Symbol && ((Symbol) lastObj).symbolType == Symbol.ADDITION) {
            this.equationObjectList.remove(this.equationObjectList.size()-1);
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
