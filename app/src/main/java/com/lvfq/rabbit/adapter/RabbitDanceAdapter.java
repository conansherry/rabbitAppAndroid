package com.lvfq.rabbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.activity.PlayerActivity;
import com.lvfq.rabbit.data.RabbitDataItem;
import com.lvfq.rabbit.util.SerializeTool;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        try {
            String storage = SerializeTool.toString(new ArrayList<RabbitDataItem>(nonOrderListRabbitData.subList(0, 50)));
            // We need an Editor object to make preference changes.
            // All objects are from android.context.Context
            SharedPreferences settings = activity.getSharedPreferences(activity.getString(R.string.app_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("DANCE", storage);
            // Commit the edits!
            editor.commit();
            Log.d("TAG", "持久化成功");
        } catch(IOException e) {
            e.printStackTrace();
        }
        super.setRabbitData(nonOrderListRabbitData);
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

            vi.setLongClickable(true);

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
        if(rabbitDataItem.maintext!=null) {
            holder.maintext.setText(rabbitDataItem.maintext);
            vi.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, rabbitDataItem.maintext+" 视频地址:http://v.youku.com/v_show/id_"+rabbitDataItem.retTitle);
                    sendIntent.setType("text/plain");
                    activity.startActivity(sendIntent);
                    return false;
                }
            });
        }
        if(rabbitDataItem.timetext!=null)
            holder.timetext.setText(rabbitDataItem.timetext);
        if(rabbitDataItem.thumbnail!=null) {
            FrameLayout frameLayout=(FrameLayout) inflater.inflate(R.layout.dance_thumb, null);
            final ImageView imageView=(ImageView)frameLayout.findViewById(R.id.dance_thumbnail_image);
            imageLoader.displayImage(rabbitDataItem.thumbnail, imageView, videoOptions);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, PlayerActivity.class);
                    intent.putExtra("vid", rabbitDataItem.retTitle);
                    activity.startActivity(intent);
                }
            });
            TextView duration=(TextView)frameLayout.findViewById(R.id.dance_duration);
            long seconds=(long)rabbitDataItem.duration;
            long hours=seconds/3600;
            long minutes=seconds%3600/60;
            long secs=seconds%60;
            DecimalFormat decimalFormat=new DecimalFormat("00");
            duration.setText((hours!=0?decimalFormat.format(hours)+":":"")+(minutes!=0?decimalFormat.format(minutes)+":":"")+decimalFormat.format(secs));
            holder.extraInfo.addView(frameLayout);
        }
        //end bind data to view

        return vi;
    }
}