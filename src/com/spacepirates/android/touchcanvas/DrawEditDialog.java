package com.spacepirates.android.touchcanvas;


import yuku.ambilwarna.AmbilWarnaDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * handle changes to a brush/drawing tool's configuration which is stored as a DrawingToolConfig.
 * @author pgaillard
 *
 */
public class DrawEditDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
	
	ToolPreview preview;
	SeekBar sb ;
	RadioGroup rg;
	Context mContext;
	
	private DrawingToolConfig config;
	
	public DrawEditDialog(Context c, OnToolChangedListener l, DrawingToolConfig config){
		super(c);
		this.toolListener = l;
		this.config = config;
		this.mContext = c;
	}
	public interface OnToolChangedListener {
	        void toolChanged(DrawingToolConfig config);
	}

	private OnToolChangedListener toolListener;

	
	/** associateClickListener will simply locate the view designated by id and bind the listener**/
	private void associateClickListener(int id, android.view.View.OnClickListener listener){
		View tmpView = (View) findViewById(id);
		tmpView.setOnClickListener(listener);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.draw_edit_dialog);
		setTitle("Edit tool properties");
		associateClickListener(R.id.applyButton, new android.view.View.OnClickListener (){
		
			@Override
			public void onClick(View v) {
				applyChanges();
			}});
		
		View view = findViewById(R.id.drawPreview2);
		preview = (ToolPreview) view;
		preview.toolChanged(config);
		sb = (SeekBar) findViewById(R.id.sizeBar);
		sb.setOnSeekBarChangeListener(this);
		sb.setProgress(config.size);
		rg = (RadioGroup) findViewById(R.id.toolTypeGroup);
		rg.setOnCheckedChangeListener(this);
		rg.check(toolButton(config.tool));
		associateClickListener(R.id.colorButton, new android.view.View.OnClickListener (){
			
			@Override
			public void onClick(View v) {
				AmbilWarnaDialog dialog = new AmbilWarnaDialog(mContext, config.color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						config.color = color; // update tool color.
						preview.toolChanged(config);
					}
						
					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// cancel was selected by the user
					}
				});

				dialog.show();

			}});
		
	}
	public static int toolButton(int toolId){
		switch(toolId){
		case DrawingToolConfig.REGULAR_ID:
			return  R.id.regularButton;
		case DrawingToolConfig.BLUR_ID:
			return R.id.blurButton;
		case DrawingToolConfig.EMBOSS_ID:
			return R.id.embossButton;
		case DrawingToolConfig.CLEAR_ID:
			return R.id.clearButton;
		}
		return -1;
	}	
	public static int toolId(int buttonId){
		switch(buttonId){
		case R.id.regularButton:
			return DrawingToolConfig.REGULAR_ID;
		case R.id.blurButton:
			return DrawingToolConfig.BLUR_ID;
		case R.id.embossButton:
			return DrawingToolConfig.EMBOSS_ID;
		case R.id.clearButton:
			return DrawingToolConfig.CLEAR_ID;
		}
		return -1;
	}

	protected void applyChanges() {
		config.size = sb.getProgress() + 1;// min can't be set on sizeBar, so we add it here.
		config.tool = toolId(rg.getCheckedRadioButtonId());
		toolListener.toolChanged(config);
		preview.toolChanged(config);
		dismiss();
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        config.size = progress;
        preview.toolChanged(config);
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// Ignore
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// Ignore
		
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		config.tool = toolId(checkedId);
		preview.toolChanged(config);
	}

	
	
	
}
