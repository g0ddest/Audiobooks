package org.fantasy_worlds.audiobooks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Replication {

    public void Init() {
        new HttpAsyncTask().execute("http://api.fantasy-worlds.org/media");
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, Vector<String>> {
        @Override
        protected Vector<String> doInBackground(String... urls) {
            Vector<String> bodies = new Vector<String>(urls.length);
            for(String url: urls)
                bodies.add( GET(url) );
            return bodies;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Vector<String> result) {
            for(String body: result)
            {
                try {
                    JSONObject obj = new JSONObject(body);
                    JSONArray items = obj.getJSONArray("items");
                    JSONArray schema = obj.getJSONArray("schema");
                    Log.d("HttpAsyncTask", schema.toString());
                }catch (JSONException e){

                }
            }
        }
    }

}
