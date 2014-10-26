package org.fantasy_worlds.audiobooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.fantasy_worlds.audiobooks.dbo.Author;
import org.fantasy_worlds.audiobooks.dbo.Media;
import org.fantasy_worlds.audiobooks.dbo.MediaPart;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PlayerActivity extends Activity {

    private class MediaPartAdapter extends ArrayAdapter<MediaPart> {

        private List<MediaPart> items;

        public MediaPartAdapter(Context context, int textViewResourceId, List<MediaPart> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                //inflate a new view for your list item
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.medialist_item, null);
            }

            MediaPart mediaPart = items.get(position);
            if (mediaPart != null) {
                //set text to view
                TextView title = (TextView) v.findViewById(R.id.media_title);
                if (title != null) {
                    title.setText(mediaPart.Title);
                }
            }
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

        setTitle("Аудиокнига «" + media.MediaTitle + "»");
        MediaPartAdapter adapter = new MediaPartAdapter(this, R.layout.medialist_item, mediaParts);
        ListView partsView = (ListView) findViewById(R.id.partsList);
        partsView.setAdapter(adapter);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
