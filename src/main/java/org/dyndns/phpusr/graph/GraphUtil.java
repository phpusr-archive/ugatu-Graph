package org.dyndns.phpusr.graph;

import com.mxgraph.io.mxGdCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author phpusr
 * Date: 27.05.12
 * Time: 17:57
 */

public class GraphUtil {

    private mxGraph graph;
    private Object parent;
    private mxGraphComponent graphComponent;
    private JFrame frame;
    private int countVertex = 0;
    private int debug = 0;


    public GraphUtil(GraphEditor frame) {
        this.frame = frame;

        graph = new mxGraph();
        parent = graph.getDefaultParent();

        graphComponent = new mxGraphComponent(graph);
        graph.getModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                if (debug > 0) System.out.println("CHANGE");

                changeEdgeTitles();
                //Сброс стиля для веришн
                resetStyleCells(graph.getChildVertices(graph.getDefaultParent()));
                //Сброс стиля для граней
                resetStyleCells(graph.getChildEdges(graph.getDefaultParent()));

                task();

                graph.refresh();
            }
        });
        graph.addListener(mxEvent.ADD_CELLS, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                if (debug > 0) System.out.println("ADD_CELLS");

                changeEdgeTitles();
                resetStyleCells((Object[]) evt.getProperty("cells"));

                graph.refresh();
            }
        });

    }

    /**
     * Найходит расстояния в графе: диаметр, центр, радиус графа
     */
    private void task() {
        mxCell diametr, radius;
        Set<mxCell> maxEdgeList = new HashSet<mxCell>();
        final Object[] vertices = graph.getChildVertices(graph.getDefaultParent());

        if (vertices.length > 0) {
            System.out.println("\n\n========= Vertices =========");
            for (Object vertice : vertices) {
                mxCell cell = (mxCell) vertice;
                mxCell maxEdge = getMaxEdge(cell);
                if (maxEdge != null) {
                    maxEdgeList.add(maxEdge);
                }
            }
        }

        if (maxEdgeList.size() > 0) {
            diametr = maxEdgeList.iterator().next();
            radius = diametr;
            for (mxCell edge : maxEdgeList) {
                if (getDist(edge).compareTo(getDist(diametr)) > 0) {
                    diametr = edge;
                }
                if (getDist(edge).compareTo(getDist(radius)) < 0) {
                    radius = edge;
                }
            }

            System.out.println("=============================");
            showDiametr(diametr);
            System.out.println("Diametr: " + getDist(diametr) + "; " + diametr);

            for (mxCell edge : maxEdgeList) {
                if (getDist(edge).compareTo(getDist(radius)) == 0) {
                    showRadius(edge);
                    System.out.println("Radius: " + getDist(edge) + "; " + edge);
                }
            }
            System.out.println("=============================");
        }

    }

    /**
     * Показывает диаметр на изображении графа
     * @param diametr диаметр
     */
    private void showDiametr(mxCell diametr) {
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(Const.STROKECOLOR_DIAMETR), new Object[]{diametr});
    }

    /**
     * Показывает радиус на изображении графа
     * @param radius радиус
     */
    private void showRadius(mxCell radius) {
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(Const.STROKECOLOR_RADIUS), new Object[]{radius});
        if (radius.getSource() != null) {
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(Const.STROKECOLOR_RADIUS), new Object[]{radius.getSource()});
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, mxUtils.hexString(Const.FILLCOLOR_RADIUS), new Object[]{radius.getSource()});
        }
    }

    /**
     * Находит максимальную по длине грань в передаваеймой вершине
     * @param cell Вершина
     * @return Максимальная по длине грань
     */
    private mxCell getMaxEdge(mxCell cell) {
        if (cell.getEdgeCount() > 0) {
            mxCell maxEdge = (mxCell) cell.getEdgeAt(0);

            if (getDist(maxEdge) != null) {
                for (int i=0; i<cell.getEdgeCount(); i++) {
                    final mxCell edge = (mxCell) cell.getEdgeAt(i);

                    final Double distEdge = getDist(edge);
                    final Double distMaxEdge = getDist(maxEdge);
                    if (distEdge!=null && distEdge.compareTo(distMaxEdge) > 0) {
                        maxEdge = edge;
                    }
                }

                System.out.println(cell.getValue() + ": maxEge=" + getDist(maxEdge, 7));
                return maxEdge;
            }
        }

        return null;
    }

    /**
     * Сброс стиля ячеек на стандартный
     * @param objects Масив вершин
     */
    private void resetStyleCells(Object[] objects) {

        graph.setCellStyles(mxConstants.STYLE_FONTSIZE, Const.FONT_SIZE_STD, objects);
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, mxUtils.hexString(Const.STROKECOLOR_STD), objects);
        graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, mxUtils.hexString(Const.FILLCOLOR_STD), objects);
        graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, mxUtils.hexString(Const.FONTCOLOR_STD), objects);
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, Const.STROKEWIDTH_STD, objects);

    }

    /**
     * Показывает длину грани на ее заголовке
     */
    private void changeEdgeTitles() {
        final Object[] edges = graph.getChildEdges(graph.getDefaultParent());

        for (Object edge : edges) {
            mxCell cell = (mxCell) edge;
            cell.setValue(getDist(cell, 7));
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
            String title = Const.VERTEX_NAME_STD + " " + Integer.toString(++countVertex);
            graph.insertVertex(parent, null, title, x, y, Const.VERTEX_WIDTH, Const.VERTEX_HEIGHT);
        }
        finally {
            graph.getModel().endUpdate();
        }
    }

    /**
     * Возвращает длину грани
     * @param cell Грань
     * @return Длина грани
     */
    private Double getDist(mxCell cell) {
        if (debug > 0) System.out.println("getDist()");

        if (cell.getSource() != null && cell.getTarget() != null) {
            final mxGeometry sourcePoint = cell.getSource().getGeometry();
            final mxGeometry targetPoint = cell.getTarget().getGeometry();

            final double width = Math.pow(sourcePoint.getX() - targetPoint.getX(), 2);
            final double height = Math.pow(sourcePoint.getY() - targetPoint.getY(), 2);

            return Math.pow(width + height, (double)1/2);
        } else {
            return null;
        }
    }

    /**
     * Возвращает длину грани в виде строки, с указанным размером
     * @param cell Грань
     * @param length Длина возвращаемой строки
     * @return Длина грани
     */
    private String getDist(mxCell cell, int length) {
        final Double dist = getDist(cell);
        return dist != null && dist.toString().length() >= length ? dist.toString().substring(0, length-1) : "";
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

    public void saveToFile(String filename) throws IOException {

            String content = mxGdCodec.encode(graph)
                    .getDocumentString();

            mxUtils.writeFile(content, filename);

    }

    public void exit() {
        frame.dispose();
    }

}

