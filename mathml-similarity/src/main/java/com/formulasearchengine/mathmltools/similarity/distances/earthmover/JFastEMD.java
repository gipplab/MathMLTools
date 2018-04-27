/**
 * This class computes the Earth Mover's Distance, using the EMD-HAT algorithm
 * created by Ofir Pele and Michael Werman.
 * <p>
 * This implementation is strongly based on the C++ code by the same authors,
 * that can be found here:
 * http://www.cs.huji.ac.il/~ofirpele/FastEMD/code/
 * <p>
 * Some of the author's comments on the original were kept or edited for
 * this context.
 */


package com.formulasearchengine.mathmltools.similarity.distances.earthmover;

import com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow.Edge;
import com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow.EdgeFlow;
import com.formulasearchengine.mathmltools.similarity.distances.earthmover.flow.MinCostFlow;

/**
 * @author Telmo Menezes (telmo@telmomenezes.com)
 * @author Ofir Pele
 */
public class JFastEMD {
    private JFastEMD() { }

    /**
     * This interface is similar to Rubner's interface. See:
     * http://www.cs.duke.edu/~tomasi/software/emd.htm
     * <p>
     * To get the same services as Rubner's code you should set extra_mass_penalty to 0,
     * and divide by the minimum of the sum of the two signature's weights. However, I
     * suggest not to do this as you lose the metric property and more importantly, in my
     * experience the performance is better with emd_hat. for more on the difference
     * between emd and emd_hat, see the paper:
     * A Linear Time Histogram Metric for Improved SIFT Matching
     * Ofir Pele, Michael Werman
     * ECCV 2008
     * <p>
     * To get shorter running time, set the ground distance function to
     * be a thresholded distance. For example: min(L2, T). Where T is some threshold.
     * Note that the running time is shorter with smaller T values. Note also that
     * thresholding the distance will probably increase accuracy. Finally, a thresholded
     * metric is also a metric. See paper:
     * Fast and Robust Earth Mover's Distances
     * Ofir Pele, Michael Werman
     * ICCV 2009
     * <p>
     * If you use this code, please cite the papers.
     */
    public static double distance(Signature signature1, Signature signature2, double extraMassPenalty) {

        java.util.Vector<Double> p = new java.util.Vector<Double>();
        java.util.Vector<Double> q = new java.util.Vector<Double>();
        for (int i = 0; i < signature1.getNumberOfFeatures() + signature2.getNumberOfFeatures(); i++) {
            p.add(0.0);
            q.add(0.0);
        }
        for (int i = 0; i < signature1.getNumberOfFeatures(); i++) {
            p.set(i, signature1.getWeights()[i]);
        }
        for (int j = 0; j < signature2.getNumberOfFeatures(); j++) {
            q.set(j + signature1.getNumberOfFeatures(), signature2.getWeights()[j]);
        }

        java.util.Vector<java.util.Vector<Double>> c = new java.util.Vector<java.util.Vector<Double>>();
        for (int i = 0; i < p.size(); i++) {
            java.util.Vector<Double> vec = new java.util.Vector<Double>();
            for (int j = 0; j < p.size(); j++) {
                vec.add(0.0);
            }
            c.add(vec);
        }
        for (int i = 0; i < signature1.getNumberOfFeatures(); i++) {
            for (int j = 0; j < signature2.getNumberOfFeatures(); j++) {
                double dist = signature1.getFeatures()[i]
                        .groundDist(signature2.getFeatures()[j]);
                assert dist >= 0;
                c.get(i).set(j + signature1.getNumberOfFeatures(), dist);
                c.get(j + signature1.getNumberOfFeatures()).set(i, dist);
            }
        }

        return emdHat(p, q, c, extraMassPenalty);
    }


