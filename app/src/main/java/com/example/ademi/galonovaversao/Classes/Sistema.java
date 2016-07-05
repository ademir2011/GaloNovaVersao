package com.example.ademi.galonovaversao.Classes;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.ademi.galonovaversao.Activities.MainActivity;
import com.example.ademi.galonovaversao.DAO.DaoAvisos;
import com.example.ademi.galonovaversao.DAO.DaoDolar;
import com.example.ademi.galonovaversao.DAO.DaoEstatistica;
import com.example.ademi.galonovaversao.DAO.DaoLog;
import com.example.ademi.galonovaversao.DAO.DaoRss;
import com.example.ademi.galonovaversao.DAO.DaoTemperatura;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import okhttp3.OkHttpClient;

/**
 * Created by ademi on 19/06/2016.
 */
public class Sistema {

    private static Sistema instancia;

    private final String PEP_ID = "1";
    public String pathSdCard = "";
    private final String initLogName = "initLog.txt";

    private final int DEFAULT_TIME_INIT                     = 1 * 1 * 5 * 1000;
    private final int DEFAULT_TIME_WRITE_STATISTICS         = 1 * 1 * 10 * 1000;

    private final int DEFAULT_TIME_UPDATE_DOLAR             = 1 * 1 * 20 * 1000;
    private final int DEFAULT_TIME_UPDATE_TEMPERATURA       = 1 * 1 * 20 * 1000;
    private final int DEFAULT_TIME_UPDATE_RSS               = 1 * 1 * 20 * 1000;
    private final int DEFAULT_TIME_UPDATE_AVISOS            = 1 * 1 * 20 * 1000;

    private final int DEFAULT_TIME_SHOW_AVISO               = 1 * 1 * 1 * 1000;
    private final int DEFAULT_TIME_SHOW_PROPAGANDA          = 1 * 1 * 1 * 10;
    private final int DEFAULT_TIME_SHOW_TEMPERATURA         = DEFAULT_TIME_UPDATE_TEMPERATURA/2;
    private final int DEFAULT_TIME_SHOW_DOLAR               = DEFAULT_TIME_UPDATE_DOLAR/2;
    private final int DEFAULT_TIME_SHOW_TIME                = 1 * 1 * 1 * 10;
    private final int DEFAULT_TIME_SHOW_RSS                 = 1 * 1 * 10 * 1000;

//    private final int DEFAULT_TIME_INIT                     = 1 * 2 * 60 * 1000;
//    private final int DEFAULT_TIME_WRITE_STATISTICS         = 1 * 1 * 10 * 1000;
//
//    private final int DEFAULT_TIME_UPDATE_DOLAR             = 1 * 5 * 60 * 1000;
//    private final int DEFAULT_TIME_UPDATE_TEMPERATURA       = 1 * 5 * 60 * 1000;
//    private final int DEFAULT_TIME_UPDATE_RSS               = 5 * 5 * 60 * 1000;
//    private final int DEFAULT_TIME_UPDATE_AVISOS            = 1 * 5 * 60 * 1000;
//
//    private final int DEFAULT_TIME_SHOW_AVISO               = 1 * 1 * 15 * 1000;
//    private final int DEFAULT_TIME_SHOW_PROPAGANDA          = 1 * 1 * 1 * 200;
//    private final int DEFAULT_TIME_SHOW_TEMPERATURA         = DEFAULT_TIME_UPDATE_TEMPERATURA/2;
//    private final int DEFAULT_TIME_SHOW_DOLAR               = DEFAULT_TIME_UPDATE_DOLAR/2;
//    private final int DEFAULT_TIME_SHOW_TIME                = 1 * 1 * 1 * 450;
//    private final int DEFAULT_TIME_SHOW_RSS                 = 1 * 5 * 60 * 1000;

    private Handler mHandlerScreen = new Handler(Looper.getMainLooper());

    private Handler mHandlerRss = new Handler(Looper.getMainLooper());

    private Handler mHandlerAvisos = new Handler(Looper.getMainLooper());

    private Handler mHandlerPropaganda = new Handler(Looper.getMainLooper());

    private Handler mHandlerTemperatura = new Handler(Looper.getMainLooper());

    private Handler mHandlerDolar = new Handler(Looper.getMainLooper());

    private URL urlAvisos;

    private final String DEFAULT_MENSSAGE = "Atualizando ou em Manutenção, aguarde. Para mais informações, www.galomidiaavacanda.com.br, anuncie conosco, ótimos planos a partir de R$ 25,00 !";
    private String cidade = "Natal";
    private String apikeyweather = "6c44fc59654648e97c2132a1acecd057";
    private String urlJsonObsTemperatura = "http://api.openweathermap.org/data/2.5/weather?q=" + cidade + ",br&appid=" + apikeyweather;
    private String urlJsonObjDolar = "http://api.promasters.net.br/cotacao/v1/valores?moedas=USD&alt=json";
    private String urlRssFonte = "http://g1.globo.com/dynamo/rn/rio-grande-do-norte/rss2.xml";
    private String urlTime = "http://galomidiaavancada.com.br/gestao/time.php";

