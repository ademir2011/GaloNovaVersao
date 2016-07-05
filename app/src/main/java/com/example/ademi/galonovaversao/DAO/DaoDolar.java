package com.example.ademi.galonovaversao.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ademi.galonovaversao.Classes.CheckConnection;
import com.example.ademi.galonovaversao.Classes.Sistema;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * Created by root on 21/04/16.
 */
public class DaoDolar {

    Sistema sistema;
    private String dolar = "00.00";
    private String dolarTemp = "00.00";
    private boolean enable = false;

    public DaoDolar() {

        sistema = Sistema.getInstancia();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    while(sistema.getCheckConnection().isOnline()){

                        OkHttpClient client = new OkHttpClient();

                        okhttp3.Request request = new okhttp3.Request.Builder().url(sistema.getUrlJsonObjDolar()).build();

                        okhttp3.Response response = null;

                        try {

                            response = client.newCall(request).execute();

                            String json = response.body().string();

                            JSONObject jsonObject = new JSONObject(json);

                            JSONObject obs1 = jsonObject.getJSONObject("valores");
                            JSONObject obs2 = obs1.getJSONObject("USD");
                            dolarTemp = obs2.getString(String.valueOf("valor"));

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        enable = false;

                        dolar = dolarTemp;

                        enable = true;

                        sistema.showMessage("Dolar atualizada", "bottom");

                        try { Thread.sleep(sistema.getDEFAULT_TIME_SHOW_DOLAR()); } catch (InterruptedException e) {}

                    }

                }

            }
        }).start();

    }

    public String getDolar() {
        return dolar;
    }

    public void setDolar(final String dolar) {
        this.dolar = dolar;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
