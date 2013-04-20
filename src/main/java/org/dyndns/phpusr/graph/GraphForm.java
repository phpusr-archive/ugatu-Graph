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

    /** Главная панель */
    private JPanel pnlMain;
    /** Панель для Графа */
    private JPanel pnlGraph;
    /** Панель для кнопок */
    private JPanel pnlBtn;

    /** Кнопка Добавления Вершины */
    private JButton btnAdd;
    /** Кнопка Создания нового Графа */
    private JButton btnNew;
    /** Кнопка Удаления Вершины */
    private JButton btnDelete;
    /** Кнопка Выхода */
    private JButton btnExit;

    /** Поле для ввода Начальной Вершины */
    private JTextField txtHead;
    /** Лейбл для вывода Графа */
    private JLabel lblVertex;
    /** Класс для работы с Графом */
    private GraphUtil util;

    /** Конструктор */
    public GraphForm(GraphUtil graphUtil) {
        this.util = graphUtil;
        lblVertex.setText("");
        localizeForm();

        //Обработчик кнопки Добавления Вершины
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.addVertex();
            }
        });
        //Обработчик кнопки Создания нового Графа
        btnNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(util.getGraphComponent(), mxResources.get("loseChanges"),
                        "Подтверждение", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    util.clear();
                    lblVertex.setText("");
                }
            }
        });
        //Обработчик кнопки Удаления Вершины
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.deleteCell();
            }
        });
        //Обработчик кнопки Выхода
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                util.exit();
            }
        });
        //Обработчик ввода Начальной вершины
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
    public synchronized static GraphForm getInstance(GraphUtil util){
        if ( INSTANCE == null ) {
            INSTANCE = new GraphForm(util);
        }
        return INSTANCE;
    }

    /**
     * Ручное создание компонентов формы
     */
    private void createUIComponents() {
        pnlGraph = new JPanel(new BorderLayout());
        pnlGraph.setSize(Const.FRAME_WIDTH, Const.FRAME_HEIGHT);
        pnlGraph.add(util.getGraphComponent());
    }

    public JLabel getLblVertex() {
        return lblVertex;
    }

    public JPanel getPnlMain() {
        return pnlMain;
    }
}
