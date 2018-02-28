package com.simpledecredwidget;

import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.brentpanther.cryptowidget.Prefs;
import com.brentpanther.cryptowidget.WidgetApplication;

import static android.R.attr.label;

public class WidgetConfigurator extends PreferenceActivity {

    ListPreference widgetType;
    ListPreference theme;
    ListPreference refresh;
    CheckBoxPreference icon, labels;
    private int refreshValue = 30;
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
            addPreferencesFromResource(com.brentpanther.cryptowidget.R.xml.preference);
            refresh = (ListPreference) findPreference("refresh");
            widgetType = (ListPreference) findPreference("widgetType");
            theme = (ListPreference) findPreference("theme");
            icon = (CheckBoxPreference) findPreference("icon");
            labels = (CheckBoxPreference) findPreference("label");
            refresh.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference p, Object value) {
                    refreshValue = Integer.valueOf(value.toString());
                    switch (refreshValue){
                        case 5:
                            refresh.setSummary("5 Minutes");
                            break;
                        case 10:
                            refresh.setSummary("10 Minutes");
                            break;
                        case 20:
                            refresh.setSummary("20 Minutes");
                            break;
                        case 30:
                            refresh.setSummary("30 Minutes");
                            break;
                        case 0:
                            refresh.setSummary("Refresh will be done manually");
                        default:
                            refresh.setSummary("3 Minutes");
                    }
                    return true;
                }

            });
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
            labels.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(Boolean.valueOf(newValue.toString()) ? "Labels would be shown" : "Labels won't be shown");
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
            findPreference("source").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/C-ollins/Simple-Decred-Widget")));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/C-ollins/Simple-Decred-Widget")));
                    }
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

    private void save() {
        L.l("Refresh Value: "+refreshValue);
        Intent broadcast = new Intent(this, WidgetProvider.class);
        broadcast.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        broadcast.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        Prefs prefs = WidgetApplication.getInstance().getPrefs(appWidgetId);
        prefs.setValues(refreshValue,Integer.valueOf(widgetType.getValue()), "DCRSTATS",
                labels.isChecked(), theme.getValue(), icon.isChecked());
        sendBroadcast(broadcast);
        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, result);
        finish();
    }
}
