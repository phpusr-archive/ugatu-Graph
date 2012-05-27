package org.dyndns.phpusr.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author phpusr
 *         Date: 27.05.12
 *         Time: 14:42
 */
public class GraphForm {
    private static GraphForm INSTANCE;

    private JButton btnAdd;
    private JPanel pnlMain;
    private JPanel pnlGraph;
    private GraphUtil util;

    public GraphForm(GraphUtil graphUtil) {
        this.util = graphUtil;

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.addVertex();
                util.getDist();
            }
        });
    }

    public synchronized static JPanel getInstance(GraphUtil util){
        if ( INSTANCE == null ) {
            INSTANCE = new GraphForm(util);
        }
        return INSTANCE.pnlMain;
    }

    public JPanel getPnlMain() {
        return pnlMain;
    }

    private void createUIComponents() {
        pnlGraph = new JPanel(new BorderLayout());
        pnlGraph.setSize(800, 600);
        pnlGraph.add(util.getGraphComponent());
    }
}
