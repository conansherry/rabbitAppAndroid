/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.lvfq.rabbit.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.dialog.UpdateDialog;
import com.lvfq.rabbit.fragment.SlidingTabsColorsFragment;
import com.lvfq.rabbit.common.activities.ActivityBase;
import com.lvfq.rabbit.util.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends ActivityBase {

    private final static String TAG="MainActivity";

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsColorsFragment fragment = new SlidingTabsColorsFragment();
            //SwipeRefreshListFragmentFragment fragment = new SwipeRefreshListFragmentFragment();
            transaction.replace(R.id.main_fragment, fragment);
            transaction.commit();
        }

        if(isNetworkConnected()) {
            new UpdateBackgroundTask().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void onUpdate(JSONObject jsonObject) {
        try {
            if(jsonObject!=null) {
                Double serverVersion = jsonObject.getDouble("version");
                if (serverVersion > Double.parseDouble(((MainApplication) getApplication()).getVersion_name())) {
                    UpdateDialog updateDialog = new UpdateDialog();
                    Bundle args = new Bundle();
                    args.putDouble("version", serverVersion);
                    args.putString("url", jsonObject.getString("url"));
                    args.putString("info", jsonObject.getString("info"));
                    updateDialog.setArguments(args);
                    updateDialog.show(getSupportFragmentManager(), "update");
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException");
        }
    }

    private class UpdateBackgroundTask extends AsyncTask<Void, Void, JSONObject> {
        private static final String TAG="UpdateBackgroundTask";
        @Override
        protected JSONObject doInBackground(Void... params) {
            String result = HttpRequest.sendGet(getString(R.string.update_server), "");
            JSONObject jsonObject=null;
            if(result==null)
                return null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                jsonObject=null;
                Log.e(TAG, "JSONException");
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Tell the Fragment that the refresh has completed
            onUpdate(jsonObject);
        }
    }
}
