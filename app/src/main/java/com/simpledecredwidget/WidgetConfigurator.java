package com.simpledecredwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class WidgetConfigurator extends PreferenceActivity {

    private final String PREFS_NAME = "com.simpledecredwidget.WidgetConfigurator";
    ListPreference widgetType;
    ListPreference theme;
    CheckBoxPreference icon;
    private int appWidgetId = -1;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        Bundle extras = getIntent().getExtras();
        appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if(extras != null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            addPreferencesFromResource(R.xml.preference);
            widgetType = (ListPreference) findPreference("widgetType");
            theme = (ListPreference) findPreference("theme");
            icon = (CheckBoxPreference) findPreference("icon");
            widgetType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(getResources().getStringArray(R.array.widgetTypes)[Integer.valueOf(newValue.toString())]);
                    return true;
                }
            });
            icon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(Boolean.valueOf(newValue.toString()) ? "Decred logo would be shown" : "Decred logo won't be shown");
                    return true;
                }
            });
            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(getResources().getStringArray(R.array.themes)[Integer.valueOf(newValue.toString())]);
                    return true;
                }
            });
        }else{
            finish();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0, 0, 0, "Save");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) save();
        return true;
    }

    public void save() {
        SharedPreferences.Editor prefs = getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(appWidgetId+"type",Integer.valueOf(widgetType.getValue()));
        prefs.putBoolean(appWidgetId+"logo",icon.isChecked());
        prefs.putInt(appWidgetId+"theme", Integer.valueOf(theme.getValue()));
        prefs.commit();
        L.l("Saving widget configuration: "+appWidgetId);
        Intent intent = new Intent(this, SimpleDecredWidget.class);
        intent.setAction(MyIntents.UPDATE);
        intent.putExtra("widgetId",appWidgetId);
        sendBroadcast(intent);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
