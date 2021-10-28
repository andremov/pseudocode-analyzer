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
        if (vals_for[2].charAt(0) == '+') {
            String reduction = vals_for[0].equals("1")? "" : "-" + (Integer.parseInt(vals_for[0]) - 1);
            String division = vals_for[2].charAt(1) == '1' ? "" : "/"+vals_for[2].charAt(1);
            String base = "(" + vals_for[1] + reduction + ")" + division;
            return new Variable(1, base);
        } else {
            String reduction = vals_for[1].equals("1")? "" : "-" + (Integer.parseInt(vals_for[1]) - 1);
            String division = vals_for[2].charAt(1) == '1' ? "" : "/"+vals_for[2].charAt(1);
            String base = "(" + vals_for[0] + reduction + ")" + division;
            return new Variable(1, base);
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
