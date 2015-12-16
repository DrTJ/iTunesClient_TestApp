package ir.royapajoohesh.itunesclient.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/* Created by Dr TJ @ March 2015 */

public class Users implements Parcelable {
    public int IDUser;
	public String Username;
	public String Password;
	public String FirstName;
	public String LastName;
	public String Email;
	public String Mobile;

	public static String Column_IDUser = "IDUser";
	public static String Column_Username = "Username";
	public static String Column_Password = "Password";
	public static String Column_FirstName = "FirstName";
	public static String Column_LastName = "LastName";
	public static String Column_Email = "Email";
	public static String Column_Mobile = "Mobile";

    public Users() { }

	public Users(int iDUser, String username, String password, String firstName, String lastName, String email, String mobile) { 
		this.IDUser = iDUser;
		this.Username = username;
		this.Password = password;
		this.FirstName = firstName;
		this.LastName = lastName;
		this.Email = email;
		this.Mobile = mobile;
	}

    public Users(Parcel in) {
        this.IDUser = in.readInt();
		this.Username = in.readString();
		this.Password = in.readString();
		this.FirstName = in.readString();
		this.LastName = in.readString();
		this.Email = in.readString();
		this.Mobile = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.IDUser);
		dest.writeString(this.Username);
		dest.writeString(this.Password);
		dest.writeString(this.FirstName);
		dest.writeString(this.LastName);
		dest.writeString(this.Email);
		dest.writeString(this.Mobile);
    }

    @Override
    public String toString() {
        return "IDUser: " + this.IDUser;
    }

    public static final Parcelable.Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }

        @Override
        public Users createFromParcel(Parcel source) {
            return new Users(source);
        }
    };


    // static methods
    public static Users FromJSon(JSONObject jsonItem) throws JSONException {
        Users res = new Users();

        res.IDUser = jsonItem.getInt("IDUser");
		res.Username = jsonItem.getString("Username");
		res.Password = jsonItem.getString("Password");
		res.FirstName = jsonItem.getString("FirstName");
		res.LastName = jsonItem.getString("LastName");
		res.Email = jsonItem.getString("Email");
		res.Mobile = jsonItem.getString("Mobile");

        return res;
    }

    public static Users FromCursor(Cursor cursorItem) {
        Users res = new Users();

        res.IDUser = cursorItem.getInt(cursorItem.getColumnIndex("IDUser"));
		res.Username = cursorItem.getString(cursorItem.getColumnIndex("Username"));
		res.Password = cursorItem.getString(cursorItem.getColumnIndex("Password"));
		res.FirstName = cursorItem.getString(cursorItem.getColumnIndex("FirstName"));
		res.LastName = cursorItem.getString(cursorItem.getColumnIndex("LastName"));
		res.Email = cursorItem.getString(cursorItem.getColumnIndex("Email"));
		res.Mobile = cursorItem.getString(cursorItem.getColumnIndex("Mobile"));

        return res;
    }

}
