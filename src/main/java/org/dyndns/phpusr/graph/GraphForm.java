package org.dyndns.phpusr.graph;

import com.mxgraph.util.mxResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
    private JPanel pnlBtn;
    private JButton btnExit;
    private JButton btnNew;
    private JButton btnDelete;
    private JTextField txtHead;
    private JLabel lblVertex;
    private GraphUtil util;

    public GraphForm(GraphUtil graphUtil) {
        this.util = graphUtil;
        util.setLblVertex(lblVertex);
        lblVertex.setText("");
        localizeForm();

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.addVertex();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.exit();
            }
        });
        btnNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(util.getGraphComponent(),
                        mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {
                    util.clear();
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.deleteCell();
            }
        });
        txtHead.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {                
                util.task(txtHead.getText());
            }
        });
    }

    /**
     * Локализация компонентов формы
     */
    private void localizeForm() {
        btnNew.setText(mxResources.get("new"));
        btnAdd.setText(mxResources.get("add"));
        btnDelete.setText(mxResources.get("delete"));
        btnExit.setText(mxResources.get("exit"));
    }

    /**
     * Создание или получение экземпляра данного класса
     * @param util Утилита работа с библиотекой графа
     * @return Экземпляр данного класса
     */
    public synchronized static JPanel getInstance(GraphUtil util){
        if ( INSTANCE == null ) {
            INSTANCE = new GraphForm(util);
        }
        return INSTANCE.pnlMain;
    }

    /**
     * Ручное создание компонентов формы
     */
    private void createUIComponents() {
        pnlGraph = new JPanel(new BorderLayout());
        pnlGraph.setSize(Const.FRAME_WIDTH, Const.FRAME_HEIGHT);
        pnlGraph.add(util.getGraphComponent());
    }
}
