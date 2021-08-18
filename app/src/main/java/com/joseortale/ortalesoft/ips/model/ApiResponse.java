package com.joseortale.ortalesoft.ips.model;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("success")
    private Boolean success;

    @SerializedName("error")
    private String error;

    @SerializedName("data")
    private Object data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
