package org.dyndns.phpusr.graph;

import com.mxgraph.util.mxResources;

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

        GraphEditor frame = new GraphEditor();
        frame.showFrame();
    }

}

class GraphEditor extends JFrame {

    /**
     * Adds required resources for i18n
     */
    static
    {
        try {
            mxResources.add("org/dyndns/phpusr/graph/editor");
        }
        catch (Exception e) {
            // ignore
        }
    }

    public GraphEditor() throws HeadlessException {
        super("JGraph");
        setContentPane(GraphForm.getInstance(new GraphUtil(this)));
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showFrame() {
        setVisible(true);
    }
}
