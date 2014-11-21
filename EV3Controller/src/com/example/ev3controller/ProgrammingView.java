package com.example.ev3controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

public class ProgrammingView extends View{
	private Point dispSize = new Point();//線を描写するのに必要な変数
	private int maxHeight;//作業スペースの高さの最大
	
	//コンストラクタ
	public ProgrammingView(Context context){
		super(context);
		this.setBackgroundColor(Color.WHITE);
		
		maxHeight=0;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		//インスタンスブロック領域線
		Paint paint = new Paint();
		
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3.0f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		
		float x = dispSize.x/5;
		float y = dispSize.y;
		
		//ジャンルエリアとインスタンスエリアの境界線
		canvas.drawLine(x, 0, x, y, paint);
		
		//インスタンスエリアとワークスペースの境界線
		x = dispSize.x / 5 * 2;
		canvas.drawLine(x, 0, x, y, paint);
	}

	//View生成時にViewの大きさを設定するためのメソッド
	public void setDisplaySize(int width, int height){
		if(maxHeight == 0){
			//Viewの画面サイズの取得
			dispSize.x = width;
			dispSize.y = height;
			
			invalidate();
		}
	}
	
}