    private DaoRss daoRss;
    private DaoAvisos daoAvisos;
    private DaoDolar daoDolar;
    private DaoTemperatura daoTemperatura;
    private CheckConnection checkConnection;
    private DaoLog daoLog;
    private DaoEstatistica daoEstatistica;

    private Calendar calendar;

    private Context context;
    private String tokenFirebase = "";
    private String messagePushFirebase = "";
    private String string64 = "";

    private Sistema(Context context) {

        this.context = context;

        if (new File("storage/external_storage/sdcard1/").exists()) {
            pathSdCard = "storage/external_storage/sdcard1/";
        } else if (new File("mnt/external_sd/").exists()) {
            pathSdCard = "mnt/external_sd/";
        }

        calendar = Calendar.getInstance();

        try {
            urlAvisos = new URL("http://onedreams.com.br/galo/gestao/pep_" + PEP_ID + "/avisos/config_avisos.txt");
        } catch (MalformedURLException e) {
            urlAvisos = null;
            e.printStackTrace();
        }

        checkConnection = new CheckConnection(context);

        daoLog = new DaoLog();

    }

    public synchronized void inicializarAtualizacoes() {

        //--------------- ATUALIZA E EXIBE RSS

        daoRss = new DaoRss();

        //--------------- ATUALIZA E EXIBE AVISOS

        daoAvisos = new DaoAvisos();

        //-------------- ATUALIZA E EXIBE DOLAR

        daoDolar = new DaoDolar();

        //-------------- ATUALIZA E EXIBE TEMPERATURA

        daoTemperatura = new DaoTemperatura();


        daoLog.SendMsgToTxt(" Classes daos instanciadas ");

    }

    public static Sistema getInstancia(Context context) {

        if (instancia == null) {
            instancia = new Sistema(context);
        }

        return instancia;

    }

