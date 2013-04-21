package com.spacepirates.android.touchcanvas;

import android.util.Log;
import android.view.MotionEvent;

public class TouchLogger {

	public static String getActionName(int value)
	{

		switch (value){
		case MotionEvent.ACTION_CANCEL: 
			return "CANCEL";
		case MotionEvent.ACTION_DOWN:
			return "DOWN";
		case MotionEvent.ACTION_MOVE:
			return "MOVE";
		case MotionEvent.ACTION_OUTSIDE:
			return "OUTSIDE";
		case MotionEvent.ACTION_POINTER_DOWN:
			return "POINTER_DOWN";
		case MotionEvent.ACTION_POINTER_ID_MASK:
			return "POINTER_ID_MASK";
		case MotionEvent.ACTION_POINTER_UP:
			return "POINTER_UP";
		case MotionEvent.ACTION_UP:
			return "UP";
		default:
			return Integer.toString(value);

		}
	}
	
	private DurationLogger mDurationLogger;
	
	public TouchLogger(){
		mDurationLogger  = new DurationLogger("TouchLogger");
	}
	/**
	 * Log information contained in event:
	 * 
	 * @param event
	 */
	public void onTouchEvent(MotionEvent event) {
		// history
		// pointers
		mDurationLogger.start();
		event.getPointerCount();
		event.getAction();
		if (event.getHistorySize() > 0){
			Log.i("TouchLogger",
					" x:" + event.getX() +
					"Touch duration:"
					+ (event.getEventTime() - event
							.getHistoricalEventTime(0)) + " size:"
							+ event.getHistorySize()
							+ "action:"
							+ event.getAction());
			for (int h = 0 ; h < event.getHistorySize() ; h ++){

				for (int i = 0 ; i < event.getPointerCount() ; i++){

					Log.i("TouchLogger",
							" h " 
							+ h
							+ " "
							+ i
							+ " "
							+ event.getHistoricalEventTime(h)
							+ " "
							+ event.getHistoricalX(i,h)
							+ " "
							+ event.getHistoricalY(i,h)
							+ " "
							+ event.getHistoricalPressure(i,h)
							+ " "
							+ event.getHistoricalSize(i,h));
				}
			}
		}

		for (int i = 0 ; i < event.getPointerCount() ; i++){
			Log.i("TouchLogger",
					" current " 
					+ getActionName(event.getAction() &  MotionEvent.ACTION_MASK)
					+ " "
					+ (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK)
					+ " "
					+ event.getEventTime()
					+ " "
					+ i
					+ " "
					+ event.getX(i)
					+ " "
					+ event.getY(i)
					+ " "
					+ event.getPressure(i)
					+ " "
					+ event.getSize(i));
		}

		mDurationLogger.end();
	}
	

}
