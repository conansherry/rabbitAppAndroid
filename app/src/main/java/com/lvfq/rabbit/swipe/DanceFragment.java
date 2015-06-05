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

package com.lvfq.rabbit.swipe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.adapter.RabbitDanceAdapter;
import com.lvfq.rabbit.data.RabbitDataItem;
import com.lvfq.rabbit.util.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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

    private static final String TAG="DanceFragment";

    private List<RabbitDataItem> rabbitData=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rabbitAdapter=new RabbitDanceAdapter(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.d(TAG, "first:"+firstVisibleItem+" visible:"+visibleItemCount+" total:"+totalItemCount+" last:"+listView.getLastVisiblePosition());
                if (listView.getLastVisiblePosition() >=0 && listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1
                        && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {
                    Log.d(TAG, "load more");
                    new DanceLoadMoreBackgroundTask().execute();
                }
            }
        }));

        if(rabbitAdapter.orderListRabbitData==null)
            initiateRefresh();
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
        new DanceRefreshBackgroundTask().execute();
    }
    // END_INCLUDE (initiate_refresh)

    private RabbitDataItem createRabbitDataItem(JSONObject oneRabbit) {
        try {
            RabbitDataItem rabbitDataItem = new RabbitDataItem();
            rabbitDataItem.title = getString(R.string.app_name);
            rabbitDataItem.maintext = oneRabbit.getString("title");
            rabbitDataItem.retTitle = oneRabbit.getString("id");
            rabbitDataItem.timetext = oneRabbit.getString("published");
            rabbitDataItem.thumbnail = oneRabbit.getString("thumbnail");

            return rabbitDataItem;
        } catch (JSONException e) {
            Log.e(TAG, "JSONException");
            return null;
        }
    }

    /**
     * Dummy {@link AsyncTask} which simulates a long running task to fetch new cheeses.
     */
    private class DanceRefreshBackgroundTask extends AsyncTask<Void, Void, List<RabbitDataItem>> {
        private static final String TAG="RefreshDance";
        private Boolean hasMore=false;
        @Override
        protected List<RabbitDataItem> doInBackground(Void... params) {
            //request the server to get rabbit data
            rabbitData=new ArrayList<RabbitDataItem>(rabbitAdapter.orderListRabbitData);
            if(rabbitData==null) {
                String result = HttpRequest.sendGet(getString(R.string.dance_server), "client_id=459086bb819ff72d&user_id=UOTcwNjUxMjQ=&count=50&page=1");
                if (result != null) {
                    Log.d(TAG, "into add rabit item. empty data.");
                    rabbitData = new ArrayList<RabbitDataItem>();
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        JSONArray jsonArray=jsonObject.getJSONArray("videos");
                        if(jsonArray.length()>0)
                            hasMore=true;
                        else
                            hasMore=false;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject oneRabbit = jsonArray.getJSONObject(i);
                            RabbitDataItem rabbitDataItem = createRabbitDataItem(oneRabbit);
                            rabbitData.add(rabbitDataItem);
                        }
                    } catch (JSONException e) {
                        rabbitData = null;
                        Log.e(TAG, "JSONException");
                    }
                }
            }
            else {
                String result = HttpRequest.sendGet(getString(R.string.dance_server), "client_id=459086bb819ff72d&user_id=UOTcwNjUxMjQ=&count=50&page=1");
                if (result != null) {
                    Log.d(TAG, "into add rabit item");
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        JSONArray jsonArray=jsonObject.getJSONArray("videos");
                        hasMore=false;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject oneRabbit = jsonArray.getJSONObject(i);
                            if(oneRabbit.getString("id").equals(rabbitData.get(0).retTitle)) {
                                Log.d(TAG, "break");
                                break;
                            }
                            RabbitDataItem rabbitDataItem = createRabbitDataItem(oneRabbit);
                            rabbitData.add(0, rabbitDataItem);
                            hasMore=true;
                        }
                    } catch (JSONException e) {
                        rabbitData = null;
                        Log.e(TAG, "JSONException");
                    }
                }
            }
            // Return rabbitdata
            return rabbitData;
        }

        @Override
        protected void onPostExecute(List<RabbitDataItem> result) {
            super.onPostExecute(result);
            // Tell the Fragment that the refresh has completed
            onRefreshComplete(result, hasMore);
        }
    }

    private class DanceLoadMoreBackgroundTask extends AsyncTask<Void, Void, List<RabbitDataItem>> {
        private static final String TAG="LoadMoreNews";
        private Boolean hasMore=false;
        @Override
        protected List<RabbitDataItem> doInBackground(Void... params) {
            //request the server to get rabbit data
            int pageId=1;
            int total=0;
            rabbitData=new ArrayList<RabbitDataItem>(rabbitAdapter.orderListRabbitData);
            String result = HttpRequest.sendGet(getString(R.string.dance_server), "client_id=459086bb819ff72d&user_id=UOTcwNjUxMjQ=&count=100&page="+pageId);
            JSONObject jsonObject=null;
            if (result != null) {
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("total") == rabbitData.size()) {
                        hasMore = false;
                    } else {
                        hasMore = true;
                        rabbitData.clear();
                    }
                } catch (JSONException e) {
                    rabbitData = null;
                    hasMore=false;
                    Log.e(TAG, "JSONException");
                }
            }
            while(hasMore) {
                if (result != null) {
                    Log.d(TAG, "into add rabit item");
                    try {
                        JSONArray jsonArray=jsonObject.getJSONArray("videos");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject oneRabbit = jsonArray.getJSONObject(i);
                            RabbitDataItem rabbitDataItem = createRabbitDataItem(oneRabbit);
                            rabbitData.add(rabbitDataItem);
                            total++;
                        }
                        if(total==jsonObject.getInt("total"))
                            break;

                        pageId++;
                        result = HttpRequest.sendGet(getString(R.string.dance_server), "client_id=459086bb819ff72d&user_id=UOTcwNjUxMjQ=&count=100&page="+pageId);
                        jsonObject=new JSONObject(result);
                    } catch (JSONException e) {
                        rabbitData = null;
                        Log.e(TAG, "JSONException");
                        break;
                    }
                }
            }
            // Return rabbitdata
            return rabbitData;
        }

        @Override
        protected void onPostExecute(List<RabbitDataItem> result) {
            super.onPostExecute(result);
            // Tell the Fragment that the refresh has completed
            onRefreshComplete(result, hasMore);
        }
    }
}
