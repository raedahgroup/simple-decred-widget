package com.simpledecredwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class DecredToBitcoin extends AppWidgetProvider {
    String ACTION_UPDATE_CLICK = "com.simpledecredwidget.DecredToBitcoin.action.UPDATE_CLICK";
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, boolean clicked) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.decred_to_bitcoin);
        views.setOnClickPendingIntent(R.id.decret_to_bitcoin,getPendingSelfIntent(context, ACTION_UPDATE_CLICK));
        if(clicked) {
            //views.setViewVisibility(R.id.currency_icon, View.GONE);
            views.setViewVisibility(R.id.content_text, View.GONE);
            views.setViewVisibility(R.id.progressBar, View.VISIBLE);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, false);
        }
    }

    void update(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);

        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, true);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(ACTION_UPDATE_CLICK.equals(intent.getAction())){
            update(context);
        }
    }
}

