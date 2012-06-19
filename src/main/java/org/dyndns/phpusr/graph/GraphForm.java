package org.dyndns.phpusr.graph;

import com.mxgraph.util.mxResources;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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
    private JButton btnEncode;
    private JButton btnExit;
    private JButton btnSave;
    private JButton btnOpen;
    private GraphUtil util;

    public GraphForm(GraphUtil graphUtil) {
        this.util = graphUtil;

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.addVertex();
            }
        });
        btnEncode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.getEncodeGraph();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.exit();
            }
        });
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fc = new JFileChooser();
                    fc.addChoosableFileFilter(new DefaultFileFilter(Const.EXT_DEF,
                            Const.CHOOSE_FILE_FILTER_DESCRIP));
                    int rc = fc.showDialog(null, mxResources.get("saveFile"));

                    if (rc == JFileChooser.APPROVE_OPTION) {
                        String filename = fc.getSelectedFile().getAbsolutePath();
                        FileFilter selectedFilter = fc.getFileFilter();

                        //Добавление расширения файлу, если нет
                        if (selectedFilter instanceof DefaultFileFilter) {
                            String ext = ((DefaultFileFilter) selectedFilter)
                                    .getExtension();
                            if (!filename.toLowerCase().endsWith(ext)) {
                                filename += ext;
                            }
                        }

                        //Проверка на существование файла
                        if (new File(filename).exists()
                                && JOptionPane.showConfirmDialog(util.getGraphComponent(),
                                mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
                        {
                            return;
                        }

                        util.saveToFile(filename);
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(util.getGraphComponent(),
                            ex.toString(), mxResources.get("error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.addChoosableFileFilter(new DefaultFileFilter(Const.EXT_DEF,
                        Const.CHOOSE_FILE_FILTER_DESCRIP));
                int rc = fc.showDialog(null, mxResources.get("openFile"));
                if (rc == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = fc.getSelectedFile();
                        util.openFile(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                                util.getGraphComponent(),
                                ex.toString(),
                                mxResources.get("error"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public synchronized static JPanel getInstance(GraphUtil util){
        if ( INSTANCE == null ) {
            INSTANCE = new GraphForm(util);
        }
        return INSTANCE.pnlMain;
    }

    private void createUIComponents() {
        pnlGraph = new JPanel(new BorderLayout());
        pnlGraph.setSize(Const.FRAME_WIDTH, Const.FRAME_HEIGHT);
        pnlGraph.add(util.getGraphComponent());
    }
}
