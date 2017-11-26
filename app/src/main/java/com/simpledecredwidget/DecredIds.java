package com.simpledecredwidget;

import com.brentpanther.cryptowidget.Ids;

/**
 * Created by Collins on 11/18/2017.
 */

public class DecredIds extends Ids {
    @Override
    protected int widgetLayout() {
        return R.layout.simple_decred_widget;
    }

    @Override
    protected int widgetLayoutDark() {
        return R.layout.simple_decred_widget_dark;
    }

    @Override
    protected int price() {
        return R.id.content_text;
    }

    @Override
    protected int provider() {
        return 0;
    }

    @Override
    protected int topText() {
        return R.id.top_text;
    }

    @Override
    protected int parent() {
        return R.id.simple_decred_widget;
    }

    @Override
    protected int bottomText() {
        return R.id.bottom_text;
    }

    @Override
    protected int loading() {
        return R.id.progressBar;
    }

    @Override
    protected int image() {
        return R.id.currency_icon;
    }

    @Override
    protected int imageSmall() {
        return R.id.currency_icon_small;
    }

}
