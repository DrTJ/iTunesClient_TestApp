package ir.royapajoohesh.itunesclient.data;

import ir.royapajoohesh.dataLibs.TableBase;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/* Created by Dr TJ @ March 2015  */

public class AppsDataSource {
	SQLiteDatabase database;
	AppsOpenHelper dbHelper;

	public static String[] AllColumns = {"1 as _id", "id", "bundleId", "name", "title", "summary", "priceAmmount", "priceCurrency", "contentType", "rights", "link", "artistName", "artistLink", "categoryID", "releaseDate", "releaseDateLabel"};

	public AppsDataSource(Context context) {
		this.dbHelper = new AppsOpenHelper(context);
	}

	public void Open() {
		this.database = dbHelper.getWritableDatabase();
	}

	public void Close() {
		this.database.close();
		dbHelper.close();
	}

	public Apps Insert(Apps newAppsValues, Boolean overwriteExisting) {
		if (TableBase.isTableExists(this.database, AppsOpenHelper.TableName) == false) {
			this.dbHelper.onCreate(this.database);
		}

		if (this.IsDuplicate(newAppsValues.id)) {
			if (overwriteExisting) return Update(newAppsValues);
			else return GetItem(newAppsValues.id);
		}

		ContentValues values = new ContentValues();

		values.put("id", newAppsValues.id);
		values.put("bundleId", newAppsValues.bundleId);
		values.put("name", newAppsValues.name);
		values.put("title", newAppsValues.title);
		values.put("summary", newAppsValues.summary);
		values.put("priceAmmount", newAppsValues.priceAmmount);
		values.put("priceCurrency", newAppsValues.priceCurrency);
		values.put("contentType", newAppsValues.contentType);
		values.put("rights", newAppsValues.rights);
		values.put("link", newAppsValues.link);
		values.put("artistName", newAppsValues.artistName);
		values.put("artistLink", newAppsValues.artistLink);
		values.put("categoryID", newAppsValues.categoryID);
		values.put("releaseDate", newAppsValues.releaseDate);
		values.put("releaseDateLabel", newAppsValues.releaseDateLabel);

		this.database.insert(AppsOpenHelper.TableName, null, values);
		return newAppsValues;
	}

	private boolean IsDuplicate(long id) {
		if (TableBase.isTableExists(this.database, AppsOpenHelper.TableName) == false) return false;

		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(id);

		Cursor cursor = this.database.query(AppsOpenHelper.TableName, AppsDataSource.AllColumns, "id = ?", whereArgs, null, null, null);
		int count = cursor.getCount();
		cursor.close();

		return count > 0;
	}

	private Apps GetItem(long id) {
		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(id);

		Cursor cursor = this.database.query(AppIconsOpenHelper.TableName, AppsDataSource.AllColumns, "id = ?", whereArgs, null, null, null);

		Apps res;
		if (cursor.moveToNext()) {
			res = Apps.FromCursor(cursor);
		} else res = null;

		cursor.close();
		return res;
	}

	public Apps Update(Apps newAppsValues) {
		ContentValues values = new ContentValues();

		values.put("bundleId", newAppsValues.bundleId);
		values.put("name", newAppsValues.name);
		values.put("title", newAppsValues.title);
		values.put("summary", newAppsValues.summary);
		values.put("priceAmmount", newAppsValues.priceAmmount);
		values.put("priceCurrency", newAppsValues.priceCurrency);
		values.put("contentType", newAppsValues.contentType);
		values.put("rights", newAppsValues.rights);
		values.put("link", newAppsValues.link);
		values.put("artistName", newAppsValues.artistName);
		values.put("artistLink", newAppsValues.artistLink);
		values.put("categoryID", newAppsValues.categoryID);
		values.put("releaseDate", newAppsValues.releaseDate);
		values.put("releaseDateLabel", newAppsValues.releaseDateLabel);

		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(newAppsValues.id);

		this.database.update(AppsOpenHelper.TableName, values, "id = ?", whereArgs);
		return newAppsValues;
	}

