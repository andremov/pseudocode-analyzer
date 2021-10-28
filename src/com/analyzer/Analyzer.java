package com.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

enum Block {
    CODE,
    FOR_LOOP,
    IF_BLOCK
}

public abstract class Analyzer {

    static final String START = "(INICIO)|(Inicio)|(inicio)";
    static final String END = "(PARE)|(Pare)|(pare)";

    static final String IF_BLOCK_START = "(SI)|(Si)|(si)";
    static final String ELSE_BLOCK_START = "(SINO)|(Sino)|(sino)";
    static final String IF_BLOCK_END = "(FSI)|(Fsi)|(fsi)";

    static final String FOR_LOOP_START = "(PARA)|(Para)|(para)";
    static final String FOR_LOOP_END = "(FPARA)|(Fpara)|(fpara)";

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

    public static String solveFile(ArrayList<String> lines) {
        int firstLineIndex = searchFor(START, lines) + 1;
        int lastLineIndex = searchFor(END, lines);

        CodeBlock mainCodeBlock = new CodeBlock(lines.subList(firstLineIndex,lastLineIndex));
        mainCodeBlock.crawl();

        Equation mainEquation = (Equation) mainCodeBlock.parseEquation();
        System.out.println(mainEquation); // Original equation directly from code blocks.

        mainEquation.minimize();
        System.out.println(mainEquation); // Minimized equation as much as possible in the code block order.

        mainEquation.sort();
        System.out.println(mainEquation); // Sorted equation in polynomial order.

        mainEquation.minimize();
        System.out.println(mainEquation); // Minimized equation even more.
        System.out.println(); // Empty line for better console reading.

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
