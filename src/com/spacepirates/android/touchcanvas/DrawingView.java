package com.spacepirates.android.touchcanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * The main drawing view.
 * 	
 */
public class DrawingView extends View {

	private DrawingModel mModel;
	private AbstractDrawingTool mTool;
	private DurationLogger dLogger;
	private CoordinateMapper mCoordinateMapper;
	private Rect mWindowRect;
	private ScrollGestureDetector mScrollDetector;
	private boolean gestureInProgress = false;
	private TouchLogger mTouchLogger;
	private DrawingTool mDrawingTool;
	private StampingTool mStampingTool;
	
	// INITIALIZATION SECTION
	
	
	private void init(Context c) {
		mModel = new DrawingModel();
		
		dLogger = new DurationLogger("draw.duration");
	    // Create our ScaleGestureDetector
		mTouchLogger = new TouchLogger();
	    //mScaleDetector = new ScaleGestureDetector(c, new ScaleListener());
	    mScrollDetector = new ScrollGestureDetector(new ScrollListener(), new ScaleListener());

	}
	
	public void setStampMode() {
		if (mStampingTool == null){
			mStampingTool = new StampingTool(mModel, mCoordinateMapper, getContext().getContentResolver());
		}
		mTool = mStampingTool;
		Log.i("drawing", "switched to stamping tool");
	}

	public DrawingView(Context c) {
		super(c);
		
		init(c);
	}

	public DrawingView(Context c, AttributeSet as) {
		super(c, as);
	
		init(c);
	}

	void blankCanvas(int width, int height, int fillMode){
		mModel.createBlank(width, height, fillMode);
		updateCoordinateMapper();
		invalidate();
	}
	// MULTI-TOUCH LISTENERS
	
	
	/**
	 *  ScaleListener receives scaling events from multi touch zooming and zooms the
	 *  drawing.
	 *  Any current stroke is canceled (i.e. not drawn).
	 */
	private class ScaleListener implements ScrollGestureDetector.ScaleGestureListener {
	    @Override
	    public boolean onScale(ScrollGestureDetector detector) {
	        mTool.cancel();
	        zoomOut(1/detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
	        Log.i("scale","scale event detected:" + detector.getScaleFactor());
	        invalidate();
	        return true;
	    }
	    
	    @Override
	    public boolean onScaleBegin(ScrollGestureDetector detector){
	        Log.i("scale","scale begin detected:");
	    	mTool.cancel();
	    	gestureInProgress = true;
	    	return true;
	    }
	    
	    @Override
	    public void onScaleEnd(ScrollGestureDetector detector){
	        Log.i("scale","scale end detected:");
	    	gestureInProgress = false;
	    }
	}
	private class ScrollListener implements ScrollGestureDetector.ScrollGestureListener {
	 
	    public boolean onScroll(ScrollGestureDetector detector) {
	        mTool.cancel();
	        float dx = detector.getMotionX();
	        float dy = detector.getMotionY();
	        Log.i("scroll","scroll event detected dx:" + dx + "dy:" + dy);
	        mCoordinateMapper.moveByWindowCoordinates(-dx, -dy); // move in reverse TODO adjust for scale as the dx,dy is in screen coordinates 
	        invalidate();
	        return true;
	    }
	    
	   
	    public void onScrollEnd(ScrollGestureDetector detector){
	        Log.i("scroll","scroll end detected:");
	    	gestureInProgress = false;
	    }


		@Override
		public boolean onScrollBegin(ScrollGestureDetector detector) {
	        Log.i("scroll","scroll begin detected:");
	    	mTool.cancel();
	    	gestureInProgress = true;
	    	return true;
		}
	}
	
	
	/**
	 * Main method that handles low level touch events and calls the specific methods
	 * touch_up, touch_down, touch_move.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float referenceCoords [] = mCoordinateMapper.toReferenceRounded(x,y);
		mTouchLogger.onTouchEvent(event);
		//mScaleDetector.onTouchEvent(event);
		mScrollDetector.onTouchEvent(event);
		if (!gestureInProgress){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTool.touch_start(referenceCoords[0], referenceCoords[1]);
	
				break;
			case MotionEvent.ACTION_MOVE:
				mTool.touch_move(referenceCoords[0], referenceCoords[1]);
	
				break;
			case MotionEvent.ACTION_UP:
				mTool.touch_up();
	
				break;
	
			default:
				Log.w("View", "unrecognized action" + event.getAction());
			}
			if (mModel.getBounds() != null) {
				invalidate(mCoordinateMapper.fromReference(mModel.getBounds()));
				//mBounds = null; // invalidate operation is destructive, so the pointer should not be kept
	
			}
			else {
				invalidate();
			}
		}
		return true;
	}

	// DRAWING COMMANDS
	
	public Bitmap getBitmap() {
	
		return mModel.getBitmap();
	}

	/**
	 * change the drawing to the given . Update coordinate mapping accordingly.
	 * @param bitmap
	 */
	public void setBitmap(Bitmap bitmap) {
		mModel.setBitmap(bitmap);
		updateCoordinateMapper();
	}
	


