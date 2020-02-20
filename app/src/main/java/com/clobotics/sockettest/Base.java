package com.clobotics.sockettest;

import java.util.UUID;

/**
 * Author: Aya
 * Date: 2020/2/10
 * Description:
 */
public class Base {
    private int requestId;
    private String guid = UUID.randomUUID().toString();
    private String action;
    private boolean success;
    private int error;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
