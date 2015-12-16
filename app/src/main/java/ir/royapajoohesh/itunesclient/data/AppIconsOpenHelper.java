package ir.royapajoohesh.itunesclient.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ir.royapajoohesh.dataLibs.TableBase;

/* Created by Dr TJ @ March 2015 */

public class AppIconsOpenHelper extends SQLiteOpenHelper {
    public TableBase AppIconsTable = null;

    public static final String DatabaseName = "iTunesClientData.db";
    public static final String TableName = "AppIcons";
    public static final int Version = 3;

    public AppIconsOpenHelper(Context context) {
        super(context, DatabaseName, null, Version);

        this.AppIconsTable = new TableBase(DatabaseName, TableName);
        this.AppIconsTable.AddColumn("appID", "INTEGER", false, false);
		this.AppIconsTable.AddColumn("imageID", "INTEGER", false, false);
		this.AppIconsTable.AddColumn("ImagePath", "TEXT", false, false);
		this.AppIconsTable.AddColumn("Height", "INTEGER", false, false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String res = this.AppIconsTable.getCreateTableCommandText();
        db.execSQL(res);
        Log.d(TableName, res);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String res = this.AppIconsTable.getDropTableCommandText();
        db.execSQL(res);
        this.onCreate(db);
    }
}