	public void setPanMode() {
		mTool = new PanningTool(mModel, mCoordinateMapper);
	}
	public void colorChanged(int color) {
		setDrawingTool(); // color changed means we want to draw TODO this needs to be refined with more tools.
		setColor(color);
	}

	private void setDrawingTool() {
		if (mDrawingTool == null) {
			mDrawingTool = new DrawingTool(mModel, mCoordinateMapper);	
		}
		mTool = mDrawingTool;
	}

	public void zoomOut(float d) {
		mCoordinateMapper.zoomOut(d);
		mModel.setBounds(null); // we update everything
		invalidate();
	}
	public void zoomOut(float d, float focusX, float focusY) {
		mCoordinateMapper.zoomOut(d, focusX, focusY);
		mModel.setBounds(null); // we update everything
		invalidate();
	}

	public void zoomIn(float d) {
		mCoordinateMapper.zoomIn(d);
		mModel.setBounds(null); // we update everything
		invalidate();
		
	}

	public void setColor(int color) {
		mTool.setColor(color);
	}

	public int getColor() {
		return mTool.getColor();
	}

	
	protected void updateCoordinateMapper(){
		float w = mModel.getBitmap().getWidth();
		float h = mModel.getBitmap().getHeight();
		float screenWidth = getWidth();
		float screenHeight = getHeight();
		float wRatio = screenWidth / w;
		float hRatio = screenHeight / h;
		if (wRatio > hRatio) {
			mCoordinateMapper = new CoordinateMapper(0, 
					0,
					w,
				    screenHeight / wRatio ,
					getWidth(),
					getHeight());			
	
			
		}
		else {
			mCoordinateMapper = new CoordinateMapper(0, 
					0,
					screenWidth / hRatio,
				    h ,
					getWidth(),
					getHeight());			
			
		}
	
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mModel.getBitmap() == null){
			mModel.createBlank(w, h);
		}
		mDrawingTool = new DrawingTool(mModel, mCoordinateMapper);
		mTool = mDrawingTool;
		mStampingTool = null;
		updateCoordinateMapper();
		mWindowRect = new Rect(0,0, getWidth(), getHeight());
	}

	/**
	 * update the canvas with latest changes to model drawing. Zoom is managed. Changes performed by tool are managed.
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		dLogger.start();
		Rect bounds = mModel.getBounds();
		Rect dummyBounds = new Rect();

		if (bounds != null && canvas.getClipBounds(dummyBounds))
			{

			// Draw current path in a scratch buffer
			Rect tmp = (Rect) new Rect(mWindowRect); // TODO restore code to manage part of change only.
			//mCoordinateMapper.fromReference(bounds); // find out what part of the canvas is impacted.
			tmp.intersect(mWindowRect); // We don't update outside of the screen
			Rect srcRect = mCoordinateMapper.toReference(tmp);
			Log.i("onDraw", "refreshing partial area " + tmp + " from " + srcRect + " clip bounds were " + dummyBounds);
			mModel.renderTentativeUpdates(mTool, srcRect);
			// update the impacted part from the corresponding input part.
			mModel.copyRendered(canvas, srcRect , tmp);

		}
		else {
			Rect srcRect = mCoordinateMapper.toReference(mWindowRect);
			Log.i("onDraw","refreshing whole area" + mWindowRect + " from " + srcRect );
			mModel.copyReference(canvas, srcRect , mWindowRect);
		}
		//canvas.drawPath(mPath, mPaint); //TODO all drawing should be performed in reference bitmap
		dLogger.end();

	}





	public AbstractDrawingTool getTool() {

		return mTool;
	}

	public void setDrawingTool(DrawingToolConfig config) {
		setDrawingTool();
		((DrawingTool) mTool).configure(config);

	}

	public void setStampMode(int imageMediaId, int ratioMode) {
		setStampMode();
		mStampingTool.change(imageMediaId, ratioMode);
		
	}

	public void undo() {
		mModel.undoLastChange();
		invalidate();
	}

}
