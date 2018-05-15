package cn.edu.gdmec.android.mvp.utils;

import android.app.DownloadManager;
import android.service.carrier.CarrierMessagingService;
import android.telecom.Call;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

import cn.edu.gdmec.android.mvp.WeatherBean;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static java.util.Calendar.getInstance;

/**
 * Created by apple on 18/5/15.
 */

public class OkHttpUtils {
    String res = null;
    private static OkHttpUtils okHttpUtils;
    private  synchronized static OkHttpUtils getInstance(){
        if (okHttpUtils == null){
            okHttpUtils = new OkHttpUtils();
        }
        return okHttpUtils;
    }
    public static void getResultCallback(String url, CarrierMessagingService.ResultCallback resultCallback){
        getInstance().sendRequest(url, resultCallback);
    }
    public void sendRequest(String url, final CarrierMessagingService.ResultCallback resultCallback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .build();
        final DownloadManager.Request request = new DownloadManager.Request.builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e){
                if (resultCallback != null){
                    resultCallback.onFailure(e);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException{
                res = response.body().string();
                Log.i("res", res);
                WeatherBean bean = JsonUtils.getWeather(res);
                if (resultCallback != null){
                    resultCallback.getWeather(bean);
                }
            }
        });
    }

    public interface Resultcallback{
        void getWeather(WeatherBean weatherBean);
        void onFailure(Exception e);
    }
}