    private static long emdHatImplLongLongInt(java.util.Vector<Long> pc, java.util.Vector<Long> qc,
                                              java.util.Vector<java.util.Vector<Long>> c, long extraMassPenalty) {

        int n = pc.size();
        assert qc.size() == n;

        // Ensuring that the supplier - P, have more mass.
        // Note that we assume here that C is symmetric
        java.util.Vector<Long> p;
        java.util.Vector<Long> q;
        long absDiffSumPSumQ;
        long sumP = 0;
        long sumQ = 0;
        for (int i = 0; i < n; i++) {
            sumP += pc.get(i);
        }
        for (int i = 0; i < n; i++) {
            sumQ += qc.get(i);
        }
        if (sumQ > sumP) {
            p = qc;
            q = pc;
            absDiffSumPSumQ = sumQ - sumP;
        } else {
            p = pc;
            q = qc;
            absDiffSumPSumQ = sumP - sumQ;
        }

        // creating the b vector that contains all vertexes
        java.util.Vector<Long> b = new java.util.Vector<Long>();
        for (int i = 0; i < 2 * n + 2; i++) {
            b.add(0L);
        }
        int thresholdNode = 2 * n;
        int artificialNode = 2 * n + 1; // need to be last !
        for (int i = 0; i < n; i++) {
            b.set(i, p.get(i));
        }
        for (int i = n; i < 2 * n; i++) {
            b.set(i, q.get(i - n));
        }

        // remark*) I put here a deficit of the extra mass, as mass that flows
        // to the threshold node
        // can be absorbed from all sources with cost zero (this is in reverse
        // order from the paper,
        // where incoming edges to the threshold node had the cost of the
        // threshold and outgoing
        // edges had the cost of zero)
        // This also makes sum of b zero.
        b.set(thresholdNode, -absDiffSumPSumQ);
        b.set(artificialNode, 0L);

        long maxC = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                assert c.get(i).get(j) >= 0;
                if (c.get(i).get(j) > maxC) {
                    maxC = c.get(i).get(j);
                }
            }
        }
        if (extraMassPenalty == -1) {
            extraMassPenalty = maxC;
        }

        java.util.Set<Integer> sourcesThatFlowNotOnlyToThresh = new java.util.HashSet<Integer>();
        java.util.Set<Integer> sinksThatGetFlowNotOnlyFromThresh = new java.util.HashSet<Integer>();
        long preFlowCost = 0;

        // regular edges between sinks and sources without threshold edges
        java.util.Vector<java.util.List<Edge>> cWithout = new java.util.Vector<java.util.List<Edge>>();
        for (int i = 0; i < b.size(); i++) {
            cWithout.add(new java.util.LinkedList<Edge>());
        }
        for (int i = 0; i < n; i++) {
            if (b.get(i) == 0) {
                continue;
            }
            for (int j = 0; j < n; j++) {
                if (b.get(j + n) == 0) {
                    continue;
                }
                if (c.get(i).get(j) == maxC) {
                    continue;
                }
                cWithout.get(i).add(new Edge(j + n, c.get(i).get(j)));
            }
        }

        // checking which are not isolated
        for (int i = 0; i < n; i++) {
            if (b.get(i) == 0) {
                continue;
            }
            for (int j = 0; j < n; j++) {
                if (b.get(j + n) == 0) {
                    continue;
                }
                if (c.get(i).get(j) == maxC) {
                    continue;
                }
                sourcesThatFlowNotOnlyToThresh.add(i);
                sinksThatGetFlowNotOnlyFromThresh.add(j + n);
            }
        }

        // converting all sinks to negative
        for (int i = n; i < 2 * n; i++) {
            b.set(i, -b.get(i));
        }

        // add edges from/to threshold node,
        // note that costs are reversed to the paper (see also remark* above)
        // It is important that it will be this way because of remark* above.
        for (int i = 0; i < n; ++i) {
            cWithout.get(i).add(new Edge(thresholdNode, 0));
        }
        for (int j = 0; j < n; ++j) {
            cWithout.get(thresholdNode).add(new Edge(j + n, maxC));
        }

        // artificial arcs - Note the restriction that only one edge i,j is
        // artificial so I ignore it...
        for (int i = 0; i < artificialNode; i++) {
            cWithout.get(i).add(new Edge(artificialNode, maxC + 1));
            cWithout.get(artificialNode).add(new Edge(i, maxC + 1));
        }

        // remove nodes with supply demand of 0
        // and vertexes that are connected only to the
        // threshold vertex
        int currentNodeName = 0;
        // Note here it should be vector<int> and not vector<int>
        // as I'm using -1 as a special flag !!!
        int removeNodeFlag = -1;
        java.util.Vector<Integer> nodesNewNames = new java.util.Vector<Integer>();
        java.util.Vector<Integer> nodesOldNames = new java.util.Vector<Integer>();
        for (int i = 0; i < b.size(); i++) {
            nodesNewNames.add(removeNodeFlag);
            nodesOldNames.add(0);
        }
        for (int i = 0; i < n * 2; i++) {
            if (b.get(i) != 0) {
                if (sourcesThatFlowNotOnlyToThresh.contains(i)
                        || sinksThatGetFlowNotOnlyFromThresh.contains(i)) {
                    nodesNewNames.set(i, currentNodeName);
                    nodesOldNames.add(i);
                    currentNodeName++;
                } else {
                    if (i >= n) {
                        preFlowCost -= b.get(i) * maxC;
                    }
                    b.set(thresholdNode, b.get(thresholdNode) + b.get(i)); // add mass(i<N) or deficit (i>=N)
                }
            }
        }
        nodesNewNames.set(thresholdNode, currentNodeName);
        nodesOldNames.add(thresholdNode);
        currentNodeName++;
        nodesNewNames.set(artificialNode, currentNodeName);
        nodesOldNames.add(artificialNode);
        currentNodeName++;

        java.util.Vector<Long> bb = new java.util.Vector<Long>();
        for (int i = 0; i < currentNodeName; i++) {
            bb.add(0L);
        }
        int j = 0;
        for (int i = 0; i < b.size(); i++) {
            if (nodesNewNames.get(i) != removeNodeFlag) {
                bb.set(j, b.get(i));
                j++;
            }
        }

        java.util.Vector<java.util.List<Edge>> cc = new java.util.Vector<java.util.List<Edge>>();
        for (int i = 0; i < bb.size(); i++) {
            cc.add(new java.util.LinkedList<Edge>());
        }
        for (int i = 0; i < cWithout.size(); i++) {
            if (nodesNewNames.get(i) == removeNodeFlag) {
                continue;
            }
            for (Edge it : cWithout.get(i)) {
                if (nodesNewNames.get(it.getTo()) != removeNodeFlag) {
                    cc.get(nodesNewNames.get(i)).add(
                            new Edge(nodesNewNames.get(it.getTo()), it.getCost()));
                }
            }
        }

        MinCostFlow mcf = new MinCostFlow();

        long myDist;

        java.util.Vector<java.util.List<EdgeFlow>> flows = new java.util.Vector<>(bb.size());
        for (int i = 0; i < bb.size(); i++) {
            flows.add(new java.util.LinkedList<>());
        }

        long mcfDist = mcf.compute(bb, cc, flows);

        myDist = preFlowCost + // pre-flowing on cases where it was possible
                mcfDist + // solution of the transportation problem
                (absDiffSumPSumQ * extraMassPenalty); // emd-hat extra mass penalty

        return myDist;
    }

    private static double emdHat(java.util.Vector<Double> p, java.util.Vector<Double> q, java.util.Vector<java.util.Vector<Double>> c,
                                 double extraMassPenalty) {

        // This condition should hold:
        // ( 2^(sizeof(CONVERT_TO_T*8)) >= ( multifactor^2 )
        // Note that it can be problematic to check it because
        // of overflow problems. I simply checked it with Linux calc
        // which has arbitrary precision.
        double multifactor = 1000000;

        // Constructing the input
        int n = p.size();
        java.util.Vector<Long> iP = new java.util.Vector<Long>();
        java.util.Vector<Long> iQ = new java.util.Vector<Long>();
        java.util.Vector<java.util.Vector<Long>> iC = new java.util.Vector<java.util.Vector<Long>>();
        for (int i = 0; i < n; i++) {
            iP.add(0L);
            iQ.add(0L);
            java.util.Vector<Long> vec = new java.util.Vector<Long>();
            for (int j = 0; j < n; j++) {
                vec.add(0L);
            }
            iC.add(vec);
        }

        // Converting to CONVERT_TO_T
        double sumP = 0.0;
        double sumQ = 0.0;
        double maxC = c.get(0).get(0);
        for (int i = 0; i < n; i++) {
            sumP += p.get(i);
            sumQ += q.get(i);
            for (int j = 0; j < n; j++) {
                if (c.get(i).get(j) > maxC) {
                    maxC = c.get(i).get(j);
                }
            }
        }
        double minSum = Math.min(sumP, sumQ);
        double maxSum = Math.max(sumP, sumQ);
        double pqnormFactor = multifactor / maxSum;
        double cnormFactor = multifactor / maxC;
        for (int i = 0; i < n; i++) {
            iP.set(i, (long) (Math.floor(p.get(i) * pqnormFactor + 0.5)));
            iQ.set(i, (long) (Math.floor(q.get(i) * pqnormFactor + 0.5)));
            for (int j = 0; j < n; j++) {
                iC.get(i)
                        .set(j,
                                (long) (
                                        Math.floor(c.get(i).get(j)
                                                * cnormFactor + 0.5)));
            }
        }

        // computing distance without extra mass penalty
        double dist = emdHatImplLongLongInt(iP, iQ, iC, 0);
        // unnormalize
        dist = dist / pqnormFactor;
        dist = dist / cnormFactor;

        // adding extra mass penalty
        if (extraMassPenalty == -1) {
            extraMassPenalty = maxC;
        }
        dist += (maxSum - minSum) * extraMassPenalty;

        return dist;
    }
}