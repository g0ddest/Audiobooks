package org.fantasy_worlds.audiobooks.dbo;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "authors")
public class Author {
    @DatabaseField(dataType = DataType.LONG)
    public int Id;
    @DatabaseField(dataType = DataType.STRING)
    public String Name;
    @DatabaseField(dataType = DataType.STRING)
    public String Surname;
}
