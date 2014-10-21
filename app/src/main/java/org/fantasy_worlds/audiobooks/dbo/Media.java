package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "media")
public class Media {

    @DatabaseField(dataType = DataType.LONG)
    public int Id;

    @DatabaseField(dataType = DataType.LONG)
    public int BookId;

    @DatabaseField(dataType = DataType.STRING)
    public String MediaTitle;

    @DatabaseField(dataType = DataType.STRING)
    public String BookTitle;

    @DatabaseField(dataType = DataType.LONG)
    public int AuthorId;

    @DatabaseField(dataType = DataType.STRING)
    public String Description;

    @DatabaseField(dataType = DataType.STRING)
    public String Cover;

}
