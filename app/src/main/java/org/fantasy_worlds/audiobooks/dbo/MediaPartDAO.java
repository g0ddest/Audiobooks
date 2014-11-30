package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
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

    public List<MediaPart> getPartsByMedia(Media media) throws SQLException{
        QueryBuilder<MediaPart, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("MediaId", media.Id);
        PreparedQuery<MediaPart> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    public MediaPart getNextPart(MediaPart mediaPart) throws SQLException {
        QueryBuilder<MediaPart, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("MediaId", mediaPart.MediaId)
                .and().gt("Sequence", mediaPart.Sequence);
        queryBuilder.limit(1L);
        queryBuilder.orderBy("Sequence", /*ascending*/ true);
        List<MediaPart> parts = queryBuilder.query();
        if (parts != null && parts.size() > 0) {
            return parts.get(0);
        } else {
            return null;
        }
    }

    public MediaPart getNextPart(Integer mediaPartID) throws SQLException {
        MediaPart mediaPart = this.queryForId(mediaPartID);
        return getNextPart(mediaPart);
    }
}
