package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
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

    public Author getAuthorById(Integer id) throws SQLException{
        QueryBuilder<Author, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("Id", id);
        PreparedQuery<Author> preparedQuery = queryBuilder.prepare();
        List<Author> authorList = query(preparedQuery);
        if(authorList.size() == 1)
            return authorList.get(0);
        else
            return null;
    }
}
