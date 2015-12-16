package ir.royapajoohesh.itunesclient.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ir.royapajoohesh.dataLibs.TableBase;

/* Created by Dr TJ @ March 2015 */

public class UsersOpenHelper extends SQLiteOpenHelper {
    public TableBase UsersTable = null;

    public static final String DatabaseName = "iTunesClientData.db";
    public static final String TableName = "Users";
    public static final int Version = 3;

    public UsersOpenHelper(Context context) {
        super(context, DatabaseName, null, Version);

        this.UsersTable = new TableBase(DatabaseName, TableName);
        this.UsersTable.AddColumn("IDUser", "INTEGER", false, false);
		this.UsersTable.AddColumn("Username", "TEXT", false, false);
		this.UsersTable.AddColumn("Password", "TEXT", false, false);
		this.UsersTable.AddColumn("FirstName", "TEXT", false, false);
		this.UsersTable.AddColumn("LastName", "TEXT", false, false);
		this.UsersTable.AddColumn("Email", "TEXT", false, false);
		this.UsersTable.AddColumn("Mobile", "TEXT", false, false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String res = this.UsersTable.getCreateTableCommandText();
        db.execSQL(res);
        
        Log.d(TableName, res);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String res = this.UsersTable.getDropTableCommandText();
        db.execSQL(res);
        this.onCreate(db);
    }
}
