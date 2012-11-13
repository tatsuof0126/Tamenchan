package com.tatsuo.tamenchan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TimerView extends View {
	private static final int MAX_REMAINING_TIME = 20000;
	private int remainingTime = MAX_REMAINING_TIME;
	
	private Paint paint = null;
	
	private boolean calculated = false;
	private int width;
	private int sideMargin;
	private int barMaxLength;		
	private int redEnd;
	private int yellowEnd;
	
	public TimerView(Context context, AttributeSet attrs){
		super(context, attrs);
		paint = new Paint();
		
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		// 初回のみウィンドウサイズからバーの長さを計算する
		if(calculated == false){
			width = getWidth();
			sideMargin = (int)(width/12);
			barMaxLength = width - sideMargin*2;			
			yellowEnd = (int)(sideMargin+barMaxLength/2);
			redEnd = (int)(sideMargin+barMaxLength/4);
			calculated = true;
		}
		
		int barEnd = sideMargin+(int)(barMaxLength*remainingTime/MAX_REMAINING_TIME);
		
		paint.setStrokeWidth(40);
		if(remainingTime > 10000){
			paint.setColor(Color.GREEN);
			canvas.drawLine(yellowEnd, 30, barEnd, 30, paint);
			paint.setColor(Color.YELLOW);
			canvas.drawLine(redEnd, 30, yellowEnd, 30, paint);
			paint.setColor(Color.RED);
			canvas.drawLine(sideMargin, 30, redEnd, 30, paint);
		} else if(remainingTime > 5000){
			paint.setColor(Color.YELLOW);
			canvas.drawLine(redEnd, 30, barEnd, 30, paint);
			paint.setColor(Color.RED);
			canvas.drawLine(sideMargin, 30, redEnd, 30, paint);
		} else if(remainingTime > 0){
			paint.setColor(Color.RED);
			canvas.drawLine(sideMargin, 30, barEnd, 30, paint);
		}
		
		paint.setStrokeWidth(2);
		paint.setColor(Color.WHITE);
		canvas.drawLine(sideMargin-2, 0, sideMargin-2, 60, paint);		
	}
	
	public void updateTimerView(int remainingTime){
		this.remainingTime = remainingTime;
		invalidate();
	}

}
