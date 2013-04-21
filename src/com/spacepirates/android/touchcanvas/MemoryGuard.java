package com.spacepirates.android.touchcanvas;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryGuard {

	static int MARGIN=1000000;
	
	static public long getAvailableMemory(){
		Log.i("mem","max memory:" + Runtime.getRuntime().maxMemory() +
				" available memory:" + Runtime.getRuntime().freeMemory() +
				" used memory:" + Runtime.getRuntime().totalMemory());
		return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() ;// Runtime.getRuntime().freeMemory();
	}

	static public boolean canFit(int size){
		return size + MARGIN < getAvailableMemory();
	}
	
	static public int size(Bitmap b){
		return b.getRowBytes() * b.getHeight();
	}
	
}
