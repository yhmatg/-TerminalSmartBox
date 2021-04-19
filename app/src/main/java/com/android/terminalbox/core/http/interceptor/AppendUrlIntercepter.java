package com.android.terminalbox.core.http.interceptor;

import android.util.Log;
import com.android.terminalbox.core.DataManager;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AppendUrlIntercepter implements Interceptor {
    private String baseUrl;
    public AppendUrlIntercepter(String baseUrl){
        this.baseUrl=baseUrl;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = DataManager.getInstance().getToken();
        String cacheHost= DataManager.getInstance().getHostUrl();
        Request oldRequest = chain.request();
        HttpUrl.Builder builder = oldRequest
                .url()
                .newBuilder();
        //add 20190729 start
        builder.addQueryParameter("token",token);
        builder.addQueryParameter("rnd",String.valueOf(System.currentTimeMillis()));
        //add 20190729 start
        Log.e("AppendUrlIntercepter","builder.build()===" + builder.build());
        Request newRequest = oldRequest
                .newBuilder()
                //.addHeader("Authorization",token)
                .url(builder.build())
                .build();
        return chain.proceed(newRequest);
    }
}