package ir.royapajoohesh.itunesclient.data;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;
import ir.royapajoohesh.dataLibs.TableBase;

/* Created by Dr TJ @ March 2015  */

public class UsersDataSource {
    SQLiteDatabase database;
    UsersOpenHelper dbHelper;

    public static String[] AllColumns = {
        "1 as _id",
		"IDUser",
		"Username",
		"Password",
		"FirstName",
		"LastName",
		"Email",
		"Mobile"
    };

    public UsersDataSource(Context context){
        this.dbHelper = new UsersOpenHelper(context);
    }

    public void Open(){
        this.database = dbHelper.getWritableDatabase();
    }

    public void Close(){
    	this.database.close();
    	dbHelper.close();
    }

    public Users Insert(Users newUsersValues){
        ContentValues values = new ContentValues();

        values.put("IDUser", newUsersValues.IDUser);
		values.put("Username", newUsersValues.Username);
		values.put("Password", newUsersValues.Password);
		values.put("FirstName", newUsersValues.FirstName);
		values.put("LastName", newUsersValues.LastName);
		values.put("Email", newUsersValues.Email);
		values.put("Mobile", newUsersValues.Mobile);

        this.database.insert(UsersOpenHelper.TableName, null, values);
        return newUsersValues;
    }

    public Users Update(Users newUsersValues){
        ContentValues values = new ContentValues();

		values.put("Username", newUsersValues.Username);
		values.put("Password", newUsersValues.Password);
		values.put("FirstName", newUsersValues.FirstName);
		values.put("LastName", newUsersValues.LastName);
		values.put("Email", newUsersValues.Email);
		values.put("Mobile", newUsersValues.Mobile);

        String whereArgs[] = new String[1];
        whereArgs[0] = String.valueOf(newUsersValues.IDUser);

        this.database.update(UsersOpenHelper.TableName, values, "IDUser = ?", whereArgs);
        return newUsersValues;
    }

    public int DeleteRow(int IDUser){
        String whereArgs[] = new String[1];
        whereArgs[0] = String.valueOf(IDUser);

        return this.database.delete(UsersOpenHelper.TableName, "IDUser = ?", whereArgs);
    }

	public Cursor SelectAllAsCursor() {
        Cursor cursor = this.database.query(UsersOpenHelper.TableName, UsersDataSource.AllColumns, null, null, null, null, null);
        return cursor;
    }

    public ArrayList<Users> SelectAll() {
        ArrayList<Users> res = new ArrayList<Users>();
        Cursor cursor = this.database.query(UsersOpenHelper.TableName, UsersDataSource.AllColumns, null, null, null, null, null);
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                res.add(Users.FromCursor(cursor));
            }
        }

        cursor.close();
        return res;
    }


    public ArrayList<Users> Select(String whereClause, String orderByClause){
        ArrayList<Users> res = new ArrayList<Users>();
        Cursor cursor = this.database.query(UsersOpenHelper.TableName, UsersDataSource.AllColumns, whereClause, null, null, null, orderByClause);

        if(cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                res.add(Users.FromCursor(cursor));
            }
        }
        
        cursor.close();
        return res;
    }

    public void ReplaceRows(ArrayList<Users> res) {
        // clear all rows

        if (TableBase.isTableExists(this.database, UsersOpenHelper.TableName) == false){
			this.dbHelper.onCreate(this.database);
		}
		
		this.database.delete(UsersOpenHelper.TableName, null, null);

        for (Users item : res) {
            Insert(item);
        }
    }


}