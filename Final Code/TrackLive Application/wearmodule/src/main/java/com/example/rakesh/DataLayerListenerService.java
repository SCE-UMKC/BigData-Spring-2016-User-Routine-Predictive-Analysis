/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.rakesh;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.jtransforms.fft.DoubleFFT_2D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerListenerService extends WearableListenerService {






    private static final String TAG = "DataLayerListenerServic";

    private static final String START_ACTIVITY_PATH = "/start-activity1";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    public static final String COUNT_PATH = "/count";
    public static final String IMAGE_PATH = "/image";
    public static final String IMAGE_KEY = "photo";
    GoogleApiClient mGoogleApiClient;
    private final String MESSAGE1_PATH = "/message1";
    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        LOGD(TAG, "onDataChanged: " + dataEvents);
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient, "
                        + "error code: " + connectionResult.getErrorCode());
                return;
            }
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : dataEvents) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();

                // Send the rpc
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                        payload);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        LOGD(TAG, "onMessageReceived: " + messageEvent);
        String hoster=messageEvent.getSourceNodeId();
        new LongOperation().execute(hoster);
        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           // startActivity(startIntent);
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        LOGD(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        LOGD(TAG, "onPeerDisconnected: " + peer);
    }

    public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> implements SensorEventListener {
        SensorManager sensorManager;
        List<Double[]> acc_in=new ArrayList<Double[]>();

        Sensor mAccelerometer;
        int flag=1;
        String s="hello";
        byte[] bytes=s.getBytes();
        @Override
        protected String doInBackground(String... params) {
            Log.e("Params",params[0]);
            s=params[0];
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, mAccelerometer, 1);
            for (int i = 0; i < 1; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            sensorManager.unregisterListener(this);

            if(acc_in.size()%2==1){
                acc_in.remove(acc_in.size()-1);
            }
            else {

            }
           double[][] input = new double[acc_in.size()][3];

            for(int i=0;i<acc_in.size();i++){
                input[i][0]=acc_in.get(i)[0];
                input[i][1]=acc_in.get(i)[1];
                input[i][2]=acc_in.get(i)[2];

            }


            DoubleFFT_2D fftDo = new DoubleFFT_2D(acc_in.size(),3);
            double[][] fft = new double[acc_in.size() ][6];
            //System.arraycopy(input, 0, fft, 0, acc_in.size());
            for(int i=0;i<acc_in.size();i++){
                fft[i][0]=input[i][0];
                fft[i][1]=input[i][1];
                fft[i][2]=input[i][2];

            }
            fftDo.realForwardFull(fft);
            //String featurestr="";
            String featuredata="";
            int f2=1;
            for(int i=0;i<acc_in.size();i++){
                int f=1;
                String featurestr="";
                for(double d: fft[i]) {

                    if(f==1){
                        featurestr=d+"";
                        f=0;
                    }
                    else{
                        featurestr =featurestr+ ","+d;
                    }
                    System.out.println(d);
                   // Log.e("features",d+"");

                }
                if(f2==1) {
                    featuredata = featurestr;;
                    f2=0;
                }
                else {
                    featuredata = featuredata+"*" + featurestr;
                }
            }

            return featuredata;
        }

        @Override
        protected void onPostExecute(String result) {
            //TextView txt = (TextView) findViewById(R.id.output);
           // txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
           // bytes=result.getBytes();
           //String s= new String(bytes);
            Log.e("Features",s);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, s, MESSAGE1_PATH,
                    bytes);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            flag=0;
            if (event.sensor.getType()==1) {
                s=event.values[0]+","+event.values[1]+","+event.values[2];
//            bytes = ByteBuffer.allocate(4).putFloat(event.values[0]).array();
                acc_in.add(new Double[]{(double) event.values[0],(double)event.values[1],(double)event.values[2]});
                bytes=s.getBytes();
                Log.e("sensor data","data");
            }
            else {
                String example = "This is an example";
                bytes = example.getBytes();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }


    }
}
