package com.simpledecredwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.simpledecredwidget.service.DcrStats;
import com.simpledecredwidget.service.DcrStatsService;

import java.util.HashMap;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class SimpleDecredWidget extends AppWidgetProvider {
    private final String PREFS_NAME = "com.simpledecredwidget.WidgetConfigurator";
    private final String ACTION_CLICK = "com.simpledecredwidget.CLICK";
    private boolean waitingForService = false;
    private PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra("widgetId", appWidgetId);
        L.l("Pending intent widgetId: "+appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        //L.l("Updating widget");
        if(appWidgetId == 0){
            return;
        }
        Configuration config = loadConfiguration(context, appWidgetId);
        RemoteViews views = applyConfiguration(config, context, appWidgetId);
        L.l("Calling Pending Intent from updateAppWidget: "+appWidgetId);
        views.setOnClickPendingIntent(R.id.simple_decred_widget,getPendingSelfIntent(context,ACTION_CLICK+""+appWidgetId, appWidgetId));
        views.setTextViewText(R.id.content_text, "--");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    RemoteViews applyConfiguration(Configuration config, Context context, int appWidgetId){
        L.l("applying config");
        RemoteViews views;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int minWidth = prefs.getInt("widgetWidth"+appWidgetId,0);
        L.l("Widget Width: "+minWidth);
        if(config.theme == 0){
            views = new RemoteViews(context.getPackageName(), R.layout.simple_decred_widget);
        }else{
            views = new RemoteViews(context.getPackageName(), R.layout.simple_decred_widget_dark);
        }
        if(config.showLogo){
            if(minWidth <= 71){
                if(config.type == 5){
                    views.setTextViewTextSize(R.id.content_text,COMPLEX_UNIT_SP,15);
                }else{
                    views.setTextViewTextSize(R.id.content_text,COMPLEX_UNIT_SP,20);
                }
                views.setViewVisibility(R.id.currency_icon_small, VISIBLE);
                views.setViewVisibility(R.id.currency_icon, INVISIBLE);
            }else{
                views.setViewVisibility(R.id.currency_icon_small, INVISIBLE);
                views.setViewVisibility(R.id.currency_icon, VISIBLE);
                if(config.type == 5){
                    views.setTextViewTextSize(R.id.content_text,COMPLEX_UNIT_SP,25);
                }else{
                    views.setTextViewTextSize(R.id.content_text,COMPLEX_UNIT_SP,30);
                }
            }
        }else{
            views.setViewVisibility(R.id.currency_icon, View.GONE);
            views.setViewVisibility(R.id.currency_icon_small, View.GONE);
            if(config.type == 5){
                views.setTextViewTextSize(R.id.content_text,COMPLEX_UNIT_SP,25);
            }else{
                views.setTextViewTextSize(R.id.content_text,COMPLEX_UNIT_SP,30);
            }
        }
        switch (config.type){
            case 0:
                views.setViewVisibility(R.id.top_text, View.GONE);
                views.setTextViewText(R.id.bottom_text,"BTC/DCR");
                break;
            case 1:
                views.setViewVisibility(R.id.top_text, View.GONE);
                views.setTextViewText(R.id.bottom_text,"USD/DCR");
                break;
            case 2:
                views.setViewVisibility(R.id.top_text, VISIBLE);
                views.setTextViewText(R.id.top_text,"PoS Ticket Price");
                views.setTextViewText(R.id.bottom_text,"Current");
                break;
            case 3:
                views.setViewVisibility(R.id.top_text, VISIBLE);
                views.setTextViewText(R.id.top_text,"PoS Ticket Price");
                views.setTextViewText(R.id.bottom_text,"Change");
                break;
            case 4:
                views.setViewVisibility(R.id.top_text, VISIBLE);
                views.setTextViewText(R.id.top_text,"PoS Ticket Price");
                views.setTextViewText(R.id.bottom_text,"Est. Next Price");
                break;
            case 5:
                views.setViewVisibility(R.id.top_text, VISIBLE);
                views.setTextViewText(R.id.top_text,"PoW Network");
                views.setTextViewText(R.id.bottom_text,"Difficulty");
                break;
            case 6:
                views.setViewVisibility(R.id.top_text, VISIBLE);
                views.setTextViewText(R.id.top_text,"PoW Network");
                views.setTextViewText(R.id.bottom_text,"Hash Rate");
                break;
        }
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private Configuration loadConfiguration(Context context, int appWidgetId){
        Configuration configuration = new Configuration();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        configuration.type = prefs.getInt(appWidgetId+"type",0);
        configuration.showLogo = prefs.getBoolean(appWidgetId+"logo", true);
        configuration.theme = prefs.getInt(appWidgetId+"theme", -1);
        L.l("Loading Configuration theme: "+configuration.theme+" App Widget ID: "+appWidgetId);
        return configuration;
    }

    void update(Context context, DcrStats stats, int appWidgetId){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);
        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        Configuration config = loadConfiguration(context, appWidgetId);
        RemoteViews views = applyConfiguration(config, context, appWidgetId);
        L.l("Calling Pending Intent From update: "+appWidgetId);
        views.setOnClickPendingIntent(R.id.simple_decred_widget,getPendingSelfIntent(context,ACTION_CLICK+""+appWidgetId, appWidgetId));
        switch (config.type){
            case 0:
                //dcr/btc
                views.setTextViewText(R.id.content_text, stats.getBtcPrice());
                break;
            case 1:
                //dcr/usd
                views.setTextViewText(R.id.content_text, "$"+stats.getUsdPrice());
                break;
            case 2:
                //current
                views.setTextViewText(R.id.content_text, stats.getTicketPrice());
                break;
            case 3:
                //Change
                views.setTextViewText(R.id.content_text, new ChangeTime(stats.getPriceChangeInSeconds()).format());
                break;
            case 4:
                //Est. next price
                views.setTextViewText(R.id.content_text, stats.getEstNextPrice());
                break;
            case 5:
                //difficulty
                views.setTextViewText(R.id.content_text, stats.getDifficulty());
                break;
            case 6:
                //hash rate
                double networkHash = stats.getNetworkHash();
                views.setTextViewText(R.id.content_text, new HashRate(networkHash).format());
                break;
        }
        views.setViewVisibility(R.id.content_text, VISIBLE);
        views.setViewVisibility(R.id.progressBar, View.GONE);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt("widgetWidth" + appWidgetId, newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH));
        prefs.commit();
        updateAppWidget(context,appWidgetManager, appWidgetId);
        sendIntentToService(context, appWidgetId);
    }

    void error(Context context, int appWidgetId){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        Configuration config = loadConfiguration(context, appWidgetId);
        RemoteViews views = applyConfiguration(config, context, appWidgetId);
        L.l("Calling Pending Intent from error: " + appWidgetId);
        views.setOnClickPendingIntent(R.id.simple_decred_widget,getPendingSelfIntent(context,ACTION_CLICK+""+appWidgetId, appWidgetId));
        views.setTextViewText(R.id.content_text, "--");
//        views.setTextViewText(R.id.content_text, appWidgetId + "");
        views.setViewVisibility(R.id.content_text, VISIBLE);
        views.setViewVisibility(R.id.progressBar, View.GONE);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().startsWith(ACTION_CLICK)){
            L.l("CLICK ACTION: "+intent.getAction());
        }
        if(intent.getAction().equals(MyIntents.UPDATE)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                    (context);
            int widgetId = intent.getExtras().getInt("widgetId");
            if(widgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
                return;
            }
            L.l("WidgetId FOR UPDATE: "+widgetId);
            updateAppWidget(context, appWidgetManager, widgetId);
            sendIntentToService(context, widgetId);
        }else if(intent.getAction().startsWith(ACTION_CLICK)){
            SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
            Bundle b = intent.getExtras();
            if(b == null) {
                L.l("Bundle is null: " + intent.getIntExtra("widgetId", -1));
                return;
            }
            final int widgetId = Integer.parseInt(intent.getAction().replace(ACTION_CLICK,""));
            L.l("Click Widget ID: "+widgetId);
            Long clickTimer = preferences.getLong("widgetClick"+widgetId, 0);
            if((System.currentTimeMillis() - clickTimer) < 500){
                SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
                prefs.putLong("widgetClick" + widgetId, System.currentTimeMillis() + 500);
                prefs.commit();
                Intent i = new Intent(context, WidgetConfigurator.class);
                L.l("Click App Widget ID: "+widgetId);
                i.putExtra("widgetId", widgetId);
                i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }else {
                new Thread() {
                    public void run() {
                        try {
                            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
                            prefs.putLong("widgetClick" + widgetId, System.currentTimeMillis());
                            prefs.commit();
                            sleep(505);
                            SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
                            Long clickTimer = preferences.getLong("widgetClick" + widgetId, 0);
                            Looper.prepare();
                            System.out.println((System.currentTimeMillis() - clickTimer)+" click timer");
                            if ((System.currentTimeMillis() - clickTimer) > 500) {
                                sendIntentToService(context, widgetId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }else if(MyIntents.DRAW_STATS.equals(intent.getAction())){
            waitingForService =  false;
            Bundle b = intent.getExtras();
            DcrStats dcrStats = b.getParcelable("stats");
            int appWidgetId = b.getInt("appWidgetId");
            update(context, dcrStats, appWidgetId);
        }else if(MyIntents.DRAW_ERROR.equals(intent.getAction())){
            waitingForService =  false;
            int appWidgetId = intent.getExtras().getInt("appWidgetId");
            error(context, appWidgetId);
        }
    }

    void showProgressBar(Context context, int appWidgetId){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
            Configuration config = loadConfiguration(context, appWidgetId);
            RemoteViews views = applyConfiguration(config, context, appWidgetId);
            L.l("Calling Pending intent from showProgressBar:" + appWidgetId);
            views.setOnClickPendingIntent(R.id.simple_decred_widget,getPendingSelfIntent(context,ACTION_CLICK+""+appWidgetId, appWidgetId));
            views.setViewVisibility(R.id.content_text, View.GONE);
            views.setViewVisibility(R.id.progressBar, VISIBLE);
            appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void sendIntentToService(Context context, int appWidgetId) {
        waitingForService = true;
        showProgressBar(context, appWidgetId);
        Intent msgIntent = new Intent(context, DcrStatsService.class);
        msgIntent.putExtra("widgetId", appWidgetId);
        msgIntent.setAction(MyIntents.GET_STATS);
        context.startService(msgIntent);
    }
}