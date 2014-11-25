package com.example.ev3controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

public class ProgrammingView extends View{
	Bitmap genreImage[] = new Bitmap[3];//ブロックイメージの読み込み
	Bitmap startImage;
	
	private Point dispSize = new Point();//線を描写するのに必要な変数
	
	private int maxHeight;//作業スペースの高さの最大
	
	private static final double BLOCK_SCALE = 0.6;
	
	//コンストラクタ
	public ProgrammingView(Context context){
		super(context);
		
		//背景の色を設定
		this.setBackgroundColor(Color.WHITE);
		
		//ジャンルブロックの画像を設定
		Resources r = context.getResources();
		genreImage[0] = BitmapFactory.decodeResource(r, R.drawable.movebuton);
		genreImage[1] = BitmapFactory.decodeResource(r, R.drawable.ifbutton);
		genreImage[2] = BitmapFactory.decodeResource(r, R.drawable.forbutton);
		
		//スタートブロックの画像の設定
		r = context.getResources();
		startImage = BitmapFactory.decodeResource(r, R.drawable.start);
		
		startImage = Bitmap.createScaledBitmap(startImage, (int)(startImage.getWidth()*BLOCK_SCALE), (int)(startImage.getHeight()*BLOCK_SCALE), false);
		
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
		
		float x1 = dispSize.x/5;
		float y = dispSize.y;
		
		//ジャンルエリアとインスタンスエリアの境界線
		canvas.drawLine(x1, 0, x1, y, paint);
		
		//インスタンスエリアとワークスペースの境界線
		float x2 = x1;
		canvas.drawLine(x2, 0, x2, y, paint);
		
		//ジャンルボタン
		for(int i=0; i<3; i++){
			canvas.drawBitmap(genreImage[i], 25, 25+200*i, null);
		}
		
		//スタートボタン
		canvas.drawBitmap(startImage, x2+30, 25, null);
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
