package com.spacepirates.android.touchcanvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ToolPreview extends View {

	DrawingTool mTool;
	
	public ToolPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTool = new DrawingTool(null, null);
		

	}

	@Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
		setMeasuredDimension(50, 50);
	}
	


	@Override
	protected void onDraw(Canvas canvas) {
		mTool.renderSample(canvas);		
	}

	void toolChanged(DrawingToolConfig config){
		mTool.configure(config);
		invalidate();
	}

}
 