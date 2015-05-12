package com.lvfq.rabbit;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import com.lvfq.rabbit.data.RabbitDataItem;

import java.util.List;
import java.util.ArrayList;

public class MainApplication extends Application {
    private final static String TAG="MainApplication";
    //global variables
    private List<RabbitDataItem> orderListRabbitData_NEWS=null;
    private List<RabbitDataItem> orderListRabbitData_DANCE=null;

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO Put your application initialization code here.
        Log.d(TAG, "onCreate");
    }

    public void setListRabbitDataItem_NEWS(List<RabbitDataItem> rabbitData) {
        orderListRabbitData_NEWS=rabbitData;
    }

    public List<RabbitDataItem> getListRabbitDataItem_NEWS() {
        return orderListRabbitData_NEWS;
    }

    public void setListRabbitDataItem_DANCE(List<RabbitDataItem> rabbitData) {
        orderListRabbitData_DANCE=rabbitData;
    }

    public List<RabbitDataItem> getListRabbitDataItem_DANCE() {
        return orderListRabbitData_DANCE;
    }
}