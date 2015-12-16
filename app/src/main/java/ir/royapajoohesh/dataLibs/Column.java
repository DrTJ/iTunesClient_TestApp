package ir.royapajoohesh.dataLibs;

import java.util.ArrayList;

/* Created by DrTJ @ March 2015 */

public class Column {

	public String Name;
	public String ColumnType;
	public boolean Boolean1;
	public boolean Boolean2;
	
	public static String GetDefenitionForCreateTable(ArrayList<Column> columns) {
		String res = "";
		
		for (Column column : columns) {
			if(res.length() > 0)
				res += ",";
			
			res += "`{ColumnName}` {ColumnType}".replace("{ColumnName}", column.Name)
												.replace("{ColumnType}", column.ColumnType);
		}
		
		return res;
	}

}
