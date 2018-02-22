package com.formulasearchengine.mathmlsim.distances.earthmover.flow;

/**
 * @author Andre Greiner-Petter
 * originally from Telmo Menezes (telmo@telmomenezes.com)
 */
public class ResidualReducedEdge extends ReducedEdge {
    private long residualCapacity;

    ResidualReducedEdge(int to, long reducedCost, long residualCapacity) {
        super(to, reducedCost);
        this.residualCapacity = residualCapacity;
    }

    public long getResidualCapacity() {
        return residualCapacity;
    }

    public void setResidualCapacity(long residualCapacity) {
        this.residualCapacity = residualCapacity;
    }
}
