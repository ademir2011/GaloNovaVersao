package com.example.ademi.galonovaversao.Activities;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.ademi.galonovaversao.Classes.Sistema;
import com.example.ademi.galonovaversao.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        tvRssBottomMain.setMovementMethod(new ScrollingMovementMethod());
        tvRssBottomMain.setSelected(true);

        sistema = Sistema.getInstancia(this);

        sistema.fullscreen(getWindow().getDecorView());

        sistema.getDaoLog().SendMsgToTxt("----------- Sistema iniciado -----------");

        new Thread(new Runnable() {
            @Override
            public void run() {

                try { Thread.sleep(sistema.getDEFAULT_TIME_INIT()); } catch (InterruptedException e) {}

                while (!sistema.getCheckConnection().isOnline()) {
                    try {
                        sistema.getDaoLog().SendMsgToTxt(" Dispositivo sem internet ");
                        sistema.showMessage("Sem internet", "top");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }

                if(sistema.getCheckConnection().isOnline()){

                    sistema.getDaoLog().SendMsgToTxt(" Dispositivo com internet ");

                    sistema.configurarHora();

                    if(sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY) == 21 &&
                        sistema.getCalendar().get(sistema.getCalendar().MINUTE) == 0 ){
                        boolean close = false;
                        while(!close){
                            sistema.inicializarAtualizacoes();
                            if(sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY) != 21 &&
                                    sistema.getCalendar().get(sistema.getCalendar().MINUTE) != 0) { close = true; }
                        }
                    } else {
                        sistema.inicializarAtualizacoes();
                    }

                    try { executaAtualizacoes(); } catch (IOException e) { sistema.getDaoLog().SendMsgToTxt(" problema ao exibir no display "); }

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

                while (true) {

                    if (sistema.getDaosDcard().isEnable()) {

                        if (!showPb) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pbLoading.setVisibility(View.INVISIBLE);
                                }
                            });
                            showPb = true;
                            listValueMain = sistema.getDaosDcard().getListValues();
                        }

                        hora        = sistema.getCalendar().get(sistema.getCalendar().HOUR_OF_DAY);
                        segundos    = sistema.getCalendar().get(sistema.getCalendar().SECOND);

                        if (secondTemp != segundos &&  ( segundos == 0 || segundos == 15 || segundos == 30 || segundos == 45) ) {
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

                            } else if (hora >= 21 && hora <= 5) {

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

                                                vvPropagandaMain.setVisibility(View.GONE);
                                                ivPropagandaMain.setVisibility(View.VISIBLE);
                                                ivPropagandaMain.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pep003));

                                            }

                                        } else {

                                            try {

                                                vvPropagandaMain.setVisibility(View.GONE);
                                                ivPropagandaMain.setVisibility(View.VISIBLE);

                                                Bitmap bm = decodeSampledBitmapFromResource(fileSDcard.getAbsolutePath(), 1870, 824);
                                                ivPropagandaMain.setImageBitmap(bm);

                                            } catch (Exception e) {

                                                vvPropagandaMain.setVisibility(View.GONE);
                                                ivPropagandaMain.setVisibility(View.VISIBLE);
                                                ivPropagandaMain.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pep003));

                                            }

                                        }

                                    } catch (Exception e) {
                                    }

                                }
                            });

                            sistema.deleteCache(MainActivity.this);

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

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String strPath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strPath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(strPath, options);
    }

    private void showAvisos() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    if (sistema.getDaoAvisos().isEnable()) {

                        sistema.getmHandlerRss().post(new Runnable() {

                            @Override
                            public void run() {

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
                        });

                        try {
                            Thread.sleep(sistema.getDEFAULT_TIME_SHOW_AVISO());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
        }).start();
    }

    private void showRss() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    if (sistema.getDaoRss().isEnable()) {

                        sistema.getDaoRss().setEnable(false);

                        try {

                            sistema.getmHandlerAvisos().post(new Runnable() {

                                @Override
                                public void run() {

                                    tvRssBottomMain.setText(sistema.getDaoRss().getRss());


                                }
                            });

                        } catch (Exception e) {
                            sistema.getDaoLog().SendMsgToTxt("problema ao exibir/atualizar rss");
                        }
                    }

                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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

                while (true) {

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
                        sistema.getDaoLog().SendMsgToTxt(" problema ao exibir temperatura/dolar/hora ");
                    }

                }

            }
        }).start();

    }

    public void executaAtualizacoes() throws IOException {

        //-------------- ENVIA ESTATISTICAS

        //daoEstatistica = new DaoEstatistica(PEP_ID, requestQueue, sistema.getPathSdCard()+"initLog.txt", this);

        //-------------- UPDATE SCREEN

        showPropagandas();

        showTimeTemperaturaDolar();

        showRss();

        showAvisos();

        sistema.getDaoLog().SendMsgToTxt(" Itens exibidos na tela com sucesso ");

    }

    @Override
    protected void onResume() {
        onStart();
        super.onResume();
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}
