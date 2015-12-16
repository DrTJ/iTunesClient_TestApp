package ir.royapajoohesh.itunesclient.data;

import ir.royapajoohesh.dataLibs.TableBase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/* Created by Dr TJ @ March 2015  */

public class AppIconsDataSource {
	SQLiteDatabase database;
	AppIconsOpenHelper dbHelper;

	public static String[] AllColumns = { "1 as _id", "appID", "imageID", "ImagePath", "Height" };

	public AppIconsDataSource(Context context) {
		this.dbHelper = new AppIconsOpenHelper(context);
	}

	public void Open() {
		this.database = dbHelper.getWritableDatabase();
	}

	public void Close() {
		this.database.close();
		dbHelper.close();
	}

	public AppIcons Insert(AppIcons newAppIconsValues, Boolean overwriteExisting) {
		if (TableBase.isTableExists(this.database, AppIconsOpenHelper.TableName) == false) {
			this.dbHelper.onCreate(this.database);
		}

		if (this.IsDuplicate(newAppIconsValues.appID, newAppIconsValues.imageID)) {
			if (overwriteExisting)
				return Update(newAppIconsValues);
			else
				return GetItem(newAppIconsValues.appID, newAppIconsValues.imageID);
		}

		ContentValues values = new ContentValues();

		values.put("appID", newAppIconsValues.appID);
		values.put("imageID", newAppIconsValues.imageID);
		values.put("ImagePath", newAppIconsValues.ImagePath);
		values.put("Height", newAppIconsValues.Height);

		this.database.insert(AppIconsOpenHelper.TableName, null, values);
		return newAppIconsValues;
	}

	public AppIcons Update(AppIcons newAppIconsValues) {
		ContentValues values = new ContentValues();

		values.put("ImagePath", newAppIconsValues.ImagePath);
		values.put("Height", newAppIconsValues.Height);

		String whereArgs[] = new String[2];
		whereArgs[0] = String.valueOf(newAppIconsValues.appID);
		whereArgs[1] = String.valueOf(newAppIconsValues.imageID);

		this.database.update(AppIconsOpenHelper.TableName, values, "appID = ?, imageID = ?", whereArgs);
		return newAppIconsValues;
	}

	public int DeleteRow(long appID, int imageID) {
		String whereArgs[] = new String[2];
		whereArgs[0] = String.valueOf(appID);
		whereArgs[1] = String.valueOf(imageID);

		return this.database.delete(AppIconsOpenHelper.TableName, "appID = ?, imageID = ?", whereArgs);
	}

	public Cursor SelectAllAsCursor() {
		Cursor cursor = this.database.query(AppIconsOpenHelper.TableName, AppIconsDataSource.AllColumns, null, null,
				null, null, null);
		return cursor;
	}

	public ArrayList<AppIcons> SelectAll() {
		ArrayList<AppIcons> res = new ArrayList<AppIcons>();
		Cursor cursor = this.database.query(AppIconsOpenHelper.TableName, AppIconsDataSource.AllColumns, null, null,
				null, null, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				res.add(AppIcons.FromCursor(cursor));
			}
		}

		cursor.close();
		return res;
	}

	public AppIcons GetItem(long appID, int imageID) {
		String whereClause = "appID = " + appID + " and imageID = " + imageID;

		Cursor cursor = this.database.query(AppIconsOpenHelper.TableName, AppIconsDataSource.AllColumns, whereClause,
				null, null, null, null);

		AppIcons res;
		if (cursor.moveToNext()) {
			res = AppIcons.FromCursor(cursor);
		} else
			res = null;

		cursor.close();
		return res;
	}

	public ArrayList<AppIcons> Select(String whereClause, String orderByClause) {
		ArrayList<AppIcons> res = new ArrayList<AppIcons>();
		Cursor cursor = this.database.query(AppIconsOpenHelper.TableName, AppIconsDataSource.AllColumns, whereClause,
				null, null, null, orderByClause);

		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				res.add(AppIcons.FromCursor(cursor));
			}
		}

		cursor.close();
		return res;
	}

	public void ReplaceRows(ArrayList<AppIcons> res) {
		// clear all rows

		if (TableBase.isTableExists(this.database, AppIconsOpenHelper.TableName) == false) {
			this.dbHelper.onCreate(this.database);
		}

		this.database.delete(AppIconsOpenHelper.TableName, null, null);

		for (AppIcons item : res) {
			Insert(item, true);
		}
	}

	public Boolean IsDuplicate(long appID, int imageID) {
		String whereClause = "appID = " + appID + " and imageID = " + imageID;

		if (TableBase.isTableExists(this.database, AppIconsOpenHelper.TableName) == false)
			return false;

		Cursor cursor = this.database.query(AppIconsOpenHelper.TableName, AppIconsDataSource.AllColumns, whereClause,
				null, null, null, null);
		int count = cursor.getCount();
		cursor.close();

		return count > 0;
	}

	public static void Insert(Context context, AppIcons newItem) {
		AppIconsDataSource dsAppIcons = new AppIconsDataSource(context);
		dsAppIcons.Open();
		dsAppIcons.Insert(newItem, true);
		dsAppIcons.Close();
	}

	public static void DeleteAll(Context context) {
		AppIconsDataSource ds = new AppIconsDataSource(context);

		ds.Open();
		if (TableBase.isTableExists(ds.database, AppIconsOpenHelper.TableName))
			ds.database.delete(AppIconsOpenHelper.TableName, null, null);
		ds.Close();
	}

	public static Bitmap GetLargestIcon(long id, Context context) {
		return GetLargestIcon(id, context, null);
	}

	public static Bitmap GetLargestIcon(long id, Context context, ArrayList<AppIcons> iconsList) {
		if (iconsList == null) {
			AppIconsDataSource dsAppIcons = new AppIconsDataSource(context);
			dsAppIcons.Open();
			iconsList = dsAppIcons.Select(AppIcons.Column_appID + " = " + id, AppIcons.Column_imageID);
			dsAppIcons.Close();
		}

		String largest = "";
		int maxHeight = 0;

		for (AppIcons item : iconsList) {
			if (item.Height > maxHeight) {
				maxHeight = item.Height;
				largest = item.ImagePath;
			}
		}

		Bitmap res = null;

		if (maxHeight == 0) {
			try {
				InputStream defaultIcon = context.getAssets().open("icon.png");
				res = BitmapFactory.decodeStream(defaultIcon);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			res = BitmapFactory.decodeFile(largest);
		}

		return res;
	}

	public static Bitmap GetIcon(long id, Context context, ArrayList<AppIcons> iconsList, int prefferedSize) {
		if (iconsList == null) {
			AppIconsDataSource dsAppIcons = new AppIconsDataSource(context);
			dsAppIcons.Open();
			iconsList = dsAppIcons.Select(AppIcons.Column_appID + " = " + id, AppIcons.Column_imageID);
			dsAppIcons.Close();
		}

		String iconPath = "";
		int iconHeight = 0;

		for (AppIcons item : iconsList) {
			if (item.Height > iconHeight) {
				iconHeight = item.Height;
				iconPath = item.ImagePath;

				if (iconHeight == prefferedSize && iconHeight != 0)
					break;
			}
		}

		Bitmap res = null;

		if (iconHeight == 0) {
			try {
				InputStream defaultIcon = context.getAssets().open("icon.png");
				res = BitmapFactory.decodeStream(defaultIcon);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			res = BitmapFactory.decodeFile(iconPath);
		}

		return res;
	}
}