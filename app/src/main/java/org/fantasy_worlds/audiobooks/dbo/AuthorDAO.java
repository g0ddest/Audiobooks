package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class AuthorDAO extends BaseDaoImpl<Author, Integer> {

    public AuthorDAO(ConnectionSource connectionSource,
                      Class<Author> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Author> getAllAuthors() throws SQLException{
        return this.queryForAll();
    }
}
