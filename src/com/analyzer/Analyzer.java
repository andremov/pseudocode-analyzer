package com.analyzer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Analyzer extends JFrame implements ActionListener {

    JLabel fileLocationLabel;
    JButton browseBtn;
    JTextArea textarea;

    public static void main(String[] args) {
	    new Analyzer();
    }

    public Analyzer() {
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        setTitle("Pseudocode Analyzer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        init();

        setVisible(true);
    }

    private int getPadding(String side) {
        switch(side){
            case "top":
            case "bottom":
            case "left":
            case "right":
            default:
                return 0;
        }
    }

    public int getWidth() {
        int trueValue = super.getWidth();
        return trueValue - getPadding("left") - getPadding("right");
    }

    private void init() {
        fileLocationLabel = new JLabel("");
        fileLocationLabel.setSize(getWidth()-getPadding("left")-getPadding(""),50);
        fileLocationLabel.setLocation(getPadding("left"), getPadding("top"));
        add(fileLocationLabel);

//        display = new Display();
//        display.setSize(300,180);
//        add(display);

//        nextStep = new JButton(">");
//        nextStep.setFocusable(false);
//        nextStep.setSize(50,50);
//        nextStep.addActionListener(this);
//        nextStep.setLocation(getWidth()-60, 220);
//        add(nextStep);

//        prevStep = new JButton("<");
//        prevStep.setFocusable(false);
//        prevStep.setSize(50,50);
//        prevStep.addActionListener(this);
//        prevStep.setLocation(5, 220);
//        add(prevStep);

//        changeConfig = new JButton("Change Problem");
//        changeConfig.setFocusable(false);
//        changeConfig.setSize(150,50);
//        changeConfig.addActionListener(this);
//        changeConfig.setLocation((getWidth()-150)/2, 220);
//        add(changeConfig);

//        displayStep = new JLabel("");
//        displayStep.setSize(20,50);
//        displayStep.setLocation(getWidth()/2 - 20/2, 180);
//        add(displayStep);

//        refreshButtons();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
