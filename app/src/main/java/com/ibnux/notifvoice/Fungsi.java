package com.ibnux.notifvoice;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Fungsi {

    public static long writeToFile(String data, Context context) {
        try {
            String txt = readFile(context);
            FileOutputStream stream = new FileOutputStream(new File(context.getCacheDir(), "log.txt"));
            try {
                byte[] bites = (data+"\n"+txt).getBytes();
                stream.write(bites);
                return bites.length;
            } finally {
                stream.close();
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return 0;
    }

    public static long sizeFile(Context context) {
        try {
            File file = new File(context.getCacheDir(), "log.txt");
            return file.length();
        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return 0;
    }

    public static void clearLogs(Context context) {
        try {
            FileOutputStream stream = new FileOutputStream(new File(context.getCacheDir(), "log.txt"));
            try {
                stream.write(("").getBytes());
            } finally {
                stream.close();
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFile(Context context) {
        try {
            File file = new File(context.getCacheDir(), "log.txt");
            int length = (int) file.length();

            byte[] bytes = new byte[length];

            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                in.close();
            }

            return new String(bytes);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return "";
    }
}
