package com.example.ademi.galonovaversao.DAO;

import android.util.Log;

import com.example.ademi.galonovaversao.Classes.Sistema;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by root on 21/04/16.
 */
public class DaoTemperatura {

    Sistema sistema;
    private double temperatura = 0;
    private double temperaturaTemp = 0;
    private boolean enable = false;

    public DaoTemperatura() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                sistema = Sistema.getInstancia();

                while (true) {

                    if (sistema.getCheckConnection().isOnline()) {

                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder().url(sistema.getUrlJsonObsTemperatura()).build();

                        Response response = null;

                        try {

                            temperaturaTemp = 0;

                            response = client.newCall(request).execute();

                            String json = response.body().string();

                            JSONObject jsonObject = new JSONObject(json);

                            JSONObject obs1 = jsonObject.getJSONObject("main");
                            temperaturaTemp = obs1.getDouble(String.valueOf("temp"));

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        sistema.showMessage("Temperatura atualizada", "right");

                        enable = false;

                        temperatura = temperaturaTemp;

                        enable = true;

                    }

                    try { Thread.sleep(sistema.getDEFAULT_TIME_UPDATE_TEMPERATURA()); } catch (InterruptedException e) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }

                }

            }
        }).start();

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
