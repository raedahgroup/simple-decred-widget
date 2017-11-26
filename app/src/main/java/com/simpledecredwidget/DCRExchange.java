package com.simpledecredwidget;

import com.brentpanther.cryptowidget.Exchange;

import org.json.JSONObject;
import java.util.Random;
import static com.brentpanther.cryptowidget.ExchangeHelper.getJSONObject;

public enum  DCRExchange implements Exchange{
    DCRSTATS() {
        @Override
        public String getValue() throws Exception {
            JSONObject obj = getJSONObject("https://dcrstats.com/api/v1/get_stats?origin=jamie_holdstocks_app&nonce="+String.format("%010d", new Random().nextInt(Integer.MAX_VALUE)));
            return obj.toString();
        }
    }
}
