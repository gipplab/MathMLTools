package com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow;

/**
 * @author Andre Greiner-Petter
 * originally from Telmo Menezes (telmo@telmomenezes.com)
 */
public class EdgeDistance {

    private int to;
    private long dist;

    EdgeDistance() {
        this(0, 0);
    }

    EdgeDistance(int to, long dist) {
        this.to = to;
        this.dist = dist;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public long getDist() {
        return dist;
    }

    public void setDist(long dist) {
        this.dist = dist;
    }
}
