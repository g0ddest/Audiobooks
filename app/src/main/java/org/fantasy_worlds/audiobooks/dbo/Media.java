package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "media")
public class Media implements Serializable {

    @DatabaseField(id = true)
    public Integer Id;

    @DatabaseField()
    public Integer BookId;

    @DatabaseField()
    public String MediaTitle;

    @DatabaseField()
    public String BookTitle;

    @DatabaseField()
    public Integer AuthorId;

    @DatabaseField()
    public String Description;

    @DatabaseField()
    public String Cover;

}

