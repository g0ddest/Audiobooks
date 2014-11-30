package org.fantasy_worlds.audiobooks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Vector;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class Replication {

    private class Source{
        String mUrl;
        String mData;
        Class mDbo;
    }

    private class Mirror {
        boolean mIsAlive = false;
        String mHost;
        public Mirror(String host) { mHost = "http://" + host; }
        public boolean  isAlive() { return mIsAlive; }
        public void     setAlive(boolean flag) { mIsAlive = flag; }
        public String   getHost() { return mHost; }
    }
    protected Vector<Mirror> mMirrors = new Vector<Mirror>();
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

    AQuery mAq;

    public Replication(Context context) {
        mMirrors.add(new Mirror("api.fantasy-worlds.org"));
        mMirrors.add(new Mirror("api.f-w.in"));
        mMirrors.add(new Mirror("api.fantasy-worlds.net"));
        new MirrorsChecker().execute(mMirrors);
        mAq = new AQuery(context);
    }

    protected abstract class ReplicationCallback extends AjaxCallback<JSONObject> {
        protected HashMap<String, Integer> getKey2Idx(JSONArray schema) {
            try {
                HashMap<String, Integer> result = new HashMap<String, Integer>();
                for (int i = 0; i < schema.length(); i++) {
                    result.put(schema.getString(i), i);
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        public abstract void replicate(JSONArray items, final HashMap<String, Integer> key2idx) throws Exception;
        @Override
        public void callback(String url, JSONObject obj, AjaxStatus status) {
            if (obj == null) {
                Log.e("ReplicationCallback", url + " " + status.getMessage());
            } else {
                try {
                    JSONArray items = obj.getJSONArray("items");
                    JSONArray schema = obj.getJSONArray("schema");
                    Log.d("ReplicationCallback", schema.toString());
                    HashMap<String, Integer> key2idx = getKey2Idx(schema);
                    replicate(items, key2idx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Runnable Init(final Context context) {
        return new Runnable() {
            public void run() {
                mAq.ajax("http://api.fantasy-worlds.org/authors", JSONObject.class, new ReplicationCallback() {
                    @Override
                    public void replicate(JSONArray items, final HashMap<String, Integer> key2idx) throws Exception {
                        for (int i = 0; i < items.length(); i++) {
                            final JSONArray author = items.getJSONArray(i);
                            HelperFactory.getHelper().getAuthorDao().createOrUpdate(
                                new Author() {
                                    {
                                        Id = author.getInt( key2idx.get("id") );
                                        Name = author.getString( key2idx.get("name") );
                                        Surname = author.getString( key2idx.get("surname") );
                                    }
                                }
                            );
                        }
                    }
                });
                mAq.ajax("http://api.fantasy-worlds.org/media", JSONObject.class, new ReplicationCallback() {
                    @Override
                    public void replicate(JSONArray items, final HashMap<String, Integer> key2idx) throws Exception {
                        for (int i = 0; i < items.length(); i++) {
                            final JSONArray media = items.getJSONArray(i);
                            HelperFactory.getHelper().getMediaDAO().createOrUpdate(
                                new Media() {
                                    {
                                        Id = media.getInt( key2idx.get("id") );
                                        BookId = media.getInt( key2idx.get("book_id") );
                                        MediaTitle = media.getString( key2idx.get("media_title") );
                                        BookTitle = media.getString( key2idx.get("book_title") );
                                        AuthorId = media.getInt( key2idx.get("author_id") );
                                        Description = media.getString( key2idx.get("description") );
                                        Cover = media.getString( key2idx.get("cover") );
                                    }
                                }
                            );
                        }
                    }
                });
                mAq.ajax("http://api.fantasy-worlds.org/media_part", JSONObject.class, new ReplicationCallback() {
                    @Override
                    public void replicate(JSONArray items, final HashMap<String, Integer> key2idx) throws Exception {
                        for (int i = 0; i < items.length(); i++) {
                            final JSONArray media_part = items.getJSONArray(i);
                            HelperFactory.getHelper().getMediaPartDAO().createOrUpdate(
                                new MediaPart() {
                                    {
                                        Id = media_part.getInt( key2idx.get("id") );
                                        MediaId = media_part.getInt( key2idx.get("media_id") );
                                        Sequence = media_part.getInt( key2idx.get("sequence") );
                                        Title = media_part.getString( key2idx.get("title") );
                                        Path = media_part.getString( key2idx.get("path") );
                                    }
                                }
                            );
                        }
                    }
                });

                CharSequence text = "Синхронизация завершена";
                int duration = Toast.LENGTH_SHORT;

                /*Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
            }
        };
    }

}
