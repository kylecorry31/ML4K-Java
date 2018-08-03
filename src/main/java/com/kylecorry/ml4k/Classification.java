package com.kylecorry.ml4k;

import com.google.gson.*;

import java.util.Objects;

public class Classification {
    private String data;
    private String classification;
    private double confidence;

    /**
     * Create a classification.
     *
     * @param data           The data that was classified.
     * @param classification The predicted class.
     * @param confidence     The confidence of the prediction (out of 100).
     */
    private Classification(String data, String classification, double confidence) {
        this.data = data;
        this.classification = classification;
        this.confidence = confidence;
    }

    /**
     * Create a classification from JSON.
     *
     * @param data The data that was classified.
     * @param json The JSON response from ML4K.
     * @return The classification.
     * @throws JsonParseException Thrown when the server returns malformed JSON.
     */
    static Classification fromJson(String data, String json) throws JsonParseException {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject value = jsonArray.get(0).getAsJsonObject();

        final String className = value.get("class_name").getAsString();
        final double confidence = value.get("confidence").getAsDouble();
        return new Classification(data, className, confidence);
    }

    /**
     * Get the data that was classified.
     *
     * @return The data classified.
     */
    public String getData() {
        return data;
    }

    /**
     * Get the classification of the data.
     *
     * @return The predicted classification.
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Get the confidence of the prediction.
     *
     * @return The confidence of the prediction [0-100].
     */
    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return String.format("'%s': classified as '%s' with %f%% confidence.", data, classification, confidence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classification that = (Classification) o;
        return Double.compare(that.confidence, confidence) == 0 &&
                Objects.equals(data, that.data) &&
                Objects.equals(classification, that.classification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, classification, confidence);
    }
}
