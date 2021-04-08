package com.ibnux.notifvoice;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class NotifService extends NotificationListenerService {
    TextToSpeech t1;
    PackageManager pm;
    Locale sdn;

    public NotifService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            if (t1 == null) {
                t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            sdn = new Locale("id", "ID");
                            if (t1.isLanguageAvailable(sdn) > 0) {
                                t1.setLanguage(sdn);
                            } else {
                                sdn = null;
                                t1.setLanguage(Locale.getDefault());
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        if(t1==null){
            t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        sdn = new Locale("id", "ID");
                        if (t1.isLanguageAvailable(sdn) > 0) {
                            t1.setLanguage(sdn);
                        } else {
                            sdn = null;
                            t1.setLanguage(Locale.getDefault());
                        }
                        onNotificationPosted(sbn);
                    }
                }
            });
            return;
        }

        if(pm==null){
            pm = getApplicationContext().getPackageManager();
        }


        if(sbn.isOngoing()){
            return;
        }

        try {
            String pack = sbn.getPackageName();
            String appname = "";
            try {
                appname = pm.getApplicationLabel(pm.getApplicationInfo(pack, 0)).toString();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            String ticker = "", text = "",title = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            if(extras.containsKey("android.title"))
                title = extras.getString("android.title");
            if(extras.containsKey("android.text")) {
                CharSequence cs = extras.getCharSequence("android.text");
                if(cs!=null){
                    text = cs.toString();
                }
            }
            Log.i("Package", pack + "");
            Log.i("Ticker", ticker + "");
            Log.i("Text", text + "");
            Log.i("Title", title + "");

            t1.speak("Ada notifikasi dari " + appname + " isinya " + ((title!=null)?title:"") +" "+text, TextToSpeech.QUEUE_ADD, null, null);

            long size = Fungsi.writeToFile(appname+"\n"+((title!=null)?title:"")+" "+text,this);
            if(size>500000){
                String txt = Fungsi.readFile(this);
                String[] txts = txt.split("----_-_----");
                int jml = txts.length;
                int sisa = jml/2;
                txt = "";
                for(int n = 0;n<sisa;n++){
                    txt += "----_-_----";
                }
                Fungsi.clearLogs(this);
                Fungsi.writeToFile(txt,this);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn, rankingMap, reason);
    }
}
