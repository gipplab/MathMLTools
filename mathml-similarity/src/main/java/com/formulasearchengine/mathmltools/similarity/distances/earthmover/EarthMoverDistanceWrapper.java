package com.formulasearchengine.mathmltools.similarity.distances.earthmover;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides wrapping functionality for invoking the Earth Mover Distance.
 * Created by Felix Hamborg on 13.12.16.
 */
public class EarthMoverDistanceWrapper {

    private EarthMoverDistanceWrapper() { }

    public static Signature histogramToSignature(Map<String, Double> histogram) {
        Signature signature = new Signature();
        Feature[] features = new Feature[histogram.size()];
        double[] weights = new double[histogram.size()];
        List<String> orderedKeys = new ArrayList<>(histogram.keySet());

        for (int i = 0; i < histogram.size(); i++) {
            features[i] = new Feature2D(i, histogram.get(orderedKeys.get(i)));
            weights[i] = 1.0;
        }

        signature.setFeatures(features);
        signature.setWeights(weights);
        signature.setNumberOfFeatures(features.length);

        return signature;
    }
}
