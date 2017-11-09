package com.simpledecredwidget.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simpledecredwidget.MyIntents;
import com.simpledecredwidget.L;
import com.simpledecredwidget.SimpleDecredWidget;

public class DcrStatsService extends IntentService {
    private final static DcrStatsUrl DCR_STATS_URL = new DcrStatsUrl("https://dcrstats.com");
    //private final static DcrStatsUrl DCR_STATS_URL = new DcrStatsUrl("http://10.0.2.2:8090");
    private final static int TIMEOUT_MS = 10000;
    private final static int MAX_RETRIES = 0;
    private int appWidgetId = 0;

    public DcrStatsService() {
        super("DcrStatsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        appWidgetId = intent.getExtras().getInt("widgetId");
        L.l("Service started: "+appWidgetId);
        getStats();
    }

    public void sendErrorToWidget() {
        Intent i = new Intent(this.getApplicationContext(),SimpleDecredWidget.class);
        i.setAction(MyIntents.DRAW_ERROR);
        i.putExtra("appWidgetId",appWidgetId);
        this.getApplicationContext().sendBroadcast(i);
    }

    public void sendStatsToWidget(DcrStats stats) {
        Intent i = new Intent(this.getApplicationContext(), SimpleDecredWidget.class);
        i.setAction(MyIntents.DRAW_STATS);
        i.putExtra("stats", stats);
        i.putExtra("appWidgetId",appWidgetId);
        this.getApplicationContext().sendBroadcast(i);
    }

    public void getStats() {
        RequestQueue queue = Volley.newRequestQueue(this);
        L.l("Service sending GET to " + DCR_STATS_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DCR_STATS_URL.toString(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                   L.l("Service received non-error response\n" + response);
                    DcrStats stats = new DcrStats(response);
                    sendStatsToWidget(stats);
                }
            },
            new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                L.l("Service received error response: ");
                error.printStackTrace();
                sendErrorToWidget();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}