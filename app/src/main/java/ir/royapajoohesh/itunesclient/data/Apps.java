package ir.royapajoohesh.itunesclient.data;

import ir.royapajoohesh.itunesclient.R;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/* Created by Dr TJ @ March 2015 */

public class Apps implements Parcelable {
    public long id;
	public String bundleId;
	public String name;
	public String title;
	public String summary;
	public float priceAmmount;
	public String priceCurrency;
	public String contentType;
	public String rights;
	public String link;
	public String artistName;
	public String artistLink;
	public long categoryID;
	public String releaseDate;
	public String releaseDateLabel;
	
	public ArrayList<AppIcons> IconsList;

	
	public static String Column_id = "id";
	public static String Column_bundleId = "bundleId";
	public static String Column_name = "name";
	public static String Column_title = "title";
	public static String Column_summary = "summary";
	public static String Column_priceAmmount = "priceAmmount";
	public static String Column_priceCurrency = "priceCurrency";
	public static String Column_contentType = "contentType";
	public static String Column_rights = "rights";
	public static String Column_link = "link";
	public static String Column_artistName = "artistName";
	public static String Column_artistLink = "artistLink";
	public static String Column_categoryID = "categoryID";
	public static String Column_releaseDate = "releaseDate";
	public static String Column_releaseDateLabel = "releaseDateLabel";

    public Apps() { }

	public Apps(long id, String bundleId, String name, String title, String summary, float priceAmmount, String priceCurrency, String contentType, String rights, String link, String artistName, String artistLink, long categoryID, String releaseDate, String releaseDateLabel) { 
		this.id = id;
		this.bundleId = bundleId;
		this.name = name;
		this.title = title;
		this.summary = summary;
		this.priceAmmount = priceAmmount;
		this.priceCurrency = priceCurrency;
		this.contentType = contentType;
		this.rights = rights;
		this.link = link;
		this.artistName = artistName;
		this.artistLink = artistLink;
		this.categoryID = categoryID;
		this.releaseDate = releaseDate;
		this.releaseDateLabel = releaseDateLabel;
		
		this.IconsList = new ArrayList<AppIcons>();
	}

    public Apps(Parcel in) {
        this.id = in.readLong();
		this.bundleId = in.readString();
		this.name = in.readString();
		this.title = in.readString();
		this.summary = in.readString();
		this.priceAmmount = in.readFloat();
		this.priceCurrency = in.readString();
		this.contentType = in.readString();
		this.rights = in.readString();
		this.link = in.readString();
		this.artistName = in.readString();
		this.artistLink = in.readString();
		this.categoryID = in.readLong();
		this.releaseDate = in.readString();
		this.releaseDateLabel = in.readString();
		
		this.IconsList = new ArrayList<AppIcons>();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
		dest.writeString(this.bundleId);
		dest.writeString(this.name);
		dest.writeString(this.title);
		dest.writeString(this.summary);
		dest.writeFloat(this.priceAmmount);
		dest.writeString(this.priceCurrency);
		dest.writeString(this.contentType);
		dest.writeString(this.rights);
		dest.writeString(this.link);
		dest.writeString(this.artistName);
		dest.writeString(this.artistLink);
		dest.writeLong(this.categoryID);
		dest.writeString(this.releaseDate);
		dest.writeString(this.releaseDateLabel);
    }

    @Override
    public String toString() {
        return "id: " + this.id;
    }

    public static final Parcelable.Creator<Apps> CREATOR = new Creator<Apps>() {
        @Override
        public Apps[] newArray(int size) {
            return new Apps[size];
        }

        @Override
        public Apps createFromParcel(Parcel source) {
            return new Apps(source);
        }
    };


