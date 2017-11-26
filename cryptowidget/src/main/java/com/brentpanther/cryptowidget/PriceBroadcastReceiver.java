package com.brentpanther.cryptowidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;

public class PriceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(intent.getAction() != null) {
            if (intent.getAction().equals("FALSE_CLICK")) {
                Intent i = new Intent(context, BackgroundService.class);
                i.putExtras(intent);
                context.startService(i);
                return;
            }
        }
        final int widgetId = intent.getIntExtra("appWidgetId",0);
        final Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        Long clickTimer = prefs.getLastClicked();
        if((System.currentTimeMillis() - clickTimer) < 500){
            prefs.setLastClicked(System.currentTimeMillis() + 500);
            Intent i = new Intent(context, SettingsActivity.class);
            i.putExtra("widgetId", widgetId);
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else{
            prefs.setLastClicked(System.currentTimeMillis());
            Intent i = new Intent(context, BackgroundService.class);
            i.putExtras(intent);
            context.startService(i);
            new Thread() {
                public void run() {
                    try {
                        prefs.setLastClicked(System.currentTimeMillis());
                        sleep(505);
                        Long clickTimer = prefs.getLastClicked();
                        Looper.prepare();
                        System.out.println((System.currentTimeMillis() - clickTimer)+" click timer");
                        if ((System.currentTimeMillis() - clickTimer) > 500) {
                            Intent i = new Intent(context, BackgroundService.class);
                            i.putExtras(intent);
                            context.startService(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }
}
