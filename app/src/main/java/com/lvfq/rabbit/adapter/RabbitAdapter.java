package com.lvfq.rabbit.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.BaseAdapter;

import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.RabbitDataItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.List;

public abstract class RabbitAdapter extends BaseAdapter {
    //begin data
    protected List<RabbitDataItem> orderListRabbitData=null;
    //end data

    protected Activity activity;
    protected ImageLoader imageLoader;
    protected DisplayImageOptions thumbnailOptions;
    protected DisplayImageOptions imageOptions;
    protected DisplayImageOptions videoOptions;

    public RabbitAdapter(){}

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        bmp2=Bitmap.createScaledBitmap(bmp2, 50, 50, true);
        float x=(canvas.getWidth()-bmp2.getWidth())/2;
        float y=(canvas.getHeight()-bmp2.getHeight())/2;
        canvas.drawBitmap(bmp2, x, y, null);
        return bmOverlay;
    }

    public RabbitAdapter(Activity a) {
        activity=a;
        imageLoader = ImageLoader.getInstance();

        thumbnailOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blank)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        Log.d("TAG", "image process");
                        return Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    }
                })
                .displayer(new RoundedBitmapDisplayer(90))
                .build();

        imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blank)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        Log.d("TAG", "image process");
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if (width > height) {
                            int x = (width - height) / 2;
                            return Bitmap.createBitmap(bitmap, x, 0, height, height);
                        } else {
                            int y = (height - width) / 2;
                            return Bitmap.createBitmap(bitmap, 0, y, width, width);
                        }
                    }
                })
                .build();

        videoOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.blank_small)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.play);
                        Bitmap merge=overlay(bitmap, bm);
                        return merge;
                    }
                })
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