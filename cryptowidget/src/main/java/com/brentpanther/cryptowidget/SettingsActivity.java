package com.brentpanther.cryptowidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

    ListPreference widgetType;
    ListPreference theme;
    ListPreference refresh;
    CheckBoxPreference icon, labels;
    private int refreshValue;
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
            refresh = (ListPreference) findPreference("refresh");
            widgetType = (ListPreference) findPreference("widgetType");
            theme = (ListPreference) findPreference("theme");
            icon = (CheckBoxPreference) findPreference("icon");
            labels = (CheckBoxPreference) findPreference("label");
            labels.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(Boolean.valueOf(newValue.toString()) ? "Labels would be shown" : "Labels won't be shown");
                    return true;
                }
            });
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
            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(getResources().getStringArray(R.array.themes)[Integer.valueOf(newValue.toString())]);
                    return true;
                }
            });
            Prefs prefs = WidgetApplication.getInstance().getPrefs(appWidgetId);
            refresh.setValue(prefs.getInterval()+"");
            refresh.getOnPreferenceChangeListener().onPreferenceChange(refresh, prefs.getInterval());
            widgetType.setValue(prefs.getType()+"");
            widgetType.getOnPreferenceChangeListener().onPreferenceChange(widgetType, prefs.getType());
            icon.setChecked(!prefs.getIcon());
            icon.getOnPreferenceChangeListener().onPreferenceChange(icon,!prefs.getIcon());
            theme.setValue(prefs.getTheme()+"");
            theme.getOnPreferenceChangeListener().onPreferenceChange(theme, prefs.getTheme());
            labels.setChecked(prefs.getLabel());
            labels.getOnPreferenceChangeListener().onPreferenceChange(labels,prefs.getLabel());
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
        Intent broadcast;
        try {
            broadcast = new Intent(this, Class.forName("com.simpledecredwidget.WidgetProvider"));
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
