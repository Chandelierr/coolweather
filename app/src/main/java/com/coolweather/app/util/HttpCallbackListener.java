package com.coolweather.app.util;

/**
 * Created by Chandelier on 2016/9/25.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
