package com.formulasearchengine.mathmltools.similarity.similarity.result;

import com.formulasearchengine.mathmltools.similarity.similarity.SubTreeComparison;
import com.formulasearchengine.mathmltools.similarity.similarity.node.MathNode;

/**
 * JSON wrapper for a single match between two math expression trees.
 * The Id relates to the comparison MEXT.
 *
 * @author Vincent Stange
 */
public class SubMatch {

    private String id = "";

    private double assessment = 1.0;

    private int depth = 0;

    private double coverage = 0;

    private String type = "similar";

    /**
     * Create a sub-match. A sub-match is always part of a match.
     * The match will refer to the part of the reference tree, whereas the sub-match will
     * refer to the part of the comparison tree.
     *
     * @param refTree  partial reference tree (or full tree)
     * @param compTree partial comparison tree (or full tree)
     * @param type     type of similarity (identical or similar comparison)
     */
    public SubMatch(MathNode refTree, MathNode compTree, SimilarityType type) {
        this.id = compTree.getId();
        this.depth = compTree.getDepth();
        this.type = type.name();
        this.coverage = SubTreeComparison.getCoverage(compTree.getLeafs(), refTree.getLeafs());
        this.assessment = computeAssessment(refTree, compTree);
    }

    /**
     * This method is still in testing.
     * <br/>
     * This method assesses the similarity value between two partial trees.
     * The measurement consists of a product from their depth and coverage distance.
     *
     * @param refTree  partial reference tree (or full tree)
     * @param compTree partial comparison tree (or full tree)
     * @return value between 0 to 1, 1 is a full-match
     */
    double computeAssessment(MathNode refTree, MathNode compTree) {
        int absDiff = Math.abs(refTree.getDepth() - compTree.getDepth());
        int maxDepth = compTree.getMaxDepth() + 1;
        double depthWeight = 1 - (double) absDiff / (double) maxDepth;
        return depthWeight * coverage;
    }

    public String getId() {
        return id;
    }

    public double getAssessment() {
        return assessment;
    }

    public int getDepth() {
        return depth;
    }

    public double getCoverage() {
        return coverage;
    }

    public String getType() {
        return type;
    }
}