	public int DeleteRow(long id) {
		String whereArgs[] = new String[1];
		whereArgs[0] = String.valueOf(id);

		return this.database.delete(AppsOpenHelper.TableName, "id = ?", whereArgs);
	}

	public Cursor SelectAllAsCursor() {
		Cursor cursor = this.database.query(AppsOpenHelper.TableName, AppsDataSource.AllColumns, null, null, null, null, null);
		return cursor;
	}

	public ArrayList<Apps> SelectAll() {
		ArrayList<Apps> res = new ArrayList<Apps>();
		Cursor cursor = this.database.query(AppsOpenHelper.TableName, AppsDataSource.AllColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Apps item = Apps.FromCursor(cursor);
				res.add(item);
			}
		}

		cursor.close();
		return res;
	}

	public ArrayList<Apps> Select(Context context, String whereClause, String orderByClause, boolean loadIcons, int count) {
		ArrayList<Apps> res = new ArrayList<Apps>();
		Cursor cursor = this.database.query(AppsOpenHelper.TableName, AppsDataSource.AllColumns, whereClause, null, null, null, orderByClause);

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Apps item = Apps.FromCursor(cursor);
				if (loadIcons) {
					item.IconsList = AppIcons.Get(item.id, context);
				}
				res.add(item);

				if (count > 0 && res.size() > count) break;
			}
		}

		cursor.close();
		return res;
	}

	public ArrayList<Apps> SelectByCategoryID(long categoryID, Context context, int count) {
		return Select(context, "categoryID = " + categoryID, "name", true, count);
	}

	public void ReplaceRows(ArrayList<Apps> res) {
		// clear all rows

		if (TableBase.isTableExists(this.database, AppsOpenHelper.TableName) == false) {
			this.dbHelper.onCreate(this.database);
		}

		this.database.delete(AppsOpenHelper.TableName, null, null);

		for (Apps item : res) {
			Insert(item, true);
		}
	}

	public static void Insert(Context context, Apps appItem) {
		AppsDataSource dsApps = new AppsDataSource(context);
		dsApps.Open();
		dsApps.Insert(appItem, true);
		dsApps.Close();
	}

	public static void DeleteAll(Context context) {
		AppsDataSource ds = new AppsDataSource(context);

		ds.Open();
		if (TableBase.isTableExists(ds.database, AppsOpenHelper.TableName))
			ds.database.delete(AppsOpenHelper.TableName, null, null);
		ds.Close();
	}

	public static ArrayList<Apps> GetByCategoryID(long idCategory, Context context, int count) {
		return GetByCategoryID(idCategory, -1, context, count);
	}

	public static ArrayList<Apps> GetByCategoryID(long idCategory, long currentAppIDToRemove, Context context, int count) {
        String whereClause = String.format("(%1$s = %2$s)", Apps.Column_categoryID, idCategory);
        if(currentAppIDToRemove != -1){
            whereClause +=  String.format(" and (%1$s <> %2$d)", Apps.Column_id, currentAppIDToRemove);
        }

		AppsDataSource ds = new AppsDataSource(context);
		ds.Open();
		ArrayList<Apps> list = ds.Select(context, whereClause, Apps.Column_id, false, count);
		ds.Close();

		for (Apps apps : list) {
			apps.IconsList = AppIcons.Get(apps.id, context);
		}

		return list;
	}

	public static ArrayList<Apps> GetByProducer(String producerName, long currentAppIDToRemove, Context context, int count) {
        String whereClause = String.format("(%1$s = \"%2$s\")", Apps.Column_artistName, producerName);	//.replace("\'", "\\\'")
        if(currentAppIDToRemove != -1){
            whereClause +=  String.format(" and (%1$s <> %2$d)", Apps.Column_id, currentAppIDToRemove);
        }

		Log.d("whereClause", whereClause);

		AppsDataSource ds = new AppsDataSource(context);
		ds.Open();
		ArrayList<Apps> list = ds.Select(context, whereClause, Apps.Column_name, false, count);
		ds.Close();

		for (Apps apps : list) {
			apps.IconsList = AppIcons.Get(apps.id, context);
		}

		return list;
	}
}