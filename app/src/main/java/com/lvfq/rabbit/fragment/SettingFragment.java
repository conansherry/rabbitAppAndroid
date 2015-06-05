/*
 * Copyright 2013 The Android Open Source Project
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

package com.lvfq.rabbit.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.activity.AboutActivity;
import com.lvfq.rabbit.activity.PlayerActivity;
import com.lvfq.rabbit.data.RabbitDataItem;
import com.lvfq.rabbit.util.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class SettingFragment extends Fragment {

    private Boolean canCheckUpdate=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout update=(RelativeLayout)view.findViewById(R.id.update_click);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (canCheckUpdate) {
                    if (canCheckUpdate) {
                        canCheckUpdate=false;
                        new UpdateBackgroundTask().execute();
                    }
                }
            }
        });

        RelativeLayout about=(RelativeLayout)view.findViewById(R.id.about_click);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    private void onUpdate(String result) {
        if(result==null) {
            Toast.makeText(getActivity(),getString(R.string.noupdate),Toast.LENGTH_SHORT).show();
        }
        else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
        }

        synchronized (canCheckUpdate) {
            canCheckUpdate=true;
        }
    }

    private class UpdateBackgroundTask extends AsyncTask<Void, Void, String> {
        private static final String TAG="UpdateBackgroundTask";
        @Override
        protected String doInBackground(Void... params) {
            String result = HttpRequest.sendGet(getString(R.string.update_server), "");
            try {
                JSONObject jsonObject = new JSONObject(result);
                Double serverVersion = jsonObject.getDouble("version");
                if(serverVersion>Double.parseDouble(((MainApplication)getActivity().getApplication()).getVersion_name())) {
                    result=jsonObject.getString("apk");
                }
                else
                    result=null;
            } catch (JSONException e) {
                Log.e(TAG, "JSONException");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Tell the Fragment that the refresh has completed
            onUpdate(result);
        }
    }
}
