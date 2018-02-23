package com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow;

/**
 * @author Andre Greiner-Petter
 * originally from Telmo Menezes (telmo@telmomenezes.com)
 */
public class EdgeFlow extends Edge {

    private long flow;

    public EdgeFlow(int to, long cost, long flow) {
        super(to, cost);
        this.flow = flow;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }
}
