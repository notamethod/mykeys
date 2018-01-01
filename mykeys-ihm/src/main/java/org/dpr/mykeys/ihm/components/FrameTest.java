package org.dpr.mykeys.ihm.components;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class FrameTest extends JFrame

{

    public static void main(String[] args) {
        FrameTest ft = new FrameTest();
        ft.init();
    }

    private void init() {

        JXCollapsiblePane cp = new JXCollapsiblePane();

        // JXCollapsiblePane can be used like any other container
        cp.setLayout(new BorderLayout());

        // the Controls panel with a textfield to filter the tree
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        controls.add(new JLabel("Search:"));
        controls.add(new JTextField(10));
        controls.add(new JButton("Refresh"));
        controls.setBorder(new TitledBorder("Filters"));
        cp.add("Center", controls);

        JFrame frame = new JXFrame();
        frame.setLayout(new BorderLayout());

        // Put the "Controls" first
        frame.add("North", cp);

        // Then the tree - we assume the Controls would somehow filter the tree
        JScrollPane scroll = new JScrollPane(new JTree());
        frame.add("Center", scroll);

        // Show/hide the "Controls"
        JButton toggle = new JButton(cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
        toggle.setText("Show/Hide Search Panel");
        frame.add("South", toggle);

        frame.pack();
        frame.setVisible(true);
    }
}
