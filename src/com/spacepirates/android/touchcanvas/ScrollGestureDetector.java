package com.spacepirates.android.touchcanvas;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

/**
 *  handle panning/scrolling with 2 fingers. Distinguish it from zooming
 * @author Pierre-Olivier Gaillard
 *
 */
public class ScrollGestureDetector {

	private float referenceDistance;
	private PointF referenceMiddle;
	private float dx;
	private float dy;
	private float thresholdZoom = 0.2F;
	private final int IDLE = 0;
	private final int TWO = 1;
	private final int PANNING = 2;
	private final int ZOOMING = 3;

	private float absoluteZoom = 1.0F;
	private float previousZoom = 1.0F;
	
	private int state = IDLE;
	private ScrollGestureListener scrollListener;
	private ScaleGestureListener scaleListener;
	

	public ScrollGestureDetector(ScrollGestureListener scrollListener, ScaleGestureListener scaleListener){
		this.scrollListener = scrollListener;
		this.scaleListener = scaleListener; 
	}
	
	public static interface ScrollGestureListener {

		public boolean onScroll(ScrollGestureDetector detector);


		public boolean onScrollBegin(ScrollGestureDetector detector);


		public void onScrollEnd(ScrollGestureDetector detector);

	}

	public static interface ScaleGestureListener {

		public boolean onScale(ScrollGestureDetector detector);


		public boolean onScaleBegin(ScrollGestureDetector detector);


		public void onScaleEnd(ScrollGestureDetector detector);

	}

	private void setState(int newState){
		Log.i("scroll", "changing state to " + newState);
		state = newState;
	}
	
	public boolean onTouchEvent(MotionEvent event){
		final int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_MOVE: {
			if (event.getPointerCount() == 2 ) {
				float distance = distanceBetweenPointers(event);
				previousZoom = absoluteZoom;
				absoluteZoom = distance / referenceDistance;
				PointF middle = middleOfPointers(event);
				Log.i("scroll", "Action move, 2 pointers, state:" + state + " zoom:" + absoluteZoom + " abs - 1: " + (absoluteZoom -1.0));
				float motionNorm = distance(middle, referenceMiddle);
				if (Math.abs(absoluteZoom - 1.0) < thresholdZoom ) {
					if (state == TWO){
						scrollListener.onScrollBegin(this);
						setState( PANNING);
					}
					if (state == PANNING)
					{
						dx = middle.x - referenceMiddle.x;
						dy = middle.y - referenceMiddle.y;
						scrollListener.onScroll(this);
					}
				}
				else {
					if (state == PANNING) {
						scrollListener.onScrollEnd(this);
					}
					if (state != ZOOMING) {
						scaleListener.onScaleBegin(this);
						
						setState(ZOOMING); 
					}
					if (state == ZOOMING){
						scaleListener.onScale(this);
					}
				} 
				referenceMiddle = middle;
			}
			else {
				if (state == PANNING) {
					scrollListener.onScrollEnd(this);
				}
				if (state == ZOOMING) {
					scaleListener.onScaleEnd(this);
				}
				
			}
			break;
		}

		case MotionEvent.ACTION_UP: {
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			absoluteZoom = 1.0F;
			if (event.getPointerCount() == 3 ) { // the pointer going up is counted. We are going to have 2 pointers down from 3
				referenceDistance = distanceBetweenPointers(event);
				referenceMiddle = middleOfPointers(event);
				setState(TWO);
			}
			else {
				if (state == PANNING){
					scrollListener.onScrollEnd(this);
				}
				if (state == ZOOMING){
					scaleListener.onScaleEnd(this);
					setState(IDLE);
				}
			}

			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			absoluteZoom = 1.0F;
			if (event.getPointerCount() == 2 ) {
				referenceDistance = distanceBetweenPointers(event);
				referenceMiddle = middleOfPointers(event);
				Log.i("scroll", "2 pointers distance" + referenceDistance + " middle " + referenceMiddle);
				setState(TWO);
			}
			else {
				if (state == PANNING){
					scrollListener.onScrollEnd(this);
				}
				if (state == ZOOMING){
					scaleListener.onScaleEnd(this);
				}
				setState(IDLE);
				
			}

			
			break;
		}
		}
		return true;

	}
	
	/**
	 * compute the middle of the first 2 pointers in event.
	 * @param event
	 * @return
	 */
	private PointF middleOfPointers(MotionEvent event) {
		float sx = event.getX(0) + event.getX(1);
		float sy = event.getY(0) + event.getY(1);
		return new PointF(sx / 2, sy / 2);

	}

	/**
	 * computes the distance between the first 2 pointers in event
	 * @param event
	 * @return
	 */
	private float distanceBetweenPointers(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);

		return FloatMath.sqrt(x * x + y * y);

	}
	
	private float distance(PointF a, PointF b){
		float x = a.x - b.x;
		float y = a.y - b.y;
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public float getMotionX(){
		return dx;
	}
	public float getMotionY(){
		return dy;
	}
	
	public float getFocusX(){
		return referenceMiddle.x;
	}
	public float getFocusY(){
		return referenceMiddle.y;
	}

	public float getScaleFactor() {
		return absoluteZoom/previousZoom;
	}
	
}

