package com.lvfq.rabbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.activity.PlayerActivity;
import com.lvfq.rabbit.data.RabbitDataItem;

import java.text.SimpleDateFormat;
import java.util.List;

public class RabbitDanceAdapter extends RabbitAdapter {

    private static final String TAG="RabbitDanceAdapter";

    private static LayoutInflater inflater;

    private SimpleDateFormat abstractTimeFormat;
    private SimpleDateFormat detailTimeFormat;

    public RabbitDanceAdapter(Activity a) {
        super(a);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        abstractTimeFormat = new SimpleDateFormat("MM-dd");
        detailTimeFormat = new SimpleDateFormat("HH-mm");

        orderListRabbitData=((MainApplication)activity.getApplication()).getListRabbitDataItem_DANCE();

        Log.d(TAG, "construct");
    }

    public void setRabbitData(List<RabbitDataItem> nonOrderListRabbitData) {
        ((MainApplication)activity.getApplication()).setListRabbitDataItem_DANCE(nonOrderListRabbitData);
        orderListRabbitData = nonOrderListRabbitData;
    }

        private static class ViewHolder {
        public ImageView thumbnail=null;
        public TextView title=null;
        public TextView maintext=null;
        public TextView timetext=null;
        public RelativeLayout extraInfo=null;
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
            holder.extraInfo = (RelativeLayout) vi.findViewById(R.id.gridimage);

            holder.thumbnail.setImageResource(R.drawable.video_thumb);

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
        final RabbitDataItem rabbitDataItem=getItem(position);
        if(rabbitDataItem.title!=null)
            holder.title.setText(rabbitDataItem.title);
        if(rabbitDataItem.maintext!=null)
            holder.maintext.setText(rabbitDataItem.maintext);
        if(rabbitDataItem.timetext!=null)
            holder.timetext.setText(rabbitDataItem.timetext);
        if(rabbitDataItem.thumbnail!=null) {
            ImageView imageView=new ImageView(activity);
            imageLoader.displayImage(rabbitDataItem.thumbnail, imageView, videoOptions);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, PlayerActivity.class);
                    intent.putExtra("vid", rabbitDataItem.retTitle);
                    activity.startActivity(intent);
                }
            });
            RelativeLayout.LayoutParams paramsPics = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.dance_imageview_width), (int) activity.getResources().getDimension(R.dimen.dance_imageview_height));
            holder.extraInfo.addView(imageView, paramsPics);
        }
        //end bind data to view

        return vi;
    }
}