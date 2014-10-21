package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

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