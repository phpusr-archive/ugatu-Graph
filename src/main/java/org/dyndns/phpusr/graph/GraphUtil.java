package org.dyndns.phpusr.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * 
 * @author phpusr
 * Date: 27.05.12
 * Time: 17:57
 */

/**
 * Класс для работы с Графом
 */
public class GraphUtil {

    /** Граф */
    private final mxGraph graph;
    /** Родитель всех Вершин */
    private Object parent;
    /** Компонент Граф */
    private final mxGraphComponent graphComponent;
    /** Гланое окно */
    private final JFrame frame;
    /** Кол-во вершин */
    private int countVertex;
    /** Логгирование */
    private final Logger logger;

    /** Стек */
    private Stack<mxICell> stack;
    /** Список уже используемых вершин */
    private List<mxICell> finshedList;
    /** Список вершин при обходе Графа */
    private List<mxICell> graphList;

    /** Конструктор */
    public GraphUtil(GraphEditor frame) {
        logger = LoggerFactory.getLogger(GraphUtil.class);
        this.frame = frame;
        countVertex = 1;

        graph = new mxGraph();
        customGraph(graph);
        parent = graph.getDefaultParent();

        graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setBackground(Color.WHITE);

        /** Добавление обработчика изменения Графа */
        graph.getModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                logger.debug("CHANGE");
                onChange();
            }
        });
        /** Добавление обработчика Удаления вершины ПКМ */
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseReleased(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    logger.debug("DELETE");
                    deleteCell();
                }
            }
        });

    }

    /**
     * Включение и отключение опций графа
     * @param graph Граф
     */
    private void customGraph(mxGraph graph) {
        //Отключение висячих Граней
        graph.setAllowDanglingEdges(false);
    }

    /**
     * Запускается при изменении графа
     */
    private void onChange() {
        //Изменения заголовков вершин
        changeEdgeTitles();
        //Сброс стиля для веришн
        resetStyleCells(graph.getChildVertices(parent));
        //Сброс стиля для граней
        resetStyleCells(graph.getChildEdges(parent));

        //Запуск поиска в Глубину
        task("");

        graph.refresh();
    }

    /** Поиск в Глубину с упорядочиванием вершин */
    public void task(String head) {
        stack = new Stack<mxICell>();
        finshedList = new ArrayList<mxICell>();
        graphList = new ArrayList<mxICell>();

        final Object[] vertices = graph.getChildVertices(parent);
        if (vertices.length > 0) {
            List<Object> listV = Arrays.asList(vertices);

            //Поиск начальной указанной с Формы Вершины
            mxCell cell = (mxCell) listV.get(0);
            for (Object o : listV) {
                mxCell tmpCell = (mxCell) o;
                if (tmpCell.getValue().equals(head)) {
                    cell = tmpCell;
                    break;
                }
            }

            if (cell.getEdgeCount() > 0) { // Если у Начальной вершины есть связи с другими вершинами
                stack.push(cell); // Добавляем Начальную вершину в Стек
                while(!stack.empty()) passageGraphDepth(); // Пока стек не пустой запускаем ф-ю: passageGraphDepth()
            }

            printGraphList(graphList); // Ф-я вывода полученного списка вершин
        }
    }

    /** Сортировка списка вершин */
    private void sortList(List<Object> list) {
        Collections.sort(list, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                mxICell cell1 = (mxICell) o1;
                mxICell cell2 = (mxICell) o2;

                return cell1.getValue().toString().compareTo(cell2.getValue().toString()) * -1;
            }
        });
    }

    /** Вывод вершин Графа */
    private void printGraphList(List<mxICell> graphList) {
        StringBuilder string = new StringBuilder();
        for (mxICell cell : graphList) {
            string.append(cell.getValue()).append(", ");
        }
        GraphForm.getInstance(null).getLblVertex().setText(string.toString());

        System.out.println(">> Print Graph List");
        System.out.println(string.append("\n"));
    }

    /** Прохождение Графа в глубь */
    private void passageGraphDepth() {
        mxICell cell = stack.pop(); // Извлекаем вершину из стека
        if (finshedList.indexOf(cell) == -1) { // Если вершины нет в списке используемых
            graphList.add(cell); // Добавляем вершину в список для вывода
            finshedList.add(cell); // Добавляем вершину в список используемых

            List<Object> listV = new ArrayList<Object>(); // Создаем список для вершиных связанных с вершиной cell

            for (int i = 0; i < cell.getEdgeCount(); i++) { // Добавляем в список: listV, все связанные с cell вершины
                mxICell child = ((mxCell)cell.getEdgeAt(i)).getSource();
                if (listV.indexOf(child) == -1) listV.add(child);
                child = ((mxCell)cell.getEdgeAt(i)).getTarget();
                if (listV.indexOf(child) == -1) listV.add(child);

            }
            sortList(listV); // Упорядочиваем список по уменьшению чисел

            for (Object o : listV) { // Проходим по всем вершинам списка: listV
                mxICell child = (mxCell) o;
                if (cell != child && finshedList.indexOf(child) == -1) { // Если вершины нет в списке используемых
                    stack.push(child); // Добавляем вершину в стек
                }
            }
        }
    }

    /**
     * Сброс стиля ячеек на стандартный
     * @param objects Масив вершин
     */
    private void resetStyleCells(Object[] objects) {
        //Размер шрифта
        graph.setCellStyles(mxConstants.STYLE_FONTSIZE, Const.FONT_SIZE_DEF, objects);
        //Жирный шрифт
        graph.setCellStyles(mxConstants.STYLE_FONTSTYLE, Integer.toString(mxConstants.FONT_BOLD), objects);
        //Цвет шрифта
        graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, mxUtils.hexString(Const.FONTCOLOR_DEF), objects);
        //Размер обводки
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, Const.STROKEWIDTH_DEF, objects);
        //Цвет обводки
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(Const.STROKECOLOR_DEF), objects);
        //Цвет заливки
        graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, mxUtils.hexString(Const.FILLCOLOR_DEF), objects);
        //Отключение стрелок у Граней
        graph.setCellStyles(mxConstants.STYLE_ENDARROW, mxConstants.NONE, objects);
    }

    /**
     * Изменяет Заголовок на Гранях
     */
    private void changeEdgeTitles() {
        final Object[] edges = graph.getChildEdges(parent);

        for (Object edge : edges) {
            mxCell cell = (mxCell) edge;
            cell.setValue("");
        }
    }

    /**
     * Добавляет вершину на форму
     */
    public void addVertex() {
        graph.getModel().beginUpdate();
        try {
            int x = (int) (Math.random() * (Const.FRAME_WIDTH - 2 * Const.VERTEX_WIDTH));
            int y = (int) (Math.random() * (Const.FRAME_HEIGHT - 2 * Const.VERTEX_HEIGHT));
            String title = "" + countVertex++;
            mxCell cell = new mxCell(title, new mxGeometry(x, y, Const.VERTEX_WIDTH, Const.VERTEX_HEIGHT), "shape=ellipse");
            cell.setVertex(true);
            graph.addCell(cell);
        } finally {
            graph.getModel().endUpdate();
        }
    }

    /**
     * Удаляет вершину или грань Графа
     */
    public void deleteCell() {
        mxGraphActions.getDeleteAction().actionPerformed(new ActionEvent(graphComponent, 0, ""));
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    /**
     * Очистка графа
     */
    public void clear() {
        ((mxGraphModel) graph.getModel()).clear();
        parent = graph.getDefaultParent();
        countVertex = 1;
    }

    /**
     * Выход
     */
    public void exit() {
        frame.dispose();
    }
}