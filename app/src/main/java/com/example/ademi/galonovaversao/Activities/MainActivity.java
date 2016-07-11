package com.example.ademi.galonovaversao.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.ademi.galonovaversao.Classes.Sistema;
import com.example.ademi.galonovaversao.DAO.DAOSDcard;
import com.example.ademi.galonovaversao.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tvAvisosMain) TextView tvAvisosMain;
    @Bind(R.id.ivPropagandaMain) ImageView ivPropagandaMain;
    @Bind(R.id.tvCotacaoDolarMain) TextView tvCotacaoDolarMain;
    @Bind(R.id.tvCloudMain) TextView tvCloudMain;
    @Bind(R.id.tvTimeMain) TextView tvTimeMain;
    @Bind(R.id.pbLoading) ProgressBar pbLoading;
    @Bind(R.id.vvPropagandaMain) VideoView vvPropagandaMain;
    @Bind(R.id.tvRssBottomMain) TextView tvRssBottomMain;

    Sistema sistema;
    private DAOSDcard daosDcard;
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
        wakeLock.acquire();

        //                        sistema.getDaoLog().SendMsgToTxt(" Dispositivo com internet ");

        tvRssBottomMain.setMovementMethod(new ScrollingMovementMethod());
        tvRssBottomMain.setSelected(true);

        sistema = Sistema.getInstancia(this);

        sistema.fullscreen(getWindow().getDecorView());

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    int count = 0;

                    while( (count*1000) < sistema.getDEFAULT_TIME_INIT() ){

                        sistema.showMessage(String.valueOf(count++),"top");

                        count++;

                        Thread.sleep(1000);

                    }

                } catch (InterruptedException e) {
                }

                //-------------- ATUALIZA E EXIBE PROPAGANDAS
                daosDcard = new DAOSDcard();
                showPropagandas();

                boolean close = false;

                while(!close){

                    if(sistema.getCheckConnection().isOnline()){

                        sistema.configurarHora();

                        sistema.inicializarAtualizacoes();

                        try {
                            executaAtualizacoes();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sistema.showMessage("Inicialização finalizada", "right");

                        close = true;

                    }

                }

            }
        }).start();

    }



    boolean showPb = false;
    public String value;
    int hora;
    int segundos;
    int cont = 0;
    List<String> listValueMain;
    private int secondTemp = -1;
    File fileSDcard;
    String formato;

    private void showPropagandas() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                listValueMain = new ArrayList<>();

                while(true){

                    if(daosDcard.isEnable()) {

                        if (!showPb) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pbLoading.setVisibility(View.INVISIBLE);
                                }
                            });
                            showPb = true;
                            listValueMain = daosDcard.getListValues();
                        }

                        hora        = sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY);
                        segundos    = sistema.getCalendar().get(sistema.getCalendar().SECOND);

                        if (secondTemp != segundos &&  ( segundos%15==0 ) ) {
//                        if (secondTemp != segundos &&  ( segundos%5==0 ) ) {
//                        if (secondTemp != segundos &&  ( segundos%30==0 ) ) {
                            secondTemp = segundos;

                            if (hora >= 6 && hora <= 7) {

                                if (cont < 0 || cont > 2) {
                                    cont = 0;
                                }

                                value = listValueMain.get(cont++);

                            } else if (hora >= 8 && hora <= 10) {

                                if (cont < 3 || cont > 6) {
                                    cont = 3;
                                }

                                value = listValueMain.get(cont++);

                            } else if (hora >= 11 && hora <= 13) {

                                if (cont < 7 || cont > 12) {
                                    cont = 7;
                                }

                                value = listValueMain.get(cont++);

                            } else if (hora >= 14 && hora <= 16) {

                                if (cont < 13 || cont > 16) {
                                    cont = 13;
                                }

                                value = listValueMain.get(cont++);

                            } else if (hora >= 17 && hora <= 20) {

                                if (cont < 17 || cont > 22) {
                                    cont = 17;
                                }

                                value = listValueMain.get(cont++);

                            } else if ((hora >= 21 && hora <= 23) || (hora >= 0 && hora <= 5)) {

                                if (cont < 23 || cont > 34) {
                                    cont = 23;
                                }

                                value = listValueMain.get(cont++);

                            }

                            Log.e("PROPAGANDA", "EXIBIDA - " + value + " - seconds: " + segundos + " count: " + (cont - 1));

                            sistema.getmHandlerPropaganda().post(new Runnable() {
                                @Override
                                public void run() {

                                    fileSDcard = new File(sistema.getPathSdCard() + "assets/" + value);

                                    formato = value.substring(value.lastIndexOf(".") + 1);

                                    if (formato.equals("mp4")) {

                                        try {

                                            ivPropagandaMain.setVisibility(View.GONE);
                                            vvPropagandaMain.setVisibility(View.VISIBLE);

                                            if (fileSDcard.getAbsolutePath().equals("") || fileSDcard.getAbsolutePath() == null) {
                                                vvPropagandaMain.setVisibility(View.GONE);
                                                ivPropagandaMain.setVisibility(View.VISIBLE);
                                                ivPropagandaMain.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pep003));
                                            }

                                            vvPropagandaMain.setVideoPath(returnPath(value));

                                            vvPropagandaMain.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    vvPropagandaMain.start();
                                                }
                                            });

                                            vvPropagandaMain.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                                @Override
                                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                                    //vvPropagandaMain.stopPlayback();
                                                    vvPropagandaMain.setVisibility(View.GONE);
                                                    ivPropagandaMain.setVisibility(View.VISIBLE);
                                                    ivPropagandaMain.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pep003));
                                                    return true;
                                                }
                                            });

                                        } catch (Exception e) {

                                            android.os.Process.killProcess(android.os.Process.myPid());
                                            System.exit(0);

                                        }

                                    } else {

                                        try {

                                            vvPropagandaMain.setVisibility(View.GONE);
                                            ivPropagandaMain.setVisibility(View.VISIBLE);

//                                            Bitmap bm = decodeSampledBitmapFromResource(returnPath(value), 1870, 824);
//                                            ivPropagandaMain.setImageBitmap(bm);

                                            showImage(returnPath(value));

                                        } catch (Exception e) {

                                            android.os.Process.killProcess(android.os.Process.myPid());
                                            System.exit(0);

                                        }

                                    }

                                }

                            });

                        } else {
                            secondTemp = segundos;
                        }
                    }

                    try {
                        Thread.sleep(sistema.getDEFAULT_TIME_SHOW_PROPAGANDA());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }

    private void showImage(String path)   {

        Bitmap bm = null;

        Log.i("showImage","loading:"+path);
        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inTempStorage=new byte[32 * 1024];


        File file=new File(path);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }

        try {
            if(fs!=null) bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        //bm=BitmapFactory.decodeFile(path, bfOptions); This one causes error: java.lang.OutOfMemoryError: bitmap size exceeds VM budget

        ivPropagandaMain.setImageBitmap(bm);
        //bm.recycle();
        bm=null;

    }

    public String returnPath(String value){
        for (File file : daosDcard.getListFiles()) {
            if(file.getName().equals(value)){
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String strPath, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(strPath, options);

    }

    private void showAvisos() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    sistema.getmHandlerAvisos().post(new Runnable() {
                        @Override
                        public void run() {
                            if (sistema.getDaoAvisos().isEnable()) {

                                try {

                                    tvAvisosMain.setText(sistema.getDaoAvisos().getListAvisos().get(sistema.getDaoAvisos().getContadorAvisos()));

                                    if (sistema.getDaoAvisos().getContadorAvisos() < sistema.getDaoAvisos().getListAvisos().size() - 1) {
                                        sistema.getDaoAvisos().setContadorAvisos(sistema.getDaoAvisos().getContadorAvisos() + 1);
                                    } else {
                                        sistema.getDaoAvisos().setContadorAvisos(0);
                                    }

                                } catch (Exception e) {
                                    tvAvisosMain.setText(sistema.getDEFAULT_MENSSAGE());
                                }

                            }
                        }
                    });

                    try {
                        Thread.sleep(sistema.getDEFAULT_TIME_SHOW_AVISO());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }

        }).start();

    }

    private void showRss() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    if (sistema.getDaoRss().isEnable()) {

                        sistema.getDaoRss().setEnable(false);

                        sistema.getmHandlerRss().post(new Runnable() {

                            @Override
                            public void run() {

                                tvRssBottomMain.setText(sistema.getDaoRss().getRss());

                            }

                        });

                    }

                }

            }

        }).start();

    }

    double valorDodolar;
    double temperatura;

    private void showTimeTemperaturaDolar() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    sistema.getmHandlerScreen().post(new Runnable() {
                        @Override
                        public void run() {

                            if (sistema.getDaoDolar().isEnable()) {

                                valorDodolar = sistema.round(Double.parseDouble(sistema.getDaoDolar().getDolar()), 2);

                                if (String.valueOf(valorDodolar).length() == 3) {
                                    tvCotacaoDolarMain.setText("VALOR DO DOLAR " + valorDodolar + "0 REAIS");
                                } else {
                                    tvCotacaoDolarMain.setText("VALOR DO DOLAR " + valorDodolar + " REAIS");
                                }
                            }

                            if (sistema.getDaoTemperatura().isEnable()) {

                                temperatura = sistema.getDaoTemperatura().getTemperatura() - 273.15;

                                if (temperatura != -273.15) {
                                    tvCloudMain.setText(String.valueOf((int) temperatura) + "ºC");
                                }

                            }

                            if (sistema.getCalendar().get(sistema.getCalendar().MINUTE) < 10) {
                                tvTimeMain.setText(sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY) + ":0" + sistema.getCalendar().get(sistema.getCalendar().MINUTE));
                            } else {
                                tvTimeMain.setText(sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY) + ":" + sistema.getCalendar().get(sistema.getCalendar().MINUTE));
                            }

                        }
                    });

                    try {
                        Thread.sleep(sistema.getDEFAULT_TIME_SHOW_TIME());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }

    public void executaAtualizacoes() throws IOException {

        //-------------- ENVIA ESTATISTICAS

        //daoEstatistica = new DaoEstatistica(PEP_ID, requestQueue, sistema.getPathSdCard()+"initLog.txt", this);

        //-------------- UPDATE SCREEN

        showTimeTemperaturaDolar();

        showRss();

        showAvisos();

    }

    @Override
    protected void onStop() {
        super.onStop();
        wakeLock.release();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        try {
            trimCache(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }


}
