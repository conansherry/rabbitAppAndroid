package com.lvfq.rabbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lvfq.rabbit.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.RabbitDataItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class RabbitAdapter extends BaseAdapter {
    //begin data
    protected List<RabbitDataItem> orderListRabbitData=null;
    //end data

    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;

    public RabbitAdapter() {
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.NONE)
                    .displayer(new RoundedBitmapDisplayer(90))
                    .build();
    }

    public void setRabbitData(List<RabbitDataItem> nonOrderListRabbitData) {
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