package com.lvfq.rabbit.Appcontext;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.content.Context;

import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.RabbitDataItem;

import com.lvfq.rabbit.util.SerializeTool;
import com.lvfq.rabbit.util.SpannableStringFactory;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.youku.player.YoukuPlayerBaseApplication;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MainApplication extends YoukuPlayerBaseApplication {
    private final static String TAG="MainApplication";
    //global variables
    private List<RabbitDataItem> orderListRabbitData_NEWS=null;
    private List<RabbitDataItem> orderListRabbitData_DANCE=null;

    private String version_name=null;

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO Put your application initialization code here.
        Log.d(TAG, "onCreate");
        try {
            version_name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SpannableStringFactory.context=getApplicationContext();
        SharedPreferences settings = getSharedPreferences(context.getString(R.string.app_name), 0);

        if(settings.getString("VERSION", "").isEmpty() || !settings.getString("VERSION", "").equals(version_name)) {
            settings.edit().clear().commit();
        }
        settings.edit().putString("VERSION", version_name).commit();
        String newsData = settings.getString("NEWS", "");
        String danceData = settings.getString("DANCE", "");
        try {
            if(newsData.isEmpty())
                orderListRabbitData_NEWS=null;
            else
                orderListRabbitData_NEWS=(List<RabbitDataItem>)SerializeTool.fromString(newsData);

            if(newsData.isEmpty())
                orderListRabbitData_DANCE=null;
            else
                orderListRabbitData_DANCE=(List<RabbitDataItem>)SerializeTool.fromString(danceData);
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
    }

    public String getVersion_name() {
        return version_name;
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
    /**
     * 通过覆写该方法，返回“正在缓存视频信息的界面”，
     * 则在状态栏点击下载信息时可以自动跳转到所设定的界面.
     * 用户需要定义自己的缓存界面
     */
    @Override
    public Class<? extends Activity> getCachingActivityClass() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 通过覆写该方法，返回“已经缓存视频信息的界面”，
     * 则在状态栏点击下载信息时可以自动跳转到所设定的界面.
     * 用户需要定义自己的已缓存界面
     */
    @Override
    public Class<? extends Activity> getCachedActivityClass() {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * 配置视频的缓存路径，格式举例： /appname/videocache/
     * 如果返回空，则视频默认缓存路径为： /应用程序包名/videocache/
     *
     */
    @Override
    public String configDownloadPath() {
        // TODO Auto-generated method stub

        //return "/myapp/videocache/";			//举例
        return null;
    }
}