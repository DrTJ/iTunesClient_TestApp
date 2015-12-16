package ir.royapajoohesh.itunesclient.data;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/* Created by Dr TJ @ March 2015 */

public class AppCategories implements Parcelable {
    public long id;
	public String term;
	public String scheme;
	public String label;
	
	public ArrayList<Apps> AppsList;

	public static String Column_id = "id";
	public static String Column_term = "term";
	public static String Column_scheme = "scheme";
	public static String Column_label = "label";

	public AppCategories() {
		this.AppsList = new ArrayList<Apps>();
	}

	public AppCategories(long id, String term, String scheme, String label) { 
		this.id = id;
		this.term = term;
		this.scheme = scheme;
		this.label = label;
		
		this.AppsList = new ArrayList<Apps>();
	}

    public AppCategories(Parcel in) {
        this.id = in.readLong();
		this.term = in.readString();
		this.scheme = in.readString();
		this.label = in.readString();
		
		this.AppsList = new ArrayList<Apps>();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
		dest.writeString(this.term);
		dest.writeString(this.scheme);
		dest.writeString(this.label);
    }

    @Override
    public String toString() {
        return "id: " + this.id;
    }

    public static final Parcelable.Creator<AppCategories> CREATOR = new Creator<AppCategories>() {
        @Override
        public AppCategories[] newArray(int size) {
            return new AppCategories[size];
        }

        @Override
        public AppCategories createFromParcel(Parcel source) {
            return new AppCategories(source);
        }
    };


    // static methods
    public static AppCategories FromJSon(JSONObject jsonItem) throws JSONException {
        AppCategories res = new AppCategories();

		JSONObject catAttributes = jsonItem.getJSONObject("category").getJSONObject("attributes");

		res.id = catAttributes.getLong("im:id");
		res.term = catAttributes.getString("term");
		res.scheme = catAttributes.getString("scheme");
		res.label = catAttributes.getString("label");
		
        return res;
    }

    public static AppCategories FromCursor(Cursor cursorItem) {
        AppCategories res = new AppCategories();

        res.id = cursorItem.getLong(cursorItem.getColumnIndex("id"));
		res.term = cursorItem.getString(cursorItem.getColumnIndex("term"));
		res.scheme = cursorItem.getString(cursorItem.getColumnIndex("scheme"));
		res.label = cursorItem.getString(cursorItem.getColumnIndex("label"));

        return res;
    }

}
