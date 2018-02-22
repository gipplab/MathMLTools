package com.formulasearchengine.mathmlsim.similarity.result;

import com.formulasearchengine.mathmlsim.similarity.SubTreeComparison;
import com.formulasearchengine.mathmlsim.similarity.node.MathNode;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON wrapper for matches between two math expression trees.
 * The Id relates to the reference MEXT, where submatches refer
 * to the comparison MET.
 *
 * @author Vincent Stange
 */
public class Match {

    private String id = "";

    private int depth = 0;

    private double coverage = 0;

    private List<SubMatch> matches = new ArrayList<>();

    /**
     * Create a match. A match must always contain sub-matches.
     * The match will refer to the part of the reference tree, whereas the sub-match will
     * refer to the part of the comparison tree.
     *
     * @param refTree  partial reference tree (or full tree)
     * @param compTree partial comparison tree (or full tree)
     * @param type     type of similarity (identical or similar comparison)
     */
    public Match(MathNode refTree, MathNode compTree, SimilarityType type) {
        this.id = refTree.getId();
        this.depth = refTree.getDepth();
        this.coverage = SubTreeComparison.getCoverage(refTree.getLeafs(), compTree.getLeafs());
        // initialize with the first match
        addMatch(new SubMatch(refTree, compTree, type));
    }

    public SubMatch addMatch(SubMatch subMatch) {
        this.matches.add(subMatch);
        return subMatch;
    }

    public String getId() {
        return id;
    }

    public int getDepth() {
        return depth;
    }

    public double getCoverage() {
        return coverage;
    }

    public List<SubMatch> getMatches() {
        return matches;
    }
}
