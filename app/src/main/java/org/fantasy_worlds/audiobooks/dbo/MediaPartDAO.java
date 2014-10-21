package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class MediaPartDAO extends BaseDaoImpl<MediaPart, Integer> {

    public MediaPartDAO(ConnectionSource connectionSource,
                       Class<MediaPart> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<MediaPart> getAllMediaPart() throws SQLException{
        return this.queryForAll();
    }
}
