package com.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfBlock extends CodeBlock {
    List<CodeBlock> codeBlocksAlt;
    int evals;

    public IfBlock(List<String> fileLines) {
        super(fileLines.subList(1, fileLines.size()));
        this.evals = calcEvals(fileLines.get(0));
        this.codeBlocksAlt = new ArrayList<>();
    }

    @Override
    public void crawl() {
        int codeBlockStart = 0;
        int codeBlockEnd = 0;

        for (int i = 0; i < fileLines.size(); i++) {
            String curLine = fileLines.get(i);
            String curInstruction = curLine.split(" ")[0];

            if (Pattern.matches(Analyzer.ELSE_BLOCK_START, curInstruction)) {
                if (codeBlockStart != codeBlockEnd) {
                    this.addCodeBlock(new CodeBlock(fileLines.subList(codeBlockStart, codeBlockEnd)));
                    codeBlockStart = i+1;
                    codeBlockEnd = i;
                }
            }

            codeBlockEnd ++;
        }
        if (codeBlockStart == 0) {
            this.addCodeBlock(new CodeBlock(fileLines.subList(codeBlockStart, codeBlockEnd)));
        } else {
            this.addAltCodeBlock(new CodeBlock(fileLines.subList(codeBlockStart, codeBlockEnd)));
        }

        for (CodeBlock equationItem : this.codeBlocks) {
            if (equationItem instanceof ForLoop || equationItem instanceof IfBlock) {
                 equationItem.crawl();
            }
        }
    }

    public void addAltCodeBlock(CodeBlock obj) {
        this.codeBlocksAlt.add(obj);
    }

    private int calcEvals(String line) {
        return Pattern.matches("("+Analyzer.IF_BLOCK_START+") \\(.+\\) [yoYO] \\(.+\\)", line)? 2 : 1;
    }

    private int blockLength(List<CodeBlock> blockList) {
        if (blockList.size() == 0) {
            return 0;
        }
        return blockList.get(0).fileLines.size();
    }

    public EquationObject parseEquation() {
        return new Variable(this.evals+Math.max(blockLength(this.codeBlocks), blockLength(this.codeBlocksAlt)));
    }
}
