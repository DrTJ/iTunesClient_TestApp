package ir.royapajoohesh.itunesclient.data;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import ir.royapajoohesh.dataLibs.TableBase;

/* Created by Dr TJ @ March 2015  */

public class AppCategoriesDataSource {
	SQLiteDatabase database;
	AppCategoriesOpenHelper dbHelper;
	Context context;

	public static String[] AllColumns = { "1 as _id", "id", "term", "scheme", "label" };

	public AppCategoriesDataSource(Context context) {
		this.dbHelper = new AppCategoriesOpenHelper(context);
		this.context = context;
	}

	public void Open() {
		this.database = dbHelper.getWritableDatabase();
	}

	public void Close() {
		this.database.close();
		dbHelper.close();
	}

	public AppCategories Insert(AppCategories newAppCategoriesValues, Boolean overwriteExisting) {
		if (this.IsDuplicate(newAppCategoriesValues.id)) {
			if (overwriteExisting)
				return Update(newAppCategoriesValues);
			else
				return GetItem(newAppCategoriesValues.id);
		}

		ContentValues values = new ContentValues();

		values.put("id", newAppCategoriesValues.id);
		values.put("term", newAppCategoriesValues.term);
		values.put("scheme", newAppCategoriesValues.scheme);
		values.put("label", newAppCategoriesValues.label);

		this.database.insert(AppCategoriesOpenHelper.TableName, null, values);
		return newAppCategoriesValues;
	}

	public AppCategories Update(AppCategories newAppCategoriesValues) {
		ContentValues values = new ContentValues();

		values.put("term", newAppCategoriesValues.term);
		values.put("scheme", newAppCategoriesValues.scheme);
		values.put("label", newAppCategoriesValues.label);

		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(newAppCategoriesValues.id);

		this.database.update(AppCategoriesOpenHelper.TableName, values, "id = ?", whereArgs);
		return newAppCategoriesValues;
	}

	public int DeleteRow(long id) {
		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(id);

		return this.database.delete(AppCategoriesOpenHelper.TableName, "id = ?", whereArgs);
	}

	public Cursor SelectAllAsCursor() {
		Cursor cursor = this.database.query(AppCategoriesOpenHelper.TableName, AppCategoriesDataSource.AllColumns,
				null, null, null, null, null);
		return cursor;
	}

	public ArrayList<AppCategories> SelectAll() {
		ArrayList<AppCategories> res = new ArrayList<AppCategories>();
		Cursor cursor = this.database.query(AppCategoriesOpenHelper.TableName, AppCategoriesDataSource.AllColumns,
				null, null, null, null, AppCategories.Column_term);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				AppCategories item = AppCategories.FromCursor(cursor);
				item.AppsList = AppsDataSource.GetByCategoryID(item.id, -1, context, 0);

				res.add(item);
			}
		}
		cursor.close();
		return res;
	}

	public ArrayList<AppCategories> Select(String whereClause, String orderByClause) {
		ArrayList<AppCategories> res = new ArrayList<AppCategories>();
		Cursor cursor = this.database.query(AppCategoriesOpenHelper.TableName, AppCategoriesDataSource.AllColumns,
				whereClause, null, null, null, orderByClause);

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				AppCategories item = AppCategories.FromCursor(cursor);
				item.AppsList = AppsDataSource.GetByCategoryID(item.id, context, 0);

				res.add(item);
			}
		}

		cursor.close();
		return res;
	}

	public void ReplaceRows(ArrayList<AppCategories> res) {
		if (TableBase.isTableExists(this.database, AppCategoriesOpenHelper.TableName) == false) {
			this.dbHelper.onCreate(this.database);
		}

		// clear all rows
		this.database.delete(AppCategoriesOpenHelper.TableName, null, null);

		// add new one
		for (AppCategories item : res) {
			Insert(item, true);
		}
	}

	public Boolean IsDuplicate(long id) {
		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(id);

		if (TableBase.isTableExists(this.database, AppCategoriesOpenHelper.TableName) == false)
			return false;

		Cursor cursor = this.database.query(AppCategoriesOpenHelper.TableName, AppCategoriesDataSource.AllColumns,
				"id = ?", whereArgs, null, null, null);
		int count = cursor.getCount();
		cursor.close();

		return count > 0;
	}

	public AppCategories GetItem(long id) {
		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(id);

		Cursor cursor = this.database.query(AppCategoriesOpenHelper.TableName, AppCategoriesDataSource.AllColumns,
				"id = ?", whereArgs, null, null, null);

		AppCategories res;
		if (cursor.moveToNext()) {
			res = AppCategories.FromCursor(cursor);
		} else
			res = null;
		
		cursor.close();
		return res;
	}

	public static void DeleteAll(Context context) {
		AppCategoriesDataSource dsCat = new AppCategoriesDataSource(context);

		dsCat.Open();
		if (TableBase.isTableExists(dsCat.database, AppCategoriesOpenHelper.TableName))
			dsCat.database.delete(AppCategoriesOpenHelper.TableName, null, null);
		dsCat.Close();
	}

	public static void AddIfNotExists(Context context, long id, String term, String link, String label) {
		AppCategoriesDataSource dsCat = new AppCategoriesDataSource(context);

		dsCat.Open();
		dsCat.Insert(new AppCategories(id, term, link, label), false);
		dsCat.Close();
	}

	public static AppCategories GetCategoryByID(long categoryID, Context context) {
		AppCategoriesDataSource ds = new AppCategoriesDataSource(context);
		ds.Open();
		AppCategories item = ds.GetItem(categoryID);
		ds.Close();

		return item;
	}
	
	public static AppCategories findCategoryByID(long categoryID, ArrayList<AppCategories> list) {
		for (AppCategories item : list) {
			if(item.id == categoryID)
				return item;
		}
		
		return null;
	}
}