    // static methods
    public static Apps FromJSon(JSONObject jsonItem, Context context) throws JSONException {
        Apps res = new Apps();

        JSONObject idAttributes = jsonItem.getJSONObject("id").getJSONObject("attributes");
		res.id = idAttributes.getLong("im:id");
		res.bundleId = idAttributes.getString("im:bundleId");
		
		res.name = jsonItem.getJSONObject("im:name").getString("label");
		res.title = jsonItem.getJSONObject("title").getString("label");

		res.summary = (jsonItem.has("summary")) ? jsonItem.getJSONObject("summary").getString("label") : "";
		
        JSONObject priceAttributes = jsonItem.getJSONObject("im:price").getJSONObject("attributes");
		res.priceAmmount = (float) priceAttributes.getDouble("amount");
		res.priceCurrency = priceAttributes.getString("currency");
		
		res.contentType = jsonItem.getJSONObject("im:contentType").getJSONObject("attributes").getString("label");
		res.rights = jsonItem.getJSONObject("rights").getString("label");
		res.link = jsonItem.getJSONObject("link").getJSONObject("attributes").getString("href");

		JSONObject tmpArtist = jsonItem.getJSONObject("im:artist");

		res.artistName = tmpArtist.getString("label");
		res.artistLink = tmpArtist.has("attributes") ? tmpArtist.getJSONObject("attributes").getString("href") : "";

		res.releaseDate = jsonItem.getJSONObject("im:releaseDate").getString("label");
		res.releaseDateLabel = jsonItem.getJSONObject("im:releaseDate").getJSONObject("attributes").getString("label");
		
		JSONObject catAttributes = jsonItem.getJSONObject("category").getJSONObject("attributes");
		res.categoryID = catAttributes.getLong("im:id");
		
		// parse app icons
		res.IconsList = new ArrayList<AppIcons>();
		JSONArray iconsArray = jsonItem.getJSONArray("im:image");
		for (int i = 0; i < iconsArray.length(); i++) {
			int imgID = (int) (res.id * 100 + i);
			JSONObject iconJSon = iconsArray.getJSONObject(i);
			
			AppIcons newIcon = AppIcons.FromJSon(iconJSon, res.id, imgID);
			//newIcon.ImagePath = iTunesDownloader.downloadAppIcon(context, res.id, newIcon.ImagePath, newIcon.Height);	//// here or up?!!!
			res.IconsList.add(newIcon);
		}
		
		return res;
    }

    public static Apps FromCursor(Cursor cursorItem) {
        Apps res = new Apps();

        res.id = cursorItem.getLong(cursorItem.getColumnIndex("id"));
		res.bundleId = cursorItem.getString(cursorItem.getColumnIndex("bundleId"));
		res.name = cursorItem.getString(cursorItem.getColumnIndex("name"));
		res.title = cursorItem.getString(cursorItem.getColumnIndex("title"));
		res.summary = cursorItem.getString(cursorItem.getColumnIndex("summary"));
		res.priceAmmount = cursorItem.getFloat(cursorItem.getColumnIndex("priceAmmount"));
		res.priceCurrency = cursorItem.getString(cursorItem.getColumnIndex("priceCurrency"));
		res.contentType = cursorItem.getString(cursorItem.getColumnIndex("contentType"));
		res.rights = cursorItem.getString(cursorItem.getColumnIndex("rights"));
		res.link = cursorItem.getString(cursorItem.getColumnIndex("link"));
		res.artistName = cursorItem.getString(cursorItem.getColumnIndex("artistName"));
		res.artistLink = cursorItem.getString(cursorItem.getColumnIndex("artistLink"));
		res.categoryID = cursorItem.getLong(cursorItem.getColumnIndex("categoryID"));
		res.releaseDate = cursorItem.getString(cursorItem.getColumnIndex("releaseDate"));
		res.releaseDateLabel = cursorItem.getString(cursorItem.getColumnIndex("releaseDateLabel"));

        return res;
    }

	public String GetPrice(Context context) {
		String res = context.getResources().getString(R.string.FreeCostTitle);
		if(this.priceAmmount > 0.0){
			res = "$" + this.priceAmmount + " " + this.priceCurrency;
		}
		
		return res;
	}

}
