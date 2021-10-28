package com.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BlockValue {
    List<String> lines;
    String tCalc;
    MappedVariable[] mappedVariables;
    static final String[] ALPHABET = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    public BlockValue(List<String> lines, String tCalc, MappedVariable[] mappedVariables) {
        this.lines = lines;
        this.tCalc = tCalc;
        this.mappedVariables = mappedVariables;
    }

    public BlockValue(List<String> lines) {
        this.lines = lines;
        this.tCalc = "";
        this.mappedVariables = new MappedVariable[0];
    }

    public BlockValue copy() {
        return new BlockValue(new ArrayList<>(this.lines), this.tCalc, this.mappedVariables);
    }

    public void addCost(String extraCost) {
        this.tCalc += extraCost;
    }

    public void deleteTrailingSign() {
        if (tCalc.charAt(tCalc.length()-1) == '+') {
            tCalc = tCalc.substring(0, tCalc.length()-1);
        }
    }

    private void addMappedVariable(String newVariable) {
        MappedVariable[] newMappedVariables = new MappedVariable[this.mappedVariables.length+1];
        System.arraycopy(this.mappedVariables, 0, newMappedVariables, 0, this.mappedVariables.length);
        newMappedVariables[this.mappedVariables.length] = new MappedVariable(newVariable, ALPHABET[this.mappedVariables.length]);
        this.mappedVariables = newMappedVariables;
    }

    public String findMappedVariable(String originalVariable) {
        for (MappedVariable mappedVariable : this.mappedVariables) {
            if (mappedVariable.originalValue.equals(originalVariable)) {
                return mappedVariable.newValue;
            }
        }
        this.addMappedVariable(originalVariable);
        return this.mappedVariables[this.mappedVariables.length-1].newValue;
    }

    public String getLine() {
        String line = lines.get(0);
        lines.remove(0);
        return line;
    }
}

class MappedVariable {
    String originalValue;
    String newValue;

    public MappedVariable(String originalValue, String newValue) {
        this.originalValue = originalValue;
        this.newValue = newValue;
    }
}

public abstract class Analyzer {

    static final String START = "(INICIO)|(Inicio)|(inicio)";
    static final String END = "(PARE)|(Pare)|(pare)";

    static final String IF_BLOCK_START = "(SI)|(Si)|(si)";
    static final String ELSE_BLOCK_START = "(SINO)|(Sino)|(sino)";
    static final String IF_BLOCK_END = "(FSI)|(Fsi)|(fsi)";

    static final String FOR_LOOP_START = "(PARA)|(Para)|(para)";
    static final String FOR_LOOP_END = "(FPARA)|(Fpara)|(fpara)";

    static final String READ = "(LEA)|(Lea)|(lea)";
    static final String WRITE = "(ESC)|(Esc)|(esc)";

    static final String ADDITION = "([0-9]+)([a-z_]+)\\+([0-9]+)([a-z_]+)";
    static final String MULTIPLICATION = "([0-9]+)([a-z_]+)\\*([0-9]+)([a-z_]+)";
    static final String IF_BLOCK = "\\[\\{([0-9\\+_]+)\\}\\{([0-9\\+_]+)\\}\\]";
    static final String EXTRA_PARENTHESES = "\\(([0-9a-z_]+)\\)";

    public static String solveFile(ArrayList<String> lines) throws Exception {
        int firstLineIndex = searchFor(START, lines) + 1;

        BlockValue bv = new BlockValue(lines.subList(firstLineIndex,lines.size()));
        String basicCost = solveBlock(bv, END).tCalc;

        return minimize(basicCost);
    }

    private static int searchFor(String pattern, List<String> lines) {
        int curLineIndex = 0;
        while (curLineIndex < lines.size() && !Pattern.matches(pattern, lines.get(curLineIndex))) {
            curLineIndex ++;
        }
        return curLineIndex;
    }

    private static BlockValue solveBlock(BlockValue inputData,  String END_PATTERN) throws Exception {
        BlockValue blockData = inputData.copy();
        String curLine = blockData.getLine();
        String curLineFirstWord = curLine.split(" ")[0];

        while (!Pattern.matches(END_PATTERN, curLineFirstWord)) {
//            System.out.println(curLine+"  ["+END_PATTERN+"]");

            if (Pattern.matches(FOR_LOOP_START, curLineFirstWord)) {
                String loopVar = blockData.findMappedVariable(parseForLoop(curLine));

                blockData.addCost("1_+1"+loopVar+"*(1_+");
                blockData = solveBlock(blockData, FOR_LOOP_END );
                blockData.addCost(")+");

                curLine = blockData.getLine();
                curLineFirstWord = curLine.split(" ")[0];
                continue;
            }

            if (Pattern.matches(IF_BLOCK_START, curLineFirstWord)) {
                int nextElse = searchFor(ELSE_BLOCK_START, blockData.lines);
                int nextEndIf = searchFor(IF_BLOCK_END, blockData.lines);

                if (nextElse < nextEndIf) {
                    blockData.addCost("[{");
                    blockData = solveBlock(blockData, ELSE_BLOCK_START);
                    blockData.addCost("}{");
                    blockData = solveBlock(blockData, IF_BLOCK_END);
                    blockData.addCost("}]+");
                } else {
                    blockData.addCost("(");
                    blockData = solveBlock(blockData, IF_BLOCK_END);
                    blockData.addCost(")+");
                }

                curLine = blockData.getLine();
                curLineFirstWord = curLine.split(" ")[0];
                continue;
            }

            blockData.addCost("1_+");
            curLine = blockData.getLine();
            curLineFirstWord = curLine.split(" ")[0];
        }

//        System.out.println(curLine+"  ["+END_PATTERN+"]");

        blockData.deleteTrailingSign();
        return blockData.copy();
    }

