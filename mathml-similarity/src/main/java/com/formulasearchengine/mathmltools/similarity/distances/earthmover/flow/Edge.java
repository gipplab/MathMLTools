package com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow;

/**
 * @author Andre Greiner-Petter
 * originally from Telmo Menezes (telmo@telmomenezes.com)
 */
public class Edge {

    private int to;
    private long cost;

    public Edge(int to, long cost) {
        this.to = to;
        this.cost = cost;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
