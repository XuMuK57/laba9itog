package com.example.taskplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeadlineReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent i) {
        String text = i.getStringExtra("text");
        if (text == null) text = "";
        Notify.send(c, text);
    }
}
