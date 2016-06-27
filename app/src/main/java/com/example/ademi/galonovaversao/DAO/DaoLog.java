package com.example.ademi.galonovaversao.DAO;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ademi.galonovaversao.Classes.CheckConnection;
import com.example.ademi.galonovaversao.Classes.Sistema;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by root on 03/05/16.
 */
public class DaoLog {

    Sistema sistema;
    String msgLog;

    public DaoLog() {}

    public synchronized void SendMsgToTxt(String data) {

//        sistema = Sistema.getInstancia();
//
//        DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm");
//        String date = df.format(Calendar.getInstance().getTime());
//
//        File file = new File(sistema.getPathSdCard(), sistema.getInitLogName());
//        FileWriter fileWritter = null;
//
//        try {
//
//            fileWritter = new FileWriter(file, true);
//
//            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
//
//            msgLog = date + " - " + data + "\n";
//
//            bufferWritter.write(msgLog);
//            bufferWritter.close();
//            fileWritter.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
