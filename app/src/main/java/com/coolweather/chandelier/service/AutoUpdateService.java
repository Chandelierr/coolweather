package com.coolweather.chandelier.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.coolweather.chandelier.receiver.AutoUpdateReceiver;
import com.coolweather.chandelier.util.HttpCallbackListener;
import com.coolweather.chandelier.util.HttpUtil;
import com.coolweather.chandelier.util.Utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Chandelier on 2016/10/10.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=prefs.getString("city_name","");
        try {
            weatherCode = URLEncoder.encode(weatherCode,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String address="http://wthrcdn.etouch.cn/weather_mini?city="+weatherCode;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
