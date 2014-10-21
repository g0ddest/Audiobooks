package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "media_part")
public class MediaPart {
    @DatabaseField(dataType = DataType.LONG)
    public int Id;

    @DatabaseField(dataType = DataType.LONG)
    public int MediaId;

    @DatabaseField(dataType = DataType.SHORT)
    public int Sequence;

    @DatabaseField(dataType = DataType.STRING)
    public String Title;

    @DatabaseField(dataType = DataType.STRING)
    public String Path;
}
