package ir.royapajoohesh.dataLibs;

import java.util.ArrayList;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

/* Created by DrTJ @ March 2015 */


public class TableBase {

	public String DatabaseName;
	public String TableName;
	public ArrayList<Column> Columns;

	public TableBase(String databasename, String tablename) {
		this.DatabaseName = databasename;
		this.TableName = tablename;
		this.Columns = new ArrayList<Column>();
	}
	
	public void AddColumn(String columnName, String columnType, boolean boolean1, boolean boolean2) {
		Column newItem = new Column();
		newItem.Name = columnName ;
		newItem.ColumnType = columnType;
		newItem.Boolean1 = boolean1;
		newItem.Boolean2 = boolean2;
				
		this.Columns.add(newItem);
	}

	public String getCreateTableCommandText() {
		String colDef = Column.GetDefenitionForCreateTable(this.Columns);	

		String res = "CREATE TABLE `{TableName}` ({ColumnsDeclaration});"
					 .replace("{TableName}", this.TableName)
					 .replace("{ColumnsDeclaration}", colDef);
		
		return res;
	}

	public String getDropTableCommandText() {
		String res = "DROP TABLE IF EXISTS " + this.TableName;
		return res;
	}

	public static boolean isTableExists(SQLiteDatabase db, String tableName) {
		if (tableName == null || db == null || !db.isOpen()) {
			return false;
		}
		
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
									new String[] { "table", tableName });
		
		if (!cursor.moveToFirst()) {
			return false;
		}
		
		int count = cursor.getInt(0);
		cursor.close();
		return count > 0;
	}
}
