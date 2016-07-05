package com.example.ademi.galonovaversao.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

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

import com.example.ademi.galonovaversao.Classes.DefaultExceptionHandler;
import com.example.ademi.galonovaversao.Classes.FirstService;
import com.example.ademi.galonovaversao.Classes.Sistema;
import com.example.ademi.galonovaversao.DAO.DAOSDcard;
import com.example.ademi.galonovaversao.R;

import java.io.File;
import java.io.IOException;
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
    @Bind(R.id.rlRoot) RelativeLayout rlRoot;

    Sistema sistema;
    private DAOSDcard daosDcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, FirstService.class));

        ButterKnife.bind(this);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        tvRssBottomMain.setMovementMethod(new ScrollingMovementMethod());
        tvRssBottomMain.setSelected(true);

        sistema = Sistema.getInstancia(this);

        sistema.fullscreen(getWindow().getDecorView());

        sistema.getDaoLog().SendMsgToTxt("----------- Sistema iniciado -----------");

        new Thread(
            new Runnable() {
                @Override
                public void run() {

//                    while(true){
//
//                        try {
//                            Thread.sleep(15000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        throw new RuntimeException("This is a crash");
//
//                    }

                }
            }
        ).start();

        try {
            //-------------- ATUALIZA E EXIBE PROPAGANDAS
            daosDcard = new DAOSDcard();
            showPropagandas();
        } catch (Exception e) {
            Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this, MainActivity.class));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    int count = 0;

                    while( (count*1000) < sistema.getDEFAULT_TIME_INIT() ){

                        sistema.showMessage(String.valueOf(count),"top");

                        count++;

                        Thread.sleep(1000);

                    }

                } catch (InterruptedException e) {
                    Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                }

                while (!sistema.getCheckConnection().isOnline()) {
                    try {
                        sistema.getDaoLog().SendMsgToTxt(" Dispositivo sem internet ");
                        sistema.showMessage("Sem internet", "top");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                    }
                }

                if(sistema.getCheckConnection().isOnline()){

                    sistema.getDaoLog().SendMsgToTxt(" Dispositivo com internet ");

                    sistema.configurarHora();

                    if(sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY) == 21 &&
                        sistema.getCalendar().get(sistema.getCalendar().MINUTE) < 15 ){
                        boolean close = false;
                        while(!close){
                            sistema.configurarHora();
                            if(sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY) != 21 && sistema.getCalendar().get(sistema.getCalendar().MINUTE) != 0) { close = true; }
                        }
                    } else {}

                    sistema.inicializarAtualizacoes();
                    try { executaAtualizacoes(); } catch (IOException e) {
                        sistema.getDaoLog().SendMsgToTxt(" problema ao exibir no display ");
                        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                    }

                    sistema.showMessage("Com internet", "top");
                    sistema.showMessage("Inicialização finalizada", "right");

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

//                        if (secondTemp != segundos &&  ( segundos == 0 || segundos == 15 || segundos == 30 || segundos == 45) ) {
                        if (secondTemp != segundos &&  ( segundos%3==0 ) ) {
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

                                    try {

                                        fileSDcard = new File(sistema.getPathSdCard() + "assets/" + value);

//                                        fileSDcard = daosDcard.getListFiles().get(cont-1);

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

                                                vvPropagandaMain.setVideoPath(fileSDcard.getAbsolutePath());
                                                vvPropagandaMain.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                    @Override
                                                    public void onPrepared(MediaPlayer mp) {
                                                        vvPropagandaMain.start();
                                                    }
                                                });

                                                vvPropagandaMain.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                                    @Override
                                                    public boolean onError(MediaPlayer mp, int what, int extra) {
                                                        vvPropagandaMain.stopPlayback();
                                                        vvPropagandaMain.setVisibility(View.GONE);
                                                        ivPropagandaMain.setVisibility(View.VISIBLE);
                                                        ivPropagandaMain.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pep003));
                                                        return true;
                                                    }
                                                });

                                            } catch (Exception e) {

                                                Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                                                vvPropagandaMain.setVisibility(View.GONE);
                                                ivPropagandaMain.setVisibility(View.VISIBLE);
                                                ivPropagandaMain.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pep003));

                                            }

                                        } else {

                                            try {

                                                vvPropagandaMain.setVisibility(View.GONE);
                                                ivPropagandaMain.setVisibility(View.VISIBLE);

//                                                Bitmap bm = decodeSampledBitmapFromResource(fileSDcard.getAbsolutePath(), 1870, 824);
//                                                ivPropagandaMain.setImageBitmap(bm);
                                                ivPropagandaMain.setImageURI( daosDcard.getListFiles().get(cont-1) );
                                            } catch (Exception e) {

                                                Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                                                vvPropagandaMain.setVisibility(View.GONE);
                                                ivPropagandaMain.setVisibility(View.VISIBLE);
                                                ivPropagandaMain.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pep003));

                                            }

                                        }

                                    } catch (Exception e) {
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
                        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                    }

                }

            }
        }).start();

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
                                    Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                                }

                            }
                        }
                    });

                    try {
                        Thread.sleep(sistema.getDEFAULT_TIME_SHOW_AVISO());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
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

                        try {

                            sistema.getmHandlerRss().post(new Runnable() {

                                @Override
                                public void run() {

                                    tvRssBottomMain.setText(sistema.getDaoRss().getRss());

                                }

                            });

                        } catch (Exception e) {
                            sistema.getDaoLog().SendMsgToTxt("problema ao exibir/atualizar rss");
                            Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
                        }

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

                                valorDodolar = round(Double.parseDouble(sistema.getDaoDolar().getDolar()), 2);

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
                        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(MainActivity.this, MainActivity.class));
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

        sistema.getDaoLog().SendMsgToTxt(" Itens exibidos na tela com sucesso ");

    }

    /**
     * Restarts the activity
     *
     * @param context context
     */
    public static void restartApp(final Context context) {
        Intent restart = context
                .getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(restart);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        restartApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        restartApp(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        restartApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        restartApp(this);

    }
}
