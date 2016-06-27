package com.example.ademi.galonovaversao.DAO;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ademi.galonovaversao.Classes.CheckConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 01/05/16.
 */
public class DaoEstatistica {

    RequestQueue requestQueueEstatistica;
    CheckConnection checkConnection;
    String PEP_ID;
    String urlLog = "http://galomidiaavancada.com.br/gestao/sistemaAdministrativo/sendLog.php";

    public DaoEstatistica(String PEP_ID, RequestQueue requestQueue, final String pathSdCard, Context context) {

        this.PEP_ID = PEP_ID;
        requestQueueEstatistica = requestQueue;
        checkConnection = new CheckConnection(context);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){

                    while (checkConnection.isOnline()) {

                        try {

                            sendLog(pathSdCard);

                            Thread.sleep(10 * 60 * 1000);

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                }

            }
        }).start();

    }

    String data = "";

    public void sendLog(String pathSdCard){

        File file = new File( pathSdCard );

        String line;

        data = "";

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));

            while((line = br.readLine()) != null){

                data += line;

            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("data2",data);

        StringRequest request = new StringRequest(Request.Method.POST, urlLog, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("idpep", PEP_ID);
                parameters.put("log", data);

                return parameters;
            }
        };

        requestQueueEstatistica.add(request);

    }

}
