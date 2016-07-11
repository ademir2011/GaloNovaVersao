package com.example.ademi.galonovaversao.DAO;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.ademi.galonovaversao.Classes.Sistema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    List<File> listFiles;

    public DAOSDcard() {

        sistema = Sistema.getInstancia();

        new Thread(new Runnable() {
            @Override
            public void run() {

                enable = false;

                File downloadDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).getAbsolutePath());

                getFilesFromDir(downloadDir);

                enable = true;

            }
        }).start();

    }

    public void getFilesFromDir(File filesFromSD) {

        listFiles = new ArrayList<>();

        File listAllFiles[] = filesFromSD.listFiles();

        if (listAllFiles != null && listAllFiles.length > 0) {
            for (File currentFile : listAllFiles) {
                if (currentFile.isDirectory()) {
                    getFilesFromDir(currentFile);
                } else {
                    if (currentFile.getName().endsWith("")) {

                        if(currentFile.getName().substring(0,3).equals("pep")){
                            listFiles.add( currentFile );
                            Log.e("File path", currentFile.getName());
                        } else if ( currentFile.getName().equals("config.txt") ) {
                            lerTxt(currentFile);
                            Log.e("config", currentFile.getAbsolutePath() + " - " + currentFile.getName());
                        }

                    }
                }
            }
        }
    }

    private void lerTxt(File file) {

        try {

            br = new BufferedReader(new FileReader(file));

            listValues = new ArrayList<>();

            while ((line = br.readLine()) != null) {

                listValues.add(line);

            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (IOException e) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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

    public List<File> getListFiles() {
        return listFiles;
    }

    public void setListFiles(List<File> listFiles) {
        this.listFiles = listFiles;
    }
}