    private static String parseForLoop(String loopLine) {
        String[] vals_for = loopLine.split("=")[1].split(",");

        if (vals_for[2].equals( "+1")) {
            return vals_for[1] + (vals_for[0].equals("1")? "":"-" + (Integer.parseInt(vals_for[0])-1));
        } else {
            return vals_for[0] + (vals_for[1].equals("1")? "":"-" + (Integer.parseInt(vals_for[1])-1));
        }
    }

    private static String minimize(String inputString) {
        String resultString = inputString;
        System.out.println(resultString);

        // STEP 1: SOLVE IF-BLOCKS
        resultString = solveIfBlocks(resultString);
        System.out.println(resultString);

        // STEP 2: ADD AND MULTIPLY STRAY NUMBERS
        resultString = simplifyInstructionCount(resultString);
        System.out.println(resultString);

        return resultString;
    }

    private static String solveIfBlocks(String inputString) {
        String resultString = inputString;
        Pattern p = Pattern.compile(IF_BLOCK);
        Matcher m = p.matcher(resultString);

        while (m.find()) {
            String blockValue1 = solveAdditions(m.group(1));
            String blockValue2 = solveAdditions(m.group(2));

            blockValue1 = blockValue1.substring(0, blockValue1.length()-1);
            blockValue2 = blockValue2.substring(0, blockValue2.length()-1);

            resultString = m.replaceFirst(Math.max(Integer.parseInt(blockValue1), Integer.parseInt(blockValue2))+"_");
            m = p.matcher(resultString);
        }

        return resultString;
    }

    private static String simplifyInstructionCount(String inputString) {
        String resultString = inputString;
        int lastLength = resultString.length()+1;

        while (lastLength != resultString.length()) {
            lastLength = resultString.length();
            resultString = removeExtraParentheses(resultString);
            System.out.println(resultString);
            resultString = solveMultiplications(resultString);
            System.out.println(resultString);
            resultString = solveAdditions(resultString);
            System.out.println(resultString);
        }

        return resultString;
    }

    private static String removeExtraParentheses(String inputString) {
        String resultString = inputString;
        Pattern p = Pattern.compile(EXTRA_PARENTHESES);
        Matcher m = p.matcher(resultString);

        while (m.find()) {
            resultString = m.replaceFirst("" + m.group(1));
            m = p.matcher(resultString);
        }

        return resultString;
    }

    private static String solveAdditions(String inputString) {
        String resultString = inputString;
        Pattern p = Pattern.compile(ADDITION);
        int i = 0;

        while (i < resultString.length()) {
            String search = resultString.substring(i);
            Matcher m = p.matcher(search);
            if (m.find()) {
                if (m.group(2).equals(m.group(4))) {
                    int addResult = Integer.parseInt(m.group(1)) + Integer.parseInt(m.group(3));
                    resultString = resultString.substring(0, i) + m.replaceFirst(addResult + m.group(2));
//                } else {
//                    if (m.group(2).equals("_")) {
//                        resultString = resultString.substring(0, i) + m.replaceFirst(m.group(3)+m.group(4)+"+"+m.group(1)+m.group(2));
//                    }
                }
            }
            i++;
        }

        return resultString;
    }

    private static String solveMultiplications(String inputString) {
        String resultString = inputString;
        Pattern p = Pattern.compile(MULTIPLICATION);
        int i = 0;

        while (i < resultString.length()) {
            String search = resultString.substring(i);
            Matcher m = p.matcher(search);
            if (m.find()) {
                if (m.group(2).equals("_") || m.group(4).equals("_")) {
                    int multResult = Integer.parseInt(m.group(1)) * Integer.parseInt(m.group(3));
                    String base = m.group(2).equals("_") ? m.group(4) : m.group(2);
                    resultString = resultString.substring(0,i) + m.replaceFirst(multResult+base);
                } else if (m.group(2).equals(m.group(4))) {
                    int multResult = Integer.parseInt(m.group(1)) * Integer.parseInt(m.group(3));
                    resultString = resultString.substring(0,i) + m.replaceFirst(multResult+m.group(2));
                }
            }
            i++;
        }

        return resultString;
    }
}
