package com.spacepirates.android.touchcanvas;

import java.util.Stack;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * A ContourLayer object manages annotations to an image that are essentially contours
 * It is meant to be used to annotate a DrawingModel.
 * The typical activity is:
 * * add a potential contour (rectangle, circle, freehand)
 * * cancel it or validate it
 * * provide a picture showing the current annotations
 * 
 * @author pgaillard
 *
 */
public class ContourLayer {

	int mHeight;
	int mWidth;
	private Paint mPaint;
	private Picture mCurrent;
	private Path mPath;
	private Stack<Path> mPathList;
	
	public ContourLayer(int height, int width){
		mHeight = height;
		mWidth = width;
		mPaint = new Paint();
		
	}
	public Picture getContourPicture(){
		Picture p = new Picture();
		Canvas canvas = p.beginRecording(mWidth, mHeight);
		if (mPath != null){
			canvas.drawPath(mPath, mPaint);
		}
		p.endRecording();
		return p;
	}
	
	public void addPath(Path path){
		mPathList.add(new Path(path));
	}
	
	public void addRectangle(Rect rect) {
	    Path path = new Path();
	    RectF rectf = new RectF(rect);
	    path.addRect(rectf, Path.Direction.CW);
	}
	
	public Path removeLast(){
		return mPathList.pop();
	}
}
