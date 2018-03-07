package com.example.root.project_amio;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by root on 31/01/18.
 */

public class RestTask extends AsyncTask {
    private URL url;
    private TextView httpResult;
    private View rootView;
    private Context appContext;

    public RestTask(String url, Context context) throws MalformedURLException {
        super();
        this.url = new URL("http://iotlab.telecomnancy.eu/rest/data/1/light1/last");
        this.appContext = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
            InputStream stream = null;
            HttpURLConnection connection = null;
            String result = null;
            int responseCode = -666;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                responseCode = connection.getResponseCode();

                if(responseCode!=200){
                    Log.i("response ", "code :"+responseCode);
                    publishProgress(responseCode);
                }else{
                    String body = readFullyAsString(connection.getInputStream(), "UTF-8");
                    JSONObject bodyJSON;
                    try {
                        bodyJSON = new JSONObject(body);
                        //Log.i("response content label ", bodyJSON.getJSONArray("data").getJSONObject(0).getString("label"));
                        Log.i("response content label ", bodyJSON.toString());
                        publishProgress(
                                bodyJSON.getJSONArray("data").getJSONObject(0).getLong("timestamp"),
                                bodyJSON.getJSONArray("data").getJSONObject(0).getString("label"),
                                bodyJSON.getJSONArray("data").getJSONObject(0).getDouble("value"),
                                bodyJSON.getJSONArray("data").getJSONObject(0).getDouble("mote"),
                                bodyJSON.getJSONArray("data").getJSONObject(1).getLong("timestamp"),
                                bodyJSON.getJSONArray("data").getJSONObject(1).getString("label"),
                                bodyJSON.getJSONArray("data").getJSONObject(1).getDouble("value"),
                                bodyJSON.getJSONArray("data").getJSONObject(1).getDouble("mote")

                                );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }  catch (ProtocolException e) {
                            e.printStackTrace();
        } catch (IOException e) {
                                     e.printStackTrace();
                        } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            Log.i("code de reponse ", ""+responseCode);


                    return responseCode;


    }


    public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }
    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        if (values.length == 1) {
            Toast.makeText(this.appContext, "Un problème est survenu lors de la communication HTTP. Code = " + values[0], Toast.LENGTH_SHORT).show();

        } else {

            Intent intent = new Intent("updateHttp");
            // You can also include some extra data.
            intent.putExtra("timestamp1", (Long)values[0]);
            intent.putExtra("label1", (String)values[1]);
            intent.putExtra("value1", (double)values[2]);
            intent.putExtra("mote1", (double)values[3]);
            intent.putExtra("timestamp2", (Long)values[4]);
            intent.putExtra("label2", (String)values[5]);
            intent.putExtra("value2", (double)values[6]);
            intent.putExtra("mote2", (double)values[7]);

            LocalBroadcastManager.getInstance(this.appContext).sendBroadcast(intent);

        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


}
