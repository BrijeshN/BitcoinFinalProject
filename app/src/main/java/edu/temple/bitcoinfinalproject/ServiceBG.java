package edu.temple.bitcoinfinalproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;


public class ServiceBG extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setAction("update");
                sendBroadcast(intent);

                // Update chart every 10 sec
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 10000);
    }

}
