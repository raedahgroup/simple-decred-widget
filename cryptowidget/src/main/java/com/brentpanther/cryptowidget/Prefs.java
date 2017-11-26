package com.brentpanther.cryptowidget;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Prefs {

    protected static final String LAST_UPDATE = "last_update"; //Last update time
    protected static final String REFRESH = "refresh";//refresh interval
    protected static final String EXCHANGE = "exchange";//api name
    protected static final String THEME = "theme";//dark or light theme
    protected static final String HIDE_ICON = "icon";//Decred Logo
    protected static final String LAST_VALUE = "last_value";//Last loaded value
    protected static final String SHOW_LABEL = "show_label";//Show api name
    protected static final String TYPE = "type";//Widget Type
    protected static final String LAST_CLICKED = "last_clicked";
    protected int widgetId;
    protected Context context;

    protected abstract SharedPreferences getPrefs();

    protected Prefs(Context context, int widgetId) {
        this.widgetId = widgetId;
        this.context = context;
    }

    long getLastUpdate() {
		String value = getValue(LAST_UPDATE);
        if(value==null) return 0;
        return Long.valueOf(value);
	}

	void setLastUpdate() {
        setValue(LAST_UPDATE, "" + System.currentTimeMillis());
	}

    void setLastValue(String value) {
        setValue(LAST_VALUE, value);
    }

    String getLastValue() {
        return getValue(LAST_VALUE);
    }

    public abstract int getInterval();

    public abstract Exchange getExchange();

    boolean getLabel() {
        return Boolean.valueOf(getValue(SHOW_LABEL));
    }

    boolean getIcon() {
        return Boolean.valueOf(getValue(HIDE_ICON));
    }

    int getType(){
        int type = 0;
        try{
            type = Integer.valueOf(getValue(TYPE));
        }catch (Exception e){
        }
        return type;
    }

    public abstract int getThemeLayout();

    public int getTheme(){
        String theme = getValue(THEME);
        try{
            return Integer.parseInt(theme);
        }catch (Exception e){}
        return 0;
    }

    protected void setValue(String key, String value) {
        String string = getPrefs().getString("" + widgetId, null);
        JSONObject obj;
        try {
            if(string==null) {
                obj = new JSONObject();
            }  else {
                obj = new JSONObject(string);
            }
            obj.put(key, value);
            getPrefs().edit().putString("" + widgetId, obj.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	public void setValues(int refreshValue,int type,
                          String exchange, boolean checked, String theme, boolean iconChecked) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(REFRESH, "" + refreshValue);
            obj.put(TYPE, type);
            obj.put(EXCHANGE, exchange);
            obj.put(SHOW_LABEL, "" + checked);
            obj.put(THEME, theme);
            obj.put(HIDE_ICON, "" + !iconChecked);
            getPrefs().edit().putString("" + widgetId, obj.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

    void delete() {
		getPrefs().edit().remove("" + widgetId).apply();
	}
    void setLastClicked(long lastClicked){
        setValue(LAST_CLICKED,""+lastClicked);
    }

    long getLastClicked(){
        String value = getValue(LAST_CLICKED);
        if(value == null){
            return 0;
        }
        return Long.parseLong(value);
    }

    protected String getValue(String key) {
        String string = getPrefs().getString("" + widgetId, null);
        if(string==null) return null;
        JSONObject obj;
        try {
            obj = new JSONObject(string);
            return obj.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

}
