package com.spacepirates.android.touchcanvas;


import android.graphics.Rect;


/**
 * CoordinateMapper tracks the relationship between a window show part of a graphic system
 * and that system.
 * It contains functions to move the window around as well as map coordinates back and forth between both.
 * 
 * @author POG
 * The CoordinateMapper can map the coordinates of a reference space and a window showing part of that reference space
 * at a given scale.
 */
public class CoordinateMapper {

	private float leftInReference;
	private float topInReference;
	private float widthInReference;
	private float heightInReference;
	private float width;
	private float height;
	
	public CoordinateMapper(float x, float y, float widthInReference, float heightInReference, float width, float height){
		leftInReference = x;
		topInReference = y;
		this.widthInReference = widthInReference;
		this.heightInReference = heightInReference;
		this.width = width;
		this.height = height;
		
	}
	/**
	 * Reduces the size of the representation window in the reference coordinate system by factor.
	 * This effectively performs a zoom.
	 * @param factor
	 */
	public void zoomIn(float factor){
		widthInReference /= factor;
		heightInReference /= factor;
	}

	/**
	 * Increases the size of the representation window in the reference coordinate system by factor.
	 * This effectively zooms out and shows more of the reference space in the window.
	 * @param factor
	 */
	public void zoomOut(float factor){
		widthInReference *= factor;
		heightInReference *= factor;
	}

	private float cap(float input, float floor, float ceiling){
		if (input < floor) {
			return  floor;
		}
		else 
			if (input > ceiling)
			{
				return ceiling;
			}
			else {
				return input;
			}
		
	}
	/**
	 * translate the view window relative to the reference frame.
	 * Movement is constrained to stay inside the reference frame.
	 * @param dx
	 * @param dy
	 */
	public void moveBy(float dx, float dy){
		leftInReference = cap(leftInReference + dx, 0, width );
		topInReference = cap(topInReference + dy, 0 , height);		
	}
	
	public void moveByWindowCoordinates(float dx, float dy){
		moveBy(dx * widthInReference / width, dy * heightInReference / height);
	}
	
	public void changeWindowSize(float w, float h){
		width = w;
		height = h;
	}
	
	public float [] toReference(float x, float y){
		float [] result = new float[2];
		result [0] = leftInReference + x * widthInReference / width;
		result [1] = topInReference + y * heightInReference / height;
		return result;
	}
	
	public float [] fromReference(float x, float y){
		float [] result = new float[2];
		result [0] = (x - leftInReference) / widthInReference * width;
		result [1] = (y - topInReference) / heightInReference * height;
		return result;
		
	}
	
	public Rect toReference(Rect rect){
		Rect result = new Rect();
		float [] tmp = null;
		tmp = toReference(rect.left, rect.top);
		result.left = (int) tmp[0];
		result.top = (int) tmp[1];
		tmp = toReference(rect.right, rect.bottom);
		result.right = (int) Math.ceil(tmp[0]);
		result.bottom = (int) Math.ceil(tmp[1]);
		return result;
	}
	
	public Rect fromReference(Rect rect) {
		
		Rect result = new Rect();
		float [] tmp = null;
		tmp = fromReference(rect.left, rect.top);
		result.left = (int) tmp[0];
		result.top = (int) tmp[1];
		tmp = fromReference(rect.right, rect.bottom);
		result.right = (int) Math.ceil(tmp[0]);
		result.bottom = (int) Math.ceil(tmp[1]);
		return result;
	}
	public float[] toReferenceRounded(float x, float y) {
		float result[] = toReference(x,y);
		result [0] = Math.round(result[0]);
		result [1] = Math.round(result[1]);
		return result;
	}
	/**
	 * Zoom relative to invariant focus point. The focus point is specified in representation window and
	 * the corresponding representation location maps to the same reference point before and after zoom.
	 * This corresponds to a centered zoom operation with a mouse or a pinch to zoom implementation
	 * @param factor
	 * @param focusX x coordinate of zoom center in representation window
	 * @param focusY y coordinate of zoom center in representation window
	 */
	public void zoomOut(float factor, float focusX, float focusY) {
		float focusInReference[] = toReferenceRounded(focusX, focusY);
		widthInReference *= factor;
		heightInReference *= factor;
		leftInReference =  focusInReference[0] - focusX * widthInReference/ width  ;
		topInReference = focusInReference[1] - focusY * heightInReference / height;
		
	}
}