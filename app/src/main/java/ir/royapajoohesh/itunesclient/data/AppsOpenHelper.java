package ir.royapajoohesh.itunesclient.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ir.royapajoohesh.dataLibs.TableBase;

/* Created by Dr TJ @ March 2015 */

public class AppsOpenHelper extends SQLiteOpenHelper {
    public TableBase AppsTable = null;

    public static final String DatabaseName = "iTunesClientData.db";
    public static final String TableName = "Apps";
    public static final int Version = 3;

    public AppsOpenHelper(Context context) {
        super(context, DatabaseName, null, Version);

        this.AppsTable = new TableBase(DatabaseName, TableName);
        this.AppsTable.AddColumn("id", "INTEGER", false, false);
		this.AppsTable.AddColumn("bundleId", "TEXT", false, false);
		this.AppsTable.AddColumn("name", "TEXT", false, false);
		this.AppsTable.AddColumn("title", "TEXT", false, false);
		this.AppsTable.AddColumn("summary", "TEXT", false, false);
		this.AppsTable.AddColumn("priceAmmount", "REAL", false, false);
		this.AppsTable.AddColumn("priceCurrency", "TEXT", false, false);
		this.AppsTable.AddColumn("contentType", "TEXT", false, false);
		this.AppsTable.AddColumn("rights", "TEXT", false, false);
		this.AppsTable.AddColumn("link", "TEXT", false, false);
		this.AppsTable.AddColumn("artistName", "TEXT", false, false);
		this.AppsTable.AddColumn("artistLink", "TEXT", false, false);
		this.AppsTable.AddColumn("categoryID", "INTEGER", false, false);
		this.AppsTable.AddColumn("releaseDate", "TEXT", false, false);
		this.AppsTable.AddColumn("releaseDateLabel", "TEXT", false, false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String res = this.AppsTable.getCreateTableCommandText();
        db.execSQL(res);
        Log.d(TableName, res);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String res = this.AppsTable.getDropTableCommandText();
        db.execSQL(res);
        this.onCreate(db);
    }
}
