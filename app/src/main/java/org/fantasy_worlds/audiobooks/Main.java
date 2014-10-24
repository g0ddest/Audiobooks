package org.fantasy_worlds.audiobooks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.fantasy_worlds.audiobooks.dbo.Author;
import org.fantasy_worlds.audiobooks.dbo.Media;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main extends Activity {

    private ArrayList<HashMap<String, Object>> booksList;

    private static final String TITLE = "title";
    private static final String AUTHOR = "author";

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

        booksList = new ArrayList<HashMap<String, Object>>();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                Media item = (Media) adapterView.getItemAtPosition(i);
                builder.setTitle("Сообщение")
                        .setMessage("Выбрана аудиокнига ID:"  + item.Id + " TITLE: " + item.MediaTitle + "(" + item.BookTitle + ")")
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
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
