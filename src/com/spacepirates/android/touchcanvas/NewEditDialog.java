package com.spacepirates.android.touchcanvas;


import yuku.ambilwarna.AmbilWarnaDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;

public class NewEditDialog extends Dialog {
	private Context mContext;
	private int mColor;
	public NewEditDialog(Context c, OnCreateCanvasListener l){
		super(c);
		this.canvasListener = l;
		mContext = c;
		mColor = 0;
	}
	public interface OnCreateCanvasListener {
		
	        void createCanvas(int width, int height, int fillType);
	        
	}

	private OnCreateCanvasListener canvasListener;

	
	/** associateClickListener will simply locate the view designated by id and bind the listener**/
	private void associateClickListener(int id, android.view.View.OnClickListener listener){
		View tmpView = (View) findViewById(id);
		tmpView.setOnClickListener(listener);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_edit_dialog);
		setTitle("Create new canvas");
		associateClickListener(R.id.applyButton, new android.view.View.OnClickListener (){

			@Override
			public void onClick(View v) {
				applyChanges();
			}});
		associateClickListener(R.id.colorButton, new android.view.View.OnClickListener (){

			@Override
			public void onClick(View v) {
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(mContext, mColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
	

					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						mColor = color; // update tool color.
					}
						
					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// cancel was selected by the user
					}
				});

				dialog.show();

			}});

	}

	public static int fillType(int buttonId){
		switch(buttonId){
		case R.id.opaqueButton:
			return	0xFF000000;
		case R.id.transparentButton:
			return 0x00000000;
		
		}
		return -1;
	}
	protected void applyChanges() {
	
		int fillType;
		EditText widthText = (EditText) findViewById(R.id.editWidth);
		EditText heightText = (EditText) findViewById(R.id.editHeight);
		RadioGroup rg = (RadioGroup) findViewById(R.id.fillTypeGroup);
		fillType = fillType(rg.getCheckedRadioButtonId());
		int width = Integer.parseInt(widthText.getText().toString());
		int height = Integer.parseInt(heightText.getText().toString());
		
		canvasListener.createCanvas(width, height,fillType | mColor);
		dismiss();
	}

	
	
	
}
