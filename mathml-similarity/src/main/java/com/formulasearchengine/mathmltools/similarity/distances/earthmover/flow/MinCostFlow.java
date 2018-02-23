package com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow;

/**
 * @author Andre Greiner-Petter
 * originally from Telmo Menezes (telmo@telmomenezes.com)
 */
public class MinCostFlow {

    private int numNodes;
    private java.util.Vector<Integer> nodesToQ;

    // e - supply(positive) and demand(negative).
    // c[i] - edges that goes from node i. first is the second nod
    // x - the flow is returned in it
    public long compute(java.util.Vector<Long> e, java.util.Vector<java.util.List<Edge>> c, java.util.Vector<java.util.List<EdgeFlow>> x) {
        assert e.size() == c.size();
        assert x.size() == c.size();

        numNodes = e.size();
        nodesToQ = new java.util.Vector<Integer>();
        for (int i = 0; i < numNodes; i++) {
            nodesToQ.add(0);
        }

        // init flow
        for (int from = 0; from < numNodes; ++from) {
            for (Edge it : c.get(from)) {
                x.get(from).add(new EdgeFlow(it.getTo(), it.getCost(), 0));
                x.get(it.getTo()).add(new EdgeFlow(from, -it.getCost(), 0));
            }
        }

        // reduced costs for forward edges (c[i,j]-pi[i]+pi[j])
        // Note that for forward edges the residual capacity is infinity
        java.util.Vector<java.util.List<ReducedEdge>> rCostForward = new java.util.Vector<java.util.List<ReducedEdge>>();
        for (int i = 0; i < numNodes; i++) {
            rCostForward.add(new java.util.LinkedList<ReducedEdge>());
        }
        for (int from = 0; from < numNodes; ++from) {
            for (Edge it : c.get(from)) {
                rCostForward.get(from).add(new ReducedEdge(it.getTo(), it.getCost()));
            }
        }

        // reduced costs and capacity for backward edges
        // (c[j,i]-pi[j]+pi[i])
        // Since the flow at the beginning is 0, the residual capacity is
        // also zero
        java.util.Vector<java.util.List<ResidualReducedEdge>> rCostCapBackward = new java.util.Vector<java.util.List<ResidualReducedEdge>>();
        for (int i = 0; i < numNodes; i++) {
            rCostCapBackward.add(new java.util.LinkedList<ResidualReducedEdge>());
        }
        for (int from = 0; from < numNodes; ++from) {
            for (Edge it : c.get(from)) {
                rCostCapBackward.get(it.getTo()).add(
                        new ResidualReducedEdge(from, -it.getCost(), 0));
            }
        }

        // Max supply TODO:demand?, given U?, optimization-> min out of
        // demand,supply
        long u = 0;
        for (int i = 0; i < numNodes; i++) {
            if (e.get(i) > u) {
                u = e.get(i);
            }
        }
        //long delta = (long) (Math.pow(2.0, Math.ceil(Math.log((double) U) / Math.log(2.0))));

        java.util.Vector<Long> d = new java.util.Vector<Long>();
        java.util.Vector<Integer> prev = new java.util.Vector<Integer>();
        for (int i = 0; i < numNodes; i++) {
            d.add(0L);
            prev.add(0);
        }
        long delta = 1;
        long tmp;
        while (true) { // until we break when S or T is empty
            long maxSupply = 0;
            int k = 0;
            for (int i = 0; i < numNodes; i++) {
                if (e.get(i) > 0) {
                    if (maxSupply < e.get(i)) {
                        maxSupply = e.get(i);
                        k = i;
                    }
                }
            }
            if (maxSupply == 0) {
                break;
            }
            delta = maxSupply;

            int[] l = new int[1];
            computeShortestPath(d, prev, k, rCostForward, rCostCapBackward,
                    e, l);

            // find delta (minimum on the path from k to l)
            // delta= e[k];
            // if (-e[l]<delta) delta= e[k];
            int to = l[0];
            do {
                int from = prev.get(to);
                assert from != to;

                // residual
                int itccb = 0;
                while ((itccb < rCostCapBackward.get(from).size())
                        && (rCostCapBackward.get(from).get(itccb).getTo() != to)) {
                    itccb++;
                }
                if (itccb < rCostCapBackward.get(from).size()) {
                    if (rCostCapBackward.get(from).get(itccb).getResidualCapacity() < delta) {
                        delta = rCostCapBackward.get(from).get(itccb).getResidualCapacity();
                    }
                }

                to = from;
            } while (to != k);

            // augment delta flow from k to l (backwards actually...)
            to = l[0];
            do {
                int from = prev.get(to);
                assert from != to;

                // TODO - might do here O(n) can be done in O(1)
                int itx = 0;
                while (x.get(from).get(itx).getTo() != to) {
                    itx++;
                }
                tmp = x.get(from).get(itx).getFlow();
                x.get(from).get(itx).setFlow(tmp + delta);

                // update residual for backward edges
                int itccb = 0;
                while ((itccb < rCostCapBackward.get(to).size())
                        && (rCostCapBackward.get(to).get(itccb).getTo() != from)) {
                    itccb++;
                }
                if (itccb < rCostCapBackward.get(to).size()) {
                    tmp = rCostCapBackward.get(to).get(itccb).getResidualCapacity();
                    rCostCapBackward.get(to).get(itccb).setResidualCapacity(tmp + delta);
                }
                itccb = 0;
                while ((itccb < rCostCapBackward.get(from).size())
                        && (rCostCapBackward.get(from).get(itccb).getTo() != to)) {
                    itccb++;
                }
                if (itccb < rCostCapBackward.get(from).size()) {
                    tmp = rCostCapBackward.get(from).get(itccb).getResidualCapacity();
                    rCostCapBackward.get(from).get(itccb).setResidualCapacity(tmp - delta);
                }

                // update e
                e.set(to, e.get(to) + delta);
                e.set(from, e.get(from) - delta);

                to = from;
            } while (to != k);
        }

        // compute distance from x
        long dist = 0;
        for (int from = 0; from < numNodes; from++) {
            for (EdgeFlow it : x.get(from)) {
                dist += it.getCost() * it.getFlow();
            }
        }
        return dist;
    }