    public void configurarHora() {

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url(urlTime).build();

        okhttp3.Response response = null;

        try {

            response = client.newCall(request).execute();

            String json = response.body().string();

            JSONObject jsonObject = new JSONObject(json);

            final String time = jsonObject.getString("time");

            String lineArray[] = time.split(":");

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lineArray[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(lineArray[1]));
            calendar.set(Calendar.SECOND, Integer.parseInt(lineArray[2]));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        long startTime = System.currentTimeMillis(); // Process start time

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        long elapsedTime = System.currentTimeMillis() - startTime;

                        calendar.setTimeInMillis(calendar.getTime().getTime() + elapsedTime);

                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showMessage(final String texto, final String position) {

        try {

//        Handler handler = new Handler(Looper.getMainLooper());
//
//        handler.post(new Runnable() {
//
//            @Override
//            public void run() {
//                Toast toast = Toast.makeText(context, texto, Toast.LENGTH_SHORT);
//
//                if (position.equals("left")) {
//                    toast.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL, 0, 0);
//                } else if (position.equals("right")) {
//                    toast.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
//                } else if (position.equals("bottom")) {
//                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//                } else if (position.equals("top")) {
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
//                }
//
//                toast.show();
//            }
//        });
        } catch ( Exception e) {
            Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(context, MainActivity.class));
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void fullscreen(View decorView) {

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
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
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public static synchronized Sistema getInstancia() {
        return instancia;
    }

    public String getInitLogName() {
        return initLogName;
    }

    public String getTokenFirebase() {
        return tokenFirebase;
    }

    public void setTokenFirebase(String tokenFirebase) {
        this.tokenFirebase = tokenFirebase;
    }

    public String getMessagePushFirebase() {
        return messagePushFirebase;
    }

    public void setMessagePushFirebase(String messagePushFirebase) {
        this.messagePushFirebase = messagePushFirebase;
    }

    public String getString64() {
        return string64;
    }

    public void setString64(String string64) {
        this.string64 = string64;
    }

    public String getPEP_ID() {
        return PEP_ID;
    }

    public String getPathSdCard() {
        return pathSdCard;
    }

    public int getDEFAULT_TIME_UPDATE_TEMPERATURA() {
        return DEFAULT_TIME_UPDATE_TEMPERATURA;
    }

    public int getDEFAULT_TIME_UPDATE_DOLAR() {
        return DEFAULT_TIME_UPDATE_DOLAR;
    }

    public int getDEFAULT_TIME_SHOW_AVISO() {
        return DEFAULT_TIME_SHOW_AVISO;
    }

    public int getDEFAULT_TIME_UPDATE_RSS() {
        return DEFAULT_TIME_UPDATE_RSS;
    }

    public int getDEFAULT_TIME_UPDATE_AVISOS() {
        return DEFAULT_TIME_UPDATE_AVISOS;
    }

    public int getDEFAULT_TIME_SHOW_PROPAGANDA() {
        return DEFAULT_TIME_SHOW_PROPAGANDA;
    }

    public int getDEFAULT_TIME_INIT() {
        return DEFAULT_TIME_INIT;
    }

    public int getDEFAULT_TIME_WRITE_STATISTICS() {
        return DEFAULT_TIME_WRITE_STATISTICS;
    }

    public Handler getmHandlerScreen() {
        return mHandlerScreen;
    }

    public void setmHandlerScreen(Handler mHandlerScreen) {
        this.mHandlerScreen = mHandlerScreen;
    }

    public Handler getmHandlerRss() {
        return mHandlerRss;
    }

    public void setmHandlerRss(Handler mHandlerRss) {
        this.mHandlerRss = mHandlerRss;
    }

    public Handler getmHandlerAvisos() {
        return mHandlerAvisos;
    }

    public void setmHandlerAvisos(Handler mHandlerAvisos) {
        this.mHandlerAvisos = mHandlerAvisos;
    }

    public Handler getmHandlerPropaganda() {
        return mHandlerPropaganda;
    }

    public void setmHandlerPropaganda(Handler mHandlerPropaganda) {
        this.mHandlerPropaganda = mHandlerPropaganda;
    }

    public URL getUrlAvisos() {
        return urlAvisos;
    }

    public void setUrlAvisos(URL urlAvisos) {
        this.urlAvisos = urlAvisos;
    }

    public String getDEFAULT_MENSSAGE() {
        return DEFAULT_MENSSAGE;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getApikeyweather() {
        return apikeyweather;
    }

    public void setApikeyweather(String apikeyweather) {
        this.apikeyweather = apikeyweather;
    }

    public String getUrlJsonObsTemperatura() {
        return urlJsonObsTemperatura;
    }

    public void setUrlJsonObsTemperatura(String urlJsonObsTemperatura) {
        this.urlJsonObsTemperatura = urlJsonObsTemperatura;
    }

    public String getUrlJsonObjDolar() {
        return urlJsonObjDolar;
    }

    public void setUrlJsonObjDolar(String urlJsonObjDolar) {
        this.urlJsonObjDolar = urlJsonObjDolar;
    }

    public String getUrlRssFonte() {
        return urlRssFonte;
    }

    public void setUrlRssFonte(String urlRssFonte) {
        this.urlRssFonte = urlRssFonte;
    }

    public DaoRss getDaoRss() {
        return daoRss;
    }

    public void setDaoRss(DaoRss daoRss) {
        this.daoRss = daoRss;
    }

    public DaoAvisos getDaoAvisos() {
        return daoAvisos;
    }

    public void setDaoAvisos(DaoAvisos daoAvisos) {
        this.daoAvisos = daoAvisos;
    }

    public DaoDolar getDaoDolar() {
        return daoDolar;
    }

    public void setDaoDolar(DaoDolar daoDolar) {
        this.daoDolar = daoDolar;
    }

    public DaoTemperatura getDaoTemperatura() {
        return daoTemperatura;
    }

    public void setDaoTemperatura(DaoTemperatura daoTemperatura) {
        this.daoTemperatura = daoTemperatura;
    }

    public CheckConnection getCheckConnection() {
        return checkConnection;
    }

    public void setCheckConnection(CheckConnection checkConnection) {
        this.checkConnection = checkConnection;
    }

    public DaoLog getDaoLog() {
        return daoLog;
    }

    public void setDaoLog(DaoLog daoLog) {
        this.daoLog = daoLog;
    }

    public DaoEstatistica getDaoEstatistica() {
        return daoEstatistica;
    }

    public void setDaoEstatistica(DaoEstatistica daoEstatistica) {
        this.daoEstatistica = daoEstatistica;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Handler getmHandlerTemperatura() {
        return mHandlerTemperatura;
    }

    public void setmHandlerTemperatura(Handler mHandlerTemperatura) {
        this.mHandlerTemperatura = mHandlerTemperatura;
    }

    public Handler getmHandlerDolar() {
        return mHandlerDolar;
    }

    public void setmHandlerDolar(Handler mHandlerDolar) {
        this.mHandlerDolar = mHandlerDolar;
    }

    public int getDEFAULT_TIME_SHOW_TEMPERATURA() {
        return DEFAULT_TIME_SHOW_TEMPERATURA;
    }

    public int getDEFAULT_TIME_SHOW_DOLAR() {
        return DEFAULT_TIME_SHOW_DOLAR;
    }

    public int getDEFAULT_TIME_SHOW_TIME() {
        return DEFAULT_TIME_SHOW_TIME;
    }

    public int getDEFAULT_TIME_SHOW_RSS() {
        return DEFAULT_TIME_SHOW_RSS;
    }
}
