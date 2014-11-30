package org.fantasy_worlds.audiobooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.fantasy_worlds.audiobooks.dbo.Media;
import org.fantasy_worlds.audiobooks.dbo.MediaPart;
import org.fantasy_worlds.audiobooks.dbo.SavedPosition;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PlayerActivity extends Activity {

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private HashMap<Integer, File> mCachedMedias = new HashMap<Integer, File>();
    private ImageButton mButtonPlay;
    private Integer mNowPlaying;
    private Integer mPlayingDuration;
    private SeekBar mSeekBar;
    private TextView mDurationLabel;
    private Handler mHandler = new Handler();
    private Integer mCount = 0;
    private StoppableRunnable updatePosition = new StoppableRunnable() {

        @Override
        public void stoppableRun() {
            if(mMediaPlayer != null && mPlayingDuration != null){
                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(mCurrentPosition);
                String total = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(mPlayingDuration),
                        TimeUnit.MILLISECONDS.toSeconds(mPlayingDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mPlayingDuration))
                );

                String now = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition),
                        TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition))
                );
                mDurationLabel.setText(String.format("%s из %s", now, total));

                Short savePositionTime = 5;
                if(mCount >= savePositionTime){
                    // Если проигрывается медиа, то каждые 5 секунд обновляем последнюю позицию
                    mCount = 0;
                    try {
                        HelperFactory.getHelper().getSavedPositionDAO().savePosition(mNowPlaying, mCurrentPosition);
                    }catch (SQLException e){
                        e.printStackTrace();
                    }

                }else{
                    mCount = mCount +1;
                }

            }
            mHandler.postDelayed(this, 1000);
        }
    };

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
                    mCachedMedias.put(mediaPart.Id, file);
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

    protected static void setMediaPlayerFile(MediaPlayer mediaPlayer, File file) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPlayPart(MediaPart item,  Integer position){
        mNowPlaying = item.Id;
        File audio = mCachedMedias.get(item.Id);
        setMediaPlayerFile(mMediaPlayer, audio);
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mPlayingDuration = mMediaPlayer.getDuration();
        if(mMediaPlayer != null && position != null)
            mMediaPlayer.seekTo(position);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                }
            }
        });
        updatePosition.run();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MediaPart nextPart = null;
                try {
                    HelperFactory.getHelper().getSavedPositionDAO().completeListen(mNowPlaying);
                    nextPart = HelperFactory.getHelper().getMediaPartDAO().getNextPart(mNowPlaying);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (nextPart != null) {
                    mNowPlaying = nextPart.Id;
                    File audio = mCachedMedias.get(mNowPlaying);
                    setMediaPlayerFile(mp, audio);
                    mSeekBar.setMax(mp.getDuration());
                    mPlayingDuration = mp.getDuration();
                } else {
                    mNowPlaying = null;
                }
                mButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play));
            }
        });
        mButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_pause));
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

        mButtonPlay = (ImageButton) findViewById(R.id.btnPlay);

        MediaPartAdapter adapter = new MediaPartAdapter(this, R.layout.medialist_item, mediaParts);
        ListView partsView = (ListView) findViewById(R.id.partsList);
        mSeekBar = (SeekBar) findViewById(R.id.progressBar);
        mDurationLabel = (TextView) findViewById(R.id.totalDurationLabel);
        partsView.setAdapter(adapter);
        partsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MediaPart item = (MediaPart) adapterView.getItemAtPosition(i);
                startPlayPart(item, null);
            }
        });

        for (MediaPart item : mediaParts){

            try {
                SavedPosition pos = HelperFactory.getHelper().getSavedPositionDAO().getPositionByMediaPartId(item.Id);
                if(pos != null){
                    startPlayPart(item, pos.SavedPosition);
                    break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

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

    public void onClickStart(View view) {
        if(mMediaPlayer.isPlaying()){
            mButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_play));
            mMediaPlayer.pause();
        }else{
            mButtonPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_player_pause));
            mMediaPlayer.start();
        }
    }

    public void onDestroy() {
        updatePosition.stop();
        mMediaPlayer.stop();
        super.onDestroy();
    }

}

