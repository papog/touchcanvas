package com.spacepirates.android.touchcanvas;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

import com.lamerman.FileDialog;
/*
 * import com.spacepirates.android.touchcanvas.ColorPickerDialog;
 * import com.spacepirates.android.touchcanvas.ColorPickerDialog.OnColorChangedListener;
*/
import com.spacepirates.android.touchcanvas.DrawEditDialog.OnToolChangedListener;
import com.spacepirates.android.touchcanvas.StampEditDialog.OnStampChangedListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import android.widget.Toast;

public class MainActivity extends Activity implements OnToolChangedListener, OnStampChangedListener{

	// MyView represents the canvas where the user can draw.
	private DrawingView mView;
	private String currentFileName;
	private ToolButton mCurrentToolButton;
	
	/** associateClickListener will simply locate the view designated by id and bind the listener**/
	private void associateClickListener(int id, OnClickListener listener){
		View tmpView = (View) findViewById(id);
		tmpView.setOnClickListener(listener);
	}
	/** associateClickListener will simply locate the view designated by id and bind the listener**/
	private void associateLongClickListener(int id, OnLongClickListener listener){
		View tmpView = (View) findViewById(id);
		tmpView.setOnLongClickListener(listener);
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	
		mView = (DrawingView) findViewById(R.id.drawing);
		mCurrentToolButton = (ToolButton) findViewById(R.id.tool1_button);
		mView.setDrawingTool(mCurrentToolButton.getConfig());
		
		currentFileName = "/sdcard/essai.jpg";

		if (savedInstanceState != null){ //restored bitmap if we just resurrected.
			Bitmap bitmap = savedInstanceState.getParcelable("canvas");
			if (bitmap != null) {
				mView.setBitmap(bitmap);
			}
			String fileName = savedInstanceState.getString("name");
			if (fileName != null) {
				this.currentFileName = fileName;
			}
			ToolButton b1 = (ToolButton) findViewById(R.id.tool1_button);
			ToolButton b2 = (ToolButton) findViewById(R.id.tool2_button);
			ToolButton b3 = (ToolButton) findViewById(R.id.tool3_button);
			ToolButton b4 = (ToolButton) findViewById(R.id.tool4_button);
			AndroidDrawingToolConfig config;
			config = savedInstanceState.getParcelable("tool1");
			if (config != null){
				b1.setConfig(config );
			}
			config = savedInstanceState.getParcelable("tool2");
			if (config != null){
				b2.setConfig(config);
			}
			config = savedInstanceState.getParcelable("tool3");
			if (config != null){
				b3.setConfig(config);
			}
			config = savedInstanceState.getParcelable("tool4");
			if (config != null){
				b4.setConfig(config);
			}

		}

		associateLongClickListener(R.id.save_button, new OnLongClickListener (){

			@Override
			public boolean onLongClick(View v) {
				doSaveAs();
				return true;
			}});



		associateLongClickListener(R.id.stamp_button, new OnLongClickListener (){

			@Override
			public boolean onLongClick(View v) {
				doStampingToolEdit();
				return true;
			}});

		associateLongClickListener(R.id.tool1_button, new OnLongClickListener (){

			@Override
			public boolean onLongClick(View v) {
				doEditTool((ToolButton) v);
				return true;
			}});

		associateLongClickListener(R.id.tool2_button, new OnLongClickListener (){

			@Override
			public boolean onLongClick(View v) {
				doEditTool((ToolButton) v);
				return true;
			}});
		associateLongClickListener(R.id.tool3_button, new OnLongClickListener (){

			@Override
			public boolean onLongClick(View v) {
				doEditTool((ToolButton) v);
				return true;
			}});

		associateLongClickListener(R.id.tool4_button, new OnLongClickListener (){

			@Override
			public boolean onLongClick(View v) {
				doEditTool((ToolButton) v);
				return true;
			}});


	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{	
		Log.i("Main", "onSaveInstanceState");
		if (mView != null) {
			savedInstanceState.putParcelable("canvas", mView.getBitmap());
		}
		ToolButton b1 = (ToolButton) findViewById(R.id.tool1_button);
		ToolButton b2 = (ToolButton) findViewById(R.id.tool2_button);
		ToolButton b3 = (ToolButton) findViewById(R.id.tool3_button);
		ToolButton b4 = (ToolButton) findViewById(R.id.tool4_button);
		savedInstanceState.putParcelable("tool1", b1.mTool.getConfig());
		savedInstanceState.putParcelable("tool2", b2.mTool.getConfig());
		savedInstanceState.putParcelable("tool3", b3.mTool.getConfig());
		savedInstanceState.putParcelable("tool4", b4.mTool.getConfig());
		
	}
	
	public static int SELECT_PICTURE=1;
	public static int REQUEST_SAVE=2;
	
	public boolean loadImage(View v){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
		return true;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == RESULT_OK){
			if (requestCode == SELECT_PICTURE){
				Uri selectedImageUri = data.getData();
				String path = getPath(selectedImageUri);
				loadFile(path);
				Toast.makeText(getBaseContext(), "File selected" + path, 2).show();
				Log.i("select picture" , path);
			}
			else if (requestCode == REQUEST_SAVE) {
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
				this.currentFileName = filePath;
				Toast.makeText(getBaseContext(), "File to save" + filePath, 2);
				doSave(null);
			}
		}
	}
	/**
	 * load image file in the drawing view.
	 * @param path
	 */
	public void loadFile(String path){
		try{
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			mView.setBitmap(bitmap.copy(bitmap.getConfig(), true));
		}
		catch (Exception e){
			Log.e("main","failed to load file", e);
		}
	}
	/**
	 * obtain the path of an image managed by the MediaStore
	 * @param uri
	 * @return path of image
	 */
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}


	private static final int SAVE_MENU_ID = Menu.FIRST + 1;
	private static final int ERASE_MENU_ID = Menu.FIRST + 4;
	private static final int LOAD_MENU_ID = Menu.FIRST + 5;
	private static final int CREATE_CANVAS_ID = Menu.FIRST + 6;
	private static final int DRAW_EDIT_DIALOG_ID = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SAVE_MENU_ID, 0, "Save").setShortcut('3', 's');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		menu.add(0, LOAD_MENU_ID, 0 , "Load").setShortcut('5', 'z');
		menu.add(0, CREATE_CANVAS_ID, 0 , "New").setShortcut('5', 'z');

		/****
		 * Is this the mechanism to extend with filter effects? Intent intent =
		 * new Intent(null, getIntent().getData());
		 * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		 * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new ComponentName(this,
		 * NotesList.class), null, intent, 0, null);
		 *****/
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
 
    public void toolChanged(DrawingToolConfig config){
		Toast.makeText(getBaseContext(), "Tool changed" + config.size + " " + config.tool, 2).show();
		mCurrentToolButton.toolChanged(config);
		mView.setDrawingTool(config);
    }

    public void doEditTool(){
    	DrawEditDialog dialog = new DrawEditDialog(this, this, mCurrentToolButton.getConfig());
    	dialog.show();
    }

    public void doEditTool(ToolButton b){
    	mCurrentToolButton = b;
    	doEditTool();
    }

    public void doChangeTool(View v){
    	mCurrentToolButton = (ToolButton) v;
    	mView.setDrawingTool(mCurrentToolButton.getConfig());
    }
    /*
     * show dialog to define a new canvas and replace the current canvas with a blank canvas corresponding to the parameters.
     */
    public boolean doCreateCanvas(View v){
    	NewEditDialog dialog = new NewEditDialog(this,
    			new NewEditDialog.OnCreateCanvasListener() {

    		@Override
    		public void createCanvas(int width, int height, int fillType) {
    			Log.i("main", "createCanvas " + width + " " + height + " " + fillType);
    			mView.blankCanvas(width, height, fillType);
    		}

    	});
    	dialog.show();
    	return true;
    }
    
    public void doZoomInTwo(View v){
    	mView.zoomIn(2);
    }
    public void doZoomOutTwo(View v){
    	mView.zoomOut(2);
    }
    public void setPanMode(View v){
    	mView.setPanMode();
    }
    public void setStampMode(View v){
    	mView.setStampMode();
    }
	public void undo(View v){
		mView.undo();
	}

    
	protected void doStampingToolEdit() {
		StampEditDialog dialog = new StampEditDialog(this, this);
		dialog.show();
	}

	public void stampChanged(int imageMediaId, int ratioMode){
		Toast.makeText(getBaseContext(), "Stamp changed not implement" + imageMediaId + " " + ratioMode, 2).show();
		mView.setStampMode(imageMediaId, ratioMode);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

//		mView.getTool().resetPaint();

		switch (item.getItemId()) {

			
		case SAVE_MENU_ID:
			return doSaveAs();
			
			
		}
		return super.onOptionsItemSelected(item);
	}



	public boolean doSaveAs(){
		Intent intent = new Intent(this, FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, "/sdcard");
		startActivityForResult(intent, REQUEST_SAVE);
		return true;
	}
	
	
	public boolean doSave(View v) {
		FileOutputStream ostream = null;
		try {
			ostream = new FileOutputStream(currentFileName, false);
			BufferedOutputStream bos = new BufferedOutputStream(ostream);
			mView.getBitmap().compress(CompressFormat.PNG, 0, bos);
			bos.close();
			Toast.makeText(getBaseContext(), "File saved as " + currentFileName, 2).show();
			ContentValues newImage = new ContentValues(3);

			newImage.put(Media.TITLE, "drawing1");
			newImage.put(Media.MIME_TYPE, "image/png");
			newImage.put(MediaStore.Images.Media.DATA, currentFileName);

			Uri mPictureUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, newImage);

			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mPictureUri));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(getBaseContext(), "Error saving file " + currentFileName, 3).show();
			e.printStackTrace();
		}
		// new ColorPickerDialog(this, this, mPaint.getColor()).show();
		return true;
	}
	

}
