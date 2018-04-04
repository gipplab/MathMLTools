package com.formulasearchengine.mathmltools.similarity.result;

import com.formulasearchengine.mathmltools.similarity.SubTreeComparison;
import com.formulasearchengine.mathmltools.similarity.node.MathNode;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON wrapper for matches between two math expression trees
 * from the perspective of the reference tree:
 * <p>
 * The id-attributes relates to the reference node, whereas sub-"matches" refer
 * to the comparison node. Typically we assume a 1-on-1 match relation, but it
 * is possible to match multiple occurrences inside the comparison math node.
 *
 * @author Vincent Stange
 */
public class Match {

    /* id of the node inside the comparison tree */
    private String id;

    /* depth level of the current sub-tree*/
    private int depth;

    /* coverage factor of the two sub-trees matched, from the perspective of the reference tree */
    private double coverage;

    /* list of match between the comparison math node */
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
