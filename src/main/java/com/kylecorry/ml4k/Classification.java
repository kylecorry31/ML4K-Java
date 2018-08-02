package com.kylecorry.ml4k;

import com.google.gson.*;

import java.util.Objects;

public class Classification {
    private String data;
    private String classification;
    private double confidence;

    private Classification(String data, String classification, double confidence) {
        this.data = data;
        this.classification = classification;
        this.confidence = confidence;
    }

    public static Classification fromJson(String data, String json) throws JsonParseException {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject value = jsonArray.get(0).getAsJsonObject();

        final String className = value.get("class_name").getAsString();
        final double confidence = value.get("confidence").getAsDouble();
        return new Classification(data, className, confidence);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "Classification{" +
                "data='" + data + '\'' +
                ", classification='" + classification + '\'' +
                ", confidence=" + confidence +
                '}';
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
