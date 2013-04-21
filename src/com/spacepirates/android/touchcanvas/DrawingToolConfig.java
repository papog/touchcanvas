package com.spacepirates.android.touchcanvas;

public class DrawingToolConfig {
	
	public int size;
	public int tool;
	public int color;
	
	public static final int REGULAR_ID = 0;
	public static final int BLUR_ID = 1;
	public static final int EMBOSS_ID = 2;
	public static final int CLEAR_ID = 3;

	public DrawingToolConfig( int tool, int size, int color){
		this.size = size;
		this.tool = tool;
		this.color = color;
	}

	public DrawingToolConfig( DrawingToolConfig config){
		this.size = config.size;
		this.tool = config.tool;
		this.color = config.color;
	}
	
}
