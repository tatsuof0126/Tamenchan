package com.tatsuo.tamenchan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TimerView extends View {
	private int remainingTime = 20000;
	
	public TimerView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		Paint paint = new Paint();
		
		paint.setStrokeWidth(40);
		if(remainingTime > 10000){
			paint.setColor(Color.GREEN);
			canvas.drawLine(240,30,40+(int)(remainingTime/50),30, paint);
			paint.setColor(Color.YELLOW);
			canvas.drawLine(140,30,240,30, paint);
			paint.setColor(Color.RED);
			canvas.drawLine(40,30,140,30, paint);
		} else if(remainingTime > 5000){
			paint.setColor(Color.YELLOW);
			canvas.drawLine(140,30,40+(int)(remainingTime/50),30, paint);
			paint.setColor(Color.RED);
			canvas.drawLine(40,30,140,30, paint);
		} else if(remainingTime > 0){
			paint.setColor(Color.RED);
			canvas.drawLine(40,30,40+(int)(remainingTime/50),30, paint);
		}
		
		paint.setStrokeWidth(2);
		paint.setColor(Color.WHITE);
		canvas.drawLine(38,0,38,60, paint);		
	}
	
	public void updateTimerView(int remainingTime){
		this.remainingTime = remainingTime;
		invalidate();
	}

}
