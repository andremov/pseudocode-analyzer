package com.analyzer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class Window extends JFrame {

    JLabel fileLocationLabel;
    JLabel tCostLabel;
    JButton browseBtn;
    JTextArea fileDisplayArea;
    JScrollPane scrollPane;

    public Window(ActionListener browseAction) {
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setTitle("Pseudocode Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        init(browseAction);

        setVisible(true);
    }

    private int getPadding(String side) {
        switch (side) {
            case "right":
                return 15;
            case "top":
            case "bottom":
            case "left":
            default:
                return 10;
        }
    }

    private int getInnerWidth() {
        int trueValue = super.getWidth();
        return trueValue - getPadding("left") - getPadding("right");
    }

    private int getElementMargin() {
        return 10;
    }

    private int getInnerHeight() {
        int trueValue = super.getHeight();
        return trueValue - getPadding("top") - getPadding("bottom");
    }

    private int getLocationFromRight(int targetLocation) {
        return getInnerWidth() + getPadding("left") - targetLocation;
    }

    private void init(ActionListener browseAction) {

        int elemPosY = getPadding("top");
        int elemHeight = 40;

        browseBtn = new JButton("Browse");
        browseBtn.setSize(200, elemHeight);
        browseBtn.setLocation(getLocationFromRight(200), elemPosY);
        browseBtn.addActionListener(browseAction);
        add(browseBtn);

        fileLocationLabel = new JLabel("");
        fileLocationLabel.setSize(getInnerWidth() - browseBtn.getWidth() - getElementMargin(), elemHeight);
        fileLocationLabel.setLocation(getPadding("left"), elemPosY);
        fileLocationLabel.setBorder(new LineBorder(new Color(120, 120, 120)));
        fileLocationLabel.setEnabled(false);
        add(fileLocationLabel);

        elemPosY += elemHeight + getElementMargin();
        elemHeight = 450;

        fileDisplayArea = new JTextArea("");
        fileDisplayArea.setEnabled(false);
        fileDisplayArea.setForeground(new Color(0, 0, 0));
        fileDisplayArea.setDisabledTextColor(new Color(0, 0, 0));
        fileDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        scrollPane = new JScrollPane(fileDisplayArea);
        scrollPane.setBorder(new LineBorder(new Color(120, 120, 120)));
        scrollPane.setSize(getInnerWidth(), elemHeight);
        scrollPane.setLocation(getPadding("left"), elemPosY);
        add(scrollPane);

        elemPosY += elemHeight + getElementMargin();
        elemHeight = 40;

        tCostLabel = new JLabel("");
        tCostLabel.setSize(getInnerWidth(), elemHeight);
        tCostLabel.setLocation(getPadding("left"), elemPosY);
        tCostLabel.setBorder(new LineBorder(new Color(120, 120, 120)));
        tCostLabel.setEnabled(false);
        add(tCostLabel);
    }

    public void updateUI(String fileLocation, String fileContents) {
        fileLocationLabel.setText(fileLocation);
        fileDisplayArea.setText(fileContents);
    }

    public void updateTValue(String tValue) {
        tCostLabel.setText(tValue);
    }
}
