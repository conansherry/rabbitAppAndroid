package com.lvfq.rabbit.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

import com.lvfq.rabbit.activity.ImageActivity;
import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.*;
import com.lvfq.rabbit.util.ViewId;

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
        ((MainApplication)activity.getApplication()).setListRabbitDataItem_NEWS(nonOrderListRabbitData);
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
        //convertView=null;
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
        if(rabbitDataItem.spannableMaintext!=null) {
            holder.maintext.setMovementMethod(LinkMovementMethod.getInstance());
            holder.maintext.setText(rabbitDataItem.spannableMaintext);
        }
        if(rabbitDataItem.timetext!=null)
            holder.timetext.setText(rabbitDataItem.timetext);
        if(rabbitDataItem.thumbnail!=null)
            imageLoader.displayImage(rabbitDataItem.thumbnail, holder.thumbnail, thumbnailOptions);
        holder.extraInfo.removeAllViews();
        holder.extraInfo.setBackgroundColor(Color.WHITE);
        if(rabbitDataItem.extra!=null) {
            ImageView[] arrayOfImages=new ImageView[rabbitDataItem.extra.size()];
            int offset = 1;
            for(int i=0; i<Math.ceil(rabbitDataItem.extra.size() / 3.0); i++) {
                int jCount=3;
                if(i==(Math.ceil(rabbitDataItem.extra.size() / 3.0)-1) && rabbitDataItem.extra.size()%3!=0)
                    jCount=rabbitDataItem.extra.size()%3;
                for( int j=0; j<jCount; j++) {
                    arrayOfImages[i*3+j]=new ImageView(activity);
                    arrayOfImages[i*3+j].setId(offset + i * 3 + j);
                    arrayOfImages[i*3+j].setTag(rabbitDataItem.extra.get(i * 3 + j).replace("thumbnail", "small"));
                    final String bmiddleImage=rabbitDataItem.extra.get(i*3+j).replace("thumbnail", "large");
                    arrayOfImages[i*3+j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent enlargeIntent = new Intent(activity, ImageActivity.class);
                            enlargeIntent.putExtra("imageUrl",bmiddleImage);
                            activity.startActivity(enlargeIntent);
                        }
                    });
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.imageview_height), (int) activity.getResources().getDimension(R.dimen.imageview_height));
                    imageLoader.displayImage(rabbitDataItem.extra.get(i*3+j).replace("thumbnail", "small"), arrayOfImages[i*3+j], imageOptions);
                    params.setMargins(0,0,8,8);
                    if(i==0 && j==0) {
                        holder.extraInfo.addView(arrayOfImages[i*3+j], params);
                    }
                    else {
                        if(i!=0)
                            params.addRule(RelativeLayout.BELOW, arrayOfImages[(i-1)*3+j].getId());
                        if(j!=0)
                            params.addRule(RelativeLayout.RIGHT_OF, arrayOfImages[i*3+j-1].getId());
                        holder.extraInfo.addView(arrayOfImages[i*3+j], params);
                    }
                }
            }
        }
        else if(rabbitDataItem.retTitle!=null) {
            holder.extraInfo.setBackgroundColor(activity.getResources().getColor(R.color.littlegray));

            RelativeLayout.LayoutParams paddingParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paddingParams.setMargins(8, 8, 0, 4);

            TextView retTitle=new TextView(activity);
            retTitle.setText(rabbitDataItem.retTitle);
            retTitle.setId(ViewId.getInstance().getUniqueId());
            retTitle.setTextColor(activity.getResources().getColor(R.color.black));
            holder.extraInfo.addView(retTitle, paddingParams);

            TextView retMaintext=new TextView(activity);
            retMaintext.setText(rabbitDataItem.retSpannableMaintext);
            retMaintext.setId(ViewId.getInstance().getUniqueId());
            retMaintext.setTextColor(activity.getResources().getColor(R.color.black));
            retMaintext.setMovementMethod(LinkMovementMethod.getInstance());
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,4,0,4);
            params.addRule(RelativeLayout.BELOW, retTitle.getId());
            holder.extraInfo.addView(retMaintext, params);

            if(rabbitDataItem.retExtra!=null) {
                ImageView[] arrayOfImages=new ImageView[rabbitDataItem.retExtra.size()];
                int offset = 1;
                for(int i=0; i<Math.ceil(rabbitDataItem.retExtra.size() / 3.0); i++) {
                    int jCount=3;
                    if(i==(Math.ceil(rabbitDataItem.retExtra.size() / 3.0)-1) && rabbitDataItem.retExtra.size()%3!=0)
                        jCount=rabbitDataItem.retExtra.size()%3;
                    for( int j=0; j<jCount; j++) {
                        arrayOfImages[i*3+j]=new ImageView(activity);
                        arrayOfImages[i*3+j].setId(ViewId.getInstance().getUniqueId());
                        arrayOfImages[i*3+j].setTag(rabbitDataItem.retExtra.get(i * 3 + j).replace("thumbnail", "small"));
                        final String bmiddleImage=rabbitDataItem.retExtra.get(i*3+j).replace("thumbnail", "large");
                        arrayOfImages[i*3+j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent enlargeIntent = new Intent(activity, ImageActivity.class);
                                enlargeIntent.putExtra("imageUrl",bmiddleImage);
                                activity.startActivity(enlargeIntent);
                            }
                        });
                        RelativeLayout.LayoutParams paramsPics = new RelativeLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.imageview_height), (int) activity.getResources().getDimension(R.dimen.imageview_height));
                        imageLoader.displayImage(rabbitDataItem.retExtra.get(i*3+j).replace("thumbnail", "small"), arrayOfImages[i*3+j], imageOptions);
                        paramsPics.setMargins(8,4,0,4);
                        if(i==0)
                            paramsPics.addRule(RelativeLayout.BELOW, retMaintext.getId());
                        if(i==0 && j==0) {
                            holder.extraInfo.addView(arrayOfImages[i*3+j], paramsPics);
                        }
                        else {
                            if(i!=0)
                                paramsPics.addRule(RelativeLayout.BELOW, arrayOfImages[(i-1)*3+j].getId());
                            if(j!=0)
                                paramsPics.addRule(RelativeLayout.RIGHT_OF, arrayOfImages[i*3+j-1].getId());
                            holder.extraInfo.addView(arrayOfImages[i*3+j], paramsPics);
                        }
                    }
                }
            }
        }
        //end bind data to view

        return vi;
    }
}