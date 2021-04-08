package com.ibnux.notifvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Locale;

public class SmsReceiver extends BroadcastReceiver {
    TextToSpeech t1;
    Locale sdn;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(t1==null){
            t1=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
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
                        onReceive(context,intent);
                    }
                }
            });
            return;
        }

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                try {
                    String messageFrom = getContactName(smsMessage.getOriginatingAddress(), context);
                    String messageBody = smsMessage.getMessageBody();

                    t1.speak("Ada SMS dari " + messageFrom + " isinya " + messageBody, TextToSpeech.QUEUE_ADD, null, null);
                    long size = Fungsi.writeToFile(messageFrom + " (" + smsMessage.getOriginatingAddress() + ") " + "\n" + messageBody, context);
                    if (size > 500000) {
                        String txt = Fungsi.readFile(context);
                        String[] txts = txt.split("----_-_----");
                        int jml = txts.length;
                        int sisa = jml / 2;
                        txt = "";
                        for (int n = 0; n < sisa; n++) {
                            txt += "----_-_----";
                        }
                        Fungsi.clearLogs(context);
                        Fungsi.writeToFile(txt, context);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    String messageFrom = smsMessage.getOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody();

                    t1.speak("Ada SMS dari " + messageFrom + " isinya " + messageBody, TextToSpeech.QUEUE_ADD, null, null);
                    long size = Fungsi.writeToFile(messageFrom + " (" + smsMessage.getOriginatingAddress() + ") " + "\n" + messageBody, context);
                    if (size > 500000) {
                        String txt = Fungsi.readFile(context);
                        String[] txts = txt.split("----_-_----");
                        int jml = txts.length;
                        int sisa = jml / 2;
                        txt = "";
                        for (int n = 0; n < sisa; n++) {
                            txt += "----_-_----";
                        }
                        Fungsi.clearLogs(context);
                        Fungsi.writeToFile(txt, context);
                    }
                }
            }
        }
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = phoneNumber;
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }
}
