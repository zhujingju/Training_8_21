package com.grasp.training.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.grasp.training.service.MqttService;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MqttService.class));
    }
}

