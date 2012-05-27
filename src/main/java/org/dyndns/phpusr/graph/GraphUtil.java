package org.dyndns.phpusr.graph;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import java.awt.*;

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


    public GraphUtil() {
        graph = new mxGraph();
        parent = graph.getDefaultParent();

        graphComponent = new mxGraphComponent(graph);
    }

    public void addVertex() {
        graph.getModel().beginUpdate();
        try {
            graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30);
        }
        finally {
            graph.getModel().endUpdate();
        }
    }

    //TODO метод определения длины между графами, я так и не нашел метод опр длину
    public void getDist() {
        System.out.println("getDist()");

        final Object[] cells = graphComponent.getCells(new Rectangle(0, 0, 800, 600));
        for (Object cell : cells) {
            System.out.println("Geometry: " + graph.getCellGeometry(cell));
        }

        final Object[] edges = graph.getAllEdges(cells);
        System.out.println("edges size=" + edges.length);

        for (Object edge : edges) {
            final mxGeometry geometry = graph.getCellGeometry(edge);
            System.out.println(graph.getLabel(edge) + " - " + geometry);
        }
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }
}

