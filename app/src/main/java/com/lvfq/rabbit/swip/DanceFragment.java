/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lvfq.rabbit.swip;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.util.Log;
import android.graphics.BitmapFactory;
import android.widget.BaseAdapter;

import com.lvfq.rabbit.R;
import com.lvfq.rabbit.adapter.RabbitAdapter;
import com.lvfq.rabbit.adapter.RabbitDanceAdapter;
import com.lvfq.rabbit.adapter.RabbitNewsAdapter;
import com.lvfq.rabbit.data.RabbitDataItem;
import com.lvfq.rabbit.util.HttpRequest;
import com.lvfq.rabbit.util.Base64;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;

/**
 * A sample which shows how to use {@link SwipeRefreshLayout} within a
 * {@link android.support.v4.app.ListFragment} to add the 'swipe-to-refresh' gesture to a
 * {@link android.widget.ListView}. This is provided through the provided re-usable
 * {@link SwipeRefreshListFragment} class.
 *
 * <p>To provide an accessible way to trigger the refresh, this app also provides a refresh
 * action item. This item should be displayed in the Action Bar's overflow item.
 *
 * <p>In this sample app, the refresh updates the ListView with a random set of new items.
 *
 * <p>This sample also provides the functionality to change the colors displayed in the
 * {@link SwipeRefreshLayout} through the options menu. This is meant to
 * showcase the use of color rather than being something that should be integrated into apps.
 */
public class DanceFragment extends SwipeRefreshListFragmentFragment {

    private static final int LIST_ITEM_COUNT = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rabbitAdapter=new RabbitDanceAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    // BEGIN_INCLUDE (initiate_refresh)
    /**
     * By abstracting the refresh process to a single method, the app allows both the
     * SwipeGestureLayout onRefresh() method and the Refresh action item to refresh the content.
     */
    protected void initiateRefresh() {
        super.initiateRefresh();
        /**
         * Execute the background task, which uses {@link AsyncTask} to load the data.
         */
        new DummyBackgroundTask().execute();
    }
    // END_INCLUDE (initiate_refresh)

    /**
     * Dummy {@link AsyncTask} which simulates a long running task to fetch new cheeses.
     */
    private class DummyBackgroundTask extends AsyncTask<Void, Void, List<RabbitDataItem>> {
        private static final String TAG="BackgoundTask";
        @Override
        protected List<RabbitDataItem> doInBackground(Void... params) {
            //request the server to get rabbit data
            List<RabbitDataItem> rabbitData=null;
            String result=HttpRequest.sendGet(getString(R.string.server), "");
            if(result!=null) {
                Log.d(TAG,"into add rabit item");
                rabbitData=new ArrayList<RabbitDataItem>();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("rabbitData");
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject oneRabbit=jsonArray.getJSONObject(i);
                        RabbitDataItem rabbitDataItem=new RabbitDataItem();
                        rabbitDataItem.title=oneRabbit.getString("title");
                        rabbitDataItem.maintext=oneRabbit.getString("maintext");
                        rabbitDataItem.timetext=oneRabbit.getString("timetext");
                        byte[] thumbnailBytes=null;
                        byte[] extraInfoBytes=null;
                        try {
                            thumbnailBytes=Base64.decode(oneRabbit.getString("thumbnail"));
                            extraInfoBytes=Base64.decode(oneRabbit.getString("picture"));
                        }
                        catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            thumbnailBytes=null;
                            extraInfoBytes=null;
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            thumbnailBytes=null;
                            extraInfoBytes=null;
                        }
                        if(thumbnailBytes!=null)
                            rabbitDataItem.thumbnail=BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                        else
                            rabbitDataItem.thumbnail=null;
                        if(extraInfoBytes!=null)
                            rabbitDataItem.extraInfo=BitmapFactory.decodeByteArray(extraInfoBytes, 0, extraInfoBytes.length);
                        else
                            rabbitDataItem.extraInfo=null;
                        rabbitData.add(rabbitDataItem);
                    }
                }
                catch (JSONException e) {
                    rabbitData=null;
                    Log.e(TAG, "JSONException");
                }
            }
            // Return rabbitdata
            return rabbitData;
        }

        @Override
        protected void onPostExecute(List<RabbitDataItem> result) {
            super.onPostExecute(result);
            // Tell the Fragment that the refresh has completed
            onRefreshComplete(result);
        }
    }
}
