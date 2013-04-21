package com.spacepirates.android.touchcanvas;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;

/**
 * StampingTool class handles drawing a bitmap, scaled to the area designated by the user.
 *
 */
public class StampingTool extends AbstractDrawingTool {

	public static final int LOCK_ASPECT_RATIO_ID = 0;
	public static final int FREESCALE_ID = 1;

	private int mImageMediaId;
	private int mRatioMode;
	
	private DrawingModel mModel;
	private Bitmap mBitmap;
	private Rect mSourceRect;
	private Paint mPaint;
	private Rect mTargetRect;
	private Rect mBounds;
	private ContentResolver mResolver;
	
	public StampingTool(DrawingModel model, CoordinateMapper mapper, ContentResolver contentResolver){
		mModel = model;
		try{
		    mBitmap = BitmapFactory.decodeFile("/sdcard/encore.png");
		}
		catch (Throwable e){
			Log.e("main","failed to load file", e);
		}
		finally {
			if (mBitmap == null) {
				Log.e("main","failed to load file");
				mBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
			}
		}
		mPaint = new Paint();
		mResolver = contentResolver;		
		mSourceRect = new Rect(0,0, mBitmap.getWidth(), mBitmap.getHeight());
		mBounds = null;
	}
	
	
	/**
	 * update the bounds on the model being drawn on.
	 */
	private void updateBounds() {
		if (mBounds != null){
			mBounds.union(mTargetRect);
		}
		else {
			mBounds = new Rect(mTargetRect);
		}
		mModel.setBounds(mBounds);
	}


	@Override
	public void renderTentative(Canvas canvas){
		if (mTargetRect != null) {
			canvas.drawBitmap(mBitmap, mSourceRect, mTargetRect, mPaint) ;
		}
	}

	@Override
	public void touch_start(float x, float y) {
		// TODO keep a backup of the reference bitmap in case the user is in fact performing a gesture and we need to cancel
		Log.i("touch_start", "at x:" + x + "y:" + y);
		mTargetRect = new Rect((int) x, (int) y, (int) x+1, (int) y + 1);
		updateBounds();
	}

	@Override
	public void touch_move(float x, float y) {
		mTargetRect.right = (int) x;
		mTargetRect.bottom = (int) y;
		updateBounds();
	}

	@Override
	public void touch_up() {
		mModel.recordBeforeChange();
		renderTentative(mModel.getCanvas());
			// make sure the update zone will contain the newly drawn path.
		updateBounds();	
			// kill this so we don't double draw
		mTargetRect = null;
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}


	public void change(int imageMediaId, int ratioMode) {
		mRatioMode = ratioMode;
		mImageMediaId = imageMediaId;
		String path = MediaFunctions.getPath(mResolver, imageMediaId);
		Log.i("StampingTool", "loading bitmap" + path);
	    mBitmap = BitmapFactory.decodeFile(path);
		mSourceRect = new Rect(0,0, mBitmap.getWidth(), mBitmap.getHeight());
	    
	}


}
