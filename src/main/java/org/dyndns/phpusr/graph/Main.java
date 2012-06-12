package org.dyndns.phpusr.graph;

import javax.swing.*;
import java.awt.*;

/**
 * @author phpusr
 *         Date: 27.05.12
 *         Time: 16:04
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }

        MainFrame frame = new MainFrame();
        frame.showFrame();
    }

}

class MainFrame extends JFrame {
    public MainFrame() throws HeadlessException {
        super("JGraph");
        setContentPane(GraphForm.getInstance(new GraphUtil(this)));
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showFrame() {
        setVisible(true);
    }
}
