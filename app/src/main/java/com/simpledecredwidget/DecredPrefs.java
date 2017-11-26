package com.simpledecredwidget;

import android.content.Context;
import android.content.SharedPreferences;

import com.brentpanther.cryptowidget.Exchange;
import com.brentpanther.cryptowidget.Prefs;

public class DecredPrefs extends Prefs {

    protected DecredPrefs(Context context, int widgetId) {
        super(context, widgetId);
    }

    @Override
    protected SharedPreferences getPrefs() {
        return context.getSharedPreferences(context.getString(R.string.prefs_key), Context.MODE_PRIVATE);
    }

    @Override
    public int getInterval() {
        String value = getValue(REFRESH);
        if (value == null) value = "3";
        return Integer.valueOf(value);
    }

    @Override
    public Exchange getExchange() {
        String value = getValue(EXCHANGE);
        if (value == null) value = "DCRSTATS";
        return DCRExchange.valueOf(value);
    }

    @Override
    public int getThemeLayout() {
        String value = getValue(THEME);
        if(value == null) value = "0";
        return value.equals("0") ? R.layout.simple_decred_widget : R.layout.simple_decred_widget_dark;
    }
}
