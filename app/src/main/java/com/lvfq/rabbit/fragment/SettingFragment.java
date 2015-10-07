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

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.activity.AboutActivity;
import com.lvfq.rabbit.activity.LoginActivity;
import com.lvfq.rabbit.activity.PlayerActivity;
import com.lvfq.rabbit.data.RabbitDataItem;
import com.lvfq.rabbit.data.UserDataItem;
import com.lvfq.rabbit.dialog.UpdateDialog;
import com.lvfq.rabbit.util.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class SettingFragment extends Fragment {

    private static final String TAG="SettingFragment";

    private Boolean canCheckUpdate=true;
    private Boolean canShare=true;

    private UserDataItem userDataItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting, container, false);
    }

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView userName = (TextView)view.findViewById(R.id.username);
        userDataItem = ((MainApplication)getActivity().getApplication()).getUserInfo();
        if(userDataItem != null) {
            userName.setText(userDataItem.name);
            TextView userLevel = (TextView)view.findViewById(R.id.userlevel);
            userLevel.setText(userDataItem.title);
        }
        else {
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }

        RelativeLayout update=(RelativeLayout)view.findViewById(R.id.update_click);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    synchronized (canCheckUpdate) {
                        if (canCheckUpdate) {
                            canCheckUpdate = false;
                            new UpdateBackgroundTask().execute();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RelativeLayout share=(RelativeLayout)view.findViewById(R.id.share_click);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    synchronized (canShare) {
                        if (canShare) {
                            canShare = false;
                            new ShareBackgroundTask().execute();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
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

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root+"/"+getActivity().getApplication().getPackageName()+"/images");
        myDir.mkdirs();
        String fname = "share_qr.png";
        File file = new File(myDir, fname);
        Log.d(TAG, file.getAbsolutePath());
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onShare(Bitmap bitmap) {
        if(bitmap!=null && isExternalStorageReadable() && isExternalStorageWritable()) {
            Uri imageUri = null;
            try {
                SaveImage(bitmap);
                String root = Environment.getExternalStorageDirectory().toString();
                File shareImage = new File(root+"/"+getActivity().getApplication().getPackageName()+"/images/share_qr.png");
                imageUri = Uri.fromFile(shareImage);
                Log.d(TAG, imageUri.toString());
            } catch (Exception e) {
                imageUri=null;
                e.printStackTrace();
            }
            if(imageUri!=null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_app)));
            }
        }
        else {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, "黄艺林-菟籽琳app： "+getString(R.string.share_apk));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_app)));
        }
        synchronized (canShare) {
            canShare = true;
        }
    }

    private class ShareBackgroundTask extends AsyncTask<Void, Void, Bitmap> {
        private static final String TAG="ShareBackgroundTask";
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                InputStream in = new java.net.URL(getString(R.string.share_url)).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // Tell the Fragment that the refresh has completed
            onShare(bitmap);
        }
    }

    private void onUpdate(JSONObject jsonObject) {
        try {
            if(jsonObject!=null) {
                Double serverVersion = jsonObject.getDouble("version");
                if (serverVersion > Double.parseDouble(((MainApplication) getActivity().getApplication()).getVersion_name())) {
                    UpdateDialog updateDialog = new UpdateDialog();
                    Bundle args = new Bundle();
                    args.putDouble("version", serverVersion);
                    args.putString("url", jsonObject.getString("url"));
                    args.putString("info", jsonObject.getString("info"));
                    updateDialog.setArguments(args);
                    updateDialog.show(getActivity().getSupportFragmentManager(), "update");
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.noupdate), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException");
        } finally {
            synchronized (canCheckUpdate) {
                canCheckUpdate=true;
            }
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
