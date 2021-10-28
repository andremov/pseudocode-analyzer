package com.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CodeBlock {
    List<CodeBlock> codeBlocks;
    List<String> fileLines;

    public CodeBlock(List<String> fileLines) {
        this.codeBlocks = new ArrayList<>();
        this.fileLines = fileLines;
    }

    public void crawl() {
        int codeBlockStart = 0;
        int codeBlockEnd = 0;
        int pendingClosures = 0;
        Block currentBlock = Block.CODE;

        for (int i = 0; i < fileLines.size(); i++) {
            String curLine = fileLines.get(i);
            String curInstruction = curLine.split(" ")[0];

            if (pendingClosures > 0) {
                if (Pattern.matches(Analyzer.getBlockEndPattern(currentBlock), curInstruction)) {
                    pendingClosures --;
                    if (pendingClosures == 0) {
                        this.addCodeBlock(Analyzer.newBlockClass(currentBlock, this.fileLines.subList(codeBlockStart, codeBlockEnd+1)));
                        codeBlockStart = i;
                        codeBlockEnd = i-1;
                        currentBlock = Block.CODE;
                    }
                } else if (Pattern.matches(Analyzer.getBlockStartPattern(currentBlock), curInstruction)) {
                    pendingClosures ++;
                }
            } else if (Pattern.matches(Analyzer.FOR_LOOP_START, curInstruction) || Pattern.matches(Analyzer.IF_BLOCK_START, curInstruction)) {
                if (codeBlockStart < codeBlockEnd) {
                    this.addCodeBlock(new CodeBlock(fileLines.subList(codeBlockStart, codeBlockEnd)));
                }
                codeBlockStart = i;
                codeBlockEnd = i-1;
                pendingClosures ++;
                currentBlock = Pattern.matches(Analyzer.FOR_LOOP_START, curInstruction) ? Block.FOR_LOOP : Block.IF_BLOCK;
            }
            codeBlockEnd ++;
        }
        if (codeBlockStart < codeBlockEnd) {
            this.addCodeBlock(new CodeBlock(fileLines.subList(codeBlockStart, codeBlockEnd)));
        }

        for (CodeBlock equationItem : this.codeBlocks) {
            if (equationItem instanceof ForLoop || equationItem instanceof IfBlock) {
                equationItem.crawl();
            }
        }
    }

    public void addCodeBlock(CodeBlock obj) {
        this.codeBlocks.add(obj);
    }

    public EquationObject parseEquation() {
        if (codeBlocks.size() <= 1) {
            return new Variable(this.fileLines.size());
        } else {
            Equation eq = new Equation();
            for (CodeBlock codeBlock : codeBlocks) {
                eq.addEquationObject(codeBlock.parseEquation());
                eq.addEquationObject(new Symbol(Symbol.ADDITION));
            }
            eq.removeTrailingSymbol();
            return eq;
        }
    }
}

