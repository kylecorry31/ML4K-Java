package com.kylecorry.ml4k;

public interface OnClassificationErrorListener {

    /**
     * Handle an error.
     * @param data The data which was being processed.
     * @param error The error received in human readable format.
     */
    void gotError(String data, String error);

}
