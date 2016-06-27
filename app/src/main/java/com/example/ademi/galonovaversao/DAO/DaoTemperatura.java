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

/**
 * Created by root on 21/04/16.
 */
public class DaoTemperatura {

    Sistema sistema;
    private double temperatura = 0;
    private boolean enable = false;

    public DaoTemperatura() {

        sistema = Sistema.getInstancia();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    while(sistema.getCheckConnection().isOnline()){

                        enable = false;

                        new UpdateTemperatura().execute(sistema.getUrlJsonObsTemperatura());

                        sistema.showMessage("Temperatura atualizada", "right");

                        enable = true;

                        try { Thread.sleep(sistema.getDEFAULT_UPDATE_TEMPERATURA()); } catch (InterruptedException e) {}

                    }

                }

            }
        }).start();

    }

    public class UpdateTemperatura extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    params[0], null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        JSONObject obs1 = response.getJSONObject("main");
                        temperatura = obs1.getDouble(String.valueOf("temp"));

                    } catch (JSONException e) {
                        temperatura = 25;
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    sistema.getDaoLog().SendMsgToTxt("Problema ao atualizar temperatura");
                    temperatura = 25;
                }

            });

            sistema.getRequestQueue().add(jsonObjectRequest);

            return null;
        }

    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
