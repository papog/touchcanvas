package com.spacepirates.android.touchcanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

public class ToolButton extends ToolPreview implements DrawEditDialog.OnToolChangedListener{

	private DrawingToolConfig mConfig;
	
	public ToolButton(Context context, AttributeSet attrs) {
		super(context, attrs); 
		// this is not used and easy to recognize (green) if it's used by mistake.
		setConfig(new DrawingToolConfig(1, 10, 0xFF00FF00));
	}
	
//	private void updateBackground() {
//		Bitmap b = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//		Canvas c = new Canvas(b);
//		DrawingTool t = new DrawingTool(null, null);
//		t.configure(mConfig);
//		t.renderSample(c);
//		setBackgroundDrawable(new BitmapDrawable(b));
//		invalidate();
//		
//	}

	public void setConfig(DrawingToolConfig config){
		mConfig = config;
		mTool = new DrawingTool(null, null);
		mTool.configure(mConfig);

	}
	
	

	@Override
	public void toolChanged(DrawingToolConfig config) {
		setConfig(new DrawingToolConfig(config));
		invalidate();
	}

	public DrawingToolConfig getConfig() {
		return new DrawingToolConfig(mConfig);
	}
	
	
	
}
