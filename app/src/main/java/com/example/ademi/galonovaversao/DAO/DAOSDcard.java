package com.example.ademi.galonovaversao.DAO;

import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.ademi.galonovaversao.Classes.Sistema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    String lineArray[];
    BufferedReader br;

    public DAOSDcard() {

        sistema = Sistema.getInstancia();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    enable = false;

                    readSdCard(sistema.getPathSdCard() + "config.txt");

                    enable = true;

                    try { Thread.sleep(sistema.getDEFAULT_TIME_READ_CONFIG_TXT()); } catch (Exception e) {}

                }
            }
        }).start();

    }

    public void readSdCard(String params) {

        file = new File(params);

        try {

            br = new BufferedReader(new FileReader(file));

            listValues = new ArrayList<>();

            while ((line = br.readLine()) != null) {

                lineArray = line.split("-");

                listValues.add(lineArray[1]);

            }

            br.close();

        } catch (FileNotFoundException e) {
            sistema.getDaoLog().SendMsgToTxt("Problema arquivo nao encontrado ao ler arquivo config");
        } catch (IOException e) {
            sistema.getDaoLog().SendMsgToTxt("Problema i/o ao ler arquivo config");
        }

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



}