    void computeShortestPath(java.util.Vector<Long> d, java.util.Vector<Integer> prev,
                             int from, java.util.Vector<java.util.List<ReducedEdge>> costForward,
                             java.util.Vector<java.util.List<ResidualReducedEdge>> costBackward, java.util.Vector<Long> e, int[] l) {
        // Making heap (all inf except 0, so we are saving comparisons...)
        java.util.Vector<EdgeDistance> edgeDistanceVector = new java.util.Vector<EdgeDistance>();
        for (int i = 0; i < numNodes; i++) {
            edgeDistanceVector.add(new EdgeDistance());
        }

        edgeDistanceVector.get(0).setTo(from);
        nodesToQ.set(from, 0);
        edgeDistanceVector.get(0).setDist(0);

        int j = 1;
        // TODO: both of these into a function?
        for (int i = 0; i < from; ++i) {
            edgeDistanceVector.get(j).setTo(i);
            nodesToQ.set(i, j);
            edgeDistanceVector.get(j).setDist(Long.MAX_VALUE);
            j++;
        }

        for (int i = from + 1; i < numNodes; i++) {
            edgeDistanceVector.get(j).setTo(i);
            nodesToQ.set(i, j);
            edgeDistanceVector.get(j).setDist(Long.MAX_VALUE);
            j++;
        }

        java.util.Vector<Boolean> finalNodesFlg = new java.util.Vector<Boolean>();
        for (int i = 0; i < numNodes; i++) {
            finalNodesFlg.add(false);
        }
        do {
            int u = edgeDistanceVector.get(0).getTo();

            d.set(u, edgeDistanceVector.get(0).getDist()); // final distance
            finalNodesFlg.set(u, true);
            if (e.get(u) < 0) {
                l[0] = u;
                break;
            }

            heapRemoveFirst(edgeDistanceVector, nodesToQ);

            // neighbors of u
            for (ReducedEdge it : costForward.get(u)) {
                assert it.getCost() >= 0;
                long alt = d.get(u) + it.getCost();
                int v = it.getTo();
                if ((nodesToQ.get(v) < edgeDistanceVector.size())
                        && (alt < edgeDistanceVector.get(nodesToQ.get(v)).getDist())) {
                    heapDecreaseKey(edgeDistanceVector, nodesToQ, v, alt);
                    prev.set(v, u);
                }
            }
            for (ResidualReducedEdge it : costBackward.get(u)) {
                if (it.getResidualCapacity() > 0) {
                    assert it.getCost() >= 0;
                    long alt = d.get(u) + it.getCost();
                    int v = it.getTo();
                    if ((nodesToQ.get(v) < edgeDistanceVector.size())
                            && (alt < edgeDistanceVector.get(nodesToQ.get(v)).getDist())) {
                        heapDecreaseKey(edgeDistanceVector, nodesToQ, v, alt);
                        prev.set(v, u);
                    }
                }
            }

        } while (edgeDistanceVector.size() > 0);

        for (int innerFrom = 0; innerFrom < numNodes; ++innerFrom) {
            for (ReducedEdge it : costForward.get(innerFrom)) {
                if (finalNodesFlg.get(innerFrom)) {
                    it.setCost(it.getCost() + d.get(innerFrom) - d.get(l[0]));
                }
                if (finalNodesFlg.get(it.getTo())) {
                    it.setCost(it.getCost() - d.get(it.getTo()) - d.get(l[0]));
                }
            }
        }

        // reduced costs and capacity for backward edges
        // (c[j,i]-pi[j]+pi[i])
        for (int innerFrom = 0; innerFrom < numNodes; ++innerFrom) {
            for (ResidualReducedEdge it : costBackward.get(innerFrom)) {
                if (finalNodesFlg.get(innerFrom)) {
                    it.setCost(it.getCost() + d.get(innerFrom) - d.get(l[0]));
                }
                if (finalNodesFlg.get(it.getTo())) {
                    it.setCost(it.getCost() - d.get(it.getTo()) - d.get(l[0]));
                }
            }
        }
    }

