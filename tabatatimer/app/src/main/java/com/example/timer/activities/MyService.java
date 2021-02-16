package com.example.timer.activities;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;


public class MyService extends Service {
    private CountDownTimer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        long[] time_remaining = {intent.getIntExtra("TimeValue", 0)*1000};
        Intent local = new Intent();
        local.setAction("timer");
        timer = new CountDownTimer(time_remaining[0], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_remaining[0]=millisUntilFinished;
                local.putExtra("Remaining", time_remaining[0]/1000);
                sendBroadcast(local);
            }

            @Override
            public void onFinish() {
                local.putExtra("Finished", 0);
                sendBroadcast(local);
            }
        }.start();

        IntentFilter filter = new IntentFilter();
        filter.addAction("Paused");
        BroadcastReceiver broadcastRec = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timer.cancel();
            }
        };

        registerReceiver(broadcastRec, filter);


        return super.onStartCommand(intent, flags, startId);
    }

}
