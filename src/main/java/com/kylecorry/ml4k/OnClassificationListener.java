package com.kylecorry.ml4k;

public interface OnClassificationListener {

    /**
     * Handle a finished classification.
     * @param classification The classification received.
     */
    void gotClassification(Classification classification);

}
