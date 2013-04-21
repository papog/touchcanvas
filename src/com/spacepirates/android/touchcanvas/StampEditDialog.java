package com.spacepirates.android.touchcanvas;


import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * StampEditDialog implements a dialog box to choose the image to be used for the
 * stamping tool. 
 *
 */
public class StampEditDialog extends Dialog {

	private Cursor cursor;
	private int columnIndex;
	private int imageIndex;
	
	public StampEditDialog(Context c, OnStampChangedListener l){
		super(c);
		this.stampListener = l;
	}
	public interface OnStampChangedListener {
		void stampChanged(int imageId, int ratioMode);
	}

	private OnStampChangedListener stampListener;


	/** associateClickListener will simply locate the view designated by id and bind the listener**/
	private void associateClickListener(int id, android.view.View.OnClickListener listener){
		View tmpView = (View) findViewById(id);
		tmpView.setOnClickListener(listener);
	}
	/** associateClickListener will simply locate the view designated by id and bind the listener**/
	private void associateItemClickListener(int id, android.widget.AdapterView.OnItemClickListener listener){
		AdapterView tmpView = (AdapterView) findViewById(id);
		tmpView.setOnItemClickListener(listener);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stamp_edit_dialog);
		setTitle("Edit Stamp tool properties");
		associateClickListener(R.id.applyButton, new android.view.View.OnClickListener (){

			@Override
			public void onClick(View v) {
				applyChanges();
			}});
		associateItemClickListener(R.id.thumbnailGrid, new android.widget.AdapterView.OnItemClickListener (){

		
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Log.i("stampedit","item clicked "+ position + " " + id);
				imageIndex = (Integer) adapterView.getItemAtPosition(position);
			}});

		setupGridViewSource();

	}

	public static int ratioId(int buttonId){
		switch(buttonId){
		case R.id.lockAspectRatioButton:
			return StampingTool.LOCK_ASPECT_RATIO_ID;
		case R.id.freeScaleButton:
			return StampingTool.FREESCALE_ID;
		}
		return -1;
	}
	
	
	protected void applyChanges() {
		int ratio;
		RadioGroup rg = (RadioGroup) findViewById(R.id.ScaleRuleGroup);
		ratio = ratioId(rg.getCheckedRadioButtonId());
		stampListener.stampChanged(imageIndex, ratio);
		dismiss();
	}

	public void setupGridViewSource(){
		GridView g = (GridView) findViewById(R.id.thumbnailGrid);
		// request only the image ID to be returned
		String[] projection = {MediaStore.Images.Media._ID};
		// Create the cursor pointing to the SDCard
		cursor = getContext().getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projection, 
				MediaStore.Images.Media.DATA + " like ? ",
				new String[] {"%sdcard%"},  
				null);
		// Get the column index of the image ID
		columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
		Log.i("thumbnailGrid", "got images:" + cursor.getCount());
		g.setAdapter(new ImageAdapter(getContext()));
	}


	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return cursor.getCount();
		}

		public Object getItem(int position) {
			cursor.moveToPosition(position);
			return new Integer(cursor.getInt(columnIndex));
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			// Move cursor to current position
			cursor.moveToPosition(position);
			// Get the current value for the requested column
			int imageID = cursor.getInt(columnIndex);
			Bitmap b = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),
					imageID, MediaStore.Images.Thumbnails.MICRO_KIND, null);
			i.setImageBitmap(b);
			i.setLayoutParams(new GridView.LayoutParams(100, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			return i;
		}

	}

}