    void heapDecreaseKey(java.util.Vector<EdgeDistance> q, java.util.Vector<Integer> nodestoQ,
                         int v, long alt) {
        int i = nodestoQ.get(v);
        q.get(i).setDist(alt);
        while (i > 0 && q.get(parent(i)).getDist() > q.get(i).getDist()) {
            swapHeap(q, nodestoQ, i, parent(i));
            i = parent(i);
        }
    }

    void heapRemoveFirst(java.util.Vector<EdgeDistance> q, java.util.Vector<Integer> nodestoQ) {
        swapHeap(q, nodestoQ, 0, q.size() - 1);
        q.remove(q.size() - 1);
        heapify(q, nodestoQ, 0);
    }

    void heapify(java.util.Vector<EdgeDistance> q, java.util.Vector<Integer> nodestoQ, int i) {
        do {
            // TODO: change to loop
            int l = left(i);
            int r = right(i);
            int smallest;
            if ((l < q.size()) && (q.get(l).getDist() < q.get(i).getDist())) {
                smallest = l;
            } else {
                smallest = i;
            }
            if ((r < q.size()) && (q.get(r).getDist() < q.get(smallest).getDist())) {
                smallest = r;
            }

            if (smallest == i) {
                return;
            }

            swapHeap(q, nodestoQ, i, smallest);
            i = smallest;

        } while (true);
    }

    void swapHeap(java.util.Vector<EdgeDistance> q, java.util.Vector<Integer> nodesToQ, int i, int j) {
        EdgeDistance tmp = q.get(i);
        q.set(i, q.get(j));
        q.set(j, tmp);
        nodesToQ.set(q.get(j).getTo(), j);
        nodesToQ.set(q.get(i).getTo(), i);
    }

    int left(int i) {
        return 2 * (i + 1) - 1;
    }

    int right(int i) {
        return 2 * (i + 1); // 2 * (i + 1) + 1 - 1
    }

    int parent(int i) {
        return (i - 1) / 2;
    }

}
