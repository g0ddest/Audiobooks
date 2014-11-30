package org.fantasy_worlds.audiobooks;

import android.content.Context;
import java.sql.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.fantasy_worlds.audiobooks.dbo.Author;
import org.fantasy_worlds.audiobooks.dbo.AuthorDAO;
import org.fantasy_worlds.audiobooks.dbo.Media;
import org.fantasy_worlds.audiobooks.dbo.MediaDAO;
import org.fantasy_worlds.audiobooks.dbo.MediaPart;
import org.fantasy_worlds.audiobooks.dbo.MediaPartDAO;
import org.fantasy_worlds.audiobooks.dbo.SavedPosition;
import org.fantasy_worlds.audiobooks.dbo.SavedPositionDAO;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME ="audiobooks.db";

    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 1;

    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private AuthorDAO authorDao = null;
    private MediaDAO mediaDAO = null;
    private MediaPartDAO mediaPartDAO = null;
    private SavedPositionDAO savedPositionDAO = null;

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, Author.class);
            TableUtils.createTable(connectionSource, Media.class);
            TableUtils.createTable(connectionSource, MediaPart.class);
            TableUtils.createTable(connectionSource, SavedPosition.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){

        try {
            TableUtils.createTable(connectionSource, SavedPosition.class);
        }catch (SQLException e){

        }

        try{
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Author.class, true);
            TableUtils.dropTable(connectionSource, Media.class, true);
            TableUtils.dropTable(connectionSource, MediaPart.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    //синглтоны
    public AuthorDAO getAuthorDao() throws SQLException{
        if(authorDao == null){
            authorDao = new AuthorDAO(getConnectionSource(), Author.class);
        }
        return authorDao;
    }

    public MediaDAO getMediaDAO() throws SQLException{
        if(mediaDAO == null){
            mediaDAO = new MediaDAO(getConnectionSource(), Media.class);
        }
        return mediaDAO;
    }

    public MediaPartDAO getMediaPartDAO() throws SQLException{
        if(mediaPartDAO == null){
            mediaPartDAO = new MediaPartDAO(getConnectionSource(), MediaPart.class);
        }
        return mediaPartDAO;
    }

    public SavedPositionDAO getSavedPositionDAO() throws SQLException{
        if(savedPositionDAO == null){
            savedPositionDAO = new SavedPositionDAO(getConnectionSource(), SavedPosition.class);
        }
        return savedPositionDAO;
    }

    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
        authorDao = null;
        mediaDAO = null;
        mediaPartDAO = null;
    }
}
