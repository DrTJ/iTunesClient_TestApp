package ir.royapajoohesh.itunesclient.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ir.royapajoohesh.dataLibs.TableBase;

/* Created by Dr TJ @ March 2015 */

public class AppCategoriesOpenHelper extends SQLiteOpenHelper {
    public TableBase AppCategoriesTable = null;

    public static final String DatabaseName = "iTunesClientData.db";
    public static final String TableName = "AppCategories";
    public static final int Version = 3;

    public AppCategoriesOpenHelper(Context context) {
        super(context, DatabaseName, null, Version);

        this.AppCategoriesTable = new TableBase(DatabaseName, TableName);
        this.AppCategoriesTable.AddColumn("id", "INTEGER", false, false);
		this.AppCategoriesTable.AddColumn("term", "TEXT", false, false);
		this.AppCategoriesTable.AddColumn("scheme", "TEXT", false, false);
		this.AppCategoriesTable.AddColumn("label", "TEXT", false, false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String res = this.AppCategoriesTable.getCreateTableCommandText();
        db.execSQL(res);
        
        Log.d(TableName, res);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String res = this.AppCategoriesTable.getDropTableCommandText();
        db.execSQL(res);
        this.onCreate(db);
    }
}
