package com.gt.rxcode.learn.io.ui.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WSAppResponse {
    @SerializedName(JsonKeys.RESULT)
    @Expose
    private Boolean result;
    @SerializedName(JsonKeys.MESSAGE)
    @Expose
    private String message;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
