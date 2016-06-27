package com.example.ademi.galonovaversao.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.ademi.galonovaversao.Classes.CheckConnection;
import com.example.ademi.galonovaversao.Classes.Sistema;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 22/04/16.
 */
public class DaoAvisos {

    int contadorAvisos = 0;
    List<String> listAvisos;
    List<String> listAvisosTemp;
    Sistema sistema;
    String line = "";
    private boolean enable = false;

    public DaoAvisos() {

        sistema = Sistema.getInstancia();

        listAvisos = new ArrayList<>();
        listAvisosTemp = new ArrayList<>();
        listAvisos.add(sistema.getDEFAULT_MENSSAGE());

        new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO Auto-generated method stub
                while(true){

                    while (sistema.getCheckConnection().isOnline()) {

                        try {

                            new UpdateAvisos().execute(sistema.getUrlAvisos());

                            enable = false;

                            contadorAvisos = 0;

                            listAvisos = new ArrayList<>(listAvisosTemp);

                            enable = true;

                            sistema.showMessage("Avisos atualizado", "left");

                            Thread.sleep(sistema.getDEFAULT_TIME_UPDATE_AVISOS());

                        } catch (Exception e) {}
                    }

                }

            }
        }).start();

    }

    public class UpdateAvisos extends AsyncTask<URL, Void, Void>{

        @Override
        protected Void doInBackground(URL... params) {

            HttpURLConnection urlConnection = null;

            try {

                urlConnection = (HttpURLConnection) params[0].openConnection();

                int code = urlConnection.getResponseCode();

                if (code == 200) {

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    if (in != null) {

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                        while ((line = bufferedReader.readLine()) != null) {
                            listAvisosTemp.add(line);
                        }

                    }
                    in.close();
                }

            } catch (IOException e) {
                listAvisos.clear();
                listAvisos.add(sistema.getDEFAULT_MENSSAGE());
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            return null;
        }

    }

    public int getContadorAvisos() {
        return contadorAvisos;
    }

    public void setContadorAvisos(int contadorAvisos) {
        this.contadorAvisos = contadorAvisos;
    }

    public List<String> getListAvisos() {
        return listAvisos;
    }

    public void setListAvisos(List<String> listAvisos) {
        this.listAvisos = listAvisos;
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
