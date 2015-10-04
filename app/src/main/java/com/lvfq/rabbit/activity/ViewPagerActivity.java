package com.lvfq.rabbit.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lvfq.rabbit.ImageView.ExtendedViewPager;
import com.lvfq.rabbit.ImageView.TouchImageView;
import com.lvfq.rabbit.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class ViewPagerActivity extends Activity {
    private static final String TAG="ViewPagerActivity";
	/**
	 * Step 1: Download and set up v4 support library: http://developer.android.com/tools/support-library/setup.html
	 * Step 2: Create ExtendedViewPager wrapper which calls TouchImageView.canScrollHorizontallyFroyo
	 * Step 3: ExtendedViewPager is a custom view and must be referred to by its full package name in XML
	 * Step 4: Write TouchImageAdapter, located below
	 * Step 5. The ViewPager in the XML should be ExtendedViewPager
	 */

    private ProgressBar rabbitProgressBar;

    protected DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.NONE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        rabbitProgressBar=(ProgressBar)findViewById(R.id.imageview_progressbar);
        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        TouchImageAdapter imageAdapter = new TouchImageAdapter();
        imageAdapter.imageUrls = getIntent().getStringArrayExtra("imageUrls");
        int index = getIntent().getIntExtra("imageIndex", 0);
        mViewPager.setAdapter(imageAdapter);
        mViewPager.setCurrentItem(index);

        Log.d(TAG, "index:"+index);
    }

    class TouchImageAdapter extends PagerAdapter {

        public String[] imageUrls;

        @Override
        public int getCount() {
        	return imageUrls.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final TouchImageView img = new TouchImageView(container.getContext());
            ImageLoader.getInstance().loadImage(imageUrls[position], imageOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    rabbitProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    rabbitProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    img.setImageBitmap(loadedImage);
                    rabbitProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    rabbitProgressBar.setVisibility(View.GONE);
                }
            });
            //img.setImageResource(images[position]);
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
