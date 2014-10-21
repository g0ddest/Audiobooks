package org.fantasy_worlds.audiobooks.dbo;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "authors")
public class Author {

    @DatabaseField()
    public int Id;

    @DatabaseField()
    public String Name;

    @DatabaseField()
    public String Surname;

}
