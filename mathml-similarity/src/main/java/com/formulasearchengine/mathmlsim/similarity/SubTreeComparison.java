package com.formulasearchengine.mathmlsim.similarity;

import com.formulasearchengine.mathmlsim.similarity.node.MathNode;
import com.formulasearchengine.mathmlsim.similarity.result.Match;
import com.formulasearchengine.mathmlsim.similarity.result.SimilarityType;
import com.google.common.collect.HashMultiset;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simplistic approach to find similar subtrees between two math expression trees.
 * <br/>
 * The algorithm will use {@link MathNode}s.
 *
 * @author Vincent Stange
 */
public class SubTreeComparison {

    private final SimilarityType type;

    /**
     * Constructor with {@link SimilarityType} declaration.
     *
     * @param type specifies the output declaration of matches, but has otherwise no effect on the comparison.
     */
    public SubTreeComparison(SimilarityType type) {
        this.type = type;
    }

    /**
     * Get a list of similarities between the reference and comparison tree.
     *
     * @param refTree       Reference MathNode tree
     * @param compTree      Comparison MathNode tree
     * @param onlyOperators find similarities only between operations, leafs are not checked
     * @return list of similarities, list can be empty but never null
     */
    @NotNull
    public List<Match> getSimilarities(MathNode refTree, MathNode compTree, boolean onlyOperators) {
        List<Match> similarities = new ArrayList<>();
        findSimilarities(refTree, compTree, similarities, false, onlyOperators);
        return similarities;
    }

    /**
     * Recursive method that goes along every node of the reference tree and tries to find
     * identical subtree with the comparison tree.
     *
     * @param refTree       Reference MathNode tree
     * @param comTree       Comparison MathNode tree
     * @param similarities  List of similarities, will be filled during process.
     * @param holdRefTree   Hold the reference tree in position and only iterate over the comparison tree
     * @param onlyOperators Find similarities only between operations, no single identifier (end leafs) are checked
     * @return true - if the current aTree ad bTree are identical subtrees, false otherwise
     */
    boolean findSimilarities(MathNode refTree, MathNode comTree, List<Match> similarities, boolean holdRefTree, boolean onlyOperators) {
        if (isIdenticalTree(refTree, comTree)) {
            // hit!
            comTree.setMarked();
            similarities.add(new Match(refTree, comTree, type));
            return true;
        }
        // iterate the comparison tree over the current node from the ref tree
        for (MathNode compChild : comTree.getChildren()) {
            // don't look at leafs if it is already marked
            // or we only want to compare branching nodes
            if (compChild.isMarked() || onlyOperators && compChild.isLeaf()) {
                continue;
            }
            // go deeper in the comp. tree but hold the ref tree
            if (findSimilarities(refTree, compChild, similarities, true, onlyOperators)) {
                return true;
            }
        }

        if (!holdRefTree) {
            // go deeper in the reference tree
            for (MathNode refChild : refTree.getChildren()) {
                if (onlyOperators && refChild.isLeaf()) {
                    continue;
                }
                findSimilarities(refChild, comTree, similarities, false, onlyOperators);
            }
        }
        return false;
    }

    /**
     * Are aTree and bTree identical subtrees? If the root node is equal,
     * all subsequent children will be compared.
     *
     * @param aTree first MathNode tree
     * @param bTree second MathNode tree
     * @return true - if both trees are identical subtrees, false otherwise
     */
    boolean isIdenticalTree(MathNode aTree, MathNode bTree) {
        // first check if they have the same number of children
        if (aTree.equals(bTree) && aTree.getChildren().size() == bTree.getChildren().size()) {
            if (aTree.isOrderSensitive()) {
                // all children order sensitive
                for (int i = 0; i < aTree.getChildren().size(); i++) {
                    if (!isIdenticalTree(aTree.getChildren().get(i), bTree.getChildren().get(i))) {
                        return false;
                    }
                }
            } else {
                // order insensitive
                List<MathNode> bChildren = new ArrayList<>(bTree.getChildren());
                OUTER:
                for (MathNode aChild : aTree.getChildren()) {
                    for (MathNode bChild : filterSameChildren(aChild, bChildren)) {
                        if (isIdenticalTree(aChild, bChild)) {
                            // found an identical child
                            bChildren.remove(bChild);
                            continue OUTER;
                        }
                    }
                    // aChild is missing in bChildren
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Filter for similar nodes from a list. Only the node itself will be compared.
     * We will not look at their respective children.
     * <br/>
     * This method is used when we want to compare children from different nodes
     * out of order.
     *
     * @param searchNode node we want to find.
     * @param list       list we want to search.
     * @return new list with the same node we search for.
     */
    List<MathNode> filterSameChildren(MathNode searchNode, List<MathNode> list) {
        return list.stream().filter(searchNode::equals).collect(Collectors.toList());
    }

    /**
     * Calculate the coverage factor between two trees, whereas only their leafs
     * are considered. Leafs are typically identifiers or constants.
     *
     * @param refLeafs  all leafs from the partial (or full) reference tree
     * @param compLeafs all leafs from the partial (or full) comparison tree
     * @return coverage factor between 0 to 1, 1 is a full-match
     */
    public static double getCoverage(List<MathNode> refLeafs, List<MathNode> compLeafs) {
        if (compLeafs.size() == 0) {
            return 1.;
        }
        HashMultiset<MathNode> tmp = HashMultiset.create();
        tmp.addAll(compLeafs);
        tmp.removeAll(refLeafs);
        return 1 - (double) tmp.size() / (double) compLeafs.size();
    }
}
