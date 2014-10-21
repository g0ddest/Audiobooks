package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "media_part")
public class MediaPart {

    @DatabaseField()
    public int Id;

    @DatabaseField()
    public int MediaId;

    @DatabaseField()
    public int Sequence;

    @DatabaseField()
    public String Title;

    @DatabaseField()
    public String Path;

}
