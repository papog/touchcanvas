package com.spacepirates.android.touchcanvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
/**
 * manage changes to a DrawingModel. record should be called before every change.
 * To undo the last change, just call undo.
 * @author pgaillard
 *
 */
public class UndoManager {
	
	
	private static class Event {
		Bitmap mBitmap;
		Rect mBounds;
	}

	private static final int BYTE_PER_PIXEL = 4;
	
	DrawingModel mModel;
	Paint mPaint;
	Stack<Event> mHistory;
	
	public UndoManager(DrawingModel model){
		mModel = model;
		mPaint = new Paint();
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
		mHistory = new Stack<Event>();
	}
	
	public void record(){
		try {
			Event event = new Event();
			event.mBounds = mModel.getBounds();

			if (event.mBounds != null){
				makeRoom(event.mBounds);
				event.mBitmap = Bitmap.createBitmap(event.mBounds.width(),
						event.mBounds.height(),
						Bitmap.Config.ARGB_8888);
				Canvas tmpCanvas = new Canvas(event.mBitmap);

				mModel.copyReference(tmpCanvas, event.mBounds, tmpCanvas.getClipBounds());
				mHistory.push(event);
			}
		}
		catch (Throwable t) {
			Log.e("undo", "can't record new undo.", t);
		}
	}
	/**
	 * monitor memory usage, check if we have space to allocate the new bitmap defined
	 * by mBounds. If we need more space, remove the oldest history element.
	 * @param mBounds
	 */
	private void makeRoom(Rect mBounds) {
	
		while (mHistory.size() > 0 && !MemoryGuard.canFit(mBounds.width()*mBounds.height() * BYTE_PER_PIXEL)){
			mHistory.removeElementAt(0);
			Log.i("undo", "removing old undo");
		}
	}

	public void undo(){
		if (isUndoPossible()){
		Event event = mHistory.pop();
		mModel.getCanvas().drawBitmap(event.mBitmap, event.mBounds.left, event.mBounds.top, mPaint);
		mModel.setBounds(event.mBounds);
		}
		else {
			Log.e("Undo", "requested undo is impossible as there are no saved bounds");
		}
	}
	
	public boolean isUndoPossible(){
		return mHistory.size()>= 1;
	}
}
