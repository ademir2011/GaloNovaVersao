package com.example.ademi.galonovaversao.DAO;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.ademi.galonovaversao.Activities.MainActivity;
import com.example.ademi.galonovaversao.Classes.DefaultExceptionHandler;
import com.example.ademi.galonovaversao.Classes.Sistema;
import com.example.ademi.galonovaversao.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 28/04/16.
 */
public class DAOSDcard {

    Sistema sistema;
    private boolean enable = false;
    List<String> listValues;
    File file;
    String line;
    BufferedReader br;
    List<Uri> listFiles;

    public DAOSDcard() {

        sistema = Sistema.getInstancia();

        new Thread(new Runnable() {
            @Override
            public void run() {

                enable = false;

                file = new File(sistema.getPathSdCard() + "config.txt");

                try {

                    br = new BufferedReader(new FileReader(file));
                    listFiles = new ArrayList<>();

                    listValues = new ArrayList<>();

                    while ((line = br.readLine()) != null) {

                        listValues.add(line);
                        Uri uri = Uri.parse( sistema.getPathSdCard()+"assets/"+line );
                        listFiles.add( uri );

                    }

                    Resources res = sistema.getContext().getResources();
                    AssetManager am = res.getAssets();
                    //Log.e(">>", );
                    String fileList[] = am.list("midia");

                    br.close();

                } catch (FileNotFoundException e) {
                    Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(sistema.getContext(), MainActivity.class));
                    e.printStackTrace();
                } catch (IOException e) {

                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException ex) {
                        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(sistema.getContext(), MainActivity.class));
                        ex.printStackTrace();
                    }
                }

                enable = true;

            }
        }).start();

    }

    public List<String> getListValues() {
        return listValues;
    }

    public void setListValues(List<String> listValues) {
        this.listValues = listValues;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<Uri> getListFiles() {
        return listFiles;
    }

    public void setListFiles(List<Uri> listFiles) {
        this.listFiles = listFiles;
    }
}
