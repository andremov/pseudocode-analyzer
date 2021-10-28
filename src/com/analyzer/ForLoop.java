package com.analyzer;

import java.util.List;

public class ForLoop extends CodeBlock {
    String loopStartLine;
    Variable loopVariable;

    public ForLoop(List<String> fileLines) {
        super(fileLines.subList(1, fileLines.size()));
        this.loopStartLine = fileLines.get(0);
        this.loopVariable = this.parseForLoop(this.loopStartLine);
    }

    private Variable parseForLoop(String loopLine) {
        String[] vals_for = loopLine.split("=")[1].split(",");

        if (vals_for[2].equals( "+1")) {
            return new Variable(1, vals_for[1] + (vals_for[0].equals("1")? "":"-" + (Integer.parseInt(vals_for[0])-1)));
        } else {
            return new Variable(1, vals_for[0] + (vals_for[1].equals("1")? "":"-" + (Integer.parseInt(vals_for[1])-1)));
        }
    }

    public EquationObject parseEquation() {
        Equation eq = new Equation();
        eq.addEquationObject(new Variable(1));
        eq.addEquationObject(new Symbol(Symbol.ADDITION));
        eq.addEquationObject(this.loopVariable);
        eq.addEquationObject(new Symbol(Symbol.MULTIPLICATION));
        eq.addEquationObject(new Symbol(Symbol.PARENTHESES_OPEN));
        eq.addEquationObject(new Variable(1));
        eq.addEquationObject(new Symbol(Symbol.ADDITION));
        for (CodeBlock codeBlock : codeBlocks) {
            eq.addEquationObject(codeBlock.parseEquation());
            eq.addEquationObject(new Symbol(Symbol.ADDITION));
        }
        eq.removeTrailingSymbol();
        eq.addEquationObject(new Symbol(Symbol.PARENTHESES_CLOSED));
        return eq;
    }
}
