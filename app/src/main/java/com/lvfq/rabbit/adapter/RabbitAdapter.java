package com.lvfq.rabbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.RabbitDataItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class RabbitAdapter extends BaseAdapter {
    //begin data
    protected List<RabbitDataItem> orderListRabbitData=null;
    //end data

    public void setRabbitData(List<RabbitDataItem> nonOrderListRabbitData) {
        if(nonOrderListRabbitData != null) {
            //Collections.sort(nonOrderListRabbitData, new ComparatorOfRabbitDataItem());
        }
        orderListRabbitData = nonOrderListRabbitData;
    }

    @Override
    public int getCount() {
        if(orderListRabbitData != null)
            return orderListRabbitData.size();
        else
            return 0;
    }

    public RabbitDataItem getItem(int position) {
        if(orderListRabbitData != null)
            return orderListRabbitData.get(position);
        else
            return null;
    }

    public int getPosition(RabbitDataItem item) {
        if(orderListRabbitData != null)
            return orderListRabbitData.indexOf(item);
        else
            return 0;
    }

    public long getItemId(int position) {
        return position;
    }
}