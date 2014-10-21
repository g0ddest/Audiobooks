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
import java.util.Vector;

public class Replication {
    private class Mirror {
        boolean m_is_alive = false;
        String m_host;
        public Mirror(String host) { m_host = "http://" + host; }
        public boolean  isAlive() { return m_is_alive; }
        public void     setAlive(boolean flag) { m_is_alive = flag; }
        public String   getHost() { return m_host; }
    }
    protected Vector<Mirror> m_mirrors = new Vector<Mirror>();
    private class MirrorsChecker extends AsyncTask<Vector<Mirror>, Void, Void> {
        @Override
        protected Void doInBackground(Vector<Mirror>... mirrors) {
            HttpClient httpClient = new DefaultHttpClient();
            for(Mirror mirror: mirrors[0]) {
                try {
                    HttpResponse httpResponse = httpClient.execute(new HttpGet(mirror.getHost() + "/"));
                    httpResponse.getEntity().consumeContent();
                    mirror.setAlive(true);
                    Log.d("MirrorsChecker", mirror.getHost() + " is alive");
                } catch (Exception e) {
                    mirror.setAlive(false);
                    Log.d("MirrorsChecker", mirror.getHost() + " is dead");
                }
            }
            return null;
        }
    }

    public Replication() {
        m_mirrors.add( new Mirror("api.fantasy-worlds.org") );
        m_mirrors.add( new Mirror("api.f-w.in") );
        new MirrorsChecker().execute(m_mirrors);
    }

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
