package com.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Block {
    CODE,
    FOR_LOOP,
    IF_BLOCK
}

//class BlockValue {
//    List<String> lines;
//    String tCalc;
//
//    public BlockValue(List<String> lines, String tCalc, MappedVariable[] mappedVariables) {
//        this.lines = lines;
//        this.tCalc = tCalc;
//        this.mappedVariables = mappedVariables;
//    }
//
//    public BlockValue(List<String> lines) {
//        this.lines = lines;
//        this.tCalc = "";
//        this.mappedVariables = new MappedVariable[0];
//    }
//
//    public BlockValue copy() {
//        return new BlockValue(new ArrayList<>(this.lines), this.tCalc, this.mappedVariables);
//    }
//
//    public void addCost(String extraCost) {
//        this.tCalc += extraCost;
//    }
//
//    public void deleteTrailingSign() {
//        if (tCalc.charAt(tCalc.length()-1) == '+') {
//            tCalc = tCalc.substring(0, tCalc.length()-1);
//        }
//    }
//
//    private void addMappedVariable(String newVariable) {
//        MappedVariable[] newMappedVariables = new MappedVariable[this.mappedVariables.length+1];
//        System.arraycopy(this.mappedVariables, 0, newMappedVariables, 0, this.mappedVariables.length);
//        newMappedVariables[this.mappedVariables.length] = new MappedVariable(newVariable, ALPHABET[this.mappedVariables.length]);
//        this.mappedVariables = newMappedVariables;
//    }
//
//    public String findMappedVariable(String originalVariable) {
//        for (MappedVariable mappedVariable : this.mappedVariables) {
//            if (mappedVariable.originalValue.equals(originalVariable)) {
//                return mappedVariable.newValue;
//            }
//        }
//        this.addMappedVariable(originalVariable);
//        return this.mappedVariables[this.mappedVariables.length-1].newValue;
//    }
//
//    public String getLine() {
//        String line = lines.get(0);
//        lines.remove(0);
//        return line;
//    }
//}

//class MappedVariable {
//    String originalValue;
//    String newValue;
//
//    public MappedVariable(String originalValue, String newValue) {
//        this.originalValue = originalValue;
//        this.newValue = newValue;
//    }
//}

public abstract class Analyzer {

    static final String START = "(INICIO)|(Inicio)|(inicio)";
    static final String END = "(PARE)|(Pare)|(pare)";

    static final String IF_BLOCK_START = "(SI)|(Si)|(si)";
    static final String ELSE_BLOCK_START = "(SINO)|(Sino)|(sino)";
    static final String IF_BLOCK_END = "(FSI)|(Fsi)|(fsi)";

    static final String FOR_LOOP_START = "(PARA)|(Para)|(para)";
    static final String FOR_LOOP_END = "(FPARA)|(Fpara)|(fpara)";

//    static final String READ = "(LEA)|(Lea)|(lea)";
//    static final String WRITE = "(ESC)|(Esc)|(esc)";
//
//    static final String ADDITION = "([0-9]+)([a-z_]+)\\+([0-9]+)([a-z_]+)";
//    static final String MULTIPLICATION = "([0-9]+)([a-z_]+)\\*([0-9]+)([a-z_]+)";
//    static final String IF_BLOCK = "\\[\\{([0-9\\+_]+)\\}\\{([0-9\\+_]+)\\}\\]";
//    static final String EXTRA_PARENTHESES = "\\(([0-9a-z_]+)\\)";

    public static CodeBlock newBlockClass(Block block, List<String> fileLines) {
        switch(block) {
            case FOR_LOOP:
                return new ForLoop(fileLines);
            case IF_BLOCK:
                return new IfBlock(fileLines);
            default:
                return new CodeBlock(fileLines);
        }
    }

    public static String getBlockStartPattern(Block block) {
        switch(block) {
            case FOR_LOOP:
                return FOR_LOOP_START;
            case IF_BLOCK:
                return IF_BLOCK_START;
            default:
                return START;
        }
    }

    public static String getBlockEndPattern(Block block) {
        switch(block) {
            case FOR_LOOP:
                return FOR_LOOP_END;
            case IF_BLOCK:
                return IF_BLOCK_END;
            default:
                return END;
        }

    }

    public static String solveFile(ArrayList<String> lines) throws Exception {
        int firstLineIndex = searchFor(START, lines) + 1;
        int lastLineIndex = searchFor(END, lines);

        CodeBlock mainCodeBlock = new CodeBlock(lines.subList(firstLineIndex,lastLineIndex));
        mainCodeBlock.crawl();
        Equation mainEquation = (Equation) mainCodeBlock.parseEquation();

        return mainEquation.toString();
    }

    private static int searchFor(String pattern, List<String> lines) {
        int curLineIndex = 0;
        while (curLineIndex < lines.size() && !Pattern.matches(pattern, lines.get(curLineIndex))) {
            curLineIndex ++;
        }
        return curLineIndex;
    }
}
