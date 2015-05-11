package com.lvfq.rabbit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.lvfq.rabbit.data.*;

public class RabbitNewsAdapter extends BaseAdapter {

    private static final String TAG="RabbitNewsAdapter";

    private Activity activity;
    private static LayoutInflater inflater;

    private SimpleDateFormat abstractTimeFormat;
    private SimpleDateFormat detailTimeFormat;

    //begin data
    private static List<RabbitDataItem> orderListRabbitData=null;
    //end data

    public RabbitNewsAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        abstractTimeFormat = new SimpleDateFormat("MM-dd");
        detailTimeFormat = new SimpleDateFormat("HH-mm");

        if(orderListRabbitData==null) {
            orderListRabbitData = new ArrayList<RabbitDataItem>();

            orderListRabbitData.add(new RabbitDataItem());
            orderListRabbitData.add(new RabbitDataItem());
            orderListRabbitData.add(new RabbitDataItem());
        }

        Log.d(TAG, "construct");
    }

    public void setRabbitData(List<RabbitDataItem> nonOrderListRabbitData) {
        if(nonOrderListRabbitData != null) {
            //Collections.sort(nonOrderListRabbitData, new ComparatorOfRabbitDataItem());
        }
        orderListRabbitData = nonOrderListRabbitData;
    }

    public int getCount() {
        if(orderListRabbitData != null)
            return orderListRabbitData.size();
        else
            return 0;
    }

    public Object getItem(int position) {
        if(orderListRabbitData != null)
            return orderListRabbitData.get(position);
        else
            return position;
    }

    public int getPosition(Object item) {
        if(orderListRabbitData != null)
            return orderListRabbitData.indexOf(item);
        else
            return 0;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        //begein bind data to view
        TextView title=(TextView)vi.findViewById(R.id.title);
        TextView maintext=(TextView)vi.findViewById(R.id.maintext);
        TextView timetext=(TextView)vi.findViewById(R.id.timetext);
        ImageView thumbnail=(ImageView)vi.findViewById(R.id.thumbnail);

        RabbitDataItem rabbitDataItem=orderListRabbitData.get(position);
        if(rabbitDataItem.title!=null)
            title.setText(rabbitDataItem.title);
        if(rabbitDataItem.maintext!=null)
            maintext.setText(rabbitDataItem.maintext);
        if(rabbitDataItem.timetext!=null)
            timetext.setText(rabbitDataItem.timetext);
        if(rabbitDataItem.thumbnail!=null)
            thumbnail.setImageBitmap(rabbitDataItem.thumbnail);
        //end bind data to view

        return vi;
    }
}