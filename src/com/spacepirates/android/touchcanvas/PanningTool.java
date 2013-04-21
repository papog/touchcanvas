package com.spacepirates.android.touchcanvas;

import android.graphics.Canvas;

public class PanningTool extends AbstractDrawingTool {

	private float mX,mY;
	private DrawingModel mModel;
	private CoordinateMapper mCoordinateMapper;
	
	public PanningTool(DrawingModel model, CoordinateMapper mapper){
		mModel = model;
		mCoordinateMapper = mapper;
	}
	@Override
	public void renderTentative(Canvas canvas) {
	}

	@Override
	public void touch_start(float x, float y) {
		mModel.setBounds(null); // Nothing changed in panning.
		mX=x;
		mY=y;
	}

	@Override
	public void touch_move(float x, float y) {
		float dX = mX - x ;
		float dY = mY - y ;
		mCoordinateMapper.moveBy(dX, dY);


	}

	@Override
	public void touch_up() {
		mX = -1 ;
		mY = -1 ;

	}
	@Override
	public void cancel() {
		mX = -1;
		mY = -1;
	}

}
