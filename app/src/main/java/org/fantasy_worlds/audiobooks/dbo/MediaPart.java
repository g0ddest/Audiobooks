package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "media_part")
public class MediaPart implements Serializable {

    @DatabaseField(id = true)
    public Integer Id;

    @DatabaseField()
    public Integer MediaId;

    @DatabaseField()
    public Integer Sequence;

    @DatabaseField()
    public String Title;

    @DatabaseField()
    public String Path;

}