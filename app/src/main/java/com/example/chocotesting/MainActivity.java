package com.example.chocotesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connectServer(View v){
        EditText ipv4AddressView = findViewById(R.id.IPAddress);
        String ipv4Address = ipv4AddressView.getText().toString();
        EditText portNumberView = findViewById(R.id.portNumber);
        String portNumber = portNumberView.getText().toString();

        String postUrl= "http://"+ipv4Address+":"+portNumber+"/";

        String postBodyText="Hello";
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        RequestBody postBody = RequestBody.create(mediaType, postBodyText);

//        postRequest(postUrl, postBody);

        PostRequestTask postRequestTask = new PostRequestTask(postUrl, postBody);
        postRequestTask.execute();
    }

    public void checkDetection(View v) {
        EditText ipv4AddressView = findViewById(R.id.IPAddress);
        String ipv4Address = ipv4AddressView.getText().toString();
        EditText portNumberView = findViewById(R.id.portNumber);
        String portNumber = portNumberView.getText().toString();
        String postUrl = "http://" + ipv4Address + ":" + portNumber + "/detect";
        String postBodyText = "1677429442763";

        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        RequestBody postBody = RequestBody.create(mediaType, postBodyText);

//        postRequest(postUrl, postBody);

        PostRequestTask postRequestTask = new PostRequestTask(postUrl, postBody);
        postRequestTask.execute();

    }


    public class PostRequestTask extends AsyncTask<Void, Void, String> {

        private String postUrl;
        private RequestBody postBody;

        public PostRequestTask(String postUrl, RequestBody postBody) {
            this.postUrl = postUrl;
            this.postBody = postBody;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(postBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String responseString) {
            TextView responseText = findViewById(R.id.responseText);
            if (responseString != null) {
                responseText.setText(responseString);
                if (Boolean.parseBoolean(responseString)) {
                    // if booleanVal is true, vibrate the phone twice
                    Vibrator vibrator = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // for Android 8.0 or newer
                        VibrationEffect vibrationEffect = VibrationEffect.createWaveform(new long[]{0, 200, 200, 200}, -1);
                        vibrator.vibrate(vibrationEffect);
                    } else {
                        // for older versions of Android
                        vibrator.vibrate(new long[]{0, 200, 200, 200}, -1);
                    }
                } else {
                    // if booleanVal is false, vibrate the phone once
                    Vibrator vibrator = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // for Android 8.0 or newer
                        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(vibrationEffect);
                    } else {
                        // for older versions of Android
                        vibrator.vibrate(200);
                    }
                }
            } else {
                responseText.setText("Failed to Connect to Server");
            }
        }
    }

}