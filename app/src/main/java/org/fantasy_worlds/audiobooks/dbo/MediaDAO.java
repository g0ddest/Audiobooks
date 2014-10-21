package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
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
}
