package com.simpledecredwidget;

import android.content.Context;
import android.content.res.Resources;

import com.brentpanther.cryptowidget.Ids;
import com.brentpanther.cryptowidget.Prefs;
import com.brentpanther.cryptowidget.WidgetApplication;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ConfigurationBuilder;

/**
 * Created by Collins on 11/18/2017.
 */
@ReportsCrashes(formUri = "https://decred-widget-crash.herokuapp.com/logs/SimpleDecredWidget",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogTheme = R.style.AppTheme_Dialog
)
public class DecredWidgetApplication extends WidgetApplication {

    private DecredIds ids;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
//            final ACRAConfiguration config = new ConfigurationBuilder(this)
//                    .setSendReportsInDevMode(true)
//                    .setFormUri("https://decred-widget-crash.herokuapp.com/logs/SimpleDecredWidget")
//                    .build();
            ACRA.init(this);
            L.l("ACRA INIT SUCCESS");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ids = new DecredIds();
    }

    @Override
    public Ids getIds() {
        return ids;
    }

    @Override
    public Prefs getPrefs(int widgetId) {
        return new DecredPrefs(this, widgetId);
    }

    @Override
    public Class getWidgetProvider() {
        return WidgetProvider.class;
    }
}