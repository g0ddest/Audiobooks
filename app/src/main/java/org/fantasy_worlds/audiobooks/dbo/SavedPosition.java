package org.fantasy_worlds.audiobooks.dbo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "saved_position")
public class SavedPosition {
    @DatabaseField(id = true, uniqueIndex = true)
    public Integer MediaId;

    // TODO: change to foreign key
    @DatabaseField()
    public Integer MediaPartId;

    @DatabaseField()
    public Integer SavedPosition;
}
