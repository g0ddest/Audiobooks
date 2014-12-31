package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class SavedPositionDAO extends BaseDaoImpl<SavedPosition, Integer> {

    public SavedPositionDAO(ConnectionSource connectionSource,
                        Class<SavedPosition> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<SavedPosition> getAllMediaPart() throws SQLException{
        return this.queryForAll();
    }

    public SavedPosition getPositionByMediaPartId(Integer mediaPartId) throws SQLException{
        QueryBuilder<SavedPosition, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("MediaPartId", mediaPartId);
        PreparedQuery<SavedPosition> preparedQuery = queryBuilder.prepare();
        List<SavedPosition> positions = query(preparedQuery);
        if(positions != null && !positions.isEmpty())
            return positions.get(0);
        return null;
    }

    public void savePosition(final Integer mediaId, final Integer mediaPartId, final Integer savedPosition) throws SQLException{

        createOrUpdate(new SavedPosition(){
            {
                MediaId = mediaId;
                MediaPartId = mediaPartId;
                SavedPosition = savedPosition;
            }
        });
    }

    public void completeListen(Integer mediaPartId) throws SQLException{
        DeleteBuilder<SavedPosition, Integer> deleteBuilder = deleteBuilder();
        deleteBuilder.where().eq("MediaPartId", mediaPartId);
        deleteBuilder.delete();
    }
}
