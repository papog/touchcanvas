package com.spacepirates.android.touchcanvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;

public class DrawingModel {
	private Bitmap mBitmap;  // stores the reference drawing created by the user.
	private Canvas mCanvas;
	private Rect mBounds;
	private Bitmap mScratchBitmap;
	private Canvas mScratchCanvas;
	private Paint mBitmapPaint;
	private UndoManager mUndoManager;
	private ContourLayer mContourLayer;
	
	public DrawingModel(){
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

		mUndoManager = new UndoManager(this);
	}
	public DrawingModel(int width, int height, int fillMode) {
		this();
		mContourLayer = new ContourLayer(width, height);
	}
	
	/**
	 * 
	 * @return a Rect that contains the whole drawing area.
	 */
	public Rect getBoundingBox(){
		return new Rect(0,
				0,
				mBitmap.getWidth(),
				mBitmap.getHeight());
	}
	/**
	 * Set the Rect that which part of the model is updated and needs displaying
	 * @param bounds
	 */
	public void setBounds(Rect bounds) {
		if (bounds != null){
			mBounds = new Rect(bounds);
			mBounds.intersect(getBoundingBox());
		}
		else {
			mBounds = null;
		}}

	/**
	 * get the Rect that indicates with part of the model is updated and needs displaying
	 * @param bounds
	 */
	public Rect getBounds() {
		return mBounds;
		
	}

	public void createBlank(int w, int h){
		createBlank(w, h, 0x00000000 );
	}
	
	/**
	 * create a new blank painting of required width, height and filled with the provided color
	 * @param w width
	 * @param h height
	 * @param fillMode color
	 */
	public void createBlank(int w, int h, int fillMode) {
		// TODO Auto-generated method stub
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(fillMode);
		mScratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mScratchCanvas = new Canvas(mScratchBitmap);

	}

	public void renderTentativeUpdates(AbstractDrawingTool tool, Rect clipRect){

		mScratchCanvas.clipRect(clipRect, Region.Op.REPLACE);
		mScratchCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		tool.renderTentative(mScratchCanvas);
	}
	
	public void copyRendered(Canvas canvas, Rect srcRect, Rect dstRect){
		canvas.drawBitmap(mScratchBitmap, srcRect , dstRect, mBitmapPaint);
	}
	
	public void copyReference(Canvas canvas, Rect srcRect, Rect dstRect) {
		canvas.drawBitmap(mBitmap, srcRect , dstRect, mBitmapPaint);		
	}

	/**
	 * ask undo manager to restore canvas as it was before last change.
	 */
	public void undoLastChange() {
		mUndoManager.undo();
	}
	
	/**
	 * ask undo manager to remember the current state of the canvas before a new change.
	 */
	public void recordBeforeChange() {
		mUndoManager.record();
	}
	
	public void commitChanges(AbstractDrawingTool tool) {
		recordBeforeChange();
		renderTentativeUpdates(tool, mBounds);
		copyRendered(mCanvas, mBounds, mBounds);
	}
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
	protected Canvas getCanvas() {
		return mCanvas;
	}
	
	/**
	 * get the contour layer object that contains contours and is displayed on top of the picture.
	 * @return
	 */
	protected ContourLayer getContour() {
		return mContourLayer;
	}
	
	
	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
		mCanvas = new Canvas(mBitmap);
		mScratchBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		mScratchCanvas = new Canvas(mScratchBitmap);
	}
}
