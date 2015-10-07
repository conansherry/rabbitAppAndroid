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

package com.lvfq.rabbit.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lvfq.rabbit.Appcontext.MainApplication;
import com.lvfq.rabbit.R;
import com.lvfq.rabbit.data.UserDataItem;
import com.lvfq.rabbit.util.HttpRequest;

import java.io.InputStream;
import java.net.HttpRetryException;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class RegisterActivity extends Activity {

    private static final String TAG="RegisterActivity";

    private Boolean canRegister = true;
    private UserDataItem userDataItem = new UserDataItem();

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private int isValid(EditText name, EditText pwd, EditText pwd_confirm) {
        Log.d(TAG, pwd.getText().toString() + " " + pwd_confirm.getText().toString());
        if(name.getText().toString().isEmpty())
            return 1;
        else if(pwd.getText().toString().isEmpty())
            return 2;
        else if(!pwd.getText().toString().equals(pwd_confirm.getText().toString()))
            return 3;
        else
            return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        final EditText registerUsername = (EditText)findViewById(R.id.register_username);
        final EditText registerPwd = (EditText)findViewById(R.id.register_password);
        final EditText registerPwdConfirm = (EditText)findViewById(R.id.register_password_confirm);
        Button okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected() && isValid(registerUsername, registerPwd, registerPwdConfirm) == 0) {
                    userDataItem.name = registerUsername.getText().toString();
                    userDataItem.pwd = registerPwd.getText().toString();
                    synchronized (canRegister) {
                        if (canRegister) {
                            canRegister = false;
                            new RegisterBackgroundTask().execute();
                        }
                    }
                } else {
                    if(isValid(registerUsername, registerPwd, registerPwdConfirm) == 1)
                        Toast.makeText(RegisterActivity.this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                    else if(isValid(registerUsername, registerPwd, registerPwdConfirm) == 2)
                        Toast.makeText(RegisterActivity.this, getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
                    else if(isValid(registerUsername, registerPwd, registerPwdConfirm) == 3)
                        Toast.makeText(RegisterActivity.this, getString(R.string.pwd_notequal), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onRegister(String res) {
        if(res.equals("USER_EXISTS"))
            Toast.makeText(RegisterActivity.this, getString(R.string.user_exists), Toast.LENGTH_SHORT).show();
        else if(res.equals("REGISTER_OK")) {
            ((MainApplication)getApplication()).setUserInfo(userDataItem);
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("index", 2);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            RegisterActivity.this.startActivity(intent);
        }
        else
            Toast.makeText(RegisterActivity.this, getString(R.string.unknown_err), Toast.LENGTH_SHORT).show();
        synchronized (canRegister) {
            canRegister=true;
        }
    }

    private class RegisterBackgroundTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "RegisterBackgroundTask";
        @Override
        protected String doInBackground(Void... params) {
            try {
                String info = "name="+userDataItem.name+"&pwd="+userDataItem.pwd;
                return HttpRequest.sendPost(getString(R.string.register_server), info);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            // Tell the Fragment that the refresh has completed
            onRegister(res);
        }
    }
}
