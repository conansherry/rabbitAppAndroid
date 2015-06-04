package com.lvfq.rabbit.adapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

import com.lvfq.rabbit.activity.ImageActivity;
import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.*;
import com.lvfq.rabbit.util.SerializeTool;
import com.lvfq.rabbit.util.SpannableStringFactory;
import com.lvfq.rabbit.util.ViewId;

public class RabbitNewsAdapter extends RabbitAdapter {

    private static final String TAG="RabbitNewsAdapter";

    private static LayoutInflater inflater;

    private SimpleDateFormat abstractTimeFormat;
    private SimpleDateFormat detailTimeFormat;

    //缓存SpannableString
    private Map<Long, SpannableString> cacheSpannableString=new HashMap<Long, SpannableString>();

    public RabbitNewsAdapter(Activity a) {
        super(a);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        abstractTimeFormat = new SimpleDateFormat("MM-dd");
        detailTimeFormat = new SimpleDateFormat("HH-mm");

        orderListRabbitData = ((MainApplication)activity.getApplication()).getListRabbitDataItem_NEWS();

        Log.d(TAG, "construct");
}

    public void setRabbitData(List<RabbitDataItem> nonOrderListRabbitData) {
        ((MainApplication)activity.getApplication()).setListRabbitDataItem_NEWS(nonOrderListRabbitData);
        try {
            String storage = SerializeTool.toString(new ArrayList<RabbitDataItem>(nonOrderListRabbitData.subList(0, 10)));
            // We need an Editor object to make preference changes.
            // All objects are from android.context.Context
            SharedPreferences settings = activity.getSharedPreferences(activity.getString(R.string.app_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("NEWS", storage);
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
            holder.maintext.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableString mainTextSpannableString=null;
            if(cacheSpannableString.containsKey(rabbitDataItem.id))
                mainTextSpannableString=cacheSpannableString.get(rabbitDataItem.id);
            else {
                mainTextSpannableString=SpannableStringFactory.createSpannableText(rabbitDataItem.maintext);
                cacheSpannableString.put(rabbitDataItem.id, mainTextSpannableString);
            }
            holder.maintext.setText(mainTextSpannableString);

            vi.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, rabbitDataItem.maintext);
                    sendIntent.setType("text/plain");
                    activity.startActivity(sendIntent);
                    return false;
                }
            });
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

            SpannableString retMainTextSpannableString=null;
            if(cacheSpannableString.containsKey(rabbitDataItem.retId))
                retMainTextSpannableString=cacheSpannableString.get(rabbitDataItem.retId);
            else {
                retMainTextSpannableString=SpannableStringFactory.createSpannableText(rabbitDataItem.retMaintext);
                cacheSpannableString.put(rabbitDataItem.retId, retMainTextSpannableString);
            }
            retMaintext.setText(retMainTextSpannableString);

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