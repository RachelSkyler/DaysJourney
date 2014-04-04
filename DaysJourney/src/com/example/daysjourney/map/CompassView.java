package com.example.daysjourney.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.daysjourney.R;

public class CompassView extends View {
	private Drawable compass;
	private float azimuth;
	private boolean sideBottom;
	private int padding = 2;

	public CompassView(Context context, final boolean sideBottom) {
		super(context);
		// TODO Auto-generated constructor stub
		this.compass = context.getResources().getDrawable(R.drawable.arrow);
		this.sideBottom = sideBottom;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.save();
		if (this.sideBottom) {
			canvas.rotate(360 - this.azimuth,
					this.padding + this.compass.getMinimumWidth() / 2,
					this.padding + this.compass.getMinimumHeight() / 2);
			this.compass.setBounds(padding, padding,
					padding + compass.getMinimumWidth(),
					padding + compass.getMinimumHeight());
		} else {
			canvas.rotate(360 - azimuth, padding + compass.getMinimumWidth()
					/ 2, this.getHeight() - compass.getMinimumHeight() / 2
					- padding);
			compass.setBounds(padding,
					this.getHeight() - compass.getMinimumHeight() - padding,
					padding + compass.getMinimumWidth(), this.getHeight()
							- padding);
		}
		this.compass.draw(canvas);
		canvas.restore();
		super.onDraw(canvas);
	}
	
	public void setAzimuth(float azimuth){
		this.azimuth=azimuth;
	}

}
