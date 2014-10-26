package org.fantasy_worlds.audiobooks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fantasy_worlds.audiobooks.dbo.Author;
import org.fantasy_worlds.audiobooks.dbo.Media;
import org.fantasy_worlds.audiobooks.dbo.MediaPart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Replication {

    private class Source{
        String url;
        String data;
        Class dbo;
    }

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
        m_mirrors.add( new Mirror("api.fantasy-worlds.net") );
        new MirrorsChecker().execute(m_mirrors);
    }

    public void Init() {

        new HttpAsyncTask().execute(
                new Source() {
                    {
                        url = "http://api.fantasy-worlds.org/authors";
                        dbo = Author.class;
                    }
                },
                new Source() {
                    {
                        url = "http://api.fantasy-worlds.org/media";
                        dbo = Media.class;
                    }
                },
                new Source() {
                    {
                        url = "http://api.fantasy-worlds.org/media_part";
                        dbo = MediaPart.class;
                    }
                }
        );

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

    private class HttpAsyncTask extends AsyncTask<Source, Void, Vector<Source>> {

        @Override
        protected Vector<Source> doInBackground(Source... urls) {
            Vector<Source> bodies = new Vector<Source>(urls.length);
            for(Source source: urls) {
                source.data = GET(source.url);
                bodies.add(source);
            }
            return bodies;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Vector<Source> result) {

            // Костыльчик пока не появится тип данных
            List<String> intKeys =
                    Arrays.asList("id", "book_id", "author_id", "media_id", "sequence");

            for(Source source: result)
            {
                try {
                    JSONObject obj = new JSONObject(source.data);
                    JSONArray items = obj.getJSONArray("items");
                    final JSONArray schema = obj.getJSONArray("schema");

                    ArrayList<HashMap<String, Object>> list =
                            new ArrayList<HashMap<String, Object>>();

                    for (int i = 0 ; i < items.length(); i++) {

                        JSONArray item = items.getJSONArray(i);
                        HashMap<String, Object> itemMap = new HashMap<String, Object>();

                        for (int j = 0 ; j < schema.length(); j++){
                            // Переделать с использованием Reflection
                            String key = schema.getString(j);

                            if(intKeys.contains(key)){
                                itemMap.put(key, item.getInt(j));
                            }else {
                                itemMap.put(key, item.getString(j));
                            }
                        }

                        list.add(itemMap);

                    }

                    // if ужасен, но остальное слишком дооолго
                    if(source.dbo == Author.class){
                        for(final HashMap<String, Object> author : list)
                            try {
                                HelperFactory.getHelper().getAuthorDao().createOrUpdate(
                                        new Author() {
                                            {
                                                Id = (Integer) author.get("id");
                                                Name = author.get("name").toString();
                                                Surname = author.get("surname").toString();
                                            }
                                        }
                                );
                            }catch (Exception e){
                                Log.d("Error", e.toString());
                            }
                    }
                    if(source.dbo == Media.class){
                        for(final HashMap<String, Object> media : list)
                            try {
                                HelperFactory.getHelper().getMediaDAO().createOrUpdate(
                                        new Media() {
                                            {
                                                Id = (Integer) media.get("id");
                                                BookId = (Integer) media.get("book_id");
                                                MediaTitle = media.get("media_title").toString();
                                                BookTitle = media.get("book_title").toString();
                                                AuthorId = (Integer) media.get("author_id");
                                                Description = media.get("description").toString();
                                                Cover = media.get("cover").toString();
                                            }
                                        }
                                );
                            }catch (SQLException e){

                            }
                    }
                    if(source.dbo == MediaPart.class){
                        for(final HashMap<String, Object> media : list)
                            try {
                                HelperFactory.getHelper().getMediaPartDAO().createOrUpdate(
                                        new MediaPart() {
                                            {
                                                Id = (Integer) media.get("id");
                                                MediaId = (Integer) media.get("media_id");
                                                Sequence = (Integer) media.get("sequence");
                                                Title = media.get("title").toString();
                                                Path = media.get("path").toString();
                                            }
                                        }
                                );
                            }catch (SQLException e){

                            }
                    }

                    Log.d("HttpAsyncTask", schema.toString());
                }catch (JSONException e){

                }
            }
        }
    }

}
