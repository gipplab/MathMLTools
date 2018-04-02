package com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow;

/**
 * @author Andre Greiner-Petter
 * originally from Telmo Menezes (telmo@telmomenezes.com)
 */
public class ReducedEdge extends Edge {

    ReducedEdge(int to, long reducedCost) {
        super(to, reducedCost);
    }

}
