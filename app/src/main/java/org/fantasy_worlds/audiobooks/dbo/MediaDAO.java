package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class MediaDAO extends BaseDaoImpl<Media, Integer> {

    public MediaDAO(ConnectionSource connectionSource,
                        Class<Media> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Media> getAllMedia() throws SQLException{
        return this.queryForAll();
    }

    public Media getMediaById(Integer id) throws SQLException{
        QueryBuilder<Media, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("Id", id);
        PreparedQuery<Media> preparedQuery = queryBuilder.prepare();
        List<Media> mediaList = query(preparedQuery);
        if(mediaList.size() == 1)
            return mediaList.get(0);
        else
            return null;
    }
}
