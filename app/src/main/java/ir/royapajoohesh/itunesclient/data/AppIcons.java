package ir.royapajoohesh.itunesclient.data;

import ir.royapajoohesh.utils.ImageDownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* Created by Dr TJ @ March 2015 */

public class AppIcons implements Parcelable {
    public long appID;
	public int imageID;
	public String ImagePath;
	public int Height;

	public static String Column_appID = "appID";
	public static String Column_imageID = "imageID";
	public static String Column_ImagePath = "ImagePath";
	public static String Column_Height = "Height";

	public static String TAG = "AppIcons";
	public static boolean isRunning;
	
    public AppIcons() { }

	public AppIcons(long appID, int imageID, String imagePath, int height) { 
		this.appID = appID;
		this.imageID = imageID;
		this.ImagePath = imagePath;
		this.Height = height;
	}

    public AppIcons(Parcel in) {
        this.appID = in.readLong();
		this.imageID = in.readInt();
		this.ImagePath = in.readString();
		this.Height = in.readInt();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.appID);
		dest.writeInt(this.imageID);
		dest.writeString(this.ImagePath);
		dest.writeInt(this.Height);
    }

    @Override
    public String toString() {
        return "appID: " + this.appID + "\nimageID: " + this.imageID;
    }

    public static final Parcelable.Creator<AppIcons> CREATOR = new Creator<AppIcons>() {
        @Override
        public AppIcons[] newArray(int size) {
            return new AppIcons[size];
        }

        @Override
        public AppIcons createFromParcel(Parcel source) {
            return new AppIcons(source);
        }
    };


    // static methods
    public static AppIcons FromJSon(JSONObject jsonItem, long idApp, int imgID) throws JSONException {
        AppIcons res = new AppIcons();

        res.appID = idApp;
		res.imageID = imgID;
		res.ImagePath = jsonItem.getString("label");
		res.Height = jsonItem.getJSONObject("attributes").getInt("height");

        return res;
    }

    public static AppIcons FromCursor(Cursor cursorItem) {
        AppIcons res = new AppIcons();

        res.appID = cursorItem.getLong(cursorItem.getColumnIndex("appID"));
		res.imageID = cursorItem.getInt(cursorItem.getColumnIndex("imageID"));
		res.ImagePath = cursorItem.getString(cursorItem.getColumnIndex("ImagePath"));
		res.Height = cursorItem.getInt(cursorItem.getColumnIndex("Height"));

        return res;
    }

	/*public static void ParseAndInsertItem(long idApp, int idImage, JSONObject iconJSon, Context context, Boolean overwriteExisting) throws JSONException {
		AppIcons icon = AppIcons.FromJSon(iconJSon, idApp, idImage);
		
		// insert to database
		AppIconsDataSource dsIcons = new AppIconsDataSource(context);
		dsIcons.Open();
		
		// download and save the image
		String imageFileFullPath = DownloadAndSaveImage(context, icon.ImagePath);

		// insert the row to DB
		dsIcons.Insert(new AppIcons(icon.appID, icon.imageID, imageFileFullPath, icon.Height), overwriteExisting);

		dsIcons.Close();
	}*/
	
/*	public static RequestQueue queue;
	public static int activeRequestsCount;
*/
	public static void DownloadAndSaveImage(Context context, String imageURL, String localPath, String fileName) {
		File tmpPathFile = new File(localPath);
		if(!tmpPathFile.exists())
			tmpPathFile.mkdirs();
		
		isRunning = true;
		
		AsyncTask<String, String, String> call = new AsyncTask<String, String, String>(){
			@Override
			protected String doInBackground(String... params) {
				
				String imgURL = params[0];
				File imgFile = new File(params[1], params[2]);
			
				Log.d("ImageFile", "URL :" + imgURL);
				
				// if file doesnt exists, then create it
				/*if (!imgFile.exists()) {
					try {
						boolean tmpResImageFile = imgFile.createNewFile();
						Log.d("ImageFile", "Image File Made :" + tmpResImageFile + "");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
					Log.d("ImageFile", "Image file already exists!");*/

				if(!isRunning)
					return "Aborted";
							
				// download using ImageDownloader
				byte[] imgBytes = ImageDownloader.getBitmapBytesFromURL(imgURL);

				if(!isRunning)
					return "Aborted";

				Log.d("ImageFile", "Image Size :" + imgBytes.length);				
				
				FileOutputStream fileStream = null;
				try {
					fileStream = new FileOutputStream(imgFile);
					fileStream.write(imgBytes);
					fileStream.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					fileStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
/*				// download the image
				URL url;
				FileOutputStream fileStream = null;
				try {
					url = new URL(imgURL);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream inStream = conn.getInputStream();
					
					
					
					
					Bitmap resImage = BitmapFactory.decodeStream(inStream);
		
					// save the image
					fileStream = new FileOutputStream(imgFile);
					resImage.compress(CompressFormat.PNG, 100, fileStream);
					
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					if (fileStream == null)
						throw new Exception("fileStream is null!!!!!!!!!!!");
					else
						fileStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
*/				
				
				return "Download Finished";
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
			}
		};
		
		call.execute(imageURL, localPath, fileName);	//  context.getCacheDir().getPath() + "/images"
	}

	public static ArrayList<AppIcons> Get(long appID, Context context) {
		AppIconsDataSource dsIcons = new AppIconsDataSource(context);
		dsIcons.Open();
		ArrayList<AppIcons> res = dsIcons.Select("appID = "+appID, Column_appID);
		dsIcons.Close();
		
		return res;
	}
	
	
	// OLD CODE
	
/*
		if(queue == null) {
			queue = Volley.newRequestQueue(context);
			activeRequestsCount = 0;
		}
		// byte[] imageBytes = ImageDownloader.getBitmapBytesFromURL(imageURL);

		Listener<Bitmap> responseListener = new Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				activeRequestsCount--;

				// generate local path
				String fileName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
				final File imgFile = new File(this.CachePath + "images", fileName);
				String imageFileFullPath = imgFile.getPath();
				
				
				// save the image
				FileOutputStream fileStream = null;
				try {
					fileStream = new FileOutputStream(imgFile);
					response.compress(CompressFormat.JPEG, 80, fileStream);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					if (fileStream == null)
						throw new Exception("fileStream is null!!!!!!!!!!!");
					else
						fileStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		// * / 
				
		// download the image
		ImageRequest imgRequest = new ImageRequest(imageURL, responseListener, 512, 512, ScaleType.CENTER_INSIDE, Config.ARGB_8888, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				activeRequestsCount--;
				Log.d(TAG, error.getMessage());
			}
		});
		
		imgRequest.setTag(TAG);
		imgRequest.

		activeRequestsCount++;
		queue.add(imgRequest);
		if(activeRequestsCount == 0)
			queue.start();
			
		//	*/
		
		
//		// download the image
//		URL url;
//		FileOutputStream fileStream = null;
//		try {
//			url = new URL(imageURL);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoInput(true);
//			conn.connect();
//			InputStream inStream = conn.getInputStream();
//			Bitmap resImage = BitmapFactory.decodeStream(inStream);
//
//			// save the image
//			fileStream = new FileOutputStream(imgFile);
//			resImage.compress(CompressFormat.JPEG, 80, fileStream);
//			
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			if (fileStream == null)
//				throw new Exception("fileStream is null!!!!!!!!!!!");
//			else
//				fileStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
}
