package org.dyndns.phpusr.graph;

import com.mxgraph.io.mxGdCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;

import javax.swing.*;

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


    public GraphUtil(MainFrame frame) {
        this.frame = frame;

        graph = new mxGraph();
        parent = graph.getDefaultParent();

        graphComponent = new mxGraphComponent(graph);
        graph.getModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                System.out.println("CHANGE");

                changeEdgeTitles();
            }
        });
        graph.addListener(mxEvent.ADD_CELLS, new mxEventSource.mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                System.out.println("ADD_CELLS");

                changeEdgeTitles();
                final Object cell = ((Object[]) evt.getProperty("cells"))[0];
                changeStyle(cell);
            }
        });

    }

    //TODO: Найти расстояния в графе, диаметр, центр, радиус графа

    private void changeStyle(Object object) {

        graph.setCellStyles(mxConstants.STYLE_FONTSIZE, "18", new Object[]{object});

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

        graph.refresh();
    }

    /**
     * Добавляет вершину на форму
     */
    public void addVertex() {
        graph.getModel().beginUpdate();
        try {
            int x = (int) (Math.random() * (Const.FRAME_WIDTH - 2 * Const.VERTEX_WIDTH));
            int y = (int) (Math.random() * (Const.FRAME_HEIGHT - 2 * Const.VERTEX_HEIGHT));
            String title = "Vertex " + Integer.toString(++countVertex);
            final Object vertex = graph.insertVertex(parent, null, title, x, y, Const.VERTEX_WIDTH, Const.VERTEX_HEIGHT);
            changeStyle(vertex);
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
        System.out.println("getDist()");

        final mxGeometry sourcePoint = cell.getSource().getGeometry();
        final mxGeometry targetPoint = cell.getTarget().getGeometry();

        final double width = Math.pow(sourcePoint.getX() - targetPoint.getX(), 2);
        final double height = Math.pow(sourcePoint.getY() - targetPoint.getY(), 2);

        return Math.pow(width + height, (double)1/2);
    }

    /**
     * Возвращает длину грани в виде строки, с указанным размером
     * @param cell Грань
     * @param length Длина возвращаемой строки
     * @return Длина грани
     */
    private String getDist(mxCell cell, int length) {
        final Double dist = getDist(cell);
        return dist.toString().substring(0, length-1);
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    //TODO возможно не нужный метод
    public void getEncodeGraph() {
        String content = mxGdCodec.encode(graph).getDocumentString();

        System.out.println("Encode: " + content);
    }

    public void exit() {
        frame.dispose();
    }
}

