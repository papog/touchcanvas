package com.spacepirates.android.touchcanvas;

import android.util.Log;

public class DurationLogger {
	
	private long startTime; 
	private String tag;
	public DurationLogger(String tag ){
		Log.i(tag, "creating timer" );
		startTime = -1;
		this.tag = tag;
	}
	
	public void start(){
		startTime = System.nanoTime();
	}
	
	public void end(){
	    Log.i(tag,"end after:" + (System.nanoTime() - startTime));
	}
}
