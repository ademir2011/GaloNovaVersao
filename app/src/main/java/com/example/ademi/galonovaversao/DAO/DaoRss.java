package com.example.ademi.galonovaversao.DAO;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ademi.galonovaversao.Classes.CheckConnection;
import com.example.ademi.galonovaversao.Classes.RssItem;
import com.example.ademi.galonovaversao.Classes.RssReader;
import com.example.ademi.galonovaversao.Classes.Sistema;


/**
 * Created by root on 21/04/16.
 */
public class DaoRss {

    private String rss = "";
    private String rssTemp = "";
    private boolean enable = false;
    char character;
    RssReader rssReader;
    String tempTitle;

    Sistema sistema;

    public DaoRss() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                sistema = Sistema.getInstancia();

                while(true) {

                    if (sistema.getCheckConnection().isOnline()) {

                        try {

                            rssReader = new RssReader(sistema.getUrlRssFonte());

                            rssTemp = "";

                            for (RssItem item : rssReader.getItems()){

                                tempTitle = item.getTitle();
                                tempTitle = tempTitle.replace("\"", "'");

                                character = tempTitle.charAt(0);

                                if (  Character.isUpperCase( character ) ){
                                    rssTemp += tempTitle + " - Fonte G1 - ";
                                }

                            }

                        } catch (Exception e) {
                            rssTemp = sistema.getDEFAULT_MENSSAGE();
                        }

                        enable = false;

                        rss = rssTemp;

                        enable = true;

                        sistema.showMessage("Rss atualizada", "right");

                    }

                    try { Thread.sleep(sistema.getDEFAULT_TIME_UPDATE_RSS()); } catch (Exception e) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }

                }

            }
        }).start();

    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
