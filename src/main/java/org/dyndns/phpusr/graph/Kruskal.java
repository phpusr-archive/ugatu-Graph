package org.dyndns.phpusr.graph;

/**
 * Created with IntelliJ IDEA.
 * User: phpusr
 * Date: 28.04.13
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */

import com.mxgraph.model.mxCell;

/**
 * Класс для хранения информации о Вершинах и Ребрах
 * Используется в алгоритме Крускала
 */
public class Kruskal {
    /** Ребро */
    private mxCell edge;

    /** Используется ли */
    private boolean use;

    /** Стоимость */
    private int cost;

    public Kruskal(mxCell edge) {
        this.edge = edge;
    }

    public mxCell getEdge() {
        return edge;
    }

    public void setEdge(mxCell edge) {
        this.edge = edge;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "" +
                edge.getValue() + ".\t\t(" +
                edge.getSource().getValue() + '-' +
                edge.getTarget().getValue() + ")\t" +
                (use ? '+' : '-') + '\t' +
                "C=" + cost + "\t\t";
    }
}
