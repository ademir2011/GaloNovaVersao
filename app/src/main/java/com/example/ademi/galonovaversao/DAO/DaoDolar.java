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
public class DaoDolar {

    Sistema sistema;
    private String dolar = "00.00";
    private boolean enable = false;

    public DaoDolar() {

        sistema = Sistema.getInstancia();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    while(sistema.getCheckConnection().isOnline()){

                        enable = false;

                        new Update().execute(sistema.getUrlJsonObjDolar());

                        enable = true;

                        sistema.showMessage("Dolar atualizada", "bottom");

                        try { Thread.sleep(sistema.getDEFAULT_UPDATE_DOLAR()); } catch (InterruptedException e) {}

                    }

                }

            }
        }).start();

    }

    public class Update extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    params[0], null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        JSONObject obs1 = response.getJSONObject("valores");
                        JSONObject obs2 = obs1.getJSONObject("USD");
                        dolar = obs2.getString(String.valueOf("valor"));

                    } catch (JSONException e) {}

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    sistema.getDaoLog().SendMsgToTxt("problema ao atualizar dolar");
                    dolar = "0";
                }

            });

            sistema.getRequestQueue().add(jsonObjectRequest);

            return null;

        }

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
