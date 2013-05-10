package org.dyndns.phpusr.graph;

import com.mxgraph.io.gd.mxGdDocument;
import com.mxgraph.io.mxGdCodec;
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
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * 
 * @author phpusr
 * Date: 27.05.12
 * Time: 17:57
 */

public class GraphUtil {

    private final mxGraph graph;
    private Object parent;
    private final mxGraphComponent graphComponent;
    private final JFrame frame;
    /** Кол-во вершин */
    private int countVertex;
    /** Логгирование */
    private final Logger logger;

    /** Таблица Крускала */
    private List<Kruskal> kruskalTable;

    public GraphUtil(GraphEditor frame) {
        logger = LoggerFactory.getLogger(GraphUtil.class);
        this.frame = frame;
        countVertex = 1;

        graph = new mxGraph();
        customGraph(graph);
        parent = graph.getDefaultParent();

        graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setBackground(Color.WHITE);

        graph.getModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                logger.debug("CHANGE");
                onChange();
            }
        });
        graph.addListener(mxEvent.ADD_CELLS, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                logger.debug("ADD_CELLS");
                onChange();
            }
        });
        getGraphComponent().getGraphControl().addMouseListener(new MouseAdapter() {
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
        generateEdgeValues();
        //Сброс стиля для веришн
        resetStyleCells(graph.getChildVertices(parent));
        //Сброс стиля для граней
        resetStyleCells(graph.getChildEdges(parent));

        graph.refresh();
    }

    /** Генератор значений для вершин */
    private void generateEdgeValues() {
        List<Object> edges = Arrays.asList(graph.getChildEdges(parent));

        for (Object edge : edges) {
            mxCell cell = (mxCell) edge;
            if (cell.getValue().equals("")) {
                cell.setValue(Integer.toString(Math.round((float)Math.random()*100)));
            }
        }


    }

    /** Алгоритм Крускала */
    public int kruskal() {
        System.out.println(">>Kruskal");
        //Ребра
        List<Object> edges = Arrays.asList(graph.getChildEdges(parent));
        //Вершины
        Object[] vertices = graph.getChildVertices(parent);
        //Компоненты
        List<List<mxCell>> components = new ArrayList<List<mxCell>>();
        //Таблица Крускала
        kruskalTable = new ArrayList<Kruskal>();

        //Сортируем ребра по весу
        sortList(edges);
        System.out.print("\t--Edges:\t\t");
        for (Object edge : edges) {
            mxCell cell = (mxCell) edge;
            if (edges.indexOf(cell) > 0) System.out.print(",");
            System.out.print(cell.getValue());
        }
        System.out.println();

        //Формируем Список компоненты
        for (Object vertice : vertices) {
            mxCell cell = (mxCell) vertice;
            ArrayList<mxCell> cells = new ArrayList<mxCell>();
            cells.add(cell);
            components.add(cells);
        }
        System.out.print("\t--Componets:\t");
        printComponents(components);

        System.out.println(">>Build Graph");
        int cost = 0;
        for (Object edge : edges) {
            mxCell cell = (mxCell) edge;
            Kruskal kruskal = new Kruskal(cell);

            boolean cykle = isCycle(cell, components);
            if (!cykle) {
                kruskal.setUse(true);
                cost += Integer.parseInt(cell.getValue().toString());
                kruskal.setCost(cost);
            } else {
                kruskal.setUse(false);
            }
            kruskalTable.add(kruskal);
            System.out.print(kruskal);

            if (!cykle) printComponents(components);
            else System.out.println();
        }
        onChange();
        System.out.println(">>Min cost: " + cost + "\n");

        return cost;
    }

    /** Образует ли добавление cell цикл */
    private boolean isCycle(mxCell cell, List<List<mxCell>> components) {
        List<mxCell> sourceComponent = null, targetComponent = null;

        for (List<mxCell> component : components) {
            //Если вершины из одной компоненты
            if (component.contains(cell.getSource()) && component.contains(cell.getTarget())) {
                return true;
            } else { //Если из разных
                if (component.contains(cell.getSource())) {
                    sourceComponent = component;
                } else if (component.contains(cell.getTarget())) {
                    targetComponent = component;
                }
            }
        }

        if (sourceComponent == null || targetComponent == null) return false;

        if (sourceComponent.size() > targetComponent.size()) {
            sourceComponent.addAll(targetComponent);
            targetComponent.clear();
        } else {
            targetComponent.addAll(sourceComponent);
            sourceComponent.clear();
        }

        return false;
    }

    /** Вывод Компонент */
    private void printComponents(List<List<mxCell>> components) {
        for (List<mxCell> component : components) {
            System.out.print("(");
            for (mxCell cell : component) {
                if (component.indexOf(cell) > 0) System.out.print(",");
                System.out.print(cell.getValue());
            }
            System.out.print(")");
        }
        System.out.println();
    }

    /** Сортировка списка ребер */
    private void sortList(List<Object> list) {
        Collections.sort(list, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                int value1 = Integer.parseInt(((mxICell) o1).getValue().toString());
                int value2 = Integer.parseInt(((mxICell) o2).getValue().toString());

                return Integer.compare(value1, value2);
            }
        });
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

        if (kruskalTable != null) {
            mxCell[] delVert = getDelVert();
            graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, mxUtils.hexString(Const.KRUSKAL_FONT_COLOR), delVert);
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(Const.KRUSKAL_STROKE_COLOR), delVert);
        }
    }

    /** Возвращает удаляемые ребра */
    private mxCell[] getDelVert() {
        mxCell[] cells = new mxCell[kruskalTable.size()];
        int count = 0;
        for (Kruskal kruskal : kruskalTable) {
            if (!kruskal.isUse()) {
                cells[count++] = kruskal.getEdge();
            }
        }

        return cells;
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
            mxCell cell = new mxCell(title, new mxGeometry(x, y, Const.VERTEX_WIDTH, Const.VERTEX_HEIGHT), null);
            cell.setVertex(true);
            graph.addCell(cell);
        } finally {
            graph.getModel().endUpdate();
        }
    }

    /**
     * Удаляет вершину или грань графа
     */
    public void deleteCell() {
        mxGraphActions.getDeleteAction().actionPerformed(new ActionEvent(getGraphComponent(), 0, ""));
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    /**
     * Показывает текстовое представление графа
     */
    public void getEncodeGraph() {
        String content = mxGdCodec.encode(graph).getDocumentString();

        System.out.println("Encode:\n" + content);
    }

    /**
     * Сохраняет граф в файл
     * @param filename Имя файла с графом
     * @throws IOException
     */
    public void saveToFile(String filename) throws IOException {
        String content = mxGdCodec.encode(graph).getDocumentString();
        mxUtils.writeFile(content, filename);
    }

    /**
     * Открывает граф из файла
     * @param file Файл с графом
     * @throws IOException
     */
    public void openFile(File file) throws IOException {
        mxGdDocument document = new mxGdDocument();
        document.parse(mxUtils.readFile(file.getAbsolutePath()));
        openGD(file, document);
        countVertex = graph.getChildVertices(parent).length+2;
    }

    /** Открывает граф из файла */
    private void openGD(File file, mxGdDocument document) {

        // Replaces file extension with .mxe
        String filename = file.getName();
        filename = filename.substring(0, filename.length() - 4) + ".mxe";

        if (new File(filename).exists()
                && JOptionPane.showConfirmDialog(getGraphComponent(),
                mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
            return;
        }

        ((mxGraphModel) graph.getModel()).clear();
        mxGdCodec.decode(document, graph);
        parent = graph.getDefaultParent();
        getGraphComponent().zoomAndCenter();

        onChange();
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