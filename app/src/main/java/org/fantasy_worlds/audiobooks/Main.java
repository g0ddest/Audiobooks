package org.fantasy_worlds.audiobooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.fantasy_worlds.audiobooks.dbo.Author;
import org.fantasy_worlds.audiobooks.dbo.Media;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main extends Activity {

    private float coverHeight = (float) 144.0;

    private class MediaAdapter extends ArrayAdapter<Media> {

        private List<Media> items;

        public MediaAdapter(Context context, int textViewResourceId, List<Media> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                //inflate a new view for your list item
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.booklist_item, null);
            }
            AQuery aq = new AQuery(v);
            Media media = items.get(position);
            if (media != null) {
                //set text to view
                TextView title = (TextView) v.findViewById(R.id.title);
                TextView author = (TextView) v.findViewById(R.id.author);
                if (title != null) {
                    title.setText(media.MediaTitle);
                }
                if(author != null){
                    try {
                        Author authorObj = HelperFactory.getHelper().getAuthorDao().getAuthorById(media.AuthorId);
                        author.setText(authorObj.Name + " " + authorObj.Surname);
                    }catch (SQLException e){
                        author.setText("");
                    }
                }
                boolean memCache = false;
                boolean fileCache = true;
                // TODO: change to Mirrors
                aq.id(R.id.cover).image("http://fantasy-worlds.org" + media.Cover, memCache, fileCache, 0, 0, new BitmapAjaxCallback(){
                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
                        /*int width = bm.getWidth();
                        int height = bm.getHeight();
                        float scale = coverHeight / height;
                        Matrix matrix = new Matrix();
                        matrix.postScale(scale, scale);
                        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);*/
                        iv.setImageBitmap(bm);
                    }
                });
            }
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperFactory.setHelper(getApplicationContext());
        setContentView(R.layout.activity_main);
        setTitle("Аудиокниги Fantasy-Worlds");

        Replication replication = new Replication();
        replication.Init();

        ListView booksView = (ListView) findViewById(R.id.booksView);

        ArrayList<HashMap<String, Object>> booksList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        List<Media> medias = new ArrayList<Media>();

        try {
            medias = HelperFactory.getHelper().getMediaDAO().getAllMedia();
        }catch (SQLException e){

        }

        MediaAdapter adapter = new MediaAdapter(this, R.layout.booklist_item, medias);

        booksView.setAdapter(adapter);

        booksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                Media item = (Media) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("mediaId", item.Id);
                startActivity(intent);

                /*builder.setTitle("Сообщение")
                        .setMessage("Выбрана аудиокнига ID:"  + item.Id + " TITLE: " + item.MediaTitle + "(" + item.BookTitle + ")")
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();*/
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    public void onDestroy() {
        HelperFactory.releaseHelper();
        super.onDestroy();
    }
}
