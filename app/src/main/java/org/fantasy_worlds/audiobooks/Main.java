package org.fantasy_worlds.audiobooks;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class Main extends Activity {

    private ArrayList<HashMap<String, Object>> booksList;

    private static final String TITLE = "title";
    private static final String AUTHOR = "author";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Аудиокниги Fantasy-Worlds");

        ListView booksView = (ListView) findViewById(R.id.booksView);

        booksList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        // Тестируем грид

        hm = new HashMap<String, Object>();
        hm.put(TITLE, "Книга 1"); // Название
        hm.put(AUTHOR, "Автор 1"); // Автор
        booksList.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(TITLE, "Книга 2"); // Название
        hm.put(AUTHOR, "Автор 2"); // Автор
        booksList.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(TITLE, "Книга 3"); // Название
        hm.put(AUTHOR, "Автор 3"); // Автор
        booksList.add(hm);

        hm = new HashMap<String, Object>();
        hm.put(TITLE, "Книга 4"); // Название
        hm.put(AUTHOR, "Автор 4"); // Автор
        booksList.add(hm);

        SimpleAdapter adapter = new SimpleAdapter(this, booksList,
                R.layout.booklist_item, new String[] { TITLE, AUTHOR },
                new int[] { R.id.title, R.id.author });

        booksView.setAdapter(adapter);

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
}
