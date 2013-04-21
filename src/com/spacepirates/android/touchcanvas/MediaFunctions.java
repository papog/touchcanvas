package com.spacepirates.android.touchcanvas;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaFunctions {
	/**
	 * obtain the path of an image managed by the MediaStore
	 * @param uri
	 * @return path of image
	 */
	public static String getPath(Activity activity, Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	public static String getPath(ContentResolver resolver, int mediaId) {
		Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + mediaId);
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = resolver.query(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    String result = cursor.getString(column_index);
	    cursor.close();
	    return result;
	}

}
