package com.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BlockValue {
    int lineSkip;
    String tCalc;

    public BlockValue(int lineSkip, String tCalc) {
        this.lineSkip = lineSkip;
        this.tCalc = tCalc;
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

    static final String ADDITION = "([0-9]+)\\+([0-9]+)";
    static final String MULTIPLICATION = "\\(([a-zA-Z]+[\\+\\-]?[0-9]*)\\)\\*\\(([0-9]+)\\)";

    public static String solveFile(ArrayList<String> lines) throws Exception {
        int firstLineIndex = searchFor(START, lines) + 1;

        String basicCost = solveBlock( lines.subList(firstLineIndex,lines.size()), END, firstLineIndex ).tCalc;

        return simplifyInstructionCount(basicCost);
    }

    private static int searchFor(String pattern, List<String> lines) {
        int curLineIndex = 0;
        while (!Pattern.matches(pattern, lines.get(curLineIndex)) && curLineIndex < lines.size()) {
            curLineIndex ++;
        }
        return curLineIndex;
    }

    private static BlockValue solveBlock(List<String> lines, String END_PATTERN, int startingLineIndex) throws Exception {
        String tCalc = "";
        for (int i = 0; i < lines.size(); i++) {
            String curLine = lines.get(i);
            String curLineFirstWord = curLine.split(" ")[0];

            if (Pattern.matches(FOR_LOOP_START, curLineFirstWord)) {
                BlockValue vals = solveBlock(lines.subList(i+1, lines.size()), FOR_LOOP_END, startingLineIndex + i + 1 );
                i = vals.lineSkip - startingLineIndex;
                String[] vals_for = curLine.split("=")[1].split(",");
                String it_for;

                if (vals_for[2].equals( "+1")) {
                    it_for = vals_for[1] + (vals_for[0].equals("1")? "":"-" + (Integer.parseInt(vals_for[0])-1));
                } else {
                    it_for = vals_for[0] + (vals_for[1].equals("1")? "":"-" + (Integer.parseInt(vals_for[1])-1));
                }
                tCalc += "1+("+it_for+")*(1+" + vals.tCalc + ")+";
                continue;
            }

            if (Pattern.matches(IF_BLOCK_START, curLineFirstWord)) {
                int nextElse = searchFor(ELSE_BLOCK_START, lines.subList(i+1, lines.size()))+i+2;
                int nextEndIf = searchFor(IF_BLOCK_END, lines.subList(i+1, lines.size()))+i+2;

                if (nextElse < nextEndIf) {
                    BlockValue vals = solveBlock(lines.subList(i+1, nextElse), ELSE_BLOCK_START, startingLineIndex + i + 1);
                    i = vals.lineSkip - startingLineIndex;
                    int tcostIf = Integer.parseInt(simplifyInstructionCount(vals.tCalc));

                    vals = solveBlock(lines.subList(i+1, nextEndIf), IF_BLOCK_END, startingLineIndex + i + 1);
                    i = vals.lineSkip - startingLineIndex;
                    int tcostElse = Integer.parseInt(simplifyInstructionCount(vals.tCalc));

                    tCalc += ""+(Math.max(tcostElse,tcostIf)+1);
                } else {
                    BlockValue vals = solveBlock(lines.subList(i+1, nextEndIf), IF_BLOCK_END, startingLineIndex + i + 1);
                    i = vals.lineSkip - startingLineIndex;
                    int tcostIf = Integer.parseInt(simplifyInstructionCount(vals.tCalc));
                    tCalc += "" + (tcostIf+1);
                }

                continue;
            }
            if (Pattern.matches(END_PATTERN, curLineFirstWord)) {
                if (tCalc.charAt(tCalc.length()-1) == '+') {
                    tCalc = tCalc.substring(0, tCalc.length()-1);
                }
                return new BlockValue(i + startingLineIndex, tCalc);
            }

            tCalc += "1+";
        }
        throw new Exception(
                "Code blocks are not correct:\n" +
                "Searching for " + END_PATTERN + " turned no results." +
                "Starting from line ["+(startingLineIndex)+"]"
        );
    }

    private static String simplifyInstructionCount(String inputString) {
        String resultString = inputString;
        Pattern p = Pattern.compile(ADDITION);
        Matcher m = p.matcher(resultString);

        while (m.find()) {
            resultString = m.replaceFirst("" + (Integer.parseInt(m.group(1)) + Integer.parseInt(m.group(2))));
            m = p.matcher(resultString);
        }

        p = Pattern.compile(MULTIPLICATION);
        m = p.matcher(resultString);

        while (m.find()) {
            resultString = m.replaceFirst((m.group(2) +"("+ m.group(1)+")"));
            m = p.matcher(resultString);
        }

        return resultString;
    }
}
