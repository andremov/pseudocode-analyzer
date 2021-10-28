package com.analyzer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class PseudocodeAnalyzer {

    static Window window;

    String fileLocation;
    ArrayList<String> fileLines;

    public static void main(String[] args) {
        new PseudocodeAnalyzer();
    }

    public PseudocodeAnalyzer() {
        ActionListener browseAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBrowseAction();
            }
        };
        fileLines = new ArrayList<>();
        window = new Window(browseAction);
    }

    private String convertLines(ArrayList<String> lines) {
        StringBuilder result = new StringBuilder();
        int numDigits = ("" + lines.size()).length();
        for (int i = 0; i < lines.size(); i++) {
            result.append(" ").append("[").append(formatNumber(i + 1, numDigits)).append("]");
            result.append("  ").append(lines.get(i)).append("\n");
        }
        return String.valueOf(result);
    }

    private String formatNumber(int inputNumber, int numDigits) {
        StringBuilder resultNumber = new StringBuilder("" + inputNumber);
        while (resultNumber.length() < numDigits) {
            resultNumber.insert(0, " ");
        }
        return resultNumber.toString();
    }

    private void readFile(File inputFile) {
        try {
            Scanner reader = new Scanner(inputFile);
            fileLocation = inputFile.getPath();
            while (reader.hasNextLine()) {
                fileLines.add(reader.nextLine());
            }
            reader.close();
            window.updateUI("  " + fileLocation, convertLines(fileLines));
            window.updateTValue("  " + Analyzer.solveFile(fileLines));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(window, "No se pudo leer el archivo.");
            e.printStackTrace();
        }
    }

    private void handleBrowseAction() {
        JFileChooser fileBrowsePopup = new JFileChooser();
        int choice = fileBrowsePopup.showOpenDialog(window);
        if (choice != JFileChooser.APPROVE_OPTION) return;
        readFile(fileBrowsePopup.getSelectedFile());
    }

}
