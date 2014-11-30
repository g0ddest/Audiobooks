package org.fantasy_worlds.audiobooks.dbo;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "authors")
public class Author implements Serializable {

    @DatabaseField(id = true)
    public Integer Id;

    @DatabaseField()
    public String Name;

    @DatabaseField()
    public String Surname;

}

