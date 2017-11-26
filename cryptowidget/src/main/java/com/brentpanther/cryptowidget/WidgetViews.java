package com.brentpanther.cryptowidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

class WidgetViews {

    private static final double TEXT_HEIGHT = .70;

    static void setText(Context context, RemoteViews views, String amount, int widgetId) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        String text = buildText(amount);
        prefs.setLastValue(text);
        putValue(context, views, text, widgetId);
    }

    static void setLastText(Context context, RemoteViews views, int widgetId) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        String lastValue = prefs.getLastValue();
        if (!TextUtils.isEmpty(lastValue)) {
            putValue(context, views, lastValue, widgetId);
        } else {
            //putValue(context, views, context.getString(R.string.value_unknown), widgetId);
            putValue(context, views, "--", widgetId);
        }
    }

    private static void putValue(Context context, RemoteViews views, String text, int widgetId) {
        Ids ids = WidgetApplication.getInstance().getIds();
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        int price = ids.price();
        setImageVisibility(views, ids, widgetId);
        Pair<Integer,Integer> size = getWidgetSize(context, widgetId);
        Pair<Integer, Integer> availableSize = getTextAvailableSize(context, ids, widgetId, size);
        if (availableSize == null) return;
        float textSize = TextSizer.getTextSize(context, text, availableSize);
        views.setTextViewText(price, text);
        if(text.equals(context.getString(R.string.value_unknown))){
            views.setTextViewTextSize(price, TypedValue.COMPLEX_UNIT_SP, 40);
        }else{
            views.setTextViewTextSize(price, TypedValue.COMPLEX_UNIT_SP, textSize);
        }
        show(views, ids.price(), ids.topText(), ids.bottomText());
        hide(views, ids.loading());
        int type = prefs.getType();
        String labelText = "", topText = "";
        switch (type){
            case 0:
                views.setTextViewText(ids.topText(),"");
                labelText = "BTC/DCR";
                break;
            case 1:
                views.setTextViewText(ids.topText(),"");
                labelText = "USD/DCR";
                break;
            case 2:
                views.setTextViewText(ids.topText(),"PoS Ticket");
                topText = "PoS Ticket";
                labelText = "CURRENT PRICE";
                break;
            case 3:
                topText = "PoS Ticket";
                labelText = "EST. NEXT PRICE";
                break;
            case 4:
                topText = "PoS Ticket";
                labelText = "NEXT WINDOW";
                break;
            case 5:
                topText = "PoW Network";
                labelText = "DIFFICULTY";
                break;
            case 6:
                topText = "PoW Network";
                labelText = "HASH RATE";
                break;
        }
        availableSize = getLabelAvailableSize(context, ids, widgetId,size);
        if(availableSize != null) {
            float labelSize = TextSizer.getLabelSize(context, labelText, availableSize);
            views.setTextViewText(ids.bottomText(), labelText);
            views.setTextViewTextSize(ids.bottomText(), TypedValue.COMPLEX_UNIT_DIP, labelSize);
        }
        availableSize = getTitleAvailableSize(context, ids, widgetId,size);
        if(availableSize != null) {
            float titleSize = TextSizer.getTextSize(context, labelText, availableSize);
            views.setTextViewText(ids.topText(), topText);
            views.setTextViewTextSize(ids.topText(), TypedValue.COMPLEX_UNIT_DIP, titleSize);
        }
        if(size.first <= 72){
            views.setViewVisibility(ids.image(), GONE);
            views.setViewVisibility(ids.imageSmall(), VISIBLE);
        }else{
            views.setViewVisibility(ids.image(), VISIBLE);
            views.setViewVisibility(ids.imageSmall(), GONE);
        }
        if(type >= 2 && size.first <= 72){
            views.setViewVisibility(ids.imageSmall(), GONE);
            views.setViewVisibility(ids.image(), GONE);
        }
        if(prefs.getIcon()){
            views.setViewVisibility(ids.image(), GONE);
            views.setViewVisibility(ids.imageSmall(), GONE);
        }
        show(views, price);
        if(prefs.getLabel()) {
            show(views, ids.topText(), ids.bottomText());
        }else{
            disappear(views,ids.topText(), ids.bottomText());
        }
        hide(views, ids.loading());
    }

    private static void setImageVisibility(RemoteViews views, Ids ids, int widgetId) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        boolean hideIcon = prefs.getIcon();
        if (hideIcon) {
            hide(views, ids.imageSmall());
            hide(views, ids.image());
        } else {
            hide(views, ids.imageSmall());
            show(views, ids.image());
        }
    }

    private static Pair<Integer, Integer> getTextAvailableSize(Context context, Ids ids, int widgetId,Pair<Integer, Integer> size) {
        //Pair<Integer, Integer> size = getWidgetSize(context, widgetId);
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        if (size == null) {
            return null;
        }
        int width = size.first - 10;
        int height = size.second - 10;

        if (!prefs.getIcon() && size.first > 72) {
            // icon is 25% of width
            width *= .75;
        }
        if (prefs.getLabel()) {
            height *= TEXT_HEIGHT;
        }
        return Pair.create((int)(width * .9), (int)(height * .85));
    }

    private static Pair<Integer, Integer> getLabelAvailableSize(Context context, Ids ids, int widgetId,Pair<Integer, Integer> size) {
//        Pair<Integer, Integer> size = getWidgetSize(context, widgetId);
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        if (size == null) {
            return null;
        }
        int height = size.second;
        int width = size.first;
        if (!prefs.getIcon()) {
            // icon is 25% of width
            width *= .75;
        }
        height *= ((1 - TEXT_HEIGHT) / 2);
        return Pair.create((int)(width * .9), (int)(height * .75));
    }

    private static Pair<Integer, Integer> getTitleAvailableSize(Context context, Ids ids, int widgetId,Pair<Integer, Integer> size) {
        Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
        if (size == null) {
            return null;
        }
        int height = size.second;
        int width = size.first;
        if (!prefs.getIcon()) {
            // icon is 25% of width
            width *= .75;
        }
        height *= ((1 - TEXT_HEIGHT) / 2);
        return Pair.create((int)(width * .9), (int)(height * .75));
    }

    private static Pair<Integer, Integer> getWidgetSize(Context context, int widgetId) {
        boolean portrait = context.getResources().getConfiguration().orientation == 1;
        String w = portrait ? AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH : AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;
        String h = portrait ? AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT : AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int width = appWidgetManager.getAppWidgetOptions(widgetId).getInt(w);
        int height = appWidgetManager.getAppWidgetOptions(widgetId).getInt(h);
        return Pair.create(width, height);
    }

    private static String buildText(String amount) {

        return amount;
    }

    static void setLoading(RemoteViews views, Ids ids, int widgetId) {
         Prefs prefs = WidgetApplication.getInstance().getPrefs(widgetId);
         show(views, ids.loading());
         views.setViewVisibility(ids.price(), View.INVISIBLE);
         views.setViewVisibility(ids.image(), GONE);
         views.setViewVisibility(ids.bottomText(), View.INVISIBLE);
         views.setViewVisibility(ids.topText(), View.INVISIBLE);
         if (!prefs.getIcon()) {
             views.setViewVisibility(ids.image(), View.INVISIBLE);
         }


    }

    private static void show(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, View.VISIBLE);
    }

    private static void disappear(RemoteViews views, int... ids){
        for (int id : ids) views.setViewVisibility(id, INVISIBLE);
    }

    private static void hide(RemoteViews views, int... ids) {
        for (int id : ids) views.setViewVisibility(id, GONE);
    }

    static void setOld(RemoteViews views, boolean isOld, Ids ids, boolean hideIcon) {
        show(views, ids.price());
        hide(views, ids.loading());
    }
    static int dpToPx(int dp, Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    static int pxToDp(int px, Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }
}
