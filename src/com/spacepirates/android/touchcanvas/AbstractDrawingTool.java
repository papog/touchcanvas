package com.spacepirates.android.touchcanvas;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

public abstract class AbstractDrawingTool {

	public void setColor(int color) {
	}

	public int getColor() {
		return 0;
	}
	

	public abstract void renderTentative(Canvas canvas);

	public abstract void touch_start(float x, float y);

	public abstract void touch_move(float x, float y);

	public abstract void touch_up();

	public abstract void cancel();	


}
