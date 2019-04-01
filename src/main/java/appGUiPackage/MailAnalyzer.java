package appGUiPackage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MailAnalyzer extends JFrame {

    private JPanel panel;
    private JTextField textField;
    private JButton findEMailsContainingButton;
    private JButton classifyAnEMailButton;
    private JButton findRelevantMailsToButton;
    private JLabel logo;

    private boolean firstTimeClassifier = true;
    Naive n;

    public MailAnalyzer(){

        findEMailsContainingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String input = textField.getText();
                String output = null;
                if (input.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Please enter query.\nFor more info on syntax please follow the report.");
                } else {
                    SQLi database = new SQLi();
                    output =SQLi.queryBooleanTable(database ,input);
                    JOptionPane.showMessageDialog(null,output);
                }
            }
        });

        classifyAnEMailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String input = textField.getText();
                String output = null;
                if (input.length()==0) {
                    JOptionPane.showMessageDialog(null,"Please enter absolute email path.");

                }else {
                    if (!new File(input).exists())
                        JOptionPane.showMessageDialog(null, "No such file found. Please enter absolute path");
                    else {
                        try {
                            if (firstTimeClassifier) {
                                n = new Naive();
                                firstTimeClassifier = false;
                            }
                            output = n.waitforUserInput(input);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        JOptionPane.showMessageDialog(null, output);

                    }
                }
            }
        });

        findRelevantMailsToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });

    }

    public static void main(String [] args){
        JFrame frame = new JFrame("MailAnalyzer");
        frame.setContentPane(new MailAnalyzer().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
