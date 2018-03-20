package com.formulasearchengine.mathmltools.similarity.distances;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import com.formulasearchengine.mathmltools.utils.mml.ValidCSymbols;
import com.formulasearchengine.mathmltools.similarity.distances.earthmover.EarthMoverDistanceWrapper;
import com.formulasearchengine.mathmltools.similarity.distances.earthmover.JFastEMD;
import com.formulasearchengine.mathmltools.similarity.distances.earthmover.Signature;
import com.formulasearchengine.mathmltools.helper.XMLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Felix Hamborg <felixhamborg@gmail.com> on 05.12.16.
 */
public class Distances {

    private static final Logger LOG = LogManager.getLogger(Distances.class.getName());

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");

    private Distances() {
    }

    /**
     * probably only makes sense to compute this on CI
     *
     * @param h1
     * @param h2
     * @return
     */
    public static double computeEarthMoverAbsoluteDistance(Map<String, Double> h1, Map<String, Double> h2) {
        Signature s1 = EarthMoverDistanceWrapper.histogramToSignature(h1);
        Signature s2 = EarthMoverDistanceWrapper.histogramToSignature(h2);

        return JFastEMD.distance(s1, s2, 0.0);
    }

    public static double computeRelativeDistance(Map<String, Double> h1, Map<String, Double> h2) {
        int totalNumberOfElements = 0;
        for (Double frequency : h1.values()) {
            totalNumberOfElements += frequency;
        }
        for (Double frequency : h2.values()) {
            totalNumberOfElements += frequency;
        }
        if (totalNumberOfElements == 0) {
            return 0.0;
        }

        final double absoluteDistance = computeAbsoluteDistance(h1, h2);

        return absoluteDistance / totalNumberOfElements;
    }


    /**
     * compares two histograms and returns the accumulated number of differences (absolute)
     *
     * @param h1
     * @param h2
     * @return
     */
    public static double computeAbsoluteDistance(Map<String, Double> h1, Map<String, Double> h2) {
        double distance = 0;

        final Set<String> keySet = new HashSet();
        keySet.addAll(h1.keySet());
        keySet.addAll(h2.keySet());

        for (String key : keySet) {
            double v1 = 0.0;
            double v2 = 0.0;
            if (h1.get(key) != null) {
                v1 = h1.get(key);
            }
            if (h2.get(key) != null) {
                v2 = h2.get(key);
            }

            distance += Math.abs(v1 - v2);
        }

        return distance;
    }

    /**
     * Returns a map of the names and their accumulated frequency of the given content-elements (that could be identifiers, numbers, or operators)
     *
     * @param nodes
     * @return
     */
    protected static HashMap<String, Double> contentElementsToHistogram(NodeList nodes) {
        final HashMap<String, Double> histogram = new HashMap<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String contentElementName = node.getTextContent().trim();
            // increment frequency by 1
            histogram.put(contentElementName, histogram.getOrDefault(contentElementName, 0.0) + 1.0);
        }

        return histogram;
    }

    /**
     * Adds all elements from all histogram
     *
     * @return
     */
    public static Map<String, Double> histogramsPlus(List<Map<String, Double>> histograms) {
        return histogramsPlus(histograms.toArray(new HashMap[histograms.size()]));
    }

    /**
     * Adds all elements from all histogram
     *
     * @param histograms
     *
     * @return .
     */
    @SafeVarargs
    public static Map<String, Double> histogramsPlus(Map<String, Double>... histograms) {
        switch (histograms.length) {
            case 0:
                throw new IllegalArgumentException("histograms.length=" + histograms.length + "; needs to be >= 2");
                // return null;
            case 1:
                return histograms[0];
            default:
        }

        final Set<String> mergedKeys = new HashSet<>();
        for (Map<String, Double> histogram : histograms) {
            mergedKeys.addAll(histogram.keySet());
        }
        final HashMap<String, Double> mergedHistogram = new HashMap<>();

        for (String key : mergedKeys) {
            double value = 0.0;
            for (Map<String, Double> histogram : histograms) {
                value += histogram.getOrDefault(key, 0.0);
            }
            mergedHistogram.put(key, value);
        }

        return mergedHistogram;
    }

    /**
     * converts strict content math ml to a histogram for the given tagname, e.g., ci
     *
     * @param strictCmml
     * @param tagName
     * @return
     */
    private static HashMap<String, Double> strictCmmlInfoToHistogram(CMMLInfo strictCmml, String tagName) {
        final NodeList elements = strictCmml.getElementsByTagName(tagName);
        return contentElementsToHistogram(elements);
    }


    /**
     * converts content math ml to a histogram for the given tagname, e.g., cn
     *
     * @param node
     * @param tagName
     * @return
     */
    private static HashMap<String, Double> cmmlNodeToHistrogram(Node node, String tagName) throws XPathExpressionException {
        final NodeList elements = XMLHelper.getElementsB(node, "*//*:" + tagName);
        return contentElementsToHistogram(elements);
    }


    /**
     * this cleanup is necessary due to error in the xslt conversion script (contentmathmml to strict cmml)
     *
     * @param tagName
     * @param histogram
     */
    private static void cleanupHistogram(String tagName, Map<String, Double> histogram) {
        switch (tagName) {
            case "csymbol":
                for (String key : ValidCSymbols.VALID_CSYMBOLS) {
                    histogram.remove(key);
                }
                break;
            case "ci":
                histogram.remove("integer");
                break;
            case "cn":
                Set<String> toberemovedKeys = new HashSet<>();
                for (String key : histogram.keySet()) {
                    if (!isNumeric(key)) {
                        toberemovedKeys.add(key);
                    }
                }
                // now we can remove the keys
                for (String key : toberemovedKeys) {
                    histogram.remove(key);
                }
                break;
            default:
        }
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
