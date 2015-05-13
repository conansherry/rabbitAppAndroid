package com.lvfq.rabbit.adapter;

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

import com.lvfq.rabbit.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.*;

public class RabbitNewsAdapter extends RabbitAdapter {

    private static final String TAG="RabbitNewsAdapter";

    private Activity activity;
    private static LayoutInflater inflater;

    private SimpleDateFormat abstractTimeFormat;
    private SimpleDateFormat detailTimeFormat;

    public RabbitNewsAdapter(Activity a) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        abstractTimeFormat = new SimpleDateFormat("MM-dd");
        detailTimeFormat = new SimpleDateFormat("HH-mm");

        orderListRabbitData = ((MainApplication)activity.getApplication()).getListRabbitDataItem_NEWS();

        Log.d(TAG, "construct");
    }

    public void setRabbitData(List<RabbitDataItem> nonOrderListRabbitData) {
        if(nonOrderListRabbitData != null) {
            //Collections.sort(nonOrderListRabbitData, new ComparatorOfRabbitDataItem());
        }
        ((MainApplication)activity.getApplication()).setListRabbitDataItem_NEWS(nonOrderListRabbitData);
        orderListRabbitData = nonOrderListRabbitData;
    }

    private static class ViewHolder {
        public ImageView thumbnail=null;
        public TextView title=null;
        public TextView maintext=null;
        public TextView timetext=null;
        public ImageView extraInfo=null;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder=null;
        if(convertView==null) {
            //The view is not a recycled one: we have to inflate
            vi = inflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();

            //begein bind data to view
            holder.title = (TextView) vi.findViewById(R.id.title);
            holder.maintext = (TextView) vi.findViewById(R.id.maintext);
            holder.timetext = (TextView) vi.findViewById(R.id.timetext);
            holder.thumbnail = (ImageView) vi.findViewById(R.id.thumbnail);

            vi.setTag(holder);

            Log.d(TAG, "The view is not a recycled one: we have to inflate");
        }
        else {
            // view recycled !
            // no need to inflate
            // no need to findViews by id
            holder = (ViewHolder)vi.getTag();
            Log.d(TAG, "view recycled");
        }
        RabbitDataItem rabbitDataItem=getItem(position);
        if(rabbitDataItem.title!=null)
            holder.title.setText(rabbitDataItem.title);
        if(rabbitDataItem.maintext!=null)
            holder.maintext.setText(rabbitDataItem.maintext);
        if(rabbitDataItem.timetext!=null)
            holder.timetext.setText(rabbitDataItem.timetext);
        if(rabbitDataItem.thumbnail!=null)
            holder.thumbnail.setImageBitmap(rabbitDataItem.thumbnail);
        //end bind data to view

        return vi;
    }
}