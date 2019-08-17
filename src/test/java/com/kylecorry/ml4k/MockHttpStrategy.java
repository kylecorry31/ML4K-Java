package com.kylecorry.ml4k;

import java.io.IOException;
import java.net.URL;

public class MockHttpStrategy implements HttpStrategy {

    private APIResponse response;

    public void setResponse(APIResponse response){
        this.response = response;
    }

    @Override
    public APIResponse getJSON(URL url) throws IOException {
        return response;
    }

    @Override
    public APIResponse postJSON(URL url, String data) throws IOException {
        return response;
    }
}
