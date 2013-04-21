package com.spacepirates.android.touchcanvas;

import android.os.Parcel;
import android.os.Parcelable;

public class AndroidDrawingToolConfig extends DrawingToolConfig implements Parcelable{

	public AndroidDrawingToolConfig(DrawingToolConfig config) {
		super(config);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(size);
		dest.writeInt(tool);
		dest.writeInt(color);
	}

	public static final Parcelable.Creator<AndroidDrawingToolConfig> CREATOR
	= new Parcelable.Creator<AndroidDrawingToolConfig>() {
		public AndroidDrawingToolConfig createFromParcel(Parcel in) {
			return new AndroidDrawingToolConfig(in);
		}

		public AndroidDrawingToolConfig[] newArray(int size) {
			return new AndroidDrawingToolConfig[size];
		}
	};

	private AndroidDrawingToolConfig(Parcel in) {
		super(0, 0, 0);
		size = in.readInt();
		tool = in.readInt();
		color = in.readInt();

	}






}
