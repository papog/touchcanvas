package com.spacepirates.android.touchcanvas;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcelable;
import android.util.Log;
/**
 * DrawingTool offers operations that can be performed on a DrawingModel to update
 * it and keep track of changes.
 * 
 * @author POG
 *
 */
public class DrawingTool extends AbstractDrawingTool {

	private int size;
	private Paint mPaint;
	private int mTool;
	private MaskFilter mEmboss;
	private MaskFilter mBlur;
	private Path mPath;
	private float mX, mY; // store last drawing coordinates in reference space
	private DrawingModel mModel;
	private static final float TOUCH_TOLERANCE = 4;
	private static final int FILTER_WIDTH = 8;
	private static final int SPILL_MARGIN = 3;
	private boolean mDrawing;
	private DrawingToolConfig mConfig;
	
	public void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPath = new Path();
		mTool = DrawingToolConfig.REGULAR_ID;
		setSize(12);
		mDrawing = false;

	}

	
	public DrawingTool(DrawingModel model, CoordinateMapper mapper){
		mModel = model;
		init();
	}
	
	/**
	 * update the bounds on the model being drawn on.
	 */
	private void updateBounds() {
		Rect mBounds;
		RectF boundsF = new RectF();
		if (! mPath.isEmpty()) {
			int effectiveWidth = Math.max((int) mPaint.getStrokeWidth(), FILTER_WIDTH) + SPILL_MARGIN;
			mPath.computeBounds(boundsF, false);
			mBounds = new Rect((int) boundsF.left, (int) boundsF.top,
					(int) boundsF.right, (int) boundsF.bottom);
			mBounds.inset(- effectiveWidth,
					-(int) effectiveWidth);
			if (mBounds.top == 0){
				Log.w("updateBounds", "top is 0");
			}
			Log.i("updateBounds", "new bounds:" + mBounds);
		}
		else {
			mBounds = null;  // Nothing to draw, no bounds
		}
		mModel.setBounds(mBounds);
	}

	public void setSize(int size){
		this.size = size;
		mPaint.setStrokeWidth(size);
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, size/2,
				3.5f);

		mBlur = new BlurMaskFilter(FILTER_WIDTH, BlurMaskFilter.Blur.NORMAL);

	}
	
	public int getSize(){
		return size;
	}
	
	/**
	 * set the color to paint with. Note that the alpha component is taken into account.
	 */
	public void setColor(int color) {
		mPaint.setColor(color);
	}
	
	public void setSolidColor(int color) {
		mPaint.setColor(0xFF000000 | color);
	}

	public int getColor() {

		return mPaint.getColor();
	}
	
	public void renderTentative(Canvas canvas){
		canvas.drawPath(mPath, mPaint);
	}


	public void renderSample(Canvas canvas){
		Path samplePath = new Path();
		Rect bounds = canvas.getClipBounds();
		samplePath.reset();
		//TODO FIXME a partial expose event can make us draw in wrong part of button.
		samplePath.moveTo(bounds.exactCenterX() , bounds.exactCenterY() );	
		samplePath.lineTo(bounds.exactCenterY() + 0.001F, bounds.exactCenterY() + 0.001F); // adding tiny delta to make
		canvas.drawPath(samplePath, mPaint);
	}
	public void touch_start(float x, float y) {
		// TODO keep a backup of the reference bitmap in case the user is in fact performing a gesture and we need to cancel
		Log.i("touch_start", "at x:" + x + "y:" + y);
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		updateBounds();
		mDrawing = true;
	}

	public void touch_move(float x, float y) {

		if (mDrawing){
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);

			Log.i("touch_move", "at x:" + x + " y:" + y);

			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) { // need to store screen coordinates too?
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
			updateBounds();
		}
		else {
			Log.i("touch_move", "ignoring event as drawing flag is false");
		}
	}

	public void touch_up() {
		Log.i("touch_up", "Last point at x:" + mX + "y:" + mY);

		if (mDrawing){
			mPath.lineTo(mX + 0.001F, mY + 0.001F); // adding tiny delta to make
			// sure that a single point can
			// be drawn lineT
			// (simple tap on the screen)

			// first make sure the update zone will contain the newly drawn path.
			updateBounds();

			// commit the path to our off screen
			mModel.commitChanges(this);

			// kill this path so we don't double draw
			mPath.reset();
		}
		else {
			Log.i("touch_up", "ignoring touch_up as mDrawing is false");
		}
		
	}
	public void resetPaint() {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);
		mPaint.setMaskFilter(null);
	}

	public void setSrcATopMode() {
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		mPaint.setAlpha(0x80);
		mPaint.setMaskFilter(null);
	}
	public void setBlurFilter() {
		if (mPaint.getMaskFilter() != mBlur) {
			mPaint.setMaskFilter(mBlur);
		} else {
			mPaint.setMaskFilter(null);
		}
	}
	public void setEmbossFilter() {
		if (mPaint.getMaskFilter() != mEmboss) {
			mPaint.setMaskFilter(mEmboss);
		} else {
			mPaint.setMaskFilter(null);
		}
	}
	public void setEraseMode() {
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

	/**
	 * configure tool (tool type, color, size) according to config.
	 * @param config
	 */
	public void configure(DrawingToolConfig config){
		mConfig = config;
		setTool(config.tool);
		setSolidColor(config.color);
		setSize(config.size);
	}
	
	/**
	 * change the tool according to tool interpreted according to DrawingToolConfig values (EMBOSS_ID, REGULAR_ID etc.)
	 * @param tool
	 */
	public void setTool(int tool) {
		resetPaint();
		mTool = tool;
		switch(tool){

		case DrawingToolConfig.EMBOSS_ID:
			setEmbossFilter();
			break;
		case DrawingToolConfig.REGULAR_ID:
			resetPaint();
			break;
		case DrawingToolConfig.BLUR_ID:
			setBlurFilter();
			break;
		case DrawingToolConfig.CLEAR_ID:
			resetPaint();
			setEraseMode();
			break;
			
		}
	}

	public void cancel(){
		Log.i("drawing tool","cancel current path");
		mPath.reset();
		mDrawing = false;
	}


	public AndroidDrawingToolConfig getConfig() {
		return new AndroidDrawingToolConfig(new DrawingToolConfig(mTool, size, mPaint.getColor())); 
	}
	
}
