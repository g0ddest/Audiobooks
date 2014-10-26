package org.fantasy_worlds.audiobooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.fantasy_worlds.audiobooks.dbo.Media;
import org.fantasy_worlds.audiobooks.dbo.MediaPart;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PlayerActivity extends Activity {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private HashMap<Integer, File> cachedMedias = new HashMap<Integer, File>();

    private class MediaPartAdapter extends ArrayAdapter<MediaPart> {

        private List<MediaPart> items;

        public MediaPartAdapter(Context context, int textViewResourceId, List<MediaPart> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ImageView loadStatus;
            final ProgressBar mediaLoadBar;
            if (v == null) {
                //inflate a new view for your list item
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.medialist_item, null);
            }

            loadStatus = (ImageView) v.findViewById(R.id.load_status);
            mediaLoadBar = (ProgressBar) v.findViewById(R.id.media_progressbar);

            AQuery aq = new AQuery(v);

            final MediaPart mediaPart = items.get(position);
            if (mediaPart != null) {
                //set text to view
                TextView title = (TextView) v.findViewById(R.id.media_title);
                if (title != null) {
                    title.setText(mediaPart.Title);
                }
            }

            assert mediaPart != null;
            long expire = 0;
            aq.progress(R.id.media_progressbar).ajax(mediaPart.Path, File.class, expire, new AjaxCallback<File>(){
                public void callback(String url, File file, AjaxStatus status) {
                    cachedMedias.put(mediaPart.Id, file);
                    loadStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ok));
                    mediaLoadBar.setVisibility(View.GONE);
                    if(file != null) {
                        Log.d("PlayerActivity", "OK");
                    } else {
                        Log.e("PlayerActivity", "Failed" + status);
                    }
                }
            });

            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Integer mediaId = intent.getIntExtra("mediaId", 0);
        Media media = new Media();
        List<MediaPart> mediaParts = new ArrayList<MediaPart>();
        try {
            media = HelperFactory.getHelper().getMediaDAO().getMediaById(mediaId);
            mediaParts = HelperFactory.getHelper().getMediaPartDAO().getPartsByMedia(media);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        setTitle(media.MediaTitle);
        MediaPartAdapter adapter = new MediaPartAdapter(this, R.layout.medialist_item, mediaParts);
        ListView partsView = (ListView) findViewById(R.id.partsList);
        partsView.setAdapter(adapter);
        partsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mediaPlayer.stop();
                mediaPlayer = new MediaPlayer();
                MediaPart item = (MediaPart) adapterView.getItemAtPosition(i);
                File audio = cachedMedias.get(item.Id);
                try {
                    mediaPlayer.setDataSource(audio.getPath());
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
