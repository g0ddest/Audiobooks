package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "media")
public class Media {

    @DatabaseField()
    public int Id;

    @DatabaseField()
    public int BookId;

    @DatabaseField()
    public String MediaTitle;

    @DatabaseField()
    public String BookTitle;

    @DatabaseField()
    public int AuthorId;

    @DatabaseField()
    public String Description;

    @DatabaseField()
    public String Cover;

}
