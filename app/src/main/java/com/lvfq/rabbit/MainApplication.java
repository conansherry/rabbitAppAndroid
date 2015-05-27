package com.lvfq.rabbit;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;
import android.content.Context;

import com.lvfq.rabbit.data.RabbitDataItem;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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

        initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
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

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}