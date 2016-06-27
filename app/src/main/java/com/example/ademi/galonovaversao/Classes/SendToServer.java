package com.example.ademi.galonovaversao.Classes;

import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ademi on 20/06/2016.
 */
public class SendToServer extends AsyncTask<String, Void, Void>{

    Sistema sistema;

    public SendToServer() {
        sistema = Sistema.getInstancia();
    }

    @Override
    protected Void doInBackground(final String... strings) {

        Handler handler =  new Handler(sistema.getContext().getMainLooper());
        handler.post( new Runnable(){
            public void run(){
                Toast.makeText(sistema.getContext(), sistema.getString64().substring(0,20), Toast.LENGTH_LONG).show();
            }
        });

        StringRequest request = new StringRequest(Request.Method.POST, "http://galomidiaavancada.com.br/gestao/push_notification.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters  = new HashMap<String, String>();
                parameters.put("idpep", sistema.getPEP_ID());
                parameters.put("string64", sistema.getString64());

                return parameters;
            }
        };

        sistema.getRequestQueue().add(request);


        return null;
    }

}
